import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class DayPlan implements Serializable {
        private static final long serialVersionUID = 2L;
        private final int dayNumber;
        private String theme = "";
        private final List<Activity> activities = new ArrayList<>();

        public DayPlan(int d) { dayNumber = d; }
        public DayPlan(int d, String theme) { dayNumber = d; this.theme = theme; }

        public void addActivity(Activity a)  { activities.add(a); }
        public int  getDayNumber()           { return dayNumber; }
        public String getTheme()             { return theme; }
        public void setTheme(String t)       { this.theme = t; }
        public List<Activity> getActivities(){ return activities; }

        /** Plain-text fallback used by the export-to-text feature. */
        @Override public String toString() {
            StringBuilder sb = new StringBuilder(" Day " + dayNumber);
            if (!theme.isEmpty()) sb.append(" — ").append(theme);
            sb.append("\n");
            for (Activity a : activities) {
                sb.append("   ").append(a.emoji).append("  ");
                if (!a.time.isEmpty()) sb.append("[").append(a.time).append("] ");
                sb.append(a.title);
                if (!a.description.isEmpty()) sb.append(" — ").append(a.description);
                sb.append("\n");
            }
            return sb.toString();
        }
    }
