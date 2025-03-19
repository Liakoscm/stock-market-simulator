//Code written by: Chris Liakos, Gabe Karras, and Rick Butler for the 2025 Eastern Michigan University Undergraduate Symposium

package symposium;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        Scanner scanner = new Scanner(System.in);

        // Ask the user to choose which simulations
        System.out.println("Choose a simulation to run (1-3)");
        System.out.println("1. Accumulation");
        System.out.println("2. Withdrawal");
        System.out.println("3. Both");
        System.out.print("Enter choice: ");

        int simulationChoice;
        do {
            simulationChoice = getInteger(scanner);
        } while (simulationChoice < 1 || simulationChoice > 3);

        scanner.nextLine(); // Eat new line character

        // Ask for parameters
        System.out.print("Enter start date (MM/YYYY): ");
        YearMonth startDate = getYearMonth(formatter, scanner);

        System.out.print("Enter end date (MM/YYYY): ");
        // Checking if end date comes after start
        YearMonth endDate;
        do {
            endDate = getYearMonth(formatter, scanner);

            if (endDate.isBefore(startDate)) {
                System.out.println("Invalid input. End date must be after the start date. Please re-enter end date.");
            }
        } while (endDate.isBefore(startDate));

        switch (simulationChoice) {
            case 3: {
                System.out.print("Enter starting amount: $");
                double startAmount = getDouble(scanner);
                System.out.print("Enter monthly contribution: $");
                double monthlyContribution = getDouble(scanner);
                System.out.print("Enter start stock allocation (percentage): ");
                double startStockAlloc = getDouble(scanner);
                System.out.print("Enter end stock allocation (percentage): ");
                double endStockAlloc = getDouble(scanner);
                System.out.print("Enter fixed income yearly yield (percentage): ");
                double fixedYield = getDouble(scanner);
                System.out.print("Enter annual contribution increase (percentage): ");
                double annualIncrease = getDouble(scanner);

                InvestSimulator investSim = new InvestSimulator(startAmount, monthlyContribution, startStockAlloc / 100.0, endStockAlloc / 100.0, fixedYield, annualIncrease, startDate, endDate);
                investSim.runSimulation();

                // Get the final amount from InvestSimulator
                double finalAmount = investSim.getFinalAmount();

                // End date after investment period for deallocation
                System.out.print("Enter end date of deallocation (MM/YYYY): ");
                scanner.nextLine(); // Eat new line character
                YearMonth endWithdrawlDate;
                do {
                    endWithdrawlDate = getYearMonth(formatter, scanner);

                    if (endWithdrawlDate.isBefore(endDate)) {
                        System.out.println("Invalid input. End date must be after the end date of investment period. Please re-enter end date.");
                    }
                } while (endWithdrawlDate.isBefore(endDate));

                // Gather withdrawal parameters
                System.out.print("Enter annual withdrawal percentage: ");
                double annualWithdrawal = getAllocation(scanner);
                System.out.print("Enter start withdrawal stock allocation (percentage): ");
                double withdrawStartStock = getDouble(scanner);
                System.out.print("Enter end withdrawal stock allocation (percentage): ");
                double withdrawEndStock = getDouble(scanner);
                System.out.print("Enter annual withdrawal increase (percentage): ");
                double withdrawIncrease = getDouble(scanner);

                // Run withdrawal simulation
                WithdrawSimulator withdrawSim = new WithdrawSimulator(finalAmount, annualWithdrawal / 100, withdrawStartStock / 100.0, withdrawEndStock / 100.0, fixedYield, withdrawIncrease, endDate, endWithdrawlDate);
                withdrawSim.runSimulation();
                break;
            }
            case 1: {
                System.out.print("Enter starting amount: $");
                double startAmount = getDouble(scanner);
                System.out.print("Enter monthly contribution: $");
                double monthlyContribution = getDouble(scanner);
                System.out.print("Enter start stock allocation (percentage): ");
                double startStockAlloc = getDouble(scanner);
                System.out.print("Enter end stock allocation (percentage): ");
                double endStockAlloc = getDouble(scanner);
                System.out.print("Enter fixed income yearly yield (percentage): ");
                double fixedYield = getDouble(scanner);
                System.out.print("Enter annual contribution increase (percentage): ");
                double annualIncrease = getDouble(scanner);
                InvestSimulator investSim = new InvestSimulator(startAmount, monthlyContribution, startStockAlloc / 100.0, endStockAlloc / 100.0, fixedYield, annualIncrease, startDate, endDate);
                investSim.runSimulation();
                break;
            }
            default: {
                System.out.print("Enter starting amount from investments: $");
                double finalAmount = getDouble(scanner);
                System.out.print("Enter annual withdrawal (percentage): ");
                double annualWithdrawal = getAllocation(scanner);
                System.out.print("Enter start withdrawal stock allocation (percentage): ");
                double startStockAlloc = getDouble(scanner);
                System.out.print("Enter end withdrawal stock allocation (percentage): ");
                double endStockAlloc = getDouble(scanner);
                System.out.print("Enter fixed income yearly yield (percentage): ");
                double fixedYield = getDouble(scanner);
                System.out.print("Enter annual withdrawal increase (percentage): ");
                double withdrawIncrease = getDouble(scanner);

                // Run withdrawal simulation
                WithdrawSimulator withdrawSim = new WithdrawSimulator(finalAmount, annualWithdrawal / 100.0, startStockAlloc / 100.0, endStockAlloc / 100.0, fixedYield, withdrawIncrease, startDate, endDate);
                withdrawSim.runSimulation();
                break;
            }
        }
        scanner.close();
    }

    // Gets a double value with error checking
    private static double getDouble(Scanner scanner) {
        double value;
        while (true) {
            try {
                value = scanner.nextDouble();
                return value;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume invalid input
            }
        }
    }

    // Gets a double value with error checking
    private static int getInteger(Scanner scanner) {
        int value;
        while (true) {
            try {
                value = scanner.nextInt();
                return value;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine(); // Consume invalid input
            }
        }
    }

    //check to make sure startalloc and endalloc are valid and not over 100
    private static double getAllocation(Scanner scanner) {
        double value;
        while (true) {
            try {
                value = scanner.nextDouble();
                if (value < 0 || value >= 100) {
                    System.out.println("Invalid input. Please enter a number between 0 and 100.");
                } else {
                    return value;
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume invalid input
            }
        }
    }

    // Gets a year/month combo with error checking
    private static YearMonth getYearMonth(DateTimeFormatter formatter, Scanner scanner) {
        YearMonth formattedDate;
        final YearMonth MIN_DATE = YearMonth.of(1985, 1);
        final YearMonth MAX_DATE = YearMonth.of(2024, 12);

        do {
            String dateString = scanner.nextLine();
            formattedDate = parseYearMonth(dateString, formatter);

            if (formattedDate != null) {
                if (formattedDate.isBefore(MIN_DATE) || formattedDate.isAfter(MAX_DATE)) {
                    System.out.println("Invalid date. Please enter a date between 01/1985 and 12/2024.");
                    formattedDate = null;
                }
            }
        } while (formattedDate == null);

        return formattedDate;
    }

    private static YearMonth parseYearMonth(String dateString, DateTimeFormatter formatter) {
        try {
            return YearMonth.parse(dateString, formatter);
        } catch (java.time.format.DateTimeParseException e) {
            System.out.println("Invalid date format. Please use MM/YYYY.");
            return null;
        }
    }

}




