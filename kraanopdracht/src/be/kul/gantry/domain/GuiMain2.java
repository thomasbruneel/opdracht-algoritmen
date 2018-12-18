package be.kul.gantry.domain;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Random;

import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GuiMain2 extends Application {
    
	@Override
	public void start(Stage primaryStage) throws IOException {
		final LineChart<Number, Number> chart = createChart();
		
		final StackPane chartContainer = new StackPane();
		chartContainer.getChildren().add(chart);
		
		final Rectangle zoomRect = new Rectangle();
		zoomRect.setManaged(false);
		zoomRect.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
		chartContainer.getChildren().add(zoomRect);
		
		setUpZooming(zoomRect, chart);
		
		final HBox controls = new HBox(10);
		controls.setPadding(new Insets(10));
		controls.setAlignment(Pos.CENTER);
		
		final Button zoomButton = new Button("Zoom");
		final Button resetButton = new Button("Reset");
		zoomButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doZoom(zoomRect, chart);
            }
        });
		resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final NumberAxis xAxis = (NumberAxis)chart.getXAxis();
                xAxis.setLowerBound(0);
                xAxis.setUpperBound(20000);
                final NumberAxis yAxis = (NumberAxis)chart.getYAxis();
                yAxis.setLowerBound(0);
                yAxis.setUpperBound(1100);
                
                zoomRect.setWidth(0);
                zoomRect.setHeight(0);
            }
        });
		final BooleanBinding disableControls = 
		        zoomRect.widthProperty().lessThan(5)
		        .or(zoomRect.heightProperty().lessThan(5));
		zoomButton.disableProperty().bind(disableControls);
		controls.getChildren().addAll(zoomButton, resetButton);
		
		final BorderPane root = new BorderPane();
		root.setCenter(chartContainer);
		root.setBottom(controls);
		
		final Scene scene = new Scene(root, 2000, 1000);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private LineChart<Number, Number> createChart() throws IOException {
	    final NumberAxis xAxis = createXAxis();
	    final NumberAxis yAxis = createYAxis();	    
	    final LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
	    chart.setAnimated(false);
	    chart.setCreateSymbols(false);
	    chart.getData().add(generateChartData0());
	    chart.getData().add(generateChartData1());
	    return chart ;
	}

    private NumberAxis createXAxis() {
        final NumberAxis xAxis = new NumberAxis();
	    xAxis.setAutoRanging(false);
	    xAxis.setLowerBound(0);
	    xAxis.setUpperBound(20000);
	    xAxis.setLabel("time");
        return xAxis;
    }
    
    private NumberAxis createYAxis() {
        final NumberAxis yAxis = new NumberAxis();
	    yAxis.setAutoRanging(false);
	    yAxis.setLowerBound(0);
	    yAxis.setUpperBound(1100);
	    yAxis.setLabel("x");
        return yAxis;
    }
    
    private Series<Number, Number> generateChartData0() throws IOException {
        final Series<Number, Number> series = new Series<>();
        series.setName("InputKraan");
        FileInputStream fstream = new FileInputStream("output.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String line;
        br.readLine();
        while ((line = br.readLine()) != null)   {
        	String[] args = line.split(";");
        	if(args[0].equals("0")){
        		series.getData().add(new XYChart.Data<Number, Number>(Double.parseDouble(args[1]), Double.parseDouble(args[2])));
        	}
        	
          
        }

        return series;
    }
    
    private Series<Number, Number> generateChartData1() throws IOException {
        final Series<Number, Number> series = new Series<>();
        series.setName("OutputKraan");
        FileInputStream fstream = new FileInputStream("output.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String line;
        br.readLine();
        while ((line = br.readLine()) != null)   {
        	String[] args = line.split(";");
        	if(args[0].equals("1")){
        		series.getData().add(new XYChart.Data<Number, Number>(Double.parseDouble(args[1]), Double.parseDouble(args[2])));
        	}
        	
          
        }

        return series;
    }
    
    private void setUpZooming(final Rectangle rect, final Node zoomingNode) {
        final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
        zoomingNode.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseAnchor.set(new Point2D(event.getX(), event.getY()));
                rect.setWidth(0);
                rect.setHeight(0);
            }
        });
        zoomingNode.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();
                rect.setX(Math.min(x, mouseAnchor.get().getX()));
                rect.setY(Math.min(y, mouseAnchor.get().getY()));
                rect.setWidth(Math.abs(x - mouseAnchor.get().getX()));
                rect.setHeight(Math.abs(y - mouseAnchor.get().getY()));
            }
        });
    }
    
    private void doZoom(Rectangle zoomRect, LineChart<Number, Number> chart) {
        Point2D zoomTopLeft = new Point2D(zoomRect.getX(), zoomRect.getY());
        Point2D zoomBottomRight = new Point2D(zoomRect.getX() + zoomRect.getWidth(), zoomRect.getY() + zoomRect.getHeight());
        final NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        Point2D yAxisInScene = yAxis.localToScene(0, 0);
        final NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        Point2D xAxisInScene = xAxis.localToScene(0, 0);
        double xOffset = zoomTopLeft.getX() - yAxisInScene.getX() ;
        double yOffset = zoomBottomRight.getY() - xAxisInScene.getY();
        double xAxisScale = xAxis.getScale();
        double yAxisScale = yAxis.getScale();
        xAxis.setLowerBound(xAxis.getLowerBound() + xOffset / xAxisScale);
        xAxis.setUpperBound(xAxis.getLowerBound() + zoomRect.getWidth() / xAxisScale);
        yAxis.setLowerBound(yAxis.getLowerBound() + yOffset / yAxisScale);
        yAxis.setUpperBound(yAxis.getLowerBound() - zoomRect.getHeight() / yAxisScale);
        zoomRect.setWidth(0);
        zoomRect.setHeight(0);
    }

	public static void main(String[] args) {
		launch(args);
	}
}