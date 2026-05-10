import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * This class represents a full trip created by a user.
 * It stores destination, budget, itinerary, expenses, etc.
 */
public class Trip implements Serializable {
    private static final long serialVersionUID = 1L;

    // basic trip info
    private final String tripId, owner, interest;
    private final Destination destination;
    private final int days;

    private double budget;

    // itinerary generated for this trip
    private Itinerary itinerary = new Itinerary();

    // lists to store expenses and bookings
    private final List<Expense> expenses = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    // date when trip was created
    private final LocalDate createdOn;

    private String notes = "";

    public Trip(String owner, Destination d, int days, double budget, String interest)
            throws InvalidTripException {

        // basic validation checks
        if (days <= 0) throw new InvalidTripException("Duration must be at least 1 day.");
        if (budget <= 0) throw new InvalidTripException("Budget must be greater than zero.");
        if (d.getName().trim().isEmpty())
            throw new InvalidTripException("Destination name cannot be empty.");

        // unique id based on time
        tripId = "TRIP-" + System.currentTimeMillis();

        this.owner = owner;
        destination = d;
        this.days = days;
        this.budget = budget;
        this.interest = interest;

        // set creation date
        createdOn = LocalDate.now();
    }

    // getter methods
    public String       getTripId()      { return tripId; }
    public String       getOwner()       { return owner; }
    public Destination  getDestination() { return destination; }
    public int          getDays()        { return days; }
    public double       getBudget()      { return budget; }
    public String       getInterest()    { return interest; }
    public Itinerary    getItinerary()   { return itinerary; }
    public List<Expense> getExpenses()   { return expenses; }
    public List<Booking> getBookings()   { return bookings; }
    public LocalDate    getCreatedOn()   { return createdOn; }
    public String       getNotes()       { return notes; }

    // setter methods
    public void setItinerary(Itinerary i) { itinerary = i; }
    public void addExpense(Expense e)     { expenses.add(e); }
    public void addBooking(Booking b)     { bookings.add(b); }
    public void setBudget(double b)       { budget = b; }
    public void setNotes(String n)        { notes = n; }

    // calculate total spent money
    public double totalSpent() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    // remaining budget calculation
    public double remaining() {
        return budget - totalSpent();
    }

    @Override
    public String toString() {
        return String.format("✈ %-22s  %2d days  Rs.%,.0f  [%s]",
            destination.toString(), days, budget,
            interest == null ? "general" : interest);
    }
}