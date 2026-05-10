import java.io.Serializable;
public class Destination implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String name, country, type;
        private double latitude, longitude;

        public Destination(String n, String c, String t) {
            name = n; country = c; type = t;
        }
        public String getName()    { return name; }
        public String getCountry() { return country; }
        public String getType()    { return type; }
        public double getLat()     { return latitude; }
        public double getLon()     { return longitude; }
        public void setCoords(double lat, double lon) { latitude = lat; longitude = lon; }
        @Override public String toString() { return name + ", " + country; }
    }