import java.util.Collections;
import java.util.List;
public class UserManager {
        private static final String FILE = "data/users.dat";
        private final List<User> users;

        public UserManager() { users = FileHandler.load(FILE); }

        public void register(String u, String p, String e, boolean admin)
                throws AuthenticationException {
            if (u.trim().isEmpty() || p.trim().isEmpty())
                throw new AuthenticationException("Username and password cannot be empty.");
            for (User x : users)
                if (x.getUsername().equalsIgnoreCase(u))
                    throw new AuthenticationException("Username '" + u + "' is already taken.");
            if (p.length() < 6)
                throw new AuthenticationException("Password must be at least 6 characters.");
            users.add(admin ? new AdminUser(u, p, e) : new RegularUser(u, p, e));
            FileHandler.save(FILE, users);
        }

        public void register(String u, String p, String e) throws AuthenticationException {
            register(u, p, e, false);
        }

        public User login(String u, String p) throws AuthenticationException {
            for (User x : users)
                if (x.getUsername().equals(u) && x.getPassword().equals(p)) return x;
            throw new AuthenticationException("Invalid username or password.");
        }

        public List<User> getAllUsers() { return Collections.unmodifiableList(users); }
    }
