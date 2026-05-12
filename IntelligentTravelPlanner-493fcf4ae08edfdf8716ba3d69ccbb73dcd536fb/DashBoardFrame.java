import java.awt.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
public class DashBoardFrame extends JFrame {
        public static final Color CLR_PRIMARY  = new Color( 13,  71, 161);
        public static final Color CLR_ACCENT   = new Color( 25, 118, 210);
        public static final Color CLR_SUCCESS  = new Color( 27, 128,  60);
        public static final Color CLR_DANGER   = new Color(183,  28,  28);
        public static final Color CLR_PURPLE   = new Color(106,  27, 154);
        public static final Color CLR_TEAL     = new Color(  0, 121, 107);
        public static final Color CLR_ORANGE   = new Color(230, 100,   0);
        public static final Color CLR_DARK     = new Color( 18,  18,  30);
        public static final Color CLR_BG       = new Color(240, 244, 255);
        public static final Color CLR_CARD     = new Color(255, 255, 255);
        public static final Color CLR_BORDER   = new Color(180, 200, 235);
        public static final Color CLR_SOFT     = new Color(248, 250, 255);

        public static final Color CLR_ADV_BG   = new Color(255, 240, 230);
        public static final Color CLR_FOOD_BG  = new Color(255, 245, 225);
        public static final Color CLR_HIST_BG  = new Color(240, 235, 255);
        public static final Color CLR_NAT_BG   = new Color(232, 248, 235);
        public static final Color CLR_SHOP_BG  = new Color(252, 235, 245);
        public static final Color CLR_CULT_BG  = new Color(235, 245, 255);

        public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD,  24);
        public static final Font FONT_HEADER   = new Font("Segoe UI", Font.BOLD,  16);
        public static final Font FONT_SUBHEAD  = new Font("Segoe UI", Font.BOLD,  13);
        public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 13);
        public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
        public static final Font FONT_MONO     = new Font("Consolas", Font.PLAIN, 13);
        public static final Font FONT_BTN      = new Font("Segoe UI", Font.BOLD,  13);
        public static final Font FONT_DAY      = new Font("Segoe UI", Font.BOLD,  18);
        public static final Font FONT_PLACE    = new Font("Segoe UI", Font.BOLD,  14);
        public static final Font FONT_DESC     = new Font("Segoe UI", Font.PLAIN, 12);

        private final User user;
        private final List<Trip> trips;
        private final DefaultListModel<Trip> model = new DefaultListModel<>();
        private final JList<Trip> list = new JList<>(model);
        private final TitledBorder titledBorder;

        public DashBoardFrame(User u) {
            this.user = u;
            setTitle("Dashboard — Intelligent Travel Planner");
            setSize(1500, 680);
            setMinimumSize(new Dimension(1500, 600));
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout(10, 10));
            getContentPane().setBackground(CLR_BG);

            // HEADER
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(CLR_PRIMARY);
            header.setBorder(new EmptyBorder(15, 20, 15, 20));
            JLabel welcome = new JLabel("Welcome back, " + user.getUsername() + "!");
            welcome.setFont(FONT_TITLE);
            welcome.setForeground(Color.WHITE);
            header.add(welcome, BorderLayout.WEST);
            JLabel roleLabel = new JLabel("  " + user.getRole() + "  ");
            roleLabel.setFont(FONT_HEADER);
            roleLabel.setForeground(Color.WHITE);
            roleLabel.setOpaque(true);
            roleLabel.setBackground(user.getRoleColor());
            roleLabel.setBorder(new EmptyBorder(6, 12, 6, 12));
            header.add(roleLabel, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);

            // TRIPS LIST
            trips = FileHandler.load("data/trips_" + user.getUsername() + ".dat");
            for (Trip t : trips) model.addElement(t);
            list.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            list.setFixedCellHeight(45);
            list.setSelectionBackground(new Color(200, 220, 255));
            list.setBackground(CLR_CARD);

            titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1),
                "  Your Saved Trips (" + trips.size() + " total)",
                TitledBorder.LEFT, TitledBorder.TOP, FONT_HEADER, CLR_PRIMARY);

            JScrollPane sp = new JScrollPane(list);
            sp.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 20, 10, 20), titledBorder));
            add(sp, BorderLayout.CENTER);

            // BUTTONS — single FlowLayout, no duplicate-add bug
            JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            bp.setBackground(CLR_BG);
            bp.setBorder(new EmptyBorder(5, 10, 15, 10));

            JButton bN = IntelligentTravelPlanner.styledBtn(" New Trip",        CLR_SUCCESS);
            JButton bV = IntelligentTravelPlanner.styledBtn(" View Itinerary",  CLR_ACCENT);
            JButton bB = IntelligentTravelPlanner.styledBtn(" Budget",          CLR_ORANGE);
            JButton bK = IntelligentTravelPlanner.styledBtn(" Bookings",        CLR_PURPLE);
            JButton bR = IntelligentTravelPlanner.styledBtn(" Route Planner",   CLR_TEAL);
            JButton bD = IntelligentTravelPlanner.styledBtn(" Delete",          CLR_DANGER);
            JButton bL = IntelligentTravelPlanner.styledBtn(" Logout",          new Color(90, 90, 110));

            Dimension btnSize = new Dimension(150, 42);
            for (JButton b : new JButton[]{bN, bV, bB, bK, bR, bD, bL}) {
                b.setPreferredSize(btnSize);
                b.setMinimumSize(btnSize);
                bp.add(b);
            }
            add(bp, BorderLayout.SOUTH);

            bN.addActionListener(e -> new TripPlannerFrame(user, t -> {
                trips.add(t);
                model.addElement(t);
                save();
                titledBorder.setTitle("  Your Saved Trips (" + trips.size() + " total)");
                sp.repaint();
            }).setVisible(true));

            bV.addActionListener(e -> {
                Trip t = list.getSelectedValue();
                if (t == null) {
                    JOptionPane.showMessageDialog(this, "Select a trip first.",
                        "Selection needed", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                new ItineraryViewerFrame(t).setVisible(true);
            });

            bB.addActionListener(e -> {
                Trip t = list.getSelectedValue();
                if (t == null) {
                    JOptionPane.showMessageDialog(this, "Select a trip first.",
                        "Selection needed", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                new BudgetTrackerFrame(t, this::save).setVisible(true);
            });

            bK.addActionListener(e -> {
                Trip t = list.getSelectedValue();
                if (t == null) {
                    JOptionPane.showMessageDialog(this, "Select a trip first.",
                        "Selection needed", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                new BookingFrame(t, this::save).setVisible(true);
            });

            bR.addActionListener(e -> {
                RoutePlanner rp = new RoutePlanner();
                String[] cities = rp.getCities().toArray(new String[0]);
                Arrays.sort(cities);
                new RouteFrame(rp, cities).setVisible(true);
            });

            bD.addActionListener(e -> {
                Trip t = list.getSelectedValue();
                if (t == null) return;
                int opt = JOptionPane.showConfirmDialog(this,
                    "Delete trip to " + t.getDestination() + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                    trips.remove(t); model.removeElement(t); save();
                    titledBorder.setTitle("  Your Saved Trips (" + trips.size() + " total)");
                    sp.repaint();
                }
            });

            bL.addActionListener(e -> {
                new LoginFrame(new UserManager()).setVisible(true);
                dispose();
            });
        }

        private void save() {
            FileHandler.save("data/trips_" + user.getUsername() + ".dat", trips);
        }
    }