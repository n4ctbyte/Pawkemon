package ui;

import game.Hero;
import hero.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import skill.Skill;
import java.io.IOException;
import java.util.List;

public class PokedexController {

    @FXML private Button backButton;
    @FXML private StackPane rootPane;
    @FXML private VBox heroListVBox;

    private List<Hero> availableHeroes = List.of(
        new KucingAkmal(),
        new KucingCukurukuk(),
        new GregTheCrocodile(),
        new BananaCat(),
        new SadCat(),
        new CryingHamster()
    );

    @FXML
    public void initialize() {
        populateHeroList();
    }

    private void populateHeroList() {
        heroListVBox.getChildren().clear();

        for (int i = 0; i < availableHeroes.size(); i++) {
            Hero hero = availableHeroes.get(i);

            VBox heroEntry = new VBox(15.0);
            heroEntry.setPadding(new Insets(10));

            HBox headerBox = new HBox(20.0);
            headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            String heroFolder = hero.getName().toLowerCase().replace(" ", "_");
            String idlePath = String.format("/images/%s/idle_right.gif", heroFolder);
            ImageView heroIdleView = new ImageView();
            heroIdleView.setFitHeight(120.0);
            heroIdleView.setFitWidth(120.0);
            heroIdleView.setPreserveRatio(true);
            heroIdleView.setScaleX(-1);
            try {
                Image idleGif = new Image(getClass().getResourceAsStream(idlePath));
                heroIdleView.setImage(idleGif);
            } catch (Exception e) {
                System.err.println("Gagal load " + idlePath);
            }

            Label heroNameLabel = new Label(hero.getName());
            heroNameLabel.setTextFill(Color.web("#f1c40f"));
            heroNameLabel.setFont(Font.font("Transcity", 48.0));

            headerBox.getChildren().addAll(heroIdleView, heroNameLabel);

            Label statsTitle = new Label("Base Attributes");
            statsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

            GridPane statsGrid = new GridPane();
            statsGrid.setHgap(20.0);
            statsGrid.setVgap(5.0);
            statsGrid.add(createStatLabel("HP:", String.valueOf(hero.getMaxHP())), 0, 0);
            statsGrid.add(createStatLabel("ATK:", String.valueOf(hero.getBaseAttack())), 1, 0);
            statsGrid.add(createStatLabel("DEF:", String.valueOf(hero.getBaseDefense())), 2, 0);
            statsGrid.add(createStatLabel("ENERGY:", String.valueOf(hero.getMaxEnergy())), 3, 0);

            Label skillsTitle = new Label("Skills");
            skillsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

            VBox skillsVBox = new VBox(10.0);
            for (Skill skill : hero.getSkills()) {
                VBox skillEntry = new VBox(5.0);

                String cost = (skill instanceof skill.Ultimate) ? "(ULT)" : "(Cost: " + skill.getEnergyCost() + ")";
                Label skillName = new Label(skill.getName() + " " + cost);
                skillName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #f1c40f;");

                Label skillDesc = new Label(skill.getDescription());
                skillDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
                skillDesc.setWrapText(true);

                skillEntry.getChildren().addAll(skillName, skillDesc);
                skillsVBox.getChildren().add(skillEntry);
            }

            heroEntry.getChildren().addAll(headerBox, statsTitle, statsGrid, skillsTitle, skillsVBox);
            heroListVBox.getChildren().add(heroEntry);

            if (i < availableHeroes.size() - 1) {
                heroListVBox.getChildren().add(new Separator());
            }
        }
    }

    private Label createStatLabel(String title, String value) {
        Label label = new Label(title + " " + value);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold;");
        return label;
    }

    @FXML
    private void onBackClicked() throws IOException {
        MusicManager.getInstance().playClickSound();
        Parent root = FXMLLoader.load(getClass().getResource("main_menu.fxml"));
        rootPane.getScene().setRoot(root);
    }
}