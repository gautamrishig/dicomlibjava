package com.sidewinder.dicomlib.util;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.sidewinder.dicomlib.dicom.DicomElement;

public class ImageVisualizer extends JFrame {

	public ImageVisualizer(DicomElement pixelData) {
		super("Image Visualizer");
		ImageIcon imgIcon = new ImageIcon((byte[])(pixelData.getValue().get(0).getValue()));
		JLabel hello = new JLabel(imgIcon);
		add(hello);
		setSize(100, 100);
		setVisible(true);
	}
}
