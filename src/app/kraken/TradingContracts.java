package app.kraken;

import java.util.List;

public class TradingContracts{
//	public class TradingContracts implements Cloneable, Comparable<TradingContracts> {
	
	public final double USDstart = 0;
	public final double XMRstart = 0.4;
	
	public double USD = USDstart;
	public double XMR = XMRstart;

	public Integer[] combList;
	public List<OHLC> krakenData;
	
	public TradingContracts(Integer[] combList, List<OHLC> krakenData) {
		this.combList = combList;
		this.krakenData = krakenData;
		trade(false);
	}
	
	public void trade(boolean print) {
		for (int i = 0; i < this.combList.length; i++) {
			if (this.combList[i] != null) {
				sellOrBuy(this.krakenData.get(this.combList[i]), this.combList[i], print);
			}
		}
	}
	
	public void sellOrBuy(OHLC data, int day, boolean print) {
		if (USD > 0 && XMR == 0) {
			buyXMR(data, day, print);
		} else if (XMR > 0){
			sellXMR(data, day, print);
		}
	}
	
	public void buyXMR(OHLC data, int day, boolean print) {
		XMR = USD / data.getLow();
		USD -= USD;
		if (print) {
//			System.out.println(data);
			System.out.println(String.format("Buying  XMR on day %2d - XMR/USD %6.2f; XMR: %5.2f, USD: %05.2f$", day, data.getLow(), XMR, USD));
		}
	}
	
	public void sellXMR(OHLC data, int day, boolean print) {
		USD = XMR * data.getHigh();
		XMR -= XMR;
		if (print) {
//			System.out.println(data);
			System.out.println(String.format("Selling XMR on day %2d - XMR/USD %6.2f; XMR: %5.2f, USD: %05.2f$", day, data.getLow(), XMR, USD));
		}
	}
	
	public void printTrading(){
		this.USD = USDstart;
		this.XMR = XMRstart;
		trade(true);
	}

	public double getUSD() {
		return USD;
	}

	public void setUSD(double uSD) {
		USD = uSD;
	}

	public double getXMR() {
		return XMR;
	}

	public void setXMR(double xMR) {
		XMR = xMR;
	}

	public Integer[] getCombList() {
		return combList;
	}

	public void setCombList(Integer[] combList) {
		this.combList = combList;
	}

	public List<OHLC> getKrakenData() {
		return krakenData;
	}

	public void setKrakenData(List<OHLC> krakenData) {
		this.krakenData = krakenData;
	}

	/*
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < recordPlans.size(); i++) {
			sb.append(recordPlans.get(i).getPlanName());
			if ((i + 1) != recordPlans.size()) {
				sb.append(", ");
			} else {
				sb.append(".");
			}
		}
		return "InvestmentRecord: " + sb + "\nGained cash for " + recordActivePeriod + " days: "
				+ Utils.formatter.format(recordCash) + "$. Max Hash Rate gained: " + recordMaxGainedHash + "H/s"
				+ " for " + recordPassedDays + " days" + "(" + (recordPassedDays / 30) + " months). \nFuture Income: "
				+ Utils.formatter.format(recordFutureIncome) + "$ after " + recordFuturePeriod + " days.\n";
	}
	
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public int compareTo(TradingContracts tc) {
		int diffUSD;
		int diffXMRs;

		diffUSD = (int) (this.USD - tc.USD);
		if (diffUSD != 0)
			return diffUSD;

		diffXMRs = (int) (this.XMR - tc.XMR);
		if (diffXMRs != 0)
			return diffXMRs;

		return 0;
	}
	*/
	
	

}
