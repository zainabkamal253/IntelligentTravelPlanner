//BudgetTrackerFrame is the UI window for managing trip expenses.
//It shows total budget, spending, remaining amount, and category-wise breakdown.

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Map;

public class BudgetTrackerFrame extends JFrame {

        // UI color theme
        public static final Color CLR_PRIMARY  = new Color(13, 71, 161);
        public static final Color CLR_ACCENT   = new Color(25, 118, 210);
        public static final Color CLR_SUCCESS  = new Color(27, 128, 60);
        public static final Color CLR_DANGER   = new Color(183, 28, 28);
        public static final Color CLR_PURPLE   = new Color(106, 27, 154);
        public static final Color CLR_ORANGE   = new Color(230, 100, 0);
        public static final Color CLR_BG       = new Color(240, 244, 255);

        // fonts used in UI
        public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
        public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);

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

            // header
            add(IntelligentTravelPlanner.headerPanel(
                    "Budget Tracker — " + trip.getDestination(), CLR_ORANGE),
                    BorderLayout.NORTH);

            // text area for budget report
            ta = IntelligentTravelPlanner.styledTA();
            refresh();
            add(IntelligentTravelPlanner.scrollWrap(ta), BorderLayout.CENTER);

            // input panel
            JPanel inp = new JPanel(new GridBagLayout());
            inp.setBackground(CLR_BG);

            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(5, 6, 5, 6);
            g.fill = GridBagConstraints.HORIZONTAL;

            JComboBox<String> catCB = new JComboBox<>(new String[]{
                    "Accommodation", "Food", "Transport", "Activities",
                    "Shopping", "Health", "Miscellaneous"
            });

            JTextField amtF  = new JTextField(8);
            JTextField noteF = new JTextField(16);
            JButton addBtn   = IntelligentTravelPlanner.styledBtn("Add", CLR_SUCCESS);

            // layout fields
            g.gridx = 0; g.gridy = 0;
            inp.add(IntelligentTravelPlanner.lbl("Category:"), g);

            g.gridx = 1;
            inp.add(catCB, g);

            g.gridx = 2;
            inp.add(IntelligentTravelPlanner.lbl("Amount:"), g);

            g.gridx = 3;
            inp.add(amtF, g);

            g.gridx = 0; g.gridy = 1;
            inp.add(IntelligentTravelPlanner.lbl("Note:"), g);

            g.gridx = 1; g.gridwidth = 2;
            inp.add(noteF, g);

            g.gridx = 3; g.gridwidth = 1;
            inp.add(addBtn, g);

            add(inp, BorderLayout.SOUTH);

            // add expense button logic
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
                            (String) catCB.getSelectedItem(),
                            amt,
                            noteF.getText().trim()
                    ));

                    amtF.setText("");
                    noteF.setText("");

                } catch (BudgetExceededException ex) {
                    JOptionPane.showMessageDialog(this,
                            ex.getMessage(),
                            "Budget Alert",
                            JOptionPane.WARNING_MESSAGE);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Amount must be a number.");
                }

                refresh();
                onSave.run();
            });
        }

        // refresh UI display
        void refresh() {

            double total  = bm.totalSpent();
            double budget = trip.getBudget();
            double remain = bm.remaining();
            double pct    = bm.percentUsed();

            StringBuilder sb = new StringBuilder();

            sb.append("BUDGET OVERVIEW\n\n");
            sb.append("Budget   : ").append(budget).append("\n");
            sb.append("Spent    : ").append(total).append("\n");
            sb.append("Remaining: ").append(remain).append("\n");
            sb.append("Status   : ").append(bm.statusLabel()).append("\n\n");

            sb.append("CATEGORY BREAKDOWN\n");

            Map<String, Double> cats = bm.byCategory();

            for (Map.Entry<String, Double> e : cats.entrySet()) {
                sb.append(e.getKey())
                        .append(" : ")
                        .append(e.getValue())
                        .append("\n");
            }

            sb.append("\nEXPENSE LOG\n");

            if (trip.getExpenses().isEmpty()) {
                sb.append("No expenses yet.");
            } else {
                for (Expense e : trip.getExpenses()) {
                    sb.append(e).append("\n");
                }
            }

            ta.setText(sb.toString());
            ta.setCaretPosition(0);
        }
}