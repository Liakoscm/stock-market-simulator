package symposium;

import java.io.FileWriter;
import java.io.IOException;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

public class InvestmentExperiment {

    private static final double INITIAL_BALANCE = 0;
    private static final double MONTHLY_CONTRIBUTION = 1000;
    private static final double STOCK_ALLOCATION = 0.80;
    private static final double FIXED_ALLOCATION = 0.20;
    private static final double FIXED_YIELD = 2.0;
    private static final double ANNUAL_INCREASE = 0;
    private static final int[] YEARS = {10, 15, 20, 25, 30, 35, 40};
    private static final double[] ALLOCATION_PERCENTS = {100, 80, 60, 40, 20, 0};
    private static final double[] INITIAL_AMOUNTS = {0, 10_000, 20_000, 50_000, 100_000};
    private static final double[] YIELDS = {0, 2, 4, 6, 8, 10};
    private static final double[] MONTHLY_CONTRIBUTIONS = {500, 1000, 1500, 2000, 2500, 3000};
    private static final double[] INCREASES = {0, 2, 4, 6, 8, 10};
    private static final int RUNS = 10;
    private static final int YEAR_SPAN = 30;

    public static void main(String[] args) {
        try (FileWriter writer = new FileWriter("investment_results.txt")) {
            // Write the headers for each section
            writer.write("Variable\tFinal Balance\tTotal Contribution\tAverage Monthly Contribution\n");

            // Allocation Experiment
            runAllocationExperiment(writer);

            // Time Experiment
            runTimeExperiment(writer);

            // Contribution Experiment
            runContributionExperiment(writer);

            // Increase Experiment
            runIncreaseExperiment(writer);

            // Initial Balance Experiment
            runInitialBalanceExperiment(writer);

            // Yield Experiment
            runYieldExperiment(writer);

            // Glide Experiment
            runGlideExperiment(writer);

            // Special Time Ranges Experiment
            runSpecialExperiment(writer);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runAllocationExperiment(FileWriter writer) throws IOException {
        writer.write("\nTesting Allocation:\n");
        for (double stockPercent : ALLOCATION_PERCENTS) {
            double finalBalance = 0;
            double totalContribution = 0;
            double avgMonthlyContribution = 0;
            for (int i = 0; i < RUNS; i++) {
                // Run simulator for iteration
                InvestSimulator sim = new InvestSimulator(INITIAL_BALANCE, MONTHLY_CONTRIBUTION, stockPercent / 100, stockPercent / 100, FIXED_YIELD, ANNUAL_INCREASE, YearMonth.of(1985 + i, 5), YearMonth.of(2015 + i, 5));
                sim.runSimulation();

                // Tally up sums
                finalBalance += sim.getFinalAmount();
                totalContribution += sim.getTotalContribution();
            }

            // Averaging values
            finalBalance /= RUNS;
            totalContribution /= RUNS;
            avgMonthlyContribution = totalContribution / (12 * YEAR_SPAN); // (MONTH * YEAR SPAN)

            // Write the variable along with results
            writer.write(String.format("%.2f\t%.2f\t%.2f\t%.2f%n", stockPercent, finalBalance, totalContribution, avgMonthlyContribution));
        }
    }

    private static void runTimeExperiment(FileWriter writer) throws IOException {
        writer.write("\nTesting Time:\n");
        for (int years : YEARS) {
            double finalBalance = 0;
            double totalContribution = 0;
            double avgMonthlyContribution = 0;
            int validRuns = 0;

            if (years < 40) {
                for (int startYear = 1985; startYear + years <= 2024; startYear++) {
                    // Run simulator for iteration
                    InvestSimulator sim = new InvestSimulator(INITIAL_BALANCE, MONTHLY_CONTRIBUTION, STOCK_ALLOCATION, STOCK_ALLOCATION, FIXED_YIELD, ANNUAL_INCREASE, YearMonth.of(startYear, 5), YearMonth.of(startYear + years, 5));
                    sim.runSimulation();

                    // Tally up sums
                    finalBalance += sim.getFinalAmount();
                    totalContribution += sim.getTotalContribution();
                    validRuns++;
                }

                // Averaging values
                finalBalance /= validRuns;
                totalContribution /= validRuns;
                avgMonthlyContribution = totalContribution / (12 * years); // (MONTH * YEAR SPAN)

            } else {
                // Define simulation start and end dates
                YearMonth startDate = YearMonth.of(1985, 5);
                YearMonth endDate = YearMonth.of(2024, 12);

                // Calculate the number of months between the start and end dates
                int totalMonths = (int) ChronoUnit.MONTHS.between(startDate, endDate);

                // Run simulator for iteration
                var sim = new InvestSimulator(INITIAL_BALANCE, MONTHLY_CONTRIBUTION, STOCK_ALLOCATION, STOCK_ALLOCATION, FIXED_YIELD, ANNUAL_INCREASE, startDate, endDate);
                sim.runSimulation();

                // Retrieve the results from the simulation
                finalBalance = sim.getFinalAmount();
                totalContribution = sim.getTotalContribution();
                avgMonthlyContribution = totalContribution / totalMonths; // Use dynamic totalMonths

            }

            // Write the variable along with results
            writer.write(String.format("%d\t%.2f\t%.2f\t%.2f%n", years, finalBalance, totalContribution, avgMonthlyContribution));
        }
    }

    private static void runContributionExperiment(FileWriter writer) throws IOException {
        writer.write("\nTesting Monthly Contribution:\n");
        for (double monthly : MONTHLY_CONTRIBUTIONS) {
            double finalBalance = 0;
            double totalContribution = 0;
            double avgMonthlyContribution = 0;

            for (int i = 0; i < RUNS; i++) {
                // Run simulator for iteration
                InvestSimulator sim = new InvestSimulator(INITIAL_BALANCE, monthly, STOCK_ALLOCATION, STOCK_ALLOCATION, FIXED_YIELD, ANNUAL_INCREASE, YearMonth.of(1985 + i, 5), YearMonth.of(2015 + i, 5));
                sim.runSimulation();

                // Tally up sums
                finalBalance += sim.getFinalAmount();
                totalContribution += sim.getTotalContribution();
            }

            // Averaging values
            finalBalance /= RUNS;
            totalContribution /= RUNS;
            avgMonthlyContribution = totalContribution / (12 * YEAR_SPAN); // (MONTH * YEAR SPAN)

            // Write the variable along with results
            writer.write(String.format("%.2f\t%.2f\t%.2f\t%.2f%n", monthly, finalBalance, totalContribution, avgMonthlyContribution));
        }
    }

    private static void runIncreaseExperiment(FileWriter writer) throws IOException {
        writer.write("\nTesting Annual Increase:\n");
        for (double increase : INCREASES) {
            double finalBalance = 0;
            double totalContribution = 0;
            double avgMonthlyContribution = 0;

            for (int i = 0; i < RUNS; i++) {
                // Run simulator for iteration
                InvestSimulator sim = new InvestSimulator(INITIAL_BALANCE, MONTHLY_CONTRIBUTION, STOCK_ALLOCATION, STOCK_ALLOCATION, FIXED_YIELD, increase, YearMonth.of(1985 + i, 5), YearMonth.of(2015 + i, 5));
                sim.runSimulation();

                // Tally up sums
                finalBalance += sim.getFinalAmount();
                totalContribution += sim.getTotalContribution();
            }

            // Averaging values
            finalBalance /= RUNS;
            totalContribution /= RUNS;
            avgMonthlyContribution = totalContribution / (12 * YEAR_SPAN); // (MONTH * YEAR SPAN)

            // Write the variable along with results
            writer.write(String.format("%.2f\t%.2f\t%.2f\t%.2f%n", increase, finalBalance, totalContribution, avgMonthlyContribution));
        }
    }

    private static void runInitialBalanceExperiment(FileWriter writer) throws IOException {
        writer.write("\nTesting Initial Balance:\n");
        for (double initial : INITIAL_AMOUNTS) {
            double finalBalance = 0;
            double totalContribution = 0;
            double avgMonthlyContribution = 0;

            for (int i = 0; i < RUNS; i++) {
                // Run simulator for iteration
                InvestSimulator sim = new InvestSimulator(initial, MONTHLY_CONTRIBUTION, STOCK_ALLOCATION, STOCK_ALLOCATION, FIXED_YIELD, ANNUAL_INCREASE, YearMonth.of(1985 + i, 5), YearMonth.of(2015 + i, 5));
                sim.runSimulation();

                // Tally up sums
                finalBalance += sim.getFinalAmount();
                totalContribution += sim.getTotalContribution();
            }

            // Averaging values
            finalBalance /= RUNS;
            totalContribution /= RUNS;
            avgMonthlyContribution = totalContribution / (12 * YEAR_SPAN); // (MONTH * YEAR SPAN)

            // Write the variable along with results
            writer.write(String.format("%.2f\t%.2f\t%.2f\t%.2f%n", initial, finalBalance, totalContribution, avgMonthlyContribution));
        }
    }

    private static void runYieldExperiment(FileWriter writer) throws IOException {
        writer.write("\nTesting Yield:\n");
        for (double yield : YIELDS) {
            double finalBalance = 0;
            double totalContribution = 0;
            double avgMonthlyContribution = 0;

            for (int i = 0; i < RUNS; i++) {
                // Run simulator for iteration
                InvestSimulator sim = new InvestSimulator(INITIAL_BALANCE, MONTHLY_CONTRIBUTION, STOCK_ALLOCATION, STOCK_ALLOCATION, yield, ANNUAL_INCREASE, YearMonth.of(1985 + i, 5), YearMonth.of(2015 + i, 5));
                sim.runSimulation();

                // Tally up sums
                finalBalance += sim.getFinalAmount();
                totalContribution += sim.getTotalContribution();
            }

            // Averaging values
            finalBalance /= RUNS;
            totalContribution /= RUNS;
            avgMonthlyContribution = totalContribution / (12 * YEAR_SPAN); // (MONTH * YEAR SPAN)

            // Write the variable along with results
            writer.write(String.format("%.2f\t%.2f\t%.2f\t%.2f%n", yield, finalBalance, totalContribution, avgMonthlyContribution));
        }
    }

    private static void runGlideExperiment(FileWriter writer) throws IOException {
        writer.write("\nTesting Glide Path:\n");
        for (double eStock : ALLOCATION_PERCENTS) {
            double finalBalance = 0;
            double totalContribution = 0;
            double avgMonthlyContribution = 0;

            for (int i = 0; i < RUNS; i++) {
                // Run simulator for iteration
                InvestSimulator sim = new InvestSimulator(INITIAL_BALANCE, MONTHLY_CONTRIBUTION, 0.60, eStock / 100, FIXED_YIELD, ANNUAL_INCREASE, YearMonth.of(1985 + i, 5), YearMonth.of(2015 + i, 5));
                sim.runSimulation();

                // Tally up sums
                finalBalance += sim.getFinalAmount();
                totalContribution += sim.getTotalContribution();
            }

            // Averaging values
            finalBalance /= RUNS;
            totalContribution /= RUNS;
            avgMonthlyContribution = totalContribution / (12 * YEAR_SPAN); // (MONTH * YEAR SPAN)

            // Write the variable along with results
            writer.write(String.format("%.2f\t%.2f\t%.2f\t%.2f%n", eStock, finalBalance, totalContribution, avgMonthlyContribution));
        }
    }

    private static void runSpecialExperiment(FileWriter writer) throws IOException {
        writer.write("\nTesting Special Periods:\n");

        // Define start and end dates for special periods
        YearMonth[] startDates = {YearMonth.of(2000, 5), YearMonth.of(2000, 5),
                YearMonth.of(2007, 5), YearMonth.of(2009, 5)};
        YearMonth[] endDates = {YearMonth.of(2012, 5), YearMonth.of(2009, 5),
                YearMonth.of(2017, 5), YearMonth.of(2019, 5)};

        // Loop over each special period
        for (int i = 0; i < startDates.length; i++) {
            // Calculate YEAR_SPAN dynamically
            int yearSpan = endDates[i].getYear() - startDates[i].getYear();

            // Simulate investment for the current special period
            InvestSimulator sim = new InvestSimulator(INITIAL_BALANCE, MONTHLY_CONTRIBUTION, STOCK_ALLOCATION, STOCK_ALLOCATION, FIXED_YIELD, ANNUAL_INCREASE, startDates[i], endDates[i]);
            sim.runSimulation();

            // Retrieve the results from the simulation
            double finalBalance = sim.getFinalAmount();
            double totalContribution = sim.getTotalContribution();
            double avgMonthlyContribution = totalContribution / (12 * yearSpan); // (MONTH * YEAR SPAN)

            // Write the results to the file, formatted to two decimal places
            writer.write(String.format("%s - %s\t%.2f\t%.2f\t%.2f%n", startDates[i], endDates[i], finalBalance, totalContribution, avgMonthlyContribution));
        }
    }
}






