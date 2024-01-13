package com.tourism.asianex.Controllers;

import com.tourism.asianex.Models.Role;
import com.tourism.asianex.Models.User;
import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.EnumFilter;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
import io.github.palexdev.materialfx.utils.others.observables.When;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class VisitorsController implements Initializable {
    private final ObservableList<User> users = FXCollections.observableArrayList();
    @FXML
    private MFXPaginatedTableView<User> visitorsTable;

    public VisitorsController(List<User> users) {
        this.users.addAll(users);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpVisitorsTable();
        visitorsTable.autosizeColumnsOnInitialization();
        When.onChanged(visitorsTable.currentPageProperty())
                .then((oldValue, newValue) -> visitorsTable.autosizeColumns())
                .listen();
    }

    private void setUpVisitorsTable() {
        MFXTableColumn<User> idColumn = new MFXTableColumn<>("ID", false, Comparator.comparing(User::getId));
        MFXTableColumn<User> nameColumn = new MFXTableColumn<>("Name", false, Comparator.comparing(User::getName));
        MFXTableColumn<User> emailColumn = new MFXTableColumn<>("Email", false, Comparator.comparing(User::getEmail));
        MFXTableColumn<User> dateOfBirthColumn = new MFXTableColumn<>("Date Of Birth", false, Comparator.comparing(User::getDateOFBirth));
        MFXTableColumn<User> phoneColumn = new MFXTableColumn<>("Phone", false, Comparator.comparing(User::getPhone));
        MFXTableColumn<User> roleColumn = new MFXTableColumn<>("Role", false, Comparator.comparing(User::getRole));

        idColumn.setRowCellFactory(user -> new MFXTableRowCell<>(User::getId));
        nameColumn.setRowCellFactory(user -> new MFXTableRowCell<>(User::getName));
        emailColumn.setRowCellFactory(user -> new MFXTableRowCell<>(User::getEmail));
        dateOfBirthColumn.setRowCellFactory(user -> new MFXTableRowCell<>(User::getDateOFBirth));
        phoneColumn.setRowCellFactory(user -> new MFXTableRowCell<>(User::getPhone));
        roleColumn.setRowCellFactory(user -> new MFXTableRowCell<>(User::getRole));
        visitorsTable.widthProperty().addListener((observable, oldValue, newValue) -> {
            idColumn.setPrefWidth(newValue.doubleValue() / 6);
            nameColumn.setPrefWidth(newValue.doubleValue() / 6);
            emailColumn.setPrefWidth(newValue.doubleValue() / 6);
            dateOfBirthColumn.setPrefWidth(newValue.doubleValue() / 6);
            phoneColumn.setPrefWidth(newValue.doubleValue() / 6);
            roleColumn.setPrefWidth(newValue.doubleValue() / 6);
        });
        setColumnStyle(idColumn);
        setColumnStyle(nameColumn);
        setColumnStyle(emailColumn);
        setColumnStyle(dateOfBirthColumn);
        setColumnStyle(phoneColumn);
        setColumnStyle(roleColumn);

        List<MFXTableColumn<User>> columns = List.of(idColumn, nameColumn, emailColumn, dateOfBirthColumn, phoneColumn, roleColumn);

        visitorsTable.getTableColumns().addAll(columns);

        List<AbstractFilter<User, ?>> filters = List.of(new IntegerFilter<>("ID", User::getId), new StringFilter<>("Name", User::getName), new StringFilter<>("Email", User::getEmail), new StringFilter<>("Phone", User::getPhone), new EnumFilter<>("Role", User::getRole, Role.class));
        visitorsTable.getFilters().addAll(
                filters
        );
        visitorsTable.setItems(users);
    }

    private void setColumnStyle(MFXTableColumn<User> column) {
        column.setStyle("-fx-font-family: 'Cosmata Extra Bold';");
    }
}
