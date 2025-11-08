package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    @FXML private StackPane rootPane;
    @FXML private Button battleButton;
    @FXML private Button devsButton;
    @FXML private Button creditsButton;

    @FXML
    public void initialize() {
    }

    @FXML
    private void onBattleClicked() {
        try {
            Parent selectionRoot = FXMLLoader.load(getClass().getResource("selection.fxml"));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(selectionRoot));
            stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onMeetDevsClicked() {
        try {
            Parent devsRoot = FXMLLoader.load(getClass().getResource("developers.fxml"));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(devsRoot));
            stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCreditsClicked() {
        try {
            Parent creditsRoot = FXMLLoader.load(getClass().getResource("credits.fxml"));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(creditsRoot));
            stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}