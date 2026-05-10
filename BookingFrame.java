import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;

public class BookingFrame extends JFrame {

        // color theme for UI
        public static final Color CLR_PRIMARY  = new Color(13, 71, 161);
        public static final Color CLR_ACCENT   = new Color(25, 118, 210);
        public static final Color CLR_SUCCESS  = new Color(27, 128, 60);
        public static final Color CLR_DANGER   = new Color(183, 28, 28);
        public static final Color CLR_PURPLE   = new Color(106, 27, 154);
        public static final Color CLR_TEAL     = new Color(0, 121, 107);
        public static final Color CLR_ORANGE   = new Color(230, 100, 0);

        public static final Color CLR_DARK     = new Color(18, 18, 30);
        public static final Color CLR_BG       = new Color(240, 244, 255);
        public static final Color CLR_CARD     = new Color(255, 255, 255);
        public static final Color CLR_BORDER   = new Color(180, 200, 235);
        public static final Color CLR_SOFT     = new Color(248, 250, 255);

        // fonts used in UI
        public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD, 24);
        public static final Font FONT_HEADER   = new Font("Segoe UI", Font.BOLD, 16);
        public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 13);
        public static final Font FONT_BTN      = new Font("Segoe UI", Font.BOLD, 13);

        public BookingFrame(Trip trip, Runnable onSave) {

            setTitle("Bookings — " + trip.getDestination());
            setSize(680, 600);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());
            getContentPane().setBackground(CLR_BG);

            // top header panel
            add(IntelligentTravelPlanner.headerPanel(
                    "Booking Manager — " + trip.getDestination(), CLR_PURPLE),
                    BorderLayout.NORTH);

            // booking list model
            DefaultListModel<Booking> lm = new DefaultListModel<>();
            trip.getBookings().forEach(lm::addElement);

            JList<Booking> list = new JList<>(lm);
            list.setFont(FONT_BODY);
            list.setFixedCellHeight(38);
            list.setBackground(CLR_CARD);

            add(new JScrollPane(list), BorderLayout.CENTER);

            // bottom panel (form + buttons)
            JPanel south = new JPanel(new BorderLayout(0, 6));
            south.setBackground(CLR_BG);

            // input form
            JPanel inp = new JPanel(new GridLayout(3, 2, 8, 6));
            inp.setBackground(CLR_BG);

            JComboBox<String> typeCB = new JComboBox<>(
                    new String[]{"Hotel", "Flight", "Bus", "Train", "Car Rental", "Ferry", "Tour Package"});
            JTextField nameF = new JTextField();
            JTextField costF = new JTextField();

            inp.add(IntelligentTravelPlanner.lbl("Type:"));
            inp.add(typeCB);

            inp.add(IntelligentTravelPlanner.lbl("Name:"));
            inp.add(nameF);

            inp.add(IntelligentTravelPlanner.lbl("Cost:"));
            inp.add(costF);

            south.add(inp, BorderLayout.CENTER);

            // buttons panel
            JPanel btns = new JPanel(new GridLayout(1, 2, 10, 0));
            btns.setBackground(CLR_BG);

            JButton addBtn = IntelligentTravelPlanner.styledBtn("Add Booking", CLR_SUCCESS);
            JButton confBtn = IntelligentTravelPlanner.styledBtn("Confirm", CLR_PURPLE);

            btns.add(addBtn);
            btns.add(confBtn);

            south.add(btns, BorderLayout.SOUTH);
            add(south, BorderLayout.SOUTH);

            // add booking button logic
            addBtn.addActionListener(e -> {
                try {
                    if (nameF.getText().trim().isEmpty() || costF.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Name and Cost are required.");
                        return;
                    }

                    double cost = Double.parseDouble(costF.getText().trim());

                    Booking b = new Booking(
                            (String) typeCB.getSelectedItem(),
                            nameF.getText().trim(),
                            cost
                    );

                    trip.addBooking(b);
                    lm.addElement(b);

                    nameF.setText("");
                    costF.setText("");

                    onSave.run();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Cost must be a valid number.");
                }
            });

            // confirm booking logic
            confBtn.addActionListener(e -> {
                Booking b = list.getSelectedValue();

                if (b == null) {
                    JOptionPane.showMessageDialog(this, "Select a booking first.");
                    return;
                }

                b.book();
                list.repaint();
                onSave.run();

                JOptionPane.showMessageDialog(this,
                        "Booking confirmed:\n" + b.getName());
            });
        }
}