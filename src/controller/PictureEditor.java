/**
 * 
 */
package controller;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * @author Adam "Prorok" Nowik
 *
 */
public class PictureEditor {
	public PictureEditor()
	{
		image = null;
	}
	
	public PictureEditor(BufferedImage anImage)
	{
		image = anImage;
	}
	/**
	 * Returnes convolved ARGB pixel matrix in samplesARGB
	 * @param elements
	 * @param w
	 * @param h
	 * @param samplesARGB
	 * @param tmpSamplesARGB
	 */
	public void convolve(float[] elements, int w, int h, int[][][] samplesARGB)
	{

		int width = image.getWidth();
		int height = image.getHeight();
		int[][][] tmpSamplesARGB = PictureAnalyser.translateSamples(PictureAnalyser.retranslateSamples(samplesARGB, width, height), width, height);

		// external loop for image processing
		for (int x = (int)(Math.floor(w/2)); x < width - (int)(Math.floor(w/2)); x++)
		{
			for (int y = (int)(Math.floor(h/2)); y < height - (int)(Math.floor(h/2)); y++)
			{
				// intra loop for convolving mask
				float REDvalue = 0.0f;
				float GREENvalue = 0.0f;
				float BLUEvalue = 0.0f;
				int k = 0;
				float maskSum = 0;
				for (int j = y - (int)(Math.floor(h/2)); j < y + (int)(Math.floor(h/2) + 1); j ++)
				{
					for (int i = x - (int)(Math.floor(w/2)); i < x + (int)(Math.floor(w/2) + 1); i++ )
					{
						REDvalue += tmpSamplesARGB[i][j][0] * elements[k];
						GREENvalue += tmpSamplesARGB[i][j][1] * elements[k];
						BLUEvalue += tmpSamplesARGB[i][j][2] * elements[k];
						maskSum += elements[k];
						k++;
					}
				}
				
				// now he have rgb values multiplied by their weights
				samplesARGB[x][y][0] = (int)Math.floor(REDvalue / maskSum);
				samplesARGB[x][y][1] = (int)Math.floor(GREENvalue / maskSum);
				samplesARGB[x][y][2] = (int)Math.floor(BLUEvalue / maskSum);
			}
		}	
	
	}
	
	public void ranking(int type, int w, int h, int[][][] samplesARGB)
	{

		int width = image.getWidth();
		int height = image.getHeight();
		int[][][] tmpSamplesARGB = PictureAnalyser.translateSamples(PictureAnalyser.retranslateSamples(samplesARGB, width, height), width, height);

		// external loop for image processing
		for (int x = (int)(Math.floor(w/2)); x < width - (int)(Math.floor(w/2)); x++)
		{
			for (int y = (int)(Math.floor(h/2)); y < height - (int)(Math.floor(h/2)); y++)
			{
				// intra loop for ranking filter
				int[] REDvalue = new int[w*h];
				int[] GREENvalue = new int[w*h];
				int[] BLUEvalue = new int[w*h];
				int[] GRAY = new int[w*h];
				int k = 0;
				for (int j = y - (int)(Math.floor(h/2)); j < y + (int)(Math.floor(h/2))+1; j ++)
				{
					for (int i = x - (int)(Math.floor(w/2)); i < x + (int)(Math.floor(w/2)+1); i++ )
					{
						// data gathering
						REDvalue[k] = tmpSamplesARGB[i][j][0];
						GREENvalue[k] = tmpSamplesARGB[i][j][1];
						BLUEvalue[k] = tmpSamplesARGB[i][j][2];
						GRAY[k] = (int)((REDvalue[k] + GREENvalue[k] + BLUEvalue[k]) / 3);
						k++;
					}
				}
				// select sort O(NlogN)
				for (int i = 0; i < w*h - 1; i++)
				{
					for (int j = i+1; j < w*h; j++)
						if(GRAY[j] < GRAY[i])
						{
							int tmp = GRAY[i];
							GRAY[i] = GRAY[j];
							GRAY[j] = tmp;
							tmp = REDvalue[i];
							REDvalue[i] = REDvalue[j];
							REDvalue[j] = tmp;
							tmp = GREENvalue[i];
							GREENvalue[i] = GREENvalue[j];
							GREENvalue[j] = tmp;
							tmp = BLUEvalue[i];
							BLUEvalue[i] = BLUEvalue[j];
							BLUEvalue[j] = tmp;
						}
				}
				// we have RGBA values sorted in incremental order
				if (type < 0) // minimum
				{
					samplesARGB[x][y][0] = REDvalue[0];
					samplesARGB[x][y][1] = GREENvalue[0];
					samplesARGB[x][y][2] = BLUEvalue[0];
				} 
				else if (type == 0) // median
				{
					samplesARGB[x][y][0] = REDvalue[(int)REDvalue.length/2];
					samplesARGB[x][y][1] = GREENvalue[(int)GREENvalue.length/2];
					samplesARGB[x][y][2] = BLUEvalue[(int)BLUEvalue.length/2];
				} 
				else if (type > 0) // maximum
				{
					samplesARGB[x][y][0] = REDvalue[REDvalue.length - 1];
					samplesARGB[x][y][1] = GREENvalue[GREENvalue.length - 1];
					samplesARGB[x][y][2] = BLUEvalue[BLUEvalue.length - 1];
				}
				
				
			}
		}	
	
	}
	
	public void blur()
	{
		WritableRaster raster = image.getRaster();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] samples = new int[4 * width * height]; // image pixels buffer, 
													 // pixel values are stored in order: red, green, blue, alfa
		raster.getPixels(0, 0, width, height, samples);
		int[][][] samplesARGB = PictureAnalyser.translateSamples(samples, width, height);
		float[] elements = 
		{
				1.0f, 1.0f, 1.0f,
				1.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 1.0f
		};
		convolve(elements, 3, 3, samplesARGB);
		raster.setPixels(0, 0, width, height, PictureAnalyser.retranslateSamples(samplesARGB, width, height));
	}
	
	public void prewitt()
	{
		WritableRaster raster = image.getRaster();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] samples = new int[4 * width * height]; // image pixels buffer, 
													 // pixel values are stored in order: red, green, blue, alfa
		raster.getPixels(0, 0, width, height, samples);
		int[][][] samplesARGB = PictureAnalyser.translateSamples(samples, width, height);
		int[][][] tmpSamplesARGB = PictureAnalyser.translateSamples(samples, width, height);
		float[] elements = 
		{
				-1.0f, -1.0f, -1.0f,
				-1.0f, 4.0f, -1.0f,
				-1.0f, -1.0f, -1.0f
		};
		convolve(elements, 3, 3, samplesARGB);
		convolve(elements, 3, 3, samplesARGB);
		for (int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
			{
				samplesARGB[x][y][0] = Math.abs(tmpSamplesARGB[x][y][0] - samplesARGB[x][y][0]);
				if (samplesARGB[x][y][0] < 0) samplesARGB[x][y][0] = 0;
				samplesARGB[x][y][1] = Math.abs(tmpSamplesARGB[x][y][1] - samplesARGB[x][y][1]);
				if (samplesARGB[x][y][1] < 0) samplesARGB[x][y][1] = 0;
				samplesARGB[x][y][2] = Math.abs(tmpSamplesARGB[x][y][2] - samplesARGB[x][y][2]);
				if (samplesARGB[x][y][2] < 0) samplesARGB[x][y][2] = 0;
			}
		raster.setPixels(0, 0, width, height, PictureAnalyser.retranslateSamples(samplesARGB, width, height));

	}
	
	public void blackWhite()
	{

		WritableRaster raster = image.getRaster();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] samples = new int[4 * width * height]; // image pixels buffer, 
													 // pixel values are stored in order: red, green, blue, alfa
		raster.getPixels(0, 0, width, height, samples);
		int[][][] samplesARGB = PictureAnalyser.translateSamples(samples, width, height);
		
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				int gray = samplesARGB[x][y][0] + samplesARGB[x][y][0] + samplesARGB[x][y][0];
				gray /= 3;
				samplesARGB[x][y][0] = gray;
				samplesARGB[x][y][1] = gray;
				samplesARGB[x][y][2] = gray;
			}
		}
		raster.setPixels(0, 0, width, height, PictureAnalyser.retranslateSamples(samplesARGB, width, height));	
	}
	
	public void sharpen()
	{
		WritableRaster raster = image.getRaster();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] samples = new int[4 * width * height]; // image pixels buffer, 
													 // pixel values are stored in order: red, green, blue, alfa
		raster.getPixels(0, 0, width, height, samples);
		int[][][] samplesARGB = PictureAnalyser.translateSamples(samples, width, height);
		int[][][] tmpSamplesARGB = PictureAnalyser.translateSamples(samples, width, height);
		float[] elements = 
		{
				1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f
		};
		convolve(elements, 3, 3, samplesARGB);
		for (int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
			{
				samplesARGB[x][y][0] = tmpSamplesARGB[x][y][0] + (tmpSamplesARGB[x][y][0] - samplesARGB[x][y][0]);
				if (samplesARGB[x][y][0] > 255) samplesARGB[x][y][0] = 255;
				else if (samplesARGB[x][y][0] < 0) samplesARGB[x][y][0] = 0;
				samplesARGB[x][y][1] = tmpSamplesARGB[x][y][1] + (tmpSamplesARGB[x][y][1] - samplesARGB[x][y][1]);
				if (samplesARGB[x][y][1] > 255) samplesARGB[x][y][1] = 255;
				else if (samplesARGB[x][y][1] < 0) samplesARGB[x][y][1] = 0;
				samplesARGB[x][y][2] = tmpSamplesARGB[x][y][2] + (tmpSamplesARGB[x][y][2] - samplesARGB[x][y][2]);
				if (samplesARGB[x][y][2] > 255) samplesARGB[x][y][2] = 255;
				else if (samplesARGB[x][y][2] < 0) samplesARGB[x][y][2] = 0;
			}
		raster.setPixels(0, 0, width, height, PictureAnalyser.retranslateSamples(samplesARGB, width, height));

	}
	
	
	public void sobell() {
		WritableRaster raster = image.getRaster();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] samples = new int[4 * width * height]; // image pixels buffer, 
													 // pixel values are stored in order: red, green, blue, alfa
		raster.getPixels(0, 0, width, height, samples);
		int[][][] samplesARGB = PictureAnalyser.translateSamples(samples, width, height);
		int[][][] tmpSamplesARGB = PictureAnalyser.translateSamples(samples, width, height);
		float[] elements = 
		{
				1.0f, 2.0f, 1.0f,
				0.0f, 0.0f, 0.0f,
				-1.0f, -2.0f, -1.0f
		};
		convolve(elements, 3, 3, samplesARGB);
//		float[] elements2 = 
//		{
//				-2.0f, -1.0f, 0.0f,
//				-1.0f, 0.0f, 1.0f,
//				0.0f, 1.0f, 2.0f
//		};
//		convolve(elements2, 3, 3, samplesARGB);
//		float[] elements3 = 
//		{
//				0.0f, 1.0f, 2.0f,
//				-1.0f, 0.0f, 1.0f,
//				-2.0f, -1.0f, 0.0f
//		};
//		convolve(elements3, 3, 3, samplesARGB);
//		float[] elements4 = 
//		{
//				0.0f, -1.0f, -2.0f,
//				1.0f, 0.0f, -1.0f,
//				2.0f, 1.0f, 0.0f
//		};
//		convolve(elements4, 3, 3, samplesARGB);
		for (int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
			{
				samplesARGB[x][y][0] = Math.abs(tmpSamplesARGB[x][y][0] - samplesARGB[x][y][0]);
				samplesARGB[x][y][1] = Math.abs(tmpSamplesARGB[x][y][1] - samplesARGB[x][y][1]);
				samplesARGB[x][y][2] = Math.abs(tmpSamplesARGB[x][y][2] - samplesARGB[x][y][2]);
			}
		raster.setPixels(0, 0, width, height, PictureAnalyser.retranslateSamples(samplesARGB, width, height));
	}

	public void rankingFilter(int type)
	{
		WritableRaster raster = image.getRaster();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] samples = new int[4 * width * height]; // image pixels buffer, 
													 // pixel values are stored in order: red, green, blue, alfa
		raster.getPixels(0, 0, width, height, samples);
		int[][][] samplesARGB = PictureAnalyser.translateSamples(samples, width, height);
		ranking(type, 3, 3, samplesARGB);
		raster.setPixels(0, 0, width, height, PictureAnalyser.retranslateSamples(samplesARGB, width, height));
	}
	
	public void filter()
	{
		WritableRaster raster = image.getRaster();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] samples = new int[4 * width * height]; // image pixels buffer, 
													 // pixel values are stored in order: red, green, blue, alfa
		raster.getPixels(0, 0, width, height, samples);
		int[][][] samplesARGB = PictureAnalyser.translateSamples(samples, width, height);
		
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				int red = samplesARGB[x][y][0];
				int green = samplesARGB[x][y][1];
				int blue = samplesARGB[x][y][2];
				if ((red >= 150 && red <= 170) || (red >= 200 && red <= 230) 
						|| (blue >= 15 && blue <= 25) || (green >= 180 && green <= 230) || (green >= 90 && green <= 120))
				{
					samplesARGB[x][y][0] = 255;
					samplesARGB[x][y][1] = 255;
					samplesARGB[x][y][2] = 255;
				}
//				if (blue <= 40 || blue >= 200 || (green >= 90 && green <= 110) || (green >= 90 && green <= 110))
//				if (gray < 20 || gray > 150)
//				{
//					samplesARGB[x][y][0] = 255;
//					samplesARGB[x][y][1] = 255;
//					samplesARGB[x][y][2] = 255;
//				}
			}
		}
		
		samples = PictureAnalyser.retranslateSamples(samplesARGB, width, height);
		raster.setPixels(0, 0, width, height, samples);
	}
	
	
	public void onlyBlue()
	{
		WritableRaster raster = image.getRaster();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] samples = new int[4 * width * height]; // image pixels buffer, 
													 // pixel values are stored in order: red, green, blue, alfa
		raster.getPixels(0, 0, width, height, samples);
		int[][][] samplesARGB = PictureAnalyser.translateSamples(samples, width, height);
		
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				int red = samplesARGB[x][y][0];
				int green = samplesARGB[x][y][1];
				int blue = samplesARGB[x][y][2];
				int[] hsv = new int[3];
				rgb2hsv(red, green, blue, hsv);
				int gray = (samplesARGB[x][y][0] + samplesARGB[x][y][1] + samplesARGB[x][y][2])/3;
//				 blur, filter, close
				if ((hsv[0] <= 110 || hsv[0] >= 150) && // dragon 
						(hsv[0] <= -10 || hsv[0] >= 20) && // cross
						(hsv[0] <= 214 || gray <=1) // border
						|| gray >= 250) // border i cross
				{
					samplesARGB[x][y][0] = 255;
					samplesARGB[x][y][1] = 255;
					samplesARGB[x][y][2] = 255;
				}
				else
				{
					samplesARGB[x][y][0] = 0;
					samplesARGB[x][y][1] = 0;
					samplesARGB[x][y][2] = 0;
				}
			}
		}
		
		samples = PictureAnalyser.retranslateSamples(samplesARGB, width, height);
		raster.setPixels(0, 0, width, height, samples);
	}
	
	public void seed(int[][][] samples, int x, int y)
	{
		
	}
	
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
	
	private void rgb2hsv(int r, int g, int b, int hsv[]) {
		
		int min;    //Min. value of RGB
		int max;    //Max. value of RGB
		int delMax; //Delta RGB value
		
		if (r > g) { min = g; max = r; }
		else { min = r; max = g; }
		if (b > max) max = b;
		if (b < min) min = b;
								
		delMax = max - min;
	 
		float H = 0, S;
		float V = max;
		   
		if ( delMax == 0 ) { H = 0; S = 0; }
		else {                                   
			S = delMax/255f;
			if ( r == max ) 
				H = (      (g - b)/(float)delMax)*60;
			else if ( g == max ) 
				H = ( 2 +  (b - r)/(float)delMax)*60;
			else if ( b == max ) 
				H = ( 4 +  (r - g)/(float)delMax)*60;   
		}
								 
		hsv[0] = (int)(H);
		hsv[1] = (int)(S*100);
		hsv[2] = (int)(V*100);
	}
	private BufferedImage image;
	
}
