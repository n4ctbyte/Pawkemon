package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class DevelopersController {

    @FXML
    private Button backButton;

    @FXML
    private void onBackClicked() throws IOException {
        Parent menuRoot = FXMLLoader.load(getClass().getResource("main_menu.fxml"));
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(menuRoot));
        stage.setFullScreen(true);
    }
}