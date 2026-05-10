import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.util.List;

// This class shows the full itinerary in a GUI window
public class ItineraryViewerFrame extends JFrame {

        // Main color theme used in the UI
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

        // Background colors based on trip interest
        public static final Color CLR_ADV_BG   = new Color(255, 240, 230);
        public static final Color CLR_FOOD_BG  = new Color(255, 245, 225);
        public static final Color CLR_HIST_BG  = new Color(240, 235, 255);
        public static final Color CLR_NAT_BG   = new Color(232, 248, 235);
        public static final Color CLR_SHOP_BG  = new Color(252, 235, 245);
        public static final Color CLR_CULT_BG  = new Color(235, 245, 255);

        // Fonts used in the UI
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

        private final Trip trip;

        public ItineraryViewerFrame(Trip t) {
            this.trip = t;

            // Window setup
            setTitle("Itinerary — " + trip.getDestination());
            setSize(820, 760);
            setMinimumSize(new Dimension(680, 600));
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());
            getContentPane().setBackground(CLR_BG);

            // Header with trip info
            Color accent = IntelligentTravelPlanner.interestAccent(trip.getInterest());
            add(IntelligentTravelPlanner.headerPanel(
                    "  " + trip.getDays() + "-Day " + capitalize(trip.getInterest())
                            + " Trip — " + trip.getDestination(),
                    accent),
                BorderLayout.NORTH);

            // Summary section of the trip
            JPanel summary = new JPanel();
            summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
            summary.setBackground(CLR_CARD);
            summary.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(15, 20, 8, 20),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                    new EmptyBorder(12, 16, 12, 16))));

            JLabel sumTitle = new JLabel("Trip Overview");
            sumTitle.setFont(FONT_HEADER);
            sumTitle.setForeground(accent);
            summary.add(sumTitle);

            String summaryText = trip.getItinerary().getSummary();
            if (summaryText == null || summaryText.isEmpty()) {
                summaryText = trip.getDays() + "-day " + trip.getInterest()
                        + " trip to " + trip.getDestination().getName() + ".";
            }

            JLabel sumBody = new JLabel("<html><body style='width:680px'>"
                    + escape(summaryText) + "</body></html>");
            sumBody.setFont(FONT_BODY);
            summary.add(sumBody);

            // Meta info like budget, days, etc.
            JPanel meta = new JPanel(new GridLayout(1, 4, 8, 0));
            meta.setBackground(CLR_CARD);
            meta.add(metaTile(" Days", String.valueOf(trip.getDays())));
            meta.add(metaTile(" Budget", String.format("Rs. %,.0f", trip.getBudget())));
            meta.add(metaTile(" Interest", capitalize(trip.getInterest())));
            meta.add(metaTile(" Trip ID",
                    trip.getTripId().substring(trip.getTripId().length() - 6)));
            summary.add(meta);

            // Notes section if user added notes
            if (!trip.getNotes().isEmpty()) {
                JLabel notes = new JLabel("<html><body style='width:680px'>"
                        + "<b>Your notes:</b> " + escape(trip.getNotes())
                        + "</body></html>");
                summary.add(notes);
            }

            // Main container for all day plans
            JPanel daysPanel = new JPanel();
            daysPanel.setLayout(new BoxLayout(daysPanel, BoxLayout.Y_AXIS));
            daysPanel.setBackground(CLR_BG);

            daysPanel.add(summary);

            // Add each day card
            for (DayPlan d : trip.getItinerary().getDays()) {
                daysPanel.add(buildDayCard(d, accent));
                daysPanel.add(Box.createVerticalStrut(12));
            }

            // Scroll support
            JScrollPane scroll = new JScrollPane(daysPanel);
            scroll.setBorder(null);
            add(scroll, BorderLayout.CENTER);

            // Bottom buttons
            JPanel southBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
            JButton exportBtn = IntelligentTravelPlanner.styledBtn("Export", CLR_ACCENT);
            JButton closeBtn  = IntelligentTravelPlanner.styledBtn("Close", new Color(110, 110, 130));

            southBar.add(exportBtn);
            southBar.add(closeBtn);
            add(southBar, BorderLayout.SOUTH);

            exportBtn.addActionListener(e -> exportToText());
            closeBtn.addActionListener(e -> dispose());
        }

        // Builds a single day card UI
        private JPanel buildDayCard(DayPlan day, Color accent) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(CLR_CARD);
            card.setBorder(BorderFactory.createLineBorder(CLR_BORDER));

            JLabel dayLabel = new JLabel("Day " + day.getDayNumber());
            dayLabel.setFont(FONT_DAY);
            dayLabel.setForeground(accent);
            card.add(dayLabel);

            // Loop through all activities of the day
            List<Activity> activities = day.getActivities();
            for (Activity a : activities) {
                card.add(buildActivityRow(a, accent));
            }

            return card;
        }

        // Builds one activity row (emoji, title, description)
        private JPanel buildActivityRow(Activity a, Color accent) {
            JPanel row = new JPanel(new BorderLayout());

            JLabel emoji = new JLabel(a.emoji);
            row.add(emoji, BorderLayout.WEST);

            JLabel title = new JLabel(a.title);
            row.add(title, BorderLayout.CENTER);

            return row;
        }

        // Meta box UI
        private JPanel metaTile(String label, String value) {
            JPanel tile = new JPanel();
            tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));

            tile.add(new JLabel(label));
            tile.add(new JLabel(value));

            return tile;
        }

        // Export itinerary to text file
        private void exportToText() {
            try (PrintWriter pw = new PrintWriter(new FileWriter("itinerary.txt"))) {
                pw.println(trip.getItinerary().toString());
                JOptionPane.showMessageDialog(this, "Exported successfully");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Export failed");
            }
        }

        private static String capitalize(String s) {
            if (s == null || s.isEmpty()) return "";
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }

        private static String escape(String s) {
            if (s == null) return "";
            return s.replace("<", "&lt;").replace(">", "&gt;");
        }
    }