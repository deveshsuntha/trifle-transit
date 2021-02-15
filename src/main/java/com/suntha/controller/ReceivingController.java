package com.suntha.controller;

import com.suntha.services.ReceivingService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;

public class ReceivingController {

    private AppController parent;

    @FXML
    private Button start;

    @FXML
    private Label status;

    private File selectedDirectory;

    @FXML
    private TextField serverHost;

    @FXML
    private TextField serverPort;

    public void setParent(AppController appController) {
        this.parent = appController;
        parent.setHeading("Receiving Files");
    }

    @FXML
    private void initialize() {

    }

    @FXML
    public void directoryChooser() {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        selectedDirectory = directoryChooser.showDialog(start.getScene().getWindow());
        parent.log("Directory selected : " + selectedDirectory.getAbsolutePath());
    }

    @FXML
    private void controlServer() {

        status.setText("Listening for new Connections");

        try {
            Integer.valueOf(serverPort.getText());
        } catch (NumberFormatException ex) {
            Notifications.create()
                    .title("Invalid port")
                    .text("Please enter a valid port Err:" + serverPort.getText())
                    .hideAfter(Duration.seconds(4))
                    .position(Pos.BOTTOM_LEFT)
                    .darkStyle()
                    .showError();
        }

        ReceivingService receivingService = new ReceivingService(Integer.valueOf(serverPort.getText()), selectedDirectory.getPath(), serverHost.getText());

        receivingService.setOnFailed(workerStateEvent -> {
            status.setText("Unable to connect");
            parent.log("Unable to connect to sender.");
        });

        receivingService.setOnSucceeded(workerStateEvent -> {
            status.setText("Connected");
            parent.log("Connected to sender.");
            parent.log("Waiting for incoming files.");
        });

        receivingService.start();
    }
}
