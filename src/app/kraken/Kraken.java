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

import app.entities.Utils;
import app.multiData.MultiDataUtils;

public class Kraken {
	
	private int period;
	private String pair;
	private String strUrl;
	
	private List<OHLC> periodList;
	private double lastPrice;
	private double lastOpen;
	private double periodTR;
	private double periodMIN;
	private double periodMAX;

	public Kraken(String pair, int period) {
		this.pair = pair.toUpperCase();
		// interval is in minutes 1440 = 1day
		// 1 (default), 5, 15, 30, 60, 240, 1440, 10080, 21600
		this.strUrl = "https://api.kraken.com/0/public/OHLC?pair=" + pair.toUpperCase() + "&interval=1440";
		this.period = period;
	}
	
	public double getPeriodMIN() {
		return periodMIN;
	}

	public double getPeriodMAX() {
		return periodMAX;
	}

	public List<OHLC> getPeriodList() {
		return periodList;
	}

	public void init(){
		getLastData();
		calculateMinMax();
		calculateTR();
		print();
	}

	private void getLastData() {
		String element = "result";
		StringBuilder fileUrl = new StringBuilder("market_data/");
		fileUrl.append(pair);
		fileUrl.append("_dataList.json");
		File file = new File(fileUrl.toString());
		this.periodList = new ArrayList<OHLC>();

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
				System.out.println("... Downloading New Data From kraken.com");
				URL url = new URL(strUrl);
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

				data = result.getAsJsonArray(pair);

				FileWriter  fw = new FileWriter(file, false);

				fw.write(data.toString());
				fw.close();
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
		int temp = data.size() - (period + 1);
		
		for (int i = temp; i < data.size(); i++) {
			this.periodList.add(jsonElementToOHLC(gson, data.get(i)));
		}
		this.lastPrice =  this.periodList.get(period).getClose();
		this.lastOpen =  this.periodList.get(period).getOpen();
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
//		List<Double> listTRMax = new ArrayList<>();
		PriorityQueue<Double> pq;
		Double sum = 0.0;

		for (int i = 1; i < this.periodList.size(); i++) {
			TR1 = Math.abs(this.periodList.get(i).getHigh() - this.periodList.get(i).getLow());
			TR2 = Math.abs(this.periodList.get(i - 1).getClose() - this.periodList.get(i).getHigh());
			TR3 = Math.abs(this.periodList.get(i - 1).getClose() - this.periodList.get(i).getLow());

			pq = new PriorityQueue<>(3, Collections.reverseOrder());
			pq.add(TR1);
			pq.add(TR2);
			pq.add(TR3);
//			listTRMax.add(pq.peek());
			sum += pq.peek();
		}

//		for (Double TRMax: listTRMax) {
//			sum += TRMax;
//		}
//		this.periodTR =  sum.doubleValue() / listTRMax.size();
		this.periodTR =  sum.doubleValue() / this.periodList.size();

	}

	private void calculateMinMax() {
		PriorityQueue<Double> pqMin = new PriorityQueue<>(this.periodList.size());
		PriorityQueue<Double> pqMax = new PriorityQueue<>(this.periodList.size(), Collections.reverseOrder());

		for (OHLC element : this.periodList) {
			pqMin.add(element.getLow());
			pqMax.add(element.getHigh());
		}

		this.periodMIN = pqMin.peek();
		this.periodMAX = pqMax.peek();

	}

	private void print() {
//		System.out.println(String.format("***** TRADING INFO %s ***** ", MultiDataUtils.readPair(this.pair)));
//		System.out.println(String.format("%d day/s period from %s", (this.periodList.size() - 1), KRAKEN));
		System.out.println(String.format("***** TRADING INFO %s %d day/s period from %s *****", MultiDataUtils.readPair(this.pair),  (this.periodList.size() - 1), MultiDataUtils.KRAKEN));
		System.out.println(String.format("Average TR: %.2f", this.periodTR));
		System.out.println(String.format("Open Price: %.2f$", this.lastOpen));
		System.out.println(String.format("Curr Price: %.2f$ %s", this.lastPrice, Utils.calcPrintPercentage(this.lastPrice, this.lastOpen)));
		System.out.println(String.format(" Min Price: %.2f$ %s", this.periodMIN, Utils.calcPrintPercentage(this.periodMIN, this.lastPrice)));
		System.out.println(String.format(" Max Price: %.2f$ %s", this.periodMAX, Utils.calcPrintPercentage(this.periodMAX, this.lastPrice)));
	}
	
	
	public List<OHLC> getFinalList() {
		return periodList;
	}

	public void setFinalList(List<OHLC> finalList) {
		this.periodList = finalList;
	}

	public static void main(String args[]) {
		String pair = "XXMRZUSD";
		Kraken k10 = new Kraken(pair, 10);
		k10.init();
		Kraken k20 = new Kraken(pair, 20);
		k20.init();
		Kraken k55 = new Kraken(pair, 55);
		k55.init();
	}
}
