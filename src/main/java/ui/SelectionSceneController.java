package ui;

import game.Hero;
import hero.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import game.BattleLogger;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectionSceneController {

    @FXML private GridPane pokemonGrid;
    @FXML private HBox team1Box;
    @FXML private HBox team2Box;
    @FXML private Button startButton;
    @FXML private Button backButton; 

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
    
    private boolean isPlayer1Turn = true; 
    private Label turnLabel;

    public void initialize() {
        turnLabel = new Label("Giliran Player 1 Memilih");
        turnLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        VBox rootVBox = (VBox) pokemonGrid.getParent(); 
        rootVBox.getChildren().add(1, turnLabel);
        
        populateHeroGrid();
    }

    private void populateHeroGrid() {
        int col = 0;
        int row = 0;
        for (Hero hero : availableHeroes) {
            VBox heroCard = createHeroCard(hero);
            pokemonGrid.add(heroCard, col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createHeroCard(Hero hero) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: white; -fx-padding: 10; -fx-border-width: 1; -fx-alignment: center; " +
                      "-fx-background-color: rgba(0, 0, 0, 0.4); -fx-background-radius: 10; -fx-border-radius: 10;");

        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        String heroFolder = hero.getName().toLowerCase().replace(" ", "_");
        String idlePath = String.format("/images/%s/idle.gif", heroFolder); 
        
        try {
            Image idleGif = new Image(getClass().getResourceAsStream(idlePath));
            imageView.setImage(idleGif);
        } catch (Exception e) {
            System.err.println("Gagal load GIF idle untuk " + hero.getName() + " di path: " + idlePath);
        }

        Label nameLabel = new Label(hero.getName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Button selectButton = new Button("Pilih");
        selectButton.setOnAction(e -> onSelectHero(hero, selectButton)); 

        card.getChildren().addAll(imageView, nameLabel, selectButton); 
        return card;
    }


    private void onSelectHero(Hero hero, Button clickedButton) {
        MusicManager.getInstance().playClickSound();
    
        HBox targetBox;
        List<Hero> targetTeam;

        if (isPlayer1Turn) {
            targetBox = team1Box;
            targetTeam = player1Team;
        } else {
            targetBox = team2Box;
            targetTeam = player2Team;
        }

        if (targetTeam.size() >= 3) {
            turnLabel.setText("Tim Anda sudah penuh (3 hero)!");
            return;
        }

        boolean alreadyPicked = false;
        for (Hero h : targetTeam) {
            if (h.getName().equals(hero.getName())) {
                alreadyPicked = true;
                break;
            }
        }
        if (alreadyPicked) {
            turnLabel.setText(hero.getName() + " sudah ada di tim Anda!");
            return;
        }
        
        VBox card = (VBox) clickedButton.getParent();
        ScaleTransition st = new ScaleTransition(Duration.millis(100), card);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.1); st.setToY(1.1);
        st.setCycleCount(2); 
        st.setAutoReverse(true);
        st.play();
        
        targetTeam.add(hero.clone()); 
        
        Label teamMemberLabel = new Label(hero.getName());
        teamMemberLabel.setStyle("-fx-text-fill: white; -fx-padding: 5; -fx-background-color: #555;");
        targetBox.getChildren().add(teamMemberLabel);
        
        if (isPlayer1Turn) {
            isPlayer1Turn = false;
            turnLabel.setText("Giliran Player 2 Memilih");
        } else {
            isPlayer1Turn = true;
            turnLabel.setText("Giliran Player 1 Memilih");
        }
        
        if (player1Team.size() == 3 && player2Team.size() == 3) {
            startButton.setDisable(false);
            pokemonGrid.setDisable(true);
            turnLabel.setText("Tim Penuh! Siap Bertarung!");
        }
    }

    @FXML
    private void startBattle() throws IOException {
        MusicManager.getInstance().playClickSound();
        BattleSceneController.setTeams(player1Team, player2Team);
        Parent root = FXMLLoader.load(getClass().getResource("battle.fxml"));
        startButton.getScene().setRoot(root);
    }
 
    @FXML
    private void onBackClicked() throws IOException {
        MusicManager.getInstance().playClickSound();
        MusicManager.getInstance().stopBattleMusic();
        Parent root = FXMLLoader.load(getClass().getResource("main_menu.fxml"));
        backButton.getScene().setRoot(root); 
    }
}