package ui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MusicManager {

    private static final MusicManager instance = new MusicManager();

    private MediaPlayer menuMusicPlayer;
    private MediaPlayer battleMusicPlayer;
    private MediaPlayer sfxPlayer;
    private Media clickSound;

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
        if (menuMusicPlayer != null) {
            menuMusicPlayer.stop();
        }
    }

    public void playBattleMusic() {
        if (menuMusicPlayer != null) menuMusicPlayer.stop();
        if (battleMusicPlayer != null && battleMusicPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            battleMusicPlayer.play();
        }
    }

    public void stopBattleMusic() {
        if (battleMusicPlayer != null) {
            battleMusicPlayer.stop();
        }
    }

    public void playClickSound() {
        if (sfxPlayer != null) {
            sfxPlayer.stop();
            sfxPlayer.play();
        }
    }
}