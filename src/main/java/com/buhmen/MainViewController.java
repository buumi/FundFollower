package com.buhmen;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gillius.jfxutils.chart.JFXChartUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class MainViewController {

    @FXML
    public TableColumn<Fund, String> nameColumn, valueColumn, oneDayAgoColumn, threeDaysAgoColumn, oneWeekAgoColumn, oneMonthAgoColumn;
    @FXML
    public NumberAxis xAxis, yAxis;
    @FXML
    private TableView<Fund> table;
    @FXML
    private LineChart<Long, Double> chart;

    private ObservableList<JSONObject> data = FXCollections.observableArrayList();

    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        oneDayAgoColumn.setCellValueFactory(new PropertyValueFactory<>("oneDayAgo"));
        threeDaysAgoColumn.setCellValueFactory(new PropertyValueFactory<>("threeDaysAgo"));
        oneWeekAgoColumn.setCellValueFactory(new PropertyValueFactory<>("oneWeekAgo"));
        oneMonthAgoColumn.setCellValueFactory(new PropertyValueFactory<>("oneMonthAgo"));

        JFXChartUtil.setupZooming(chart);

        data.addListener((ListChangeListener<JSONObject>) c -> updateViews());
    }

    // TODO consider refactoring this at least, even though whole class could be better done.
    private void updateViews() {
        Double yMinValue = null, yMaxValue = null;
        Long xMinValue = null, xMaxValue = 1L;

        for (JSONObject jsonObject : data) {
            String name = (String) jsonObject.get("Name");
            XYChart.Series<Long, Double> series = new XYChart.Series<>();
            series.setName(name);

            Double today = null, oneDayAgo = null, threeDaysAgo = null, oneWeekAgo = null, oneMonthAgo = null;
            Map<String, Double> values = (Map<String, Double>) jsonObject.get("Values");

            for (String dateString : values.keySet()) {
                Double value = values.get(dateString);

                // DateAxis gives issues with zooming. Let's just use NumberAxis for now telling how many days there are between points
                Long daysAgo = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(dateString));

                XYChart.Data<Long, Double> singlePoint = new XYChart.Data<>(daysAgo, value);

                // Determine axis limits
                if (yMinValue == null || value < yMinValue) {
                    yMinValue = value;
                }
                if (yMaxValue == null || yMaxValue < value) {
                    yMaxValue = value;
                }
                if (xMinValue == null || daysAgo < xMinValue)
                    xMinValue = daysAgo;

                // Determine if this is one of the special values
                if (daysAgo == 0) {
                    today = value;
                }
                else if (daysAgo == -1) {
                    oneDayAgo = value;
                }
                else if (daysAgo == -3) {
                    threeDaysAgo = value;
                }
                else if (daysAgo == -7) {
                    oneWeekAgo = value;
                }
                else if (daysAgo == -30) {
                    oneMonthAgo = value;
                }

                series.getData().add(singlePoint);
            }

            Fund fund = new Fund(name, today, oneDayAgo, threeDaysAgo, oneWeekAgo, oneMonthAgo);

            table.getItems().add(fund);
            chart.getData().add(series);

            createTooltips();
        }

        setAxisLimits(yMinValue, yMaxValue, xMinValue, xMaxValue);
    }

    private void setAxisLimits(Double yMinValue, Double yMaxValue, Long xMinValue, Long xMaxValue) {
        // By default draw at max last month
        if (xMinValue < -30 ) {
            xMinValue = -30L;
        }

        // Set axis limits
        xAxis.setLowerBound(xMinValue - 1);
        xAxis.setUpperBound(xMaxValue);

        yAxis.setLowerBound(yMinValue - 10);
        yAxis.setUpperBound(yMaxValue + 10);
    }

    private void createTooltips() {
        for (XYChart.Series<Long, Double> s : chart.getData()) {
            for (XYChart.Data<Long, Double> d : s.getData()) {

                // To tooltip add actual date instead of just how far it is from this day.
                LocalDate pointDate = LocalDate.now().plusDays(d.getXValue());
                String pointDateString = String.format("%s.%s.%s", pointDate.getDayOfMonth(), pointDate.getMonthValue(), pointDate.getYear());

                // Show tooltip when hovering over a point.
                Tooltip.install(d.getNode(), new Tooltip(
                        pointDateString + "\n" +
                                s.getName() + "\n" +
                                "Value : " + d.getYValue()));

                // Change style of a point on hover
                d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));

                // Change style of a point back to normal when ending hover
                d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
            }
        }
    }

    void setData(Object data) {
        JSONArray jsonArray = (JSONArray) data;

        this.data.clear();
        this.data.addAll(jsonArray);
    }

    public void quitProgam(ActionEvent actionEvent) {
        System.exit(0);
    }
}
