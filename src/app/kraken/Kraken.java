package app.kraken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class Kraken {
	public int last = 20;
	public String strUrl = "https://api.kraken.com/0/public/OHLC?pair=XXMRZUSD&interval=1440";
	public List<OHLC> finalList;
	public double lastTR;
	public double lastMIN;
	public double lastMAX;
	
	public void init(){
		getLastData();
		calculateMinMax();
		calculateTR();
		print();
	}
	
	private void getLastData() {
		BufferedReader rd;
		OutputStreamWriter wr;
		this.finalList = new ArrayList<OHLC>();

		try {
			URL url = new URL(strUrl);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			wr = new OutputStreamWriter(conn.getOutputStream());
			wr.flush();

			// Get the response
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
//				System.out.println(line);
			}
			
			String element = "result";
			String subElement = "XXMRZUSD";
			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			
			JsonObject json = parser.parse(sb.toString()).getAsJsonObject();
			JsonObject result = json.getAsJsonObject(element);
			
			JsonArray  data = result.getAsJsonArray(subElement);
			
			int temp = data.size() - (last + 1);
			
			for (int i = temp; i < data.size(); i++) {
				this.finalList.add(jsonElementToOHLC(gson, data.get(i)));
			}
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	
	private OHLC jsonElementToOHLC(Gson gson, JsonElement jsonElement) {
		OHLC result = new OHLC();

		List<String> yourList = gson.fromJson(jsonElement, new TypeToken<List<String>>(){}.getType());
	    
		result.setTime(yourList.get(0));
		result.setOpen(Double.parseDouble(yourList.get(1)));
		result.setHigh(Double.parseDouble(yourList.get(2)));
		result.setLow(Double.parseDouble(yourList.get(3)));
		result.setClose(Double.parseDouble(yourList.get(4)));
		result.setVwap(Double.parseDouble(yourList.get(5)));
		result.setVolume(Double.parseDouble(yourList.get(6)));
		result.setCount(Double.parseDouble(yourList.get(7)));
		return result;
	}

	private void calculateTR() {
		// TR1 today H/L
		double TR1;
		// TR2 yest close today H
		double TR2;
		// TR3 yest close today L
		double TR3;
		List<Double> listTRMax = new ArrayList<>();
		PriorityQueue<Double> pq;
		
		for (int i = 1; i < this.finalList.size(); i++) {
			TR1 = Math.abs(this.finalList.get(i).getHigh() - this.finalList.get(i).getLow());
			TR2 = Math.abs(this.finalList.get(i - 1).getClose() - this.finalList.get(i).getHigh());
			TR3 = Math.abs(this.finalList.get(i - 1).getClose() - this.finalList.get(i).getLow());
			
			pq = new PriorityQueue<>(3, Collections.reverseOrder());
			pq.add(TR1);
			pq.add(TR2);
			pq.add(TR3);
			listTRMax.add(pq.peek());
		}
		
		Double sum = 0.0;
		for (Double TRMax: listTRMax) {
		    sum += TRMax;
		}
		this.lastTR =  sum.doubleValue() / listTRMax.size();
		
	}
	
	private void calculateMinMax() {
		PriorityQueue<Double> pqMin = new PriorityQueue<>(this.finalList.size());
		PriorityQueue<Double> pqMax = new PriorityQueue<>(this.finalList.size(), Collections.reverseOrder());
		
		for (OHLC element : this.finalList) {
			pqMin.add(element.getLow());
			pqMax.add(element.getHigh());
		}
		
		this.lastMIN = pqMin.peek();
		this.lastMAX = pqMax.peek();;
		
	}
	
	private void print() {
		System.out.println("***** TRADING INFO *****");
		System.out.println("Data info for " + (this.finalList.size() - 1) + " day/s period.");
		System.out.println(String.format("Average TR: %.2f", this.lastTR));
		System.out.println(String.format("Min XMRUSD: %.2f$", this.lastMIN));
		System.out.println(String.format("Max XMRUSD: %.2f$", this.lastMAX));
//		System.out.println("Average TR for period: " + this.lastTR);
//		System.out.println("Min XMRUSD for period: " + this.lastMIN + "$");
//		System.out.println("Max XMRUSD for period: " + this.lastMAX + "$");
	}
	
	
	
	public static void main(String args[]) {
		Kraken k = new Kraken();
		k.init();
	}
}
