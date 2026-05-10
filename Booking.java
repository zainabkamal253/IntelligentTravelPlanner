import java.io.Serializable;
import java.time.LocalDate;

public class Booking implements Bookable, Serializable {

        private static final long serialVersionUID = 1L;

        // basic booking details
        private final String type;
        private final String name;
        private final double cost;

        private boolean confirmed;
        private final LocalDate bookingDate;

        // constructor sets booking info and current date
        public Booking(String t, String n, double c) {
            type = t;
            name = n;
            cost = c;
            bookingDate = LocalDate.now();
        }

        @Override
        public void book() {
            confirmed = true;
        }

        @Override
        public void cancel() {
            confirmed = false;
        }

        @Override
        public double getCost() {
            return cost;
        }

        @Override
        public boolean isConfirmed() {
            return confirmed;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public LocalDate getDate() {
            return bookingDate;
        }

        @Override
        public String toString() {
            return String.format("%s  [%s]  %s  —  Rs.%,.0f",
                    confirmed ? "" : "", type, name, cost);
        }
}