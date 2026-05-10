//Destination class represents a travel destination.
//It stores basic info like name, country, type, and coordinates.

import java.io.Serializable;

public class Destination implements Serializable {

        private static final long serialVersionUID = 1L;

        // basic destination details
        private final String name;
        private final String country;
        private final String type;

        // coordinates (optional, set later)
        private double latitude;
        private double longitude;

        // constructor
        public Destination(String n, String c, String t) {
                name = n;
                country = c;
                type = t;
        }

        public String getName() {
                return name;
        }

        public String getCountry() {
                return country;
        }

        public String getType() {
                return type;
        }

        public double getLat() {
                return latitude;
        }

        public double getLon() {
                return longitude;
        }

        // set coordinates for map/route use
        public void setCoords(double lat, double lon) {
                latitude = lat;
                longitude = lon;
        }

        @Override
        public String toString() {
                return name + ", " + country;
        }
}