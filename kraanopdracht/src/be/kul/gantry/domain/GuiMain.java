package be.kul.gantry.domain;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
public class GuiMain extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("time");
    yAxis.setLabel("x");
    final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

    lineChart.setTitle("grafiek");
    
    XYChart.Series<Number, Number> series1 = new XYChart.Series<Number, Number>();
    series1.setName("kraan1");
    
    XYChart.Series<Number, Number> series2 = new XYChart.Series<Number, Number>();
    series2.setName("kraan2");

    Scene scene = new Scene(lineChart, 2000, 1000);

    
    FileInputStream fstream = new FileInputStream("output.csv");
    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

    String line;
    br.readLine();
    while ((line = br.readLine()) != null)   {
    	String[] args = line.split(";");
    	if(args[0].equals("0")){
    		series1.getData().add(new XYChart.Data<Number, Number>(Double.parseDouble(args[1]), Double.parseDouble(args[2])));
    	}
    	else{
    		series2.getData().add(new XYChart.Data<Number, Number>(Double.parseDouble(args[1]), Double.parseDouble(args[2])));
    	}
      
    }
    lineChart.getData().add(series1);
    lineChart.getData().add(series2);

    //Close the input stream
    br.close();

    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}