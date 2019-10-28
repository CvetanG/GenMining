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

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import app.entities.OHLC;
import app.entities.Utils;
import app.multidata.MultiDataUtils;

public class Kraken {
	
	public static final String TRADE_DATA = "https://api.kraken.com/0/public/OHLC?pair=%s&interval=1440";
	public static final String ASSET_DATA = "https://api.kraken.com/0/public/AssetPairs?pair=%s";
	
	private int period;
	private int dec;
	private String pair;
	
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
		this.period = period;
		this.periodList = new ArrayList<>();
	}
	
	public void init(){
		getLastData();
		calculateMinMax();
		calculateTR();
		getDec();
		print();
	}

	private void getLastData() {
		String element = "result";
		StringBuilder fileUrl = new StringBuilder("market_data/");
		fileUrl.append(getPair());
		fileUrl.append("_dataList.json");
		File file = new File(fileUrl.toString());

		OutputStreamWriter wr;
		JsonArray data =  null;
		String line;

		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		StringBuilder sb = new StringBuilder();
		int minute = 30;
		int seconds = minute * 60;
		long periodMillis = (seconds * 1000);
		long millis = System.currentTimeMillis();
		boolean check = (file.lastModified() + periodMillis) > millis;

		if (file.exists() && check) {
			try (FileReader fr = new FileReader(file);
					BufferedReader rd = new BufferedReader(fr)){
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
				URL url = new URL(createTradeDateUrl());
				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				wr = new OutputStreamWriter(conn.getOutputStream());
				wr.flush();

				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));


				while ((line = rd.readLine()) != null) {
					sb.append(line);
					//						System.out.println(line);
				}
				JsonObject json = parser.parse(sb.toString()).getAsJsonObject();
				JsonObject result = json.getAsJsonObject(element);

				data = result.getAsJsonArray(getPair());

				FileWriter  fw = new FileWriter(file, false);

				fw.write(data.toString());
				fw.close();
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
		int temp = data.size() - (getPeriod() + 1);
		
		for (int i = temp; i < data.size(); i++) {
			this.getPeriodList().add(jsonElementToOHLC(gson, data.get(i)));
		}
		this.setLastPrice(this.getPeriodList().get(getPeriod()).getClose());
		this.setLastOpen(this.getPeriodList().get(getPeriod()).getOpen());
	}
	
	private void getDec() {
		
		String element = "result";
		try {

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			
			String strUrl = String.format(ASSET_DATA, this.pair);
			
			HttpUriRequest httpGet = new HttpGet(strUrl);
			System.out.println("Executing request : " + strUrl);
			HttpResponse resp = httpClient.execute(httpGet);
			String strResp = MultiDataUtils.responseToString(resp);
			if (!StringUtils.EMPTY.equals(strResp)) {
				JsonParser parser = new JsonParser();
				JsonObject jsonResp = parser.parse(strResp).getAsJsonObject();
				JsonObject jsonAsset = jsonResp.getAsJsonObject(element).getAsJsonObject(this.pair);
				this.dec = jsonAsset.get("pair_decimals").getAsInt();
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
//		List<Double> listTRMax = new ArrayList<>();
		PriorityQueue<Double> pq;
		Double sum = 0.0;

		for (int i = 1; i < this.getPeriodList().size(); i++) {
			TR1 = Math.abs(this.getPeriodList().get(i).getHigh() - this.getPeriodList().get(i).getLow());
			TR2 = Math.abs(this.getPeriodList().get(i - 1).getClose() - this.getPeriodList().get(i).getHigh());
			TR3 = Math.abs(this.getPeriodList().get(i - 1).getClose() - this.getPeriodList().get(i).getLow());

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
		this.setPeriodTR(sum.doubleValue() / this.getPeriodList().size());

	}

	private void calculateMinMax() {
		PriorityQueue<Double> pqMin = new PriorityQueue<>(this.getPeriodList().size());
		PriorityQueue<Double> pqMax = new PriorityQueue<>(this.getPeriodList().size(), Collections.reverseOrder());

		for (OHLC element : this.getPeriodList()) {
			pqMin.add(element.getLow());
			pqMax.add(element.getHigh());
		}

		this.setPeriodMIN(pqMin.peek());
		this.setPeriodMAX(pqMax.peek());

	}
	
	public String createTradeDateUrl() {
		return String.format(TRADE_DATA, pair.toUpperCase());
	}

	private void print() {
//		System.out.println(String.format("***** TRADING INFO %s ***** ", MultiDataUtils.readPair(this.pair)));
//		System.out.println(String.format("%d day/s period from %s", (this.periodList.size() - 1), KRAKEN));
		System.out.println(String.format("***** TRADING INFO %s %d day/s period from %s *****", MultiDataUtils.readPair(this.getPair()),  (this.getPeriodList().size() - 1), MultiDataUtils.KRAKEN));
		System.out.println(String.format("Average TR: %." + dec + "f", this.getPeriodTR()));
		System.out.println(String.format("Curr Price: %." + dec + "f$", this.getLastPrice()));
		System.out.println(String.format("Open Price: %." + dec + "f$ %s", this.getLastOpen(), Utils.calcPrintPercentage(this.getLastOpen(), this.getLastPrice())));
		System.out.println(String.format(" Min Price: %." + dec + "f$ %s", this.getPeriodMIN(), Utils.calcPrintPercentage(this.getPeriodMIN(), this.getLastPrice())));
		System.out.println(String.format(" Max Price: %." + dec + "f$ %s", this.getPeriodMAX(), Utils.calcPrintPercentage(this.getPeriodMAX(), this.getLastPrice())));
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
	
	public List<OHLC> getFinalList() {
		return getPeriodList();
	}

	public void setFinalList(List<OHLC> finalList) {
		this.setPeriodList(finalList);
	}

	public double getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(double lastPrice) {
		this.lastPrice = lastPrice;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public double getLastOpen() {
		return lastOpen;
	}

	public void setLastOpen(double lastOpen) {
		this.lastOpen = lastOpen;
	}

	public void setPeriodList(List<OHLC> periodList) {
		this.periodList = periodList;
	}

	public String getPair() {
		return pair;
	}

	public void setPair(String pair) {
		this.pair = pair;
	}

	public double getPeriodTR() {
		return periodTR;
	}

	public void setPeriodTR(double periodTR) {
		this.periodTR = periodTR;
	}

	public void setPeriodMIN(double periodMIN) {
		this.periodMIN = periodMIN;
	}

	public void setPeriodMAX(double periodMAX) {
		this.periodMAX = periodMAX;
	}
	
	public static void main(String args[]) {
//		String pair = "XXMRZUSD";
		String pair = "XXRPZUSD";
		Kraken k10 = new Kraken(pair, 10);
		k10.init();
		Kraken k20 = new Kraken(pair, 20);
		k20.init();
		Kraken k55 = new Kraken(pair, 55);
		k55.init();
	}
	
}
