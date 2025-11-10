package ui;

import game.BattleLogger;
import game.Hero;
import game.Player;
import game.TargetType;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import skill.Skill;
import skill.SkillWithTargetType;
import skill.Ultimate;
import skill.HealSkill;
import skill.HoTSkill;
import javafx.util.Duration;
import java.util.stream.Stream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BattleSceneController implements BattleLogger.LogListener {

    @FXML private StackPane rootStackPane;
    @FXML private VBox gameOverPane;
    @FXML private Label winnerLabel;
    @FXML private Button rematchButton;
    @FXML private Button exitButton;
    @FXML private VBox team1VBox;
    @FXML private VBox team2VBox;
    @FXML private Label announcementLabel;
    @FXML private TextArea battleLogArea;
    @FXML private GridPane skillGrid;
    @FXML private VBox controlPanel;
    @FXML private Button backButton;

    private static List<Hero> playerTeamList;
    private static List<Hero> enemyTeamList;
    private Player player1, player2, currentPlayer;
    private List<HeroCardController> player1Cards = new ArrayList<>();
    private List<HeroCardController> player2Cards = new ArrayList<>();
    private GameState currentState = GameState.SELECTING_HERO;
    private Hero selectedHero;
    private Skill selectedSkill;

    private enum GameState {
        SELECTING_HERO, SELECTING_SKILL, SELECTING_TARGET, BUSY, GAME_OVER
    }

    public static void setTeams(List<Hero> team1, List<Hero> team2) {
        playerTeamList = team1;
        enemyTeamList = team2;
    }

    @FXML
    public void initialize() {
        for (Hero hero : playerTeamList) hero.reset();
        for (Hero hero : enemyTeamList) hero.reset();
        player1 = new Player("Player 1");
        playerTeamList.forEach(player1::addHero);
        player2 = new Player("Player 2");
        enemyTeamList.forEach(player2::addHero);
        currentPlayer = player1;
        BattleLogger.getInstance().setListener(this);
        battleLogArea.setEditable(false);
        battleLogArea.setWrapText(true);
        battleLogArea.setText("");
        gameOverPane.setVisible(false);
        gameOverPane.setOpacity(0.0);

        loadHeroCards(player1, team1VBox, player1Cards);
        loadHeroCards(player2, team2VBox, player2Cards);

        currentState = GameState.BUSY;
        team1VBox.setTranslateX(-400);
        team2VBox.setTranslateX(400);
        team1VBox.setOpacity(0);
        team2VBox.setOpacity(0);
        controlPanel.setOpacity(0);
        skillGrid.setDisable(true);
        backButton.setVisible(false);

        updateAllHeroCards();
        resetAllAnimationsToIdle();

        Platform.runLater(this::playIntroAnimation);
    }

    private void playIntroAnimation() {
        TranslateTransition tt1 = new TranslateTransition(Duration.millis(800), team1VBox);
        tt1.setToX(0);
        TranslateTransition tt2 = new TranslateTransition(Duration.millis(800), team2VBox);
        tt2.setToX(0);
        FadeTransition ft1 = new FadeTransition(Duration.millis(500), team1VBox);
        ft1.setToValue(1.0);
        FadeTransition ft2 = new FadeTransition(Duration.millis(500), team2VBox);
        ft2.setToValue(1.0);
        FadeTransition ft3 = new FadeTransition(Duration.millis(500), controlPanel);
        ft3.setToValue(1.0);
        ParallelTransition slideInTeams = new ParallelTransition(tt1, tt2, ft1, ft2);
        PauseTransition pt1 = new PauseTransition(Duration.millis(500));
        SequentialTransition introSequence = new SequentialTransition(slideInTeams, pt1, ft3);
        introSequence.setOnFinished(e -> {
            log("Giliran Player 1. Pilih hero yang akan beraksi.");
            currentState = GameState.SELECTING_HERO;
        });
        introSequence.play();
    }

    private void loadHeroCards(Player player, VBox teamVBox, List<HeroCardController> cardList) {
        String idleAnim = (player == player1) ? "idle_left" : "idle_right";
        for (Hero hero : player.getTeam()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("HeroCard.fxml"));
                VBox heroCardNode = loader.load();
                HeroCardController controller = loader.getController();
                controller.setIdleAnimation(idleAnim);
                controller.updateData(hero);
                heroCardNode.setOnMouseClicked(e -> onHeroCardClicked(controller));
                teamVBox.getChildren().add(heroCardNode);
                cardList.add(controller);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    @FXML
    private void onBackButtonClicked() {
        MusicManager.getInstance().playClickSound();
        if (currentState == GameState.SELECTING_SKILL) {
            currentState = GameState.SELECTING_HERO;
            log("Pilih hero yang akan beraksi.");
            skillGrid.getChildren().clear();
            skillGrid.setDisable(true);
            backButton.setVisible(false);
            clearAllHighlights();
            selectedHero = null;
        } else if (currentState == GameState.SELECTING_TARGET) {
            currentState = GameState.SELECTING_SKILL;
            log(selectedHero.getName() + " dipilih. Silakan pilih skill.");
            clearAllHighlights();
            highlightHero(selectedHero, "#2ecc71");
            selectedSkill = null;
        }
    }

    private void onHeroCardClicked(HeroCardController clickedCard) {
        MusicManager.getInstance().playClickSound();
        Hero clickedHero = clickedCard.getHero();
        List<HeroCardController> currentPlayerCards = (currentPlayer == player1) ? player1Cards : player2Cards;
        List<HeroCardController> opponentPlayerCards = (currentPlayer == player1) ? player2Cards : player1Cards;
        switch (currentState) {
            case SELECTING_HERO:
                if (clickedHero.isDead()) { log(clickedHero.getName() + " sudah mati!"); return; }
                if (currentPlayerCards.contains(clickedCard)) {
                    if (clickedHero.isStunned()) { log(clickedHero.getName() + " sedang STUN, tidak bisa beraksi!"); return; }
                    this.selectedHero = clickedHero;
                    populateSkillGrid(selectedHero);
                    currentState = GameState.SELECTING_SKILL;
                    clearAllHighlights();
                    clickedCard.highlight("#2ecc71");
                    backButton.setVisible(true);
                    log(selectedHero.getName() + " dipilih. Silakan pilih skill.");
                } else { log("Bukan giliranmu! Ini giliran " + currentPlayer.getName()); }
                break;
            case SELECTING_TARGET:
                TargetType requiredType = getSkillTargetType(selectedSkill);
                boolean isValidTarget = false;
                if (selectedSkill.getName().equals("Divine Tears")) {
                    if (currentPlayerCards.contains(clickedCard) && clickedHero.isDead()) isValidTarget = true;
                    else if (currentPlayerCards.contains(clickedCard) && clickedHero.isAlive()) log(clickedHero.getName() + " masih hidup!");
                    else log("Hanya bisa me-revive kawan!");
                } else {
                    if (clickedHero.isDead()) { log(clickedHero.getName() + " sudah mati, tidak bisa ditarget!"); return; }
                    if ((requiredType == TargetType.SINGLE_ENEMY || requiredType == TargetType.ALL_ENEMIES) && opponentPlayerCards.contains(clickedCard)) isValidTarget = true;
                    else if ((requiredType == TargetType.SINGLE_ALLY || requiredType == TargetType.ALL_ALLIES) && currentPlayerCards.contains(clickedCard)) isValidTarget = true;
                    else if (requiredType == TargetType.SELF && clickedHero == selectedHero) isValidTarget = true;
                }
                if (isValidTarget) executeTurn(selectedHero, clickedHero);
                else if (!selectedSkill.getName().equals("Divine Tears")) log("Target tidak valid untuk skill " + selectedSkill.getName());
                break;
            case SELECTING_SKILL:
                if (clickedHero.isDead()) { log(clickedHero.getName() + " sudah mati!"); return; }
                if (currentPlayerCards.contains(clickedCard) && clickedHero != selectedHero) {
                    if (clickedHero.isStunned()) { log(clickedHero.getName() + " sedang STUN!"); return; }
                    this.selectedHero = clickedHero;
                    populateSkillGrid(selectedHero);
                    clearAllHighlights();
                    clickedCard.highlight("#2ecc71");
                    log(selectedHero.getName() + " dipilih. Silakan pilih skill.");
                }
                break;
        }
    }

    private void onSkillButtonClicked(Skill skill) {
        MusicManager.getInstance().playClickSound();
        if (currentState != GameState.SELECTING_SKILL) return;
        this.selectedSkill = skill;
        TargetType targetType = getSkillTargetType(skill);
        if (targetType == TargetType.SELF) executeTurn(selectedHero, selectedHero);
        else if (targetType == TargetType.ALL_ALLIES || targetType == TargetType.ALL_ENEMIES) {
            Hero dummyTarget = (targetType == TargetType.ALL_ALLIES) ? currentPlayer.getAliveHeroes().get(0) : ((currentPlayer == player1) ? player2 : player1).getAliveHeroes().get(0);
            executeTurn(selectedHero, dummyTarget);
        } else {
            currentState = GameState.SELECTING_TARGET;
            log(skill.getName() + " dipilih. Pilih target!");
            highlightTargetOptions(targetType);
        }
    }

    private void executeTurn(Hero user, Hero target) {
        currentState = GameState.BUSY;
        skillGrid.setDisable(true);
        backButton.setVisible(false);
        clearAllHighlights();
        battleLogArea.setText("");
        log(user.getName() + " menggunakan " + selectedSkill.getName() + "!");

        HeroCardController userCard = getCardForHero(user);
        if (userCard != null) userCard.playOneShotAnimation(selectedSkill.getName());

        Player opponentPlayer = (currentPlayer == player1) ? player2 : player1;
        TargetType targetType = getSkillTargetType(selectedSkill);

        List<Hero> targets = new ArrayList<>();
        switch (targetType) {
            case SELF: targets.add(user); break;
            case SINGLE_ALLY:
            case SINGLE_ENEMY: targets.add(target); break;
            case ALL_ALLIES: targets.addAll(currentPlayer.getAliveHeroes()); break;
            case ALL_ENEMIES: targets.addAll(opponentPlayer.getAliveHeroes()); break;
        }

        if ((targetType == TargetType.ALL_ALLIES || targetType == TargetType.ALL_ENEMIES)) {
            try {
                java.lang.reflect.Method method = selectedSkill.getClass().getMethod("useAOE", Hero.class, List.class, Player.class);
                method.invoke(selectedSkill, user, targets, currentPlayer);
            } catch (Exception e) {
                try {
                    if (selectedSkill instanceof skill.AoeSkill) ((skill.AoeSkill) selectedSkill).useAOE(user, targets);
                    else for (Hero t : targets) selectedSkill.use(user, t);
                } catch (Exception ex) { System.err.println("Gagal panggil useAOE: " + ex.getMessage()); }
            }
        } else {
            try {
                java.lang.reflect.Method method = selectedSkill.getClass().getMethod("use", Hero.class, Hero.class, Player.class);
                method.invoke(selectedSkill, user, targets.get(0), currentPlayer);
            } catch (NoSuchMethodException e) {
                try {
                    selectedSkill.use(user, targets.get(0));
                } catch (Exception ex) { System.err.println("Gagal panggil method 'use' standar: " + ex.getMessage()); }
            } catch (Exception e) { System.err.println("Gagal panggil method 'use' custom: " + e.getMessage()); }
        }

        boolean isHeal = (selectedSkill instanceof HealSkill || selectedSkill instanceof HoTSkill || selectedSkill.getName().equals("Divine Tears"));
        for (Hero t : targets) {
            HeroCardController targetCard = getCardForHero(t);
            if (targetCard != null) {
                if (isHeal) targetCard.playOneShotAnimation("heal");
                else if (targetType != TargetType.SELF) targetCard.playOneShotAnimation("hit");
            }
        }

        if (selectedSkill instanceof Ultimate) user.setUltimateBar(0);
        else if (!(selectedSkill instanceof skill.BasicAttack)) {
            user.setCurrentEnergy(user.getCurrentEnergy() - selectedSkill.getEnergyCost());
            selectedSkill.startCooldown();
            user.gainUltimateBar(20);
        }

        player1.getTeam().forEach(h -> { if (h.isAlive()) h.updateStatusEffects(); });
        player2.getTeam().forEach(h -> { if (h.isAlive()) h.updateStatusEffects(); });
        currentPlayer.getTeam().forEach(Hero::reduceStatusEffectDurations);
        currentPlayer.getTeam().forEach(h -> h.getSkills().forEach(Skill::reduceCooldown));

        updateAllHeroCards();
        if (!player1.isTeamAlive() || !player2.isTeamAlive()) { endGame(); return; }

        new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException e) {}
            Platform.runLater(this::switchTurn);
        }).start();
    }

    private void switchTurn() {
        selectedHero = null;
        selectedSkill = null;
        skillGrid.getChildren().clear();
        skillGrid.setDisable(true);
        updateAllHeroCards();
        updateAllPersistentAnimations();

        if (currentPlayer == player1) {
            currentPlayer = player2;
            log("Giliran Player 2. Pilih hero yang akan beraksi.");
        } else {
            currentPlayer = player1;
            log("Giliran Player 1. Pilih hero yang akan beraksi.");
        }
        currentState = GameState.SELECTING_HERO;

        List<Hero> aliveHeroes = currentPlayer.getAliveHeroes();
        if (aliveHeroes.isEmpty()) return;
        boolean allStunned = aliveHeroes.stream().allMatch(Hero::isStunned);
        if (allStunned) {
            log(currentPlayer.getName() + " tidak bisa bergerak! (Semua hero stun)");
            BattleLogger.getInstance().log("--- Giliran " + currentPlayer.getName() + " dilewati (Stun) ---");
            new Thread(() -> {
                try { Thread.sleep(1500); } catch (InterruptedException e) {}
                Platform.runLater(this::skipTurn);
            }).start();
        }
    }

    private void skipTurn() {
        currentState = GameState.BUSY;
        skillGrid.setDisable(true);
        backButton.setVisible(false);
        clearAllHighlights();
        log(currentPlayer.getName() + " melewatkan giliran...");
        battleLogArea.setText("");
        player1.getTeam().forEach(h -> { if (h.isAlive()) h.updateStatusEffects(); });
        player2.getTeam().forEach(h -> { if (h.isAlive()) h.updateStatusEffects(); });
        currentPlayer.getTeam().forEach(Hero::reduceStatusEffectDurations);
        currentPlayer.getTeam().forEach(h -> h.getSkills().forEach(Skill::reduceCooldown));
        updateAllHeroCards();
        updateAllPersistentAnimations();
        if (!player1.isTeamAlive() || !player2.isTeamAlive()) { endGame(); return; }
        new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException e) {}
            Platform.runLater(this::switchTurn);
        }).start();
    }

    private void endGame() {
        currentState = GameState.GAME_OVER;
        controlPanel.setDisable(true);
        backButton.setVisible(false);
        Player winner = player1.isTeamAlive() ? player1 : player2;
        log(winner.getName() + " MENANG!");
        winnerLabel.setText(winner.getName() + " MENANG!");
        gameOverPane.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(500), gameOverPane);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    @FXML
    private void onRematchClicked() throws IOException {
        MusicManager.getInstance().playClickSound();
        Parent battleRoot = FXMLLoader.load(getClass().getResource("battle.fxml"));
        rootStackPane.getScene().setRoot(battleRoot);
    }

    @FXML
    private void onExitToMenuClicked() throws IOException {
        MusicManager.getInstance().playClickSound();
        MusicManager.getInstance().stopBattleMusic();
        Parent menuRoot = FXMLLoader.load(getClass().getResource("main_menu.fxml"));
        rootStackPane.getScene().setRoot(menuRoot);
    }

    private void populateSkillGrid(Hero hero) {
        skillGrid.getChildren().clear();
        skillGrid.setDisable(false);
        List<Skill> skills = hero.getSkills();
        for (int i = 0; i < skills.size(); i++) {
            Skill skill = skills.get(i);
            Button skillButton = new Button(skill.getName());
            skillButton.setPrefSize(130, 60);
            skillButton.setWrapText(true);
            String reason = "";
            boolean isSkillReady = false;
            if (skill instanceof Ultimate) {
                skillButton.setText(skill.getName() + "\n(ULTIMATE)");
                if (hero.getUltimateBar() >= 100) {
                    if (skill.getName().equals("Divine Tears")) {
                        if (currentPlayer.getDeadHeros().isEmpty()) {
                            isSkillReady = false;
                            reason = "(Tidak ada kawan mati)";
                        } else isSkillReady = true;
                    } else isSkillReady = true;
                } else {
                    isSkillReady = false;
                    reason = "(Ulti: " + hero.getUltimateBar() + "%)";
                }
            } else {
                skillButton.setText(skill.getName() + "\n(Cost: " + skill.getEnergyCost() + ")");
                boolean isReady = skill.isReady();
                boolean hasEnergy = hero.getCurrentEnergy() >= skill.getEnergyCost();
                if (isReady && hasEnergy) isSkillReady = true;
                else {
                    isSkillReady = false;
                    reason = !isReady ? " (CD: " + skill.getCurrentCooldown() + ")" : " (No Energy)";
                }
            }
            if (isSkillReady) {
                skillButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                skillButton.setOnAction(e -> onSkillButtonClicked(skill));
            } else {
                skillButton.setDisable(true);
                skillButton.setText(skill.getName() + "\n" + reason);
                skillButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: #bdc3c7;");
            }
            skillGrid.add(skillButton, i % 2, i / 2);
        }
    }
    
    private void updateAllHeroCards() {
        for (HeroCardController card : player1Cards) card.updateData(card.getHero());
        for (HeroCardController card : player2Cards) card.updateData(card.getHero());
    }
    private HeroCardController getCardForHero(Hero hero) {
        return Stream.concat(player1Cards.stream(), player2Cards.stream())
                .filter(card -> card.getHero() == hero)
                .findFirst()
                .orElse(null);
    }
    
    private void resetAllAnimationsToIdle() {
        updateAllPersistentAnimations();
    }
    
    private void updateAllPersistentAnimations() {
        for (HeroCardController card : player1Cards) {
            card.updatePersistentAnimation();
        }
        for (HeroCardController card : player2Cards) {
            card.updatePersistentAnimation();
        }
    }
    
    private void highlightHero(Hero hero, String color) {
        getCardForHero(hero).highlight(color);
    }
    private void highlightTargetOptions(TargetType targetType) {
        clearAllHighlights();
        highlightHero(selectedHero, "#2ecc71");
        String targetColor = "#e74c3c";
        List<HeroCardController> allies = (currentPlayer == player1) ? player1Cards : player2Cards;
        List<HeroCardController> enemies = (currentPlayer == player1) ? player2Cards : player1Cards;
        if (selectedSkill.getName().equals("Divine Tears")) {
            allies.forEach(card -> { if (card.getHero().isDead()) card.highlight(targetColor); });
        } else if (targetType == TargetType.SINGLE_ALLY) {
            allies.forEach(card -> { if (card.getHero() != selectedHero && card.getHero().isAlive()) card.highlight(targetColor); });
        } else if (targetType == TargetType.SINGLE_ENEMY) {
            enemies.forEach(card -> { if (card.getHero().isAlive()) card.highlight(targetColor); });
        }
    }
    private void clearAllHighlights() {
        player1Cards.forEach(HeroCardController::clearHighlight);
        player2Cards.forEach(HeroCardController::clearHighlight);
    }
    private TargetType getSkillTargetType(Skill skill) {
        if (skill instanceof SkillWithTargetType) return ((SkillWithTargetType) skill).getTargetType();
        return TargetType.SINGLE_ENEMY;
    }
    private void log(String message) {
        announcementLabel.setText(message);
        System.out.println("[Announcement] " + message);
    }
    @Override
    public void onNewLog(String message) {
        battleLogArea.appendText("  > " + message + "\n");
    }
}