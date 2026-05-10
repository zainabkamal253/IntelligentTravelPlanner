import java.util.List;

// This interface is used for recommendation system
// Any class implementing this will provide tourist place suggestions
public interface Recommendable {
        // returns list of recommended tourist places based on destination and interest
        List<TouristPlace> recommend(String destination, String interest);
}