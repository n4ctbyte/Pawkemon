package ui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MusicManager {

    private static final MusicManager instance = new MusicManager();

    private MediaPlayer menuMusicPlayer;
    private MediaPlayer battleMusicPlayer;
    private MediaPlayer sfxPlayer;
    private MediaPlayer logoSfxPlayer;
    private MediaPlayer bounceSfxPlayer;
    private MediaPlayer bgRevealSfxPlayer;

    private Media clickSound;
    private Media logoSound;
    private Media bounceSound;
    private Media bgRevealSound;

    private MusicManager() {
        try {
            String menuPath = getClass().getResource("/sounds/menu_music.mp3").toURI().toString();
            Media menuMedia = new Media(menuPath);
            menuMusicPlayer = new MediaPlayer(menuMedia);
            menuMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            menuMusicPlayer.setOnEndOfMedia(() -> {
                menuMusicPlayer.seek(Duration.ZERO);
                menuMusicPlayer.play();
            });
            menuMusicPlayer.setVolume(0.3);
        } catch (Exception e) {
            System.err.println("Gagal memuat /sounds/menu_music.mp3.");
        }

        try {
            String battlePath = getClass().getResource("/sounds/battle_music.mp3").toURI().toString();
            Media battleMedia = new Media(battlePath);
            battleMusicPlayer = new MediaPlayer(battleMedia);
            battleMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            battleMusicPlayer.setOnEndOfMedia(() -> {
                battleMusicPlayer.seek(Duration.ZERO);
                battleMusicPlayer.play();
            });
            battleMusicPlayer.setVolume(0.3);
        } catch (Exception e) {
            System.err.println("Gagal memuat /sounds/battle_music.mp3.");
        }

        try {
            String clickPath = getClass().getResource("/sounds/click.mp3").toURI().toString();
            clickSound = new Media(clickPath);
            sfxPlayer = new MediaPlayer(clickSound);
            sfxPlayer.setVolume(1.0);
        } catch (Exception e) {
            System.err.println("Gagal memuat /sounds/click.mp3.");
        }

        try {
            String logoSfxPath = getClass().getResource("/sounds/logo_sfx.mp3").toURI().toString();
            logoSound = new Media(logoSfxPath);
            logoSfxPlayer = new MediaPlayer(logoSound);
            logoSfxPlayer.setVolume(1.0);
        } catch (Exception e) {
            System.err.println("Gagal memuat /sounds/logo_sfx.mp3.");
        }

        try {
            String bounceSfxPath = getClass().getResource("/sounds/landing.mp3").toURI().toString();
            bounceSound = new Media(bounceSfxPath);
            bounceSfxPlayer = new MediaPlayer(bounceSound);
            bounceSfxPlayer.setVolume(1.0);
        } catch (Exception e) {
            System.err.println("Gagal memuat /sounds/bounce_sfx.mp3.");
        }

        try {
            String bgRevealSfxPath = getClass().getResource("/sounds/bg_reveal.mp3").toURI().toString();
            bgRevealSound = new Media(bgRevealSfxPath);
            bgRevealSfxPlayer = new MediaPlayer(bgRevealSound);
            bgRevealSfxPlayer.setVolume(0.8);
        } catch (Exception e) {
            System.err.println("Gagal memuat /sounds/bg_reveal_sfx.mp3.");
        }
    }

    public static MusicManager getInstance() {
        return instance;
    }

    public void playMenuMusic() {
        if (battleMusicPlayer != null) battleMusicPlayer.stop();
        if (menuMusicPlayer != null && menuMusicPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            menuMusicPlayer.play();
        }
    }

    public void stopMenuMusic() {
        if (menuMusicPlayer != null) menuMusicPlayer.stop();
    }

    public void playBattleMusic() {
        if (menuMusicPlayer != null) menuMusicPlayer.stop();
        if (battleMusicPlayer != null && battleMusicPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            battleMusicPlayer.play();
        }
    }

    public void stopBattleMusic() {
        if (battleMusicPlayer != null) battleMusicPlayer.stop();
    }

    public void playClickSound() {
        if (sfxPlayer != null) {
            sfxPlayer.stop();
            sfxPlayer.play();
        }
    }

    public void playLogoSound() {
        if (logoSfxPlayer != null) {
            logoSfxPlayer.stop();
            logoSfxPlayer.play();
        }
    }

    public void playBounceSound() {
        if (bounceSfxPlayer != null) {
            bounceSfxPlayer.stop();
            bounceSfxPlayer.play();
        }
    }

    public void playBgRevealSound() {
        if (bgRevealSfxPlayer != null) {
            bgRevealSfxPlayer.stop();
            bgRevealSfxPlayer.play();
        }
    }
}