package com.tourism.asianex;

import com.jthemedetecor.OsThemeDetector;
import com.tourism.asianex.Services.MongoService;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.function.Consumer;

public class MainApplication extends Application {
    // TODO ADD MONGODB URL IN APPLICATION.PROPERTIES FILE IN RESOURCES FOLDER
    private static void setThemeListener(Scene scene) {
        final OsThemeDetector detector = OsThemeDetector.getDetector();
        PropertiesCache cache = PropertiesCache.getInstance();
        Consumer<Boolean> darkThemeListener = isDark -> Platform.runLater(() -> {
            if (isDark) {
                cache.setProperty("theme", "dark");
                scene.getStylesheets().remove(ResourceLoader.getCss("lightMode.css"));
                scene.getStylesheets().add(ResourceLoader.getCss("darkMode.css"));
            } else {
                cache.setProperty("theme", "light");
                scene.getStylesheets().remove(ResourceLoader.getCss("darkMode.css"));
                scene.getStylesheets().add(ResourceLoader.getCss("lightMode.css"));
            }
        });
        darkThemeListener.accept(detector.isDark());
        detector.registerListener(darkThemeListener);
    }

    private static Scene setupStage(Stage stage) throws IOException {
        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA)
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();
        ResourceLoader.loadFonts();
        FXMLLoader fxmlLoader = new FXMLLoader(ResourceLoader.getFxml("splash.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 720, Color.TRANSPARENT);
        scene.getStylesheets().add(ResourceLoader.getCss("mainStyle.css"));
        setThemeListener(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().add(new Image(ResourceLoader.getImage("logo.png")));
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = setupStage(stage);
        stage.setTitle("AsianEx - Asian Tourism Management System");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        MongoService.closeMongoClient();
        PropertiesCache.getInstance().clear();
        System.exit(0);
    }
}