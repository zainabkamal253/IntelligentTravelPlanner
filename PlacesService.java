import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class PlacesService implements APIService {

        @Override
        public String callAPI(String query) throws Exception {
            String[] parts = query.split("\\|", 2);
            return searchPlaces(parts[0].trim(),
                parts.length > 1 ? parts[1].trim() : "historical");
        }

        public String searchPlaces(String city, String interest) {
            // 1) Local CSV first — instant for Pakistani cities
            String localResult = formatLocalCsvResult(city, interest);
            if (localResult != null) return localResult;

            // 2) Try Foursquare with local-coords (no Nominatim hop)
            double[] coords = TouristDB.getInstance().getCoords(city);
            if (coords == null) coords = geocode(city);

            if (USE_FOURSQUARE && !FOURSQUARE_TOKEN.startsWith("YOUR_") && coords != null) {
                try {
                    String result = searchFoursquare(city, interest, coords[0], coords[1]);
                    if (result != null && !result.isEmpty()) return result;
                } catch (Exception e) {
                    System.err.println("Foursquare error: " + e.getMessage());
                }
            }
            if (coords != null) {
                try {
                    return searchOverpass(city, interest, coords[0], coords[1]);
                } catch (Exception e) {
                    System.err.println("Overpass error: " + e.getMessage());
                }
            }
            return "📌  No data available for " + city + ".\n" +
                   "    Check spelling, or try one of the well-known cities like\n" +
                   "    Lahore, Islamabad, Karachi, Hunza, Murree, Skardu.";
        }

        /** Render the CSV-based answer for a city. */
        private String formatLocalCsvResult(String city, String interest) {
            List<TouristPlace> places = TouristDB.getInstance().placesFor(city, interest);
            if (places.isEmpty()) return null;

            StringBuilder sb = new StringBuilder();
            sb.append("📌  TOP ").append(interest.toUpperCase()).append(" PLACES — ");
            sb.append(city.toUpperCase()).append("\n");
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            int i = 1;
            for (TouristPlace p : places) {
                sb.append(String.format("%2d. %s  %s%n",
                    i++, p.getEmoji(), p.getName()));
                sb.append(String.format("    📂  %s%n", p.getCategory()));
                if (!p.getDescription().isEmpty())
                    sb.append(String.format("    📝  %s%n", p.getDescription()));
                sb.append("\n");
                if (i > 12) break;
            }
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append("💡  These will form the backbone of your itinerary.\n");
            sb.append("📊  Source: curated local database (").append(places.size())
              .append(" places matched)\n");
            return sb.toString();
        }

        private double[] geocode(String city) {
            try {
                String url = "https://nominatim.openstreetmap.org/search?q="
                    + HTTP.urlEncode(city + ", Pakistan")
                    + "&format=json&limit=1";
                Map<String, String> h = new HashMap<>();
                h.put("User-Agent", "TravelPlannerProject/5.0");
                String json = HTTP.get(url, h);
                String lat = HTTP.field(json, "lat");
                String lon = HTTP.field(json, "lon");
                if (lat == null) return null;
                return new double[]{
                    Double.parseDouble(lat.replace("\"", "")),
                    Double.parseDouble(lon.replace("\"", ""))
                };
            } catch (Exception e) { return null; }
        }

        private static final Map<String, String> FSQ_CATEGORIES = new LinkedHashMap<>();
        static {
            FSQ_CATEGORIES.put("adventure",  "16000,16032,16019,10000");
            FSQ_CATEGORIES.put("food",       "13000,13065,13338,13032");
            FSQ_CATEGORIES.put("historical", "12067,12062,12069,12100");
            FSQ_CATEGORIES.put("nature",     "16032,16019,16057,10000");
            FSQ_CATEGORIES.put("shopping",   "17000,17069,17114,17145");
            FSQ_CATEGORIES.put("cultural",   "10000,12067,10027");
        }

        private String searchFoursquare(String city, String interest,
                double lat, double lon) throws Exception {
            String cats = FSQ_CATEGORIES.getOrDefault(interest.toLowerCase(),
                "16000,12067,13000");
            String url = "https://api.foursquare.com/v3/places/search"
                + "?ll=" + lat + "," + lon
                + "&radius=15000"
                + "&categories=" + cats
                + "&limit=12"
                + "&sort=RELEVANCE"
                + "&fields=name,categories,location,distance,rating";
            Map<String, String> h = new HashMap<>();
            h.put("Authorization", FOURSQUARE_TOKEN);
            h.put("Accept", "application/json");
            String json = HTTP.get(url, h);
            if (json.contains("\"error\"") || !json.contains("\"results\""))
                return null;
            return parseFoursquare(json, city, interest, lat, lon);
        }

        private String parseFoursquare(String json, String city, String interest,
                double lat, double lon) {
            StringBuilder sb = new StringBuilder();
            sb.append("📍  NEARBY PLACES — ").append(city.toUpperCase()).append("\n");
            sb.append("🎯  Interest  : ").append(interest).append("\n");
            sb.append("🗺   Source   : Foursquare Places\n");
            sb.append(String.format("📡  Location  : %.4f°N  %.4f°E%n", lat, lon));
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

            int resultsStart = json.indexOf("\"results\":");
            if (resultsStart < 0) return null;
            String[] items = json.substring(resultsStart).split("\\{\"fsq_id\"");
            int count = 0;
            for (int i = 1; i < items.length && count < 10; i++) {
                String item = items[i];
                String name = HTTP.field(item, "name");
                if (name == null || name.trim().isEmpty()) continue;
                String distRaw = HTTP.field(item, "distance");
                String dist = "";
                if (distRaw != null) {
                    try {
                        int dm = Integer.parseInt(distRaw.trim());
                        dist = dm < 1000 ? dm + " m" : String.format("%.1f km", dm / 1000.0);
                    } catch (Exception ignored) {}
                }
                String catName = "Place";
                int ci = item.indexOf("\"short_name\":\"");
                if (ci >= 0) {
                    ci += 14;
                    int cj = item.indexOf('"', ci);
                    if (cj > ci) catName = item.substring(ci, cj);
                }
                sb.append(String.format("%d. %s%n", ++count, name));
                sb.append(String.format("   📂  %s%n", catName));
                if (!dist.isEmpty()) sb.append(String.format("   📏  %s from centre%n", dist));
                sb.append("\n");
            }
            if (count == 0) return null;
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append("💡  Add these to your itinerary!\n");
            return sb.toString();
        }

        private String searchOverpass(String city, String interest, double lat, double lon) throws Exception {
            StringBuilder query = new StringBuilder();
            query.append("[out:json][timeout:20];(");
            query.append("nwr[\"tourism\"](around:80000,").append(lat).append(",").append(lon).append(");");
            query.append("nwr[\"historic\"](around:80000,").append(lat).append(",").append(lon).append(");");
            query.append("nwr[\"natural\"=\"peak\"](around:80000,").append(lat).append(",").append(lon).append(");");
            query.append(");out center 12;");
            Map<String, String> h = new HashMap<>();
            h.put("Content-Type", "application/x-www-form-urlencoded");
            String response = HTTP.post("https://overpass-api.de/api/interpreter",
                "data=" + HTTP.urlEncode(query.toString()), h);
            return parseOverpass(response, city, interest, lat, lon);
        }

        private String parseOverpass(String json, String city, String interest,
                double lat, double lon) {
            StringBuilder sb = new StringBuilder();
            sb.append("📍  NEARBY PLACES — ").append(city.toUpperCase()).append("\n");
            sb.append("🎯  Interest  : ").append(interest).append("\n");
            sb.append("🗺   Source   : OpenStreetMap (Overpass API)\n");
            sb.append(String.format("📡  Location  : %.4f°N  %.4f°E%n", lat, lon));
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            String[] elements = json.split("\"type\":\"node\"");
            int count = 0;
            for (int i = 1; i < elements.length && count < 10; i++) {
                String el = elements[i];
                int ts = el.indexOf("\"tags\":{");
                if (ts < 0) continue;
                String tags = el.substring(ts);
                String name = HTTP.field(tags, "name");
                if (name == null || name.trim().isEmpty()) continue;
                String type = firstNonNull(
                    HTTP.field(tags, "tourism"),  HTTP.field(tags, "historic"),
                    HTTP.field(tags, "amenity"),  HTTP.field(tags, "natural"), "place");
                sb.append(String.format("%d. %s%n", ++count, name));
                sb.append(String.format("   📂  %s%n", type.replace("_", " ")));
                sb.append("\n");
            }
            if (count == 0) {
                sb.append("No tagged places found via OpenStreetMap.\n");
                return sb.toString();
            }
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            return sb.toString();
        }

        private String firstNonNull(String... vals) {
            for (String v : vals) if (v != null) return v;
            return "place";
        }
    }