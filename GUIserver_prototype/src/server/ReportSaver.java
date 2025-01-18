package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import common.BorrowTimeReport;
import common.SubscriberStatusReport;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReportSaver {
    private static final String REPORTS_DIR = "reports/";
    private static final Gson gson = new Gson();

    static {
        new File(REPORTS_DIR).mkdirs(); // Create reports directory if not exists
    }

    public static void saveReports(List<BorrowTimeReport> borrowTimesReport, List<SubscriberStatusReport> subscriberStatusReport, String monthYear) {
        saveReport(borrowTimesReport, REPORTS_DIR + "borrowTimesReport_" + monthYear + ".json");
        saveReport(subscriberStatusReport, REPORTS_DIR + "subscriberStatusReport_" + monthYear + ".json");
    }

    private static void saveReport(Object report, String fileName) {
        try (Writer writer = new FileWriter(fileName)) {
            gson.toJson(report, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getReportMonths() {
        File reportsDir = new File(REPORTS_DIR);
        String[] files = reportsDir.list((dir, name) -> name.startsWith("borrowTimesReport_"));
        if (files == null) return new ArrayList<>();
        
        return Arrays.stream(files)
            .map(filename -> filename.replace("borrowTimesReport_", "").replace(".json", ""))
            .collect(Collectors.toList());
    }

    public static List<BorrowTimeReport> loadBorrowTimesReport(String monthYear) {
        Type reportType = new TypeToken<List<BorrowTimeReport>>() {}.getType();
        return loadReport(REPORTS_DIR + "borrowTimesReport_" + monthYear + ".json", reportType);
    }

    public static List<SubscriberStatusReport> loadSubscriberStatusReport(String monthYear) {
        Type reportType = new TypeToken<List<SubscriberStatusReport>>() {}.getType();
        return loadReport(REPORTS_DIR + "subscriberStatusReport_" + monthYear + ".json", reportType);
    }

    private static <T> T loadReport(String fileName, Type reportType) {
        try (Reader reader = new FileReader(fileName)) {
            return gson.fromJson(reader, reportType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}