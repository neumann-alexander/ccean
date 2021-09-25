package zviconv;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class ZVIConverterUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JLabel sourceLabel, targetLabel;
	private JTextField sourceField, targetField;
	private JButton sourceButton, targetButton, convertButton;
	private JProgressBar progressBar;

	GroupLayout layout;

	public ZVIConverterUI() {
		super("ZVI Batch Converter");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel form = new JPanel();

		form.setLayout(layout = new GroupLayout(form));

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		JPanel dum1 = new JPanel();
		JPanel dum2 = new JPanel();

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(sourceLabel = new JLabel("ZVI Folder:"))
				.addComponent(targetLabel = new JLabel("Target Folder:"))
				.addComponent(dum1));
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(sourceField = new JTextField(20))
				.addComponent(targetField = new JTextField(20))
				.addComponent(dum2));
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(sourceButton = new JButton("Choose..."))
				.addComponent(targetButton = new JButton("Choose..."))
				.addComponent(convertButton = new JButton("Convert")));
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(sourceLabel).addComponent(sourceField)
				.addComponent(sourceButton));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(targetLabel).addComponent(targetField)
				.addComponent(targetButton));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(dum1).addComponent(dum2)
				.addComponent(convertButton));
		layout.setVerticalGroup(vGroup);

		sourceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				open(sourceField);
			}
		});

		targetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				open(targetField);
			}
		});

		convertButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				convertButton.setEnabled(false);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				task = new Task();
				task.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if ("progress" == evt.getPropertyName()) {
							int progress = (Integer) evt.getNewValue();
							progressBar.setValue(progress);
						}
					}

				});
				task.execute();
			}
		});

		progressBar = new JProgressBar(0, 100);

		getContentPane().add(form, BorderLayout.CENTER);
		getContentPane().add(progressBar, BorderLayout.SOUTH);

	}

	private static void open(JTextField field) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			field.setText(chooser.getSelectedFile().getAbsolutePath());
		}

	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ZVIConverterUI ui = new ZVIConverterUI();
				ui.pack();
				ui.setLocationRelativeTo(null);
				ui.setVisible(true);
			}
		});
	}

	private Task task;

	class Task extends SwingWorker<Void, Void> {
		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			setProgress(0);
			File zviFolder = new File(sourceField.getText());
			File targetFolder = new File(targetField.getText());
			File[] files = zviFolder.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()
						&& files[i].getName().toLowerCase().endsWith(".zvi")) {
					ZVIConverter.convert(files[i], targetFolder);
				}
				setProgress((int) (((double) (i+1) / (double) files.length) * 100.0));
			}
			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			convertButton.setEnabled(true);
			setCursor(null);
		}
	}
}