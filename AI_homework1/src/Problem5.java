import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Problem5 {
	String path;
	Map<String, Slice> theSlices;
	int[][] tempSlice;
	int numRows;
	int numColumns;

	public Problem5(String thePath, int theNumRows, int theNumColumns) {
		path = thePath;
		numRows = theNumRows;
		numColumns = theNumColumns;
		tempSlice = new int[numRows][numColumns];
		theSlices = new HashMap<String, Slice>();
	}

	// reads the data from slices into a hashmap, the key for which is
	// the name of the file, and the value is a 2D array of pixels
	public void readSlices() {
		File theFolder = new File(path);
		File[] listOfFiles = theFolder.listFiles();

		for (File file : listOfFiles) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));

				String tempLine;
				int rowIndex = 0;
				// For every line in slices file, read its values
				while ((tempLine = br.readLine()) != null) {
					// System.out.println(tempLine);
					String[] splitTempLine = tempLine.split(" ");

					int columnIndex = 0;
					for (String word : splitTempLine) {
						if (word.length() > 2) { // eliminates whitespace words
							if (word.equals("2.5500000e+002")) {
								tempSlice[rowIndex][columnIndex] = 1; // white
							} else {
								tempSlice[rowIndex][columnIndex] = 0; // black
							}
							columnIndex++;
						}
					}
					rowIndex++; // increment row number before next line is
								// read
				}
				// place slice into hashmap, accessible by name of file
				theSlices.put(file.getName(), new Slice(tempSlice));
				// reset tempslice for use with next slice
				tempSlice = new int[numRows][numColumns];
				// close buffered reader for current slices file
				br.close();
			} catch (IOException e) {
				System.err.println("Error: " + e);
			}
		}
	}

	public void displaySlices(int[] theOrder) throws IOException {
		BufferedImage img = new BufferedImage(3020, 2396,
				BufferedImage.TYPE_INT_RGB);
		int slicesProcessed = 0;

		// for every slice in the set
		for (int i = 0; i < 151; i++) {
			Slice workingSlice = theSlices.get("slices"
					+ Integer.toString(theOrder[i]));
			for (int rowNumber = 0; rowNumber < 2396; rowNumber++) {
				for (int columnNumber = 0; columnNumber < 20; columnNumber++) {
					if (workingSlice.
							pixels[rowNumber][columnNumber] == 1) { // white
						img.setRGB((columnNumber + (slicesProcessed * 20)),
								rowNumber, Color.white.getRGB());

					} else if (workingSlice.
							pixels[rowNumber][columnNumber] == 0) { // black
						img.setRGB((columnNumber + (slicesProcessed * 20)),
								rowNumber, Color.black.getRGB());
					} else
						System.out.println("Error in displaySlices");
				}
			}
			slicesProcessed++;
		}

		File f = new File("/Users/jsreese/Desktop/dump/dump.jpg");
		ImageIO.write(img, "JPG", f);
	}

	public double[][] disagreementArray() {
		double dArray[][] = new double[151][151];
		for (int iSliceNum = 1; iSliceNum <= 151; iSliceNum++) {
			for (int jSliceNum = 1; jSliceNum <= 151; jSliceNum++) {
				Slice iSlice = theSlices.get("slices"
						+ Integer.toString(iSliceNum));
				Slice jSlice = theSlices.get("slices"
						+ Integer.toString(jSliceNum));

				// Have to put -1 because slices files start out numbered
				// from 1 to 151
				dArray[iSliceNum - 1][jSliceNum - 1] = computeDisagreement(
						iSlice, jSlice);
			}
		}
		return dArray;
	}

	// computes disagreement across all slices
	private double totalDisagreement(double theArray[][], int theOrder[]) {
		double disagreement = 0;
		for (int i = 0; i < 149; i++) {
			disagreement = disagreement
					+ (theArray[(theOrder[i])][(theOrder[i + 1])]);
		}
		return disagreement;
	}

	// implements pure hill climbing algorithm to reorder slices
	public int[] hillClimb(double theArray[][]) {
		int[] order = new int[151];
		int numIterations = 0;
		// set initial order, which is just natural counting order
		for (int i = 0; i < 151; i++)
			order[i] = i + 1;
		boolean stop = false;

		double theDisagreement = totalDisagreement(theArray, order);
		while (!stop) {
			int newOrder[] = incrementOrder(order, numIterations);
			double compareDisagreement = totalDisagreement(theArray, newOrder);
			if (compareDisagreement < theDisagreement) {
				order = newOrder;
				theDisagreement = compareDisagreement;
				stop = true;
			} else
				numIterations++;
		}
		System.out.println("disagreement: " + theDisagreement);
		System.out.print("order: ");
		showOrder(order);
		return order;
	}

	// increments order as shown on homework assignment explanation
	private int[] incrementOrder(int orderArray[], int theNumIterations) {
		int temp = 0;
		temp = orderArray[theNumIterations + 1];
		orderArray[theNumIterations + 1] = orderArray[theNumIterations];
		orderArray[theNumIterations] = temp;
		return orderArray;
	}

	// shows order of final ordering array
	private void showOrder(int orderArray[]) {
		for (int i = 0; i < 151; i++) {
			System.out.print(orderArray[i] + ",");
		}
		System.out.println();
	}

	// computes disagreement between two slices
	private double computeDisagreement(Slice thisSlice, Slice otherSlice) {
		double disagreement = 0;
		for (int i = 1; i < 2396; i++) {
			disagreement = disagreement + (thisSlice.pixels[i][19])
					- (otherSlice.pixels[i][0]);
		}
		disagreement = Math.sqrt(Math.abs(disagreement));
		return disagreement;
	}
}
