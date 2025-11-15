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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color; 
import javafx.util.Duration;

public class HeroCardController {

    @FXML private StackPane shieldPane;
    @FXML private ProgressBar shieldBar;
    @FXML private Label shieldLabel;
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
    
    private boolean isMirrored = false;
    private String currentPersistentAnimationBase = "idle";
    private PauseTransition oneShotTimer; 
    
    private double maxShieldValue = 1; 

    public void updateData(Hero hero) {
        this.hero = hero;
        
        int shieldAmount = 0; 
        boolean justGotShielded = false; 
        
        statusBox.getChildren().clear();
        for (StatusEffect effect : hero.getActiveEffects()) {
            String text = "";
            String style = "-fx-padding: 2 4 2 4; -fx-background-radius: 3; -fx-background-color: ";
            style += "-fx-font-size: 9px; -fx-font-weight: bold; ";
            
            switch (effect.getType()) {
                case SHIELD:
                    shieldAmount += effect.getValue();
                    text = "SHIELD (" + effect.getValue() + ")";
                    style += "#3498db;";
                    if (effect.getDuration() == 4 || effect.getDuration() == 2) { 
                        maxShieldValue = Math.max(maxShieldValue, effect.getValue());
                        justGotShielded = true;
                    }
                    break;
                case BUFF:
                    String attrStrB = "";
                    if (effect.getAttribute() != null) {
                        switch (effect.getAttribute()) {
                            case ATTACK: attrStrB = "ATK"; break;
                            case DEFENSE: attrStrB = "DEF"; break;
                            case CRIT_CHANCE: attrStrB = "CR"; break;
                            case CRIT_DAMAGE: attrStrB = "CD"; break;
                            case DODGE_CHANCE: attrStrB = "DODGE"; break;
                            default: attrStrB = "";
                        }
                    }
                    text = String.format("BUFF %s(%d, %d)", attrStrB, effect.getValue(), effect.getDuration());
                    style += "#2ecc71;";
                    break;
                case DEBUFF:
                    String attrStrD = "";
                    if (effect.getAttribute() != null) {
                        switch (effect.getAttribute()) {
                            case ATTACK: attrStrD = "ATK"; break;
                            case DEFENSE: attrStrD = "DEF"; break;
                            default: attrStrD = "";
                        }
                    }
                    text = String.format("DEB %s(%d, %d)", attrStrD, effect.getValue(), effect.getDuration());
                    style += "#e74c3c;";
                    break;
                case STUN: text = String.format("STUN(%d)", effect.getDuration()); style += "#f1c40f;"; break;
                case TAUNT: text = String.format("TAUNT(%d)", effect.getDuration()); style += "#9b59b6;"; break;
                case DOT: text = String.format("DOT(%d, %d)", effect.getValue(), effect.getDuration()); style += "#e74c3c;"; break;
                case POISON: text = String.format("POISON(%d, %d)", effect.getValue(), effect.getDuration()); style += "#e74c3c;"; break;
                case HEAL_OVER_TIME: text = String.format("HOT(%d, %d)", effect.getValue(), effect.getDuration()); style += "#2ecc71;"; break;
                default: text = effect.getType().toString() + "(" + effect.getDuration() + ")"; style += "#95a5a6;";
            }
            Label effectLabel = new Label(text);
            effectLabel.setStyle(style); 
            effectLabel.setTextFill(Color.WHITE); 
            statusBox.getChildren().add(effectLabel);
        }
        
        if (shieldAmount > 0) {
            shieldPane.setVisible(true);
            if (justGotShielded) { maxShieldValue = Math.max(maxShieldValue, shieldAmount); }
            shieldBar.setProgress((double) shieldAmount / maxShieldValue);
            shieldLabel.setText(String.valueOf(shieldAmount));
        } else {
            shieldPane.setVisible(false);
            maxShieldValue = 1;
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
            shieldPane.setVisible(false);
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

    public void setMirrored(boolean mirrored) {
        this.isMirrored = mirrored;
        heroImageView.setScaleX(isMirrored ? -1 : 1);
    }

    private void playAnimation(String animName) {
        if (this.hero == null) return;
        
        heroImageView.setScaleX(isMirrored ? -1 : 1);

        if (hero.isDead()) {
            heroImageView.setImage(null);
            return;
        }

        String heroFolder = hero.getName().toLowerCase().replace(" ", "_");
        
        String animFile = animName.toLowerCase().replace(" ", "_") + ".gif";
        String path = String.format("/images/%s/%s", heroFolder, animFile);

        try {
            Image newGif = new Image(getClass().getResourceAsStream(path));
            heroImageView.setImage(newGif);
        } catch (Exception e) {
            String idlePath = String.format("/images/%s/idle.gif", heroFolder);
            try {
                Image idleGif = new Image(getClass().getResourceAsStream(idlePath));
                heroImageView.setImage(idleGif);
            } catch (Exception e2) {
                System.err.println("Gagal total load GIF: " + path + " ATAU " + idlePath);
                heroImageView.setImage(null);
            }
        }
    }

    public void playOneShotAnimation(String animFileName) {
        if (oneShotTimer != null) {
            oneShotTimer.stop();
        }
        
        playAnimation(animFileName); 
        
        oneShotTimer = new PauseTransition(Duration.seconds(1.0)); 
        
        oneShotTimer.setOnFinished(e -> {
            oneShotTimer = null;
            updatePersistentAnimation();
        });
        
        oneShotTimer.play();
    }
    
    public void showAnimation(String animationName) {
        playOneShotAnimation(animationName);
    }

    public void updatePersistentAnimation() {
        if (hero == null) return;
        
        String animBaseName;
        
        if (hero.isDead()) {
            animBaseName = "dead";
        } else if (hero.isStunned()) {
            animBaseName = "stun";
        } else if (hero.isTaunted()) {
            animBaseName = "taunt";
        } else if (hero.isBuffed()) {
            animBaseName = "buffed";
        } else if (hero.isDebuffed()) {
            animBaseName = "debuffed";
        } else {
            animBaseName = "idle";
        }

        if (oneShotTimer == null) {
            playAnimation(animBaseName);
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