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

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.tourism.asianex.Services.UtilService.errorBox;
import static com.tourism.asianex.Services.UtilService.isValidEmail;
import static com.tourism.asianex.Utils.Common.*;
import static io.github.palexdev.materialfx.utils.StringUtils.containsAny;


public class LoginController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
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
    private VBox loginPane;

    @FXML
    private MFXTextField emailField;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    private Label emailvalidationLabel;

    @FXML
    private Label passwordvalidationLabel;

    public LoginController() {
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
        clip.widthProperty().bind(loginPane.widthProperty());
        clip.heightProperty().bind(loginPane.heightProperty());

        clip.setArcWidth(20);
        clip.setArcHeight(20);
        loginPane.setClip(clip);

        setFieldValidation();

    }

    private void setFieldValidation() {
        Constraint emailConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Email is not valid")
                .setCondition(Bindings.createBooleanBinding(
                        () -> isValidEmail(emailField.getText()),
                        emailField.textProperty()
                ))
                .get();

        emailField.getValidator().constraint(emailConstraint);

        emailField.getValidator().validProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                emailvalidationLabel.setVisible(false);
                emailField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
            }
        });

        emailField.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                List<Constraint> constraints = emailField.validate();
                if (!constraints.isEmpty()) {
                    emailField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
                    emailvalidationLabel.setText(constraints.getFirst().getMessage());
                    emailvalidationLabel.setVisible(true);
                }
            }
        });

        Constraint lengthConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Password must be at least 8 characters long")
                .setCondition(passwordField.textProperty().length().greaterThanOrEqualTo(8))
                .get();

        Constraint digitConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Password must contain at least one digit")
                .setCondition(Bindings.createBooleanBinding(
                        () -> containsAny(passwordField.getText(), "", digits),
                        passwordField.textProperty()
                ))
                .get();

        Constraint charactersConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Password must contain at least one lowercase and one uppercase characters")
                .setCondition(Bindings.createBooleanBinding(
                        () -> containsAny(passwordField.getText(), "", upperChar) && containsAny(passwordField.getText(), "", lowerChar),
                        passwordField.textProperty()
                ))
                .get();

        Constraint specialCharactersConstraint = Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage("Password must contain at least one special character")
                .setCondition(Bindings.createBooleanBinding(
                        () -> containsAny(passwordField.getText(), "", specialCharacters),
                        passwordField.textProperty()
                ))
                .get();

        passwordField.getValidator()
                .constraint(digitConstraint)
                .constraint(charactersConstraint)
                .constraint(specialCharactersConstraint)
                .constraint(lengthConstraint);

        passwordField.getValidator().validProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                passwordvalidationLabel.setVisible(false);
                passwordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
            }
        });

        passwordField.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                List<Constraint> constraints = passwordField.validate();
                if (!constraints.isEmpty()) {
                    passwordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
                    passwordvalidationLabel.setText(constraints.getFirst().getMessage());
                    passwordvalidationLabel.setVisible(true);
                }
            }
        });

    }

    @FXML
    private void register() throws IOException {
        Parent nextScene = FXMLLoader.load(ResourceLoader.getFxml("register.fxml"));
        Scene scene = container.getScene();
        nextScene.translateXProperty().set(scene.getWidth());
        rootPane.getChildren().add(nextScene);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(nextScene.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.millis(900), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(event -> rootPane.getChildren().remove(container));
        timeline.play();
    }

    @FXML
    private void login() {
        if (emailField.validate().isEmpty() && passwordField.validate().isEmpty()) {
            String email = emailField.getText();
            String password = passwordField.getText();
            ObservableSubscriber<Document> subscriber = new OperationSubscriber<>();
            if (!isEmailExists(email, password, subscriber)) {
                errorBox("The Password you entered does not match the Email", "Login failed", "Login failed");
                return;
            }
            Document user = subscriber.first();
            Role role = Role.valueOf(user.getString("role"));
            user.forEach((key, value) -> PropertiesCache.getInstance().setProperty(key, value.toString()));
            if (role == Role.ADMIN) {
                setAdminPage();
            } else {
                setUserPage();
            }
        } else {
            errorBox("Please check your email and password again", "Login failed", "Login failed");
        }
    }

    private void setAdminPage() {
        try {
            setNextPage("admin.fxml");
            LOGGER.info("Admin Logged in Successfully");
        } catch (IOException e) {
            LOGGER.severe("Error loading fxml file: " + e.getMessage());
        }
    }

    private void setUserPage() {
        try {
            setNextPage("home.fxml");
            LOGGER.info("User Logged in Successfully");
        } catch (IOException e) {
            LOGGER.severe("Error loading fxml file: " + e.getMessage());
        }
    }

    private void setNextPage(String name) throws IOException {
        Parent nextScene = FXMLLoader.load(ResourceLoader.getFxml(name));
        rootPane.getChildren().add(nextScene);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(900));
        fadeIn.setOnFinished(t -> rootPane.getChildren().remove(container));
        fadeIn.setNode(nextScene);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }


    private boolean isEmailExists(String email, String password, ObservableSubscriber<Document> subscriber) {
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            return false;
        }
        collection.find(and(eq("email", email), eq("password", hashedPassword))).first().subscribe(subscriber);
        return subscriber.first() != null;
    }
}