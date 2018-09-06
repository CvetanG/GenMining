package app.kraken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
	public static final String STR_URL = "https://api.kraken.com/0/public/OHLC?pair=XXMRZUSD&interval=1440";
	private static final String INDEX = "XMRUSD";
	
	public int last;
	public List<OHLC> finalList;
	public double curPrice;
	public double lastTR;
	public double lastMIN;
	public double lastMAX;

	public Kraken(int last) {
		this.last = last;
	}

	public void init(){
		getLastData();
		calculateMinMax();
		calculateTR();
		print();
	}

	private void getLastData() {
		String element = "result";
		String subElement = "XXMRZUSD";
		File file = new File(subElement + "_dataList.json");
		this.finalList = new ArrayList<OHLC>();

		BufferedReader rd;
		OutputStreamWriter wr;
		JsonArray data =  null;
		String line;
		FileReader fr;

		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		StringBuilder sb = new StringBuilder();
		int minute = 30;
		int seconds = minute * 60;
		long periodMillis = (seconds * 1000);
		long millis = System.currentTimeMillis();
		boolean check = (file.lastModified() + periodMillis) > millis;

		if (file.exists() && check) {
			try {
				fr = new FileReader(file);
				rd = new BufferedReader(fr);
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				data = parser.parse(sb.toString()).getAsJsonArray();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				System.out.println("... Downloading New Data From Kraken");
				URL url = new URL(STR_URL);
				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				wr = new OutputStreamWriter(conn.getOutputStream());
				wr.flush();

				// Get the response
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));


				while ((line = rd.readLine()) != null) {
					sb.append(line);
					//						System.out.println(line);
				}
				JsonObject json = parser.parse(sb.toString()).getAsJsonObject();
				JsonObject result = json.getAsJsonObject(element);

				data = result.getAsJsonArray(subElement);

				FileWriter  fw = new FileWriter(file, false);

				fw.write(data.toString());
				fw.close();
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}

		int temp = data.size() - (last + 1);

		for (int i = temp; i < data.size(); i++) {
			this.finalList.add(jsonElementToOHLC(gson, data.get(i)));
		}
		this.curPrice =  this.finalList.get(last).getClose();

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
		System.out.println(String.format("Current Price: %.2f$", this.curPrice));
		System.out.println(String.format("Min %s: %.2f$/%.2f%s", INDEX, this.lastMIN, calcPercent(this.lastMIN, this.curPrice), "%"));
		System.out.println(String.format("Max %s: %.2f$/%.2f%s", INDEX, this.lastMAX, calcPercent(this.lastMAX, this.curPrice), "%"));
	}
	
	private double calcPercent(double a, double b) {
		 double pers = (a * 100.0f) / b;
		 return -(100.0 - pers);
	}
	
	public List<OHLC> getFinalList() {
		return finalList;
	}

	public void setFinalList(List<OHLC> finalList) {
		this.finalList = finalList;
	}

	public static void main(String args[]) {
		Kraken k10 = new Kraken(10);
		k10.init();
		Kraken k20 = new Kraken(20);
		k20.init();
		Kraken k55 = new Kraken(55);
		k55.init();
	}
}
