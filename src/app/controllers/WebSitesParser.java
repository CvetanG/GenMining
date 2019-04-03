package app.controllers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import app.entities.GMRowEntry;
import app.entities.Utils;

public class WebSitesParser {
	
	private static final int TIMEOUT = 50000;
	private static WebSitesParser instance;
	
	private GMRowEntry rowEntry;
    
	public WebSitesParser(){
		this.rowEntry = new GMRowEntry();
	}
	
	public GMRowEntry getRowEntry() {
		return rowEntry;
	}

	public static WebSitesParser getInstance(){
        if(instance == null){
            instance = new WebSitesParser();
        }
        return instance;
    }
	
	public void getMoneroInfo() throws IOException {
		getMoneroInfoCoinWarz();
		System.out.println();
		getMoneroInfoCoinMarketCap();
	}
	
	public void getMoneroInfoCoinWarz() throws IOException {
		String myUrl = "https://www.coinwarz.com/cryptocurrency/coins/monero/";
		
		Document doc = Jsoup.connect(myUrl)
				.timeout(TIMEOUT).validateTLSCertificates(false)
				.get();
		
		String XMR_USD;
		double blocks;
		String netwHashRate;
		double difficulty;
		
		Element elementXMR_USD = (Element) doc.getElementsByClass("table table-striped table-bordered").get(0).childNode(1).childNode(0).childNode(3).childNode(1);
		XMR_USD = Utils.clearFormatCurr(elementXMR_USD.text());
		System.out.println("Monero USD: " + elementXMR_USD.text());
		
		Element elementBlocks = (Element) doc.getElementsByClass("table table-bordered table-striped").get(0).childNode(3).childNode(1).childNode(5);
		blocks = Utils.removeSeparetors(elementBlocks.text());
		System.out.println("Block Count: " + elementBlocks.text());
		
		Element elementNetwHashRate = (Element) doc.getElementsByClass("table table-bordered table-striped").get(0).childNode(3).childNode(1).childNode(7);
		netwHashRate = Utils.clearFormatCurr(elementNetwHashRate.text());
		System.out.println("Network Hashrate: " + elementNetwHashRate.text());
		
		Element elementDifficulty = (Element) doc.getElementsByClass("table table-bordered table-striped").get(0).childNode(3).childNode(1).childNode(9);
		difficulty = Utils.removeSeparetors(elementDifficulty.text());
		System.out.println("Monero Difficulty: " + elementDifficulty.text());
		
		this.rowEntry.setXMR_USD(XMR_USD);
		this.rowEntry.setBlocks(blocks);
		this.rowEntry.setNetwHashRate(netwHashRate);
		this.rowEntry.setDifficulty(difficulty);
	}
	
	public void getMoneroInfoCoinMarketCap() throws IOException {
		String myUrl = "https://coinmarketcap.com/currencies/monero/";
		
		Document doc = Jsoup.connect(myUrl)
				.timeout(TIMEOUT).validateTLSCertificates(false)
				.get();
		
		long marketCap;
		long volume;
		long circulatingSupply;
		
		String tagClassName= "details-panel-item--marketcap-stats flex-container";
		Element elementMarketCap = (Element) doc.getElementsByClass(tagClassName).get(0).childNode(1).childNode(3).childNode(1).childNode(1);
		marketCap = Utils.removeSeparetors(elementMarketCap.text());
		System.out.println("Market Cap: $" + elementMarketCap.text());
		
		Element elementVolume = (Element) doc.getElementsByClass(tagClassName).get(0).childNode(3).childNode(3).childNode(1);
		volume = Utils.removeSeparetors(elementVolume.text().substring(0, elementVolume.text().length()-4));
		System.out.println("Volume (24h): $" + elementVolume.text());
		
		Element elementCirculatingSupply = (Element) doc.getElementsByClass(tagClassName).get(0).childNode(5).childNode(3).childNode(1);
		circulatingSupply = Utils.removeSeparetors(elementCirculatingSupply.text());
		System.out.println("Circulating Supply: " + elementCirculatingSupply.text());
		
		this.rowEntry.setMarketCap(marketCap);
		this.rowEntry.setVolume(volume);
		this.rowEntry.setCirculatingSupply(circulatingSupply);
	}
	
	public static void main(String[] args) throws IOException {
		WebSitesParser myParser = new WebSitesParser();
		myParser.getMoneroInfo();
	}
}
