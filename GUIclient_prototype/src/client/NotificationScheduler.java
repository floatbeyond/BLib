package client;

import java.util.Timer;
import java.util.TimerTask;

/**
 * NotificationScheduler class is responsible for scheduling the notification fetching task.
 * This class is responsible for starting and stopping the notification fetching task.
 */
public class NotificationScheduler {

    private static final long FETCH_INTERVAL = 60000; // 1 minute
    private static Timer timer;

    /**
     * This method starts the notification fetching task.
     *
     * @param user The user.
     * @param userId The user ID.
     */
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

    /**
     * This method stops the notification fetching task.
     */
    public static void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}