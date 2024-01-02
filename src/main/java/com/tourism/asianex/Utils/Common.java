package com.tourism.asianex.Utils;

import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class Common {
    public static final PseudoClass INVALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("invalid");
    public static final String[] upperChar = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");
    public static final String[] lowerChar = "a b c d e f g h i j k l m n o p q r s t u v w x y z".split(" ");
    public static final String[] digits = "0 1 2 3 4 5 6 7 8 9".split(" ");
    public static final String[] specialCharacters = "! @ # & ( ) – [ { } ]: ; ' , ? / * ~ $ ^ + = < > -".split(" ");
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

}
