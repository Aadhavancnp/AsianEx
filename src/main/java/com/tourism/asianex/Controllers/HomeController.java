package com.tourism.asianex.Controllers;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.tourism.asianex.Models.City;
import com.tourism.asianex.PropertiesCache;
import com.tourism.asianex.ResourceLoader;
import com.tourism.asianex.Services.MongoService;
import com.tourism.asianex.Utils.SubscriberHelpers.ObservableSubscriber;
import com.tourism.asianex.Utils.SubscriberHelpers.OperationSubscriber;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import org.bson.Document;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static com.tourism.asianex.Utils.Common.*;

public class HomeController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(HomeController.class.getName());
    private final MongoCollection<Document> citiesCollection;
    private final MongoCollection<Document> savedCitiesCollection;
    private double xOffset;
    private double yOffset;
    private ExploreController exploreController;
    private SavedController savedController;

    @FXML
    private HBox windowHeader;

    @FXML
    private MFXFontIcon closeIcon;

    @FXML
    private MFXFontIcon minimizeIcon;

    @FXML
    private MFXFontIcon alwaysOnTopIcon;

    @FXML
    private Circle profileImage;

    @FXML
    private StackPane contentPane;

    @FXML
    private StackPane rootPane;

    @FXML
    private Label userName;

    public HomeController() {
        this.citiesCollection = MongoService.getCollection("cities");
        this.savedCitiesCollection = MongoService.getCollection("savedCities");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeToolBar(closeIcon, minimizeIcon, alwaysOnTopIcon, windowHeader);
        windowHeader.setOnMousePressed(event -> {
            xOffset = windowHeader.getScene().getWindow().getX() - event.getScreenX();
            yOffset = windowHeader.getScene().getWindow().getY() - event.getScreenY();
        });
        windowHeader.setOnMouseDragged(event -> {
            windowHeader.getScene().getWindow().setX(event.getScreenX() + xOffset);
            windowHeader.getScene().getWindow().setY(event.getScreenY() + yOffset);
        });
        profileImage.setFill(new ImagePattern(new Image(ResourceLoader.getImage("profile.jpg"))));
        initializeLoader();
        setUserInfo();
    }

    private void setUserInfo() {
        PropertiesCache propertiesCache = PropertiesCache.getInstance();
        String name = propertiesCache.getProperty("name");
        if (name == null || name.isEmpty() || name.isBlank()) {
            name = "User";
        } else {
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        userName.setText(name);
    }

    private List<City> getCities() {
        ObservableSubscriber<Document> documentSubscriber = new OperationSubscriber<>();
        citiesCollection.find().subscribe(documentSubscriber);
        return documentSubscriber.get().stream().map(City::fromDocument).toList();
    }

    private List<City> getSavedCities() {
        ObservableSubscriber<Document> documentSubscriber = new OperationSubscriber<>();
        savedCitiesCollection.find().subscribe(documentSubscriber);
        return documentSubscriber.get().stream().map(City::fromDocument).toList();
    }

    private void initializeLoader() {
        loadFxml(EXPLORE);
    }

    private void loadFxml(String fxmlName) {
        shutdownImages();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ResourceLoader.getFxml(fxmlName));
            fxmlLoader.setControllerFactory(c -> switch (fxmlName) {
                case EXPLORE -> {
                    List<City> cities = getCities();
                    exploreController = new ExploreController(rootPane, cities);
                    yield exploreController;
                }
                case SAVED -> {
                    List<City> savedCities = getSavedCities();
                    savedController = new SavedController(savedCities);
                    yield savedController;
                }
                case CHAT -> new ChatController();
                case PROFILE -> new ProfileController();
                case SETTINGS -> new SettingsController(rootPane);
                default -> null;
            });
            Parent fxml = fxmlLoader.load();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            LOGGER.severe("Error loading fxml file: " + e.getMessage());
        }
    }


    @FXML
    private void handleExploreButton(ActionEvent event) {
        loadFxml(EXPLORE);
    }

    @FXML
    private void handleSavedButton(ActionEvent event) {
        loadFxml(SAVED);
    }

    @FXML
    private void handleChatButton(ActionEvent event) {
        loadFxml(CHAT);
    }


    @FXML
    private void handleProfileButton(ActionEvent event) {
        loadFxml(PROFILE);
    }


    @FXML
    private void handleSettingsButton(ActionEvent event) {
        loadFxml(SETTINGS);
    }

    private void shutdownImages() {
        if (exploreController != null) {
            exploreController.shutdownImages();
            exploreController = null;
        }
        if (savedController != null) {
            savedController.shutdownImages();
            savedController = null;
        }
    }

}
