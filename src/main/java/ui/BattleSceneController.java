package ui;

import game.BattleSystem;
import game.Hero;
import game.Player;
import skill.Skill;
import skill.Ultimate;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class BattleSceneController {

    // FXML Elements for Team 1 (Player)
    @FXML private Label team1NameLabel;
    @FXML private ProgressBar team1HPBar;
    @FXML private ProgressBar team1EnergyBar;
    @FXML private ProgressBar team1UltimateBar;
    @FXML private ImageView team1ImageView;
    @FXML private HBox team1Members;

    // FXML Elements for Team 2 (Enemy)
    @FXML private Label team2NameLabel;
    @FXML private ProgressBar team2HPBar;
    @FXML private ProgressBar team2EnergyBar;
    @FXML private ProgressBar team2UltimateBar;
    @FXML private ImageView team2ImageView;
    @FXML private HBox team2Members;

    // FXML Elements for Control Panel
    @FXML private Label battleLog;
    @FXML private GridPane skillGrid;
    @FXML private Button switchButton;
    @FXML private AnchorPane battleArea;

    private static List<Hero> playerTeamList;
    private static List<Hero> enemyTeamList;
    private Player player1;
    private Player player2;
    private BattleSystem battleSystem;
    private Hero playerActiveHero;
    private Hero enemyActiveHero;
    private boolean isPlayerTurn = true;
    private Skill selectedSkill = null;

    public static void setTeams(List<Hero> team1, List<Hero> team2) {
        playerTeamList = team1;
        enemyTeamList = team2;
    }

    public void initialize() {
        // 1. Buat Player dari List<Hero>
        player1 = new Player("Player 1");
        player2 = new Player("Player 2");

        for (Hero h : playerTeamList) {
            player1.addHero(h);
        }
        for (Hero h : enemyTeamList) {
            player2.addHero(h);
        }

        // 2. Inisialisasi BattleSystem
        battleSystem = new BattleSystem(player1, player2);
        playerActiveHero = player1.getTeam().get(0);
        enemyActiveHero = player2.getTeam().get(0);

        // 3. Setup UI
        updateUI();
    }

    private void updateUI() {
        // Update Player 1 UI
        team1NameLabel.setText(playerActiveHero.getName());
        team1HPBar.setProgress((double) playerActiveHero.getCurrentHP() / playerActiveHero.getMaxHP());
        team1EnergyBar.setProgress((double) playerActiveHero.getCurrentEnergy() / playerActiveHero.getMaxEnergy());
        team1UltimateBar.setProgress((double) playerActiveHero.getUltimateBar() / 100);

        // Update Player 2 UI
        team2NameLabel.setText(enemyActiveHero.getName());
        team2HPBar.setProgress((double) enemyActiveHero.getCurrentHP() / enemyActiveHero.getMaxHP());
        team2EnergyBar.setProgress((double) enemyActiveHero.getCurrentEnergy() / enemyActiveHero.getMaxEnergy());
        team2UltimateBar.setProgress((double) enemyActiveHero.getUltimateBar() / 100);

        // Update Images
        team1ImageView.setImage(new Image("file:assets/images/" + playerActiveHero.getName().toLowerCase().replace(" ", "_") + ".png"));
        team2ImageView.setImage(new Image("file:assets/images/" + enemyActiveHero.getName().toLowerCase().replace(" ", "_") + ".png"));
    }

    private void onSkillClicked(Hero hero, int skillIndex) {
        Skill skill = hero.getSkills().get(skillIndex);

        if (!skill.isReady() || hero.getCurrentEnergy() < skill.getEnergyCost()) {
            battleLog.setText("Not enough energy or skill on cooldown!");
            return;
        }

        selectedSkill = skill;

        // Ambil energy dan start cooldown
        hero.setCurrentEnergy(hero.getCurrentEnergy() - skill.getEnergyCost());
        skill.startCooldown();

        if (skill instanceof Ultimate) {
            hero.setUltimateBar(0);
        }

        // Sekarang pilih target
        VBox targetBox = new VBox();
        targetBox.setSpacing(5);

        Player targetTeam = (player1.getTeam().contains(hero)) ? player2 : player1;

        for (Hero target : targetTeam.getAliveHeroes()) { // ← Ganti jadi getAliveHeroes()
            if (target.isAlive()) {
                Hero finalTarget = target;
                Button targetButton = new Button(target.getName());
                targetButton.setOnAction(e -> onTargetClicked(hero, skillIndex, finalTarget));
                targetBox.getChildren().add(targetButton);
            }
        }

        // Tambahkan ke scene
        battleArea.getChildren().add(targetBox);
    }

    private void onTargetClicked(Hero hero, int skillIndex, Hero target) {
        hero.getSkills().get(skillIndex).use(hero, target);
        battleLog.setText(hero.getName() + " uses skill on " + target.getName());

        // Update status efek
        for (Hero h : player1.getTeam()) {
            h.updateStatusEffects();
        }
        for (Hero h : player2.getTeam()) {
            h.updateStatusEffects();
        }

        // Hapus target buttons
        battleArea.getChildren().clear();

        // Ganti giliran
        isPlayerTurn = !isPlayerTurn;
        updateUI();
    }
}