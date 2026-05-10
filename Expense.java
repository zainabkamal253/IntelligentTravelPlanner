import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class Expense implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String category, note;
        private final double amount;
        private final LocalDate date;

        public Expense(String c, double a, String n) {
            category = c; amount = a; note = n; date = LocalDate.now();
        }
        public String    getCategory() { return category; }
        public double    getAmount()   { return amount; }
        public LocalDate getDate()     { return date; }
        public String    getNote()     { return note; }
        @Override public String toString() {
            return String.format("[%s]  %-12s  Rs.%,9.2f  %s",
                date.format(DateTimeFormatter.ofPattern("dd-MMM")),
                category, amount, note.isEmpty() ? "" : "— " + note);
        }
    }