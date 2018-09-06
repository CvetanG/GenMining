package app.multiData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MultiData {

	private int topNum;
	private List<Kraken_02> krakenList;
	
	public MultiData(List<PairDec> set, int period, int topNum) {
		this.topNum = topNum;
		krakenList = new ArrayList<>();
		for (PairDec pair : set) {
			krakenList.add(new Kraken_02(pair, period));
		} 
	}
	
	public void init(boolean print) {
		for (Kraken_02 kr : krakenList) {
			kr.init(print);
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Start Finding Deals");
		findDeals();
	}
	
	private void findDeals() {
		if (topNum > krakenList.size()) {
			topNum = krakenList.size();
		}
		System.out.println();
		System.out.println("*** TOP CURRUNCIES TO BUY ***");
		Collections.sort(krakenList, buyComparator());
		for (int i = 0; i < topNum; i++) {
			krakenList.get(i).print();
		}

		System.out.println();
		System.out.println("*** TOP CURRUNCIES TO SELL ***");
		Collections.sort(krakenList, sellComparator());
		for (int i = 0; i < topNum; i++) {
			krakenList.get(i).print();
		}
	}
	
	static Comparator<Kraken_02> buyComparator() {
		return new Comparator<Kraken_02>() {
			@Override
			public int compare(Kraken_02 k1, Kraken_02 k2) {
				return Double.compare(k2.getPercCurMIN(), k1.getPercCurMIN());
			}
		};
	}
	
	static Comparator<Kraken_02> sellComparator() {
		return new Comparator<Kraken_02>() {
			@Override
			public int compare(Kraken_02 k1, Kraken_02 k2) {
				return Double.compare(k1.getPercCurMAX(), k2.getPercCurMAX());
			}
		};
	}

	public static void main(String args[]) {
		List<PairDec> pairs = new ArrayList<>();
//		pairs.add(new PairDec("BCHEUR", 1));
//		pairs.add(new PairDec("DASHEUR", 3));
//		pairs.add(new PairDec("XREPXXBT", 6));
//		pairs.add(new PairDec("GNOEUR", 2));
//		pairs.add(new PairDec("XETCZEUR", 3));
		pairs.add(new PairDec("DASHEUR", 3));
		int topNum = 1;
		MultiData md = new MultiData(pairs, 55, topNum);
		boolean print = false;
		md.init(print);
	}
}
