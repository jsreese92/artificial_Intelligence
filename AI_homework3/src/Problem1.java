import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Problem1 {

	public BufferedImage getImage() {
		BufferedImage img = null;
		try {
			img = ImageIO
					.read(new File("/Users/jsreese/Desktop/unc-bigger.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}

	public int[][] loadRGB(BufferedImage theImage) {
		int[] imageArray = null;
		int[][] imageMatrix = null;

		int w, h;
		int pixels[];
		w = theImage.getWidth();
		h = theImage.getHeight();

		pixels = new int[w * h];
		theImage.getRGB(0, 0, w, h, pixels, 0, w);

		// height and width of picture, with depth of 3 for each RGB value
		imageArray = new int[h * w * 3];
		imageMatrix = new int[h * w][3];
		for (int pixelNum = 0; pixelNum < (w * h); pixelNum++) {
			int index = pixelNum * 3;
			imageArray[index] = (pixels[pixelNum] >> 16 & 0xff);
			imageMatrix[pixelNum][0] = (pixels[pixelNum] >> 16 & 0xff);
			imageArray[index + 1] = (pixels[pixelNum] >> 8 & 0xff);
			imageMatrix[pixelNum][1] = (pixels[pixelNum] >> 8 & 0xff);
			imageArray[index + 2] = (pixels[pixelNum] & 0xff);
			imageMatrix[pixelNum][2] = (pixels[pixelNum] & 0xff);
		}
		return imageMatrix;
	}

	public void makeImage(int[][] theImageMatrix, int theWidth, int theHeight) {
		int w = theWidth;
		int h = theHeight;
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		int matrixIndex = 0;
		for (int yIndex = 0; yIndex < h; yIndex++) {
			for (int xIndex = 0; xIndex < w; xIndex++) {

				int red = theImageMatrix[matrixIndex][0];
				int green = theImageMatrix[matrixIndex][1];
				int blue = theImageMatrix[matrixIndex][2];
				int theRGB = (red << 16) | (green << 8) | blue;

				img.setRGB(xIndex, yIndex, theRGB);
				matrixIndex++;
			}
		}
		File f = new File("/Users/jsreese/Desktop/test.png");
		try {
			ImageIO.write(img, "PNG", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
