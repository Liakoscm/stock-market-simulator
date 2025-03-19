package symposium;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
public class ParentSimulator {
    double stockBalance;
    double fixedBalance;
    double startAlloc;
    double endAlloc;
    double allocation;
    double annualIncrease;
    double fixedYield;
    double dividend = 0.02;

    YearMonth currentDate;
    YearMonth endDate;
    StockData stockData;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");

    List<YearMonth> dates = new ArrayList<>();
    List<Double> stockBalances = new ArrayList<>();
    List<Double> fixedBalances = new ArrayList<>();

    int totalMonths;
    int currentMonth = 0;

    public ParentSimulator(double startAmount, double startAlloc, double endAlloc,
                           double fixedYield, double annualIncrease, YearMonth startDate, YearMonth endDate) {

        this.startAlloc = startAlloc;
        this.endAlloc = endAlloc;
        this.stockBalance = startAmount * startAlloc;
        this.fixedBalance = startAmount * (1 - startAlloc);
        this.allocation = startAlloc; // Initial allocation for the first month
        this.annualIncrease = annualIncrease;
        this.fixedYield = fixedYield;
        this.currentDate = startDate;
        this.endDate = endDate;
        this.totalMonths = (endDate.getYear() - currentDate.getYear()) * 12 + endDate.getMonthValue() - currentDate.getMonthValue();
        this.stockData = new StockData();
    }

    // Increment month
    public void nextMonth() {
        currentDate = currentDate.plusMonths(1);
        currentMonth++;
    }

    // Add stock market change
    public void applyStockMarket(double percentChange) {
        stockBalance *= (1 + percentChange / 100);
    }

    // Add dividend when current month is March, June, September, or December
    public void dividend() {
        if (currentDate.getMonthValue() == 3 || currentDate.getMonthValue() == 6 ||
                currentDate.getMonthValue() == 9 || currentDate.getMonthValue() == 12) {
            stockBalance *= (1 + dividend / 4);  // Quarterly dividend
        }
    }

    // Increase fixed income based on interest rate
    public void applyFixedIncomeYield() {
        fixedBalance *= (1 + (fixedYield / 12 / 100));
    }

    // Adjust allocation
    public void adjustAllocation() {
        double progress = (double) currentMonth / totalMonths;
        allocation = startAlloc + progress * (endAlloc - startAlloc);
    }


    // Rebalance fixed income and investments according to current allocation
    public void rebalance() {
        double total = stockBalance + fixedBalance;
        stockBalance = total * allocation;
        fixedBalance = total * (1 - allocation);
        System.out.println("Rebalanced at " + currentDate.format(formatter));
    }
}






