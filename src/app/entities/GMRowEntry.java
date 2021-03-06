package app.entities;

public class GMRowEntry {
	
	private String XMR_USD;
	private long marketCap;
	private long volume;
	private long circulatingSupply;
	private double blocks;
	private String netwHashRate;
	private double difficulty;
	
	public GMRowEntry() {
	}
	
	/*
	public GMRowEntry(String XMR_USD, double blocks, String netwHashRate, double difficulty) {
		this.XMR_USD = XMR_USD;
		this.blocks = blocks;
		this.netwHashRate = netwHashRate;
		this.difficulty = difficulty;
	}
	*/
	
	public String getXMR_USD() {
		return XMR_USD;
	}
	
	public void setXMR_USD(String XMR_USD) {
		this.XMR_USD = XMR_USD;
	}
	
	public long getMarketCap() {
		return marketCap;
	}

	public void setMarketCap(long marketCap) {
		this.marketCap = marketCap;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public long getCirculatingSupply() {
		return circulatingSupply;
	}

	public void setCirculatingSupply(long circulatingSupply) {
		this.circulatingSupply = circulatingSupply;
	}

	public double getBlocks() {
		return blocks;
	}
	
	public void setBlocks(double blocks) {
		this.blocks = blocks;
	}
	
	public String getNetwHashRate() {
		return netwHashRate;
	}
	
	public void setNetwHashRate(String netwHashRate) {
		this.netwHashRate = netwHashRate;
	}
	
	public double getDifficulty() {
		return difficulty;
	}
	
	public void setDifficulty(double difficulty) {
		this.difficulty = difficulty;
	}
}
