package ui;

import game.Hero;
import game.StatusEffect;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class HeroCardController {

    @FXML private VBox rootBox;
    @FXML private Label heroNameLabel;
    @FXML private ImageView heroImageView;
    @FXML private HBox statusBox;
    @FXML private ProgressBar hpBar;
    @FXML private ProgressBar energyBar;
    @FXML private ProgressBar ultBar;
    @FXML private Label hpLabel;
    @FXML private Label energyLabel;
    @FXML private Label ultLabel;
    @FXML private Label atkLabel;
    @FXML private Label defLabel;
    @FXML private Label critChanceLabel;
    @FXML private Label critDmgLabel;

    private Hero hero;
    private String currentIdleBase = "idle";
    private String currentDirectionSuffix = "_right";
    private String currentPersistentAnimationBase = "idle";
    private PauseTransition oneShotTimer;

    public void updateData(Hero hero) {
        this.hero = hero;

        statusBox.getChildren().clear();
        for (StatusEffect effect : hero.getActiveEffects()) {
            String text = "";
            String style = "-fx-padding: 2 4 2 4; -fx-background-radius: 3; -fx-background-color: ";
            style += "-fx-font-size: 9px; -fx-font-weight: bold; ";
            switch (effect.getType()) {
                case SHIELD: text = "SHIELD (" + effect.getValue() + ")"; style += "#3498db;"; break;
                case BUFF: text = "BUFF" + (effect.getAttribute() != null ? ":" + effect.getAttribute().toString().substring(0,3) : "") + " (" + effect.getDuration() + "t)"; style += "#2ecc71;"; break;
                case DEBUFF: text = "DEBUFF" + (effect.getAttribute() != null ? ":" + effect.getAttribute().toString().substring(0,3) : "") + " (" + effect.getDuration() + "t)"; style += "#e74c3c;"; break;
                case STUN: text = "STUN (" + effect.getDuration() + "t)"; style += "#f1c40f;"; break;
                case TAUNT: text = "TAUNT (" + effect.getDuration() + "t)"; style += "#9b59b6;"; break;
                case DOT:
                case POISON: text = effect.getType().toString() + " (" + effect.getDuration() + "t)"; style += "#e74c3c;"; break;
                case HEAL_OVER_TIME: text = "HOT (" + effect.getDuration() + "t)"; style += "#2ecc71;"; break;
                default: text = effect.getType().toString() + " (" + effect.getDuration() + "t)"; style += "#95a5a6;";
            }
            Label effectLabel = new Label(text);
            effectLabel.setStyle(style);
            effectLabel.setTextFill(Color.WHITE);
            statusBox.getChildren().add(effectLabel);
        }

        if (hero.isDead()) {
            heroNameLabel.setText(hero.getName() + " (DEAD)");
            hpBar.setProgress(0);
            energyBar.setProgress(0);
            ultBar.setProgress((double) hero.getUltimateBar() / 100);
            rootBox.setOpacity(0.5);
            hpLabel.setText("0 / " + hero.getMaxHP());
            energyLabel.setText("0 / " + hero.getMaxEnergy());
            ultLabel.setText(hero.getUltimateBar() + " / 100");
            atkLabel.setText(String.valueOf(hero.getBaseAttack()));
            defLabel.setText(String.valueOf(hero.getBaseDefense()));
            critChanceLabel.setText("0%");
            critDmgLabel.setText("0%");
        } else {
            heroNameLabel.setText(hero.getName());
            hpBar.setProgress((double) hero.getCurrentHP() / hero.getMaxHP());
            energyBar.setProgress((double) hero.getCurrentEnergy() / hero.getMaxEnergy());
            ultBar.setProgress((double) hero.getUltimateBar() / 100);
            rootBox.setOpacity(1.0);
            hpLabel.setText(hero.getCurrentHP() + " / " + hero.getMaxHP());
            energyLabel.setText(hero.getCurrentEnergy() + " / " + hero.getMaxEnergy());
            ultLabel.setText(hero.getUltimateBar() + " / 100");
            atkLabel.setText(String.valueOf(hero.getAttackPower()));
            defLabel.setText(String.valueOf(hero.getDefense()));
            critChanceLabel.setText(hero.getCritChance() + "%");
            critDmgLabel.setText(hero.getCritDamage() + "%");
        }
    }

    public void setIdleAnimation(String idleName) {
        if (idleName.endsWith("_left")) {
            this.currentDirectionSuffix = "_left";
        } else if (idleName.endsWith("_right")) {
            this.currentDirectionSuffix = "_right";
        }
        this.currentIdleBase = "idle";
        this.currentPersistentAnimationBase = "idle";
    }

    private void playAnimation(String animNameBase) {
        if (this.hero == null) return;
        if (hero.isDead()) {
            heroImageView.setImage(null);
            return;
        }

        String heroFolder = hero.getName().toLowerCase().replace(" ", "_");
        String fullAnimName = animNameBase + currentDirectionSuffix;
        String path = String.format("/images/%s/%s.gif", heroFolder, fullAnimName);

        try {
            Image newGif = new Image(getClass().getResourceAsStream(path));
            heroImageView.setImage(newGif);
        } catch (Exception e) {
            System.err.println("Gagal load GIF: " + path + ". Memutar Idle.");
            String idlePath = String.format("/images/%s/%s%s.gif", heroFolder, currentIdleBase, currentDirectionSuffix);
            try {
                Image idleGif = new Image(getClass().getResourceAsStream(idlePath));
                heroImageView.setImage(idleGif);
            } catch (Exception e2) {
                System.err.println("Gagal total load GIF: " + idlePath);
                heroImageView.setImage(null);
            }
        }
    }

    public void playOneShotAnimation(String animNameBase) {
        if (oneShotTimer != null) {
            oneShotTimer.stop();
        }

        playAnimation(animNameBase);

        oneShotTimer = new PauseTransition(Duration.seconds(1.0));
        oneShotTimer.setOnFinished(e -> {
            playAnimation(this.currentPersistentAnimationBase);
            oneShotTimer = null;
        });
        oneShotTimer.play();
    }

    public void updatePersistentAnimation() {
        if (hero == null) return;
        if (hero.isDead()) {
            currentPersistentAnimationBase = "dead";
        } else if (hero.isStunned()) {
            currentPersistentAnimationBase = "stun";
        } else if (hero.isBuffed()) {
            currentPersistentAnimationBase = "buff";
        } else if (hero.isDebuffed()) {
            currentPersistentAnimationBase = "debuff";
        } else {
            currentPersistentAnimationBase = currentIdleBase;
        }

        if (oneShotTimer == null) {
            playAnimation(currentPersistentAnimationBase);
        }
    }

    public Hero getHero() { return this.hero; }
    public VBox getRoot() { return this.rootBox; }

    public void highlight(String color) {
        rootBox.setStyle(
            "-fx-background-color: rgba(0, 0, 0, 0.7); -fx-background-radius: 10;" +
            "-fx-padding: 10; -fx-border-radius: 10;" +
            "-fx-border-width: 3; -fx-border-color: " + color + ";"
        );
    }

    public void clearHighlight() {
        rootBox.setStyle(
            "-fx-background-color: rgba(0, 0, 0, 0.7); -fx-background-radius: 10;" +
            "-fx-padding: 10; -fx-border-radius: 10;" +
            "-fx-border-width: 1; -fx-border-color: transparent;"
        );
    }
}