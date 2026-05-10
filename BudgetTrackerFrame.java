import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Map;
public class BudgetTrackerFrame extends JFrame {
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
            
        private final BudgetManager bm;
        private final Trip trip;
        private final Runnable onSave;
        private JTextArea ta;

        public BudgetTrackerFrame(Trip trip, Runnable onSave) {
            this.trip = trip;
            this.bm = new BudgetManager(trip);
            this.onSave = onSave;
            setTitle("Budget — " + trip.getDestination());
            setSize(720, 680);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());
            getContentPane().setBackground(CLR_BG);
            add(IntelligentTravelPlanner.headerPanel("💰  Budget Tracker — " + trip.getDestination(), CLR_ORANGE),
                BorderLayout.NORTH);

            ta = IntelligentTravelPlanner.styledTA();
            refresh();
            add(IntelligentTravelPlanner.scrollWrap(ta), BorderLayout.CENTER);

            JPanel inp = new JPanel(new GridBagLayout());
            inp.setBackground(CLR_BG);
            inp.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(8, 10, 12, 10),
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(CLR_BORDER),
                    "  ➕  Add New Expense",
                    TitledBorder.LEFT, TitledBorder.TOP, FONT_HEADER, CLR_ORANGE)));

            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(5, 6, 5, 6);
            g.fill = GridBagConstraints.HORIZONTAL;

            JComboBox<String> catCB = new JComboBox<>(new String[]{
                "Accommodation", "Food", "Transport", "Activities",
                "Shopping", "Health", "Miscellaneous"});
            catCB.setFont(FONT_BODY);
            JTextField amtF  = new JTextField(8);  amtF.setFont(FONT_BODY);
            JTextField noteF = new JTextField(16); noteF.setFont(FONT_BODY);
            JButton addBtn   = IntelligentTravelPlanner.styledBtn("➕  Add", CLR_SUCCESS);

            g.gridx = 0; g.gridy = 0; inp.add(IntelligentTravelPlanner.lbl("Category:"), g);
            g.gridx = 1; inp.add(catCB, g);
            g.gridx = 2; inp.add(IntelligentTravelPlanner.lbl("Amount (Rs):"), g);
            g.gridx = 3; inp.add(amtF, g);
            g.gridx = 0; g.gridy = 1; inp.add(IntelligentTravelPlanner.lbl("Note:"), g);
            g.gridx = 1; g.gridwidth = 2; inp.add(noteF, g);
            g.gridx = 3; g.gridwidth = 1; inp.add(addBtn, g);
            add(inp, BorderLayout.SOUTH);

            addBtn.addActionListener(e -> {
                try {
                    if (amtF.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Amount is required.");
                        return;
                    }
                    double amt = Double.parseDouble(amtF.getText().trim());
                    if (amt <= 0) {
                        JOptionPane.showMessageDialog(this, "Amount must be positive.");
                        return;
                    }
                    bm.addExpense(new Expense(
                        (String) catCB.getSelectedItem(), amt, noteF.getText().trim()));
                    amtF.setText(""); noteF.setText("");
                } catch (BudgetExceededException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Budget Alert!", JOptionPane.WARNING_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Amount must be a number.");
                }
                refresh();
                onSave.run();
            });
        }

        void refresh() {
            double total  = bm.totalSpent();
            double budget = trip.getBudget();
            double remain = bm.remaining();
            double pct    = bm.percentUsed();
            int    bar    = (int) (Math.min(pct / 100.0, 1.0) * 40);
            boolean over  = total > budget;

            StringBuilder sb = new StringBuilder();
            sb.append("╔══════════════════════════════════════════════════╗\n");
            sb.append("║              💰  BUDGET OVERVIEW                ║\n");
            sb.append("╚══════════════════════════════════════════════════╝\n\n");
            sb.append(String.format("  Total Budget  : Rs. %,12.2f%n", budget));
            sb.append(String.format("  Total Spent   : Rs. %,12.2f%n", total));
            sb.append(String.format("  Remaining     : Rs. %,12.2f%n", remain));
            sb.append(String.format("  Status        : %s%n%n", bm.statusLabel()));

            sb.append("  [");
            for (int i = 0; i < 40; i++) sb.append(i < bar ? (over ? "█" : "▓") : "░");
            sb.append(String.format("]  %.1f%%%n%n", pct));

            Map<String, Double> cats = bm.byCategory();
            if (!cats.isEmpty()) {
                sb.append("──────────────────────────────────────────────────\n");
                sb.append("  SPENDING BY CATEGORY:\n");
                cats.forEach((cat, amt) -> {
                    double catPct = (amt / budget) * 100;
                    sb.append(String.format("  %-18s  Rs.%,9.2f  (%.1f%%)%n", cat, amt, catPct));
                });
                sb.append("\n");
            }

            sb.append("──────────────────────────────────────────────────\n");
            sb.append("  EXPENSE LOG:\n\n");
            if (trip.getExpenses().isEmpty()) {
                sb.append("  (No expenses added yet)\n");
            } else {
                sb.append(String.format("  %-8s  %-18s  %12s  Note%n", "Date", "Category", "Amount"));
                sb.append("  ────────────────────────────────────────────\n");
                for (Expense e : trip.getExpenses())
                    sb.append("  ").append(e).append("\n");
            }
            ta.setText(sb.toString());
            ta.setCaretPosition(0);
        }
    }
