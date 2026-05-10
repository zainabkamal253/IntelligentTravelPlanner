import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// This class stores information about a single expense and can be saved to a file
public class Expense implements Serializable {
    // ID for serializing the class
    private static final long serialVersionUID = 1L;
    
    // Variables to hold the expense details
    private final String category, note;
    private final double amount;
    private final LocalDate date;

    // Constructor to set up the expense with category, amount, and a note
    public Expense(String c, double a, String n) {
        category = c; 
        amount = a; 
        note = n; 
        // Automatically sets the date to today
        date = LocalDate.now();
    }

    // Getter methods to access the private data
    public String    getCategory() { return category; }
    public double    getAmount()   { return amount; }
    public LocalDate getDate()     { return date; }
    public String    getNote()     { return note; }

    // This method prints the expense in a nice, readable format
    @Override public String toString() {
        // Formats the date like 10-May, aligns the text, and adds commas to the price
        return String.format("[%s]  %-12s  Rs.%,9.2f  %s",
            date.format(DateTimeFormatter.ofPattern("dd-MMM")),
            category, amount, note.isEmpty() ? "" : "— " + note);
    }
}