package ui;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SplashScreenController {

    @FXML private StackPane rootPane;
    @FXML private ImageView logoView;
    @FXML private Label brandLabel;
    @FXML private VBox brandBox;

    @FXML
    public void initialize() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logoView.setImage(logo);
        } catch (Exception e) {
            System.err.println("Gagal memuat /images/logo.png. Pastikan file ada.");
        }

        Platform.runLater(this::playTypingAnimation);
    }

    private void playTypingAnimation() {
        final String fullText = "Yaudahlah Studio";

        Timeline timeline = new Timeline();
        timeline.setCycleCount(fullText.length());
        KeyFrame keyFrame = new KeyFrame(
            Duration.millis(150),
            event -> {
                int currentIndex = brandLabel.getText().length();
                brandLabel.setText(fullText.substring(0, currentIndex + 1));
            }
        );
        timeline.getKeyFrames().add(keyFrame);

        FadeTransition brandFadeIn = new FadeTransition(Duration.seconds(1), brandBox);
        brandFadeIn.setFromValue(0.0);
        brandFadeIn.setToValue(1.0);
        
        FadeTransition logoFadeIn = new FadeTransition(Duration.seconds(1), logoView);
        logoFadeIn.setFromValue(0.0);
        logoFadeIn.setToValue(1.0);

        PauseTransition pt1_startDelay = new PauseTransition(Duration.seconds(1));
        PauseTransition pt2_postTypingDelay = new PauseTransition(Duration.seconds(0.5));
        PauseTransition pt3_brandHold = new PauseTransition(Duration.seconds(2.5));

        SequentialTransition sequence = new SequentialTransition(
            brandFadeIn,
            pt1_startDelay,
            timeline,
            pt2_postTypingDelay,
            logoFadeIn,
            pt3_brandHold
        );
        
        sequence.setOnFinished(event -> loadNextScene());
        sequence.play();
    }

    private void loadNextScene() {
        try {
            Parent mainMenuRoot = FXMLLoader.load(getClass().getResource("main_menu.fxml"));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(mainMenuRoot));
            stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}