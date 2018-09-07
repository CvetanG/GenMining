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

public class RunMD {

	private final static String allPairsUrl = "https://api.kraken.com/0/public/AssetPairs";
	private static List<String> pairsOnly = new ArrayList<>();
	private static List<String> pairsRemove = new ArrayList<>();
	static {
		pairsRemove.add(".d");
		pairsRemove.add("USDT");

		pairsOnly.add("XMR");
		pairsOnly.add("USD");
	}

	public static List<PairDec> getAllPairs() {
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
					boolean isGood = true;

					if (!pairsRemove.isEmpty()) {
						for (String remove : pairsRemove) {
							if (pair.contains(remove)) {
								isGood = false;
								break;
							}
						}	
							if (isGood) {
								if (!pairsOnly.isEmpty()) {
									for (String only : pairsOnly) {
										if (pair.contains(only)) {
											processJson(pairs, entry, pair);
											break;
										} else {
											continue;
										}
									}
								} else {
									processJson(pairs, entry, pair);
								}
						}
					} else {
						if (!pairsOnly.isEmpty()) {
							for (String only : pairsOnly) {
								if (pair.contains(only)) {
									processJson(pairs, entry, pair);
									break;
								} else {
									continue;
								}
							}
						} else {
							processJson(pairs, entry, pair);
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return pairs;
	}

	private static void processJson(List<PairDec> pairs, Entry<String, JsonElement> entry, String pair) {
		JsonObject object = entry.getValue().getAsJsonObject();
		int dec = object.get("pair_decimals").getAsInt();
		pairs.add(new PairDec(pair, dec));
	}

	public static List<PairDec> getAllPairs(List<PairDec> pairs) {
		return null;
	}
	
	public static void main(String[] args) {
		List<PairDec> pairs = getAllPairs();
		System.out.println(pairs.size());
		
//		for (PairDec p : pairs) {
//			System.out.println(p);
//		}
		
		int period = 55;
		int topNum = 3;
		MultiData md = new MultiData(pairs, period, topNum);
		md.init(false);
	}
}


