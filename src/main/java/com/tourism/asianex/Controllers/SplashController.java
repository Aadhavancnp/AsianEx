package com.tourism.asianex.Controllers;

import com.tourism.asianex.ResourceLoader;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.tourism.asianex.Services.UtilService.delay;

public class SplashController implements Initializable {
    @FXML
    private StackPane rootPane;

    @FXML
    private BorderPane container;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        delay(3000, this::onSplashEnd);
    }

    private void onSplashEnd() {
        Parent nextScene;
        try {
            nextScene = FXMLLoader.load(ResourceLoader.getFxml("login.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        rootPane.getChildren().addFirst(nextScene);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(900));
        fadeOut.setOnFinished(t -> rootPane.getChildren().remove(container));
        fadeOut.setNode(container);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.play();
    }
}
