import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// This class stores the full itinerary of a trip
public class Itinerary implements Serializable {
        private static final long serialVersionUID = 2L;

        // List of all days in the trip plan
        private final List<DayPlan> days = new ArrayList<>();

        // Short description/overview of the whole trip
        private String summary = "";

        // Add a new day to the itinerary
        public void addDay(DayPlan d)        { days.add(d); }

        // Get all the days in the itinerary
        public List<DayPlan> getDays()       { return days; }

        // Get the trip summary
        public String getSummary()           { return summary; }

        // Set or update the trip summary
        public void setSummary(String s)     { this.summary = s; }

        // Convert the whole itinerary into text format
        @Override public String toString() {
            StringBuilder sb = new StringBuilder();

            // Loop through all days and add them to string
            for (DayPlan d : days) sb.append(d).append("\n");

            return sb.toString();
        }
    }