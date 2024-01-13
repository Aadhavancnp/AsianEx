package com.tourism.asianex.Controllers;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.tourism.asianex.Models.City;
import com.tourism.asianex.Services.MongoService;
import com.tourism.asianex.Utils.Common;
import com.tourism.asianex.Utils.SubscriberHelpers.OperationSubscriber;
import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
import io.github.palexdev.materialfx.utils.others.observables.When;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.bson.Document;

import java.net.URL;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.tourism.asianex.Services.UtilService.errorBox;

public class DestinationsController implements Initializable {
    private final MongoCollection<Document> usersCollection;
    private final ObservableList<City> cities = FXCollections.observableArrayList();
    @FXML
    private MFXPaginatedTableView<City> destinationsTable;
    @FXML
    private MFXTextField countryField;

    @FXML
    private MFXTextField descriptionField;

    @FXML
    private MFXTextField nameField;

    @FXML
    private MFXTextField noOfDaysField;

    @FXML
    private MFXTextField priceField;

    @FXML
    private MFXTextField searchField;

    @FXML
    private MFXTextField imageField;


    public DestinationsController(List<City> cities) {
        this.cities.addAll(cities);
        this.usersCollection = MongoService.getCollection("cities");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDestinationsTable();
        destinationsTable.autosizeColumnsOnInitialization();
        When.onChanged(destinationsTable.currentPageProperty())
                .then((oldValue, newValue) -> destinationsTable.autosizeColumns())
                .listen();
        FilteredList<City> filteredCities = new FilteredList<>(cities, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredCities.setPredicate(city ->
                        city.getName().toLowerCase().contains(newValue.toLowerCase()) ||
                                city.getCountry().toLowerCase().contains(newValue.toLowerCase()) ||
                                String.valueOf(city.getNoOfDays()).contains(newValue) ||
                                String.valueOf(city.getPrice()).contains(newValue) ||
                                city.getDescription().toLowerCase().contains(newValue.toLowerCase())
                ));
        onSelectedCity();
    }

    private void setupDestinationsTable() {
        MFXTableColumn<City> nameColumn = new MFXTableColumn<>("Name", false, Comparator.comparing(City::getName));
        MFXTableColumn<City> countryColumn = new MFXTableColumn<>("Country", false, Comparator.comparing(City::getCountry));
        MFXTableColumn<City> descriptionColumn = new MFXTableColumn<>("Description", false, Comparator.comparing(City::getDescription));
        MFXTableColumn<City> priceColumn = new MFXTableColumn<>("Price ($)", false, Comparator.comparing(City::getPrice));
        MFXTableColumn<City> noOfDaysColumn = new MFXTableColumn<>("No of Days", false, Comparator.comparing(City::getNoOfDays));

        nameColumn.setRowCellFactory(city -> new MFXTableRowCell<>(City::getName));
        countryColumn.setRowCellFactory(city -> new MFXTableRowCell<>(City::getCountry));
        descriptionColumn.setRowCellFactory(city -> new MFXTableRowCell<>(City::getDescription) {{
            if (city.getDescription().length() > 10)
                city.setDescription(city.getDescription().substring(0, 10) + "...");
        }});
        descriptionColumn.setWrapText(true);
        priceColumn.setRowCellFactory(city -> new MFXTableRowCell<>(City::getPrice));
        noOfDaysColumn.setRowCellFactory(city -> new MFXTableRowCell<>(City::getNoOfDays));

        setColumnStyle(nameColumn);
        setColumnStyle(countryColumn);
        setColumnStyle(descriptionColumn);
        setColumnStyle(priceColumn);
        setColumnStyle(noOfDaysColumn);

        List<MFXTableColumn<City>> columns = List.of(nameColumn, countryColumn, descriptionColumn, priceColumn, noOfDaysColumn);

        destinationsTable.getTableColumns().addAll(columns);
        List<AbstractFilter<City, ?>> filters = new ArrayList<>();
        filters.add(new StringFilter<>("Name", City::getName));
        filters.add(new StringFilter<>("Country", City::getCountry));
        filters.add(new IntegerFilter<>("Price", City::getPrice));
        filters.add(new IntegerFilter<>("No of Days", City::getNoOfDays));
        destinationsTable.getFilters().addAll(filters);
        destinationsTable.setItems(cities);
    }


    private void setColumnStyle(MFXTableColumn<City> column) {
        column.setStyle("-fx-font-family: 'Cosmata Extra Bold';");
    }

    private void onSelectedCity() {
        destinationsTable.getSelectionModel().selectionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                City city = destinationsTable.getSelectionModel().getSelectedValue();
                nameField.setText(city.getName());
                countryField.setText(city.getCountry());
                descriptionField.setText(city.getDescription());
                priceField.setText(String.valueOf(city.getPrice()));
                noOfDaysField.setText(String.valueOf(city.getNoOfDays()));
                imageField.setText(city.getImage());
            }
        });
    }

    @FXML
    void addCity(ActionEvent event) {
        String name = nameField.getText();
        String country = countryField.getText();
        String description = descriptionField.getText();
        int price = Integer.parseInt(Objects.equals(priceField.getText(), "") ? "0" : priceField.getText());
        int noOfDays = Integer.parseInt(Objects.equals(noOfDaysField.getText(), "") ? "0" : noOfDaysField.getText());
        String image = imageField.getText();
        if (name.isEmpty() || country.isEmpty() || description.isEmpty() || image.isEmpty()) {
            errorBox("Please fill all the fields", Common.ERROR, Common.ERROR);
            return;
        }
        City city = new City(name, country, description, image, price, noOfDays);
        cities.add(city);
        Document document = new Document("name", name)
                .append("country", country)
                .append("description", description)
                .append("image", image)
                .append("price", price)
                .append("noOfDays", noOfDays);
        usersCollection.insertOne(document).subscribe(new OperationSubscriber<>());
        clearFields(event);
    }

    @FXML
    void clearFields(ActionEvent event) {
        nameField.clear();
        countryField.clear();
        descriptionField.clear();
        priceField.clear();
        noOfDaysField.clear();
        imageField.clear();
    }

    @FXML
    void deleteCity(ActionEvent event) {
        City city = destinationsTable.getSelectionModel().getSelectedValue();
        if (city == null) {
            errorBox("Please select a city to delete", Common.ERROR, Common.ERROR);
            return;
        }
        cities.remove(city);
        usersCollection.deleteOne(eq("name", city.getName())).subscribe(new OperationSubscriber<>());
    }

    @FXML
    void updateCity(ActionEvent event) {
        City city = destinationsTable.getSelectionModel().getSelectedValue();
        if (city == null) {
            errorBox("Please select a city to update", Common.ERROR, Common.ERROR);
            return;
        }
        String name = nameField.getText();
        String country = countryField.getText();
        String description = descriptionField.getText();
        int price = Integer.parseInt(priceField.getText());
        int noOfDays = Integer.parseInt(noOfDaysField.getText());
        String image = imageField.getText();
        if (name.isEmpty() || country.isEmpty() || description.isEmpty() || image.isEmpty()) {
            errorBox("Please fill all the fields", Common.ERROR, Common.ERROR);
            return;
        }
        city.setName(name);
        city.setCountry(country);
        city.setDescription(description);
        city.setPrice(price);
        city.setNoOfDays(noOfDays);
        city.setImage(image);
        Document document = new Document("name", name)
                .append("country", country)
                .append("description", description)
                .append("image", image)
                .append("price", price)
                .append("noOfDays", noOfDays);
        usersCollection.replaceOne(eq("name", city.getName()), document).subscribe(new OperationSubscriber<>());
        int index = cities.indexOf(city);
        if (index >= 0 && index < cities.size()) {
            cities.set(index, city);
        }
        clearFields(event);
    }


}
