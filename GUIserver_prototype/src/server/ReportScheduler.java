package server;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ReportScheduler {
    private ScheduledExecutorService scheduler;
    private static final Logger logger = Logger.getLogger(ReportScheduler.class.getName());
    
    public ReportScheduler() {
        // Generate missing reports for past months up to current
        generateMissingMonthlyReports();
        scheduleMonthlyReportGeneration();
    }

    private void generateMissingMonthlyReports() {
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);
        List<String> existingReports = ReportSaver.getReportMonths();
        
        while (startDate.isBefore(now) && !startDate.isAfter(lastMonth)) {
            // Changed format from MM-yyyy to yyyy-MM
            String monthYear = startDate.format(DateTimeFormatter.ofPattern("MM-yyyy"));
            
            if (!existingReports.contains(monthYear)) {
                logger.info("Generating reports for: " + monthYear);
                try {
                    Logic.generateMonthlyReports(startDate);
                } catch (Exception e) {
                    logger.severe("Failed to generate reports for " + monthYear + ": " + e.getMessage());
                }
            }
            
            startDate = startDate.plusMonths(1);
        }
    }
    
    private void scheduleMonthlyReportGeneration() {
        scheduler = Executors.newScheduledThreadPool(1);
        LocalDate now = LocalDate.now();
        LocalDate firstOfNextMonth = now.with(TemporalAdjusters.firstDayOfNextMonth());
        
        long initialDelay = ChronoUnit.MILLIS.between(
            now.atStartOfDay(), 
            firstOfNextMonth.atStartOfDay()
        );
    
        scheduler.scheduleAtFixedRate(
            () -> {
                LocalDate reportDate = LocalDate.now().minusMonths(1);
                Logic.generateMonthlyReports(reportDate);
            },
            initialDelay,
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.MILLISECONDS
        );
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