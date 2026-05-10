import java.io.Serializable;

/**
 * This class represents a tourist place in a city.
 * It stores basic info like name, category and description.
 */
public class TouristPlace implements Serializable {
    private static final long serialVersionUID = 1L;

    // basic details of a tourist place
    private final String name, category, description, city;

    public TouristPlace(String name, String category, String description, String city) {
        this.name = name;
        this.category = category;

        // safety check in case description is null
        this.description = description == null ? "" : description;

        // safety check in case city is null
        this.city = city == null ? "" : city;
    }

    public String getName()        { return name; }
    public String getCategory()    { return category; }
    public String getDescription() { return description; }
    public String getCity()        { return city; }

    // simple string representation used in printing/debugging
    @Override
    public String toString() {
        return name + " — " + description;
    }
}