import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
public class TripPlannerFrame extends JFrame {
        public TripPlannerFrame(User owner, Consumer<Trip> onCreate) {
            setTitle("Plan New Trip");
            setSize(720, 820);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());
            getContentPane().setBackground(CLR_BG);
            add(headerPanel("✈  Plan Your Next Adventure", CLR_SUCCESS), BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(CLR_BG);
            form.setBorder(new EmptyBorder(16, 24, 8, 24));
            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(7, 8, 7, 8);
            g.fill = GridBagConstraints.HORIZONTAL;

            // --- Destination as combo box (with all known cities) ---
            //     plus the field is editable so users can type custom ones.
            Set<String> known = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            known.addAll(TouristDB.getInstance().knownCities());
            // Capitalize for display
            DefaultComboBoxModel<String> destModel = new DefaultComboBoxModel<>();
            destModel.addElement(""); // empty default
            for (String c : known) {
                destModel.addElement(toTitleCase(c));
            }
            JComboBox<String> destCB = new JComboBox<>(destModel);
            destCB.setEditable(true);
            destCB.setFont(FONT_BODY);

            JTextField countryF = new JTextField("Pakistan");
            JTextField daysF    = new JTextField();
            JTextField budgetF  = new JTextField();
            JTextField notesF   = new JTextField();
            JComboBox<String> intB = new JComboBox<>(
                new String[]{"adventure", "food", "historical",
                             "nature", "shopping", "cultural"});
            intB.setFont(FONT_BODY);

            styleField(countryF); styleField(daysF);
            styleField(budgetF); styleField(notesF);

            String[] labels = {
                "📍 Destination:", "🌍 Country:", "📅 Days:",
                "💵 Budget (Rs):", "🎯 Interest:", "📝 Notes (optional):"};
            JComponent[] fields = {destCB, countryF, daysF, budgetF, intB, notesF};

            for (int i = 0; i < fields.length; i++) {
                g.gridx = 0; g.gridy = i; g.gridwidth = 1; g.weightx = 0;
                form.add(lbl(labels[i]), g);
                g.gridx = 1; g.weightx = 1; form.add(fields[i], g);
            }

            JButton wBtn = styledBtn("🌤  Live Weather",                CLR_ACCENT);
            JButton pBtn = styledBtn("🗺  Discover Nearby Places",       new Color(0, 128, 80));
            JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
            btnRow.setBackground(CLR_BG); btnRow.add(wBtn); btnRow.add(pBtn);
            g.gridx = 0; g.gridy = 6; g.gridwidth = 2; g.weightx = 1; g.weighty = 0;
            g.fill = GridBagConstraints.HORIZONTAL;
            form.add(btnRow, g);

            JTextArea out = styledTA(); out.setRows(14);
            out.setText(
                "Enter a destination above, choose your interest, then:\n\n" +
                "  • Live Weather         — current conditions & travel tip\n" +
                "  • Discover Nearby Places — see what's there, filtered by your interest\n\n" +
                "When you click 'Create Trip', a customized day-by-day itinerary\n" +
                "is built using REAL places matched to your interest.\n\n" +
                "Try: Lahore (food), Hunza (nature), Murree (adventure),\n" +
                "     Skardu (adventure), Karachi (cultural), Peshawar (historical)\n");
            g.gridy = 7; g.weighty = 1; g.fill = GridBagConstraints.BOTH;
            JScrollPane sp = new JScrollPane(out);
            sp.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
            sp.setPreferredSize(new Dimension(0, 240));
            form.add(sp, g);
            add(form, BorderLayout.CENTER);

            JButton create = styledBtn("✅  Create Trip & Generate Itinerary", CLR_SUCCESS);
            create.setFont(new Font("Segoe UI", Font.BOLD, 15));
            JPanel south = new JPanel(new BorderLayout());
            south.setBackground(CLR_BG);
            south.setBorder(new EmptyBorder(8, 24, 16, 24));
            south.add(create);
            add(south, BorderLayout.SOUTH);

            PlacesService ps = new PlacesService();
            WeatherService ws = new WeatherService();

            wBtn.addActionListener(e -> {
                String city = currentCity(destCB);
                if (city.isEmpty()) {
                    out.setText("Please enter or select a destination first.");
                    return;
                }
                out.setText("⏳  Fetching live weather for " + city + "...");
                new Thread(() -> {
                    String w = ws.callAPI(city);
                    SwingUtilities.invokeLater(() -> {
                        out.setText(w);
                        out.setCaretPosition(0);
                    });
                }).start();
            });

            pBtn.addActionListener(e -> {
                String city     = currentCity(destCB);
                String interest = (String) intB.getSelectedItem();
                if (city.isEmpty()) {
                    out.setText("Please enter or select a destination first.");
                    return;
                }
                out.setText("⏳  Searching for " + interest + " places in " + city + "...");
                new Thread(() -> {
                    String r = ps.searchPlaces(city, interest);
                    SwingUtilities.invokeLater(() -> {
                        out.setText(r);
                        out.setCaretPosition(0);
                    });
                }).start();
            });

            create.addActionListener(e -> {
                final String dest;
                final String country;
                final String interest;
                final int dayCount;
                final double budget;
                final String notes;
                try {
                    dest = currentCity(destCB);
                    if (dest.isEmpty())
                        throw new InvalidTripException("Please enter a destination.");
                    country  = countryF.getText().trim();
                    interest = (String) intB.getSelectedItem();
                    dayCount = Integer.parseInt(daysF.getText().trim());
                    budget   = Double.parseDouble(budgetF.getText().trim());
                    notes    = notesF.getText().trim();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                        "⚠  Days must be a whole number (e.g. 5)\n" +
                        "    Budget must be a number (e.g. 50000)",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } catch (InvalidTripException ex) {
                    JOptionPane.showMessageDialog(this, "⚠  " + ex.getMessage(),
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                create.setEnabled(false);
                out.setText("⏳  Building " + interest + " itinerary for "
                    + dest + " — checking local places + live APIs...\n\nPlease wait.");

                new Thread(() -> {
                    try {
                        Destination d = new Destination(dest,
                            country.isEmpty() ? "Pakistan" : country, interest);
                        // Set coords from local CSV if available — for any
                        // future map-style enhancements.
                        double[] c = TouristDB.getInstance().getCoords(dest);
                        if (c != null) d.setCoords(c[0], c[1]);

                        final Trip trip = new Trip(owner.getUsername(),
                            d, dayCount, budget, interest);
                        Itinerary it = new RecommendationEngine().generateItinerary(trip);
                        trip.setItinerary(it);
                        if (!notes.isEmpty()) trip.setNotes(notes);

                        SwingUtilities.invokeLater(() -> {
                            onCreate.accept(trip);
                            JOptionPane.showMessageDialog(this,
                                "✅  Trip to " + dest + " created!\n\n" +
                                trip.getDays() + "-day " + interest +
                                " itinerary is ready.\n" +
                                "Click 'View Itinerary' on the dashboard to explore it.",
                                "Trip Created!", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        });
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() -> {
                            create.setEnabled(true);
                            JOptionPane.showMessageDialog(this,
                                "⚠  " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                }).start();
            });
        }

        private String currentCity(JComboBox<String> cb) {
            Object sel = cb.getEditor().getItem();
            if (sel == null) sel = cb.getSelectedItem();
            return sel == null ? "" : sel.toString().trim();
        }

        private static String toTitleCase(String s) {
            if (s == null || s.isEmpty()) return s;
            StringBuilder sb = new StringBuilder();
            boolean cap = true;
            for (char ch : s.toCharArray()) {
                if (Character.isWhitespace(ch) || ch == '-') { cap = true; sb.append(ch); }
                else { sb.append(cap ? Character.toUpperCase(ch) : ch); cap = false; }
            }
            return sb.toString();
        }
    }