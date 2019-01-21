package app.kraken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;

public class Calculator extends Application {

	public static List<Map<String, Double>> list;

	public static final int size = 10;
	
	public static double min;
	public static double max;

	public static List<TradingContracts> calcBestTradingContracts(Kraken k, Combinations c) {

		List<TradingContracts> unOrderedList = new ArrayList<>();

		for (Integer[] resultList : c.resultList) {
			TradingContracts temp = new TradingContracts(resultList, k.getPeriodList());
			unOrderedList.add(temp);
		}

		return unOrderedList;

	}

	static Comparator<TradingContracts> getUSDComparator() {
		return new Comparator<TradingContracts>() {
			@Override
			public int compare(TradingContracts tc1, TradingContracts tc2) {
				return Double.compare(tc2.getUSD(), tc1.getUSD());
			}
		};
	}

	static Comparator<TradingContracts> getXMRComparator() {
		return new Comparator<TradingContracts>() {
			@Override
			public int compare(TradingContracts tc1, TradingContracts tc2) {
				return Double.compare(tc2.getXMR(), tc1.getXMR());
			}
		};
	}

	private XYChart.Series loadSeriesData(Map<String, Double> data) {
		XYChart.Series series = new XYChart.Series();
		for (Map.Entry<String, Double> entry : data.entrySet()) {
			series.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
		}
		return series;
	}

	@Override
	public void start(Stage stage) {

		String line01Name = "maxPrice/Sell";
		String line02Name = "minPrice/Buy";
		String line03Name = "Buy/Sell";
		String stageTitle = "Line Chart Sample";
		String lineChartTitle = "Stock Monitoring, last " + size + " days";
		String xAxisLabel = "Date";
		String yAxisLabel = "Price";

		stage.setTitle(stageTitle);
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel(xAxisLabel);

		yAxis.setLabel(yAxisLabel);
		yAxis.setAutoRanging(false);
		yAxis.setLowerBound(Math.round(min - (min * 5 / 100)));
	    yAxis.setUpperBound(Math.round(max + (max * 5 / 100)));
		
	    final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);

		lineChart.setTitle(lineChartTitle);

		List<Series<String, Number>> listSeries = new ArrayList<>();

		for (Map<String, Double> data : this.list) {
			listSeries.add(loadSeriesData(data));
		}

		listSeries.get(0).setName(line01Name);
		listSeries.get(1).setName(line02Name);
		listSeries.get(2).setName(line03Name);

		Scene scene = new Scene(lineChart, 800, 600);

		lineChart.getData().addAll(listSeries);

		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {

		SimpleDateFormat dtf = new SimpleDateFormat("dd/MM");
		String pair = "XXMRZUSD";
		Kraken k = new Kraken(pair, size);
		k.init();

		Combinations c = new Combinations(size);

		List<TradingContracts> resultList = Calculator.calcBestTradingContracts(k, c);

		System.out.println();

		int topNum = 5;

		System.out.println("*** TOP USD ***");
		Integer[] topTradingUSD = null;
		Collections.sort(resultList, getUSDComparator());
		for (int i = 0; i < topNum; i++) {
			resultList.get(i).printTrading();
			if (i == 0) {
				topTradingUSD = resultList.get(i).getCombList();
			}
			System.out.println();
		}

		System.out.println("*** TOP XMR ***");
		Collections.sort(resultList, getXMRComparator());
		for (int i = 0; i < topNum; i++) {
			resultList.get(i).printTrading();
			System.out.println();
		}

		list = new ArrayList<>();
		min = k.getPeriodMIN();
		max = k.getPeriodMAX();

		Map<String, Double> maxPrice = new LinkedHashMap<>();
		Map<String, Double> minPrice = new LinkedHashMap<>();
		Map<String, Double> buySell = new LinkedHashMap<>();

		for (int i = 0; i < k.getFinalList().size() - 0; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, i - k.getFinalList().size() + 1);
			Date date = cal.getTime();

			maxPrice.put(dtf.format(date), k.getFinalList().get(i).getHigh());
			minPrice.put(dtf.format(date), k.getFinalList().get(i).getLow());

		}

		for (int i = 0; i < topTradingUSD.length; i++) {
			if (topTradingUSD[i] != null) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, topTradingUSD[i] - topTradingUSD.length);
				Date date = cal.getTime();
				if (i % 2 == 0) {
					buySell.put(dtf.format(date), k.getFinalList().get(topTradingUSD[i]).getHigh());
				} else {
					buySell.put(dtf.format(date), k.getFinalList().get(topTradingUSD[i]).getLow());
				}
			}
		}

		list.add(maxPrice);
		list.add(minPrice);
		list.add(buySell);

		launch(args);
	}

}
