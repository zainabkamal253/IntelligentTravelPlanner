import javax.swing.*;
import java.awt.*;
public class LoginFrame extends JFrame {
        public LoginFrame(UserManager um) {
            setTitle("Login - Travel Planner");
            setSize(440, 360);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout(10, 10));
            getContentPane().setBackground(CLR_BG);

            add(headerPanel("✈  Travel Planner", CLR_PRIMARY), BorderLayout.NORTH);

            JPanel center = new JPanel(new GridBagLayout());
            center.setBackground(CLR_BG);
            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(8, 10, 8, 10);
            g.fill = GridBagConstraints.HORIZONTAL;

            JTextField userF = new JTextField(15);
            JPasswordField passF = new JPasswordField(15);
            styleField(userF);
            passF.setFont(FONT_BODY);
            passF.setPreferredSize(new Dimension(200, 34));
            passF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));

            g.gridx = 0; g.gridy = 0; center.add(lbl("Username:"), g);
            g.gridx = 1; center.add(userF, g);
            g.gridx = 0; g.gridy = 1; center.add(lbl("Password:"), g);
            g.gridx = 1; center.add(passF, g);

            JLabel hint = new JLabel("<html><center><i>First time? Click Register.<br>"
                + "Default admin: <b>admin / admin123</b></i></center></html>");
            hint.setFont(FONT_SMALL);
            hint.setForeground(new Color(100, 100, 130));
            g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
            center.add(hint, g);

            add(center, BorderLayout.CENTER);

            JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            south.setBackground(CLR_BG);
            JButton loginBtn = styledBtn("Login", CLR_PRIMARY);
            JButton regBtn   = styledBtn("Register", CLR_SUCCESS);
            south.add(loginBtn); south.add(regBtn);
            add(south, BorderLayout.SOUTH);

            loginBtn.addActionListener(e -> {
                try {
                    User u = um.login(userF.getText(), new String(passF.getPassword()));
                    new DashboardFrame(u).setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Login Failed: Invalid Username or Password",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            });

            regBtn.addActionListener(e -> {
                try {
                    um.register(userF.getText(), new String(passF.getPassword()),
                        "user@nust.edu.pk", false);
                    JOptionPane.showMessageDialog(this,
                        "✅  Registration Successful! You can now login.",
                        "Welcome", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Registration Failed: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Enter key triggers login
            getRootPane().setDefaultButton(loginBtn);
        }
    }
