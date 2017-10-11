package com.buhmen;

import javafx.beans.property.SimpleStringProperty;

public class Fund {
    private SimpleStringProperty name, value, oneDayAgo, threeDaysAgo, oneWeekAgo, oneMonthAgo;

    public Fund(String name, Double value, Double oneDayAgo, Double threeDaysAgo, Double oneWeekAgo, Double oneMonthAgo) {
        this.name = new SimpleStringProperty(name);

        if (value != null) {
            this.value = new SimpleStringProperty(String.format("%.2f", value));
        } else {
            this.value = new SimpleStringProperty("N/A");
        }

        this.oneDayAgo = getValue(value, oneDayAgo);
        this.threeDaysAgo = getValue(value, threeDaysAgo);
        this.oneWeekAgo = getValue(value, oneWeekAgo);
        this.oneMonthAgo = getValue(value, oneMonthAgo);
    }

    private SimpleStringProperty getValue(Double currentValue, Double earlierValue) {
        if (currentValue != null && earlierValue != null) {
            return new SimpleStringProperty(String.format("%.2f %%", 100 * (currentValue - earlierValue) / earlierValue));
        }
        else {
            return new SimpleStringProperty("N/A");
        }
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getValue() {
        return value.get();
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public String getOneDayAgo() {
        return oneDayAgo.get();
    }

    public SimpleStringProperty oneDayAgoProperty() {
        return oneDayAgo;
    }

    public String getThreeDaysAgo() {
        return threeDaysAgo.get();
    }

    public SimpleStringProperty threeDaysAgoProperty() {
        return threeDaysAgo;
    }

    public String getOneWeekAgo() {
        return oneWeekAgo.get();
    }

    public SimpleStringProperty oneWeekAgoProperty() {
        return oneWeekAgo;
    }

    public String getOneMonthAgo() {
        return oneMonthAgo.get();
    }

    public SimpleStringProperty oneMonthAgoProperty() {
        return oneMonthAgo;
    }
}
