package server;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReportScheduler {

    private ScheduledExecutorService scheduler;
    
    public ReportScheduler() {
        // Generate missing reports once during server initialization
        Logic.generateMissingReports();
        
        // Schedule future report generation
        scheduleMonthlyReportGeneration();
    }

    private void scheduleMonthlyReportGeneration() {
        scheduler = Executors.newScheduledThreadPool(1);

        LocalDate now = LocalDate.now();
        LocalDate firstOfNextMonth = now.with(TemporalAdjusters.firstDayOfNextMonth());
        long initialDelay = java.time.Duration.between(now.atStartOfDay(), firstOfNextMonth.atStartOfDay()).toMillis();

        scheduler.scheduleAtFixedRate(() -> {
            Logic.generateMonthlyReports();
        }, initialDelay, TimeUnit.DAYS.toMillis(30), TimeUnit.MILLISECONDS);
    }

    public void stopScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}