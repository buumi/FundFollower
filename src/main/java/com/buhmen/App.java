package com.buhmen;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;


public class App extends Application
{
    final private String DATA_URL = "https://buhmen.fi/rahastot/rahastot_v2.json";
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Fund Follower");
        showLoadingScreen();
        fetchData();
    }

    private void fetchData() {
        Task task = new Task<Scene>() {
            protected Scene call() throws Exception {
                JSONArray jsonArray = (JSONArray) JSONValue.parse(IOUtils.toString(new URL(DATA_URL), Charset.forName("UTF-8")));
                return constructMainScene(jsonArray);
            }
        };

        Thread thread = new Thread(task);
        thread.start();

        task.setOnSucceeded(event -> primaryStage.setScene((Scene) task.getValue()));
    }

    private Scene constructMainScene(JSONArray jsonArray) {
        Scene scene;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
            VBox vbox = fxmlLoader.load();
            ((MainViewController) fxmlLoader.getController()).setData(jsonArray);
            scene = new Scene(vbox);
        } catch (IOException e) {
            System.err.println("ERROR: Opening main program view failed!");
            e.printStackTrace();

            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);
            Label label = new Label("ERROR: Something went wrong. Unfortunately program will not function correctly");
            vbox.getChildren().add(label);
            scene = new Scene(vbox);
        }

        return scene;
    }

    private void showLoadingScreen() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        Label label = new Label("Fetching data.. Please wait.");
        ProgressIndicator progressIndicator = new ProgressIndicator();
        vbox.getChildren().add(progressIndicator);
        vbox.getChildren().add(label);
        Scene scene = new Scene(vbox);

        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
