package com.tourism.asianex.Controllers;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.tourism.asianex.Models.City;
import com.tourism.asianex.Models.User;
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

public class AdminController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(AdminController.class.getName());
    private final MongoCollection<Document> citiesCollection;
    private final MongoCollection<Document> usersCollection;
    private double xOffset;
    private double yOffset;
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

    public AdminController() {
        this.citiesCollection = MongoService.getCollection("cities");
        this.usersCollection = MongoService.getCollection("users");
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
            name = "Admin";
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

    private List<User> getUsers() {
        ObservableSubscriber<Document> documentSubscriber = new OperationSubscriber<>();
        usersCollection.find().subscribe(documentSubscriber);
        return User.fromDocuments(documentSubscriber.get());
    }

    private void initializeLoader() {
        loadFxml(DASHBOARD);
    }

    private void loadFxml(String fxmlName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ResourceLoader.getFxml(fxmlName));
            fxmlLoader.setControllerFactory(c -> switch (fxmlName) {
                case DASHBOARD -> new DashboardController();
                case DESTINATIONS -> new DestinationsController(getCities());
                case VISITORS -> new VisitorsController(getUsers());
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
    private void handleDashboardButton(ActionEvent event) {
        loadFxml(DASHBOARD);
    }

    @FXML
    private void handleDestinationsButton(ActionEvent event) {
        loadFxml(DESTINATIONS);
    }

    @FXML
    private void handleVisitorsButton(ActionEvent event) {
        loadFxml(VISITORS);
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
}
