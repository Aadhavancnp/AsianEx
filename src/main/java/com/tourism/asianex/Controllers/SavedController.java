package com.tourism.asianex.Controllers;

import com.tourism.asianex.Models.City;
import com.tourism.asianex.Services.ImageLoaderService;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static com.tourism.asianex.Utils.Common.getPane;

public class SavedController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(SavedController.class.getName());
    private final ObservableList<City> cities = FXCollections.observableArrayList();
    private final ImageLoaderService imageLoaderService = new ImageLoaderService(10);

    private final Map<String, Pane> paneCache = new HashMap<>();
    @FXML
    private MFXScrollPane scrollPane;

    @FXML
    private GridPane savedCitiesGrid;

    public SavedController(List<City> savedCities) {
        cities.addAll(savedCities);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        cities.addAll(City.getCities().getFirst(), City.getCities().get(1), City.getCities().getLast());
        for (City city : cities) {
            try {
                Pane pane = loadCityPane(city);
                paneCache.putIfAbsent(city.getName(), pane);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        updateSavedCitiesGrid(cities);
        ScrollUtils.addSmoothScrolling(scrollPane);
    }

    @FXML
    private void updateSavedCitiesGrid(List<City> cities) {
        savedCitiesGrid.getChildren().clear();
        int column = 0;
        int row = 1;
        for (City city : cities) {
            Pane pane = paneCache.get(city.getName());
            if (pane != null) {
                if (column == 2) {
                    column = 0;
                    row++;
                }
                savedCitiesGrid.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(15));
            }
        }
    }

    private Pane loadCityPane(City city) throws IOException {
        return getPane(city, imageLoaderService);
    }

    public void shutdownImages() {
        imageLoaderService.shutdown();
    }

}
