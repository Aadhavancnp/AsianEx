package com.tourism.asianex.Controllers;

import com.tourism.asianex.Models.City;
import com.tourism.asianex.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

public class CityController {
    @FXML
    private ImageView image;

    @FXML
    private Label name;

    @FXML
    private Label noOfDays;

    @FXML
    private Label price;

    @FXML
    private HBox imagePane;

    @FXML
    public void setCity(City city) {
        Image img = new Image(ResourceLoader.getImage(city.getImage()));
        image.setImage(img);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(imagePane.widthProperty());
        clip.heightProperty().bind(imagePane.heightProperty());

        clip.setArcWidth(20);
        clip.setArcHeight(20);
        imagePane.setClip(clip);

        name.setText(city.getName());
        noOfDays.setText(city.getNoOfDays() + " Days");
        price.setText("$" + city.getPrice());
    }

    @FXML
    public ImageView getImageView() {
        return image;
    }
}
