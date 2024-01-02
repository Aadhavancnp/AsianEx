package com.tourism.asianex.Controllers;

import com.mongodb.client.model.UpdateOptions;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.tourism.asianex.Models.Role;
import com.tourism.asianex.PropertiesCache;
import com.tourism.asianex.Services.MongoService;
import com.tourism.asianex.Utils.SubscriberHelpers.OperationSubscriber;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleGroup;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class ProfileController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(ProfileController.class.getName());
    private final MongoCollection<Document> usersCollection;

    private final ToggleGroup toggleGroup;

    @FXML
    private MFXDatePicker dateOfBirthField;

    @FXML
    private MFXTextField emailField;

    @FXML
    private MFXTextField phoneField;

    @FXML
    private MFXTextField usernameField;

    @FXML
    private MFXRadioButton adminRoleRadio;

    @FXML
    private MFXRadioButton userRoleRadio;

    public ProfileController() {
        this.usersCollection = MongoService.getCollection("users");
        this.toggleGroup = new ToggleGroup();
        ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adminRoleRadio.setToggleGroup(toggleGroup);
        userRoleRadio.setToggleGroup(toggleGroup);
        PropertiesCache propertiesCache = PropertiesCache.getInstance();
        String name = propertiesCache.getProperty("name") == null ? "" : propertiesCache.getProperty("name");
        String phone = propertiesCache.getProperty("phone") == null ? "" : propertiesCache.getProperty("phone");
        String date = propertiesCache.getProperty("dateOfBirth") == null ? "" : propertiesCache.getProperty("dateOfBirth");
        LocalDate dateOfBirth = Objects.equals(date, "") ? null : LocalDate.parse(date);
        usernameField.setText(name);
        emailField.setText(propertiesCache.getProperty("email"));
        phoneField.setText(phone);
        if (dateOfBirth != null) {
            dateOfBirthField.setValue(dateOfBirth);
        }
        String role = propertiesCache.getProperty("role");
        if (role.equals("ADMIN")) {
            adminRoleRadio.setSelected(true);
        } else {
            userRoleRadio.setSelected(true);
        }
    }

    @FXML
    void updateProfile(ActionEvent event) {
        PropertiesCache propertiesCache = PropertiesCache.getInstance();
        String name = usernameField.getText();
        String phone = phoneField.getText();
        LocalDate dateOfBirth = dateOfBirthField.getValue();
        String date = dateOfBirth == null ? "" : dateOfBirth.toString();
        MFXRadioButton radioButton = (MFXRadioButton) toggleGroup.getSelectedToggle();
        String roleName = radioButton.getText();
        Role role = Role.valueOf(roleName.toUpperCase());
        ObjectId id = new ObjectId(propertiesCache.getProperty("_id"));
        if (name.isEmpty() || name.isBlank()) {
            name = propertiesCache.getProperty("name") == null ? "" : propertiesCache.getProperty("name");
        }
        if (phone.isEmpty() || phone.isBlank()) {
            phone = propertiesCache.getProperty("phone") == null ? "" : propertiesCache.getProperty("phone");
        }
        if (date.isEmpty() || date.isBlank()) {
            date = "";
        } else {
            date = LocalDate.parse(date).toString();
        }
        UpdateOptions options = new UpdateOptions().upsert(false);
        Bson profile = combine(set("name", name), set("dateOfBirth", date), set("phone", phone), set("role", role));
        usersCollection.updateOne(eq("_id", id), profile, options).subscribe(new OperationSubscriber<>());

        propertiesCache.setProperty("name", name);
        propertiesCache.setProperty("dateOfBirth", date);
        propertiesCache.setProperty("phone", phone);
        propertiesCache.setProperty("role", role.toString());
        LOGGER.info("Profile updated");
    }

}
