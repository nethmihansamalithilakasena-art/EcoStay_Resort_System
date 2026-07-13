
CREATE DATABASE IF NOT EXISTS ecostay_db;
USE ecostay_db;


DROP TABLE IF EXISTS tbl_payment_ledger;
DROP TABLE IF EXISTS tbl_bookings;
DROP TABLE IF EXISTS tbl_users;
DROP TABLE IF EXISTS tbl_accommodation;
DROP TABLE IF EXISTS tbl_guests;

CREATE TABLE tbl_guests (
    guest_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    nationality_type ENUM('Local', 'Foreign') NOT NULL,
    identity_number VARCHAR(30) NOT NULL UNIQUE, -- Holds NIC for Locals, Passport for Foreigners
    phone_number VARCHAR(20) NOT NULL,
    email_address VARCHAR(50) NOT NULL
);

CREATE TABLE tbl_accommodation (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_type VARCHAR(50) NOT NULL,
    base_rate_usd DECIMAL(10,2) NOT NULL,
    base_rate_lkr DECIMAL(10,2) NOT NULL,
    room_status VARCHAR(20) DEFAULT 'Available'
);

CREATE TABLE tbl_users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    associated_role ENUM('Admin Manager', 'Reception Operator') NOT NULL,
    display_name VARCHAR(100) NOT NULL
);


CREATE TABLE tbl_bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    billing_currency VARCHAR(10) NOT NULL,
    tax_amount DECIMAL(10,2) NOT NULL,
    final_amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('Full Settlement', 'Deposit Advance', 'Pending Clearance') NOT NULL DEFAULT 'Pending Clearance',
    outstanding_balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (guest_id) REFERENCES tbl_guests(guest_id) ON DELETE RESTRICT,
    FOREIGN KEY (room_id) REFERENCES tbl_accommodation(room_id) ON DELETE RESTRICT
);

CREATE TABLE tbl_payment_ledger (
    ledger_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    payment_amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_type VARCHAR(50) NOT NULL, -- 'Initial Advance', 'Balance Settlement', etc.
    FOREIGN KEY (booking_id) REFERENCES tbl_bookings(booking_id) ON DELETE CASCADE
);

INSERT INTO tbl_accommodation (room_type, base_rate_usd, base_rate_lkr, room_status) VALUES
('Luxury Canopy Treehouse', 250.00, 75000.00, 'Available'),
('Geodesic Safari Dome', 180.00, 54000.00, 'Available'),
('Riverside Eco Lodge', 200.00, 60000.00, 'Available');

INSERT INTO tbl_users (username, password_hash, associated_role, display_name) VALUES
('manager', 'manager123', 'Admin Manager', 'Mr. Perera (Director)'),
('reception', 'reception123', 'Reception Operator', 'Nethmi (Front Desk Console)');


SELECT * FROM tbl_guests;
SELECT * FROM tbl_accommodation;
SELECT * FROM tbl_users;
SELECT * FROM tbl_bookings;
SELECT * FROM tbl_payment_ledger;

INSERT INTO tbl_guests (guest_id, full_name, nationality_type, identity_number, phone_number, email_address) VALUES
(1, 'Steve Rogers', 'Foreign', 'US-PP-998231', '+1 555-019-2831', 'steve.rogers@stark.com'),
(2,'Peter Parker', 'Foreign', 'US-PP-442156', '+1 555-014-9923', 'peter.parker@dailybugle.com'),
(3, 'Nimal Perera', 'Local', '199234859123', '+94 77 123 4567', 'nimal.p@gmail.com');

INSERT INTO tbl_bookings (booking_id, guest_id, room_id, check_in_date, check_out_date, billing_currency, tax_amount, final_amount, payment_status, outstanding_balance) VALUES
(1, 1, 1, '2026-07-12', '2026-07-16', 'USD', 80.00, 880.00, 'Full Settlement', 0.00),
(2, 2, 2, '2026-07-14', '2026-07-16', 'USD', 36.00, 396.00, 'Deposit Advance', 198.00),
(3, 3, 3, '2026-07-12', '2026-07-14', 'LKR', 12000.00, 132000.00, 'Pending Clearance', 132000.00);

INSERT INTO tbl_payment_ledger (booking_id, payment_amount, payment_type) VALUES
(1, 880.00, 'Initial Advance'),
(2, 198.00, 'Deposit Advance');
