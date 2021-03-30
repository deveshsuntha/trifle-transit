package com.suntha.controller;

import com.suntha.services.SenderService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;

public class SendingController {

    private AppController parent;

    @FXML
    private Button start;

    @FXML
    private Label status;

    @FXML
    private TextField port;

    @FXML
    private TextField host;

    @FXML
    private Button dirChooser;

    private File selectedDirectory;

    public void setParent(AppController appController) {
        this.parent = appController;
        parent.setHeading("Sending Files");
    }

    @FXML
    private void initialize() {

        dirChooser.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            selectedDirectory = directoryChooser.showDialog(dirChooser.getScene().getWindow());
            parent.log("Directory selected : " + selectedDirectory.getAbsolutePath());
        });

    }

    @FXML
    private void startAction() {

        try {
            Integer.valueOf(port.getText());
        } catch (NumberFormatException ex) {
            Notifications.create()
                    .title("Invalid port")
                    .text("Please enter a valid port Err:" + port.getText())
                    .hideAfter(Duration.seconds(4))
                    .position(Pos.BOTTOM_LEFT)
                    .darkStyle()
                    .showError();
            return;
        }


        status.setText("Connecting Receiver");

        SenderService senderService = new SenderService(parent, selectedDirectory.getPath(), host.getText(), Integer.valueOf(port.getText()));

        senderService.setOnSucceeded(workerStateEvent -> {
            status.setText("Connected");
        });

        senderService.setOnFailed(workerStateEvent -> {
            status.setText("Unable to connect");
            parent.log("Unable to connect to receiver.");
        });

        senderService.start();
    }
}
