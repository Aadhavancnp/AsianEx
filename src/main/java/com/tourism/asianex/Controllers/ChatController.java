package com.tourism.asianex.Controllers;

import com.tourism.asianex.ResourceLoader;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ChatController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(ChatController.class.getName());
    private final MFXIconWrapper plus = new MFXIconWrapper("fas-plus", 27, 60).defaultRippleGeneratorBehavior();
    private final MFXIconWrapper send = new MFXIconWrapper("fas-paper-plane", 27, 60).defaultRippleGeneratorBehavior();

    @FXML
    private MFXIconWrapper plusIcon;

    @FXML
    private MFXIconWrapper sendIcon;

    @FXML
    private Circle profileImage;

    @FXML
    private Circle profileImage1;

    private static void makeRegionCircular(Region region) {
        Circle circle = new Circle();
        circle.radiusProperty().bind(region.widthProperty().divide(2.0));
        circle.centerXProperty().bind(region.widthProperty().divide(2.0));
        circle.centerYProperty().bind(region.heightProperty().divide(2.0));
        try {
            region.setClip(circle);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Could not set region's clip to make it circular", ex);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        makeRegionCircular(plus);
        makeRegionCircular(send);
        plusIcon.setIcon(plus);
        sendIcon.setIcon(send);
        profileImage.setFill(new ImagePattern(new Image(ResourceLoader.getImage("profile.jpg"))));
        profileImage1.setFill(new ImagePattern(new Image(ResourceLoader.getImage("profile1.jpg"))));
    }

}
