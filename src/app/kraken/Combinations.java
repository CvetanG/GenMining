package app.kraken;

import java.util.ArrayList;
import java.util.List;

public class Combinations {
	
	public List<Integer[]> resultList;
	
	public int size;
	
    public Combinations(int size) {
		this.size = size;
		this.resultList = new ArrayList<Integer[]>();
		Integer[] result = new Integer[size];
        List<Integer> dataToCombine = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			dataToCombine.add(i + 1);
		}
		
		combineList(result, dataToCombine, 0);
//		System.out.println("***" + resultList.size() + "***");
		
	}
    
	public List<Integer[]> getResultList() {
		return resultList;
	}

//	public void setResultList(List<Integer[]> resultList) {
//		this.resultList = resultList;
//	}



	// print all subsets of the characters in s
    public static void combStringChar1(String s) {
    	combStringChar1("", s);
    }
    // print all subsets of the remaining elements, with given prefix 
    private static void combStringChar1(String prefix, String s) {
        if (s.length() > 0) {
//            System.out.println(prefix + s.charAt(0));
            combStringChar1(prefix + s.charAt(0), s.substring(1));
            combStringChar1(prefix,               s.substring(1));
        }
    }  
    
    // alternate implementation
    public static void combStringChar2(String s) {
    	combStringChar2("", s);
    }
    private static void combStringChar2(String prefix, String s) {
    	if (!"".equals(prefix)) {
    		System.out.println(prefix);
		}
        for (int i = 0; i < s.length(); i++)
        	combStringChar2(prefix + s.charAt(i), s.substring(i + 1));
    }
    
    
    ///////////////////////////////////
    
    private void combineList(Integer[] result, List<Integer> dataToCombine, int count) {
    	if (result[0] != null) {
    		resultList.add(result.clone());
    		for (int i = 0; i < result.length; i++) {
				if (result[i] != null) {
//					System.out.print(result[i] + " ");
				}
			}
//    		System.out.println();
    	}
			
        for (int i = 0; i < dataToCombine.size(); i++) {
        	result[count] = dataToCombine.get(i);
        	combineList(result.clone(), dataToCombine.subList(i + 1 , dataToCombine.size()), count + 1);
        }
	}
    
    public static void main(String[] args) {
    	
    	/*
    	int n = 4;
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String elements = alphabet.substring(0, n);
        String elements02 = alphabet.substring(n);
        System.out.println(elements02);

        // using first implementation

        // using second implementation
//        combStringChar2(elements);
//        System.out.println();
        
        */
        Combinations c = new Combinations(20);
		System.out.println("***" + c.getResultList().size() + "***");
		
    }
    
}

