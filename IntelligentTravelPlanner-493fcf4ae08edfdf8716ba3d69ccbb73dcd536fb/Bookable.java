public interface Bookable {
        void book();
        void cancel();
        double getCost();
        boolean isConfirmed();
    }