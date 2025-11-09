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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.IOException;

public class SplashScreenController {

    @FXML private StackPane rootPane;
    @FXML private ImageView logoView;
    @FXML private Label brandLabel;
    @FXML private VBox brandBox;
    private MediaPlayer musicPlayer;
    private MediaPlayer typingAudioPlayer;

    @FXML
    public void initialize() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logoView.setImage(logo);
        } catch (Exception e) { System.err.println("Gagal memuat /images/logo.png."); }
        try {
            String sfxPath = getClass().getResource("/sounds/typing.mp3").toURI().toString();
            Media sfxMedia = new Media(sfxPath);
            typingAudioPlayer = new MediaPlayer(sfxMedia);
        } catch (Exception e) { System.err.println("Gagal memuat /sounds/typing.mp3."); }
        
        Platform.runLater(this::playTypingAnimation);
    }

    private void playTypingAnimation() {
        final String fullText = "Yaudahlah Studio";
        final long typingSpeedMs = 150;
        final long totalTypingTimeMs = fullText.length() * typingSpeedMs;

        if (typingAudioPlayer != null) {
            typingAudioPlayer.setStartTime(Duration.seconds(3));
            typingAudioPlayer.setStopTime(Duration.seconds(3).add(Duration.millis(totalTypingTimeMs)));
            typingAudioPlayer.setCycleCount(1);
        }
        Timeline timeline = new Timeline();
        timeline.setCycleCount(fullText.length());
        KeyFrame keyFrame = new KeyFrame(
            Duration.millis(typingSpeedMs), 
            event -> {
                int currentIndex = brandLabel.getText().length();
                brandLabel.setText(fullText.substring(0, currentIndex + 1));
            }
        );
        timeline.getKeyFrames().add(keyFrame);

        FadeTransition logoFadeIn = new FadeTransition(Duration.seconds(1), logoView);
        logoFadeIn.setFromValue(0.0);
        logoFadeIn.setToValue(1.0);

        brandBox.setOpacity(1.0);

        PauseTransition pt1_startDelay = new PauseTransition(Duration.seconds(1));
        PauseTransition pt2_postTypingDelay = new PauseTransition(Duration.seconds(0.5));
        PauseTransition pt3_brandHold = new PauseTransition(Duration.seconds(2.5));

        pt1_startDelay.setOnFinished(event -> { if (typingAudioPlayer != null) typingAudioPlayer.play(); });
        timeline.setOnFinished(event -> { if (typingAudioPlayer != null) typingAudioPlayer.stop(); });

        SequentialTransition sequence = new SequentialTransition(
            pt1_startDelay, timeline, pt2_postTypingDelay, logoFadeIn, pt3_brandHold
        );
        sequence.setOnFinished(event -> loadNextScene());
        sequence.play();
    }

    private void loadNextScene() {
        if (musicPlayer != null) musicPlayer.stop();
        if (typingAudioPlayer != null) typingAudioPlayer.stop();
    
        try {
            Parent selectionRoot = FXMLLoader.load(getClass().getResource("main_menu.fxml"));
            rootPane.getScene().setRoot(selectionRoot);
        } catch (IOException e) { e.printStackTrace(); }
    }
}