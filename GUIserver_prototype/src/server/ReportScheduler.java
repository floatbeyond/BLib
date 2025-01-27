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

/**
 * The ReportScheduler class is responsible for scheduling and generating monthly reports.
 * It ensures that reports are generated for past months and schedules future report generation.
 */
public class ReportScheduler {
    private ScheduledExecutorService scheduler;
    private static final Logger logger = Logger.getLogger(ReportScheduler.class.getName());
    
    /**
     * Constructs a ReportScheduler instance.
     * Generates missing reports for past months and schedules future report generation.
     */
    public ReportScheduler() {
        // Generate missing reports for past months up to current
        generateMissingMonthlyReports();
        scheduleMonthlyReportGeneration();
    }

    /**
     * Generates missing monthly reports from a specified start date to the last month.
     * Checks for existing reports and generates only the missing ones.
     */
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
    
    /**
     * Schedules the generation of monthly reports on the first day of each month.
     * Uses a ScheduledExecutorService to schedule the task.
     */
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

    /**
     * Stops the scheduler and shuts down the executor service.
     */
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