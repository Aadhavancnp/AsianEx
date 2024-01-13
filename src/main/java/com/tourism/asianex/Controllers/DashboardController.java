package com.tourism.asianex.Controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class DashboardController implements Initializable {
    private final Map<String, Number> visitors = new HashMap<>();
    private XYChart.Series<Number, Number> lineSeries;
    private XYChart.Series<Number, Number> lineSeries1;
    private XYChart.Series<Number, Number> lineSeries2;
    @FXML
    private VBox visitorsBox;

    @FXML
    private HBox realTimeVisitorsBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setVisitors();
        setBarChart();
        setLineChart();
    }

    private void setLineChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Real Time Visitors");
        xAxis.setLabel("Time");
        yAxis.setLabel("Number of Visitors");
        lineSeries = new XYChart.Series<>();
        lineSeries1 = new XYChart.Series<>();
        lineSeries2 = new XYChart.Series<>();
        lineChart.setLegendVisible(false);

        List<XYChart.Series<Number, Number>> seriesList = new ArrayList<>();
        seriesList.add(lineSeries);
        seriesList.add(lineSeries1);
        seriesList.add(lineSeries2);
        lineChart.getData().addAll(seriesList);

        realTimeVisitorsBox.getChildren().add(lineChart);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            addData(lineSeries, 10);
            addData(lineSeries1, 9);
            addData(lineSeries2, 8);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void addData(XYChart.Series<Number, Number> series, int maxSize) {
        series.getData().add(new XYChart.Data<>(System.currentTimeMillis() % maxSize, Math.random() * 1000));
        if (series.getData().size() > maxSize) {
            series.getData().removeFirst();
        }
    }


    private void setBarChart() {
        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();
        BarChart<Number, String> visitorsBar = new BarChart<>(xAxis, yAxis);
        visitorsBar.setTitle("Popular Destinations");
        visitorsBar.setLegendVisible(false);
        XYChart.Series<Number, String> series = new XYChart.Series<>();
        visitors.forEach((key, value) -> series.getData().add(new XYChart.Data<>(0, key)));
        visitorsBar.getData().add(series);
        visitorsBox.getChildren().add(visitorsBar);
        Timeline timeline = new Timeline();
        series.getData().forEach(data -> {
            KeyValue keyValue = new KeyValue(data.XValueProperty(), visitors.get(data.getYValue()), Interpolator.EASE_BOTH);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), keyValue);
            timeline.getKeyFrames().add(keyFrame);
        });
        timeline.play();
    }

    private void setVisitors() {
        visitors.put("Kyoto", 123456);
        visitors.put("Chiang Mai", 98765);
        visitors.put("Hoi An", 87654);
        visitors.put("Siem Reap", 76543);
        visitors.put("Tokyo", 65432);
        visitors.put("Bali", 54321);
        visitors.put("Luang Prabang", 43210);
        visitors.put("Singapore", 32109);
        visitors.put("Hong Kong", 21098);
        visitors.put("Seoul", 10987);
    }
}

