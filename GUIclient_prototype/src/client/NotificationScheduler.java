package client;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationScheduler {

    private static final long FETCH_INTERVAL = 60000; // 1 minute
    private static Timer timer;

    public static void start(String user, int userId) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Logic.fetchNotifications(user, userId);
            }
        }, 0, FETCH_INTERVAL);
    }

    public static void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}