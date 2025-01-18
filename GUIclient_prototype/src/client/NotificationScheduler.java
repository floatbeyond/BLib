package client;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationScheduler {

    private static final long FETCH_INTERVAL = 60000; // 1 minute

    public static void start(int subId) {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Logic.fetchNotifications(subId);
            }
        }, 0, FETCH_INTERVAL);
    }
}