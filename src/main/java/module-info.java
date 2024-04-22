module com.example.vidguessr.vidguessr {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires javafx.media;
    requires json.simple;
    requires org.jxmapviewer.jxmapviewer2;
    requires java.desktop;
    requires commons.logging;
    requires javafx.swing;

    opens com.example.vidguessr.vidguessr to javafx.fxml;
    exports com.example.vidguessr.vidguessr;
}