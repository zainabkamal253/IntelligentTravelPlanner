import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
public class BookingFrame extends JFrame {
        public BookingFrame(Trip trip, Runnable onSave) {
            setTitle("Bookings — " + trip.getDestination());
            setSize(680, 600);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());
            getContentPane().setBackground(CLR_BG);
            add(headerPanel("🏨  Booking Manager — " + trip.getDestination(), CLR_PURPLE),
                BorderLayout.NORTH);

            DefaultListModel<Booking> lm = new DefaultListModel<>();
            trip.getBookings().forEach(lm::addElement);
            JList<Booking> list = new JList<>(lm);
            list.setFont(FONT_BODY);
            list.setFixedCellHeight(38);
            list.setSelectionBackground(new Color(225, 200, 255));
            list.setBackground(CLR_CARD);
            add(new JScrollPane(list), BorderLayout.CENTER);

            JPanel south = new JPanel(new BorderLayout(0, 6));
            south.setBackground(CLR_BG);

            JPanel inp = new JPanel(new GridLayout(3, 2, 8, 6));
            inp.setBackground(CLR_BG);
            inp.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(6, 10, 6, 10),
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(CLR_BORDER),
                    "  ➕  Add New Booking",
                    TitledBorder.LEFT, TitledBorder.TOP, FONT_HEADER, CLR_PURPLE)));

            JComboBox<String> typeCB = new JComboBox<>(
                new String[]{"Hotel", "Flight", "Bus", "Train", "Car Rental", "Ferry", "Tour Package"});
            typeCB.setFont(FONT_BODY);
            JTextField nameF = new JTextField(); nameF.setFont(FONT_BODY);
            JTextField costF = new JTextField(); costF.setFont(FONT_BODY);

            inp.add(lbl("Type:"));            inp.add(typeCB);
            inp.add(lbl("Name / Details:"));  inp.add(nameF);
            inp.add(lbl("Cost (Rs):"));       inp.add(costF);
            south.add(inp, BorderLayout.CENTER);

            JPanel btns = new JPanel(new GridLayout(1, 2, 10, 0));
            btns.setBackground(CLR_BG);
            btns.setBorder(new EmptyBorder(4, 10, 12, 10));
            JButton addBtn  = styledBtn("➕  Add Booking",     CLR_SUCCESS);
            JButton confBtn = styledBtn("✅  Confirm Selected", CLR_PURPLE);
            btns.add(addBtn);
            btns.add(confBtn);
            south.add(btns, BorderLayout.SOUTH);
            add(south, BorderLayout.SOUTH);

            addBtn.addActionListener(e -> {
                try {
                    if (nameF.getText().trim().isEmpty() || costF.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Name and Cost are required.");
                        return;
                    }
                    double cost = Double.parseDouble(costF.getText().trim());
                    if (cost < 0) {
                        JOptionPane.showMessageDialog(this, "Cost can't be negative.");
                        return;
                    }
                    Booking b = new Booking(
                        (String) typeCB.getSelectedItem(),
                        nameF.getText().trim(), cost);
                    trip.addBooking(b);
                    lm.addElement(b);
                    nameF.setText(""); costF.setText("");
                    onSave.run();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Cost must be a number.");
                }
            });

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
                    "✅  Booking confirmed:\n" + b.getName(),
                    "Confirmed", JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }
