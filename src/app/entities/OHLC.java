package app.entities;

public class OHLC {
	
	private String time;
	private double open;
	private double high;
	private double low;
	private double close;
	private double vwap;
	private double volume;
	private double count;
	private double dailyTR;
	private double OC; // Close minus Open
	
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
