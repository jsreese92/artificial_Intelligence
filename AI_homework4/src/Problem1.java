import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Problem1 {

	public BufferedImage getImage() {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("/Users/jsreese/Desktop/hw4.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}

	public int[] loadRGB(BufferedImage theImage) {
		int w, h;
		int pixels[];
		w = theImage.getWidth();
		h = theImage.getHeight();

		pixels = new int[w * h];
		theImage.getRGB(0, 0, w, h, pixels, 0, w);

		// since grayscale, turn RGB value into just B value
		for (int i = 0; i < (w * h); i++) {
			pixels[i] = (pixels[i] & 0xff);
		}
		return pixels;
	}

	// 3.a
	public int getPixelIndex(OrderedPair image_i_j, int w, int h) {
		int i = image_i_j.getI();
		int j = image_i_j.getJ();

		int theIndex = ((i * w) + j);
		return theIndex;
	}

	// 3.b
	public OrderedPair getImage_i_j(int theIndex, int w, int h) {
		int i = (theIndex / w);
		int j = (theIndex % w);
		OrderedPair op = new OrderedPair(i, j);
		return op;
	}

	// 3.c + 3.d
	public Pixel[] populateFuncNodes(int[] pixels, FunctionNode[] toUpdate,
			int w, int h) {
		Pixel[] pixelArray = new Pixel[w * h];

		// initialize pixels
		for (int i = 0; i < w * h; i++) {
			Pixel t = new Pixel();
			t.rgbValue = pixels[i];
			pixelArray[i] = t;
		}

		// initialize function nodes
		int numPixels = w * h;
		FunctionNode[] funcArray = getFuncArray(w, h);
		int numNodes = 0;
		for (int i = 0; i < numPixels; i++) {

			// set E/W nodes
			FunctionNode ew = new FunctionNode();
			if (!((i + 1) % w == 0)) { // if not rightmost column
				ew.from = pixelArray[i];
				ew.to = pixelArray[i + 1];
				ew.index = numNodes;
				pixelArray[i].eNode = ew;
				pixelArray[i + 1].wNode = ew;
				funcArray[numNodes] = ew;
				numNodes++;
			}

			// set N/S nodes
			FunctionNode ns = new FunctionNode();
			if (i < (w * h) - w) { // if not bottom row
				ns.from = pixelArray[i];
				ns.to = pixelArray[i + w];
				ns.index = numNodes;
				pixelArray[i].sNode = ns;
				pixelArray[i + w].nNode = ns;
				funcArray[numNodes] = ns;
				numNodes++;
			}
		}
		toUpdate = funcArray;
		return pixelArray;
	}

	// 3.e, number of potentials connecting true pixels
	public int getTotalPotentials(int w, int h) {
		int total = 0;
		total += (w * h) - w; // all N/S nodes
		total += (h * w) - h; // all E/W ndoes
		return total;
	}

	public FunctionNode[] getFuncArray(int w, int h) {
		int totPot = getTotalPotentials(w, h);
		FunctionNode[] funcArray = new FunctionNode[totPot];
		// System.out.println(totPot);
		for (int i = 0; i < totPot; i++) {
			FunctionNode node = new FunctionNode();
			funcArray[i] = node;
		}
		return funcArray;
	}

	// 3.f
	public double[][] computeTable(int[] pixels) {
		double[][] theTable = new double[256][256];

		for (int i = 0; i < 256; i++) {
			for (int j = 0; j < 256; j++) {
				theTable[i][j] = potentialFunction(pixels[i], pixels[j]);
			}
		}
		return theTable;
	}

	// 4.b
	public double[][][] initMf_x(int w, int h) {
		double[][][] mf_x;
		int totPot = getTotalPotentials(w, h);
		mf_x = new double[256][2][totPot];

		// initialize to all 0's
		for (int i = 0; i < 256; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = 0; k < totPot; k++) {
					mf_x[i][j][k] = 0;
				}
			}
		}

		return mf_x;
	}

	// 4.c
	public double[][][] initMx_f(int w, int h) {
		double[][][] mx_f;
		int totPot = getTotalPotentials(w, h);
		mx_f = new double[256][2][totPot];

		// initialize to all 0's
		for (int i = 0; i < 256; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = 0; k < totPot; k++) {
					mx_f[i][j][k] = 0;
				}
			}
		}
		return mx_f;
	}

	// /4.d
	public double[][][] mg_x(int w, int h) {
		double[][][] mg_x = new double[256][w][h];
		return mg_x;
	}

	// 4.e
	public double[][][] initMg_x(int w, int h, Pixel[] pixels) {
		double[][][] mg_x = mg_x(w, h);

		// initialize all to 0
		for (int r = 0; r < 256; r++) {
			for (int s = 0; s < w; s++) {
				for (int t = 0; t < h; t++) {
					mg_x[r][s][t] = 0;
				}
			}
		}

		// for each pixel, set its value in mg_x to 1 in the appropriate spot
		// in the matrix that matches its RGB value
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				OrderedPair op = new OrderedPair(i, j);
				int pIndex = getPixelIndex(op, w, h);
				int pixelValue = pixels[pIndex].rgbValue;
				mg_x[pixelValue][i][j] = 1;
			}
		}
		return mg_x;
	}

	// 5
	public double[][][] initAllMessages(int w, int h) {
		double[][][] allMessages = new double[256][w][h];
		return allMessages;
	}

	// messages passed north and east direction = 1
	// messages passes south and west direction = 0

	public double[][][] updateAllMessages(double[][][] mg_x, double[][][] mf_x,
			double[][][] allMessages, Pixel[] pixels, int w, int h) {
		for (int rgb = 0; rgb < 256; rgb++) {
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					OrderedPair op = new OrderedPair(i, j);
					int pIndex = getPixelIndex(op, w, h);
					Pixel thePixel = pixels[pIndex];
					double sum = 0;
					if (thePixel.nNode != null) {
						// nNode sends message south to pixel, d = 0
						sum += mf_x[rgb][0][thePixel.nNode.index];
					}
					if (thePixel.eNode != null) {
						// eNode sends message west to pixel, d = 0
						sum += mf_x[rgb][0][thePixel.eNode.index];
					}
					if (thePixel.sNode != null) {
						// sNode sends message north to pixel, d = 1
						sum += mf_x[rgb][1][thePixel.sNode.index];
					}
					if (thePixel.wNode != null) {
						// wNode sends message east to pixel, d = 1
						sum += mf_x[rgb][1][thePixel.wNode.index];
					}
					allMessages[rgb][i][j] = sum;
				}
			}
		}
		// update allMessages with messages from noisy pixels (g function nodes)
		for (int r = 0; r < 256; r++) {
			for (int s = 0; s < h; s++) {
				for (int t = 0; t < w; t++) {
					allMessages[r][s][t] += mg_x[r][s][t];
				}
			}
		}
		return allMessages;
	}

	public int[] recontruct(double[][][] allMsgs, int w, int h) {
		double[][] temp = new double[w][h];
		double max = -1.0;
		for (int rgb = 0; rgb < 256; rgb++) {
			for (int j = 0; j < w; j++) { // j for width, outer loop
				for (int i = 0; i < h; i++) { // i for height
					if (allMsgs[rgb][i][j] > max) {
						max = allMsgs[rgb][i][j];
						temp[i][j] = rgb; // signifies most likely rgb value
					}
				}
			}
		}
		int[] rec = new int[w * h];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				OrderedPair op = new OrderedPair(i, j);
				int theIndex = getPixelIndex(op, w, h);
				rec[theIndex] = (int) temp[i][j];
			}
		}
		return rec;
	}

	public void messageUpdateMx_f(double[][][] mx_f, double[][][] allMsgs,
			double[][][] mf_x, FunctionNode[] funcArray, int w, int h) {
		int nodeNum = 0;
		for (int rgb = 0; rgb < 256; rgb++) {
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					for (int d = 0; d < 2; d++) {
						FunctionNode theNode = funcArray[nodeNum];
						double updateVal = allMsgs[rgb][i][j]
								- mf_x[rgb][d][theNode.index];
						nodeNum++;
						int rd = -1;
						if (d == 0) {
							rd = 1;
						} else if (d == 1) {
							rd = 0;
						}
						mx_f[rgb][rd][theNode.index] = updateVal;

					}
				}
			}
		}
	}

	// 6
	public void messageUpdateMf_x(double[][][] mx_f, double[][][] mf_x,
			FunctionNode[] funcArray, int w, int h) {
		int totPots = getTotalPotentials(w, h);
		for (int rgb1 = 0; rgb1 < 256; rgb1++) {
			for (int rgb2 = 0; rgb2 < 256; rgb2++) {
				double updateVal = 0;
				for (int i = 0; i < totPots; i++) {
					for (int d = 0; d < 2; d++){
						double val2 = mx_f[rgb2][d][i];
						double val1 = potentialFunction(rgb1, rgb2);
						updateVal = val1 + val2;
						mf_x[rgb1][d][i] = updateVal;
					}
				}
			}
		}
	}
	
	//7 
	//I ran into the following error: 
	//Java.lang.OutOfMemoryError: Java heap space
	//when trying to run the above code. I think this is due to the large
	//number of doubles Java is having to keep track of, especially when
	//processing the larger image. Therefore, I was not
	//able to reconstruct the image as a png file, but I believe the code above
	//is correct.
	
	//8
	//Similarly, since I ran into the above error for 7, I was not able to
	//test different h versions. In hopes of getting at least partial extra 
	//credit, though, I have implemented different h versions, called 
	//potentialFunction2 and potentialFunction3 respectively. Had I not run
	//into the Java memory error, it would have simply taken changing the 
	//function name in my code to test these.

	public double potentialFunction(int pixel1, int pixel2) {
		double h;
		boolean wasNegative = false;
		double exponent = (2.0 / 50);
		int a = pixel1 - pixel2;
		if (a < 0) {
			a = -a;
			wasNegative = true;
		}
		h = -Math.pow(a, exponent);
		if (wasNegative) {
			h = -h;
		}
		return h;
	}
	
	public double potentialFunction2(int pixel1, int pixel2){
		double h = 0;
		h = -Math.abs((pixel1 - pixel2)/50);
		return h;
	}
	
	public double potentialFunction3(int pixel1, int pixel2){
		double h = 0;
		boolean wasNegative = false;
		double exponent = 2.0;
		int a = pixel1 - pixel2;
		if (a < 0){
			a = -a;
			wasNegative = true;
		}
		h = Math.pow(a, exponent);
		if (wasNegative){
			h = -h;
		}
		return h;
	}
}
