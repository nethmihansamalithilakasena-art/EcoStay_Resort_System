package com.ecostay.view;

import com.ecostay.util.DBConnection;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReservationPanel extends JPanel {

    private final Color NAVY_DARK = new Color(20, 36, 65);
    private final Color CARD_BORDER = new Color(226, 232, 240);
    private final Color TEAL_SUBMIT = new Color(0, 184, 148);
    private final Color TEXT_MAIN = new Color(30, 41, 59);

    private JTextField txtName, txtIdentity, txtContact, txtEmail;
    private JComboBox<String> comboResidency, comboPaymentStatus;
    private DatePicker checkInPicker, checkOutPicker;
    private ButtonGroup roomButtonGroup;

    private String selectedRoomType = "Luxury Canopy Treehouse Suite";

    public ReservationPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints mainConstraints = new GridBagConstraints();
        mainConstraints.fill = GridBagConstraints.BOTH;
        mainConstraints.weighty = 1.0;
        mainConstraints.insets = new Insets(0, 10, 0, 10);

        JPanel leftOuterWrapper = new JPanel(new GridBagLayout());
        leftOuterWrapper.setOpaque(false);

        JPanel leftFormCard = new JPanel(new GridBagLayout());
        leftFormCard.setBackground(Color.WHITE);
        leftFormCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(20, 25, 20, 25)
        ));
        leftFormCard.setPreferredSize(new Dimension(550, 620));
        leftFormCard.setMinimumSize(new Dimension(550, 620));

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.gridx = 0;
        fc.insets = new Insets(4, 0, 4, 0);

        JLabel formTitle = new JLabel("New Customer Reservation Profile");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(NAVY_DARK);
        formTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        leftFormCard.add(formTitle, fc);

        leftFormCard.add(createInputLabel("Full Name"), fc);
        txtName = createStyledTextField(); leftFormCard.add(txtName, fc);

        leftFormCard.add(createInputLabel("Residency Type"), fc);
        comboResidency = new JComboBox<>(new String[]{"Local", "Foreign"});
        comboResidency.setPreferredSize(new Dimension(0, 36));
        leftFormCard.add(comboResidency, fc);

        leftFormCard.add(createInputLabel("Passport / National ID Serial Identifier"), fc);
        txtIdentity = createStyledTextField(); leftFormCard.add(txtIdentity, fc);

        leftFormCard.add(createInputLabel("Contact Number"), fc);
        txtContact = createStyledTextField(); leftFormCard.add(txtContact, fc);

        leftFormCard.add(createInputLabel("Email Address"), fc);
        txtEmail = createStyledTextField(); leftFormCard.add(txtEmail, fc);

        leftFormCard.add(createInputLabel("Payment Status Allocation"), fc);
        comboPaymentStatus = new JComboBox<>(new String[]{"Full Settlement", "Deposit Advance", "Pending Clearance"});
        comboPaymentStatus.setPreferredSize(new Dimension(0, 36));
        leftFormCard.add(comboPaymentStatus, fc);

        JPanel datesRow = new JPanel(new GridLayout(1, 2, 15, 0));
        datesRow.setOpaque(false);

        JPanel checkInBlock = new JPanel(new BorderLayout(0, 3)); checkInBlock.setOpaque(false);
        checkInBlock.add(createInputLabel("Check-In Date"), BorderLayout.NORTH);
        checkInPicker = createModernDatePicker(); checkInBlock.add(checkInPicker, BorderLayout.CENTER);
        datesRow.add(checkInBlock);

        JPanel checkOutBlock = new JPanel(new BorderLayout(0, 3)); checkOutBlock.setOpaque(false);
        checkOutBlock.add(createInputLabel("Check-Out Date"), BorderLayout.NORTH);
        checkOutPicker = createModernDatePicker(); checkOutBlock.add(checkOutPicker, BorderLayout.CENTER);
        datesRow.add(checkOutBlock);

        fc.insets = new Insets(8, 0, 15, 0);
        leftFormCard.add(datesRow, fc);

        JButton btnSubmit = new JButton("Authorize & Commit Transaction");
        btnSubmit.setPreferredSize(new Dimension(0, 42));
        btnSubmit.setBackground(TEAL_SUBMIT);
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        fc.insets = new Insets(5, 0, 0, 0);
        leftFormCard.add(btnSubmit, fc);

        leftOuterWrapper.add(leftFormCard, new GridBagConstraints());
        mainConstraints.weightx = 0.55;
        mainConstraints.gridx = 0;
        add(leftOuterWrapper, mainConstraints);

        JPanel rightSelectionCard = new JPanel();
        rightSelectionCard.setLayout(new BoxLayout(rightSelectionCard, BoxLayout.Y_AXIS));
        rightSelectionCard.setOpaque(false);

        roomButtonGroup = new ButtonGroup();
        rightSelectionCard.add(createPremiumRoomCard("Luxury Canopy Treehouse Suite", "Premium elevated canopy living configurations.", true));
        rightSelectionCard.add(Box.createRigidArea(new Dimension(0, 12)));
        rightSelectionCard.add(createPremiumRoomCard("Geodesic Wilderness Safari Dome", "Panoramic structural glass dome templates.", false));
        rightSelectionCard.add(Box.createRigidArea(new Dimension(0, 12)));
        rightSelectionCard.add(createPremiumRoomCard("Riverside Sustainable Eco Lodge", "Direct flowing riverfront access with automated utility frameworks.", false));

        mainConstraints.weightx = 0.45;
        mainConstraints.gridx = 1;
        add(rightSelectionCard, mainConstraints);

        btnSubmit.addActionListener(e -> processEnterpriseReservationFlow());
    }

    private JLabel createInputLabel(String labelText) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_MAIN);
        return lbl;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 36));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(0, 10, 0, 10)
        ));
        return field;
    }

    private DatePicker createModernDatePicker() {
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFontValidDate(new Font("Segoe UI", Font.PLAIN, 13));
        settings.setAllowKeyboardEditing(true);
        DatePicker picker = new DatePicker(settings);
        picker.setPreferredSize(new Dimension(0, 36));
        picker.setDate(LocalDate.now());
        return picker;
    }

    private JPanel createPremiumRoomCard(String title, String description, boolean defaultSelected) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

        JRadioButton radioButton = new JRadioButton();
        radioButton.setSelected(defaultSelected);
        radioButton.setOpaque(false);
        radioButton.addActionListener(e -> selectedRoomType = title);
        roomButtonGroup.add(radioButton);
        card.add(radioButton, BorderLayout.WEST);

        JPanel details = new JPanel(new GridLayout(2, 1, 2, 2));
        details.setOpaque(false);
        JLabel lblTitle = new JLabel(title); lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13)); lblTitle.setForeground(NAVY_DARK);
        JLabel lblDesc = new JLabel("<html>" + description + "</html>"); lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11)); lblDesc.setForeground(Color.GRAY);
        details.add(lblTitle); details.add(lblDesc);
        card.add(details, BorderLayout.CENTER);

        return card;
    }

    private void processEnterpriseReservationFlow() {
        String guestName = txtName.getText().trim();
        String residency = comboResidency.getSelectedItem().toString();
        String identity = txtIdentity.getText().trim();
        String contact = txtContact.getText().trim();
        String email = txtEmail.getText().trim();
        String payStatus = comboPaymentStatus.getSelectedItem().toString();
        LocalDate checkIn = checkInPicker.getDate();
        LocalDate checkOut = checkOutPicker.getDate();

        // Standard Empty Field Validation Check
        if (guestName.isEmpty() || identity.isEmpty() || contact.isEmpty() || email.isEmpty() || checkIn == null || checkOut == null) {
            JOptionPane.showMessageDialog(this, "Validation Failure: All field metrics required.", "Form Interruption", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- SPECIFIC USER REQUIREMENT 1: EMAIL REGISTRATION VALIDATION ---
        if (!email.toLowerCase().endsWith("@gmail.com")) {
            JOptionPane.showMessageDialog(this, "Registration Stopped: The entered email is not valid. It must end with @gmail.com to continue.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return; // Completely stops the process right here
        }

        // --- SPECIFIC USER REQUIREMENT 2: CURRENT DATE PROTECTION VALIDATION ---
        LocalDate todayDate = LocalDate.now();
        if (checkIn.isBefore(todayDate)) {
            JOptionPane.showMessageDialog(this, "Registration Stopped: The checking date is not valid. The check-in date cannot be a previous date.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return; // Completely stops the process right here
        }

        long daysBetween = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (daysBetween <= 0) {
            JOptionPane.showMessageDialog(this, "Operational Error: Check-Out must occur after Check-In.", "Invalid Dates", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = DBConnection.getInstance().getConnection();

            String roomQuery = "SELECT room_id, base_rate_usd, base_rate_lkr FROM tbl_accommodation " +
                               "WHERE room_type LIKE ? OR room_type LIKE ? OR room_type LIKE ? LIMIT 1";
            PreparedStatement psRoom = conn.prepareStatement(roomQuery);

            String keyword = "";
            if (selectedRoomType.contains("Treehouse")) keyword = "%Treehouse%";
            else if (selectedRoomType.contains("Safari") || selectedRoomType.contains("Geodesic")) keyword = "%Safari%";
            else if (selectedRoomType.contains("Lodge") || selectedRoomType.contains("Riverside")) keyword = "%Lodge%";

            psRoom.setString(1, keyword);
            psRoom.setString(2, "%" + selectedRoomType.substring(0, 8) + "%");
            psRoom.setString(3, selectedRoomType);

            ResultSet rsRoom = psRoom.executeQuery();

            if (!rsRoom.next()) {
                JOptionPane.showMessageDialog(this, "Infrastructure Error: Selected room configuration missing.", "Database Sync Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int roomId = rsRoom.getInt("room_id");
            double rateUsd = rsRoom.getDouble("base_rate_usd");
            double rateLkr = rsRoom.getDouble("base_rate_lkr");

            double totalGrossAmount = ("Foreign".equalsIgnoreCase(residency)) ? (rateUsd * daysBetween) : (rateLkr * daysBetween);
            String currencyMarker = ("Foreign".equalsIgnoreCase(residency)) ? "USD" : "LKR";

            double computedTax = totalGrossAmount * 0.10;
            double totalWithTax = totalGrossAmount + computedTax;

            double amountPaid = 0.00;
            if ("Full Settlement".equalsIgnoreCase(payStatus)) {
                amountPaid = totalWithTax;
            } else if ("Deposit Advance".equalsIgnoreCase(payStatus)) {
                amountPaid = totalWithTax * 0.5;
            }
            double outstandingBalance = totalWithTax - amountPaid;

            String modernHtmlMessage = "<html>" +
                "<body style='font-family: \"Segoe UI\", sans-serif; color: #1e293b; width: 320px;'>" +
                "  <div style='background-color: #142441; padding: 12px; margin-bottom: 15px; border-radius: 4px;'>" +
                "    <h3 style='color: #ffffff; margin: 0; font-size: 15px;'>Transaction Preview Summary</h3>" +
                "  </div>" +
                "  <table style='width: 100%; font-size: 12px; border-collapse: collapse;'>" +
                "    <tr style='border-bottom: 1px solid #f1f5f9;'><td style='padding: 6px 0; color: #64748b;'><b>Guest Name:</b></td><td style='text-align: right; font-weight: bold;'>" + guestName + " (" + residency + ")</td></tr>" +
                "    <tr style='border-bottom: 1px solid #f1f5f9;'><td style='padding: 6px 0; color: #64748b;'><b>Room Type:</b></td><td style='text-align: right;'>" + selectedRoomType + "</td></tr>" +
                "    <tr style='border-bottom: 1px solid #f1f5f9;'><td style='padding: 6px 0; color: #64748b;'><b>Duration:</b></td><td style='text-align: right;'>" + daysBetween + " Nights</td></tr>" +
                "    <tr style='border-bottom: 1px solid #f1f5f9;'><td style='padding: 6px 0; color: #64748b;'><b>Base Subtotal:</b></td><td style='text-align: right;'>" + currencyMarker + " " + String.format("%,.2f", totalGrossAmount) + "</td></tr>" +
                "    <tr style='border-bottom: 1px solid #f1f5f9;'><td style='padding: 6px 0; color: #64748b;'><b>Tax Component (10%):</b></td><td style='text-align: right;'>" + currencyMarker + " " + String.format("%,.2f", computedTax) + "</td></tr>" +
                "    <tr style='border-bottom: 2px solid #cbd5e1; background-color: #f8fafc;'><td style='padding: 8px 0; color: #142441;'><b>Gross Total Bill:</b></td><td style='text-align: right; font-weight: bold; color: #00b894;'>" + currencyMarker + " " + String.format("%,.2f", totalWithTax) + "</td></tr>" +
                "    <tr style='border-bottom: 1px solid #f1f5f9;'><td style='padding: 6px 0; color: #64748b;'><b>Status Allocation:</b></td><td style='text-align: right; font-weight: bold; color: #0984e3;'>" + payStatus + "</td></tr>" +
                "    <tr><td style='padding: 8px 0; color: #64748b;'><b>Outstanding Balance:</b></td><td style='text-align: right; font-weight: bold; color: #d63031;'>" + currencyMarker + " " + String.format("%,.2f", outstandingBalance) + "</td></tr>" +
                "  </table>" +
                "  <p style='font-size: 11px; color: #64748b; margin-top: 15px;'>Commit this secure transaction log entry to the master ledger database?</p>" +
                "</body>" +
                "</html>";

            int userChoice = JOptionPane.showConfirmDialog(this, modernHtmlMessage, "System Ledger Authorization", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (userChoice != JOptionPane.YES_OPTION) return;

            conn.setAutoCommit(false);
            try {
                String insertGuest = "INSERT INTO tbl_guests (full_name, nationality_type, identity_number, phone_number, email_address) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psG = conn.prepareStatement(insertGuest, Statement.RETURN_GENERATED_KEYS);
                psG.setString(1, guestName); psG.setString(2, residency); psG.setString(3, identity); psG.setString(4, contact); psG.setString(5, email);
                psG.executeUpdate();

                ResultSet rsG = psG.getGeneratedKeys();
                rsG.next();
                int assignedGuestId = rsG.getInt(1);

                String insertBooking = "INSERT INTO tbl_bookings (guest_id, room_id, check_in_date, check_out_date, final_amount, billing_currency, payment_status, outstanding_balance, tax_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psB = conn.prepareStatement(insertBooking, Statement.RETURN_GENERATED_KEYS);
                psB.setInt(1, assignedGuestId);
                psB.setInt(2, roomId);
                psB.setDate(3, java.sql.Date.valueOf(checkIn));
                psB.setDate(4, java.sql.Date.valueOf(checkOut));
                psB.setDouble(5, totalWithTax);
                psB.setString(6, currencyMarker);
                psB.setString(7, payStatus);
                psB.setDouble(8, outstandingBalance);
                psB.setDouble(9, computedTax);
                psB.executeUpdate();

                ResultSet rsB = psB.getGeneratedKeys();
                int assignedBookingId = 0;
                if (rsB.next()) {
                    assignedBookingId = rsB.getInt(1);
                }

                if (amountPaid > 0 && assignedBookingId > 0) {
                    String logInitialPay = "INSERT INTO tbl_payment_ledger (booking_id, payment_amount, payment_type) VALUES (?, ?, ?)";
                    PreparedStatement psLog = conn.prepareStatement(logInitialPay);
                    psLog.setInt(1, assignedBookingId);
                    psLog.setDouble(2, amountPaid);
                    psLog.setString(3, "Deposit Advance".equalsIgnoreCase(payStatus) ? "Initial Advance" : "Full Settlement");
                    psLog.executeUpdate();
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "Transaction securely logged to database!", "Ledger Entry Success", JOptionPane.INFORMATION_MESSAGE);

                clearFormInputFields();

            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Transaction Exception: " + ex.getMessage(), "System Write Fault", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFormInputFields() {
        txtName.setText(""); txtIdentity.setText(""); txtContact.setText(""); txtEmail.setText("");
        comboResidency.setSelectedIndex(0); comboPaymentStatus.setSelectedIndex(0);
        checkInPicker.setDate(LocalDate.now()); checkOutPicker.setDate(LocalDate.now());
    }
}