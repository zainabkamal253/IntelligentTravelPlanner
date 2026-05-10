import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;
import java.lang.StringBuilder;
public class RoutePlanner {
        private final Map<String, Map<String, Integer>> graph = new LinkedHashMap<>();

        public RoutePlanner() {
            r("Islamabad","Rawalpindi",15);   r("Rawalpindi","Taxila",35);
            r("Taxila","Attock",45);          r("Attock","Peshawar",90);
            r("Peshawar","Mardan",60);        r("Mardan","Swat",100);
            r("Swat","Kalam",95);             r("Peshawar","Kohat",55);
            r("Kohat","Dera Ismail Khan",200);r("Dera Ismail Khan","Multan",300);
            r("Islamabad","Lahore",375);      r("Lahore","Gujranwala",65);
            r("Gujranwala","Sialkot",70);     r("Lahore","Faisalabad",130);
            r("Faisalabad","Sargodha",90);    r("Sargodha","Multan",230);
            r("Lahore","Multan",340);         r("Multan","Bahawalpur",100);
            r("Bahawalpur","Rahim Yar Khan",110); r("Rahim Yar Khan","Sukkur",220);
            r("Sukkur","Larkana",80);         r("Sukkur","Hyderabad",270);
            r("Hyderabad","Karachi",160);     r("Multan","Dera Ghazi Khan",170);
            r("Dera Ghazi Khan","Quetta",420);r("Karachi","Hub",50);
            r("Hub","Quetta",500);            r("Quetta","Zhob",370);
            r("Quetta","Turbat",600);         r("Islamabad","Murree",60);
            r("Murree","Nathia Gali",35);     r("Islamabad","Naran",250);
            r("Naran","Chilas",120);          r("Chilas","Gilgit",90);
            r("Gilgit","Hunza",90);           r("Hunza","Khunjerab Pass",120);
            r("Gilgit","Skardu",200);         r("Skardu","Shigar",35);
            r("Chilas","Besham",110);         r("Besham","Islamabad",220);
            r("Rawalpindi","Jehlum",90);      r("Jehlum","Gujrat",65);
            r("Gujrat","Lahore",120);         r("Islamabad","Abbottabad",120);
            r("Abbottabad","Mansehra",35);    r("Mansehra","Naran",130);
            r("Naran","Babusar Top",80);      r("Gilgit","Chitral",320);
            r("Peshawar","Chitral",320);      r("Lahore","Sahiwal",120);
            r("Sahiwal","Multan",190);        r("Karachi","Thatta",100);
            r("Thatta","Hyderabad",90);       r("Karachi","Gwadar",650);
            r("Turbat","Gwadar",180);
        }

        private void r(String a, String b, int km) {
            graph.computeIfAbsent(a, k -> new LinkedHashMap<>()).put(b, km);
            graph.computeIfAbsent(b, k -> new LinkedHashMap<>()).put(a, km);
        }

        public void addCustomRoute(String a, String b, int km) { r(a, b, km); }

        public String[] shortestRoute(String start, String end) {
            if (!graph.containsKey(start)) return err("City not found: " + start);
            if (!graph.containsKey(end))   return err("City not found: " + end);
            if (start.equals(end))         return ok("Already at " + start + "!");

            Map<String, Integer> dist = new HashMap<>();
            Map<String, String>  prev = new HashMap<>();
            for (String n : graph.keySet()) dist.put(n, Integer.MAX_VALUE);
            dist.put(start, 0);
            PriorityQueue<String> pq = new PriorityQueue<>(
                Comparator.comparingInt(n -> dist.getOrDefault(n, Integer.MAX_VALUE)));
            pq.add(start);

            while (!pq.isEmpty()) {
                String cur = pq.poll();
                if (cur.equals(end)) break;
                if (dist.get(cur) == Integer.MAX_VALUE) continue;
                for (Map.Entry<String, Integer> e : graph.get(cur).entrySet()) {
                    int alt = dist.get(cur) + e.getValue();
                    if (alt < dist.getOrDefault(e.getKey(), Integer.MAX_VALUE)) {
                        dist.put(e.getKey(), alt);
                        prev.put(e.getKey(), cur);
                        pq.remove(e.getKey());
                        pq.add(e.getKey());
                    }
                }
            }
            if (dist.getOrDefault(end, Integer.MAX_VALUE) == Integer.MAX_VALUE)
                return err("No route found between " + start + " and " + end + ".\n" +
                           "Try connecting via Islamabad, Lahore, or Karachi.");

            LinkedList<String> path = new LinkedList<>();
            for (String at = end; at != null; at = prev.get(at)) path.addFirst(at);

            int km = dist.get(end);
            double hrs  = km / 80.0;
            double fuel = km * 25.0;
            double bus  = km * 3.5;

            StringBuilder sb = new StringBuilder();
            sb.append("╔══════════════════════════════════════════════════╗\n");
            sb.append(String.format("║  🛣  ROUTE: %-37s║\n", start + "  →  " + end));
            sb.append("╚══════════════════════════════════════════════════╝\n\n");
            sb.append("📍  PATH:\n");
            for (int i = 0; i < path.size() - 1; i++) {
                String a = path.get(i), b = path.get(i + 1);
                sb.append(String.format("    %-22s  →  %-22s  (%d km)\n",
                    a, b, graph.get(a).get(b)));
            }
            sb.append("\n──────────────────────────────────────────────────\n");
            sb.append(String.format("📏  Total Distance   : %,d km\n", km));
            sb.append(String.format("⏱   Est. Drive Time  : %.1f hours  (avg 80 km/h)\n", hrs));
            sb.append(String.format("⛽  Private Car Cost  : Rs.%,.0f  (Rs.25/km)\n", fuel));
            sb.append(String.format("🚌  Bus Estimate      : Rs.%,.0f  (Rs.3.5/km)\n", bus));
            sb.append("\n🚌  RECOMMENDED TRANSPORT:\n");
            if (km <= 60)
                sb.append("    • Careem / InDrive / Bykea (ride-hailing)\n    • Local bus\n");
            else if (km <= 150)
                sb.append("    • Daewoo Express / Faisal Movers\n    • Self-drive\n");
            else if (km <= 500)
                sb.append("    • Daewoo Express (recommended)\n    • NATCO (for northern areas)\n    • Pakistan Railways\n");
            else
                sb.append("    • PIA / Airblue / SereneAir (flight)\n    • Daewoo overnight bus\n    • Pakistan Railways\n");
            sb.append("\n⚠  Travel times vary with road conditions & traffic.\n");
            sb.append("   Mountain roads may be closed Nov–Apr (check NHMP).");
            return ok(sb.toString());
        }

        private String[] ok(String s)  { return new String[]{"OK",    s}; }
        private String[] err(String s) { return new String[]{"ERROR", s}; }
        public Set<String> getCities() { return graph.keySet(); }
    }
