package app.entities;

public class PairDec {
	
	private String pair;
	private int dec;
	private Boolean good;
	
	public PairDec(String pair, int dec) {
		this.pair = pair;
		this.dec = dec;
		this.good = false;
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
	
	public Boolean getGood() {
		return good;
	}

	public void setGood(Boolean good) {
		this.good = good;
	}

	@Override
	public String toString() {
		return "PairDec [" + pair + " "+ dec + " " + good + "]";
	}
	
	
}
