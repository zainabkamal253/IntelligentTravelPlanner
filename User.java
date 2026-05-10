import java.awt.Color;
import java.io.Serializable;
import java.time.LocalDate;
public abstract class User implements Serializable {
        private static final long serialVersionUID = 2L;
        private final String username;
        private String password;
        private final String email;
        private final LocalDate registeredOn;

        protected User(String u, String p, String e) {
            username = u; password = p; email = e;
            registeredOn = LocalDate.now();
        }
        public String    getUsername()     { return username; }
        public String    getPassword()     { return password; }
        public String    getEmail()        { return email; }
        public LocalDate getRegisteredOn() { return registeredOn; }
        public abstract String getRole();
        public abstract Color  getRoleColor();
        @Override public String toString() { return username + " [" + getRole() + "]"; }
    }
