module hr.algebra.cugomatfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires static lombok;
    requires jjwt.api;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;
    requires net.sf.jasperreports.core;
    requires junit;
    requires org.slf4j;

    opens hr.algebra.cugomatfx to javafx.fxml, com.fasterxml.jackson.databind;
    opens hr.algebra.cugomatfx.dto to com.fasterxml.jackson.databind;
    opens hr.algebra.cugomatfx.controllers to javafx.fxml;
    opens hr.algebra.cugomatfx.mapper to javafx.fxml;

    exports hr.algebra.cugomatfx;
    exports hr.algebra.cugomatfx.controllers;
    exports hr.algebra.cugomatfx.mapper;
    exports hr.algebra.cugomatfx.models;
    opens hr.algebra.cugomatfx.models to com.fasterxml.jackson.databind, javafx.fxml;
}