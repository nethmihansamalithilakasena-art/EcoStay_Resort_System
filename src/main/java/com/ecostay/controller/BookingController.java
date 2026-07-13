package com.ecostay.controller;

import com.ecostay.model.Guest;
import com.ecostay.util.DBConnection;
import com.ecostay.exception.InvalidBookingException;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Vector;

public class BookingController {

    
    public void processReservation(Guest guest, String roomType, String checkInStr, String checkOutStr) throws Exception {
        LocalDate checkIn = LocalDate.parse(checkInStr);
        LocalDate checkOut = LocalDate.parse(checkOutStr);

      
        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidBookingException("Operational Timeline Conflict: Check-out must be after Check-in date.");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        Connection conn = DBConnection.getInstance().getConnection();

        // Begin ACiD Transaction Block for full relational integrity
        conn.setAutoCommit(false);
        try {
            
            String insertGuestSQL = "INSERT INTO tbl_guests (full_name, nationality_type, identity_number, phone_number, email_address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psGuest = conn.prepareStatement(insertGuestSQL, Statement.RETURN_GENERATED_KEYS);
            psGuest.setString(1, guest.getFullName());
            psGuest.setString(2, guest.getNationalityType());
            psGuest.setString(3, guest.getIdentityNumber());
            psGuest.setString(4, guest.getPhoneNumber());
            psGuest.setString(5, guest.getEmailAddress());
            psGuest.executeUpdate();

            ResultSet rs = psGuest.getGeneratedKeys();
            int guestId = 0;
            if (rs.next()) {
                guestId = rs.getInt(1);
            }

            String roomSQL = "SELECT room_id, base_rate_usd, base_rate_lkr FROM tbl_accommodation WHERE room_type = ? LIMIT 1";
            PreparedStatement psRoom = conn.prepareStatement(roomSQL);
            psRoom.setString(1, roomType);
            ResultSet rsRoom = psRoom.executeQuery();

            int roomId = 0;
            double baseRate = 0;
            String currency = "LKR";

            if (rsRoom.next()) {
                roomId = rsRoom.getInt("room_id");
                if (guest.getNationalityType().equals("Foreign")) {
                    baseRate = rsRoom.getDouble("base_rate_usd");
                    currency = "USD";
                } else {
                    baseRate = rsRoom.getDouble("base_rate_lkr");
                    currency = "LKR";
                }
            } else {
                throw new SQLException("Error: Selected Eco-Dwelling infrastructure configuration not found.");
            }

            double subTotal = baseRate * nights;
            double tax = subTotal * 0.08; // 8% Regional SSCL/VAT matrix calculation
            double total = subTotal + tax;

           
            String bookingSQL = "INSERT INTO tbl_bookings (guest_id, room_id, check_in_date, check_out_date, billing_currency, tax_amount, final_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psBook = conn.prepareStatement(bookingSQL);
            psBook.setInt(1, guestId);
            psBook.setInt(2, roomId);
            psBook.setDate(3, java.sql.Date.valueOf(checkIn));
            psBook.setDate(4, java.sql.Date.valueOf(checkOut));
            psBook.setString(5, currency);
            psBook.setDouble(6, tax);
            psBook.setDouble(7, total);
            psBook.executeUpdate();

            conn.commit(); // Finalize save event pipeline
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }


    public Vector<Vector<Object>> getLiveLogs() throws Exception {
        Vector<Vector<Object>> data = new Vector<>();
        Connection conn = DBConnection.getInstance().getConnection();
        String query = "SELECT b.booking_id, g.full_name, g.nationality_type, a.room_type, CONCAT(b.final_amount, ' ', b.billing_currency) as total " +
                       "FROM tbl_bookings b " +
                       "INNER JOIN tbl_guests g ON b.guest_id = g.guest_id " +
                       "INNER JOIN tbl_accommodation a ON b.room_id = a.room_id " +
                       "ORDER BY b.booking_id DESC";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add("BKG-" + rs.getInt("booking_id"));
            row.add(rs.getString("full_name"));
            row.add(rs.getString("nationality_type"));
            row.add(rs.getString("room_type"));
            row.add(rs.getString("total"));
            data.add(row);
        }
        return data;
    }
}