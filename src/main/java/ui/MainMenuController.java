package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import java.io.IOException;

public class MainMenuController {

    @FXML private StackPane rootPane;
    @FXML private Button battleButton;
    @FXML private Button devsButton;
    @FXML private Button creditsButton;
    @FXML private Button pokedexButton;

    @FXML
    public void initialize() {
        MusicManager.getInstance().playMenuMusic();
    }

    @FXML
    private void onButtonHover(MouseEvent event) {
        Button hoveredButton = (Button) event.getSource();
        hoveredButton.setScaleX(1.05);
        hoveredButton.setScaleY(1.05);
    }

    @FXML
    private void onButtonExit(MouseEvent event) {
        Button exitedButton = (Button) event.getSource();
        exitedButton.setScaleX(1.0);
        exitedButton.setScaleY(1.0);
    }

    @FXML
    private void onBattleClicked() {
        try {
            MusicManager.getInstance().playClickSound();
            MusicManager.getInstance().playBattleMusic();
            Parent root = FXMLLoader.load(getClass().getResource("selection.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onPokedexClicked() {
        try {
            MusicManager.getInstance().playClickSound();
            Parent root = FXMLLoader.load(getClass().getResource("pokedex.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onMeetDevsClicked() {
        try {
            MusicManager.getInstance().playClickSound();
            Parent root = FXMLLoader.load(getClass().getResource("developers.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCreditsClicked() {
        try {
            MusicManager.getInstance().playClickSound();
            Parent root = FXMLLoader.load(getClass().getResource("credits.fxml"));
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}