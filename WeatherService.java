public class WeatherService implements APIService {

    // Configuration constants (usually defined in a Config file)
    private static final boolean USE_REAL_WEATHER = true;
    private static final String WEATHER_API_KEY = "YOUR_OPENWEATHER_API_KEY_HERE";

    @Override
    public String callAPI(String city) {
        if (city == null || city.trim().isEmpty()) return "Please specify a city.";
        
        // Strategy: Use OWM if key is provided, otherwise use wttr.in fallback
        if (USE_REAL_WEATHER && !WEATHER_API_KEY.startsWith("YOUR_")) {
            return callOWM(city);
        }
        return callWttr(city);
    }

    private String callOWM(String city) {
        try {
            String url = "https://api.openweathermap.org/data/2.5/weather?q="
                + HTTP.urlEncode(city) + "&appid=" + WEATHER_API_KEY + "&units=metric";
            String json = HTTP.get(url);

            if (json.contains("\"cod\":\"404\"") || json.contains("city not found")) {
                return callWttr(city);
            }

            String temp   = HTTP.field(json, "temp");
            String feels  = HTTP.field(json, "feels_like");
            String humid  = HTTP.field(json, "humidity");
            String wind   = HTTP.field(json, "speed");
            String desc   = HTTP.field(json, "description");
            String pres   = HTTP.field(json, "pressure");
            String vis    = HTTP.field(json, "visibility");

            if (temp == null) return callWttr(city);
            return formatWeather(city, temp, feels, desc, humid, wind, pres, vis, "OpenWeatherMap");
        } catch (Exception e) { 
            return callWttr(city); 
        }
    }

    private String callWttr(String city) {
        try {
            // wttr.in format=j1 returns a clean JSON response
            String json = HTTP.get("https://wttr.in/" + HTTP.urlEncode(city) + "?format=j1");
            String temp  = HTTP.field(json, "temp_C");
            String humid = HTTP.field(json, "humidity");
            String wind  = HTTP.field(json, "windspeedKmph");
            String feels = HTTP.field(json, "FeelsLikeC");
            
            String desc  = null;
            int i = json.indexOf("\"weatherDesc\":[{\"value\":\"");
            if (i >= 0) { 
                i += 25; 
                int j = json.indexOf('"', i); 
                if (j > i) desc = json.substring(i, j); 
            }

            if (temp == null) {
                return "🌤  Weather data unavailable for \"" + city + "\".\n" +
                       "    Try: Lahore, Islamabad, Karachi, Murree, Hunza";
            }
            return formatWeather(city, temp, feels, desc, humid, wind, null, null, "wttr.in");
        } catch (Exception e) {
            return "⚠  Weather fetch failed: " + e.getMessage();
        }
    }

    private String formatWeather(String city, String temp, String feels, String desc,
                               String humid, String wind, String pres, String vis, String src) {
        String icon = weatherIcon(desc);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s  LIVE WEATHER — %s%n", icon, city.toUpperCase()));
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append(String.format("🌡  Temperature  : %s°C", temp != null ? temp : "N/A"));
        
        if (feels != null) sb.append(String.format("  (feels like %s°C)", feels));
        sb.append("\n");
        
        if (desc  != null) sb.append(String.format("☁   Condition    : %s%n", cap(desc)));
        if (humid != null) sb.append(String.format("💧   Humidity     : %s%%%n", humid));
        if (wind  != null) sb.append(String.format("💨   Wind Speed   : %s km/h%n", wind));
        if (pres  != null) sb.append(String.format("🌀   Pressure     : %s hPa%n", pres));
        
        if (vis   != null) {
            try { 
                sb.append(String.format("👁   Visibility   : %.1f km%n", Double.parseDouble(vis) / 1000)); 
            } catch (Exception ignored) {}
        }
        
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("📡  Source: ").append(src).append("  •  Live data\n");
        
        String tip = weatherTip(desc, temp);
        if (tip != null) sb.append("\n💡  Travel Tip: ").append(tip);
        
        return sb.toString();
    }

    private String weatherIcon(String desc) {
        if (desc == null) return "🌤";
        String d = desc.toLowerCase();
        if (d.contains("thunder")) return "⛈";
        if (d.contains("snow")) return "❄";
        if (d.contains("rain") || d.contains("drizzle")) return "🌧";
        if (d.contains("fog") || d.contains("mist")) return "🌫";
        if (d.contains("cloud")) return "☁";
        if (d.contains("clear") || d.contains("sunny")) return "☀";
        return "🌤";
    }

    private String weatherTip(String desc, String temp) {
        if (desc == null) return null;
        String d = desc.toLowerCase();
        if (d.contains("rain")) return "Carry an umbrella and waterproof shoes.";
        if (d.contains("snow")) return "Pack warm layers — roads may be slippery.";
        if (d.contains("thunder")) return "Avoid outdoor activities; stay indoors.";
        try {
            double t = Double.parseDouble(temp);
            if (t > 38) return "Extreme heat — hydrate well and avoid midday sun.";
            if (t < 5) return "Very cold — pack thermals and a heavy jacket.";
        } catch (Exception ignored) {}
        return null;
    }

    private String cap(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}