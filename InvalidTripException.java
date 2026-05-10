// Custom exception class to handle errors when a trip is set up incorrectly
public class InvalidTripException extends Exception {
    // Constructor that takes an error message and passes it to the main Exception class
    public InvalidTripException(String m) { 
        super(m); 
    }
}