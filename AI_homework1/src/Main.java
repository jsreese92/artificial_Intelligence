import java.io.IOException;

public class Main {
	public static void main(String args[]) throws IOException{		
		String path = "/Users/jsreese/Desktop/hw1";
		int numRows = 2396;
		int numColumns = 20;
		
		Problem5 p = new Problem5(path, numRows, numColumns);
		p.readSlices();
		p.displaySlices(p.hillClimb(p.disagreementArray()));
	}
}
