package com.ecostay.view;

import com.ecostay.util.DBConnection;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class ReportsEnginePanel extends JPanel {

    private JTable dataTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private JComboBox<String> filterDropdown;
    private JComboBox<String> orderSortDropdown;
    private JButton btnResetMatrix; 

    private JPanel inspectorPanel;
    private JLabel lblGuestName, lblGuestId, lblGuestPhone, lblGuestEmail;
    private JLabel lblStayDates, lblRoomCategory;
    private JLabel lblBaseTariff, lblTaxComponent, lblGrossTotal, lblStatusTag, lblBalanceDue;
    private JEditorPane epPaymentHistory;

    private final Color DEEP_BLUE = new Color(20, 36, 65);
    private final Color BRIGHT_TEAL = new Color(0, 184, 148);
    private final Color NEUTRAL_SLATE = new Color(148, 163, 184); 
    private final Color LIGHT_BG = new Color(245, 247, 250);
    private final Color BORDER_GRAY = new Color(226, 232, 240);

    public ReportsEnginePanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(LIGHT_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topRibbon = new JPanel(new BorderLayout(10, 10));
        topRibbon.setBackground(LIGHT_BG);

        JLabel headerTitle = new JLabel("Master Booking Analytics & Financial Audit Deck");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerTitle.setForeground(DEEP_BLUE);
        topRibbon.add(headerTitle, BorderLayout.NORTH);

        JPanel controlsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlsRow.setBackground(Color.WHITE);
        controlsRow.setBorder(BorderFactory.createLineBorder(BORDER_GRAY, 1, true));

        controlsRow.add(new JLabel("Search Registry:"));
        searchField = new JTextField(12); controlsRow.add(searchField);

        controlsRow.add(new JLabel("Filter Category:"));
        filterDropdown = new JComboBox<>(new String[]{"All Categories", "Treehouse", "Safari Dome", "Eco Lodge"});
        controlsRow.add(filterDropdown);

        controlsRow.add(new JLabel("Order Sequence:"));
        orderSortDropdown = new JComboBox<>(new String[]{"Latest Reservations First", "Oldest Reservations First"});
        controlsRow.add(orderSortDropdown);

        JButton btnPrintJasper = new JButton("Compile & Print Report (Jasper)");
        btnPrintJasper.setBackground(BRIGHT_TEAL); btnPrintJasper.setForeground(Color.WHITE);
        btnPrintJasper.setFont(new Font("Segoe UI", Font.BOLD, 12));
        controlsRow.add(btnPrintJasper);

        // --- ADDED: Styled and Initialized the Reset Button ---
        btnResetMatrix = new JButton("Reset Matrix");
        btnResetMatrix.setBackground(NEUTRAL_SLATE);
        btnResetMatrix.setForeground(Color.WHITE);
        btnResetMatrix.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnResetMatrix.setFocusPainted(false);
        btnResetMatrix.setCursor(new Cursor(Cursor.HAND_CURSOR));
        controlsRow.add(btnResetMatrix);

        topRibbon.add(controlsRow, BorderLayout.CENTER);

        JLabel lblUserTip = new JLabel("Tip: Double-click any row item to update payment status allocations instantly.");
        lblUserTip.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblUserTip.setForeground(new Color(100, 116, 139));
        lblUserTip.setBorder(new EmptyBorder(5, 5, 0, 0));
        topRibbon.add(lblUserTip, BorderLayout.SOUTH);

        add(topRibbon, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(730);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        String[] columnHeaders = {"Booking ID", "Guest Full Name", "Room ID", "Allocated Category", "Check-In", "Check-Out", "Invoice Total", "Payment Status", "Balance Due"};
        tableModel = new DefaultTableModel(columnHeaders, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        dataTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        dataTable.setRowSorter(rowSorter);
        dataTable.setRowHeight(32);

        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        splitPane.setLeftComponent(scrollPane);

        splitPane.setRightComponent(buildSideAuditInspectorPanel());
        add(splitPane, BorderLayout.CENTER);

        setupRealtimeInteractions(btnPrintJasper);
        loadLiveDatabaseRecords();
    }

    private JPanel buildSideAuditInspectorPanel() {
        inspectorPanel = new JPanel();
        inspectorPanel.setLayout(new BoxLayout(inspectorPanel, BoxLayout.Y_AXIS));
        inspectorPanel.setBackground(Color.WHITE);
        inspectorPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        inspectorPanel.setPreferredSize(new Dimension(430, 0));

        JLabel lblDeckHead = new JLabel("LIVE AUDIT INSPECTOR");
        lblDeckHead.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDeckHead.setForeground(DEEP_BLUE);
        lblDeckHead.setAlignmentX(Component.LEFT_ALIGNMENT);
        inspectorPanel.add(lblDeckHead);

        inspectorPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        inspectorPanel.add(createSectionHeaderLabel("Guest Profile Card"));
        lblGuestName = createMetadataFieldLabel("Full Legal Name: ---"); inspectorPanel.add(lblGuestName);
        lblGuestId = createMetadataFieldLabel("NIC / Passport Serial: ---"); inspectorPanel.add(lblGuestId);
        lblGuestPhone = createMetadataFieldLabel("Mobile Number: ---"); inspectorPanel.add(lblGuestPhone);
        lblGuestEmail = createMetadataFieldLabel("Email Address: ---"); inspectorPanel.add(lblGuestEmail);

        inspectorPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        inspectorPanel.add(createSectionHeaderLabel("Stay Logistics"));
        lblRoomCategory = createMetadataFieldLabel("Allocated Deck: ---"); inspectorPanel.add(lblRoomCategory);
        lblStayDates = createMetadataFieldLabel("Stay Interval: ---"); inspectorPanel.add(lblStayDates);

        inspectorPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        inspectorPanel.add(createSectionHeaderLabel("General Financial Ledger"));
        lblBaseTariff = createMetadataFieldLabel("Base Net Subtotal: ---"); inspectorPanel.add(lblBaseTariff);
        lblTaxComponent = createMetadataFieldLabel("Operational Tax (10%): ---"); inspectorPanel.add(lblTaxComponent);
        lblGrossTotal = createMetadataFieldLabel("Gross Total Bill: ---"); inspectorPanel.add(lblGrossTotal);
        lblStatusTag = createMetadataFieldLabel("Settlement State: ---"); inspectorPanel.add(lblStatusTag);

        lblBalanceDue = new JLabel("OUTSTANDING DUE: ---");
        lblBalanceDue.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBalanceDue.setForeground(new Color(214, 48, 49));
        lblBalanceDue.setAlignmentX(Component.LEFT_ALIGNMENT);
        inspectorPanel.add(lblBalanceDue);

        inspectorPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        inspectorPanel.add(createSectionHeaderLabel("Historical Payment Timeline"));

        epPaymentHistory = new JEditorPane();
        epPaymentHistory.setContentType("text/html");
        epPaymentHistory.setEditable(false);
        epPaymentHistory.setOpaque(false);
        epPaymentHistory.setText("<html><body style='font-family:Segoe UI; font-size:11px; color:#64748b;'>Select a row entry to view tracking milestones.</body></html>");

        JScrollPane epScroll = new JScrollPane(epPaymentHistory);
        epScroll.setBorder(null);
        epScroll.setOpaque(false);
        epScroll.getViewport().setOpaque(false);
        epScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        inspectorPanel.add(epScroll);

        return inspectorPanel;
    }

    private JLabel createSectionHeaderLabel(String headingText) {
        JLabel lbl = new JLabel("* " + headingText.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(BRIGHT_TEAL);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel createMetadataFieldLabel(String plainText) {
        JLabel lbl = new JLabel(plainText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(51, 65, 85));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    public void clearInspectorDeck() {
    if (searchField != null) searchField.setText("");
    if (filterDropdown != null) filterDropdown.setSelectedIndex(0);
    if (orderSortDropdown != null) orderSortDropdown.setSelectedIndex(0);

    if (rowSorter != null) {
        rowSorter.setSortKeys(null); 
        rowSorter.setRowFilter(null);
    }

    if (dataTable != null) dataTable.clearSelection();

    lblGuestName.setText("Full Legal Name: ---");
    lblGuestId.setText("NIC / Passport Serial: ---");
    lblGuestPhone.setText("Mobile Number: ---");
    lblGuestEmail.setText("Email Address: ---");
    lblRoomCategory.setText("Allocated Deck: ---");
    lblStayDates.setText("Stay Interval: ---");
    lblBaseTariff.setText("Base Net Subtotal: ---");
    lblTaxComponent.setText("Operational Tax (10%): ---");
    lblGrossTotal.setText("Gross Total Bill: ---");
    lblStatusTag.setText("Settlement State: ---");
    lblBalanceDue.setText("OUTSTANDING DUE: ---");
    epPaymentHistory.setText("<html><body style='font-family:Segoe UI; font-size:11px; color:#64748b;'>Select a row entry to view tracking milestones.</body></html>");
}

    private void setupRealtimeInteractions(JButton btnPrintJasper) {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyRealtimeFilteringMatrix(); }
            public void removeUpdate(DocumentEvent e) { applyRealtimeFilteringMatrix(); }
            public void changedUpdate(DocumentEvent e) { applyRealtimeFilteringMatrix(); }
        });

        filterDropdown.addActionListener(e -> applyRealtimeFilteringMatrix());

        orderSortDropdown.addActionListener(e -> loadLiveDatabaseRecords());

        btnResetMatrix.addActionListener(e -> {
            clearInspectorDeck();
            loadLiveDatabaseRecords();
            JOptionPane.showMessageDialog(this,
                    "All analytics workspace states and filters have been successfully reset.",
                    "Workspace Refreshed", JOptionPane.INFORMATION_MESSAGE);
        });

        dataTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && dataTable.getSelectedRow() != -1) {
                int modelRow = dataTable.convertRowIndexToModel(dataTable.getSelectedRow());
                String rawBookingId = tableModel.getValueAt(modelRow, 0).toString().replace("BKG-", "");
                int bookingId = Integer.parseInt(rawBookingId);

                fetchAndRenderDossierToSideDeck(bookingId);
            }
        });

        dataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && dataTable.getSelectedRow() != -1) {
                    int modelRow = dataTable.convertRowIndexToModel(dataTable.getSelectedRow());
                    String rawBookingId = tableModel.getValueAt(modelRow, 0).toString().replace("BKG-", "");
                    int bookingId = Integer.parseInt(rawBookingId);
                    String guestName = tableModel.getValueAt(modelRow, 1).toString();
                    String currentStatus = tableModel.getValueAt(modelRow, 7).toString();

                    modifyPaymentWorkflowInline(bookingId, guestName, currentStatus);
                }
            }
        });

        btnPrintJasper.addActionListener(e -> {
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                String sourcePath = "src/main/java/com/ecostay/reports/CorporateRevenueReport.jrxml";
                JasperReport jasperReport = JasperCompileManager.compileReport(sourcePath);
                JasperPrint printLayout = JasperFillManager.fillReport(jasperReport, new HashMap<>(), connection);
                JasperViewer.viewReport(printLayout, false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Reporting Failure: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void fetchAndRenderDossierToSideDeck(int bookingId) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String query = "SELECT b.check_in_date, b.check_out_date, b.final_amount, b.billing_currency, " +
                           "b.payment_status, b.outstanding_balance, b.tax_amount, a.room_type, " +
                           "g.full_name, g.nationality_type, g.identity_number, g.phone_number, g.email_address " +
                           "FROM tbl_bookings b " +
                           "INNER JOIN tbl_guests g ON b.guest_id = g.guest_id " +
                           "INNER JOIN tbl_accommodation a ON b.room_id = a.room_id " +
                           "WHERE b.booking_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String currency = rs.getString("billing_currency");
                double total = rs.getDouble("final_amount");
                double tax = rs.getDouble("tax_amount");
                double base = total - tax;
                String status = rs.getString("payment_status");
                double balance = rs.getDouble("outstanding_balance");

                lblGuestName.setText("<html><b>Full Legal Name:</b> " + rs.getString("full_name") + " (" + rs.getString("nationality_type") + ")</html>");
                lblGuestId.setText("<html><b>NIC / Passport ID:</b> <font color='#0984e3'>" + rs.getString("identity_number") + "</font></html>");
                lblGuestPhone.setText("<html><b>Mobile Number:</b> " + rs.getString("phone_number") + "</html>");
                lblGuestEmail.setText("<html><b>Email Address:</b> " + rs.getString("email_address") + "</html>");

                lblRoomCategory.setText("<html><b>Allocated Deck:</b> " + rs.getString("room_type") + "</html>");
                lblStayDates.setText("<html><b>Interval Window:</b> " + rs.getDate("check_in_date") + " to " + rs.getDate("check_out_date") + "</html>");

                lblBaseTariff.setText(String.format("Base Net Subtotal: %s %,.2f", currency, base));
                lblTaxComponent.setText(String.format("Operational Tax (10%%): %s %,.2f", currency, tax));
                lblGrossTotal.setText(String.format("<html><b>Gross Total Bill: <font color='#00b894'>" + currency + " %,.2f</font></b></html>", total));
                lblStatusTag.setText("<html><b>Settlement State:</b> <font color='#6c5ce7'>" + status + "</font></html>");
                lblBalanceDue.setText(String.format("OUTSTANDING DUE: %s %,.2f", currency, balance));

                String ledgerQuery = "SELECT payment_amount, payment_date, payment_type FROM tbl_payment_ledger WHERE booking_id = ? ORDER BY payment_date ASC";
                PreparedStatement psL = conn.prepareStatement(ledgerQuery);
                psL.setInt(1, bookingId);
                ResultSet rsL = psL.executeQuery();

                StringBuilder htmlTimeline = new StringBuilder("<html><body style='font-family:Segoe UI; font-size:11px; color:#334155;'>");
                htmlTimeline.append("<table style='width:100%; border-collapse:collapse;'>");

                boolean hasLogs = false;
                while (rsL.next()) {
                    hasLogs = true;
                    String dateStr = rsL.getTimestamp("payment_date").toString().substring(0, 16);
                    double amt = rsL.getDouble("payment_amount");
                    String type = rsL.getString("payment_type");

                    htmlTimeline.append("<tr style='border-bottom:1px solid #f1f5f9;'>")
                                .append("<td style='padding:4px 0; color:#00b894;'><b># ").append(type).append("</b></td>")
                                .append("<td style='padding:4px 0; text-align:center; color:#64748b;'>").append(dateStr).append("</td>")
                                .append("<td style='padding:4px 0; text-align:right; font-weight:bold;'>").append(currency).append(" ").append(String.format("%,.2f", amt)).append("</td>")
                                .append("</tr>");
                }

                if (!hasLogs) {
                    htmlTimeline.append("<tr><td colspan='3' style='color:#94a3b8; font-style:italic;'>No explicit metrics located inside payment ledger database fields.</td></tr>");
                }

                htmlTimeline.append("</table></body></html>");
                epPaymentHistory.setText(htmlTimeline.toString());
            }
        } catch (Exception ex) {
            System.out.println("Side deck fetch fault: " + ex.getMessage());
        }
    }

    private void modifyPaymentWorkflowInline(int bookingId, String guestName, String currentStatus) {
        String[] options = {"Full Settlement", "Deposit Advance", "Pending Clearance"};
        String selection = (String) JOptionPane.showInputDialog(this,
                "Update status allocation for: " + guestName, "Ledger Manager",
                JOptionPane.QUESTION_MESSAGE, null, options, currentStatus);

        if (selection == null || selection.equals(currentStatus)) return;

        try {
            Connection conn = DBConnection.getInstance().getConnection();

            String selectQuery = "SELECT final_amount, outstanding_balance FROM tbl_bookings WHERE booking_id = ?";
            PreparedStatement psSelect = conn.prepareStatement(selectQuery);
            psSelect.setInt(1, bookingId);
            ResultSet rs = psSelect.executeQuery();

            double totalBill = 0.00;
            double oldOutstanding = 0.00;
            if (rs.next()) {
                totalBill = rs.getDouble("final_amount");
                oldOutstanding = rs.getDouble("outstanding_balance");
            }

            double updatedBalance = 0.00;
            double structuralPaymentMade = 0.00;

            if ("Full Settlement".equalsIgnoreCase(selection)) {
                updatedBalance = 0.00;
                structuralPaymentMade = oldOutstanding;
            } else if ("Deposit Advance".equalsIgnoreCase(selection)) {
                updatedBalance = totalBill * 0.5;
                structuralPaymentMade = totalBill * 0.5;
            } else {
                updatedBalance = totalBill;
                structuralPaymentMade = 0.00;
            }

            String updateQuery = "UPDATE tbl_bookings SET payment_status = ?, outstanding_balance = ? WHERE booking_id = ?";
            PreparedStatement psUpdate = conn.prepareStatement(updateQuery);
            psUpdate.setString(1, selection);
            psUpdate.setDouble(2, updatedBalance);
            psUpdate.setInt(3, bookingId);
            psUpdate.executeUpdate();

            if (structuralPaymentMade > 0) {
                String logUpdatePay = "INSERT INTO tbl_payment_ledger (booking_id, payment_amount, payment_type) VALUES (?, ?, ?)";
                PreparedStatement psLog = conn.prepareStatement(logUpdatePay);
                psLog.setInt(1, bookingId);
                psLog.setDouble(2, structuralPaymentMade);
                psLog.setString(3, "Full Settlement".equalsIgnoreCase(selection) ? "Balance Settlement" : "Adjustment Pay");
                psLog.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Transaction status fully updated!", "Ledger State Synced", JOptionPane.INFORMATION_MESSAGE);

            loadLiveDatabaseRecords();
            fetchAndRenderDossierToSideDeck(bookingId);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ledger Error: " + ex.getMessage(), "Database Fault", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyRealtimeFilteringMatrix() {
        String searchText = searchField.getText().trim();
        String selectedCategory = (String) filterDropdown.getSelectedItem();

        RowFilter<DefaultTableModel, Object> textFilter = RowFilter.regexFilter("(?i)" + searchText);
        RowFilter<DefaultTableModel, Object> dropdownFilter = null;

        if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
            dropdownFilter = RowFilter.regexFilter("(?i)" + selectedCategory, 3);
        }

        java.util.List<RowFilter<DefaultTableModel, Object>> stages = new java.util.ArrayList<>();
        stages.add(textFilter);
        if (dropdownFilter != null) stages.add(dropdownFilter);

        rowSorter.setRowFilter(RowFilter.andFilter(stages));
    }

    public void loadLiveDatabaseRecords() {
        try {
            tableModel.setRowCount(0);
            Connection conn = DBConnection.getInstance().getConnection();

            String sortDirection = "DESC";
            if (orderSortDropdown != null && orderSortDropdown.getSelectedIndex() == 1) {
                sortDirection = "ASC";
            }

            String query = "SELECT b.booking_id, g.full_name, a.room_id, a.room_type, b.check_in_date, b.check_out_date, b.final_amount, b.billing_currency, b.payment_status, b.outstanding_balance " +
                           "FROM tbl_bookings b " +
                           "INNER JOIN tbl_guests g ON b.guest_id = g.guest_id " +
                           "INNER JOIN tbl_accommodation a ON b.room_id = a.room_id " +
                           "ORDER BY b.booking_id " + sortDirection;

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String currency = rs.getString("billing_currency");
                    tableModel.addRow(new Object[]{
                            "BKG-" + rs.getInt("booking_id"),
                            rs.getString("full_name"),
                            rs.getInt("room_id"),
                            rs.getString("room_type"),
                            rs.getDate("check_in_date").toString(),
                            rs.getDate("check_out_date").toString(),
                            currency + " " + String.format("%,.2f", rs.getDouble("final_amount")),
                            rs.getString("payment_status"),
                            currency + " " + String.format("%,.2f", rs.getDouble("outstanding_balance"))
                    });
                }
            }
        } catch (Exception ex) {
            System.out.println("Live Table Sync Interruption: " + ex.getMessage());
        }
    }
}

