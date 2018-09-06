package app.multiData;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MultiDataUtils {

	public static String readCurruncy(String curr) {
		Properties prop = new Properties();
		try (InputStream input = new FileInputStream("curruncy.properties")) {
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return prop.getProperty(curr);
	}

	public static void main(String[] args) {
		System.out.println(readCurruncy("BCH"));
		System.out.println(readCurruncy("DASH"));
	}
}
