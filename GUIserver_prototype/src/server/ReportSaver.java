package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import common.BorrowReport;
import common.SubscriberReport;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The ReportSaver class is responsible for saving and loading monthly reports.
 * It handles the serialization and deserialization of reports to and from JSON files.
 */
public class ReportSaver {
    private static final String REPORTS_DIR = "reports/";
    private static final Gson gson = new Gson();

    static {
        try {
            File dir = new File(REPORTS_DIR);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    throw new RuntimeException("Failed to create reports directory");
                }
                System.out.println("Created reports directory at: " + dir.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize reports directory", e);
        }
    }

    /**
     * Saves the borrow and subscriber reports for a given month and year.
     * In the format YYYY-MM, the reports are saved in the reports directory.
     *
     * @param borrowReport the list of borrow reports to save
     * @param subscriberReport the list of subscriber reports to save
     * @param monthYear the month and year in MM-yyyy format
     * @throws RuntimeException if an error occurs while saving the reports
     */
    public static void saveReports(List<BorrowReport> borrowReport, 
                                 List<SubscriberReport> subscriberReport, 
                                 String monthYear) {
        try {
            // Convert MM-YYYY to YYYY-MM
            String[] parts = monthYear.split("-");
            String formattedMonthYear = parts[1] + "-" + parts[0];

            System.out.println("Saving reports for " + formattedMonthYear);
            System.out.println("Borrow reports: " + borrowReport.size());
            System.out.println("Subscriber reports: " + subscriberReport.size());
            
            // Create month directory
            File monthDir = new File(REPORTS_DIR + formattedMonthYear);
            if (!monthDir.exists()) {
                boolean created = monthDir.mkdirs();
                if (!created) {
                    throw new IOException("Failed to create month directory: " + monthDir);
                }
            }
    
            // Save borrow report
            saveReport(borrowReport, monthDir.getPath() + "/borrow_report.json");
            
            // Save subscriber report  
            saveReport(subscriberReport, monthDir.getPath() + "/subscriber_report.json");
            
            System.out.println("Successfully saved reports for " + formattedMonthYear);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to save reports for " + monthYear, e); 
        }
    }

    /**
     * Saves a report to a specified file.
     *
     * @param report the report object to save
     * @param fileName the name of the file to save the report to
     * @throws RuntimeException if an error occurs while saving the report
     */
    private static void saveReport(Object report, String fileName) {
        try {
            String json = gson.toJson(report);
            Files.write(Paths.get(fileName), json.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to save report to " + fileName, e);
        }
    }

    /**
     * Retrieves a list of months for which reports are available.
     *
     * @return a list of month-year strings in yyyy-MM format
     */
    public static List<String> getReportMonths() {
        File reportsDir = new File(REPORTS_DIR);
        System.out.println("Checking directory: " + reportsDir.getAbsolutePath());
        
        if (!reportsDir.exists() || !reportsDir.isDirectory()) {
            System.out.println("Reports directory not found or not a directory");
            return new ArrayList<>();
        }
    
        // List directories only
        File[] monthDirs = reportsDir.listFiles(File::isDirectory);
        if (monthDirs == null) {
            System.out.println("No monthly report directories found");
            return new ArrayList<>();
        }
        
        System.out.println("Found directories: " + Arrays.toString(monthDirs));
        
        return Arrays.stream(monthDirs)
            .map(File::getName)
            // Verify directory contains reports
            .filter(dirname -> new File(REPORTS_DIR + "/" + dirname + "/borrow_report.json").exists())
            .sorted()
            .collect(Collectors.toList());
    }

    /**
     * Loads the borrow reports for a given month and year.
     *
     * @param monthYear the month and year in MM-yyyy format
     * @return a list of borrow reports
     * @throws RuntimeException if an error occurs while loading the reports
     */
    public static List<BorrowReport> loadBorrowReport(String monthYear) {
        Type reportType = new TypeToken<List<BorrowReport>>() {}.getType();
        String path = String.format("%s%s%s%sborrow_report.json", 
            REPORTS_DIR, File.separator, monthYear, File.separator);
        return loadReport(path, reportType);
    }

    /**
     * Loads the subscriber reports for a given month and year.
     *
     * @param monthYear the month and year in MM-yyyy format
     * @return a list of subscriber reports
     * @throws RuntimeException if an error occurs while loading the reports
     */    
    public static List<SubscriberReport> loadSubscriberReport(String monthYear) {
        Type reportType = new TypeToken<List<SubscriberReport>>() {}.getType();
        String path = String.format("%s%s%s%ssubscriber_report.json",
            REPORTS_DIR, File.separator, monthYear, File.separator);
        return loadReport(path, reportType);
    }
    
    /**
     * Loads a report from a specified file.
     *
     * @param fileName the name of the file to load the report from
     * @param reportType the type of the report to load
     * @param <T> the type of the report
     * @return the loaded report
     * @throws RuntimeException if an error occurs while loading the report
     */
    private static <T> T loadReport(String fileName, Type reportType) {
        try (Reader reader = new FileReader(fileName)) {
            return gson.fromJson(reader, reportType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load report from " + fileName, e);
        }
    }
}