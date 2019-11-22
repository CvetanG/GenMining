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
		
		Document doc = urlConnect(myUrl);
		
		String XMR_USD;
		double blocks;
		String netwHashRate;
		double difficulty;
		
//		Element elementXMR_USD = (Element) doc.getElementsByClass("table table-striped table-bordered").get(0).childNode(1).childNode(0).childNode(3).childNode(1);
//		Element elementXMR_USD = (Element) doc.getElementsByClass("cryptocurrency-price").first().select("h1").first();
		Element elementXMR_USD = doc.select("section.cryptocurrency-price > span.price").first();
		XMR_USD = Utils.currencyFormater(elementXMR_USD.ownText());
		System.out.println("Monero USD: " + elementXMR_USD.text());
		
		Element elementParrent = (Element) doc.getElementsByClass("table table-bordered table-striped").get(0).childNode(3).childNode(1);
		
		Element elementBlocks = (Element) elementParrent.childNode(5);
		blocks = Utils.removeSeparetors(elementBlocks.text());
		System.out.println("Block Count: " + elementBlocks.text());
		
		Element elementNetwHashRate	= (Element) elementParrent.childNode(7);
		netwHashRate = Utils.currencyFormaterWithSuffix(elementNetwHashRate.text());
		System.out.println("Network Hashrate: " + elementNetwHashRate.text());
		
		Element elementDifficulty	= (Element) elementParrent.childNode(9);
		difficulty = Utils.removeSeparetors(elementDifficulty.text());
		System.out.println("Monero Difficulty: " + elementDifficulty.text());
		
		this.rowEntry.setXMR_USD(XMR_USD);
		this.rowEntry.setBlocks(blocks);
		this.rowEntry.setNetwHashRate(netwHashRate);
		this.rowEntry.setDifficulty(difficulty);
	}
	
	public void getMoneroInfoCoinMarketCap() throws IOException {
		String myUrl = "https://coinmarketcap.com/currencies/monero/";
		
		Document doc = urlConnect(myUrl);
		
//		String tagClassName= "details-panel-item--marketcap-stats flex-container";
		String tagClassName= "cmc-details-panel-stats k1ayrc-0 OZKKF";
		Element element = doc.getElementsByClass(tagClassName).get(0);
		
		String elementMarketCap = ((Element) element.childNode(0).childNode(1).childNode(0)).text();
		System.out.println("Market Cap: " + elementMarketCap);
		Long marketCap = Utils.removeSeparetorsAndCurrency(elementMarketCap);
		
		String elementVolume = ((Element) element.childNode(1).childNode(1).childNode(0)).text();
		System.out.println("Volume (24h): " + elementVolume);
		long volume = Utils.removeSeparetorsAndCurrency(elementVolume);
		
		String elementCirculatingSupply = element.childNode(2).childNode(1).childNode(0).outerHtml();
		System.out.println("Circulating Supply: " + elementCirculatingSupply.replace("\n", "").replace("\r", ""));
		long circulatingSupply = Utils.removeSeparetorsAndCurrency(elementCirculatingSupply);
		
		this.rowEntry.setMarketCap(marketCap);
		this.rowEntry.setVolume(volume);
		this.rowEntry.setCirculatingSupply(circulatingSupply);
	}

	private Document urlConnect(String myUrl) throws IOException {
		return Jsoup.connect(myUrl)
				.timeout(TIMEOUT).validateTLSCertificates(false)
				.get();
	}
	
	public static void main(String[] args) throws IOException {
		WebSitesParser myParser = new WebSitesParser();
		myParser.getMoneroInfo();
	}
}
