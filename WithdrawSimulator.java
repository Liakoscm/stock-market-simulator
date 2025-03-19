package symposium;

import java.awt.*;
import java.time.YearMonth;
import java.util.List;
import javax.swing.*;

public class WithdrawSimulator extends ParentSimulator {
    double withdrawal;
    double withdrawnTotal = 0;

    public WithdrawSimulator(double startAmount, double annualWithdrawalPercentage, double startAlloc, double endAlloc,
                             double fixedYield, double annualIncrease, YearMonth startDate, YearMonth endDate) {

        super(startAmount, startAlloc, endAlloc, fixedYield, annualIncrease, startDate, endDate);

        withdrawal = annualWithdrawalPercentage / 12;
    }

    // Figure out what to take from based on allocation
    public void allocateWithdrawal() {
        double realTimeAllocation = stockBalance / (stockBalance + fixedBalance);
        double realTimeWithdrawal = withdrawal * (stockBalance + fixedBalance);

        if (realTimeAllocation < allocation)      // Not enough stock
            fixedBalance -= realTimeWithdrawal;
        else if (realTimeAllocation > allocation) // Not enough fixed
            stockBalance -= realTimeWithdrawal;
        else {                                    // Perfect
            fixedBalance -= realTimeWithdrawal * (1 - allocation);
            stockBalance -= realTimeWithdrawal * allocation;
        }

        withdrawnTotal += realTimeWithdrawal;
    }

    // Increase annual withdrawal
    public void increaseWithdrawal() {
        withdrawal *= (1 + annualIncrease / 100);
    }

    // Print formatted monthly update of values
    public void printValues() {
        System.out.printf("Date: %s | Stock: %.2f | Fixed: %.2f | Allocation: %.2f/%.2f | Withdrawal: %.4f | Saved Total: %.2f | Total Withdrawn: %.2f%n",
                currentDate.format(formatter), stockBalance, fixedBalance, allocation * 100, (1 - allocation) * 100, withdrawal, fixedBalance + stockBalance, withdrawnTotal);
    }

    // Gets called if both stock and fixed balances are in debt, repays it with the withdrawn total
    public void bankruptcy() {
        double debt = -1 * (fixedBalance + stockBalance);

        withdrawnTotal -= debt;

        fixedBalance = 0;
        stockBalance = 0;
    }


    // Run simulation
    public void runSimulation() {
        // Setup to be run once
        adjustAllocation();
        rebalance();
        allocateWithdrawal();
        printValues();
        nextMonth();

        // Main simulation loop
        while (currentDate.isBefore(endDate) || currentDate.equals(endDate)) {
            YearMonth previousMonth = currentDate.minusMonths(1);
            double percentChange = 0;
            try {
                // Calculate the percent change in stock price for the current month
                percentChange = stockData.calculatePercentChange(currentDate, previousMonth);
            } catch (IllegalArgumentException e) {
                System.out.println("Stock data not available for the specified months.");
                break;
            }

            applyStockMarket(percentChange);

            // Actions to be performed every January
            if (currentDate.getMonthValue() == 1) {
                adjustAllocation();
                rebalance();
                increaseWithdrawal();
            }

            // Actions to be performed every month
            dividend(); // Checks month in method
            applyFixedIncomeYield();
            allocateWithdrawal();

            // Check for bankruptcy if something goes below zero
            if (fixedBalance < 0 || stockBalance < 0) {
                // First, try to emergency rebalance and fix the issue
                rebalance();

                // If both are in debt, shut down
                if (fixedBalance < 0 && stockBalance < 0) {
                    bankruptcy();
                    System.out.println("Out of money!");
                    break;
                }
            }
            // Update logged data
            dates.add(currentDate);
            stockBalances.add(stockBalance);
            fixedBalances.add(fixedBalance);
            printValues();
            nextMonth();
        }
        // Rebalance neatly for the end
        allocation = endAlloc;
        rebalance();
        printValues();
        // Display graph
        //SwingUtilities.invokeLater(this::displayChart);
    }

    public double getFinalAmount() {
        return stockBalance + fixedBalance;
    }

    public double getTotalWithdrawal() {
        return withdrawnTotal;
    }

    private void displayChart() {
        JFrame frame = new JFrame("Investment Growth");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 1000);
        frame.add(new InvestSimulator.GraphPanel(dates, stockBalances, fixedBalances));
        frame.setVisible(true);
    }

    static class GraphPanel extends JPanel {
        List<YearMonth> dates;
        List<Double> stockBalances;
        List<Double> fixedBalances;

        GraphPanel(List<YearMonth> dates, List<Double> stockBalances, List<Double> fixedBalances) {
            this.dates = dates;
            this.stockBalances = stockBalances;
            this.fixedBalances = fixedBalances;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (dates.isEmpty() || stockBalances.isEmpty() || fixedBalances.isEmpty()) {
                return; // Prevent division by zero if no data exists
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 50;
            int graphWidth = width - 2 * padding;
            int graphHeight = height - 2 * padding;


            double maxBalance = stockBalances.stream().max(Double::compareTo).orElse(1.0) + fixedBalances.stream().max(Double::compareTo).orElse(1.0);

            int numBars = dates.size();
            if (numBars == 0) return;

            int barWidth = graphWidth / (numBars * 2);
            int spacing = barWidth / 2;

            g2.drawLine(padding, height - padding, padding + graphWidth, height - padding);
            g2.drawLine(padding, height - padding, padding, padding);

            for (int i = 0; i < numBars; i++) {
                int xStock = padding + i * (barWidth * 2 + spacing);
                int xFixed = xStock + barWidth;


                int stockBarHeight = (int) (((stockBalances.get(i) + fixedBalances.get(i)) / maxBalance) * graphHeight);



                g2.setColor(Color.BLUE);
                g2.fillRect(xStock, height - padding - stockBarHeight, barWidth, stockBarHeight);


            }

            // Legend
            g2.setColor(Color.BLUE);
            g2.drawString("Total", width - 150, 30);


        }

    }
}






