package app.multiData;

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

import app.entities.PairDec;

public class RunMD {

	private final static String allPairsUrl = "https://api.kraken.com/0/public/AssetPairs";
	private static List<String> pairsOnly = new ArrayList<>();
	private static List<String> pairsRemove = new ArrayList<>();
	static {
		pairsOnly.add("XMR");
		pairsOnly.add("USD");
		
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
			HttpUriRequest httpGet = new HttpGet(allPairsUrl);
			System.out.println("Executing request : " + httpGet.getRequestLine());
			HttpResponse resp = httpClient.execute(httpGet);
			String strResp = MultiDataUtils.responseToString(resp);
			if (strResp != StringUtils.EMPTY) {
				JsonObject json = parser.parse(strResp).getAsJsonObject();
				result = json.getAsJsonObject(element);
				// Set<String> resSet = result.keySet();
				// Set<Entry<String, JsonElement>> entrySet = result.entrySet();

				for (Entry<String, JsonElement> entry : result.entrySet()) {
					String pair = entry.getKey().toString();
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
		List<PairDec> pairs = RunMD.getAllPairs();
		System.out.println(pairs.size());
		
//		for (PairDec p : pairs) {
//			System.out.println(p);
//		}
		
		int period = 55;
		int topNum = 5;
		List<PairDec> filteredPairs = RunMD.filterPairs(pairs);
		System.out.println(filteredPairs.size());
		
//		for (PairDec p : filteredPairs) {
//			System.out.println(p);
//		}
		MultiData md = new MultiData(filteredPairs, period, topNum);
		md.init(false);
	}
}


