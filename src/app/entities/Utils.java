package app.entities;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class Utils {
	
	private Utils() {
		throw new AssertionError();
	}
	
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
	
	public static String currencyFormaterWithSuffix(String curr) {
		curr = removeCurrSuffix(curr);
		return currencyFormater(curr);
	}
	
	public static String removeCurrSuffix(String curr) {
		return curr.substring(0, curr.length()-4);
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

	public static long removeSeparetors(String curr) {
	    return Long.parseLong(curr.replace(",", ""));
	}
	
	public static long removeSeparetorsAndCurrency(String curr) {
		curr = curr.replace(",", "");
		curr = curr.replace("$", "");
		curr = curr.replace("\n", "").replace("\r", "");
		String str = removeCurrSuffix(curr);
	    return Long.parseLong(str);
	}
	
	/*
	private static void createVariationsRepetition(List<int[]> listResults, List<Integer> dataToCombine, int[] result, int count) {
		if (count < result.length) {
			for (int i = 0; i < dataToCombine.size(); i++) {
				result[count] = dataToCombine.get(i);
				createVariationsRepetition(listResults, dataToCombine, result, count + 1);
			}
		} else {
//			int index = 0;
//			dataToCombine.remove(index);
//			index ++;
			listResults.add(result.clone());
		}
	}
	*/
	
	public static List<List<Integer>> listPermutations(List<Integer> permIntList, int count) {
	    if (count == permIntList.size()) {
	        List<List<Integer>> result = new ArrayList<>();
	        result.add(new ArrayList<Integer>());
	        return result;
	    }
	    List<List<Integer>> returnList = new ArrayList<>();
	    Integer firstElement = permIntList.get(count);
	    List<List<Integer>> recursiveReturn = listPermutations(permIntList, count + 1);
	    for (List<Integer> intList : recursiveReturn) {
	        for (int index = 0; index <= intList.size(); index++) {
	            List<Integer> temp = new ArrayList<>(intList);
	            temp.add(index, firstElement);
	            returnList.add(temp);
	        }
	    }
	    return returnList;
	}
	
	public static void deleteLower(List<List<Integer>> listInteger) {
		for (List<Integer> intList : listInteger) {
			List<Integer> toRemove = new ArrayList<>();
	        for (int i = 1; i < intList.size(); i++) {
				if (intList.get(i) < intList.get(i - 1)) {
					toRemove.add(intList.get(i));
				}
			}
	        intList.removeAll(toRemove);
	        System.out.println();
	    }
	}
	
	public static double calcPercentage(double price, double curPrice) {
		double pers = (price * 100.0f) / curPrice;
		return -(100.0 - pers);
	}
	
	public static String printPercentage(double pro) {
		if (pro > 0.0) {
			return String.format("(+%.2f%s)", pro, "%");
		} else {
			return String.format("(%.2f%s)", pro, "%");
		}
	}
	
	public static String calcPrintPercentage(double a, double b) {
		return printPercentage(calcPercentage(a, b));
	}
	
	public static void main(String[] args) {
		
//		List<int[]> listResults = new ArrayList<>();
		int size = 4;
		
		List<Integer> dataToCombine = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			dataToCombine.add(i + 1);
		}
		
		/*
		int[] result = new int[5];
		Utils.createVariationsRepetition(listResults, dataToCombine, result, 0);
		
		for (int[] rep : listResults) {
			System.out.println(Arrays.toString(rep));
		}
		*/
		
		//
		List<List<Integer>> resultPermLists = listPermutations(dataToCombine, 0);
		Utils.deleteLower(resultPermLists);
		
		
	    for (List<Integer> intList : resultPermLists) {
	        String appender = "";
	        for (Integer i : intList) {
	            System.out.print(appender + i);
	            appender = " ";
	        }
	        System.out.println();
	    }
	    
	    String testString = "17,296,526 XMR";
	    Long l = removeSeparetorsAndCurrency(testString);
	    System.out.println(l);
	    
	    
	}

	
}
