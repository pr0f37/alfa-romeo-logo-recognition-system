package video;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class HistogramFrame extends JFrame {

	public HistogramFrame(String name) 
	{
		setTitle(name);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setLocation(100, 200);
		Box box = Box.createVerticalBox();
		image = new BufferedImage(700, 276, BufferedImage.TYPE_INT_ARGB);
		box.add(new JLabel(new ImageIcon(image)));
		setContentPane(new JScrollPane(box));
		validate();
	}
	
	public HistogramFrame(String name, int[] aHistogramData)
	{
		new HistogramFrame(name); 
		setHistogramData(aHistogramData);
	}
	
	public int[] getHistogramData()
	{
		return histogramData;
	}
	
	public void setHistogramData(int[] aHistogramData)
	{
		histogramData = aHistogramData;
	}
	
	public void createHistogram()
	{
		if (histogramData != null )
		{
			WritableRaster raster = image.getRaster();
			for (int i = 0; i < 256; i++)
			{
				if (histogramData[i] > 500 || histogramData[i] < 0)
					histogramData[i] = 500;
				int[] buffer = new int[histogramData[i]*4 ];
				for (int j = 0; j < buffer.length; ) // let's insert black pixels into the buffer
				{
					buffer[j++] = 0; // R
					buffer[j++] = 0; // G
					buffer[j++] = 0; // B
					buffer[j++] = 255; // alfa
				}
				raster.setPixels(40, 10+i, histogramData[i], 1, buffer);
			}
			int[] buffer = new int[512 * 4];
			for (int i = 0; i < 2048;)
			{
				buffer[i++] = 0;
				buffer[i++] = 0;
				buffer[i++] = 0;
				buffer[i++] = 255;
			}
			raster.setPixels(30, 10, 2, 256, buffer);
			buffer = new int[10 * 4];
			for (int i = 0; i < 40;)
			{
				buffer[i++] = 0;
				buffer[i++] = 0;
				buffer[i++] = 0;
				buffer[i++] = 255;
			}
			for(int i = 0; i < 8; i++)
			{
				raster.setPixels(25, 10 + i*32, 5, 2, buffer);
			}
			raster.setPixels(25, 264, 5, 2, buffer);

		}
		repaint();
	}
	
	private BufferedImage image;
	private int[] histogramData;
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_WIDTH = 650;
	private static final int DEFAULT_HEIGHT = 350;
}
