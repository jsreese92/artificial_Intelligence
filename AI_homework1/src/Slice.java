public class Slice {
	public int[][] pixels;

	public Slice(int[][] theTempSlice) {
		pixels = theTempSlice;
	}

	public void printSlice() {
		for (int numRows = 0; numRows < 2396; numRows++) {
			for (int numColumns = 0; numColumns < 20; numColumns++) {
				System.out.print(this.pixels[numRows][numColumns]);
			}
			System.out.println();
		}
	}
}
