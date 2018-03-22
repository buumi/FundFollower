package com.buhmen;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;


public class App extends Application
{
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        primaryStage.setTitle("Fund Follower");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);

        fetchData();
    }

    private void fetchData() {
        Task task = new Task<Scene>() {
            protected Scene call() throws Exception {
                updateProgress(0, 100);
                updateMessage("Connecting to database. Please wait..");

                // Read-only credentials and only to database of this application
                final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
                        .withRegion(Regions.EU_CENTRAL_1)
                        .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("AKIAJ55AZ7JSNTLPAQ3A",
                                "VA5EuP7pYmVrm1Ug0CGNZ+x524XijlYugcqNKXFf")))
                        .build();

                updateProgress(10, 100);
                updateMessage("Fetching data. Please wait..");

                ScanRequest scanRequest = new ScanRequest("Rahastot");
                ScanResult scanResult = ddb.scan(scanRequest);
                List<Map<String, AttributeValue>> values = scanResult.getItems();
                Set<DBData> dbDataList = new HashSet<>();

                updateProgress(40, 100);
                updateMessage("Processing data. Please wait..");

                for (Map<String, AttributeValue> map : values) {
                    DBData dbData = dbDataList.stream().filter(dbData1 -> dbData1.getName()
                            .equals(map.get("Nimi").getS()))
                            .findAny()
                            .orElse(new DBData(map.get("Nimi").getS()));
                    dbData.getValues().put(map.get("Paiva").getS(), Double.parseDouble(map.get("Arvo").getN()));
                    dbDataList.add(dbData);
                }

                updateProgress(70, 100);
                updateMessage("Building user interface. Please wait..");

                return constructMainScene(dbDataList);
            }
        };

        showLoadingScreen(task);

        Thread thread = new Thread(task);
        thread.start();

        task.setOnSucceeded(event -> primaryStage.setScene((Scene) task.getValue()));
    }

    private void showLoadingScreen(Task task) {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("/loadscreen.fxml"));
            Label label = (Label) pane.lookup("#loadscreenLabel");
            label.textProperty().bind(task.messageProperty());
            label.setPadding(new Insets(10));
            ProgressIndicator progressIndicator = (ProgressIndicator) pane.lookup("#progressIndicator");
            progressIndicator.progressProperty().bind(task.progressProperty());
            Scene scene = new Scene(pane);

            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (IOException e) {
            System.out.println("FATAL ERROR: Loading scenery failed. Exiting..");
            System.exit(-1);
        }
    }

    private Scene constructMainScene(Set<DBData> dbData) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
            VBox vbox = fxmlLoader.load();
            ((MainViewController) fxmlLoader.getController()).setData(dbData);
            return new Scene(vbox);
        } catch (IOException e) {
            System.err.println("ERROR: Opening main program view failed!");
            e.printStackTrace();

            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);
            Label label = new Label("ERROR: Something went wrong. Unfortunately program will not function correctly");
            vbox.getChildren().add(label);
            return new Scene(vbox);
        }
    }
}
