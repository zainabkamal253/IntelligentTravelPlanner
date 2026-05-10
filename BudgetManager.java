//BudgetManager handles all budget-related calculations for a trip.
//It tracks spending, remaining budget, and category-wise expenses.
import java.util.Map;
import java.util.LinkedHashMap;

public class BudgetManager {

        private final Trip trip;

        // constructor assigns the trip
        public BudgetManager(Trip t) {
                trip = t;
        }

        // total money spent so far
        public double totalSpent() {
                return trip.totalSpent();
        }

        // remaining budget
        public double remaining() {
                return trip.remaining();
        }

        // percentage of budget used
        public double percentUsed() {
                return (totalSpent() / trip.getBudget()) * 100;
        }

        // add new expense and check if budget is exceeded
        public void addExpense(Expense e) throws BudgetExceededException {
                trip.addExpense(e);

                if (totalSpent() > trip.getBudget()) {
                        double over = totalSpent() - trip.getBudget();
                        throw new BudgetExceededException(
                                String.format("Budget exceeded by Rs.%,.2f", over)
                        );
                }
        }

        // returns budget status message
        public String statusLabel() {
                double p = percentUsed();

                if (p >= 100) return "OVER BUDGET (" + (int) p + "% used)";
                if (p >= 80)  return "WARNING (" + (int) p + "% used)";
                if (p >= 50)  return "MODERATE (" + (int) p + "% used)";
                return "ON TRACK (" + (int) p + "% used)";
        }

        // returns spending grouped by category
        public Map<String, Double> byCategory() {
                Map<String, Double> map = new LinkedHashMap<>();

                for (Expense e : trip.getExpenses()) {
                        map.merge(e.getCategory(), e.getAmount(), Double::sum);
                }

                return map;
        }
}