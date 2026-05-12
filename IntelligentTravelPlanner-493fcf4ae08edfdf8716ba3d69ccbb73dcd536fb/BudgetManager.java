import java.util.Map;
import java.util.LinkedHashMap;
public class BudgetManager {
        private final Trip trip;

        public BudgetManager(Trip t) { trip = t; }

        public double totalSpent()  { return trip.totalSpent(); }
        public double remaining()   { return trip.remaining(); }
        public double percentUsed() { return (totalSpent() / trip.getBudget()) * 100; }

        public void addExpense(Expense e) throws BudgetExceededException {
            trip.addExpense(e);
            if (totalSpent() > trip.getBudget()) {
                double over = totalSpent() - trip.getBudget();
                throw new BudgetExceededException(
                    String.format("⚠ Budget exceeded by Rs.%,.2f! Consider revising your plan.", over));
            }
        }

        public String statusLabel() {
            double p = percentUsed();
            if (p >= 100) return "  OVER BUDGET  (" + (int) p + "% used)";
            if (p >=  80) return "  WARNING — "   + (int) p + "% used";
            if (p >=  50) return "  MODERATE — "  + (int) p + "% used";
            return                "  ON TRACK — "  + (int) p + "% used";
        }

        public Map<String, Double> byCategory() {
            Map<String, Double> map = new LinkedHashMap<>();
            for (Expense e : trip.getExpenses())
                map.merge(e.getCategory(), e.getAmount(), Double::sum);
            return map;
        }
    }
