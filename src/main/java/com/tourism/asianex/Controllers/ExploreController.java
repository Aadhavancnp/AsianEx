package com.tourism.asianex.Controllers;

import com.tourism.asianex.Models.City;
import com.tourism.asianex.ResourceLoader;
import com.tourism.asianex.Services.ImageLoaderService;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class ExploreController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(ExploreController.class.getName());
    private final ImageLoaderService imageLoaderService = new ImageLoaderService(10);
    private final Map<String, Pane> paneCache = new HashMap<>();
    private final ObservableList<City> cities = FXCollections.observableArrayList();
    private final MFXIconWrapper filter = new MFXIconWrapper("fas-filter", 32, 60).defaultRippleGeneratorBehavior();
    private final MFXIconWrapper clearFilter = new MFXIconWrapper("fas-filter-circle-xmark", 31, 60).defaultRippleGeneratorBehavior();
    private final MFXIconWrapper logout = new MFXIconWrapper("fas-right-from-bracket", 32, 60).defaultRippleGeneratorBehavior();
    private final MFXIconWrapper notification = new MFXIconWrapper("fas-bell", 32, 60).defaultRippleGeneratorBehavior();
    private final StackPane rootPane;
    private boolean isAscending = true;
    @FXML
    private MFXTextField searchField;

    @FXML
    private GridPane citiesGrid;

    @FXML
    private MFXScrollPane scrollPane;

    @FXML
    private MFXIconWrapper filterIcon;

    @FXML
    private MFXIconWrapper notificationIcon;

    @FXML
    private MFXIconWrapper logoutIcon;

    public ExploreController(StackPane rootPane, List<City> cities) {
        this.rootPane = rootPane;
        this.cities.addAll(cities);
    }

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
        makeRegionCircular(filter);
        makeRegionCircular(clearFilter);
        makeRegionCircular(logout);
        makeRegionCircular(notification);
        filterIcon.setIcon(filter);
        logoutIcon.setIcon(logout);
        notificationIcon.setIcon(notification);

//        cities.addAll(City.getCities());

        for (City city : cities) {
            try {
                Pane pane = loadCityPane(city);
                paneCache.putIfAbsent(city.getName(), pane);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        FilteredList<City> filteredCities = new FilteredList<>(cities, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredCities.setPredicate(city ->
                    city.getName().toLowerCase().contains(newValue.toLowerCase()) ||
                            city.getCountry().toLowerCase().contains(newValue.toLowerCase()) ||
                            String.valueOf(city.getNoOfDays()).contains(newValue) ||
                            String.valueOf(city.getPrice()).contains(newValue)
            );
            updateCitiesGrid(filteredCities);
        });

        populateCitiesGrid();
        ScrollUtils.addSmoothScrolling(scrollPane);
    }

    @FXML
    private void populateCitiesGrid() {
        updateCitiesGrid(cities);
    }

    @FXML
    private void updateCitiesGrid(List<City> cities) {
        citiesGrid.getChildren().clear();
        int column = 0;
        int row = 1;
        for (City city : cities) {
            Pane pane = paneCache.get(city.getName());
            if (pane != null) {
                if (column == 2) {
                    column = 0;
                    row++;
                }
                citiesGrid.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(20));
            }
        }
    }

    @FXML
    private void handleFilterButton(MouseEvent event) {
        List<City> sortedCities = new ArrayList<>(cities);
        if (isAscending) {
            filterIcon.setIcon(clearFilter);
            sortedCities.sort(Comparator.comparing(City::getName));
        } else {
            filterIcon.setIcon(filter);
            sortedCities.sort(Comparator.comparing(City::getName).reversed());
        }
        isAscending = !isAscending;
        updateCitiesGrid(sortedCities);
    }

    private Pane loadCityPane(City city) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResourceLoader.getFxml("city.fxml"));
        Pane pane = fxmlLoader.load();
        CityController cityController = fxmlLoader.getController();
        cityController.setCity(city);
        imageLoaderService.loadImage(ResourceLoader.getImage(city.getImage()), cityController.getImageView());
        return pane;
    }

    @FXML
    private void logout() {
        try {
            shutdownImages();
            Node removeScene = rootPane.getChildren().getFirst();
            Parent nextScene = FXMLLoader.load(ResourceLoader.getFxml("login.fxml"));
            rootPane.getChildren().addFirst(nextScene);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(900));
            fadeOut.setOnFinished(t -> rootPane.getChildren().remove(removeScene));
            fadeOut.setNode(removeScene);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.play();
        } catch (Exception e) {
            LOGGER.severe("Error logging out: " + e.getMessage());
        }
    }

    public void shutdownImages() {
        imageLoaderService.shutdown();
    }

}
