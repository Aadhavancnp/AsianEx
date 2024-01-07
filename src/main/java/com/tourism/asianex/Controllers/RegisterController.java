package com.tourism.asianex.Controllers;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.tourism.asianex.Models.Role;
import com.tourism.asianex.PropertiesCache;
import com.tourism.asianex.ResourceLoader;
import com.tourism.asianex.Services.MongoService;
import com.tourism.asianex.Utils.SubscriberHelpers.ObservableSubscriber;
import com.tourism.asianex.Utils.SubscriberHelpers.OperationSubscriber;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.bson.Document;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;
import static com.tourism.asianex.Services.UtilService.errorBox;
import static com.tourism.asianex.Utils.Common.*;

public class RegisterController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(RegisterController.class.getName());
    private final MongoCollection<Document> collection;
    private double xOffset;
    private double yOffset;
    @FXML
    private StackPane rootPane;

    @FXML
    private AnchorPane container;

    @FXML
    private HBox windowHeader;

    @FXML
    private MFXFontIcon closeIcon;

    @FXML
    private MFXFontIcon minimizeIcon;

    @FXML
    private MFXFontIcon alwaysOnTopIcon;

    @FXML
    private VBox registerPane;

    @FXML
    private MFXTextField emailField;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    private MFXPasswordField confirmPasswordField;

    @FXML
    private Label emailvalidationLabel;

    @FXML
    private Label passwordvalidationLabel;

    @FXML
    private Label confirmPasswordvalidationLabel;

    public RegisterController() {
        this.collection = MongoService.getCollection("users");
    }

    @FXML
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
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(registerPane.widthProperty());
        clip.heightProperty().bind(registerPane.heightProperty());

        clip.setArcWidth(20);
        clip.setArcHeight(20);
        registerPane.setClip(clip);

        setFieldValidationWithConfirmPassword();

    }

    private void setFieldValidationWithConfirmPassword() {
        setFieldValidation(emailField, emailvalidationLabel, passwordField, passwordvalidationLabel);

        Constraint confirmPasswordConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Password does not match")
                .setCondition(Bindings.createBooleanBinding(
                        () -> confirmPasswordField.getText().equals(passwordField.getText()),
                        confirmPasswordField.textProperty()
                ))
                .get();

        confirmPasswordField.getValidator().constraint(confirmPasswordConstraint);

        confirmPasswordField.getValidator().validProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                confirmPasswordvalidationLabel.setVisible(false);
                confirmPasswordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
            }
        });

        confirmPasswordField.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                List<Constraint> constraints = confirmPasswordField.validate();
                if (!constraints.isEmpty()) {
                    confirmPasswordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
                    confirmPasswordvalidationLabel.setText(constraints.getFirst().getMessage());
                    confirmPasswordvalidationLabel.setVisible(true);
                }
            }
        });
    }

    @FXML
    private void login() throws IOException {
        Parent nextScene = FXMLLoader.load(ResourceLoader.getFxml(LOGIN));
        Scene scene = container.getScene();
        nextScene.translateXProperty().set(-1 * scene.getWidth());
        rootPane.getChildren().add(nextScene);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(nextScene.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.millis(900), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(event -> rootPane.getChildren().remove(container));
        timeline.play();
    }

    @FXML
    private void register() throws IOException {
        if (emailField.validate().isEmpty() && passwordField.validate().isEmpty() && confirmPasswordField.validate().isEmpty()) {
            String email = emailField.getText();
            String password = passwordField.getText();
            if (isEmailExists(email)) {
                errorBox("Email already exists", "Registration Failed", "Error");
                return;
            }
            String hashedPassword = hashPassword(password);
            if (hashedPassword == null) {
                return;
            }
            Document user = new Document("name", "")
                    .append("email", email)
                    .append("password", hashedPassword)
                    .append("role", Role.USER);
            collection.insertOne(user).subscribe(new OperationSubscriber<>());
            user.forEach((key, value) -> PropertiesCache.getInstance().setProperty(key, value.toString()));
            LOGGER.info("User registered successfully");

            Parent nextScene = FXMLLoader.load(ResourceLoader.getFxml("home.fxml"));
            rootPane.getChildren().add(nextScene);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(900));
            fadeIn.setOnFinished(t -> rootPane.getChildren().remove(container));
            fadeIn.setNode(nextScene);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        } else {
            errorBox("Please fill all the fields correctly", "Registration Failed", "Error");
        }
    }


    private boolean isEmailExists(String email) {
        ObservableSubscriber<Document> subscriber = new OperationSubscriber<>();
        collection.find(eq("email", email)).first().subscribe(subscriber);
        return subscriber.first() != null;
    }
}
