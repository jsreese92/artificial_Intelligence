
public class OrderedPair {
	public int i; //vertical index
	public int j; //horizontal index

	public OrderedPair(int theI, int theJ){
		i = theI;
		j = theJ;
	}
	public void setJ(int theJ){
		j = theJ;
	}
	
	public void setI(int theI){
		i = theI;
	}
	
	public int getJ(){
		return j;
	}
	
	public int getI(){
		return i;
	}

}
