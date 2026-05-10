import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.util.List;
public class ItineraryViewerFrame extends JFrame {

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

        // Interest accent colors — used by day-cards in the new viewer
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

        private final Trip trip;

        public ItineraryViewerFrame(Trip t) {
            this.trip = t;
            setTitle("Itinerary — " + trip.getDestination());
            setSize(820, 760);
            setMinimumSize(new Dimension(680, 600));
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());
            getContentPane().setBackground(CLR_BG);

            // --- HEADER (interest-themed) ---
            Color accent = IntelligentTravelPlanner.interestAccent(trip.getInterest());
            add(IntelligentTravelPlanner.headerPanel("📋  " + trip.getDays() + "-Day "
                + capitalize(trip.getInterest()) + " Trip — "
                + trip.getDestination(), accent), BorderLayout.NORTH);

            // --- TRIP SUMMARY CARD ---
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
            sumTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            summary.add(sumTitle);
            summary.add(Box.createVerticalStrut(6));

            String summaryText = trip.getItinerary().getSummary();
            if (summaryText == null || summaryText.isEmpty()) {
                summaryText = trip.getDays() + "-day " + trip.getInterest()
                    + " trip to " + trip.getDestination().getName() + ".";
            }
            JLabel sumBody = new JLabel("<html><body style='width:680px'>"
                + escape(summaryText) + "</body></html>");
            sumBody.setFont(FONT_BODY);
            sumBody.setForeground(CLR_DARK);
            sumBody.setAlignmentX(Component.LEFT_ALIGNMENT);
            summary.add(sumBody);
            summary.add(Box.createVerticalStrut(8));

            JPanel meta = new JPanel(new GridLayout(1, 4, 8, 0));
            meta.setBackground(CLR_CARD);
            meta.add(metaTile("📅 Days", String.valueOf(trip.getDays())));
            meta.add(metaTile("💰 Budget",
                String.format("Rs. %,.0f", trip.getBudget())));
            meta.add(metaTile("🎯 Interest", capitalize(trip.getInterest())));
            meta.add(metaTile("🆔 Trip ID",
                trip.getTripId().substring(trip.getTripId().length() - 6)));
            meta.setAlignmentX(Component.LEFT_ALIGNMENT);
            meta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            summary.add(meta);

            if (!trip.getNotes().isEmpty()) {
                summary.add(Box.createVerticalStrut(8));
                JLabel notes = new JLabel("<html><body style='width:680px'>"
                    + "<b>📝 Your notes:</b> " + escape(trip.getNotes())
                    + "</body></html>");
                notes.setFont(FONT_DESC);
                notes.setForeground(new Color(80, 80, 100));
                notes.setAlignmentX(Component.LEFT_ALIGNMENT);
                summary.add(notes);
            }

            // --- DAY CARDS ---
            JPanel daysPanel = new JPanel();
            daysPanel.setLayout(new BoxLayout(daysPanel, BoxLayout.Y_AXIS));
            daysPanel.setBackground(CLR_BG);
            daysPanel.setBorder(new EmptyBorder(8, 20, 20, 20));

            daysPanel.add(summary);
            daysPanel.add(Box.createVerticalStrut(12));

            for (DayPlan d : trip.getItinerary().getDays()) {
                daysPanel.add(buildDayCard(d, accent));
                daysPanel.add(Box.createVerticalStrut(12));
            }

            // Footer message
            JLabel footer = new JLabel(
                "<html><center><i>🌟 Have an amazing trip — safe travels!</i></center></html>",
                SwingConstants.CENTER);
            footer.setFont(FONT_BODY);
            footer.setForeground(new Color(120, 120, 140));
            footer.setAlignmentX(Component.CENTER_ALIGNMENT);
            daysPanel.add(footer);

            JScrollPane scroll = new JScrollPane(daysPanel);
            scroll.setBorder(null);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.setBackground(CLR_BG);
            scroll.getViewport().setBackground(CLR_BG);
            add(scroll, BorderLayout.CENTER);

            // --- BOTTOM BAR ---
            JPanel southBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
            southBar.setBackground(CLR_BG);
            JButton exportBtn = IntelligentTravelPlanner.styledBtn("📄  Export to Text File", CLR_ACCENT);
            JButton closeBtn  = IntelligentTravelPlanner.styledBtn("✖  Close",                new Color(110, 110, 130));
            southBar.add(exportBtn);
            southBar.add(closeBtn);
            add(southBar, BorderLayout.SOUTH);

            exportBtn.addActionListener(e -> exportToText());
            closeBtn .addActionListener(e -> dispose());
        }

        /** Builds one styled day card for the itinerary list. */
        private JPanel buildDayCard(DayPlan day, Color accent) {
            Color bg = IntelligentTravelPlanner.interestBg(trip.getInterest());

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(CLR_CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 5, 0, 0, accent),
                    new EmptyBorder(0, 0, 0, 0))));

            // Day header bar
            JPanel head = new JPanel(new BorderLayout());
            head.setBackground(bg);
            head.setBorder(new EmptyBorder(10, 16, 10, 16));

            JLabel dayLabel = new JLabel("Day " + day.getDayNumber());
            dayLabel.setFont(FONT_DAY);
            dayLabel.setForeground(accent);
            head.add(dayLabel, BorderLayout.WEST);

            if (day.getTheme() != null && !day.getTheme().isEmpty()) {
                JLabel theme = new JLabel(day.getTheme());
                theme.setFont(FONT_SUBHEAD);
                theme.setForeground(new Color(60, 60, 80));
                theme.setHorizontalAlignment(SwingConstants.RIGHT);
                head.add(theme, BorderLayout.EAST);
            }
            card.add(head);

            // Activities
            JPanel acts = new JPanel();
            acts.setLayout(new BoxLayout(acts, BoxLayout.Y_AXIS));
            acts.setBackground(CLR_CARD);
            acts.setBorder(new EmptyBorder(8, 16, 14, 16));

            List<Activity> activities = day.getActivities();
            for (int i = 0; i < activities.size(); i++) {
                acts.add(buildActivityRow(activities.get(i), accent));
                if (i < activities.size() - 1) {
                    acts.add(Box.createVerticalStrut(2));
                    JSeparator sep = new JSeparator();
                    sep.setForeground(new Color(230, 235, 245));
                    sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                    acts.add(sep);
                    acts.add(Box.createVerticalStrut(2));
                }
            }
            card.add(acts);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            return card;
        }

        /** One activity row: emoji, time, title + description. */
        private JPanel buildActivityRow(Activity a, Color accent) {
            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setBackground(CLR_CARD);
            row.setBorder(new EmptyBorder(8, 4, 8, 4));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);

            // LEFT — emoji + time chip
            JPanel left = new JPanel();
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
            left.setBackground(CLR_CARD);
            left.setPreferredSize(new Dimension(86, 56));

            JLabel emoji = new JLabel(a.emoji, SwingConstants.CENTER);
            emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            emoji.setAlignmentX(Component.CENTER_ALIGNMENT);
            left.add(emoji);

            if (a.time != null && !a.time.isEmpty()) {
                JLabel time = new JLabel(a.time);
                time.setFont(FONT_SMALL);
                time.setForeground(accent);
                time.setAlignmentX(Component.CENTER_ALIGNMENT);
                left.add(time);
            }
            row.add(left, BorderLayout.WEST);

            // CENTER — slot, title, description
            JPanel center = new JPanel();
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            center.setBackground(CLR_CARD);

            if (a.slot != null && !a.slot.isEmpty()) {
                JLabel slot = new JLabel(a.slot.toUpperCase());
                slot.setFont(new Font("Segoe UI", Font.BOLD, 10));
                slot.setForeground(new Color(140, 140, 160));
                slot.setAlignmentX(Component.LEFT_ALIGNMENT);
                center.add(slot);
            }

            JLabel title = new JLabel(a.title);
            title.setFont(FONT_PLACE);
            title.setForeground(CLR_DARK);
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            center.add(title);

            if (a.description != null && !a.description.isEmpty()) {
                JLabel desc = new JLabel("<html><body style='width:560px'>"
                    + escape(a.description) + "</body></html>");
                desc.setFont(FONT_DESC);
                desc.setForeground(new Color(80, 80, 100));
                desc.setAlignmentX(Component.LEFT_ALIGNMENT);
                center.add(Box.createVerticalStrut(2));
                center.add(desc);
            }
            row.add(center, BorderLayout.CENTER);
            return row;
        }

        private JPanel metaTile(String label, String value) {
            JPanel tile = new JPanel();
            tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
            tile.setBackground(CLR_SOFT);
            tile.setBorder(new EmptyBorder(8, 10, 8, 10));
            JLabel l = new JLabel(label);
            l.setFont(new Font("Segoe UI", Font.BOLD, 11));
            l.setForeground(new Color(100, 100, 130));
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel v = new JLabel(value);
            v.setFont(FONT_SUBHEAD);
            v.setForeground(CLR_DARK);
            v.setAlignmentX(Component.LEFT_ALIGNMENT);
            tile.add(l);
            tile.add(v);
            return tile;
        }

        private void exportToText() {
            String fname = "itinerary_" + safeName(trip.getDestination().getName()) + ".txt";
            try (PrintWriter pw = new PrintWriter(new FileWriter(fname))) {
                pw.println("================================================");
                pw.println("  TRIP ITINERARY  —  " + trip.getDestination());
                pw.println("================================================");
                pw.println();
                pw.println("Destination : " + trip.getDestination());
                pw.println("Duration    : " + trip.getDays() + " days");
                pw.printf ("Budget      : Rs. %,.2f%n", trip.getBudget());
                pw.println("Interest    : " + capitalize(trip.getInterest()));
                pw.println("Trip ID     : " + trip.getTripId());
                if (!trip.getNotes().isEmpty())
                    pw.println("Notes       : " + trip.getNotes());
                pw.println();
                String s = trip.getItinerary().getSummary();
                if (s != null && !s.isEmpty()) {
                    pw.println("Summary: " + s);
                    pw.println();
                }
                for (DayPlan d : trip.getItinerary().getDays()) {
                    pw.println("------------------------------------------------");
                    pw.println(d);
                }
                pw.println("================================================");
                pw.println("Have a wonderful trip!");
                JOptionPane.showMessageDialog(this,
                    "✅  Saved to " + fname,
                    "Exported", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Export failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private static String safeName(String s) {
            return s == null ? "trip"
                : s.replaceAll("[^A-Za-z0-9]+", "_").toLowerCase();
        }

        private static String capitalize(String s) {
            if (s == null || s.isEmpty()) return "";
            return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
        }

        private static String escape(String s) {
            if (s == null) return "";
            return s.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
        }
    }
