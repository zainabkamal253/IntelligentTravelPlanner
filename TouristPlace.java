import java.io.Serializable;
public class TouristPlace implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String name, category, description, city;
        private final String emoji;

        public TouristPlace(String name, String category, String description, String city) {
            this.name = name;
            this.category = category;
            this.description = description == null ? "" : description;
            this.city = city == null ? "" : city;
            this.emoji = pickEmoji(name, category);
        }

        public String getName()        { return name; }
        public String getCategory()    { return category; }
        public String getDescription() { return description; }
        public String getCity()        { return city; }
        public String getEmoji()       { return emoji; }

        private static String pickEmoji(String name, String cat) {
            String n = (name == null ? "" : name).toLowerCase();
            String c = (cat  == null ? "" : cat).toLowerCase();

            if (c.equals("food"))                                   return "🍽";
            if (c.equals("adventure"))                              return "🧗";
            if (c.equals("shopping"))                               return "🛍";
            if (n.contains("mosque") || n.contains("masjid"))       return "🕌";
            if (n.contains("fort"))                                 return "🏰";
            if (n.contains("museum"))                               return "🏛";
            if (n.contains("garden") || n.contains("park"))         return "🌿";
            if (n.contains("lake"))                                 return "🏞";
            if (n.contains("peak") || n.contains("top")
                    || n.contains("pass") || n.contains("hill"))    return "⛰";
            if (n.contains("valley"))                               return "🏔";
            if (n.contains("beach"))                                return "🏖";
            if (n.contains("shrine") || n.contains("darbar")
                    || n.contains("temple"))                        return "🛕";
            if (n.contains("bazaar") || n.contains("market"))       return "🛒";
            if (c.equals("nature"))                                 return "🌄";
            if (c.equals("historical"))                             return "📜";
            if (c.equals("cultural"))                               return "🎭";
            return "📍";
        }

        @Override public String toString() {
            return emoji + " " + name + " — " + description;
        }
    }
