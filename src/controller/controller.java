package controller;
import java.awt.EventQueue;
import javax.swing.JFrame;

import video.ImageFrame;
/**
 * System enabling Alfa Romeo logotype recognision. 
 * It enables user to open and save changed graphic files.
 * @author Adam 'Prorok' Nowik
 */
public class controller {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new ImageFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}

}
