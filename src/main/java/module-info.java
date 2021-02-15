module com.suntha {

    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    requires org.controlsfx.controls;

    opens com.suntha to javafx.fxml;
    opens com.suntha.controller to javafx.fxml;

    exports com.suntha;

}