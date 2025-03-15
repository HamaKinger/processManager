package org.freedom.cleanprocess;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.freedom.cleanprocess.entiy.ProcessInfo;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ProcessApp extends Application {
    @FXML
    private TableView<ProcessInfo> processTableView;
    @Override
    public void start (Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ProcessApp.class
                .getResource("index.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),580,420);
        stage.setTitle("进程清除");
        stage.setScene(scene);
        URL resource = ProcessApp.class.getResource("/logo/logo.png");
        if(resource!=null){
            stage.getIcons().add(new Image(resource.toString()));
        }
        stage.show();
    }

    public static void main (String[] args) {
        launch();
    }
}