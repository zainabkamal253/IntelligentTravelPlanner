//DashboardFrame is the main screen of the application.
//It shows all saved trips and provides navigation to other modules.
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.Arrays;

public class DashBoardFrame extends JFrame {

        // UI colors
        public static final Color CLR_PRIMARY  = new Color(13, 71, 161);
        public static final Color CLR_ACCENT   = new Color(25, 118, 210);
        public static final Color CLR_SUCCESS  = new Color(27, 128, 60);
        public static final Color CLR_DANGER   = new Color(183, 28, 28);
        public static final Color CLR_PURPLE   = new Color(106, 27, 154);
        public static final Color CLR_TEAL     = new Color(0, 121, 107);
        public static final Color CLR_ORANGE   = new Color(230, 100, 0);
        public static final Color CLR_BG       = new Color(240, 244, 255);
        public static final Color CLR_CARD     = new Color(255, 255, 255);
        public static final Color CLR_BORDER   = new Color(180, 200, 235);

        // fonts
        public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 24);
        public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
        public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);

        private final User user;
        private final List<Trip> trips;
        private final DefaultListModel<Trip> model = new DefaultListModel<>();
        private final JList<Trip> list = new JList<>(model);
        private final TitledBorder titledBorder;

        public DashBoardFrame(User u) {

            this.user = u;

            setTitle("Dashboard — Travel Planner");
            setSize(1500, 680);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout(10, 10));
            getContentPane().setBackground(CLR_BG);

            // HEADER SECTION
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(CLR_PRIMARY);

            JLabel welcome = new JLabel("Welcome, " + user.getUsername());
            welcome.setFont(FONT_TITLE);
            welcome.setForeground(Color.WHITE);
            header.add(welcome, BorderLayout.WEST);

            JLabel roleLabel = new JLabel(user.getRole());
            roleLabel.setForeground(Color.WHITE);
            roleLabel.setOpaque(true);
            roleLabel.setBackground(user.getRoleColor());
            header.add(roleLabel, BorderLayout.EAST);

            add(header, BorderLayout.NORTH);

            // LOAD TRIPS
            trips = FileHandler.load("data/trips_" + user.getUsername() + ".dat");
            for (Trip t : trips) model.addElement(t);

            list.setFont(FONT_BODY);
            list.setFixedCellHeight(45);
            list.setBackground(CLR_CARD);

            titledBorder = BorderFactory.createTitledBorder(
                    "Your Trips (" + trips.size() + ")"
            );

            JScrollPane sp = new JScrollPane(list);
            sp.setBorder(titledBorder);

            add(sp, BorderLayout.CENTER);

            // BUTTON PANEL
            JPanel bp = new JPanel(new FlowLayout());

            JButton newTrip = new JButton("New Trip");
            JButton view    = new JButton("View");
            JButton budget  = new JButton("Budget");
            JButton book    = new JButton("Bookings");
            JButton route   = new JButton("Route");
            JButton delete  = new JButton("Delete");
            JButton logout  = new JButton("Logout");

            bp.add(newTrip);
            bp.add(view);
            bp.add(budget);
            bp.add(book);
            bp.add(route);
            bp.add(delete);
            bp.add(logout);

            add(bp, BorderLayout.SOUTH);

            // NEW TRIP
            newTrip.addActionListener(e -> {
                new TripPlannerFrame(user, t -> {
                    trips.add(t);
                    model.addElement(t);
                    save();
                }).setVisible(true);
            });

            // VIEW ITINERARY
            view.addActionListener(e -> {
                Trip t = list.getSelectedValue();
                if (t != null) new ItineraryViewerFrame(t).setVisible(true);
            });

            // BUDGET
            budget.addActionListener(e -> {
                Trip t = list.getSelectedValue();
                if (t != null) new BudgetTrackerFrame(t, this::save).setVisible(true);
            });

            // BOOKINGS
            book.addActionListener(e -> {
                Trip t = list.getSelectedValue();
                if (t != null) new BookingFrame(t, this::save).setVisible(true);
            });

            // ROUTE
            route.addActionListener(e -> {
                RoutePlanner rp = new RoutePlanner();
                String[] cities = rp.getCities().toArray(new String[0]);
                Arrays.sort(cities);
                new RouteFrame(rp, cities).setVisible(true);
            });

            // DELETE
            delete.addActionListener(e -> {
                Trip t = list.getSelectedValue();
                if (t != null) {
                    trips.remove(t);
                    model.removeElement(t);
                    save();
                }
            });

            // LOGOUT
            logout.addActionListener(e -> {
                new LoginFrame(new UserManager()).setVisible(true);
                dispose();
            });
        }

        // save trips to file
        private void save() {
            FileHandler.save("data/trips_" + user.getUsername() + ".dat", trips);
        }
}