package game;

import javafx.application.Platform;

public class BattleLogger {

    private static final BattleLogger instance = new BattleLogger();
    
    private BattleLogger() {}

    public static BattleLogger getInstance() {
        return instance;
    }

    public interface LogListener {
        void onNewLog(String message);
    }

    private LogListener listener = null;

    public void setListener(LogListener listener) {
        this.listener = listener;
    }

    public void log(String message) {
        if (listener != null) {
            Platform.runLater(() -> listener.onNewLog(message));
        }
        System.out.println("[BattleLogger] " + message);
    }
}
