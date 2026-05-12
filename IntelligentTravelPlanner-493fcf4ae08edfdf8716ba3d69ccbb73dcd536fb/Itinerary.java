import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Itinerary implements Serializable {
        private static final long serialVersionUID = 2L;
        private final List<DayPlan> days = new ArrayList<>();
        private String summary = "";

        public void addDay(DayPlan d)        { days.add(d); }
        public List<DayPlan> getDays()       { return days; }
        public String getSummary()           { return summary; }
        public void setSummary(String s)     { this.summary = s; }

        @Override public String toString() {
            StringBuilder sb = new StringBuilder();
            for (DayPlan d : days) sb.append(d).append("\n");
            return sb.toString();
        }
    }