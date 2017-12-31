package app.entities;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Utils {
	public static String duration(long startTime, long endTime) {
		long totalTime = endTime - startTime;
		
		int seconds = (int) (totalTime / 1000) % 60 ;
		int minutes = (int) ((totalTime / (1000*60)) % 60);
		int milisec = (int) (totalTime - ((seconds * 1000) + (minutes * 60 * 1000)));
		
		StringBuilder sb = new StringBuilder(64);
		sb.append("Elapsed time: ");
        sb.append(minutes);
        sb.append(" min, ");
        sb.append(seconds);
        sb.append(" sec. ");
        sb.append(milisec);
        sb.append(" milsec.");
        
		return sb.toString();
	}
	
	public static String clearFormatCurr(String curr) {
		curr = curr.substring(0, curr.length()-4);
		String result = currencyFormater(curr);
		return result;
	}
	
	public static String currencyFormater(String curr) {
		curr = curr.replace(",", "");
		curr = curr.replace("$", "");
		double dCurr = Double.parseDouble(curr);
		DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance();
		df.setMinimumFractionDigits(2);
	    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
	    dfs.setCurrencySymbol("");
	    dfs.setMonetaryDecimalSeparator('.');
//	    dfs.setGroupingSeparator(' ');
	    df.setGroupingUsed(false);
	    df.setDecimalSeparatorAlwaysShown(true);
	    df.setDecimalFormatSymbols(dfs);
	    String formCur = df.format(dCurr);
	    return formCur;
	}

	public static double removeSeparetors(String curr) {
		curr = curr.replace(",", "");
		double dCurrr = Double.parseDouble(curr);
	    return dCurrr;
	}
}
