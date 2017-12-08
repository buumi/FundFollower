package com.buhmen;

import javafx.beans.property.SimpleStringProperty;

public class Fund {
    private SimpleStringProperty name, currentValue, oneDayAgo, threeDaysAgo, oneWeekAgo, oneMonthAgo, changeFromBeginning, firstDate;

    public Fund(String name, Double value, Double oneDayAgo, Double threeDaysAgo, Double oneWeekAgo, Double oneMonthAgo, Double startValue, String firstDateString) {
        this.name = new SimpleStringProperty(name);

        if (value != null) {
            this.currentValue = new SimpleStringProperty(String.format("%.2f", value));
        } else {
            this.currentValue = new SimpleStringProperty("N/A");
        }

        if (firstDateString != null) {
            this.firstDate = new SimpleStringProperty(firstDateString);
        }

        this.oneDayAgo = getValue(value, oneDayAgo);
        this.threeDaysAgo = getValue(value, threeDaysAgo);
        this.oneWeekAgo = getValue(value, oneWeekAgo);
        this.oneMonthAgo = getValue(value, oneMonthAgo);
        this.changeFromBeginning = getValue(value, startValue);
    }

    private SimpleStringProperty getValue(Double currentValue, Double earlierValue) {
        if (currentValue != null && earlierValue != null) {
            return new SimpleStringProperty(String.format("%.2f %%", 100 * (currentValue - earlierValue) / earlierValue));
        }
        else {
            return new SimpleStringProperty("N/A");
        }
    }

    public String getFirstDate() {
        return firstDate.get();
    }

    public SimpleStringProperty firstDateProperty() {
        return firstDate;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
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

    public String getCurrentValue() {
        return currentValue.get();
    }

    public SimpleStringProperty currentValueProperty() {
        return currentValue;
    }

    public String getChangeFromBeginning() {
        return changeFromBeginning.get();
    }

    public SimpleStringProperty changeFromBeginningProperty() {
        return changeFromBeginning;
    }
}
