package ui;

import game.Hero;
import hero.BananaCat;
import hero.CryingHamster;
import hero.GregTheCrocodile;
import hero.KucingAkmal;
import hero.KucingCukurukuk;
import hero.SadCat;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectionSceneController {

    @FXML private VBox team1View;
    @FXML private VBox team2View;
    @FXML private Button startBattleButton;

    private List<Hero> availableHeroes = List.of(
        new KucingAkmal(),
        new KucingCukurukuk(),
        new GregTheCrocodile(),
        new BananaCat(),
        new SadCat(),
        new CryingHamster()
    );

    private List<Hero> player1Team = new ArrayList<>();
    private List<Hero> player2Team = new ArrayList<>();

    public void initialize() {
        // Tampilkan hero untuk pemilihan
        for (Hero hero : availableHeroes) {
            Button button = new Button(hero.getName());
            button.setOnAction(e -> onSelectHero(hero));
            team1View.getChildren().add(button);
        }
    }

    private void onSelectHero(Hero hero) {
        if (player1Team.size() < 3) {
            player1Team.add(hero.clone());
            System.out.println("Player 1 memilih: " + hero.getName());
        } else if (player2Team.size() < 3) {
            player2Team.add(hero.clone());
            System.out.println("Player 2 memilih: " + hero.getName());
        }

        if (player1Team.size() == 3 && player2Team.size() == 3) {
            startBattleButton.setDisable(false);
        }
    }

    @FXML
    private void onStartBattleClicked() throws IOException {
        BattleSceneController.setTeams(player1Team, player2Team);

        Stage stage = (Stage) startBattleButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("battle.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Battle Scene");
    }
}