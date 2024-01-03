module com.realestate.rocknroll {
    requires MaterialFX;
    requires VirtualizedFX;
    requires com.calendarfx.view;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.reactivestreams;
    requires org.mongodb.bson;
    requires org.reactivestreams;
    requires com.jthemedetector;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires java.logging;

    opens com.tourism.asianex to javafx.fxml;
    exports com.tourism.asianex;
    exports com.tourism.asianex.Controllers;
    exports com.tourism.asianex.Models;
    exports com.tourism.asianex.Utils;
    exports com.tourism.asianex.Services;
    opens com.tourism.asianex.Controllers to javafx.fxml;
}