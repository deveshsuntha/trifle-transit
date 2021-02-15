package com.suntha.controller;

import com.suntha.TrifleApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Date;

public class AppController {

    @FXML
    private Label subheading;

    @FXML
    private HBox content;

    @FXML
    private TextArea logs;

    @FXML
    private void startsending() throws IOException {

        content.getChildren().clear();
        FXMLLoader fxmlLoader = new FXMLLoader(TrifleApp.class.getResource("/fxmls/" + "sending" + ".fxml"));
        Node root = fxmlLoader.load();
        SendingController sendingController = fxmlLoader.getController();
        sendingController.setParent(this);
        content.getChildren().add(root);

    }

    @FXML
    private void startreceiving() throws IOException {

        content.getChildren().clear();

        FXMLLoader fxmlLoader = new FXMLLoader(TrifleApp.class.getResource("/fxmls/" + "receiving" + ".fxml"));
        Node root = fxmlLoader.load();
        ReceivingController receivingController = fxmlLoader.getController();
        receivingController.setParent(this);
        content.getChildren().add(root);
    }

    public void log(String logLine) {
        logs.appendText(new Date() + " " + logLine + "\n");
    }

    public void setHeading(String text) {
        subheading.setText(text);
    }


}
