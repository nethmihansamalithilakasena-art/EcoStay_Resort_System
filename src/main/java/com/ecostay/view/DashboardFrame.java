package com.ecostay.view;

import com.ecostay.util.DBConnection;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardFrame extends JFrame {

    private JPanel cardWorkspaceContainer;
    private CardLayout internalWorkspaceViewSwitcher;

    private ReportsEnginePanel reportsEnginePanel;
    private ReservationPanel reservationPanel;
    private JPanel systemDashboardPanel;
    private LoginPanel loginPanel;

    private JLabel lblWorkspaceTitle;
    private JPanel mainContentWrapper;
    private JPanel sideBarMenuPanel;

    private JButton btnDashMenu;
    private JButton btnResMenu;
    private JButton btnAnalyticsReports;
    private JButton btnLogout;

    private JLabel lblTotalBookingsCount;
    private JLabel lblOccupancyRatePercent;
    private JLabel lblGrossRevenueTotal;

    private final Color SIDEBAR_BG = new Color(20, 36, 65);
    private final Color LOGOUT_RED = new Color(235, 94, 94);
    private final Color PREMIUM_HEADER_LIGHT = new Color(234, 242, 253); // Soft light blue-gray background tone

    public DashboardFrame() {
        initializeMasterFrameConfig();
        initComponentsStructure();
    }

    private void initializeMasterFrameConfig() {
        setTitle("EcoStay Premium Infrastructure Management Deck");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 760);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initComponentsStructure() {
        internalWorkspaceViewSwitcher = new CardLayout();
        cardWorkspaceContainer = new JPanel(internalWorkspaceViewSwitcher);

        reportsEnginePanel = new ReportsEnginePanel();
        reservationPanel = new ReservationPanel();
        systemDashboardPanel = createDefaultOverviewDashboard();
        loginPanel = new LoginPanel(this);

        cardWorkspaceContainer.add(loginPanel, "VIEW_LOGIN");
        cardWorkspaceContainer.add(systemDashboardPanel, "VIEW_DASHBOARD");
        cardWorkspaceContainer.add(reservationPanel, "VIEW_RECEPTION");
        cardWorkspaceContainer.add(reportsEnginePanel, "VIEW_ANALYTICS");

        // --- FIXED: Updated Header Banner background color to your clean custom tint ---
        JPanel topBanner = new JPanel(new BorderLayout());
        topBanner.setBackground(PREMIUM_HEADER_LIGHT);
        topBanner.setPreferredSize(new Dimension(0, 60));
        topBanner.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 222, 238)));

        lblWorkspaceTitle = new JLabel("System Authentication Portal");
        lblWorkspaceTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblWorkspaceTitle.setForeground(new Color(30, 41, 59));
        lblWorkspaceTitle.setBorder(new EmptyBorder(0, 20, 0, 0));
        topBanner.add(lblWorkspaceTitle, BorderLayout.WEST);

        sideBarMenuPanel = buildInteractiveSidebar();
        add(sideBarMenuPanel, BorderLayout.WEST);

        mainContentWrapper = new JPanel(new BorderLayout());
        mainContentWrapper.add(topBanner, BorderLayout.NORTH);
        mainContentWrapper.add(cardWorkspaceContainer, BorderLayout.CENTER);
        add(mainContentWrapper, BorderLayout.CENTER);

        sideBarMenuPanel.setVisible(false);
        internalWorkspaceViewSwitcher.show(cardWorkspaceContainer, "VIEW_LOGIN");
    }

    private JPanel buildInteractiveSidebar() {
        JPanel navigationDeck = new JPanel();
        navigationDeck.setLayout(new BorderLayout());
        navigationDeck.setBackground(SIDEBAR_BG);
        navigationDeck.setPreferredSize(new Dimension(240, 0));
        navigationDeck.setBorder(new EmptyBorder(25, 15, 25, 15));

        JPanel topSegment = new JPanel();
        topSegment.setLayout(new BoxLayout(topSegment, BoxLayout.Y_AXIS));
        topSegment.setOpaque(false);

        JLabel brand = new JLabel("ECOSTAY PLATFORM");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        topSegment.add(brand);

        topSegment.add(Box.createRigidArea(new Dimension(0, 40)));

        btnDashMenu = createNavigationLinkButton("*  System Dashboard");
        btnResMenu = createNavigationLinkButton("*  Reception Registry");
        btnAnalyticsReports = createNavigationLinkButton("*  Analytics Reports");

        topSegment.add(btnDashMenu);
        topSegment.add(Box.createRigidArea(new Dimension(0, 14)));
        topSegment.add(btnResMenu);
        topSegment.add(Box.createRigidArea(new Dimension(0, 14)));
        topSegment.add(btnAnalyticsReports);

        navigationDeck.add(topSegment, BorderLayout.CENTER);

        btnLogout = new JButton("LOGOUT SESSION");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setForeground(LOGOUT_RED);
        btnLogout.setBackground(SIDEBAR_BG);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setPreferredSize(new Dimension(210, 40));

        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btnLogout.setForeground(Color.WHITE); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btnLogout.setForeground(LOGOUT_RED); }
        });

        btnLogout.addActionListener(e -> executeSystemLogoutWorkflow());
        navigationDeck.add(btnLogout, BorderLayout.SOUTH);

        btnDashMenu.addActionListener(e -> {
            lblWorkspaceTitle.setText("System Overview & Infrastructure Command Deck");
            calculateLiveDashboardMetrics();
            internalWorkspaceViewSwitcher.show(cardWorkspaceContainer, "VIEW_DASHBOARD");
        });

        btnResMenu.addActionListener(e -> {
            lblWorkspaceTitle.setText("Reception Desk Operations / Transaction UI");
            internalWorkspaceViewSwitcher.show(cardWorkspaceContainer, "VIEW_RECEPTION");
        });

        btnAnalyticsReports.addActionListener(e -> {
            lblWorkspaceTitle.setText("Master Booking Analytics & Financial Audit Deck");
            internalWorkspaceViewSwitcher.show(cardWorkspaceContainer, "VIEW_ANALYTICS");
            reportsEnginePanel.loadLiveDatabaseRecords();
        });

        return navigationDeck;
    }

    private JButton createNavigationLinkButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(new Color(195, 207, 225));
        btn.setBackground(SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    private JPanel createDefaultOverviewDashboard() {
        JPanel mainDashboard = new JPanel(new BorderLayout(25, 25));
        mainDashboard.setBackground(new Color(245, 247, 250));
        mainDashboard.setBorder(new EmptyBorder(35, 35, 35, 35));

        JPanel welcomeRow = new JPanel(new GridLayout(2, 1, 5, 5));
        welcomeRow.setOpaque(false);
        JLabel title = new JLabel("Welcome Back, Operations Manager");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(20, 36, 65));
        JLabel subtitle = new JLabel("Live metrics stream overview and automated capacity management dashboard.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);
        welcomeRow.add(title);
        welcomeRow.add(subtitle);
        mainDashboard.add(welcomeRow, BorderLayout.NORTH);

        JPanel cardsGrid = new JPanel(new GridLayout(1, 3, 25, 0));
        cardsGrid.setOpaque(false);

        lblTotalBookingsCount = new JLabel("0 Loading...");
        lblOccupancyRatePercent = new JLabel("0% Active");
        lblGrossRevenueTotal = new JLabel("Calculating...");

        cardsGrid.add(createDashboardStatCard("TOTAL RESERVATIONS REGISTERED", lblTotalBookingsCount, ">> System Records Active", new Color(9, 132, 227)));
        cardsGrid.add(createDashboardStatCard("ACCOMMODATION CAPACITY RATIO", lblOccupancyRatePercent, ">> Capacity Threshold Verified", new Color(0, 184, 148)));
        cardsGrid.add(createDashboardStatCard("TOTAL AUDITED BILLING PROFIT", lblGrossRevenueTotal, ">> Corporate General Ledger", new Color(225, 112, 85)));

        mainDashboard.add(cardsGrid, BorderLayout.CENTER);
        return mainDashboard;
    }

    private JPanel createDashboardStatCard(String header, JLabel valueLabel, String footer, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 0, 0, 0, accentColor),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblHead = new JLabel(header); lblHead.setFont(new Font("Segoe UI", Font.BOLD, 11)); lblHead.setForeground(new Color(148, 163, 184));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26)); valueLabel.setForeground(new Color(30, 41, 59));
        JLabel lblFoot = new JLabel(footer); lblFoot.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lblFoot.setForeground(Color.GRAY);

        card.add(lblHead, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblFoot, BorderLayout.SOUTH);

        return card;
    }

    public void calculateLiveDashboardMetrics() {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            Statement stmt = conn.createStatement();

            ResultSet rsCount = stmt.executeQuery("SELECT COUNT(*) FROM tbl_bookings");
            if (rsCount.next()) {
                lblTotalBookingsCount.setText(String.format("%02d Active Rows", rsCount.getInt(1)));
            }

            ResultSet rsRooms = stmt.executeQuery("SELECT COUNT(*) FROM tbl_accommodation");
            int totalRooms = 1;
            if (rsRooms.next() && rsRooms.getInt(1) > 0) totalRooms = rsRooms.getInt(1);

            ResultSet rsOccupied = stmt.executeQuery("SELECT COUNT(DISTINCT room_id) FROM tbl_bookings");
            if (rsOccupied.next()) {
                double occupancyRate = ((double) rsOccupied.getInt(1) / totalRooms) * 100;
                lblOccupancyRatePercent.setText(String.format("%,.1f%% Capacity", Math.min(occupancyRate, 100.0)));
            }

            ResultSet rsRevenue = stmt.executeQuery("SELECT billing_currency, SUM(final_amount) FROM tbl_bookings GROUP BY billing_currency");
            StringBuilder revStr = new StringBuilder("<html>");
            boolean hasData = false;
            while(rsRevenue.next()) {
                hasData = true;
                revStr.append(rsRevenue.getString(1)).append(" ").append(String.format("%,.0f", rsRevenue.getDouble(2))).append("<br>");
            }
            if (!hasData) revStr.append("LKR 0.00");
            revStr.append("</html>");
            lblGrossRevenueTotal.setText(revStr.toString());

        } catch (Exception ex) {
            System.out.println("Live Dashboard Aggregator Fault: " + ex.getMessage());
        }
    }

    public void grantAccessSession(String role, String displayName) {
        lblWorkspaceTitle.setText("System Overview & Infrastructure Command Deck (" + displayName + " - " + role + ")");
        sideBarMenuPanel.setVisible(true);
        calculateLiveDashboardMetrics();

        if ("Receptionist".equalsIgnoreCase(role) || "Reception".equalsIgnoreCase(role)) {
            btnAnalyticsReports.setVisible(false);
        } else {
            btnAnalyticsReports.setVisible(true);
        }

        internalWorkspaceViewSwitcher.show(cardWorkspaceContainer, "VIEW_DASHBOARD");
    }

    private void executeSystemLogoutWorkflow() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to terminate the active management session?",
                "Confirm Session Terminate", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (reportsEnginePanel != null) {
                reportsEnginePanel.clearInspectorDeck();
            }
            sideBarMenuPanel.setVisible(false);
            lblWorkspaceTitle.setText("System Authentication Portal");
            internalWorkspaceViewSwitcher.show(cardWorkspaceContainer, "VIEW_LOGIN");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf theme.");
        }

        SwingUtilities.invokeLater(() -> {
            new DashboardFrame().setVisible(true);
        });
    }
}
