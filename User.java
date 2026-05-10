import java.awt.Color;
import java.io.Serializable;
import java.time.LocalDate;

// abstract parent class for all users
public abstract class User implements Serializable {

        // serial version id for serialization
        private static final long serialVersionUID = 2L;

        // user data fields
        private final String username;
        private String password;
        private final String email;
        private final LocalDate registeredOn;

        // constructor to initialize user details
        protected User(String u, String p, String e) {
            username = u;
            password = p;
            email = e;

            // saves current registration date
            registeredOn = LocalDate.now();
        }

        // getter methods
        public String    getUsername()     { return username; }
        public String    getPassword()     { return password; }
        public String    getEmail()        { return email; }
        public LocalDate getRegisteredOn() { return registeredOn; }

        // abstract methods implemented in child classes
        public abstract String getRole();
        public abstract Color  getRoleColor();

        // returns username with role
        @Override
        public String toString() {
            return username + " [" + getRole() + "]";
        }
    }