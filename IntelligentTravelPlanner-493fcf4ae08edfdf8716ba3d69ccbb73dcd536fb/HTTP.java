import java.io.*;
import java.net.*;
import java.util.*;
public class HTTP {
        static String get(String urlStr) throws Exception {
            return get(urlStr, new HashMap<>());
        }
        static String get(String urlStr, Map<String, String> headers) throws Exception {
            HttpURLConnection c = open(urlStr, "GET", headers);
            return readResponse(c);
        }
        static String post(String urlStr, String body, Map<String, String> headers) throws Exception {
            HttpURLConnection c = open(urlStr, "POST", headers);
            c.setDoOutput(true);
            try (OutputStream os = c.getOutputStream()) { os.write(body.getBytes("UTF-8")); }
            return readResponse(c);
        }
        private static HttpURLConnection open(String urlStr, String method,
                Map<String, String> headers) throws Exception {
            HttpURLConnection c = (HttpURLConnection) new URL(urlStr).openConnection();
            c.setRequestMethod(method);
            c.setRequestProperty("User-Agent", "IntelligentTravelPlanner/5.0 (NUST-SEECS)");
            c.setConnectTimeout(8000); c.setReadTimeout(12000);
            if (headers != null) headers.forEach(c::setRequestProperty);
            return c;
        }
        private static String readResponse(HttpURLConnection c) throws Exception {
            int code = c.getResponseCode();
            InputStream is = (code < 400) ? c.getInputStream() : c.getErrorStream();
            if (is == null) return "{\"error\":\"No response stream, HTTP " + code + "\"}";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append('\n');
                return sb.toString();
            }
        }
        static String field(String json, String key) {
            String sq = "\"" + key + "\":\"";
            String nq = "\"" + key + "\":";
            int i = json.indexOf(sq);
            if (i >= 0) {
                i += sq.length();
                int j = i;
                while (j < json.length()) {
                    if (json.charAt(j) == '"' && json.charAt(j - 1) != '\\') break;
                    j++;
                }
                return json.substring(i, j);
            }
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
        static String urlEncode(String s) {
            try { return URLEncoder.encode(s, "UTF-8"); }
            catch (Exception e) { return s; }
        }
    }