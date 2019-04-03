package app.kraken;

import java.util.ArrayList;
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

public class LineChartSample extends Application {
	
	List<Map<String, Double>> list;
	String stageTitle;
	String lineChartTitle;
	String xAxisLabel;
	
	/*
	public LineChartSample(List<Map<String, Double>> list) {
		this.list = list;
		this.stageTitle = "Line Chart Sample";
		this.lineChartTitle = "Stock Monitoring, 2010";
		this.xAxisLabel = "Month";
	}
	*/
	
	public LineChartSample(String stageTitle, String lineChartTitle, String xAxisLabel, List<Map<String, Double>> list) {
		super();
		this.stageTitle = stageTitle;
		this.lineChartTitle = lineChartTitle;
		this.xAxisLabel = xAxisLabel;
		this.list = list;
	}
	
	/*
	public LineChartSample() {
		this.stageTitle = "Line Chart Sample";
		this.lineChartTitle = "Stock Monitoring, 2010";
		this.xAxisLabel = "Month";
		
		Map<String, Double> data1 = new LinkedHashMap<>();
        
        data1.put("Jan", 23.0);
        data1.put("Feb", 14.0);
        data1.put("Mar", 15.0);
        data1.put("Apr", 24.0);
        data1.put("May", 34.0);
        data1.put("Jun", 36.0);
        data1.put("Jul", 22.0);
        data1.put("Aug", 45.0);
        data1.put("Sep", 43.0);
        data1.put("Oct", 17.0);
        data1.put("Nov", 29.0);
        data1.put("Dec", 25.0);
        
        Map<String, Double> data2 = new LinkedHashMap<>();
        
        data2.put("Jan", 33.0);
        data2.put("Feb", 34.0);
        data2.put("Mar", 25.0);
        data2.put("Apr", 44.0);
        data2.put("May", 39.0);
        data2.put("Jun", 16.0);
        data2.put("Jul", 55.0);
        data2.put("Aug", 54.0);
        data2.put("Sep", 48.0);
        data2.put("Oct", 27.0);
        data2.put("Nov", 37.0);
        data2.put("Dec", 29.0);
        
        Map<String, Double> data3 = new LinkedHashMap<>();
        
        data3.put("Jan", 44.0);
        data3.put("Feb", 35.0);
        data3.put("Mar", 36.0);
        data3.put("Apr", 33.0);
        data3.put("May", 31.0);
        data3.put("Jun", 26.0);
        data3.put("Jul", 22.0);
        data3.put("Aug", 25.0);
        data3.put("Sep", 43.0);
        data3.put("Oct", 44.0);
        data3.put("Nov", 45.0);
        data3.put("Dec", 44.0);
        
        
        list = new ArrayList<>();
        list.add(data1);
        list.add(data2);
        list.add(data3);
	}
	
	*/

	public LineChartSample() {
		super();
	}

	private XYChart.Series loadSeriesData(Map<String, Double> data) {
		XYChart.Series series = new XYChart.Series();
		for (Map.Entry<String, Double> entry : data.entrySet()) {
			 series.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
		}
		return series;
	}
	
	@Override public void start(Stage stage) {
		
		String line01Name = "maxPrice";
		String line02Name = "minPrice";
		String line03Name = "Buy/Sell";
		
        stage.setTitle(this.stageTitle);
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
         xAxis.setLabel(this.xAxisLabel);
        final LineChart<String,Number> lineChart = 
                new LineChart<>(xAxis,yAxis);
       
        lineChart.setTitle(this.lineChartTitle);
        
        List<Series<String, Number>> listSeries = new ArrayList<>();
        
        for (Map<String, Double> data : this.list) {
        	listSeries.add(loadSeriesData(data));
		}
        
        listSeries.get(0).setName(line01Name);
        listSeries.get(1).setName(line02Name);
        listSeries.get(2).setName(line03Name);
        
        Scene scene  = new Scene(lineChart, 800, 600);   
        
        lineChart.getData().addAll(listSeries);
       
        stage.setScene(scene);
        stage.show();
    }
 
 
    public static void main(String[] args) {
        launch(args);
    }

}
