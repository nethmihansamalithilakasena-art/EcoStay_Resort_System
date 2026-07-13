package com.ecostay.view;

import com.ecostay.util.DBConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPanel extends JPanel {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;
    private JToggleButton btnTogglePassword;
    private DashboardFrame parentFrame;

    private final Color TEAL_ACCENT = new Color(0, 184, 148);
    private final Color TEXT_DARK = new Color(30, 41, 59);

    public LoginPanel(DashboardFrame parent) {
        this.parentFrame = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- 1. LEFT SIDE: Premium Image Background Hero Panel ---
        // Generates a custom panel that reads the resource graphic stream and auto-scales it smoothly
        JPanel leftBrandingPanel = new JPanel() {
            private Image backgroundImage = null;

            {
                try {
                    // Pull resource stream directly alongside class path parameters safely
                    InputStream is = getClass().getResourceAsStream("/com/ecostay/view/login_hero.jpg");
                    if (is == null) {
                        // Fallback check if the leading slash structure differs in compiled jars
                        is = getClass().getResourceAsStream("login_hero.jpg");
                    }
                    if (is != null) {
                        backgroundImage = ImageIO.read(is);
                    }
                } catch (Exception e) {
                    System.out.println("System Image Loader Alert: " + e.getMessage());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (backgroundImage != null) {
                    // Compute scaling layout mechanics to make sure image fills the panel surface
                    int imgWidth = backgroundImage.getWidth(this);
                    int imgHeight = backgroundImage.getHeight(this);

                    double scale = Math.max((double) getWidth() / imgWidth, (double) getHeight() / imgHeight);
                    int drawWidth = (int) (imgWidth * scale);
                    int drawHeight = (int) (imgHeight * scale);

                    // Center the dynamic viewport calculation
                    int x = (getWidth() - drawWidth) / 2;
                    int y = (getHeight() - drawHeight) / 2;

                    g2d.drawImage(backgroundImage, x, y, drawWidth, drawHeight, this);

                    // High-end Dark Glass Overlay Layer to keep text completely readable
                    g2d.setColor(new Color(15, 23, 42, 175));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    // Fallback to solid deep corporate blue tone if file isn't loaded yet
                    g2d.setPaint(new GradientPaint(0, 0, new Color(24, 43, 73), getWidth(), getHeight(), new Color(12, 22, 40)));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        // Dynamic scaling configuration: widening footprint slightly to make image area more prominent
        leftBrandingPanel.setPreferredSize(new Dimension(460, 0));
        leftBrandingPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.fill = GridBagConstraints.HORIZONTAL;
        gbcLeft.insets = new Insets(10, 40, 10, 40);

        JLabel lblBrandTitle = new JLabel("ECOSTAY CORE");
        lblBrandTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblBrandTitle.setForeground(Color.WHITE);

        JLabel lblBrandSub = new JLabel("Boutique Operations Portal v1.0");
        lblBrandSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBrandSub.setForeground(new Color(203, 213, 225));

        gbcLeft.gridy = 0; leftBrandingPanel.add(lblBrandTitle, gbcLeft);
        gbcLeft.gridy = 1; leftBrandingPanel.add(lblBrandSub, gbcLeft);
        add(leftBrandingPanel, BorderLayout.WEST);

        // --- 2. RIGHT SIDE: Center-Anchored Fixed Form Panel Framework ---
        JPanel formOuterWrapper = new JPanel(new GridBagLayout());
        formOuterWrapper.setBackground(new Color(248, 250, 252));

        JPanel centralFormCard = new JPanel();
        centralFormCard.setLayout(new BoxLayout(centralFormCard, BoxLayout.Y_AXIS));
        centralFormCard.setOpaque(false);
        centralFormCard.setPreferredSize(new Dimension(380, 500));
        centralFormCard.setMaximumSize(new Dimension(380, 500));

        JLabel lblWelcome = new JLabel("Welcome Back");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(TEXT_DARK);
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        centralFormCard.add(lblWelcome);

        JLabel lblPrompt = new JLabel("Provide your secure credentials to log in.");
        lblPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPrompt.setForeground(Color.GRAY);
        lblPrompt.setBorder(new EmptyBorder(4, 0, 30, 0));
        lblPrompt.setAlignmentX(Component.LEFT_ALIGNMENT);
        centralFormCard.add(lblPrompt);

        centralFormCard.add(createInputLabel("Username"));
        centralFormCard.add(Box.createRigidArea(new Dimension(0, 6)));
        txtUser = new JTextField();
        styleInputField(txtUser);
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        centralFormCard.add(txtUser);

        centralFormCard.add(Box.createRigidArea(new Dimension(0, 16)));

        centralFormCard.add(createInputLabel("Password"));
        centralFormCard.add(Box.createRigidArea(new Dimension(0, 6)));

        JPanel passwordContainer = new JPanel(new BorderLayout(8, 0));
        passwordContainer.setOpaque(false);
        passwordContainer.setMaximumSize(new Dimension(380, 38));
        passwordContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPass = new JPasswordField();
        styleInputField(txtPass);
        passwordContainer.add(txtPass, BorderLayout.CENTER);

        btnTogglePassword = new JToggleButton("SHOW");
        btnTogglePassword.setPreferredSize(new Dimension(65, 38));
        btnTogglePassword.setFocusPainted(false);
        btnTogglePassword.setBackground(Color.WHITE);
        btnTogglePassword.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnTogglePassword.setForeground(new Color(100, 116, 139));
        btnTogglePassword.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true));
        btnTogglePassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        passwordContainer.add(btnTogglePassword, BorderLayout.EAST);
        centralFormCard.add(passwordContainer);

        centralFormCard.add(Box.createRigidArea(new Dimension(0, 30)));

        btnLogin = new JButton("Login");
        btnLogin.setMaximumSize(new Dimension(380, 42));
        btnLogin.setPreferredSize(new Dimension(380, 42));
        btnLogin.setBackground(TEAL_ACCENT);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setFocusPainted(false);
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        centralFormCard.add(btnLogin);

        formOuterWrapper.add(centralFormCard, new GridBagConstraints());
        add(formOuterWrapper, BorderLayout.CENTER);

        setupFormActionInteractions();
    }

    private JLabel createInputLabel(String labelText) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void styleInputField(JTextField field) {
        field.setMaximumSize(new Dimension(380, 38));
        field.setPreferredSize(new Dimension(380, 38));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(0, 12, 0, 12)
        ));
    }

    private void setupFormActionInteractions() {
        btnTogglePassword.addActionListener(e -> {
            if (btnTogglePassword.isSelected()) {
                txtPass.setEchoChar((char) 0);
                btnTogglePassword.setText("HIDE");
                btnTogglePassword.setForeground(TEAL_ACCENT);
            } else {
                txtPass.setEchoChar('•');
                btnTogglePassword.setText("SHOW");
                btnTogglePassword.setForeground(new Color(100, 116, 139));
            }
        });

        btnLogin.addActionListener(e -> attemptSystemAuthenticationWorkflow());
    }

    private void attemptSystemAuthenticationWorkflow() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            displayPremiumCustomErrorMessage("Incomplete Form Metrics", "Please fill in both your account username and password entries.");
            return;
        }

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String query = "SELECT associated_role, display_name FROM tbl_users WHERE username = ? AND password_hash = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("associated_role");
                String name = rs.getString("display_name");

                txtUser.setText("");
                txtPass.setText("");

                parentFrame.grantAccessSession(role, name);
            } else {
                displayPremiumCustomErrorMessage("Authentication Terminated", "Security Clearance Denied: The username or password specified is invalid.");
            }
        } catch (Exception ex) {
            displayPremiumCustomErrorMessage("Database System Exception", "Connection Interrupted: " + ex.getMessage());
        }
    }

    private void displayPremiumCustomErrorMessage(String title, String details) {
        String htmlModalContent = "<html>" +
            "<body style='font-family: \"Segoe UI\", sans-serif; color: #334155; width: 280px;'>" +
            "  <div style='background-color: #eb5e5e; padding: 10px; border-radius: 4px; margin-bottom: 12px;'>" +
            "    <h4 style='color: #ffffff; margin: 0; font-size: 13px;'>Error: " + title + "</h4>" +
            "  </div>" +
            "  <p style='font-size: 12px; line-height: 1.5; margin: 0; padding: 2px; color: #475569;'>" + details + "</p>" +
            "</body>" +
            "</html>";

        JOptionPane.showMessageDialog(this, htmlModalContent, "Security Operations Guard", JOptionPane.PLAIN_MESSAGE);
    }
}