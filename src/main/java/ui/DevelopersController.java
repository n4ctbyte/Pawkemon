package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import java.io.IOException;

public class DevelopersController {

    @FXML private Button backButton;

    @FXML
    private void onBackClicked() throws IOException {
        MusicManager.getInstance().playClickSound();
        Parent root = FXMLLoader.load(getClass().getResource("main_menu.fxml"));
        backButton.getScene().setRoot(root);
    }
}