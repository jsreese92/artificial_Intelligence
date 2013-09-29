import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Problem2 {

	public int[][] parseData(int numFaces, int numValues) throws IOException {
		String[][] tempArray = new String[numFaces][numValues];
		int[][] facesArray = new int[numFaces][numValues];
		File faces = new File("/Users/jsreese/Desktop/faces.txt");

		BufferedReader br = new BufferedReader(new FileReader(faces));

		String tempLine;
		int count = 0;
		// For every line in slices file, read its values
		while ((tempLine = br.readLine()) != null) {
			tempArray[count] = tempLine.split("\\s+");
			count++;
		}
		br.close();

		for (int i = 0; i < numFaces; i++) {
			for (int j = 0; j < numValues; j++) {
				int temp;
				temp = Integer.parseInt(tempArray[i][j]);
				facesArray[i][j] = temp;
			}
		}

		return facesArray;
	}

	public double[][] centering(int[][] theMatrix, int numFaces, int numValues) {
		double[][] centeredMatrix = new double[numFaces][numValues];

		for (int valueIndex = 0; valueIndex < numValues; valueIndex++) {
			int columnTotal = 0;
			double columnMean = 0;
			for (int faceIndex = 0; faceIndex < numFaces; faceIndex++) {
				columnTotal += theMatrix[faceIndex][valueIndex];
			}
			columnMean = columnTotal / numFaces;

			// subtract mean from each value in column
			for (int faceIndex = 0; faceIndex < numFaces; faceIndex++) {
				double temp = theMatrix[faceIndex][valueIndex];
				temp -= columnMean;
				centeredMatrix[faceIndex][valueIndex] = temp;
			}
		}
		return centeredMatrix;
	}

	public double[][] covarianceMatrix(double[][] theCenteredMatrix,
			int numFaces, int numValues) {
		double[][] covMatrix = new double[numValues][numValues];

		// sum up across all rows
		for (int i = 0; i < numValues; i++) {
			for (int j = 0; j < numValues; j++) {
				int sum = 0;
				for (int rowNumber = 0; rowNumber < numFaces; rowNumber++) {
					sum += theCenteredMatrix[rowNumber][i]
							* theCenteredMatrix[rowNumber][j];
				}
				double coef = .01; // (1/100)
				covMatrix[i][j] = coef * sum;
			}
		}
		return covMatrix;
	}
}
