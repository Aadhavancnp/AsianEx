package com.tourism.asianex.Services;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ImageLoaderService {
    private static final Logger LOGGER = Logger.getLogger(ImageLoaderService.class.getName());

    private final ExecutorService executorService;

    private final Map<String, Image> imageCache = new ConcurrentHashMap<>();


    public ImageLoaderService(int threadPoolSize) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void loadImage(String imageUrl, ImageView imageView) {
        Image image = imageCache.get(imageUrl);
        if (image == null) {
            Task<Image> imageTask = new Task<>() {
                @Override
                protected Image call() {
                    Image loadedImage = new Image(imageUrl, true);
                    imageCache.putIfAbsent(imageUrl, loadedImage);
                    return loadedImage;
                }
            };

            imageTask.setOnSucceeded(event -> {
                Image loadedImage = imageTask.getValue();
                Platform.runLater(() -> imageView.setImage(loadedImage));
            });

            imageTask.setOnFailed(event -> {
                Throwable exception = imageTask.getException();
                LOGGER.log(Level.SEVERE, "Failed to load image: " + imageUrl, exception);
            });

            executorService.submit(imageTask);
        } else {
            imageView.setImage(image);
        }
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }


}
