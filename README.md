[![DOI](https://zenodo.org/badge/410369848.svg)](https://zenodo.org/badge/latestdoi/410369848)


# CCEAN
The **c**ell **c**ount **e**stimator aided by **a**rea calculatio**n**

created by Alexander Neumann in 2014

<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#approach">Approach</a>
    </li>
    <li>
      <a href="#usage">Usage</a>
    </li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

Image analysis plays a large role in biological research. Although error-prone and tedious, it is still best practive to do this analysis manually. One of the most common tasks in biological image analysis is cell detection and counting.

A standard assay to quantify myogenic differentiation potential (for example in C2C12 cells) is to stain cell nuclei (DAPI, blue) and myotubes (myosin heavy chain antibody, green), perform microscopy on these cells and then calculate the fusion index. The fusion index is the percentage of nuclei within muscular tissue.

In order to calculate the fusion index, the total number of nuclei and the number of nuclei within myotubes need to be counted. The following challenges make this procedure problematic and unfeasible for manual counting:
- The nuclei are highly clustered
- One image often contains 1000-6000 nuclei
- One experiment often consists of dozens or hundreds of images

This tool was created during a Master's thesis in order to provide a fast and easy to use, high-throughput method for the quantitative analysis of fluorescence microscopy images of this type. 



<!-- APPROACH -->
## Approach

In order to analyse these microscopy images in a timely manner, there is a need for automation of the process. This is why the cell count estimation aided by area calculation tool (CCEAN, [shawn]) was created. It follows a hybrid approach that combines area calculations and detection of unclustered nuclei to estimate a total cell count. 

CCEAN uses microscopy images as input, processes them and outputs an estimate of the cell count and fusion index. The tools works semi-automaticall and only requires user input at the myotube detection step. Here, the human ability to gauge large, connected structured is helpful to set suitable separation parameters.

More preciseliy, CCEAN processes images as follows:
1. The image is loaded
2. Channels are split and blurred to remove small artifacts
3. Local Otsu thresholding is applied to the blue (DAPI/ nuclei) channel image, resulting in separation of the nuclei from background (-> total nuclei area)
4. Unclustered nuclei are detected by excluding separated areas that are larger than a certain size (-> average nucleus area = number of unclustered nuclei / area of all unclustered nuclei)
5. The number of nuclei in the clustered nuclei region is calculated as nuclei area / average nucleus area
6. The green channel image (myotubes) is presented to the user, it is possible to interactively set a separation threshold and dilate or erode the identified area
7. Once the thresholds are set, the myotube area is separated from the background (-> total myotube area)
8. Using a co-localisation filter, the area of nuclei in myotubes is inferred (-> myotube nuclei area)
9. A myotube nuclei count and the fusion index are calculated. Both the fusion index and the nuclei count are reported.

![approach_visualised](https://user-images.githubusercontent.com/59624597/134785684-04a90b44-59e6-4bc0-8e1c-3406c543e9bc.png)



<!-- USAGE -->
## Usage

CCEAN was tested for Windows 7 and Windows 10. Download and extract the repository. Then use the Windows terminal to start the CCEAN.exe. The program will guide you through the process.

If CCEAN helps you in your research, please cite via DOI 10.5281/zenodo.5528307



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.



<!-- CONTACT -->
## Contact

Alexander Neumann - alexander.neumann@omiqa.bio

Project Link: [https://github.com/neumann-alexander/ccean](https://github.com/neumann-alexander/ccean)

Im am now part of Omiqa Bioinformatics - a company offering bioinformatics services to life science researchers. Check it out at [https://omiqa.bio](https://omiqa.bio)
