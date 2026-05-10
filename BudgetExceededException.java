//Custom exception thrown when the user exceeds their budget limit.
public class BudgetExceededException extends Exception {

        // constructor passes message to parent Exception class
        public BudgetExceededException(String m) {
                super(m);
        }
}