package ui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator; 
import javafx.animation.ParallelTransition; 
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SplashScreen2Controller {

    @FXML private StackPane rootPane;
    @FXML private Pane blueBackground;
    @FXML private HBox titleHBox;

    private final String GAME_TITLE = "Pawkemon";

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            try {
                playAnimation();
            } catch (Exception e) {
                e.printStackTrace();
                loadNextScene();
            }
        });
    }

    private void playAnimation() {
        List<Label> letters = new ArrayList<>();
        for (char c : GAME_TITLE.toCharArray()) {
            Label letter = new Label(String.valueOf(c));
            letter.setFont(Font.font("Pokemon Solid", 120));
            letter.setTextFill(Color.WHITE);
            letter.setOpacity(0.0);
            letter.setTranslateY(-300);
            letters.add(letter);
            titleHBox.getChildren().add(letter);
        }

        SequentialTransition letterSequence = new SequentialTransition();
        
        for (Label letter : letters) {
            
            FadeTransition ft = new FadeTransition(Duration.millis(400), letter);
            ft.setToValue(1.0);

            // 1. Jatuh
            TranslateTransition tt_fall = new TranslateTransition(Duration.millis(400), letter);
            tt_fall.setToY(30); 
            tt_fall.setInterpolator(Interpolator.EASE_IN);
            
            // --- PERUBAHAN 1: Mainkan SFX Bounce saat jatuh selesai ---
            tt_fall.setOnFinished(e -> {
                MusicManager.getInstance().playBounceSound();
            });
            // ----------------------------------------------------

            // 2. Pantulan
            TranslateTransition tt_bounce1 = new TranslateTransition(Duration.millis(150), letter);
            tt_bounce1.setToY(-10);
            tt_bounce1.setInterpolator(Interpolator.EASE_OUT);

            // 3. Pantulan
            TranslateTransition tt_bounce2 = new TranslateTransition(Duration.millis(100), letter);
            tt_bounce2.setToY(0);
            tt_bounce2.setInterpolator(Interpolator.EASE_OUT);
            
            SequentialTransition bounce = new SequentialTransition(tt_fall, tt_bounce1, tt_bounce2);
            ParallelTransition dropIn = new ParallelTransition(ft, bounce);
            
            letterSequence.getChildren().addAll(
                new PauseTransition(Duration.millis(10)),
                dropIn
            );
        }

        // 3. Animasi Background
        FadeTransition bgFade = new FadeTransition(Duration.seconds(1), blueBackground);
        bgFade.setToValue(1.0);

        // 4. Jeda & Rangkaian
        PauseTransition pt1_startDelay = new PauseTransition(Duration.millis(500));
        
        // --- PERUBAHAN 2: Pisahkan jeda sebelum BG ---
        // Buat jeda *sebelum* background berganti
        PauseTransition pt2_preBgFade = new PauseTransition(Duration.millis(500));
        
        // Tambahkan aksi: Mainkan SFX BG Reveal saat jeda ini selesai
        pt2_preBgFade.setOnFinished(e -> {
            MusicManager.getInstance().playBgRevealSound();
        });
        // ----------------------------------------------
        
        PauseTransition pt3_brandHold = new PauseTransition(Duration.seconds(2));

        SequentialTransition fullSequence = new SequentialTransition(
            pt1_startDelay,
            letterSequence,    // Huruf jatuh (memutar SFX bounce)
            pt2_preBgFade,     // Jeda (memutar SFX reveal)
            bgFade,            // Background berubah (bersamaan SFX reveal)
            pt3_brandHold
        );

        fullSequence.setOnFinished(e -> {
            loadNextScene();
        });
        
        fullSequence.play();
    }

    private void loadNextScene() {
        try {
            Parent menuRoot = FXMLLoader.load(getClass().getResource("main_menu.fxml"));
            rootPane.getScene().setRoot(menuRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}