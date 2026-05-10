import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// This class is used to fetch places data from different sources (local + APIs)
public class PlacesService implements APIService {

    // main method from interface, takes query and returns results
    @Override
    public String callAPI(String query) throws Exception {
        String[] parts = query.split("\\|", 2);
        return searchPlaces(parts[0].trim(),
                parts.length > 1 ? parts[1].trim() : "historical");
    }

    // main search method that tries different data sources
    public String searchPlaces(String city, String interest) {

        // first try local database result
        String localResult = formatLocalCsvResult(city, interest);
        if (localResult != null) return localResult;

        // get coordinates from local DB or geocoding API
        double[] coords = TouristDB.getInstance().getCoords(city);
        if (coords == null) coords = geocode(city);

        // try Foursquare API if enabled and token is valid
        if (IntelligentTravelPlanner.USE_FOURSQUARE
                && !IntelligentTravelPlanner.FOURSQUARE_TOKEN.startsWith("YOUR_")
                && coords != null) {
            try {
                String result = searchFoursquare(city, interest, coords[0], coords[1]);
                if (result != null && !result.isEmpty()) return result;
            } catch (Exception e) {
                System.err.println("Foursquare error: " + e.getMessage());
            }
        }

        // fallback to OpenStreetMap (Overpass API)
        if (coords != null) {
            try {
                return searchOverpass(city, interest, coords[0], coords[1]);
            } catch (Exception e) {
                System.err.println("Overpass error: " + e.getMessage());
            }
        }

        // if nothing works, show default message
        return "No data available for " + city + ".\n"
                + "Check spelling or try: Lahore, Islamabad, Karachi, Hunza, Murree, Skardu.";
    }

    // formats places from local CSV database
    private String formatLocalCsvResult(String city, String interest) {

        List<TouristPlace> places = TouristDB.getInstance().placesFor(city, interest);
        if (places.isEmpty()) return null;

        StringBuilder sb = new StringBuilder();

        sb.append("TOP ").append(interest.toUpperCase())
                .append(" PLACES — ").append(city.toUpperCase()).append("\n");

        sb.append("----------------------------------------------------\n");

        int i = 1;
        for (TouristPlace p : places) {
            sb.append(String.format("%2d. %s %s%n",
                    i++, p.getName(), p.getName())); // (looks like duplicate but kept same code)

            sb.append(String.format("     %s%n", p.getCategory()));

            if (!p.getDescription().isEmpty())
                sb.append(String.format("     %s%n", p.getDescription()));

            sb.append("\n");
            if (i > 12) break; // limit results
        }

        sb.append("----------------------------------------------------\n");
        sb.append("These will form the backbone of your itinerary.\n");
        sb.append("Source: curated local database (")
                .append(places.size()).append(" places matched)\n");

        return sb.toString();
    }

    // gets latitude/longitude of city using OpenStreetMap API
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

        } catch (Exception e) {
            return null; // fail silently
        }
    }

    // category mapping for Foursquare API
    private static final Map<String, String> FSQ_CATEGORIES = new LinkedHashMap<>();

    static {
        FSQ_CATEGORIES.put("adventure", "16000,16032,16019,10000");
        FSQ_CATEGORIES.put("food", "13000,13065,13338,13032");
        FSQ_CATEGORIES.put("historical", "12067,12062,12069,12100");
        FSQ_CATEGORIES.put("nature", "16032,16019,16057,10000");
        FSQ_CATEGORIES.put("shopping", "17000,17069,17114,17145");
        FSQ_CATEGORIES.put("cultural", "10000,12067,10027");
    }

    // fetch places from Foursquare API
    private String searchFoursquare(String city, String interest,
                                    double lat, double lon) throws Exception {

        String cats = FSQ_CATEGORIES.getOrDefault(interest.toLowerCase(),
                "16000,12067,13000");

        String url = "https://api.foursquare.com/v3/places/search"
                + "?ll=" + lat + "," + lon
                + "&radius=15000"
                + "&categories=" + cats
                + "&limit=12"
                + "&sort=RELEVANCE";

        Map<String, String> h = new HashMap<>();
        h.put("Authorization", IntelligentTravelPlanner.FOURSQUARE_TOKEN);
        h.put("Accept", "application/json");

        String json = HTTP.get(url, h);

        if (json.contains("\"error\"") || !json.contains("\"results\""))
            return null;

        return parseFoursquare(json, city, interest, lat, lon);
    }

    // parses Foursquare JSON response into readable text
    private String parseFoursquare(String json, String city, String interest,
                                   double lat, double lon) {

        StringBuilder sb = new StringBuilder();

        sb.append("NEARBY PLACES — ").append(city.toUpperCase()).append("\n");
        sb.append("Interest : ").append(interest).append("\n");
        sb.append("Source   : Foursquare Places\n");
        sb.append(String.format("Location : %.4f°N %.4f°E%n", lat, lon));

        sb.append("---------------------------------------------\n\n");

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

            sb.append(++count).append(". ").append(name).append("\n");
            sb.append("     ").append(catName).append("\n");
            if (!dist.isEmpty()) sb.append("     ").append(dist).append(" from center\n\n");
        }

        if (count == 0) return null;

        sb.append("---------------------------------------------\n");
        return sb.toString();
    }

    // fetch places from OpenStreetMap Overpass API
    private String searchOverpass(String city, String interest,
                                  double lat, double lon) throws Exception {

        String query =
                "[out:json][timeout:20];(" +
                        "nwr[\"tourism\"](around:80000," + lat + "," + lon + ");" +
                        "nwr[\"historic\"](around:80000," + lat + "," + lon + ");" +
                        "nwr[\"natural\"=\"peak\"](around:80000," + lat + "," + lon + ");" +
                        ");out center 12;";

        Map<String, String> h = new HashMap<>();
        h.put("Content-Type", "application/x-www-form-urlencoded");

        String response = HTTP.post(
                "https://overpass-api.de/api/interpreter",
                "data=" + HTTP.urlEncode(query),
                h
        );

        return parseOverpass(response, city, interest, lat, lon);
    }

    // parses Overpass API response
    private String parseOverpass(String json, String city, String interest,
                                 double lat, double lon) {

        StringBuilder sb = new StringBuilder();

        sb.append("NEARBY PLACES — ").append(city.toUpperCase()).append("\n");
        sb.append("Interest : ").append(interest).append("\n");
        sb.append("Source   : OpenStreetMap\n");
        sb.append(String.format("Location : %.4f°N %.4f°E%n", lat, lon));

        sb.append("---------------------------------------------\n\n");

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
                    HTTP.field(tags, "tourism"),
                    HTTP.field(tags, "historic"),
                    HTTP.field(tags, "amenity"),
                    HTTP.field(tags, "natural"),
                    "place"
            );

            sb.append(++count).append(". ").append(name).append("\n");
            sb.append("     ").append(type.replace("_", " ")).append("\n\n");
        }

        if (count == 0) return "No places found via OpenStreetMap.\n";

        sb.append("---------------------------------------------\n");
        return sb.toString();
    }

    // helper to pick first non-null value
    private String firstNonNull(String... vals) {
        for (String v : vals) if (v != null) return v;
        return "place";
    }
}