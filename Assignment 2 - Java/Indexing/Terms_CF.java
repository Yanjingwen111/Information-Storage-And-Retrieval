package Indexing;

public class Terms_CF {
	private int term_id;
	private int cf;
	
	//term id and collection frequency
	public Terms_CF(int x, int y) {
		this.term_id = x;
		this.cf = y;
	}
	//set term id
	public void setTermID (int x) {
		this.term_id = x;
	}
	//get term id
	public int getTermID () {
		return term_id;
	}
	//set collection frequency
	public void setCf (int x) {
		this.cf = x;
	}
	//get collection frequency
	public int getCf () {
		return cf;
	}
	//calculate collection frequency
	public void cal_CF () {
		this.cf = this.cf + 1;
	}
}