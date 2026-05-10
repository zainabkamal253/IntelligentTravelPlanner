public interface APIService {

    // method that all API services must implement
    // takes a query string and returns API response
    String callAPI(String query) throws Exception;
}