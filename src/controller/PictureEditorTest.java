package controller;

import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PictureEditorTest {

	@Before
	public void setUp() throws Exception {
		
		BufferedImage image = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = image.getRaster();
		int width = image.getWidth();
		int height = image.getHeight();
		int samples[] = new int[4 * width * height];
		 
		raster.getPixels(0, 0, width, height, samples);
		int[][][] translatedSamples = PictureAnalyser.translateSamples(samples, width, height);
		
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
			{
				if (i % 4 == 2 && j % 4 == 2)
				{
					translatedSamples[i][j][0] = 15;
					translatedSamples[i][j][1] = 15;
					translatedSamples[i][j][2] = 15;
					translatedSamples[i][j][3] = 0;
				}
//				System.out.println(translatedSamples[i][j][0]);
//				System.out.println(translatedSamples[i][j][1]);
//				System.out.println(translatedSamples[i][j][2]);
//				System.out.println(translatedSamples[i][j][3]);
			}
		samples = PictureAnalyser.retranslateSamples(translatedSamples, width, height);
		raster.setPixels(0, 0, width, height, samples);
		image.setData(raster);
		testedEditor = new PictureEditor(image);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBlur() {
		System.out.println("testBlur()");
		testedEditor.blur();
		testedEditor.blur();
		BufferedImage image = testedEditor.getImage();
		WritableRaster raster = image.getRaster();
		int width = image.getWidth();
		int height = image.getHeight();
		int samples[] = new int[4 * width * height];
		raster.getPixels(0, 0, width, height, samples);
		int[][][] translatedSamples = PictureAnalyser.translateSamples(samples, width, height); 
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
			{
				assertTrue(translatedSamples[i][j][0] == 0);
				assertTrue(translatedSamples[i][j][1] == 0);
				assertTrue(translatedSamples[i][j][2] == 0);
				assertTrue(translatedSamples[i][j][3] == 0);
			}
	}

	@Test
	public void testBlackWhite() {
		System.out.println("testBlackWhite()");
		testedEditor.blackWhite();
		BufferedImage image = testedEditor.getImage();
		WritableRaster raster = image.getRaster();
		int width = image.getWidth();
		int height = image.getHeight();
		int samples[] = new int[4 * width * height];
		raster.getPixels(0, 0, width, height, samples);
		int[][][] translatedSamples = PictureAnalyser.translateSamples(samples, width, height); 
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
			{
				assertTrue(translatedSamples[i][j][0] == translatedSamples[i][j][1]);
				assertTrue(translatedSamples[i][j][1] == translatedSamples[i][j][2]);
				assertTrue(translatedSamples[i][j][2] == translatedSamples[i][j][0]);
			}
	}

	private PictureEditor testedEditor;
}
