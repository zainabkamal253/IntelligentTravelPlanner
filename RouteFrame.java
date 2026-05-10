import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
public class RouteFrame extends JFrame {

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

        private final JComboBox<String> from, to;
        private final JTextArea out;

        public RouteFrame(RoutePlanner rp, String[] cities) {
            setTitle("Route Planner — Intelligent Travel Planner");
            setSize(950, 700);
            setMinimumSize(new Dimension(850, 600));
            setLocationRelativeTo(null);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLayout(new BorderLayout(10, 10));
            getContentPane().setBackground(CLR_BG);

            add(IntelligentTravelPlanner.headerPanel("🗺  Route Planner", CLR_TEAL), BorderLayout.NORTH);

            out = IntelligentTravelPlanner.styledTA();
            String cityGrid = formatGrid(cities, 4, 22);
            out.setText("╔══════════════════════════════════════════════════════════╗\n" +
                        "║  " + String.format("%-56s", cities.length + " CITIES IN THE NETWORK") + "║\n" +
                        "╚══════════════════════════════════════════════════════════╝\n\n" +
                        cityGrid + "\n\nSelect origin and destination below, then click Find Route.");

            JScrollPane scroll = new JScrollPane(out);
            scroll.setBorder(new EmptyBorder(10, 20, 5, 20));
            add(scroll, BorderLayout.CENTER);

            JPanel bottom = new JPanel(new BorderLayout(0, 6));
            bottom.setBackground(CLR_BG);
            bottom.setBorder(new EmptyBorder(0, 10, 12, 10));

            JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            row1.setBackground(CLR_BG);
            from = new JComboBox<>(cities); from.setFont(FONT_BODY);
            to   = new JComboBox<>(cities); to.setFont(FONT_BODY);
            if (cities.length > 1) to.setSelectedIndex(1);
            row1.add(IntelligentTravelPlanner.lbl("Origin:"));      row1.add(from);
            row1.add(IntelligentTravelPlanner.lbl("Destination:")); row1.add(to);
            bottom.add(row1, BorderLayout.NORTH);

            JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            row2.setBackground(CLR_BG);
            JButton findBtn  = IntelligentTravelPlanner.styledBtn("🔍  Find Route", CLR_SUCCESS);
            JButton closeBtn = IntelligentTravelPlanner.styledBtn("✖  Close",       CLR_DANGER);
            findBtn.setPreferredSize(new Dimension(160, 42));
            closeBtn.setPreferredSize(new Dimension(140, 42));
            row2.add(findBtn);
            row2.add(closeBtn);
            bottom.add(row2, BorderLayout.SOUTH);

            add(bottom, BorderLayout.SOUTH);

            findBtn.addActionListener(e -> {
                String origin = (String) from.getSelectedItem();
                String dest   = (String) to.getSelectedItem();
                if (origin == null || dest == null) return;
                if (origin.equals(dest)) {
                    JOptionPane.showMessageDialog(this,
                        "Origin and Destination cannot be the same!",
                        "Invalid Route", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String[] result = rp.shortestRoute(origin, dest);
                if ("ERROR".equals(result[0])) {
                    JOptionPane.showMessageDialog(this, result[1],
                        "Route Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    out.setText(result[1]);
                    out.setCaretPosition(0);
                }
            });

            closeBtn.addActionListener(e -> dispose());
        }

        private String formatGrid(String[] c, int cols, int width) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < c.length; i++) {
                sb.append(String.format("  %-" + width + "s", c[i]));
                if ((i + 1) % cols == 0) sb.append('\n');
            }
            return sb.toString();
        }
    }
