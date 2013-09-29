import java.awt.image.BufferedImage;
import java.io.IOException;


public class Main {
	public static void main(String args[]){
		
		Problem1 p = new Problem1();
		BufferedImage img = p.getImage();
		int[][] pixelMatrix = p.loadRGB(img);

		Kmeans k = new Kmeans();
		k.cluster(2, pixelMatrix, img.getWidth(), img.getHeight());
		
		
		Problem2 p2 = new Problem2();
		try {
			int[][] parsedMatrix = p2.parseData(100,560);
			double[][] centeredMatrix = p2.centering(parsedMatrix, 100, 560);
			double[][] covMatrix = p2.covarianceMatrix(centeredMatrix, 100, 560);
			System.out.println(covMatrix[0][0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
