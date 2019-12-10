package app.entities;

public class PairDec {
	
	private String pair;
	private int dec;
	private boolean good;
	
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
	
	public boolean getGood() {
		return good;
	}

	public void setGood(boolean good) {
		this.good = good;
	}

	@Override
	public String toString() {
		return "PairDec [" + pair + " "+ dec + " " + good + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pair == null) ? 0 : pair.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PairDec other = (PairDec) obj;
		if (pair == null) {
			if (other.pair != null)
				return false;
		} else if (!pair.equals(other.pair))
			return false;
		return true;
	}
	
	
}
