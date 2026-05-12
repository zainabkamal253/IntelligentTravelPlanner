import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
public class TouristDB {
        private static TouristDB INSTANCE;
        // city (lower-case) -> list of places
        private final Map<String, List<TouristPlace>> byCity = new HashMap<>();
        // city (lower-case) -> {lat, lon}
        private final Map<String, double[]> coords = new HashMap<>();
        private boolean spotsLoaded = false;
        private boolean coordsLoaded = false;

        private TouristDB() {
            loadCoords();
            loadSpots();
        }

        public static synchronized TouristDB getInstance() {
            if (INSTANCE == null) INSTANCE = new TouristDB();
            return INSTANCE;
        }

        public boolean hasCoords()  { return coordsLoaded; }
        public boolean hasSpots()   { return spotsLoaded; }

        public double[] getCoords(String city) {
            if (city == null) return null;
            return coords.get(city.toLowerCase().trim());
        }

        public Set<String> knownCities() {
            return Collections.unmodifiableSet(byCity.keySet());
        }

        /**
         * Returns places in {@code city} that match {@code interest}.
         * Falls back gracefully:
         *   1. exact match on city + matching category
         *   2. all places in city if no category match
         *   3. empty list if city unknown
         * Match is case-insensitive and tolerant of substring.
         */
        public List<TouristPlace> placesFor(String city, String interest) {
            if (city == null || city.trim().isEmpty()) return new ArrayList<>();
            String key = city.toLowerCase().trim();

            List<TouristPlace> all = byCity.get(key);
            if (all == null) {
                // Try contains-match (e.g. user typed "Lahore, Pakistan")
                for (Map.Entry<String, List<TouristPlace>> e : byCity.entrySet()) {
                    if (key.contains(e.getKey()) || e.getKey().contains(key)) {
                        all = e.getValue();
                        break;
                    }
                }
            }
            if (all == null) return new ArrayList<>();

            String want = (interest == null ? "" : interest.toLowerCase().trim());
            if (want.isEmpty()) return new ArrayList<>(all);

            // Map user-facing interest -> CSV categories (some interests
            // map to multiple categories so the user gets richer results)
            Set<String> targets = mapInterestToCategories(want);
            List<TouristPlace> matched = new ArrayList<>();
            for (TouristPlace p : all) {
                if (targets.contains(p.getCategory().toLowerCase())) matched.add(p);
            }
            // If absolutely nothing matched, return everything so the
            // user still gets something useful for that city.
            return matched.isEmpty() ? new ArrayList<>(all) : matched;
        }

        /**
         * Multi-category mapping. Adventure = adventure + nature,
         * cultural overlaps with historical, etc. — keeps itineraries
         * from ever returning empty when CSV uses a slightly different
         * label than the user's interest dropdown.
         */
        private Set<String> mapInterestToCategories(String interest) {
            Set<String> s = new LinkedHashSet<>();
            switch (interest) {
                case "adventure":
                    s.add("adventure"); s.add("nature");
                    break;
                case "food":
                    s.add("food");
                    break;
                case "historical":
                    s.add("historical"); s.add("cultural");
                    break;
                case "nature":
                    s.add("nature"); s.add("adventure");
                    break;
                case "shopping":
                    s.add("shopping"); s.add("cultural");
                    break;
                case "cultural":
                    s.add("cultural"); s.add("historical");
                    break;
                default:
                    s.add(interest);
            }
            return s;
        }

        private void loadCoords() {
            File f = new File("pakistan_cities.csv");
            if (!f.exists()) {
                System.err.println("⚠ pakistan_cities.csv not found — geocoding will use API.");
                return;
            }
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line = br.readLine(); // header
                while ((line = br.readLine()) != null) {
                    String[] parts = splitCsv(line);
                    if (parts.length < 3) continue;
                    try {
                        String city = parts[0].toLowerCase().trim();
                        double lat = Double.parseDouble(parts[1].trim());
                        double lon = Double.parseDouble(parts[2].trim());
                        coords.put(city, new double[]{lat, lon});
                    } catch (NumberFormatException ignore) {}
                }
                coordsLoaded = true;
                System.out.println("✅ Loaded " + coords.size() + " city coordinates.");
            } catch (Exception e) {
                System.err.println("⚠ Could not load pakistan_cities.csv: " + e.getMessage());
            }
        }

        private void loadSpots() {
            File f = new File("tourist_spots.csv");
            if (!f.exists()) {
                System.err.println("⚠ tourist_spots.csv not found — using built-in fallback.");
                loadBuiltInFallback();
                return;
            }
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line = br.readLine(); // header
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = splitCsv(line);
                    if (parts.length < 4) continue;
                    String city = parts[0].trim();
                    String cat  = parts[1].trim();
                    String name = parts[2].trim();
                    String desc = parts[3].trim();
                    if (city.isEmpty() || name.isEmpty()) continue;
                    TouristPlace p = new TouristPlace(name, cat, desc, city);
                    byCity.computeIfAbsent(city.toLowerCase(),
                        k -> new ArrayList<>()).add(p);
                }
                spotsLoaded = true;
                int total = byCity.values().stream().mapToInt(List::size).sum();
                System.out.println("✅ Loaded " + total + " tourist spots across "
                    + byCity.size() + " cities.");
            } catch (Exception e) {
                System.err.println("⚠ Could not load tourist_spots.csv: " + e.getMessage());
                loadBuiltInFallback();
            }
        }

        /** Tiny safety net so the app still works if CSVs are missing. */
        private void loadBuiltInFallback() {
            String[][] data = {
                {"Lahore","Historical","Lahore Fort","UNESCO Mughal-era fort complex"},
                {"Lahore","Historical","Badshahi Mosque","One of the world's largest mosques"},
                {"Lahore","Food","Fort Road Food Street","Iconic rooftop food street"},
                {"Lahore","Adventure","Sozo Water Park","Largest water park in the city"},
                {"Lahore","Nature","Shalimar Gardens","Mughal garden, UNESCO heritage"},
                {"Lahore","Shopping","Liberty Market","Bustling shopping bazaar"},
                {"Islamabad","Nature","Margalla Hills Trail-3","Most popular hike in the capital"},
                {"Islamabad","Historical","Faisal Mosque","Largest mosque in South Asia"},
                {"Islamabad","Adventure","Daman-e-Koh Hike","Scenic foothill viewpoint trek"},
                {"Karachi","Nature","Clifton Beach","Long sandy beach on Arabian Sea"},
                {"Karachi","Food","Burns Road Food Street","Heritage street food district"},
                {"Hunza","Nature","Attabad Lake","Electric-blue glacial lake"},
                {"Hunza","Adventure","Eagle's Nest Trek","Sunrise viewpoint hike"},
                {"Murree","Nature","Mall Road","Pine-forest hill-station promenade"},
                {"Murree","Adventure","Patriata Chair Lift","Cable-car & chair lift ride"},
                {"Skardu","Nature","Shangrila Resort","Heart-shaped Kachura Lake"},
                {"Skardu","Adventure","Deosai Plains Safari","Highest plateau in the world"},
            };
            for (String[] r : data) {
                byCity.computeIfAbsent(r[0].toLowerCase(), k -> new ArrayList<>())
                      .add(new TouristPlace(r[2], r[1], r[3], r[0]));
            }
            spotsLoaded = true;
        }

        /** Handles quoted commas correctly (rare in our data, but safe). */
        private static String[] splitCsv(String line) {
            List<String> out = new ArrayList<>();
            StringBuilder cur = new StringBuilder();
            boolean inQuote = false;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c == '"') { inQuote = !inQuote; continue; }
                if (c == ',' && !inQuote) {
                    out.add(cur.toString());
                    cur.setLength(0);
                } else {
                    cur.append(c);
                }
            }
            out.add(cur.toString());
            // also strip trailing \r from windows line-endings
            for (int i = 0; i < out.size(); i++) {
                out.set(i, out.get(i).replace("\r", "").trim());
            }
            return out.toArray(new String[0]);
        }
    }
