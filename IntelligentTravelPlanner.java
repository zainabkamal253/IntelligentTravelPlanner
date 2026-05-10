/*
 * ====================================================================
 *  INTELLIGENT TRAVEL PLANNER — OOP Semester Project (Single File)
 *  Team: Muhammad Saadain Zahid, Armish Bibi, Zainab Kamal
 *  SEECS NUST BESE-16-A
 * --------------------------------------------------------------------
 *  v5.0 — Major Improvements:
 *   1. Interest-aware itineraries: "adventure in Lahore" gives
 *      adventure-specific places, not generic heritage sites.
 *   2. Local CSV (tourist_spots.csv) is now the PRIMARY data source —
 *      instant, accurate, offline-capable. APIs only used as enrichment.
 *   3. Local pakistan_cities.csv used for instant geocoding (no slow
 *      Nominatim call required for Pakistani cities).
 *   4. Itinerary Viewer redesigned with day-cards, descriptions,
 *      times-of-day, photos-style emoji icons, and travel tips.
 *   5. Activities are varied per day — no repetitive
 *      "Morning: Visit X / Afternoon: Explore Y" templates.
 *   6. Bug fixes: PlacesService class structure repaired, duplicate
 *      Dashboard buttons removed, validation tightened.
 * ====================================================================
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.*;

public class IntelligentTravelPlanner {

    public static final String WEATHER_API_KEY  = "2a01a9a5453a48a78cc113850260305";
    public static final String FOURSQUARE_TOKEN = "3KW001NN1DF5VL3L0SWFZKR1X4AEJGFDB5W0TPHQ1SHOQN54";

    // Feature flags
    public static final boolean USE_REAL_WEATHER = true;
    public static final boolean USE_FOURSQUARE   = true;

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

    public static void main(String[] args) {
        new File("data").mkdirs();
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        UserManager um = new UserManager();
        try { um.register("admin", "admin123", "admin@nust.edu.pk", true); }
        catch (AuthenticationException ignored) {}
        // Pre-load tourist DB so first trip creation feels instant
        TouristDB.getInstance();
        SwingUtilities.invokeLater(() -> new LoginFrame(um).setVisible(true));
    }
  
    static JButton styledBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setMargin(new Insets(8, 15, 8, 15));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    static JPanel headerPanel(String title, Color bg) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);
        p.setBorder(new EmptyBorder(16, 24, 16, 24));
        JLabel l = new JLabel(title, SwingConstants.CENTER);
        l.setFont(FONT_TITLE); l.setForeground(Color.WHITE);
        p.add(l);
        return p;
    }

    static JTextArea styledTA() {
        JTextArea ta = new JTextArea();
        ta.setFont(FONT_MONO); ta.setEditable(false);
        ta.setMargin(new Insets(12, 14, 12, 14));
        ta.setBackground(new Color(250, 252, 255));
        ta.setForeground(CLR_DARK);
        ta.setLineWrap(true); ta.setWrapStyleWord(true);
        return ta;
    }

    static JLabel lbl(String t) {
        JLabel l = new JLabel(t); l.setFont(FONT_BODY); l.setForeground(CLR_DARK); return l;
    }

    static void styleField(JTextField f) {
        f.setFont(FONT_BODY); f.setPreferredSize(new Dimension(200, 34));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CLR_BORDER, 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
    }

    static JScrollPane scrollWrap(JTextArea ta) {
        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
        return sp;
    }

    /** Background color matching the trip's interest theme. */
    static Color interestBg(String interest) {
        if (interest == null) return CLR_SOFT;
        switch (interest.toLowerCase()) {
            case "adventure":  return CLR_ADV_BG;
            case "food":       return CLR_FOOD_BG;
            case "historical": return CLR_HIST_BG;
            case "nature":     return CLR_NAT_BG;
            case "shopping":   return CLR_SHOP_BG;
            case "cultural":   return CLR_CULT_BG;
            default:           return CLR_SOFT;
        }
    }

    /** Accent color matching the interest. */
    static Color interestAccent(String interest) {
        if (interest == null) return CLR_PRIMARY;
        switch (interest.toLowerCase()) {
            case "adventure":  return CLR_ORANGE;
            case "food":       return new Color(204, 102, 0);
            case "historical": return CLR_PURPLE;
            case "nature":     return CLR_SUCCESS;
            case "shopping":   return new Color(190, 30, 100);
            case "cultural":   return CLR_TEAL;
            default:           return CLR_PRIMARY;
        }
    }

}