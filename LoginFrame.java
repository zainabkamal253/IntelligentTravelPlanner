import javax.swing.*;
import java.awt.*;

// This class is the login screen of the travel planner app
public class LoginFrame extends JFrame {
        
        // Main UI color theme used in the login window
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

        // Extra theme colors (used in other parts of app)
        public static final Color CLR_ADV_BG   = new Color(255, 240, 230);
        public static final Color CLR_FOOD_BG  = new Color(255, 245, 225);
        public static final Color CLR_HIST_BG  = new Color(240, 235, 255);
        public static final Color CLR_NAT_BG   = new Color(232, 248, 235);
        public static final Color CLR_SHOP_BG  = new Color(252, 235, 245);
        public static final Color CLR_CULT_BG  = new Color(235, 245, 255);

        // Fonts used in login screen
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

        public LoginFrame(UserManager um) {
            setTitle("Login - Travel Planner");
            setSize(440, 360);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout(10, 10));
            getContentPane().setBackground(CLR_BG);

            // Header of login screen
            add(IntelligentTravelPlanner.headerPanel("  Travel Planner", CLR_PRIMARY), BorderLayout.NORTH);

            // Center panel containing login form
            JPanel center = new JPanel(new GridBagLayout());
            center.setBackground(CLR_BG);

            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(8, 10, 8, 10);
            g.fill = GridBagConstraints.HORIZONTAL;

            JTextField userF = new JTextField(15);
            JPasswordField passF = new JPasswordField(15);

            IntelligentTravelPlanner.styleField(userF);
            passF.setFont(FONT_BODY);

            // Username field
            g.gridx = 0; g.gridy = 0;
            center.add(IntelligentTravelPlanner.lbl("Username:"), g);
            g.gridx = 1;
            center.add(userF, g);

            // Password field
            g.gridx = 0; g.gridy = 1;
            center.add(IntelligentTravelPlanner.lbl("Password:"), g);
            g.gridx = 1;
            center.add(passF, g);

            // Small info text for users
            JLabel hint = new JLabel("<html><center><i>First time? Click Register.<br>"
                + "Default admin: <b>admin / admin123</b></i></center></html>");
            hint.setFont(FONT_SMALL);
            g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
            center.add(hint, g);

            add(center, BorderLayout.CENTER);

            // Bottom buttons panel
            JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            south.setBackground(CLR_BG);

            JButton loginBtn = IntelligentTravelPlanner.styledBtn("Login", CLR_PRIMARY);
            JButton regBtn   = IntelligentTravelPlanner.styledBtn("Register", CLR_SUCCESS);

            south.add(loginBtn);
            south.add(regBtn);

            add(south, BorderLayout.SOUTH);

            // Login button action
            loginBtn.addActionListener(e -> {
                try {
                    User u = um.login(userF.getText(),
                            new String(passF.getPassword()));
                    new DashBoardFrame(u).setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Login Failed: Invalid Username or Password",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Register button action
            regBtn.addActionListener(e -> {
                try {
                    um.register(userF.getText(),
                            new String(passF.getPassword()),
                            "user@nust.edu.pk", false);

                    JOptionPane.showMessageDialog(this,
                        "Registration Successful! You can now login.",
                        "Welcome", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Registration Failed: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Allow Enter key to trigger login
            getRootPane().setDefaultButton(loginBtn);
        }
    }