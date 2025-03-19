package symposium;

import java.awt.*;
import java.time.YearMonth;
import java.util.List;
import javax.swing.*;

public class InvestSimulator extends ParentSimulator {

    double contribution;
    private double totalContribution = 0;

    public InvestSimulator(double startAmount, double monthlyContribution, double startAlloc, double endAlloc,
                           double fixedYield, double annualIncrease, YearMonth startDate, YearMonth endDate) {

        super(startAmount, startAlloc, endAlloc, fixedYield, annualIncrease, startDate, endDate);

        contribution = monthlyContribution;
    }

    // Divide contribution and split it to stock and fixed income according to current allocation
    public void allocateContribution() {
        double stockContribution = contribution * allocation;
        double fixedContribution = contribution * (1 - allocation);

        stockBalance += stockContribution;
        fixedBalance += fixedContribution;
    }

    // Increase annual contribution
    public void increaseContribution() {
        contribution *= (1 + annualIncrease / 100);
        //contribution = contribution + contribution * annualIncrease / 100;
    }

    // Print formatted monthly update of values
    public void printValues() {
        System.out.printf("Date: %s | Stock Balance: %.2f | Fixed Balance: %.2f | Allocation: %.2f/%.2f | Contribution: %.2f | Total: %.2f%n",
                currentDate.format(formatter), stockBalance, fixedBalance, allocation * 100, (1 - allocation) * 100, contribution, fixedBalance + stockBalance);
    }

    // Run simulation
    public void runSimulation() {

        // Setup to be run once
        adjustAllocation();
        rebalance();
        allocateContribution();
        //printValues();
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

            // Track the total contribution
            totalContribution += contribution;

            applyStockMarket(percentChange);
            // Actions to be performed every January
            if (currentDate.getMonthValue() == 1) {
                adjustAllocation();
                rebalance();
                increaseContribution();
            }

            // Actions to be performed every month
            dividend(); // Checks month in method
            applyFixedIncomeYield();
            allocateContribution();

            // Update logged data
            dates.add(currentDate);
            stockBalances.add(stockBalance);
            fixedBalances.add(fixedBalance);

            //printValues();
            nextMonth();
        }

        // Rebalance neatly for the end
        allocation = endAlloc;
        rebalance();
        //printValues();

        // Display graph
        //SwingUtilities.invokeLater(this::displayChart);
    }

    public double getFinalAmount() {
        return stockBalance + fixedBalance;
    }

    public double getTotalContribution() {
        return totalContribution;
    }

    private void displayChart() {
        JFrame frame = new JFrame("Investment Growth");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new GraphPanel(dates, stockBalances, fixedBalances));
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
            if (numBars == 0) {
                return;
            }

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






