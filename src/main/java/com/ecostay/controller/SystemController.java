package com.ecostay.controller;

import com.ecostay.model.Guest;
import com.ecostay.util.DBConnection;
import com.ecostay.exception.InvalidBookingException;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SystemController {

    public void saveBookingTransaction(Guest guest, String roomType, String inStr, String outStr) throws Exception {
        LocalDate checkIn = LocalDate.parse(inStr);
        LocalDate checkOut = LocalDate.parse(outStr);

        
        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidBookingException("Validation Failure: Checkout timeline cannot precede checkin date.");
        }

        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        Connection conn = DBConnection.getInstance().getConnection();
        
       
        conn.setAutoCommit(false);
        try {
            
            String guestSQL = "INSERT INTO tbl_guests (full_name, nationality_type, identity_number, phone_number, email_address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psG = conn.prepareStatement(guestSQL, Statement.RETURN_GENERATED_KEYS);
            psG.setString(1, guest.getFullName());
            psG.setString(2, guest.getNationalityType());
            psG.setString(3, guest.getIdentityNumber());
            psG.setString(4, guest.getPhoneNumber());
            psG.setString(5, guest.getEmailAddress());
            psG.executeUpdate();

            ResultSet rsG = psG.getGeneratedKeys();
            int guestId = 0;
            if (rsG.next()) guestId = rsG.getInt(1);

            
            String roomSQL = "SELECT room_id, base_rate_usd, base_rate_lkr FROM tbl_accommodation WHERE room_type = ? LIMIT 1";
            PreparedStatement psR = conn.prepareStatement(roomSQL);
            psR.setString(1, roomType);
            ResultSet rsR = psR.executeQuery();

            int roomId = 0;
            double pricePerNight = 0;
            String billingCurrency = "LKR";

            if (rsR.next()) {
                roomId = rsR.getInt("room_id");
                if (guest.getNationalityType().equalsIgnoreCase("Foreign")) {
                    pricePerNight = rsR.getDouble("base_rate_usd");
                    billingCurrency = "USD";
                } else {
                    pricePerNight = rsR.getDouble("base_rate_lkr");
                    billingCurrency = "LKR";
                }
            } else {
                throw new SQLException("Configuration Mapping Alert: Target room configuration invalid.");
            }

            double initialCost = pricePerNight * days;
            double taxComponent = initialCost * 0.08; 
            double finalInvoiceCost = initialCost + taxComponent;

           
            String bookingSQL = "INSERT INTO tbl_bookings (guest_id, room_id, check_in_date, check_out_date, billing_currency, tax_amount, final_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psB = conn.prepareStatement(bookingSQL);
            psB.setInt(1, guestId);
            psB.setInt(2, roomId);
            psB.setDate(3, java.sql.Date.valueOf(checkIn));
            psB.setDate(4, java.sql.Date.valueOf(checkOut));
            psB.setString(5, billingCurrency);
            psB.setDouble(6, taxComponent);
            psB.setDouble(7, finalInvoiceCost);
            psB.executeUpdate();

            
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }
    public java.util.Vector<java.util.Vector<Object>> fetchRelationalDatabaseLogs() throws Exception {
    java.util.Vector<java.util.Vector<Object>> manifest = new java.util.Vector<>();
    Connection conn = com.ecostay.util.DBConnection.getInstance().getConnection();
    
    
    String query = "SELECT b.booking_id, g.full_name, g.nationality_type, a.room_type, " +
                   "CONCAT(b.final_amount, ' ', b.billing_currency) AS gross_total " +
                   "FROM tbl_bookings b " +
                   "INNER JOIN tbl_guests g ON b.guest_id = g.guest_id " +
                   "INNER JOIN tbl_accommodation a ON b.room_id = a.room_id " +
                   "ORDER BY b.booking_id DESC";

    try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            java.util.Vector<Object> row = new java.util.Vector<>();
            row.add("BKG-" + rs.getInt("booking_id"));
            row.add(rs.getString("full_name"));
            row.add(rs.getString("nationality_type"));
            row.add(rs.getString("room_type"));
            row.add(rs.getString("gross_total"));
            manifest.add(row);
        }
    }
    return manifest;
}
}