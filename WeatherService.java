public class WeatherService implements APIService {

    // enables real weather API if API key is added
    private static final boolean USE_REAL_WEATHER = true;

    // OpenWeatherMap API key
    private static final String WEATHER_API_KEY = "YOUR_OPENWEATHER_API_KEY_HERE";

    // main API method
    @Override
    public String callAPI(String city) {

        // check if city is empty
        if (city == null || city.trim().isEmpty()) {
            return "Please specify a city.";
        }

        // use OpenWeatherMap if key is available
        if (USE_REAL_WEATHER && !WEATHER_API_KEY.startsWith("YOUR_")) {
            return callOWM(city);
        }

        // otherwise use wttr.in API
        return callWttr(city);
    }

    // method to fetch weather from OpenWeatherMap
    private String callOWM(String city) {

        try {
            String url = "https://api.openweathermap.org/data/2.5/weather?q="
                    + HTTP.urlEncode(city)
                    + "&appid=" + WEATHER_API_KEY
                    + "&units=metric";

            // get JSON response
            String json = HTTP.get(url);

            // if city not found, use backup API
            if (json.contains("\"cod\":\"404\"") || json.contains("city not found")) {
                return callWttr(city);
            }

            // extract fields from JSON
            String temp  = HTTP.field(json, "temp");
            String feels = HTTP.field(json, "feels_like");
            String humid = HTTP.field(json, "humidity");
            String wind  = HTTP.field(json, "speed");
            String desc  = HTTP.field(json, "description");
            String pres  = HTTP.field(json, "pressure");
            String vis   = HTTP.field(json, "visibility");

            // if no temperature found, switch API
            if (temp == null) return callWttr(city);

            // return formatted weather report
            return formatWeather(city, temp, feels, desc,
                    humid, wind, pres, vis, "OpenWeatherMap");

        } catch (Exception e) {

            // fallback API if error occurs
            return callWttr(city);
        }
    }

    // backup weather API method
    private String callWttr(String city) {

        try {
            // fetch weather data from wttr.in
            String json = HTTP.get("https://wttr.in/" + HTTP.urlEncode(city) + "?format=j1");

            // extract weather fields
            String temp  = HTTP.field(json, "temp_C");
            String humid = HTTP.field(json, "humidity");
            String wind  = HTTP.field(json, "windspeedKmph");
            String feels = HTTP.field(json, "FeelsLikeC");

            // extract weather description manually
            String desc = null;
            int i = json.indexOf("\"weatherDesc\":[{\"value\":\"");

            if (i >= 0) {
                i += 25;
                int j = json.indexOf('"', i);

                if (j > i)
                    desc = json.substring(i, j);
            }

            // if weather not available
            if (temp == null) {
                return "Weather data unavailable for \"" + city + "\".\n"
                        + "Try: Lahore, Islamabad, Karachi, Murree, Hunza";
            }

            // return formatted result
            return formatWeather(city, temp, feels, desc,
                    humid, wind, null, null, "wttr.in");

        } catch (Exception e) {

            // error message
            return "Weather fetch failed: " + e.getMessage();
        }
    }

    // method to format weather information nicely
    private String formatWeather(String city, String temp, String feels, String desc,
                                 String humid, String wind, String pres,
                                 String vis, String src) {

        StringBuilder sb = new StringBuilder();

        sb.append("LIVE WEATHER — ").append(city.toUpperCase()).append("\n");
        sb.append("=====================================\n");

        // temperature details
        sb.append("Temperature : ")
                .append(temp != null ? temp : "N/A")
                .append(" °C");

        // feels like temperature
        if (feels != null) {
            sb.append(" (feels like ").append(feels).append(" °C)");
        }

        sb.append("\n");

        // other weather details
        if (desc != null)
            sb.append("Condition   : ").append(cap(desc)).append("\n");

        if (humid != null)
            sb.append("Humidity    : ").append(humid).append("%\n");

        if (wind != null)
            sb.append("Wind Speed  : ").append(wind).append(" km/h\n");

        if (pres != null)
            sb.append("Pressure    : ").append(pres).append(" hPa\n");

        // convert visibility to km
        if (vis != null) {
            try {
                sb.append("Visibility  : ")
                        .append(Double.parseDouble(vis) / 1000)
                        .append(" km\n");
            } catch (Exception ignored) {}
        }

        sb.append("=====================================\n");

        // API source
        sb.append("Source: ").append(src).append(" - Live data\n");

        // travel tip based on weather
        String tip = weatherTip(desc, temp);

        if (tip != null) {
            sb.append("\nTravel Tip: ").append(tip);
        }

        return sb.toString();
    }

    // gives travel suggestions according to weather
    private String weatherTip(String desc, String temp) {

        if (desc == null) return null;

        String d = desc.toLowerCase();

        // weather conditions
        if (d.contains("rain"))
            return "Carry an umbrella and waterproof shoes.";

        if (d.contains("snow"))
            return "Pack warm layers — roads may be slippery.";

        if (d.contains("thunder"))
            return "Avoid outdoor activities; stay indoors.";

        try {
            double t = Double.parseDouble(temp);

            // high temperature warning
            if (t > 38)
                return "Extreme heat — stay hydrated and avoid midday sun.";

            // low temperature warning
            if (t < 5)
                return "Very cold — wear heavy warm clothing.";

        } catch (Exception ignored) {}

        return null;
    }

    // capitalize first letter
    private String cap(String s) {

        if (s == null || s.isEmpty())
            return s;

        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}