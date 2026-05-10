//DayPlan represents a single day in the itinerary.
//It contains a list of activities and an optional theme.

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DayPlan implements Serializable {

        private static final long serialVersionUID = 2L;

        // day number in itinerary
        private final int dayNumber;

        // optional theme for the day
        private String theme = "";

        // list of activities planned for the day
        private final List<Activity> activities = new ArrayList<>();

        // constructor without theme
        public DayPlan(int d) {
                dayNumber = d;
        }

        // constructor with theme
        public DayPlan(int d, String theme) {
                dayNumber = d;
                this.theme = theme;
        }

        // add activity to the day
        public void addActivity(Activity a) {
                activities.add(a);
        }

        public int getDayNumber() {
                return dayNumber;
        }

        public String getTheme() {
                return theme;
        }

        public void setTheme(String t) {
                this.theme = t;
        }

        public List<Activity> getActivities() {
                return activities;
        }

        /*
         * Converts day plan into readable text format.
         * Used for exporting itinerary.
         */
        @Override
        public String toString() {

                StringBuilder sb = new StringBuilder("Day " + dayNumber);

                if (!theme.isEmpty()) {
                        sb.append(" — ").append(theme);
                }

                sb.append("\n");

                for (Activity a : activities) {

                        sb.append("  ");

                        if (a.emoji != null) {
                                sb.append(a.emoji).append(" ");
                        }

                        if (!a.time.isEmpty()) {
                                sb.append("[").append(a.time).append("] ");
                        }

                        sb.append(a.title);

                        if (!a.description.isEmpty()) {
                                sb.append(" — ").append(a.description);
                        }

                        sb.append("\n");
                }

                return sb.toString();
        }
}