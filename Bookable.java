public interface Bookable {

    // methods related to booking functionality
    void book();
    void cancel();
    double getCost();
    boolean isConfirmed();
}