package zviconv;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileInfo;
import ij.io.Opener;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class ZVIConverter {

	public static List<File> convert(File zviFile, File targetFolder) {

		List<File> result = new ArrayList<File>();

		Map<Integer, ImageStack> st = new ZVIReader().read(zviFile);
		List<ImageStack> channels = new ArrayList<ImageStack>(st.values());
		Collections.reverse(channels);
		int numChannels = Math.min(channels.size(), 3);

		ImageStack first = channels.get(0);
		int width = first.getWidth();
		int height = first.getHeight();
		int bitDepth = first.getBitDepth();
		int numSlices = first.getSize();

		int min = 0;
		double scale = 1.0;

		// Iterate over all slices (images):
		for (int slice = 0; slice < numSlices; slice++) {
			int[] rgb = new int[width * height];

			if (bitDepth == 16) {

				// Determine min and scale:
				if (slice == 0) {
					min = Integer.MAX_VALUE;
					int size = 0;
					int max = 0;
					long sum = 0;
					for (int c = 0; c < numChannels; c++) {
						ImageStack is = channels.get(c);
						short[] pixels = (short[]) is.getImageArray()[0];
						size = pixels.length;
						for (int i = 0; i < pixels.length; i++) {
							if (pixels[i] < min) {
								min = pixels[i];
							}
							if (pixels[i] > max) {
								max = pixels[i];
							}
							sum += pixels[i];
						}
					}
					// Average value:
					int average = (int) (sum / (long) size);
					// Some adjustment:
					max = (max + average) / 3;
					// Scale factor:
					scale = 255.0 / (double) (max - min);
				}

				// Compute pixels:
				for (int c = 0; c < numChannels; c++) {
					ImageStack is = channels.get(c);
					short[] pixels = (short[]) is.getImageArray()[slice];
					int ch;
					if (numChannels == 1) {
						// gray-scale:
						for (int i = 0; i < pixels.length; i++) {
							ch = (int) (((double) (pixels[i] - min)) * scale);
							if (ch > 255)
								ch = 255;
							rgb[i] = (ch << 16) | (ch << 8) | (ch);
						}

					} else {
						// multiple channels:
						for (int i = 0; i < pixels.length; i++) {
							ch = (int) (((double) (pixels[i] - min)) * scale);
							if (ch > 255)
								ch = 255;
							rgb[i] = (rgb[i] << 8) | ch;
						}
					}
				}
			}

			// Write AWT image:
			BufferedImage res = new BufferedImage(width, height,
					numChannels == 1 ? BufferedImage.TYPE_BYTE_GRAY
							: BufferedImage.TYPE_INT_RGB);
			int x, y;
			int c = 0;
			for (y = 0; y < height; y++) {
				for (x = 0; x < width; x++) {
					res.setRGB(x, y, rgb[c++]);
				}
			}

			// Save as PNG:
			File finalImage = new File(targetFolder, zviFile.getName()
					.replaceAll(".zvi", "-" + slice) + ".png");
			try {
				ImageIO.write(res, "png", finalImage);
			} catch (IOException e) {
				e.printStackTrace();
			}
			result.add(finalImage);

		}
		return result;

	}

	private static class ZVIReader extends ZVI_Reader {

		public Map<Integer, ImageStack> read(File zviFile) {
			FileInfo[] fi = null;
			try {
				fi = getHeaderInfo(zviFile.getParent() + File.separator,
						zviFile.getName());
			} catch (Exception e) {
				return null;
			}
			if (fi == null) {
				return null;
			}
			Opener opener = new Opener();
			ImagePlus imp = opener.openTiffStack(fi);
			if (imp == null) {
				return null;
			}
			ImageStack istk = imp.getStack();
			int nSlice = istk.getSize();
			int sWidth = istk.getWidth();
			int sHeight = istk.getHeight();

			HashMap<Integer, ImageStack> img = new LinkedHashMap<Integer, ImageStack>();
			@SuppressWarnings("unchecked")
			Iterator<Integer> it = C_Set.iterator();
			while (it.hasNext()) {
				Integer theC = it.next();
				img.put(theC, new ImageStack(sWidth, sHeight));
			}
			for (int n = 0; n < nSlice; n++) {
				int theC = getChannel(fi[n]);
				ip1 = istk.getProcessor(n + 1);
				img.get(theC).addSlice(null, ip1);
			}
			return img;
		}
	}
}
