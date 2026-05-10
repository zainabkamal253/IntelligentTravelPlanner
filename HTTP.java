import java.io.*;
import java.net.*;
import java.util.*;

// This class helps us talk to the internet and fetch data from APIs
public class HTTP {
    
    // Simple GET request that doesn't need extra headers
    static String get(String urlStr) throws Exception {
        return get(urlStr, new HashMap<>());
    }

    // GET request used to ask for data from a server
    static String get(String urlStr, Map<String, String> headers) throws Exception {
        HttpURLConnection c = open(urlStr, "GET", headers);
        return readResponse(c);
    }

    // POST request used to send data (like a body/message) to a server
    static String post(String urlStr, String body, Map<String, String> headers) throws Exception {
        HttpURLConnection c = open(urlStr, "POST", headers);
        c.setDoOutput(true); // Tells the connection we are sending data out
        // Write the body text to the connection
        try (OutputStream os = c.getOutputStream()) { 
            os.write(body.getBytes("UTF-8")); 
        }
        return readResponse(c);
    }

    // Helper method to set up the connection settings
    private static HttpURLConnection open(String urlStr, String method,
                                        Map<String, String> headers) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(urlStr).openConnection();
        c.setRequestMethod(method);
        // Identify our app to the server
        c.setRequestProperty("User-Agent", "IntelligentTravelPlanner/5.0 (NUST-SEECS)");
        // Set timeouts so the app doesn't hang forever if the internet is slow
        c.setConnectTimeout(8000); 
        c.setReadTimeout(12000);
        
        // Add any extra headers we passed in
        if (headers != null) headers.forEach(c::setRequestProperty);
        return c;
    }

    // Method to read the text that the server sends back
    private static String readResponse(HttpURLConnection c) throws Exception {
        int code = c.getResponseCode();
        // If code is < 400 it's good, otherwise we read the error stream
        InputStream is = (code < 400) ? c.getInputStream() : c.getErrorStream();
        
        if (is == null) return "{\"error\":\"No response stream, HTTP " + code + "\"}";
        
        // Read the stream line by line and build a long string
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            return sb.toString();
        }
    }

    // A simple way to grab a specific value from a JSON string without using a big library
    static String field(String json, String key) {
        String sq = "\"" + key + "\":\""; // Looking for "key":"value"
        String nq = "\"" + key + "\":";   // Looking for "key":123
        
        // Logic to find the key and extract the text between quotes
        int i = json.indexOf(sq);
        if (i >= 0) {
            i += sq.length();
            int j = i;
            while (j < json.length()) {
                // Stop at the ending quote, but skip escaped quotes like \"
                if (json.charAt(j) == '"' && json.charAt(j - 1) != '\\') break;
                j++;
            }
            return json.substring(i, j);
        }
        
        // Logic for numbers or booleans (no quotes)
        i = json.indexOf(nq);
        if (i >= 0) {
            i += nq.length();
            if (i < json.length() && json.charAt(i) == '"') return null;
            int j = i;
            while (j < json.length() && json.charAt(j) != ',' && json.charAt(j) != '}') j++;
            return json.substring(i, j).trim();
        }
        return null;
    }

    // Encodes text for URLs (replaces spaces with %20, etc.)
    static String urlEncode(String s) {
        try { return URLEncoder.encode(s, "UTF-8"); }
        catch (Exception e) { return s; }
    }
}