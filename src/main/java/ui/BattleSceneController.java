package ui;

import game.BattleLogger;
import game.Hero;
import game.Player;
import game.TargetType;
import game.StatusEffect;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar; 
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode; 
import javafx.scene.input.KeyEvent; 
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox; 
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color; 
import javafx.scene.shape.Circle; 
import javafx.scene.text.Font; 
import javafx.stage.Stage;
import skill.Skill;
import skill.SkillWithTargetType;
import skill.Ultimate;
import skill.BasicAttack; 
import skill.HealSkill; 
import skill.HoTSkill; 
import javafx.util.Duration;
import java.util.stream.Stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    @FXML private StackPane castingPane;
    @FXML private Label castingArrowLabel;
    @FXML private Circle castingCircle;
    @FXML private Label castingStatusLabel;
    @FXML private HBox castingIndicatorsBox;
    @FXML private ProgressBar castingTimerBar;

    private static List<Hero> playerTeamList;
    private static List<Hero> enemyTeamList;
    private Player player1, player2, currentPlayer;
    private List<HeroCardController> player1Cards = new ArrayList<>();
    private List<HeroCardController> player2Cards = new ArrayList<>();
    private GameState currentState = GameState.SELECTING_HERO;
    private Hero selectedHero;
    private Skill selectedSkill;
    private Hero pendingUser;
    private List<Hero> pendingTargets;
    private List<KeyCode> currentSequence;
    private int currentStepIndex;
    private int successfulHits;
    private boolean isStepActive = false;
    private Timeline minigameTimer;
    private Timeline arrowTimer;
    private static final Random random = new Random();

    private enum GameState {
        SELECTING_HERO, SELECTING_SKILL, SELECTING_TARGET, CASTING_MINIGAME, BUSY, GAME_OVER
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
        castingPane.setVisible(false); 
        loadHeroCards(player1, team1VBox, player1Cards);
        loadHeroCards(player2, team2VBox, player2Cards);
        Platform.runLater(() -> {
            rootStackPane.getScene().setOnKeyPressed(this::handleKeyPress);
        });
        currentState = GameState.BUSY; 
        team1VBox.setTranslateX(-400); 
        team2VBox.setTranslateX(400);  
        team1VBox.setOpacity(0); 
        team2VBox.setOpacity(0);
        controlPanel.setOpacity(0); 
        skillGrid.setDisable(true);
        backButton.setVisible(false);
        updateAllHeroCards();
        updateAllPersistentAnimations();
        Platform.runLater(this::playIntroAnimation);
    }
    
    private void loadHeroCards(Player player, VBox teamVBox, List<HeroCardController> cardList) {
        boolean isMirrored = (player == player1);
        for (Hero hero : player.getTeam()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("HeroCard.fxml"));
                VBox heroCardNode = loader.load();
                HeroCardController controller = loader.getController();
                controller.setMirrored(isMirrored);
                controller.updateData(hero);
                heroCardNode.setOnMouseClicked(e -> onHeroCardClicked(controller));
                teamVBox.getChildren().add(heroCardNode);
                cardList.add(controller);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private void playIntroAnimation() {
        TranslateTransition tt1 = new TranslateTransition(Duration.millis(800), team1VBox); tt1.setToX(0); 
        TranslateTransition tt2 = new TranslateTransition(Duration.millis(800), team2VBox); tt2.setToX(0); 
        FadeTransition ft1 = new FadeTransition(Duration.millis(500), team1VBox); ft1.setToValue(1.0);
        FadeTransition ft2 = new FadeTransition(Duration.millis(500), team2VBox); ft2.setToValue(1.0);
        FadeTransition ft3 = new FadeTransition(Duration.millis(500), controlPanel); ft3.setToValue(1.0);
        ParallelTransition slideInTeams = new ParallelTransition(tt1, tt2, ft1, ft2);
        PauseTransition pt1 = new PauseTransition(Duration.millis(500)); 
        SequentialTransition introSequence = new SequentialTransition(slideInTeams, pt1, ft3);
        introSequence.setOnFinished(e -> { log("Giliran Player 1. Pilih hero yang akan beraksi."); currentState = GameState.SELECTING_HERO; });
        introSequence.play();
    }

    private void handleKeyPress(KeyEvent event) {
        if (currentState != GameState.CASTING_MINIGAME || !isStepActive) return;
        KeyCode input = event.getCode();
        if (!isDirectionKey(input)) return; 
        KeyCode expected = currentSequence.get(currentStepIndex);
        if (currentPlayer == player1) {
            if (input == KeyCode.W) input = KeyCode.UP;
            else if (input == KeyCode.S) input = KeyCode.DOWN;
            else if (input == KeyCode.A) input = KeyCode.LEFT;
            else if (input == KeyCode.D) input = KeyCode.RIGHT;
        }
        if (input == expected) { handleStepResult(true); } 
        else { handleStepResult(false); }
    }
    private boolean isDirectionKey(KeyCode code) {
        return code == KeyCode.UP || code == KeyCode.DOWN || code == KeyCode.LEFT || code == KeyCode.RIGHT ||
               code == KeyCode.W || code == KeyCode.S || code == KeyCode.A || code == KeyCode.D;
    }
    private void prepareTurn(Hero user, List<Hero> targets) {
        if (selectedSkill instanceof BasicAttack) {
            executeTurn(user, targets, 1.0, 0);
            return;
        }
        startMinigame(user, targets);
    }
    private void startMinigame(Hero user, List<Hero> targets) {
        currentState = GameState.CASTING_MINIGAME;
        this.pendingUser = user;
        this.pendingTargets = targets;
        currentSequence = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int r = random.nextInt(4);
            if(r==0) currentSequence.add(KeyCode.UP);
            else if(r==1) currentSequence.add(KeyCode.DOWN);
            else if(r==2) currentSequence.add(KeyCode.LEFT);
            else currentSequence.add(KeyCode.RIGHT);
        }
        castingPane.setVisible(true);
        castingStatusLabel.setText(currentPlayer.getName() + " CASTING...");
        castingIndicatorsBox.getChildren().clear();
        for(int i=0; i<4; i++) {
            Circle dot = new Circle(10, Color.GRAY);
            castingIndicatorsBox.getChildren().add(dot);
        }
        currentStepIndex = 0;
        successfulHits = 0;
        MusicManager.getInstance().playClickSound();
        showNextArrow();
    }
    private void showNextArrow() {
        if (currentStepIndex >= 4) { finishMinigame(); return; }
        KeyCode code = currentSequence.get(currentStepIndex);
        castingArrowLabel.setText(getArrowSymbol(code));
        castingArrowLabel.setTextFill(Color.WHITE);
        castingCircle.setStroke(Color.WHITE);
        ScaleTransition st = new ScaleTransition(Duration.millis(200), castingArrowLabel);
        st.setFromX(0.5); st.setFromY(0.5); st.setToX(1.0); st.setToY(1.0);
        st.play();
        isStepActive = true;
        double arrowDuration = 0.7;
        castingTimerBar.setProgress(1.0);
        Timeline progressAnim = new Timeline(
            new KeyFrame(Duration.ZERO,  e -> castingTimerBar.setProgress(1.0)),
            new KeyFrame(Duration.seconds(arrowDuration), e -> castingTimerBar.setProgress(0.0))
        );
        progressAnim.play();
        if (arrowTimer != null) arrowTimer.stop();
        arrowTimer = new Timeline(new KeyFrame(Duration.seconds(arrowDuration), e -> { handleStepResult(false); }));
        arrowTimer.setCycleCount(1);
        arrowTimer.play();
    }
    private void handleStepResult(boolean success) {
        isStepActive = false;
        arrowTimer.stop();
        Circle indicator = (Circle) castingIndicatorsBox.getChildren().get(currentStepIndex);
        if (success) {
            successfulHits++;
            indicator.setFill(Color.LIMEGREEN);
            MusicManager.getInstance().playClickSound();
            ScaleTransition st = new ScaleTransition(Duration.millis(100), castingArrowLabel);
            st.setToX(1.5); st.setToY(1.5); st.setAutoReverse(true); st.setCycleCount(2);
            st.play();
        } else {
            indicator.setFill(Color.RED);
            castingArrowLabel.setTextFill(Color.RED);
            castingCircle.setStroke(Color.RED);
        }
        currentStepIndex++;
        PauseTransition pt = new PauseTransition(Duration.millis(200));
        pt.setOnFinished(e -> showNextArrow());
        pt.play();
    }
    private void finishMinigame() {
        castingPane.setVisible(false);
        double multiplier = 1.0;
        int durationBonus = 0;
        if (successfulHits == 4) {
            multiplier = 1.5; durationBonus = 1;
            BattleLogger.getInstance().log(">>> PERFECT CAST! (1.5x Effect & +1 Turn) <<<");
        } else if (successfulHits == 3) {
            multiplier = 1.0;
            BattleLogger.getInstance().log(">>> WEAK CAST (Normal Effect) <<<");
        } else { 
            multiplier = 0.0;
            BattleLogger.getInstance().log(">>> SPELL MISSED! <<<");
        }
        executeTurn(pendingUser, pendingTargets, multiplier, durationBonus);
    }
    private String getArrowSymbol(KeyCode code) {
        switch (code) {
            case UP: return "⬆"; case DOWN: return "⬇"; case LEFT: return "⬅"; case RIGHT: return "➡️"; default: return "?";
        }
    }

    private void executeTurn(Hero user, List<Hero> targets, double multiplier, int durationBonus) {
        currentState = GameState.BUSY;
        skillGrid.setDisable(true);
        backButton.setVisible(false);
        clearAllHighlights();
        battleLogArea.setText(""); 
        log(user.getName() + " menggunakan " + selectedSkill.getName() + "!");
        
        HeroCardController userCard = getCardForHero(user);
        if (userCard != null) {
            userCard.playOneShotAnimation(selectedSkill.getName()); 
        }
        
        Player opponentPlayer = (currentPlayer == player1) ? player2 : player1;
        TargetType targetType = getSkillTargetType(selectedSkill);
        
        if ((targetType == TargetType.ALL_ALLIES || targetType == TargetType.ALL_ENEMIES)) {
            try {
                java.lang.reflect.Method method = selectedSkill.getClass().getMethod("useAOE", Hero.class, List.class, Player.class);
                method.invoke(selectedSkill, user, targets, currentPlayer);
            } catch (Exception e) {
                try {
                    if (selectedSkill instanceof skill.AoeSkill) ((skill.AoeSkill) selectedSkill).useAOE(user, targets);
                    else for(Hero t : targets) selectedSkill.use(user, t);
                } catch (Exception ex) { System.err.println("Gagal panggil useAOE: " + ex.getMessage()); }
            }
        } else {
            try {
                java.lang.reflect.Method method = selectedSkill.getClass().getMethod("use", Hero.class, Hero.class, Player.class);
                method.invoke(selectedSkill, user, targets.get(0), currentPlayer);
            } catch (NoSuchMethodException e) {
                try { selectedSkill.use(user, targets.get(0)); } catch (Exception ex) { System.err.println("Gagal panggil 'use' standar: " + ex.getMessage()); }
            } catch (Exception e) { System.err.println("Gagal panggil 'use' custom: " + e.getMessage()); }
        }

        boolean isHeal = (selectedSkill instanceof HealSkill || selectedSkill instanceof HoTSkill || selectedSkill.getName().equals("Divine Tears"));
        for (Hero t : targets) {
            HeroCardController targetCard = getCardForHero(t);
            if (targetCard != null) {
                if (multiplier > 0) {
                    if (isHeal) {
                        targetCard.playOneShotAnimation("healed");
                    } else if (getSkillTargetType(selectedSkill) != TargetType.SELF) {
                        targetCard.playOneShotAnimation("getHit");
                    }
                }
            }
        }
        
        if (selectedSkill instanceof Ultimate) user.setUltimateBar(0);
        else if (!(selectedSkill instanceof skill.BasicAttack)) {
            user.setCurrentEnergy(user.getCurrentEnergy() - selectedSkill.getEnergyCost());
            selectedSkill.startCooldown();
            user.gainUltimateBar(20);
        }
        
        for (Hero h : currentPlayer.getTeam()) h.gainEnergy(10);
        BattleLogger.getInstance().log(currentPlayer.getName() + "'s team gained 10 energy!");

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

    private void onSkillButtonClicked(Skill skill) {
        MusicManager.getInstance().playClickSound();
        if (currentState != GameState.SELECTING_SKILL) return;
        this.selectedSkill = skill;
        TargetType targetType = getSkillTargetType(skill);
        if (targetType == TargetType.SELF) {
            prepareTurn(selectedHero, List.of(selectedHero));
        } else if (targetType == TargetType.ALL_ALLIES || targetType == TargetType.ALL_ENEMIES) {
            List<Hero> targets = new ArrayList<>(); 
            if (targetType == TargetType.ALL_ALLIES) targets.addAll(currentPlayer.getAliveHeroes());
            else targets.addAll(((currentPlayer == player1) ? player2 : player1).getAliveHeroes());
            prepareTurn(selectedHero, targets);
        } else {
            currentState = GameState.SELECTING_TARGET;
            log(skill.getName() + " dipilih. Pilih target!");
            highlightTargetOptions(targetType);
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
                    if (clickedHero.isStunned()) { log(clickedHero.getName() + " sedang STUN!"); return; }
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
                if (selectedHero.isTaunted() && (requiredType == TargetType.SINGLE_ENEMY || requiredType == TargetType.ALL_ENEMIES)) {
                    String taunterName = "";
                    for(StatusEffect ef : selectedHero.getActiveEffects()) {
                        if(ef.getType() == StatusEffect.Type.TAUNT) { taunterName = ef.getSource(); break; }
                    }
                    if (!clickedHero.getName().equals(taunterName)) {
                        log(selectedHero.getName() + " terprovokasi! Harus menyerang " + taunterName + "!");
                        return;
                    }
                }
                if (selectedSkill.getName().equals("Divine Tears")) {
                    if (currentPlayerCards.contains(clickedCard) && clickedHero.isDead()) isValidTarget = true;
                    else if (currentPlayerCards.contains(clickedCard) && clickedHero.isAlive()) log(clickedHero.getName() + " masih hidup!");
                    else log("Hanya bisa me-revive kawan!");
                } else {
                    if (clickedHero.isDead()) { log(clickedHero.getName() + " sudah mati!"); return; }
                    if ( (requiredType == TargetType.SINGLE_ENEMY || requiredType == TargetType.ALL_ENEMIES) && opponentPlayerCards.contains(clickedCard) ) isValidTarget = true;
                    else if ( (requiredType == TargetType.SINGLE_ALLY || requiredType == TargetType.ALL_ALLIES) && currentPlayerCards.contains(clickedCard) ) isValidTarget = true;
                    else if ( requiredType == TargetType.SELF && clickedHero == selectedHero) isValidTarget = true;
                }
                if (isValidTarget) {
                    prepareTurn(selectedHero, List.of(clickedHero));
                } else if (!selectedSkill.getName().equals("Divine Tears")) log("Target tidak valid!");
                break;
            case SELECTING_SKILL:
                if (clickedHero.isDead()) return;
                if (currentPlayerCards.contains(clickedCard) && clickedHero != selectedHero) {
                    if (clickedHero.isStunned()) return;
                    this.selectedHero = clickedHero;
                    populateSkillGrid(selectedHero);
                    clearAllHighlights();
                    clickedCard.highlight("#2ecc71");
                    log(selectedHero.getName() + " dipilih. Silakan pilih skill.");
                }
                break;
        }
    }

    private void switchTurn() {
        selectedHero = null; selectedSkill = null;
        skillGrid.getChildren().clear(); skillGrid.setDisable(true);
        updateAllHeroCards(); 
        updateAllPersistentAnimations();
        if (currentPlayer == player1) { currentPlayer = player2; log("Giliran Player 2. Pilih hero yang akan beraksi."); } 
        else { currentPlayer = player1; log("Giliran Player 1. Pilih hero yang akan beraksi."); }
        currentState = GameState.SELECTING_HERO;
        List<Hero> aliveHeroes = currentPlayer.getAliveHeroes();
        if (aliveHeroes.isEmpty()) return;
        boolean allStunned = true;
        for (Hero h : aliveHeroes) { if (!h.isStunned()) { allStunned = false; break; } }
        if (allStunned) {
            log(currentPlayer.getName() + " tidak bisa bergerak! (Stun)");
            BattleLogger.getInstance().log("--- Giliran " + currentPlayer.getName() + " dilewati (Stun) ---");
            new Thread(() -> { try { Thread.sleep(1500); } catch (InterruptedException e) {} Platform.runLater(this::skipTurn); }).start();
        }
    }
    
    private void skipTurn() {
        currentState = GameState.BUSY; skillGrid.setDisable(true); backButton.setVisible(false); clearAllHighlights();
        log(currentPlayer.getName() + " melewatkan giliran..."); battleLogArea.setText("");
        for (Hero h : currentPlayer.getTeam()) h.gainEnergy(10); 
        player1.getTeam().forEach(h -> { if (h.isAlive()) h.updateStatusEffects(); });
        player2.getTeam().forEach(h -> { if (h.isAlive()) h.updateStatusEffects(); });
        currentPlayer.getTeam().forEach(Hero::reduceStatusEffectDurations);
        currentPlayer.getTeam().forEach(h -> h.getSkills().forEach(Skill::reduceCooldown));
        updateAllHeroCards();
        updateAllPersistentAnimations();
        if (!player1.isTeamAlive() || !player2.isTeamAlive()) {
            endGame();
            return;
        }
        new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException e) {}
            Platform.runLater(this::switchTurn);
        }).start();
    }
    
    private void endGame() {
        currentState = GameState.GAME_OVER; controlPanel.setDisable(true); backButton.setVisible(false);
        Player winner = player1.isTeamAlive() ? player1 : player2;
        log(winner.getName() + " MENANG!"); winnerLabel.setText(winner.getName() + " MENANG!");
        gameOverPane.setVisible(true); FadeTransition ft = new FadeTransition(Duration.millis(500), gameOverPane);
        ft.setFromValue(0.0); ft.setToValue(1.0); ft.play();
    }
    @FXML private void onRematchClicked() throws IOException {
        MusicManager.getInstance().playClickSound(); Parent battleRoot = FXMLLoader.load(getClass().getResource("battle.fxml")); rootStackPane.getScene().setRoot(battleRoot);
    }
    @FXML private void onExitToMenuClicked() throws IOException { 
        MusicManager.getInstance().playClickSound(); MusicManager.getInstance().stopBattleMusic();
        Parent menuRoot = FXMLLoader.load(getClass().getResource("main_menu.fxml")); rootStackPane.getScene().setRoot(menuRoot);
    }
    @FXML private void onBackButtonClicked() {
        MusicManager.getInstance().playClickSound();
        if (currentState == GameState.SELECTING_SKILL) {
            currentState = GameState.SELECTING_HERO; log("Pilih hero yang akan beraksi.");
            skillGrid.getChildren().clear(); skillGrid.setDisable(true);
            backButton.setVisible(false); clearAllHighlights(); selectedHero = null;
        } else if (currentState == GameState.SELECTING_TARGET) {
            currentState = GameState.SELECTING_SKILL; log(selectedHero.getName() + " dipilih. Silakan pilih skill.");
            clearAllHighlights(); highlightHero(selectedHero, "#2ecc71"); selectedSkill = null;
        }
    }
    
    private void populateSkillGrid(Hero hero) {
        skillGrid.getChildren().clear(); skillGrid.setDisable(false);
        List<Skill> skills = hero.getSkills();
        for (int i = 0; i < skills.size(); i++) {
            Skill skill = skills.get(i);
            Button skillButton = new Button(skill.getName());
            skillButton.setPrefSize(130, 60); skillButton.setWrapText(true);
            String reason = ""; boolean isSkillReady = false;
            if (skill instanceof Ultimate) {
                skillButton.setText(skill.getName() + "\n(ULTIMATE)");
                if (hero.getUltimateBar() >= 100) {
                    if (skill.getName().equals("Divine Tears")) {
                        if (currentPlayer.getDeadHeros().isEmpty()) { isSkillReady = false; reason = "(Tidak ada kawan mati)"; } else { isSkillReady = true; }
                    } else { isSkillReady = true; }
                } else { isSkillReady = false; reason = "(Ulti: " + hero.getUltimateBar() + "%)"; }
            } else {
                skillButton.setText(skill.getName() + "\n(Cost: " + skill.getEnergyCost() + ")");
                boolean isReady = skill.isReady(); boolean hasEnergy = hero.getCurrentEnergy() >= skill.getEnergyCost();
                if (isReady && hasEnergy) { isSkillReady = true; } else { isSkillReady = false; reason = !isReady ? " (CD: " + skill.getCurrentCooldown() + ")" : " (No Energy)"; }
            }
            if (isSkillReady) {
                skillButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                skillButton.setOnAction(e -> onSkillButtonClicked(skill));
            } else {
                skillButton.setDisable(true); skillButton.setText(skill.getName() + "\n" + reason);
                skillButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: #bdc3c7;");
            }
            skillGrid.add(skillButton, i % 2, i / 2);
        }
    }
    
    private void updateAllHeroCards() { for (HeroCardController c: player1Cards) c.updateData(c.getHero()); for (HeroCardController c: player2Cards) c.updateData(c.getHero()); }
    private void updateAllPersistentAnimations() { for (HeroCardController c: player1Cards) c.updatePersistentAnimation(); for (HeroCardController c: player2Cards) c.updatePersistentAnimation(); }
    private HeroCardController getCardForHero(Hero hero) { return Stream.concat(player1Cards.stream(), player2Cards.stream()).filter(card -> card.getHero() == hero).findFirst().orElse(null); }
    private void resetAllAnimationsToIdle() { /* Method ini tidak terpakai lagi, diganti updateAllPersistentAnimations */ }
    private void highlightHero(Hero hero, String color) { getCardForHero(hero).highlight(color); }
    private void highlightTargetOptions(TargetType targetType) {
        clearAllHighlights(); highlightHero(selectedHero, "#2ecc71"); String targetColor = "#e74c3c";
        List<HeroCardController> allies = (currentPlayer == player1) ? player1Cards : player2Cards;
        List<HeroCardController> enemies = (currentPlayer == player1) ? player2Cards : player1Cards;
        if (selectedSkill.getName().equals("Divine Tears")) allies.forEach(card -> { if (card.getHero().isDead()) card.highlight(targetColor); });
        else if (targetType == TargetType.SINGLE_ALLY) allies.forEach(card -> { if (card.getHero() != selectedHero && card.getHero().isAlive()) card.highlight(targetColor); });
        else if (targetType == TargetType.SINGLE_ENEMY) enemies.forEach(card -> { if (card.getHero().isAlive()) card.highlight(targetColor); });
    }
    private void clearAllHighlights() { player1Cards.forEach(HeroCardController::clearHighlight); player2Cards.forEach(HeroCardController::clearHighlight); }
    private TargetType getSkillTargetType(Skill skill) { if (skill instanceof SkillWithTargetType) return ((SkillWithTargetType) skill).getTargetType(); return TargetType.SINGLE_ENEMY; }
    private void log(String message) { announcementLabel.setText(message); System.out.println("[Announcement] " + message); }
    @Override public void onNewLog(String message) { battleLogArea.appendText("  > " + message + "\n"); }
}