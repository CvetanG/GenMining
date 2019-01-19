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
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import app.kraken.OHLC;

public class KrakenMD {
	
	private final static int MINUTES = 15; // downloaded files good for this period
	
	private int period; // days to get Data for
	private List<OHLC> finalList;
	private double curPrice;
	private double lastOpen;
	private double periodTR;
	private double periodMIN;
	private double percCurMIN;
	private double periodMAX;
	private double percCurMAX;
	private String index;
	private String strUrl;
	private int pairDec;

	public KrakenMD(String index, int period, int pairDec) {
		this.index = index.toUpperCase();
		this.strUrl = "https://api.kraken.com/0/public/OHLC?pair=" + index.toUpperCase() + "&interval=1440";
		this.period = period;
		this.pairDec = pairDec;
	}

	public KrakenMD(PairDec pair, int period) {
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

		
		int seconds = MINUTES * 60;
		long periodMillis = (seconds * 1000);
		long millis = System.currentTimeMillis();
		boolean check = (file.lastModified() + periodMillis) > millis;

		if (file.exists() && check) {
			System.out.println("... There is Data For Index: " + index);
			StringBuilder sb = new StringBuilder();
			try {
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
			System.out.println("... Downloading New Data From Kraken");
			final int reguestCount = 5;
			try {
//				CloseableHttpClient httpClient = HttpClientBuilder.create().build();
				
				CloseableHttpClient httpClient = HttpClients.custom()
				        .setServiceUnavailableRetryStrategy(new ServiceUnavailableRetryStrategy() {
				            @Override
				            public boolean retryRequest(
				                    final HttpResponse response, final int executionCount, final HttpContext context) {
				                int statusCode = response.getStatusLine().getStatusCode();
				                return statusCode == 403 && executionCount < reguestCount;
				            }

				            @Override
				            public long getRetryInterval() {
				                return 0;
				            }
				        })
				        .build();
				
				HttpUriRequest httpGet = new HttpGet(this.strUrl);
				System.out.println("Executing request : " + httpGet.getRequestLine());
				CloseableHttpResponse resp = httpClient.execute(httpGet);
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
				resp.close();
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	}

	private void processData(JsonArray data) {
		int temp = 0;
		if (data.size() > this.period) {
			temp = data.size() - (this.period + 1);
			for (int i = temp; i < data.size(); i++) {
				this.finalList.add(MultiDataUtils.jsonElementToOHLC(data.get(i)));
			}
			this.curPrice = this.finalList.get(this.period).getClose();
			this.lastOpen = this.finalList.get(this.period).getOpen();
		} else {
			this.period = data.size();
			for (int i = temp; i < data.size(); i++) {
				this.finalList.add(MultiDataUtils.jsonElementToOHLC(data.get(i)));
			}
			this.curPrice = this.finalList.get(this.period - 1).getClose();
			this.lastOpen = this.finalList.get(this.period - 1).getOpen();
		}
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

		for (int i = 1; i < this.finalList.size(); i++) {
			TR1 = Math.abs(this.finalList.get(i).getHigh() - this.finalList.get(i).getLow());
			TR2 = Math.abs(this.finalList.get(i - 1).getClose() - this.finalList.get(i).getHigh());
			TR3 = Math.abs(this.finalList.get(i - 1).getClose() - this.finalList.get(i).getLow());

			pq = new PriorityQueue<>(3, Collections.reverseOrder());
			pq.add(TR1);
			pq.add(TR2);
			pq.add(TR3);
//			listTRMax.add(pq.peek());
			sum += pq.peek();
		}

//		for (Double TRMax : listTRMax) {
//			sum += TRMax;
//		}
//		this.periodTR = sum.doubleValue() / listTRMax.size();
		this.periodTR = sum.doubleValue() / this.finalList.size();

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
//		System.out.println(String.format("***** TRADING INFO %s *****", MultiDataUtils.readPair(this.index)));
//		System.out.println(String.format("Data info for %d day/s period from %s", (this.finalList.size() - 1), MultiDataUtils.KRAKEN));
		System.out.println(String.format("***** TRADING INFO %s %d day/s period from %s *****", MultiDataUtils.readPair(this.index),  (this.finalList.size() - 1), MultiDataUtils.KRAKEN));
		System.out.println(String.format("Average TR: %." + pairDec + "f", this.periodTR));
		System.out.println(String.format("Open Price: %." + pairDec + "f$", this.lastOpen));
		System.out.println(String.format("Curr Price: %." + pairDec + "f$ %s", this.curPrice,  printPro(calcPercent(this.curPrice, this.lastOpen))));
		System.out.println(
				String.format(" Min Price: %." + pairDec + "f$ %s", this.periodMIN, printPro(this.percCurMIN)));
		System.out.println(
				String.format(" Max Price: %." + pairDec + "f$ %s", this.periodMAX, printPro(this.percCurMAX)));
//		System.out.println(MultiDataUtils.readPair(this.index).toUpperCase());
	}

	private double calcPercent(double price, double curPrice) {
		double pers = (price * 100.0f) / curPrice;
		return -(100.0 - pers);
	}
	
	private String printPro(double pro) {
		if (pro > 0.0) {
			return String.format("(+%.2f%s)", pro, "%");
		} else {
			return String.format("(%.2f%s)", pro, "%");
		}
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
		KrakenMD k20 = new KrakenMD(pair, 20, pairDec);
		k20.init(print);
		KrakenMD k55 = new KrakenMD(pair, 55, pairDec);
		k55.init(print);
	}
}
