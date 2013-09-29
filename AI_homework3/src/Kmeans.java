import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class Kmeans {

	int[][] theta;
	int[] h;

	public void cluster(int numClusters, int[][] theMatrix, int theWidth,
			int theHeight) {

		// contains average Red, Green, and Blue values for each cluster
		theta = new int[numClusters][3];
		// vector that contains each pixel's assignment
		h = new int[theWidth * theHeight];

		// randomly assign pixels a cluster from 1 to numClusters
		Random rand = new Random();
		int min = 1;
		int max = numClusters;

		// nextInt is normally exclusive of the top value,
		// so add 1 to manumClusterse it inclusive

		for (int i = 0; i < (theWidth * theHeight); i++) {
			h[i] = rand.nextInt(max - min + 1) + min;
		}

		// calcMeans runs once to initialize means from random assignment
		calcMeans(numClusters, theWidth, theHeight, h, theMatrix, theta);
		for (int i = 0; i < 20; i++) {
			// System.out.println("Iteration " + i + ":");
			closest(numClusters, theWidth, theHeight, h, theMatrix, theta);
			calcMeans(numClusters, theWidth, theHeight, h, theMatrix, theta);
		}

		System.out.println("Final: ");
		for (int clusterNum = 0; clusterNum < numClusters; clusterNum++) {
			int redMean = theta[clusterNum][0];
			int greenMean = theta[clusterNum][1];
			int blueMean = theta[clusterNum][2];

			System.out.println("Cluster [" + (clusterNum + 1) + "] redMean: "
					+ redMean + " greenMean: " + greenMean + " blueMean: "
					+ blueMean);
		}

		drawClusters(numClusters, theWidth, theHeight, h, theta);
	}

	public void drawClusters(int k, int w, int h, int[] hVector, int[][] t) {
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		for (int clusterNum = 0; clusterNum < k; clusterNum++) {
			int redMean = theta[clusterNum][0];
			int greenMean = theta[clusterNum][1];
			int blueMean = theta[clusterNum][2];
			int RGBMean = (redMean << 16) | (greenMean << 8) | blueMean;

			for (int pixelNum = 0; pixelNum < (w * h); pixelNum++) {
				if (hVector[pixelNum] == clusterNum) {
					OrderedPair op = rowToXY(pixelNum, w, h);

					img.setRGB(op.getX(), op.getY(), RGBMean);
				}
			}
		}

		File f = new File("/Users/jsreese/Desktop/unc-2_jsreese.png");
		try {
			ImageIO.write(img, "PNG", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public OrderedPair rowToXY(int row, int w, int h) {
		OrderedPair op = new OrderedPair();
		int x = (row % w);
		int y = (row / w);
		op.setX(x);
		op.setY(y);
		return op;
	}

	// theRow is just a pixel in the pixelArray
	public int XYtoRow(int x, int y, int w, int h) {
		return (y * w) + x;
	}

	public void calcMeans(int k, int w, int h, int[] hVector, int[][] m,
			int[][] t) {
		// for every cluster
		for (int clusterNum = 0; clusterNum < k; clusterNum++) {
			// for each pixel in vector, calculate the new mean for the cluster

			int redSum = 0;
			int greenSum = 0;
			int blueSum = 0;
			int count = 0;
			int redMean = 0;
			int greenMean = 0;
			int blueMean = 0;

			for (int j = 0; j < (w * h); j++) {
				if (hVector[j] == clusterNum) {
					redSum += m[j][0];
					greenSum += m[j][1];
					blueSum += m[j][2];
					count++;
				}
			}
			if (count != 0) {
				redMean = redSum / count;
				greenMean = greenSum / count;
				blueMean = blueSum / count;
			}

			// update cluster's mean value
			t[clusterNum][0] = redMean;
			t[clusterNum][1] = greenMean;
			t[clusterNum][2] = blueMean;

		}
	}

	public void closest(int k, int w, int h, int[] hVector, int[][] m, int[][] t) {
		// for each pixel, store cluster number it's closest to in the first
		// array element, and its distance in the second
		int[][] closestArray = new int[w * h][2];

		// initialize distance for each cluster to be "infinity"
		for (int i = 0; i < w * h; i++) {
			closestArray[i][1] = 100000000;
		}

		// iterate through clusters and pixels to determine new clustering
		for (int clusterNum = 0; clusterNum < k; clusterNum++) {
			int cRed = t[clusterNum][0];
			int cGreen = t[clusterNum][1];
			int cBlue = t[clusterNum][2];

			// compare this value with those of each pixel
			for (int pixelNum = 0; pixelNum < (w * h); pixelNum++) {
				int pRed = m[pixelNum][0];
				int pGreen = m[pixelNum][1];
				int pBlue = m[pixelNum][2];

				int distance = 0;
				distance += Math.abs(cRed - pRed);
				distance += Math.abs(cGreen - pGreen);
				distance += Math.abs(cBlue - pBlue);


				if (distance < closestArray[pixelNum][1]) {
					closestArray[pixelNum][0] = clusterNum;
					closestArray[pixelNum][1] = distance;
				}
			}
		}

		// update hVector with which pixels are now clustered to which k
		for (int i = 0; i < (w * h); i++) {
			hVector[i] = closestArray[i][0];
		}
	}
}
