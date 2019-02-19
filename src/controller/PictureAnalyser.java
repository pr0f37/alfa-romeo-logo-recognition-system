package controller;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import video.HistogramFrame;
/**
 * Analyses picture given in the constructor or by the setImage method.
 * Searches for Alfa Romeo logo in the given picture. 
 * @author Adam 'Prorok' Nowik
 *
 */
public class PictureAnalyser {

	public PictureAnalyser()
	{
		image = null;
	}
	
	/**
	 * Creates pictureAnalyser object with BufferedImage as the picture to be analysed.
	 * @param image - picture which will be analysed.
	 */
	public PictureAnalyser(BufferedImage anImage)
	{
		image = anImage;
	}
	
	/**
	 * Sets picture given in the parameter as a candidate for analysis.
	 * @param image - picture which will be analysed.
	 */
	public void setImage(BufferedImage anImage)
	{
		image = anImage;
	}
	
	/**
	 * Returns analysed picture. 
	 * @return Analysed picture.
	 */
	public BufferedImage getImage()
	{
		return image;
	}
	
	public void analyse()
	{
		WritableRaster raster = image.getRaster();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] samples = new int[4 * width * height]; // image pixels buffer, 
													 // pixel values are stored in order: red, green, blue, alfa
		raster.getPixels(0, 0, width, height, samples);
		int[][][] samplesARGB = translateSamples(samples, width, height);
		int[] histogram = new int[256];
		int[] redHistogram = new int[256];
		int[] greenHistogram = new int[256];
		int[] blueHistogram = new int[256];
		
		int amount = 0;
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				int red = samplesARGB[x][y][0];
				int green = samplesARGB[x][y][1];
				int blue = samplesARGB[x][y][2];
				int gray = samplesARGB[x][y][0] + samplesARGB[x][y][1] + samplesARGB[x][y][2];
				gray /= 3;
				if (gray < 252)
				{
					amount++;
					histogram[gray]++;
					redHistogram[red]++;
					greenHistogram[green]++;
					blueHistogram[blue]++;
				}
				
			}
		}

		logHistogram(histogram, "histogram_raw");
		
		for (int i = 0; i < 256; i++)
		{
			double t = ((histogram[i]*HISTOGRAM) / (amount));
			histogram[i] = (int)t;
			t = ((redHistogram[i]*HISTOGRAM) / (amount));
			redHistogram[i] = (int)t;
			t = ((greenHistogram[i]*HISTOGRAM) / (amount));
			greenHistogram[i] = (int)t;
			t = ((blueHistogram[i]*HISTOGRAM) / (amount));
			blueHistogram[i] = (int)t;
		}
		
		logHistogram(histogram, "histogram_normalized");
		
		HistogramFrame hFrame = new HistogramFrame("Histogram");
		hFrame.setHistogramData(histogram);
		hFrame.setVisible(true);
		hFrame.createHistogram();
		
		HistogramFrame rHFrame = new HistogramFrame("Red histogram");
		rHFrame.setHistogramData(redHistogram);
		rHFrame.setVisible(true);
		rHFrame.createHistogram();
		
		HistogramFrame gHFrame = new HistogramFrame("Green histogram");
		gHFrame.setHistogramData(greenHistogram);
		gHFrame.setVisible(true);
		gHFrame.createHistogram();
		
		HistogramFrame bHFrame = new HistogramFrame("Blue histogram");
		bHFrame.setHistogramData(blueHistogram);
		bHFrame.setVisible(true);
		bHFrame.createHistogram();
	}
	
	public void computeFactors()
	{
		WritableRaster raster = image.getRaster();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] samples = new int[4 * width * height]; // image pixels buffer, 
													 // pixel values are stored in order: red, green, blue, alfa
		raster.getPixels(0, 0, width, height, samples);
		int[][][] samplesARGB = translateSamples(samples, width, height);
		LinkedList<LinkedList<Point>> objects = new LinkedList<LinkedList<Point>>();
		for (int x = 0; x < width; x++)
		{
			samplesARGB[x][0][0] = 255;
			samplesARGB[x][0][1] = 255;
			samplesARGB[x][0][2] = 255;
			samplesARGB[x][height - 1][0] = 255;
			samplesARGB[x][height - 1][1] = 255;
			samplesARGB[x][height - 1][2] = 255;
		}
		for (int y = 0; y < height; y++)
		{
			samplesARGB[0][y][0] = 255;
			samplesARGB[0][y][1] = 255;
			samplesARGB[0][y][2] = 255;
			samplesARGB[width - 1][y][0] = 255;
			samplesARGB[width - 1][y][1] = 255;
			samplesARGB[width - 1][y][2] = 255;
		}
		for (int x = 1; x < width - 1; x++)
		{
			for (int y = 1; y < height - 1; y++)
			{
				int red = samplesARGB[x][y][0];
				int green = samplesARGB[x][y][1];
				int blue = samplesARGB[x][y][2];
				if (red == 0 && green == 0 && blue == 0)
				{
					seed(samplesARGB, x, y, objects);
				}
			}
			if (x % 50 == 0)
			{
				System.out.println(x);
				joinObjects(objects);
			}
		}
		LinkedList<LinkedList<Point>> dragons = new LinkedList<LinkedList<Point>>();
		LinkedList<LinkedList<Point>> crosses = new LinkedList<LinkedList<Point>>();
		LinkedList<LinkedList<Point>> borders = new LinkedList<LinkedList<Point>>();

		
		for (int j = 0; j < objects.size(); j++)
		{
			 if (objects.get(j).size()> 1000)
			 {
				 
					
					int A = objects.get(j).size();
					int m00 = A;
					int m10 = 0;
					int m01 = 0;
					int m11 = 0;
					int m20 = 0;
					int m02 = 0;
					for (int i = 0; i < A; i++)
					{
						m10 += (int)objects.get(j).get(i).getX();
						m01 += (int)objects.get(j).get(i).getY();
						m11 += (int)objects.get(j).get(i).getX() * (int)objects.get(j).get(i).getY();
						m20 += (int)objects.get(j).get(i).getX() * (int)objects.get(j).get(i).getX();
						m02 += (int)objects.get(j).get(i).getY() * (int)objects.get(j).get(i).getY();
					}
					float M20 = m20 - (m10 * m10) / m00;
					float M02 = m02 - (m01 * m01) / m00;
					float M11 = m11 - (m10 * m01) / m00;
					float M1 = (M20 + M02) / (m00*m00);
					float M2 = ((M20 - M02)*(M20 - M02) + 4*M11*M11)/ (m00*m00*m00*m00);
					float M7 = (M20 * M02 - M11 * M11) / (m00 * m00 * m00 * m00);
					System.out.println("M1 = " + M1);
					System.out.println("M2 = " + M2);
					System.out.println("M7 = " + M7);
					if ((Math.abs(M1 - M1dragon) / M1dragon) * 100 <= 20)
					{
						dragons.add(objects.get(j));
					}
					else if ((Math.abs(M1 - M1cross) / M1cross) * 100 <= 20)
					{
						crosses.add(objects.get(j));
					}
					else if ((Math.abs(M1 - M1border) / M1border) * 100 <= 20)
					{
						borders.add(objects.get(j));
					}
			 }
		}
		
		for (int i = 0; i < borders.size(); i++)
		{
			for (int j = 0; j < crosses.size(); j++)
			{
				for (int k = 0; k < dragons.size(); k++)
				{
					if((getMinX(borders.get(i)) < getMinX(crosses.get(j)))
							&& (getMinX(borders.get(i)) < getMinX(dragons.get(k)))
							&& (getMinY(borders.get(i)) < getMinY(crosses.get(j)))
							&& (getMinY(borders.get(i)) < getMinY(dragons.get(k)))
							&& (getMaxX(borders.get(i)) > getMaxX(crosses.get(j)))
							&& (getMaxX(borders.get(i)) > getMaxX(dragons.get(k)))
							&& (getMaxY(borders.get(i)) > getMaxY(crosses.get(j)))
							&& (getMaxY(borders.get(i)) > getMaxY(dragons.get(k)))
							&& (Math.abs((float)crosses.get(j).size()/(float)borders.get(i).size() - CrossToBorder) / CrossToBorder <= 0.1f )
							&& (Math.abs((float)dragons.get(k).size()/(float)borders.get(i).size() - DragonToBorder) / DragonToBorder <= 0.1f )
							&& (Math.abs((float)crosses.get(j).size()/(float)dragons.get(k).size() - CrossToDragon) / CrossToDragon <= 0.1f )
							) // dragon and cross inside border
					{
						drawObject(samplesARGB, borders.get(i));
						drawObject(samplesARGB, crosses.get(j));
						drawObject(samplesARGB, dragons.get(k));
						System.out.println("cross/border = " + (float)crosses.get(j).size()/(float)borders.get(i).size());
						System.out.println("dragon/border = " + (float)dragons.get(k).size()/(float)borders.get(i).size());
						System.out.println("cross/dragon = " + (float)crosses.get(j).size()/(float)dragons.get(k).size());
						borders.remove(i);
						crosses.remove(j);
						dragons.remove(k);
						i--;
						j--;
						k--;
					}
					if (j < 0 || i < 0)
						break;
				}
				if (i < 0)
					break;
			}
		}

		samples = PictureAnalyser.retranslateSamples(samplesARGB, width, height);
		raster.setPixels(0, 0, width, height, samples);
		
		
	}
	public void drawObject(int[][][] samplesARGB, LinkedList<Point> object)
	{
		for (int i = 0; i < object.size(); i++)
			{
				int x = (int)object.get(i).getX();
				int y = (int)object.get(i).getY();
				
				samplesARGB[x][y][0] = 128;
				samplesARGB[x][y][1] = 255;
				samplesARGB[x][y][2] = 128;
			}
	}
	
	public int getMinX(LinkedList<Point> object)
	{
		int coord = Integer.MAX_VALUE;
		for (int i = 0; i < object.size(); i++)
		{
			if (coord > (int)object.get(i).getX())
				coord = (int)object.get(i).getX();
		}
		return coord;
	}
	
	public int getMinY(LinkedList<Point> object)
	{
		int coord = Integer.MAX_VALUE;
		for (int i = 0; i < object.size(); i++)
		{
			if (coord > (int)object.get(i).getY())
				coord = (int)object.get(i).getY();
		}
		return coord;
	}
	
	public int getMaxX(LinkedList<Point> object)
	{
		int coord = 0;		
		for (int i = 0; i < object.size(); i++)
		{
			if (coord < (int)object.get(i).getX())
				coord = (int)object.get(i).getX();
		}
		return coord;
	}
	
	public int getMaxY(LinkedList<Point> object)
	{
		int coord = 0;
		for (int i = 0; i < object.size(); i++)
		{
			if (coord < (int)object.get(i).getY())
				coord = (int)object.get(i).getY();			
		}
		return coord;
	}
	
	public void joinObjects(LinkedList<LinkedList<Point>> objects)
	{
		for(int i = 0; i < objects.size() - 1; i ++)
		{
			for(int j = i+1; j < objects.size(); j++)
			{
				for (int k = 0; k < objects.get(i).size(); k++)
				{
					int x =(int)objects.get(i).get(k).getX();
					int y = (int)objects.get(i).get(k).getY();
					if(objects.get(j).contains(new Point (x - 1, y - 1)))
					{ // neighbour found
						for (int l = 0; l < objects.get(j).size(); l++)
						{
							if (!objects.get(i).contains(objects.get(j).get(l)))
								objects.get(i).add(objects.get(j).get(l));
						}
						objects.remove(j);
						j--;
						break;
					}
				}
			}
		}

	}
	
	public void seed(int[][][] samples, int x, int y, LinkedList<LinkedList<Point>> objects)
	{
		samples[x][y][0] = 255;
		samples[x][y][0] = 128;
		samples[x][y][0] = 128;
		if (objects.size() != 0)
		{
			int i = 0;
			while (i < objects.size())
			{
				if (objects.get(i).contains(new Point(x-1,y-1)) || objects.get(i).contains(new Point(x,y-1))
						|| objects.get(i).contains(new Point(x-1,y)) || objects.get(i).contains(new Point(x-1,y+1)))
				{
					objects.get(i).add(new Point(x,y));
					break;
				}
				i++;
			}
			if (i == objects.size()) // new shape found
			{
				LinkedList<Point> newObject = new LinkedList<Point>();
				newObject.add(new Point(x,y));
				objects.add(newObject);
			}
		}
		else 
		{
			LinkedList<Point> newObject = new LinkedList<Point>();
			newObject.add(new Point(x,y));
			objects.add(newObject);
		}
	}
	
	public void computePattern()
	{
		WritableRaster raster = image.getRaster();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] samples = new int[4 * width * height]; // image pixels buffer, 
													 // pixel values are stored in order: red, green, blue, alfa
		raster.getPixels(0, 0, width, height, samples);
		int[][][] samplesARGB = translateSamples(samples, width, height);
		for (int x = 1; x < width - 1; x++)
		{
			for (int y = 1; y < height - 1; y++)
			{
				int red = samplesARGB[x][y][0];
				int green = samplesARGB[x][y][1];
				int blue = samplesARGB[x][y][2];
				if (red <= 250 && green <= 250 && blue <= 250)
				{
					samplesARGB[x][y][0] = 255;
					samplesARGB[x][y][0] = 128;
					samplesARGB[x][y][0] = 128;
				}
				// liczenie momentów
			}
		}
		samples = PictureAnalyser.retranslateSamples(samplesARGB, width, height);
		raster.setPixels(0, 0, width, height, samples);
	}
	
	/**
	 * Translates pixel buffer filled with <b>WriteableRaster.getPixels(i,j,w,h,buffer)</b> method.<br>
	 * Fills an three-dimensional array with the pattern given: <br>
	 * <p>
	 * complexSamples[x][y][0] - Red color value<br>
	 * complexSamples[x][y][1] - Green color value<br>
	 * complexSamples[x][y][2] - Blue color value<br>
	 * complexSamples[x][y][3] - Aplha value<br><br>
	 * Where: <b>x</b> - vertical pixel coordinate, <b>y</b> - horizontal pixel coordinate
	 * </p>
	 * @param simpleSamples - pixel buffer <i>(TYPE_INT_ARGB)</i>.
	 * @param w - picture width.
	 * @param h - picture height.
	 * @return <b>int[][][]</b> ARGBpixel buffer.
	 */
	public static int[][][] translateSamples(int[] simpleSamples, int w, int h)
	{
		int[][][] complexSamples = new int[w][h][4];
		int i = 0;
		try {
			for (int x = 0; x < w; x++)
			{
				for (int y = 0; y < h; y++)
				{
					if (i == simpleSamples.length)
					{
						Exception e = new Exception("Source array lenght exceeded, check width/lenght attributes.");
						throw e;
					}
					else
					{
						complexSamples[x][y][0] = simpleSamples[i++];
						complexSamples[x][y][1] = simpleSamples[i++];
						complexSamples[x][y][2] = simpleSamples[i++];
						complexSamples[x][y][3] = simpleSamples[i++];
					}
				}
			}
		} catch (Exception e) {
		
			JOptionPane.showMessageDialog(null, e);
		}
		return complexSamples;
	}

	/**
	 * Translate form ARGBpixel buffer to pixel buffer ready to use in <b>WriteableRaster.setPixels(i,j,w,h,buffer)</b>
	 * @param complexSamples - ARGBpixel buffer.
	 * @param w - picture width.
	 * @param h - picture height.
	 * @return <b>int[]</b> pixel buffer.
	 * @see PictureAnalyser.translateSamples
	 */
	public static int[] retranslateSamples(int [][][]complexSamples, int w, int h)
	{
		int[] simpleSamples = new int[w * h * 4];
		int i = 0;
		for (int x = 0; x < w; x++)
		{
			for (int y = 0; y < h; y++)
			{
				simpleSamples[i++] = complexSamples[x][y][0];
				simpleSamples[i++] = complexSamples[x][y][1];
				simpleSamples[i++] = complexSamples[x][y][2];
				simpleSamples[i++] = complexSamples[x][y][3];
			}
		}
		return simpleSamples;
	}
	
	public void logHistogram(int[] histogramData, String fileName)
	{
		try {
			PrintWriter out = new PrintWriter(fileName + ".txt");
			for (int i = 0; i < 256; i++)
			{
				out.println("[" + i + "]: " + histogramData[i]);
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	private BufferedImage image;
	private static int HISTOGRAM = 15000;
	private static float M1dragon = 21.490854f; // 5%
	private static float M1cross = 7.927656f; // 5%
	private static float M1border = 3.3100576f; // 20%
	private static float CrossToBorder = 0.2379132f; // 10%
	private static float DragonToBorder = 0.15871075f; // 10%
	private static float CrossToDragon = 1.498365f; // 10%
}
