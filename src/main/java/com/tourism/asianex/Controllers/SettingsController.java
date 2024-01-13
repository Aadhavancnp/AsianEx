package com.tourism.asianex.Controllers;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.tourism.asianex.PropertiesCache;
import com.tourism.asianex.ResourceLoader;
import com.tourism.asianex.Services.MongoService;
import com.tourism.asianex.Utils.SubscriberHelpers.OperationSubscriber;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;
import static com.tourism.asianex.Utils.Common.screenTransition;

public class SettingsController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(SettingsController.class.getName());
    private final ToggleGroup toggleGroup;
    private final MongoCollection<Document> usersCollection;
    private final StackPane rootPane;


    @FXML
    private MFXRadioButton darkModeRadio;

    @FXML
    private MFXRadioButton lightModeRadio;

    @FXML
    private Pane paneBox;

    private MFXGenericDialog dialogContent;
    private MFXStageDialog dialog;

    public SettingsController(StackPane rootPane) {
        this.rootPane = rootPane;
        this.toggleGroup = new ToggleGroup();
        ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);
        this.usersCollection = MongoService.getCollection("users");
        Stage stage = (Stage) rootPane.getScene().getWindow();
        Platform.runLater(() -> {
            this.dialogContent = MFXGenericDialogBuilder.build()
                    .makeScrollable(false)
                    .get();
            this.dialog = MFXGenericDialogBuilder.build(dialogContent)
                    .toStageDialogBuilder()
                    .initOwner(stage)
                    .initModality(Modality.APPLICATION_MODAL)
                    .setDraggable(false)
                    .setOwnerNode(paneBox)
                    .setScrimPriority(ScrimPriority.WINDOW)
                    .setScrimOwner(true)
                    .get();

            dialogContent.addActions(
                    Map.entry(new MFXButton("Confirm"), event -> deleteAccount()),
                    Map.entry(new MFXButton("Cancel"), event -> dialog.close())
            );

            dialogContent.setMaxSize(400, 200);
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        darkModeRadio.setToggleGroup(toggleGroup);
        lightModeRadio.setToggleGroup(toggleGroup);
        PropertiesCache propertiesCache = PropertiesCache.getInstance();
        String theme = propertiesCache.getProperty("theme") == null ? "" : propertiesCache.getProperty("theme");
        if (theme.equalsIgnoreCase("dark")) {
            darkModeRadio.setSelected(true);
        } else {
            lightModeRadio.setSelected(true);
        }
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                oldValue.setSelected(true);
            } else {
                String themeMode = ((MFXRadioButton) newValue).getText().toLowerCase();
                propertiesCache.setProperty("theme", themeMode);
                Scene scene = rootPane.getScene().getWindow().getScene();
                if (themeMode.equalsIgnoreCase("dark")) {
                    scene.getStylesheets().add(ResourceLoader.getCss("darkMode.css"));
                    scene.getStylesheets().remove(ResourceLoader.getCss("lightMode.css"));
                } else {
                    scene.getStylesheets().add(ResourceLoader.getCss("lightMode.css"));
                    scene.getStylesheets().remove(ResourceLoader.getCss("darkMode.css"));
                }
            }
        });
    }

    @FXML
    void deleteAccountButton(ActionEvent event) {
        MFXFontIcon warnIcon = new MFXFontIcon("fas-circle-exclamation", 18);
        dialogContent.setHeaderIcon(warnIcon);
        dialogContent.setHeaderText("Account Deletion");
        dialogContent.getStyleClass().add("mfx-warn-dialog");
        dialogContent.setContentText("Are you sure you want to delete your account?");
        dialog.showDialog();
    }

    @FXML
    private void deleteAccount() {
        dialog.close();
        PropertiesCache propertiesCache = PropertiesCache.getInstance();
        ObjectId id = new ObjectId(propertiesCache.getProperty("_id"));
        usersCollection.deleteOne(eq("_id", id)).subscribe(new OperationSubscriber<>());
        logoutScreen();
    }

    @FXML
    void logout(ActionEvent event) {
        logoutScreen();
    }

    @FXML
    private void logoutScreen() {
        try {
            screenTransition(rootPane);
        } catch (Exception e) {
            LOGGER.severe("Error logging out: " + e.getMessage());
        }
    }

}
