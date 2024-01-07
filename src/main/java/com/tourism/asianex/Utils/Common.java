package com.tourism.asianex.Utils;

import com.tourism.asianex.Controllers.CityController;
import com.tourism.asianex.Models.City;
import com.tourism.asianex.ResourceLoader;
import com.tourism.asianex.Services.ImageLoaderService;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Logger;

import static com.tourism.asianex.Services.UtilService.isValidEmail;
import static io.github.palexdev.materialfx.utils.StringUtils.containsAny;

public class Common {
    public static final PseudoClass INVALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("invalid");
    public static final String[] upperChar = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");
    public static final String[] lowerChar = "a b c d e f g h i j k l m n o p q r s t u v w x y z".split(" ");
    public static final String[] digits = "0 1 2 3 4 5 6 7 8 9".split(" ");
    public static final String[] specialCharacters = "! @ # & ( ) – [ { } ]: ; ' , ? / * ~ $ ^ + = < > -".split(" ");
    public static final String DASHBOARD = "dashboard.fxml";
    public static final String LOGIN = "login.fxml";
    public static final String ADMIN = "admin.fxml";
    public static final String EXPLORE = "explore.fxml";
    public static final String SAVED = "saved.fxml";
    public static final String HOME = "home.fxml";
    public static final String DESTINATIONS = "destinations.fxml";
    public static final String VISITORS = "visitors.fxml";
    public static final String CHAT = "chat.fxml";
    public static final String PROFILE = "profile.fxml";
    public static final String SETTINGS = "settings.fxml";
    public static final String ERROR = "Error";
    private static final Logger LOGGER = Logger.getLogger(Common.class.getName());

    public static void initializeToolBar(MFXFontIcon closeIcon, MFXFontIcon minimizeIcon, MFXFontIcon alwaysOnTopIcon, HBox windowHeader) {
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.exit());
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) windowHeader.getScene().getWindow()).setIconified(true));
        alwaysOnTopIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            boolean newVal = !((Stage) windowHeader.getScene().getWindow()).isAlwaysOnTop();
            alwaysOnTopIcon.pseudoClassStateChanged(PseudoClass.getPseudoClass("always-on-top"), newVal);
            ((Stage) windowHeader.getScene().getWindow()).setAlwaysOnTop(newVal);
        });
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());
            return new String(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.severe("Error hashing password" + e.getMessage());
            return null;
        }
    }

    public static void makeRegionCircular(Region region) {
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

    public static Pane getPane(City city, ImageLoaderService imageLoaderService) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResourceLoader.getFxml("city.fxml"));
        Pane pane = fxmlLoader.load();
        CityController cityController = fxmlLoader.getController();
        cityController.setCity(city);
        imageLoaderService.loadImage(ResourceLoader.getImage(city.getImage()), cityController.getImageView());
        return pane;
    }

    public static void screenTransition(StackPane rootPane) throws java.io.IOException {
        Node removeScene = rootPane.getChildren().getFirst();
        Parent nextScene = FXMLLoader.load(ResourceLoader.getFxml(LOGIN));
        rootPane.getChildren().addFirst(nextScene);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(900));
        fadeOut.setOnFinished(t -> rootPane.getChildren().remove(removeScene));
        fadeOut.setNode(removeScene);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.play();
    }

    public static void setFieldValidation(MFXTextField emailField, Label emailvalidationLabel, MFXPasswordField passwordField, Label passwordvalidationLabel) {
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

}
