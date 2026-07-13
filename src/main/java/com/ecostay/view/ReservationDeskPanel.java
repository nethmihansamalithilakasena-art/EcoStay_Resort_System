package com.ecostay.view;

import com.ecostay.model.Guest;
import com.ecostay.controller.BookingController;
import com.ecostay.exception.InvalidBookingException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class ReservationDeskPanel extends JPanel {

    private JTextField txtName, txtId, txtPhone, txtEmail, txtIn, txtOut;
    private JComboBox<String> cmbRes, cmbRoom;
    private JLabel lblId, lblRateVal, lblTaxVal, lblTotalVal;
    private JTable logTable;
    private DefaultTableModel tableModel;
    private BookingController controller;

    public ReservationDeskPanel() {
        controller = new BookingController();
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(244, 246, 248));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Two Column Split View
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(getBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Left Side Form
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        gbc.insets = new Insets(0, 0, 0, 15);
        mainContent.add(buildFormPanel(), gbc);

        // Right Side Table & Summary
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.6;
        gbc.insets = new Insets(0, 15, 0, 0);
        mainContent.add(buildGridPanel(), gbc);

        add(mainContent, BorderLayout.CENTER);
        refreshTableData();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, 0);
        c.weightx = 1.0;
        c.gridx = 0;

        JLabel title = new JLabel("Transaction Booking Setup");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(title, c);

        panel.add(new JLabel("Guest Full Name:"), c);
        txtName = new JTextField(); txtName.setPreferredSize(new Dimension(0, 35));
        panel.add(txtName, c);

        panel.add(new JLabel("Residency Classification:"), c);
        cmbRes = new JComboBox<>(new String[]{"Local", "Foreign"});
        cmbRes.setPreferredSize(new Dimension(0, 35));
        panel.add(cmbRes, c);

        lblId = new JLabel("NIC Identification Number:");
        panel.add(lblId, c);
        txtId = new JTextField(); txtId.setPreferredSize(new Dimension(0, 35));
        panel.add(txtId, c);

        panel.add(new JLabel("Contact Mobile No:"), c);
        txtPhone = new JTextField(); txtPhone.setPreferredSize(new Dimension(0, 35));
        panel.add(txtPhone, c);

        panel.add(new JLabel("Email Address:"), c);
        txtEmail = new JTextField(); txtEmail.setPreferredSize(new Dimension(0, 35));
        panel.add(txtEmail, c);

        panel.add(new JLabel("Infrastructure Allocation:"), c);
        cmbRoom = new JComboBox<>(new String[]{"Luxury Canopy Treehouse", "Geodesic Safari Dome", "Riverside Eco Lodge"});
        cmbRoom.setPreferredSize(new Dimension(0, 35));
        panel.add(cmbRoom, c);

        panel.add(new JLabel("Check-In Timeline (YYYY-MM-DD):"), c);
        txtIn = new JTextField("2026-07-15"); txtIn.setPreferredSize(new Dimension(0, 35));
        panel.add(txtIn, c);

        panel.add(new JLabel("Check-Out Timeline (YYYY-MM-DD):"), c);
        txtOut = new JTextField("2026-07-20"); txtOut.setPreferredSize(new Dimension(0, 35));
        panel.add(txtOut, c);

        JButton btnSubmit = new JButton("Commit Booking Transaction");
        btnSubmit.setPreferredSize(new Dimension(0, 40));
        btnSubmit.setBackground(new Color(46, 204, 113));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        c.insets = new Insets(15, 0, 0, 0);
        panel.add(btnSubmit, c);

        cmbRes.addActionListener(e -> {
            if (cmbRes.getSelectedItem().toString().equals("Foreign")) {
                lblId.setText("Passport Serial Identifier:");
            } else {
                lblId.setText("NIC Identification Number:");
            }
        });

        btnSubmit.addActionListener(e -> handleTransaction());

        return panel;
    }

    private JPanel buildGridPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(getBackground());

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        String[] cols = {"ID", "Name", "Type", "Allocated Room", "Settled Cost"};
        tableModel = new DefaultTableModel(cols, 0);
        logTable = new JTable(tableModel);
        logTable.setRowHeight(32);
        logTable.setShowVerticalLines(false);
        logTable.setGridColor(new Color(240, 240, 240));

        tableCard.add(new JScrollPane(logTable), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    private void handleTransaction() {
        try {
            if (txtName.getText().trim().isEmpty() || txtId.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Validation Failure: Missing essential info fields.");
            }
            Guest g = new Guest(txtName.getText().trim(), cmbRes.getSelectedItem().toString(), txtId.getText().trim(), txtPhone.getText().trim(), txtEmail.getText().trim());
            controller.processReservation(g, cmbRoom.getSelectedItem().toString(), txtIn.getText().trim(), txtOut.getText().trim());
            JOptionPane.showMessageDialog(this, "Success: Transaction committed to MySQL database.", "Database Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTableData();
        } catch (InvalidBookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Logic Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "System Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshTableData() {
        try {
            Vector<Vector<Object>> data = controller.getLiveLogs();
            tableModel.setRowCount(0);
            for (Vector<Object> r : data) tableModel.addRow(r);
        } catch (Exception ignored) {}
    }
}
