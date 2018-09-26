package app.multiData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import app.kraken.OHLC;

public class Kraken_02 {
	
	private final static int minute = 15; // downloaded files good for this period
	
	private int period; // days to get Data for
	private List<OHLC> finalList;
	private double curPrice;
	private double periodTR;
	private double periodMIN;
	private double percCurMIN;
	private double periodMAX;
	private double percCurMAX;
	private String index;
	private String strUrl;
	private int pairDec;

	public Kraken_02(String index, int period, int pairDec) {
		this.index = index.toUpperCase();
		this.strUrl = "https://api.kraken.com/0/public/OHLC?pair=" + index.toUpperCase() + "&interval=1440";
		this.period = period;
		this.pairDec = pairDec;
	}

	public Kraken_02(PairDec pair, int period) {
		this.index = pair.getPair().toUpperCase();
		this.strUrl = "https://api.kraken.com/0/public/OHLC?pair=" + index.toUpperCase() + "&interval=1440";
		this.period = period;
		this.pairDec = pair.getDec();
	}

	public void init(boolean print) {
		getLastData();
		calculateMinMax();
		calculateTR();
		if (print) {
			print();
		}
	}

	private void getLastData() {
		String element = "result";
		this.finalList = new ArrayList<OHLC>();

		JsonParser parser = new JsonParser();
		StringBuilder fileUrl = new StringBuilder("market_data/");
		fileUrl.append(index);
		fileUrl.append("_dataList.json");
		File file = new File(fileUrl.toString());

		BufferedReader rd;
		JsonArray data = null;
		String line;
		FileReader fr;

		StringBuilder sb = new StringBuilder();
		int seconds = minute * 60;
		long periodMillis = (seconds * 1000);
		long millis = System.currentTimeMillis();
		boolean check = (file.lastModified() + periodMillis) > millis;

		if (file.exists() && check) {
			try {
				System.out.println("... There is Data For Index: " + index);
				fr = new FileReader(file);
				rd = new BufferedReader(fr);
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				data = parser.parse(sb.toString()).getAsJsonArray();
				processData(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				System.out.println("... Downloading New Data From Kraken");
				CloseableHttpClient httpClient = HttpClientBuilder.create().build();
				HttpUriRequest httpGet = new HttpGet(this.strUrl);
				System.out.println("Executing request : " + httpGet.getRequestLine());
				HttpResponse resp = httpClient.execute(httpGet);
				String strResp = MultiDataUtils.responseToString(resp);
				if (strResp != StringUtils.EMPTY) {
					JsonObject json = parser.parse(strResp).getAsJsonObject();
					
					JsonArray err = json.getAsJsonArray("error");
					if (err.isJsonNull()) {
						System.err.println("Error getting index " + this.index + " - "+ err.getAsString());
					} else {
						JsonObject result = json.getAsJsonObject(element);
						data = result.getAsJsonArray(index);
						
						FileWriter fw = new FileWriter(file, false);
						fw.write(data.toString());
						fw.close();
						processData(data);
					}
				}
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	}

	private void processData(JsonArray data) {
		int temp = data.size() - (this.period + 1);
		for (int i = temp; i < data.size(); i++) {
			this.finalList.add(MultiDataUtils.jsonElementToOHLC(data.get(i)));
		}
		this.curPrice = this.finalList.get(this.period).getClose();
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
		for (Double TRMax : listTRMax) {
			sum += TRMax;
		}
		this.periodTR = sum.doubleValue() / listTRMax.size();

	}

	private void calculateMinMax() {
		PriorityQueue<Double> pqMin = new PriorityQueue<>(this.finalList.size());
		PriorityQueue<Double> pqMax = new PriorityQueue<>(this.finalList.size(), Collections.reverseOrder());

		for (OHLC element : this.finalList) {
			pqMin.add(element.getLow());
			pqMax.add(element.getHigh());
		}

		this.periodMIN = pqMin.peek();
		this.percCurMIN = calcPercent(this.periodMIN, this.curPrice);
		this.periodMAX = pqMax.peek();
		this.percCurMAX = calcPercent(this.periodMAX, this.curPrice);
	}

	public void print() {
		System.out.println("***** TRADING INFO *****");
		System.out.println("Data info for " + (this.finalList.size() - 1) + " day/s period.");
		System.out.println(String.format("Average TR: %." + pairDec + "f", this.periodTR));
		System.out.println(String.format("Current Price: %." + pairDec + "f$", this.curPrice));
		System.out.println(
				String.format("Min %s: %." + pairDec + "f$/%.2f%s", index, this.periodMIN, this.percCurMIN, "%"));
		System.out.println(
				String.format("Max %s: %." + pairDec + "f$/%.2f%s", index, this.periodMAX, this.percCurMAX, "%"));
		System.out.println(MultiDataUtils.readPair(this.index).toUpperCase());
	}

	private double calcPercent(double price, double curPrice) {
		double pers = (price * 100.0f) / curPrice;
		return -(100.0 - pers);
	}

	public List<OHLC> getFinalList() {
		return finalList;
	}

	public void setFinalList(List<OHLC> finalList) {
		this.finalList = finalList;
	}

	public double getPercCurMIN() {
		return percCurMIN;
	}

	public double getPercCurMAX() {
		return percCurMAX;
	}

	public String getIndex() {
		return index;
	}

	public static void main(String args[]) {
		// String pair = "XXMRZUSD";
		String pair = "XETHXXBT";
		boolean print = true;
		int pairDec = 6;
		Kraken_02 k20 = new Kraken_02(pair, 20, pairDec);
		k20.init(print);
		Kraken_02 k55 = new Kraken_02(pair, 55, pairDec);
		k55.init(print);
	}
}
