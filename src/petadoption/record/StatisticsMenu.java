package petadoption.record;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import petadoption.exception.PetAdoptionException;
import petadoption.ui.Menu;
import petadoption.util.ConsoleUI;
import petadoption.util.InputHelper;

/**
 * Admin statistics menu that selects and configures {@link StatisticsStrategy} at runtime:
 * 1 Monthly statistics · 2 Yearly statistics · 3 Back
 */
public class StatisticsMenu extends Menu {

    private final StatisticsService statisticsService = new StatisticsService();

    @Override
    protected String getTitle() {
        return "Adoption Statistics";
    }

    @Override
    protected String[] getOptions() {
        return new String[] {
            "Monthly statistics",
            "Yearly statistics",
            "Back"
        };
    }

    @Override
    protected boolean handleChoice(int choice) throws PetAdoptionException {
        switch (choice) {
            case 1 -> viewMonthlyStatistics();
            case 2 -> viewYearlyStatistics();
            case 3 -> {
                return false;
            }
            default -> ConsoleUI.printError("Unknown option.");
        }
        return true;
    }

    private void viewMonthlyStatistics() throws PetAdoptionException {
        ConsoleUI.printHeader("Monthly Adoption Statistics");

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        int year = InputHelper.readInt("Enter year (2000-" + currentYear + "): ", 2000, currentYear);
        int maxMonth = (year == currentYear) ? currentMonth : 12;
        int month = InputHelper.readInt("Enter month (1-" + maxMonth + "): ", 1, maxMonth);

        MonthlyStatisticsStrategy monthlyStrategy = new MonthlyStatisticsStrategy(year, month);
        statisticsService.setStrategy(monthlyStrategy);

        StatisticsReport report = statisticsService.generateReport();
        printMonthlyReport(report);
        InputHelper.pause();
    }

    private void viewYearlyStatistics() throws PetAdoptionException {
        ConsoleUI.printHeader("Yearly Adoption Statistics");

        int currentYear = LocalDate.now().getYear();
        int year = InputHelper.readInt("Enter year (2000-" + currentYear + "): ", 2000, currentYear);

        YearlyStatisticsStrategy yearlyStrategy = new YearlyStatisticsStrategy(year);
        statisticsService.setStrategy(yearlyStrategy);

        StatisticsReport report = statisticsService.generateReport();
        printYearlyReport(report);
        InputHelper.pause();
    }

    private void printMonthlyReport(StatisticsReport report) {
        ConsoleUI.printHeader("Statistics Report - " + report.periodLabel());
        ConsoleUI.printField("Period", report.periodLabel());
        ConsoleUI.printField("Applications Received", String.valueOf(report.applicationsReceived()));
        ConsoleUI.printField("Applications Approved", String.valueOf(report.approved()));
        ConsoleUI.printField("Applications Rejected", String.valueOf(report.rejected()));
        ConsoleUI.printField("Applications Cancelled", String.valueOf(report.cancelled()));
        ConsoleUI.printField("Applications Still Pending", String.valueOf(report.stillPending()));
        ConsoleUI.printField("Adoptions Completed", String.valueOf(report.adoptionsCompleted()));
        ConsoleUI.printDivider();
    }

    private void printYearlyReport(StatisticsReport report) {
        ConsoleUI.printHeader("Yearly Statistics Report - " + report.periodLabel());

        String[] headers = {"Month", "Received", "Approved", "Rejected", "Cancelled", "Pending", "Completed"};
        List<String[]> rows = new ArrayList<>();

        for (StatisticsReport monthReport : report.breakdown()) {
            rows.add(new String[] {
                monthReport.periodLabel(),
                String.valueOf(monthReport.applicationsReceived()),
                String.valueOf(monthReport.approved()),
                String.valueOf(monthReport.rejected()),
                String.valueOf(monthReport.cancelled()),
                String.valueOf(monthReport.stillPending()),
                String.valueOf(monthReport.adoptionsCompleted())
            });
        }

        rows.add(new String[] {
            "TOTAL " + report.periodLabel(),
            String.valueOf(report.applicationsReceived()),
            String.valueOf(report.approved()),
            String.valueOf(report.rejected()),
            String.valueOf(report.cancelled()),
            String.valueOf(report.stillPending()),
            String.valueOf(report.adoptionsCompleted())
        });

        ConsoleUI.printTable(headers, rows);
        ConsoleUI.printDivider();
    }
}
