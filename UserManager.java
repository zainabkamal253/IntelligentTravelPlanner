import java.util.Collections;
import java.util.List;

// class used to manage users (register/login)
public class UserManager {

        // file where user data is stored
        private static final String FILE = "data/users.dat";

        // list of all users
        private final List<User> users;

        // constructor loads saved users from file
        public UserManager() {
            users = FileHandler.load(FILE);
        }

        // method for registering a new user
        public void register(String u, String p, String e, boolean admin)
                throws AuthenticationException {

            // check if username or password is empty
            if (u.trim().isEmpty() || p.trim().isEmpty())
                throw new AuthenticationException("Username and password cannot be empty.");

            // check if username already exists
            for (User x : users)
                if (x.getUsername().equalsIgnoreCase(u))
                    throw new AuthenticationException("Username '" + u + "' is already taken.");

            // password must be at least 6 characters
            if (p.length() < 6)
                throw new AuthenticationException("Password must be at least 6 characters.");

            // add user to list (admin or regular user)
            users.add(admin ? new AdminUser(u, p, e) : new RegularUser(u, p, e));

            // save updated user list to file
            FileHandler.save(FILE, users);
        }

        // overloaded register method for normal users
        public void register(String u, String p, String e)
                throws AuthenticationException {

            register(u, p, e, false);
        }

        // method for user login
        public User login(String u, String p) throws AuthenticationException {

            // check all users
            for (User x : users)

                // if username and password match
                if (x.getUsername().equals(u) && x.getPassword().equals(p))
                    return x;

            // if no match found
            throw new AuthenticationException("Invalid username or password.");
        }

        // returns read-only list of users
        public List<User> getAllUsers() {
            return Collections.unmodifiableList(users);
        }
    }