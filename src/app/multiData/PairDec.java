package app.multiData;

public class PairDec {
	String pair;
	int dec;
	
	public PairDec(String pair, int dec) {
		this.pair = pair;
		this.dec = dec;
	}
	
	public String getPair() {
		return pair;
	}
	
	public void setPair(String pair) {
		this.pair = pair;
	}
	
	public int getDec() {
		return dec;
	}
	
	public void setDec(int dec) {
		this.dec = dec;
	}

	@Override
	public String toString() {
		return "PairDec [" + pair + " "+ dec + "]";
	}
	
	
}
