package app.multidata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import app.PairsConstant;
import app.entities.PairDec;

public class RunMD {

	private static final String ALL_PAIRS_URL = "https://api.kraken.com/0/public/AssetPairs";
	private static List<String> pairsOnly = new ArrayList<>();
	private static List<String> pairsRemove = new ArrayList<>();
	static {
		pairsOnly.add("XMR");
		pairsOnly.add("USD");
		pairsOnly.add("XRP");
		
		pairsRemove.add(".d");
		pairsRemove.add("USDT");
	}
	
	private RunMD() {
		throw new AssertionError();
	}

	private static List<PairDec> getAllPairs() {
		List<PairDec> pairs = new ArrayList<>();
		JsonParser parser = new JsonParser();
		String element = "result";
		JsonObject result = null;
		try {

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpUriRequest httpGet = new HttpGet(ALL_PAIRS_URL);
			System.out.println("Executing request : " + httpGet.getRequestLine());
			HttpResponse resp = httpClient.execute(httpGet);
			String strResp = MultiDataUtils.responseToString(resp);
			if (!StringUtils.EMPTY.equals(strResp)) {
				JsonObject json = parser.parse(strResp).getAsJsonObject();
				result = json.getAsJsonObject(element);
				// Set<String> resSet = result.keySet();
				// Set<Entry<String, JsonElement>> entrySet = result.entrySet();

				for (Entry<String, JsonElement> entry : result.entrySet()) {
					String pair = entry.getKey();
					JsonObject object = entry.getValue().getAsJsonObject();
					int dec = object.get("pair_decimals").getAsInt();
					pairs.add(new PairDec(pair, dec));
				}
			}
					
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return pairs;
	}
	
	private static List<PairDec> filterPairs(List<PairDec> initialPairs) {
		List<PairDec> filteredPairs = new ArrayList<>();
		for (PairDec pair : initialPairs) {
			if (!pairsOnly.isEmpty()) {
				for (String currency : pairsOnly) {
					if (pair.getPair().contains(currency)) {
						pair.setGood(true);
						break;
					}
				}
			} else {
				pair.setGood(true);
			}
			if (!pairsRemove.isEmpty()) {
				for (String currency : pairsRemove) {
					if (pair.getPair().contains(currency)) {
						pair.setGood(false);
						break;
					}
				}
			}
		}
		
		for (PairDec pair : initialPairs) {
			if (pair.getGood()) {
				filteredPairs.add(pair);
			}
		}
		return filteredPairs;
	}
	

	public static void main(String[] args) {
		int period = 55;
		int topNum = 5;
		List<String> pairsStrRevolut = new ArrayList<>();
		List<PairDec> pairsRevolut = new ArrayList<>();
		pairsStrRevolut.add(PairsConstant.XRP_USD);

		pairsStrRevolut.add(PairsConstant.BTC_USD);
		pairsStrRevolut.add(PairsConstant.BCH_USD);
		pairsStrRevolut.add(PairsConstant.ETH_USD);
		pairsStrRevolut.add(PairsConstant.LTC_USD);
		
		for (String p : pairsStrRevolut) {
			pairsRevolut.add(new PairDec(p, 0));
		}
		
		
		
		List<PairDec> totalPairs = RunMD.getAllPairs();
		System.out.println("Total Pairs: " + totalPairs.size());
		
//		for (PairDec p : pairs) {
//			System.out.println(p);
//		}
		
		List<PairDec> filteredPairs = RunMD.filterPairs(totalPairs);
		System.out.println("Filtered Pairs: " + filteredPairs.size());
		
//		for (PairDec p : filteredPairs) {
//			System.out.println(p);
//		}
		
		MultiData md = new MultiData(filteredPairs, period, topNum);
		md.init(false);
		
		System.out.println();
		totalPairs.retainAll(pairsRevolut);
		System.out.println("***** REVOLUT DATA START *****");
		MultiData mdR = new MultiData(totalPairs, period, pairsRevolut.size());
		mdR.init(false);
		System.out.println("***** REVOLUT DATA END *****");
	}
}


