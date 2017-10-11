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
    final private String DATA_URL = "https://buhmen.com/~buumi/rahastot_v2.json";
    private Stage primaryStage;

    public static void main(String[] args ) throws IOException {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Fund Follower");
        showLoadingScreen();
        fetchData();
    }

    private void fetchData() {
        Task task = new Task<JSONArray>() {
            protected JSONArray call() throws Exception {
                return (JSONArray) JSONValue.parse(IOUtils.toString(new URL(DATA_URL), Charset.forName("UTF-8")));
            }
        };

        Thread thread = new Thread(task);
        thread.start();

        task.setOnSucceeded(event -> showMainScreen(task));
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

    private void showMainScreen(Task task) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
            VBox vbox = fxmlLoader.load();
            ((MainViewController) fxmlLoader.getController()).setData(task.getValue());
            Scene scene = new Scene(vbox);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            System.err.println("ERROR: Opening main program view failed!");
            e.printStackTrace();
        }
    }
}
