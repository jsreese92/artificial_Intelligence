import java.awt.image.BufferedImage;


public class Main {
	public static void main(String args[]){
		
		Problem1 p = new Problem1();
		BufferedImage theImage = p.getImage();
		int width = theImage.getWidth();
		int height = theImage.getHeight();
		int[] pixels = p.loadRGB(theImage);
		int totPots = p.getTotalPotentials(width, height);
		FunctionNode[] funcArray = new FunctionNode[totPots];
		Pixel[] pixelArray = p.populateFuncNodes(pixels, funcArray, width, height);
		double[][][] mg_x = p.initMg_x(width,height,pixelArray);
		double[][][] allMessages = p.initAllMessages(width, height);
		double[][][] mf_x = p.initMf_x(width, height);
		allMessages = p.updateAllMessages(mg_x, mf_x, allMessages, pixelArray, width, height);
		int[] rec = p.recontruct(allMessages, width, height);
		
		// executing this function seems to be what pushes Java's heap space
		//over the limit, a problem I have described at the bottom of 
		//Problem1.java. Since following functions require mx_f, I have not 
		//been able to test them properly. 
		double[][][] mx_f = p.initMx_f(width, height);
		p.messageUpdateMx_f(mx_f, allMessages, mf_x, funcArray, width, height);
		
	}
}
