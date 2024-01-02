package com.tourism.asianex;

import javafx.scene.text.Font;

import java.io.InputStream;
import java.net.URL;

public class ResourceLoader {
    private ResourceLoader() {
    }

    public static String getResource(String resourceType, String resourceName) {
        URL path = ResourceLoader.class.getResource(resourceType + "/" + resourceName);
        if (path != null) {
            return path.toExternalForm();
        } else {
            throw new RuntimeException("Could not find " + resourceType + " file: " + resourceName);
        }
    }

    public static String getCss(String cssName) {
        return getResource("Css", cssName);
    }

    public static String getImage(String imageName) {
        return getResource("Images", imageName);
    }

    public static URL getFxml(String fxmlName) {
        URL path = ResourceLoader.class.getResource("Fxml/" + fxmlName);
        if (path != null) {
            return path;
        } else {
            throw new RuntimeException("Could not find fxml file: " + fxmlName);
        }
    }

    public static InputStream getResourceStream(String path) {
        if (path == null) {
            throw new RuntimeException("Could not find resource file");
        }
        return ResourceLoader.class.getResourceAsStream(path);
    }

    public static void loadFonts() {
        loadFont("Fonts/Cosmata/Cosmata-Regular.otf");
        loadFont("Fonts/Cosmata/Cosmata-RegularItalic.otf");
        loadFont("Fonts/Cosmata/Cosmata-Medium.otf");
        loadFont("Fonts/Cosmata/Cosmata-MediumItalic.otf");
        loadFont("Fonts/Cosmata/Cosmata-Bold.otf");
        loadFont("Fonts/Cosmata/Cosmata-BoldItalic.otf");
        loadFont("Fonts/Cosmata/Cosmata-SemiBold.otf");
        loadFont("Fonts/Cosmata/Cosmata-SemiBoldItalic.otf");
        loadFont("Fonts/Cosmata/Cosmata-ExtraBold.otf");
        loadFont("Fonts/Cosmata/Cosmata-ExtraBoldItalic.otf");
    }

    private static void loadFont(String fontName) {
        Font.loadFont(getResourceStream(fontName), 10);
    }

}
