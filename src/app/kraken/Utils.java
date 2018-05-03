package app.kraken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
	
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
	
	public static List<List<Integer>> listPermutations(List<Integer> permIntList, int count) {
	    if (count == permIntList.size()) {
	        List<List<Integer>> result = new ArrayList<List<Integer>>();
	        result.add(new ArrayList<Integer>());
	        return result;
	    }
	    List<List<Integer>> returnList = new ArrayList<List<Integer>>();
	    Integer firstElement = permIntList.get(count);
	    List<List<Integer>> recursiveReturn = listPermutations(permIntList, count + 1);
	    for (List<Integer> intList : recursiveReturn) {
	        for (int index = 0; index <= intList.size(); index++) {
	            List<Integer> temp = new ArrayList<Integer>(intList);
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
	
	public static void main(String[] args) {
		
		List<int[]> listResults = new ArrayList<>();
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
	}
}
