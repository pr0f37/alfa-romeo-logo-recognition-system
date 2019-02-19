package controller;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PictureAnalyserTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTranslateSamples() {
		int[] testSamples = {1,2,3,4,5,6,7,8};
		int testW = 1;
		int testH = 2;
		
		int[][][] testSamplesResult = PictureAnalyser.translateSamples(testSamples, testW, testH);
			
		int[][][] expectedResult = //new int[1][2][4];
		{
			{
				{
					1,2,3,4
				},
				{
					5,6,7,8
				}
			}
		};
		for (int x = 0; x < testW; x++)
		{
			for (int y = 0; y < testH; y++)
			{
				
				assertTrue(testSamplesResult[x][y][0] == expectedResult[x][y][0]);
				assertTrue(testSamplesResult[x][y][1] == expectedResult[x][y][1]);
				assertTrue(testSamplesResult[x][y][2] == expectedResult[x][y][2]);
				assertTrue(testSamplesResult[x][y][3] == expectedResult[x][y][3]);
				
			}
		}
	}

	@Test
	public void testRetranslateSamples() {
		int[] testSamples = {1,2,3,4,5,6,7,8};
		int testW = 1;
		int testH = 2;
		int[][][] testSamplesResult = PictureAnalyser.translateSamples(testSamples, testW, testH);
		int[] testExpectedResult = PictureAnalyser.retranslateSamples(testSamplesResult, 1, 2);
		for (int x = 0; x < testSamples.length; x++)
		{
				assertTrue(testSamples[x] == testExpectedResult[x]);
		}
	}

}
