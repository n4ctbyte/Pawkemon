package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import java.io.InputStream;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            InputStream fontStreamTranscity = getClass().getResourceAsStream("/fonts/Transcity.otf");
            if (fontStreamTranscity != null) {
                Font.loadFont(fontStreamTranscity, 10);
                fontStreamTranscity.close();
            } else {
                System.err.println("File font /fonts/Transcity.otf tidak ditemukan!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gagal memuat font Transcity!");
        }

        try {
            InputStream fontStreamPokemon = getClass().getResourceAsStream("/fonts/Pokemon Solid.ttf");
            if (fontStreamPokemon != null) {
                Font.loadFont(fontStreamPokemon, 10);
                fontStreamPokemon.close();
            } else {
                System.err.println("File font /fonts/Pokemon Solid.ttf tidak ditemukan!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gagal memuat font Pokemon!");
        }

        Parent root = FXMLLoader.load(getClass().getResource("SplashScreen.fxml"));
        primaryStage.setTitle("Pawkemon Battle");
        primaryStage.setScene(new Scene(root));
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}