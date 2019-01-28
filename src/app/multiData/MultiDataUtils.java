package app.multiData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.poi.util.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import app.entities.OHLC;

public class MultiDataUtils {
	
	private MultiDataUtils() {
		throw new AssertionError();
	}
	
	public final static String KRAKEN = "kraken.com";

	public static String readCurruncy(String curr) {
		Properties prop = new Properties();
		try (InputStream input = new FileInputStream("curruncy.properties")) {
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return prop.getProperty(curr);
	}
	
	public static String readPair(String pair) {
		StringBuilder sb = new StringBuilder();
		Properties prop = new Properties();
		try (InputStream input = new FileInputStream("curruncy.properties")) {
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		Set<Object> keySet = prop.keySet();
		for (Object obj : keySet) {
			if (pair.startsWith(obj.toString())) {
				sb.append(prop.getProperty(obj.toString()));
				sb.append(" ");
				break;
			}
		}
		for (Object obj : keySet) {
			if (pair.endsWith(obj.toString())) {
				sb.append(prop.getProperty(obj.toString()));
				break;
			}
		}
		return sb.toString().toUpperCase();
	}
	
	public static String responseToString(HttpResponse response) throws IOException {
		HttpEntity entity = response.getEntity();
		if ((response.getStatusLine().getStatusCode() == HttpStatus.SC_OK || response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)
				&& entity != null) {
			InputStream stream = entity.getContent();
			String line;
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			StringBuilder strResp = new StringBuilder();
			while ((line = br.readLine()) != null) {
				strResp.append(line);
			}
			IOUtils.closeQuietly(stream);
			IOUtils.closeQuietly(br);
			return strResp.toString();
		} else {
			return StringUtils.EMPTY;
		}
	}
	
	public static OHLC jsonElementToOHLC(JsonElement jsonElement) {
		OHLC result = new OHLC();
		Gson gson = new Gson();
		Type typeOfT = new TypeToken<List<String>>() {
		}.getType();
		List<String> yourList = gson.fromJson(jsonElement, typeOfT);

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

	public static void main(String[] args) {
		System.out.println(MultiDataUtils.readCurruncy("BCH"));
		System.out.println(MultiDataUtils.readCurruncy("DASH"));
	}
}
