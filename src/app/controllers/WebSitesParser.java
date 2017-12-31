package app.controllers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import app.entities.GMRowEntry;
import app.entities.Utils;

public class WebSitesParser {
	
	private static final int timeout = 50000;
	private static WebSitesParser instance;
    
	public WebSitesParser(){
		
	}
    
    public static WebSitesParser getInstance(){
        if(instance == null){
            instance = new WebSitesParser();
        }
        return instance;
    }
	
	public GMRowEntry getMoneroInfo() throws IOException {
		String myUrl = "https://www.coinwarz.com/cryptocurrency/coins/monero/";
		
		Document doc = Jsoup.connect(myUrl)
				.timeout(timeout).validateTLSCertificates(false)
				.get();
		
		String XMR_USD;
		double blocks;
		String netwHashRate;
		double difficulty;
		
		Element elementXMR_USD = (Element) doc.getElementsByClass("table table-striped table-bordered").get(0).childNode(1).childNode(0).childNode(3).childNode(1);
		XMR_USD = Utils.clearFormatCurr(elementXMR_USD.text());
		System.out.println("Monero USD: " + XMR_USD);
		
		Element elementBlocks = (Element) doc.getElementsByClass("table table-bordered table-striped").get(0).childNode(3).childNode(1).childNode(5);
		System.out.println(elementBlocks.text());
		blocks = Utils.removeSeparetors(elementBlocks.text());
		
		Element elementNetwHashRate = (Element) doc.getElementsByClass("table table-bordered table-striped").get(0).childNode(3).childNode(1).childNode(7);
		netwHashRate = Utils.clearFormatCurr(elementNetwHashRate.text());
		System.out.println(netwHashRate);
		
		Element elementDifficulty = (Element) doc.getElementsByClass("table table-bordered table-striped").get(0).childNode(3).childNode(1).childNode(9);
		System.out.println(elementDifficulty.text());
		difficulty = Utils.removeSeparetors(elementDifficulty.text());
		
		GMRowEntry rowEntry = new GMRowEntry(
				XMR_USD,
				blocks,
				netwHashRate,
				difficulty);
		
		return rowEntry;
		
	}
	
	public static void main(String[] args) throws IOException {
		WebSitesParser myParser = new WebSitesParser();
		myParser.getMoneroInfo();
	}
}
