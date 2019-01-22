package app.kraken;

public class OHLC {
	String time;
	double open;
	double high;
	double low;
	double close;
	double vwap;
	double volume;
	double count;
	double dailyTR;
	double OC;
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public double getOpen() {
		return open;
	}
	
	public void setOpen(double open) {
		this.open = open;
	}
	
	public double getHigh() {
		return high;
	}
	
	public void setHigh(double high) {
		this.high = high;
	}
	
	public double getLow() {
		return low;
	}
	
	public void setLow(double low) {
		this.low = low;
	}
	
	public double getClose() {
		return close;
	}
	
	public void setClose(double close) {
		this.close = close;
	}
	
	public double getVwap() {
		return vwap;
	}
	
	public void setVwap(double vwap) {
		this.vwap = vwap;
	}
	
	public double getVolume() {
		return volume;
	}
	
	public void setVolume(double volume) {
		this.volume = volume;
	}
	
	public double getCount() {
		return count;
	}
	
	public void setCount(double count) {
		this.count = count;
	}
	
	public double getDailyTR() {
		return dailyTR;
	}

	public void setDailyTR(double dailyTR) {
		this.dailyTR = dailyTR;
	}
	
	public double getOC() {
		return OC;
	}

	public void setOC(double oC) {
		OC = oC;
	}

	@Override
	public String toString() {
		return "OHLC high=" + high + ", low=" + low + "";
	}
	
	
}
