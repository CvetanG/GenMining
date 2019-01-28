package app.multiData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

import app.entities.OHLC;
import app.entities.PairDec;
import app.entities.Utils;
import app.kraken.Kraken;

public class KrakenMD extends Kraken {
	
	private final static int MINUTES = 15; // downloaded files good for this period
	
//	private int period; // days to get Data for
//	private String pair;
//	private String strUrl;
	private int pairDec;
//	private List<OHLC> periodList;
//	private double lastPrice;
//	private double lastOpen;
//	private double periodTR;
	private double periodOC;
//	private double periodMIN;
//	private double periodMAX;
	private double percCurMIN;
	private double percCurMAX;

	public KrakenMD(String index, int period, int pairDec) {
//		this.pair = index.toUpperCase();
//		this.strUrl = "https://api.kraken.com/0/public/OHLC?pair=" + index.toUpperCase() + "&interval=1440";
//		this.period = period;
		super(index, period);
		this.pairDec = pairDec;
	}
	
	public KrakenMD(PairDec pairDec, int period) {
//		this.pair = pairDec.getPair().toUpperCase();
//		this.strUrl = "https://api.kraken.com/0/public/OHLC?pair=" + pairDec.toUpperCase() + "&interval=1440";
//		this.period = period;
		super(pairDec.getPair().toUpperCase(), period);
		this.pairDec = pairDec.getDec();
	}

	void init(boolean print) {
		getLastData();
		calculateMinMax();
		calculateDailyAndPeriodTR();
		if (print) {
			print();
		}
	}

	private void printObjects(int days) {
		int end = this.getPeriodList().size(); // 20
		int start = end - days; // 13
		for (int i = start; i < end; i++) {
//			printObject(this.finalList.get(i).getDailyTR(), start, i, end, "DailyTR: ");
			printObject(this.getPeriodList().get(i).getOC(), end, i, start, "DailyOC: ");
		}
		System.out.println();
	}

	private void printObject(double d, int end, int i, int start, String text) {
		if (i == start) {
			System.out.print(text);
		}
		if (d > 0.0) {
			System.out.print(String.format("+%." + pairDec + "f", d));
		} else {
			System.out.print(String.format("%." + pairDec + "f", d));
		}
		if (i != end - 1) {
			System.out.print(", ");
		}
	}

	private void getLastData() {
		String element = "result";

		JsonParser parser = new JsonParser();
		StringBuilder fileUrl = new StringBuilder("market_data/");
		fileUrl.append(getPair());
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
			System.out.println("... There is Data For Index: " + getPair());
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
				
				HttpUriRequest httpGet = new HttpGet(this.getStrUrl());
				System.out.println("Executing request : " + httpGet.getRequestLine());
				CloseableHttpResponse resp = httpClient.execute(httpGet);
				String strResp = MultiDataUtils.responseToString(resp);
				
				if (strResp != StringUtils.EMPTY) {
					JsonObject json = parser.parse(strResp).getAsJsonObject();
					
					JsonArray err = json.getAsJsonArray("error");
					if (err.isJsonNull()) {
						System.err.println("Error getting index " + this.getPair() + " - "+ err.getAsString());
					} else {
						JsonObject result = json.getAsJsonObject(element);
						data = result.getAsJsonArray(getPair());
						
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
		if (data.size() > this.getPeriod()) {
			temp = data.size() - (this.getPeriod() + 1);
			for (int i = temp; i < data.size(); i++) {
				this.getPeriodList().add(MultiDataUtils.jsonElementToOHLC(data.get(i)));
			}
			this.setLastPrice(this.getPeriodList().get(this.getPeriod()).getClose());
			this.setLastOpen(this.getPeriodList().get(this.getPeriod()).getOpen());
		} else {
			this.setPeriod(data.size());
			for (int i = temp; i < data.size(); i++) {
				this.getPeriodList().add(MultiDataUtils.jsonElementToOHLC(data.get(i)));
			}
			this.setLastPrice(this.getPeriodList().get(this.getPeriod() - 1).getClose());
			this.setLastOpen(this.getPeriodList().get(this.getPeriod() - 1).getOpen());
		}
	}

	private void calculateDailyAndPeriodTR() {
//		List<Double> listTRMax = new ArrayList<>();
		PriorityQueue<Double> pq;
		double sumTR = 0.0;
		double sumOC = 0.0;
		
		for (int i = 1; i < this.getPeriodList().size(); i++) {
			// TR1 today H-L
			double TR1;
			// TR2 yest close - today H
			double TR2;
			// TR3 yest close - today L
			double TR3;
			// OC today C - O
			double OC;
			TR1 = this.getPeriodList().get(i).getHigh() - this.getPeriodList().get(i).getLow();
			TR2 = this.getPeriodList().get(i - 1).getClose() - this.getPeriodList().get(i).getHigh();
			TR3 = this.getPeriodList().get(i - 1).getClose() - this.getPeriodList().get(i).getLow();
			OC = this.getPeriodList().get(i).getClose() - this.getPeriodList().get(i).getOpen() ;
			
//			absTR1 = Math.abs(TR1);
//			absTR2 = Math.abs(TR2);
//			absTR3 = Math.abs(TR3);
			
			Map<Double, Double> m = new HashMap<>();
			m.put(Math.abs(TR1), TR1);
			m.put(Math.abs(TR2), TR2);
			m.put(Math.abs(TR3), TR3);
			
			pq = new PriorityQueue<>(3, Collections.reverseOrder());
			pq.add(Math.abs(TR1));
			pq.add(Math.abs(TR2));
			pq.add(Math.abs(TR3));
//			listTRMax.add(pq.peek());
			double highest = pq.peek();
			sumTR += highest;
			sumOC += Math.abs(OC);
			getPeriodList().get(i).setDailyTR(m.get(highest));
			getPeriodList().get(i).setOC(OC);
		}

//		for (Double TRMax : listTRMax) {
//			sum += TRMax;
//		}
//		this.periodTR = sum.doubleValue() / listTRMax.size();
		this.setPeriodTR(sumTR / (this.getPeriodList().size() - 1)); // because we get one additional day only for its closing value
		this.periodOC = sumOC / (this.getPeriodList().size() - 1); 
		
	}
	
	private void calculateMinMax() {
		if(this.getPeriodList().size() > 0) {
			PriorityQueue<Double> pqMin = new PriorityQueue<>(this.getPeriodList().size());
			PriorityQueue<Double> pqMax = new PriorityQueue<>(this.getPeriodList().size(), Collections.reverseOrder());
			
			for (OHLC element : this.getPeriodList()) {
				pqMin.add(element.getLow());
				pqMax.add(element.getHigh());
			}
			
			this.setPeriodMIN(pqMin.peek());
			this.percCurMIN = Utils.calcPercentage(this.getPeriodMIN(), this.getLastPrice());
			this.setPeriodMAX(pqMax.peek());
			this.percCurMAX = Utils.calcPercentage(this.getPeriodMAX(), this.getLastPrice());
		} else {
			System.out.println("...Executing Request Again: " + this.getStrUrl());
			this.getLastData();
			this.calculateMinMax();
		}
	}
	
	void print() {
//		System.out.println(String.format("***** TRADING INFO %s *****", MultiDataUtils.readPair(this.index)));
//		System.out.println(String.format("Data info for %d day/s period from %s", (this.finalList.size() - 1), MultiDataUtils.KRAKEN));
		System.out.println(String.format("***** TRADING INFO %s %d day/s period from %s *****", MultiDataUtils.readPair(this.getPair()),  (this.getPeriodList().size() - 1), MultiDataUtils.KRAKEN));
		int d = 7;
		printObjects(d);
		System.out.println(String.format("Average OC: %." + pairDec + "f", this.periodOC));
		System.out.println(String.format("Average TR: %." + pairDec + "f", this.getPeriodTR()));
		System.out.println(String.format("Open Price: %." + pairDec + "f$", this.getLastOpen()));
		System.out.println(String.format("Curr Price: %." + pairDec + "f$ %s", this.getLastPrice(), Utils.calcPrintPercentage(this.getLastPrice(), this.getLastOpen())));
		System.out.println(
				String.format(" Min Price: %." + pairDec + "f$ %s", this.getPeriodMIN(), Utils.printPercentage(this.percCurMIN)));
		System.out.println(
				String.format(" Max Price: %." + pairDec + "f$ %s", this.getPeriodMAX(), Utils.printPercentage(this.percCurMAX)));
//		System.out.println(MultiDataUtils.readPair(this.index).toUpperCase());
	}

	public double getPercCurMIN() {
		return percCurMIN;
	}

	public double getPercCurMAX() {
		return percCurMAX;
	}

	public String getIndex() {
		return getPair();
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
