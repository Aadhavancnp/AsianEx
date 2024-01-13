package com.tourism.asianex.Controllers;

import com.tourism.asianex.Models.City;
import com.tourism.asianex.Services.ImageLoaderService;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

import static com.tourism.asianex.Utils.Common.*;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        makeRegionCircular(filter);
        makeRegionCircular(clearFilter);
        makeRegionCircular(logout);
        makeRegionCircular(notification);
        filterIcon.setIcon(filter);
        logoutIcon.setIcon(logout);
        notificationIcon.setIcon(notification);

        for (City city : cities) {
            try {
                Pane pane = loadCityPane(city);
                paneCache.putIfAbsent(city.getName(), pane);
            } catch (IOException e) {
                LOGGER.severe("Error loading city pane: " + e.getMessage());
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
        return getPane(city, imageLoaderService);
    }

    @FXML
    private void logout() {
        try {
            shutdownImages();
            screenTransition(rootPane);
        } catch (Exception e) {
            LOGGER.severe("Error logging out: " + e.getMessage());
        }
    }

    public void shutdownImages() {
        imageLoaderService.shutdown();
    }

}
