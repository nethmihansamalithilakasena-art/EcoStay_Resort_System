CREATE DATABASE  IF NOT EXISTS `ecostay_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `ecostay_db`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: ecostay_db
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tbl_accommodation`
--

DROP TABLE IF EXISTS `tbl_accommodation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_accommodation` (
  `room_id` int NOT NULL AUTO_INCREMENT,
  `room_type` varchar(50) NOT NULL,
  `base_rate_usd` decimal(10,2) NOT NULL,
  `base_rate_lkr` decimal(10,2) NOT NULL,
  `room_status` varchar(20) DEFAULT 'Available',
  PRIMARY KEY (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_accommodation`
--

LOCK TABLES `tbl_accommodation` WRITE;
/*!40000 ALTER TABLE `tbl_accommodation` DISABLE KEYS */;
INSERT INTO `tbl_accommodation` VALUES (1,'Luxury Canopy Treehouse',250.00,75000.00,'Available'),(2,'Geodesic Safari Dome',180.00,54000.00,'Available'),(3,'Riverside Eco Lodge',200.00,60000.00,'Available');
/*!40000 ALTER TABLE `tbl_accommodation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_bookings`
--

DROP TABLE IF EXISTS `tbl_bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_bookings` (
  `booking_id` int NOT NULL AUTO_INCREMENT,
  `guest_id` int DEFAULT NULL,
  `room_id` int DEFAULT NULL,
  `check_in_date` date NOT NULL,
  `check_out_date` date NOT NULL,
  `billing_currency` varchar(10) NOT NULL,
  `tax_amount` decimal(10,2) NOT NULL,
  `final_amount` decimal(10,2) NOT NULL,
  `payment_status` enum('Full Settlement','Deposit Advance','Pending Clearance') NOT NULL DEFAULT 'Pending Clearance',
  `outstanding_balance` decimal(10,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`booking_id`),
  KEY `guest_id` (`guest_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `tbl_bookings_ibfk_1` FOREIGN KEY (`guest_id`) REFERENCES `tbl_guests` (`guest_id`),
  CONSTRAINT `tbl_bookings_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `tbl_accommodation` (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_bookings`
--

LOCK TABLES `tbl_bookings` WRITE;
/*!40000 ALTER TABLE `tbl_bookings` DISABLE KEYS */;
INSERT INTO `tbl_bookings` VALUES (1,1,1,'2026-07-15','2026-07-18','LKR',18000.00,243000.00,'Full Settlement',0.00),(2,2,2,'2026-07-15','2026-07-20','USD',72.00,972.00,'Full Settlement',0.00),(3,4,2,'2026-07-13','2026-07-16','USD',54.00,594.00,'Full Settlement',0.00),(4,5,3,'2026-07-13','2026-07-17','LKR',24000.00,264000.00,'Full Settlement',0.00),(5,6,3,'2026-07-14','2026-07-16','LKR',12000.00,132000.00,'Deposit Advance',66000.00),(6,7,1,'2026-07-14','2026-07-16','USD',50.00,550.00,'Deposit Advance',275.00),(7,8,3,'2026-07-12','2026-07-14','LKR',12000.00,132000.00,'Full Settlement',0.00),(8,9,3,'2026-07-12','2026-07-16','USD',80.00,880.00,'Full Settlement',0.00);
/*!40000 ALTER TABLE `tbl_bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_guests`
--

DROP TABLE IF EXISTS `tbl_guests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_guests` (
  `guest_id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `nationality_type` enum('Local','Foreign') NOT NULL,
  `identity_number` varchar(30) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `email_address` varchar(50) NOT NULL,
  PRIMARY KEY (`guest_id`),
  UNIQUE KEY `identity_number` (`identity_number`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_guests`
--

LOCK TABLES `tbl_guests` WRITE;
/*!40000 ALTER TABLE `tbl_guests` DISABLE KEYS */;
INSERT INTO `tbl_guests` VALUES (1,'Nethmi Thilakasena','Local','200375400752','0773113140','nethmithilakasena@gmail.com'),(2,'Michael','Foreign','45678912','9478569823','michael1995@gmail.com'),(4,'stella','Foreign','1571654985','9654852633','stella@gmail.com'),(5,'Fernando','Local','68684161v','0742545678','ffernando@gmail.com'),(6,'Amal Kumara','Local','70458961','0784596321','amalK@gmail.com'),(7,'Max Cooper','Foreign','484512845','98745612365','coopermaxx@gmail.com'),(8,'Nimal Perera','Local','689745212','0754512365','nimalperera@gmail.com'),(9,'Steve','Foreign','78945612352','456123789','steve@gmail.com');
/*!40000 ALTER TABLE `tbl_guests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_payment_ledger`
--

DROP TABLE IF EXISTS `tbl_payment_ledger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_payment_ledger` (
  `ledger_id` int NOT NULL AUTO_INCREMENT,
  `booking_id` int NOT NULL,
  `payment_amount` decimal(10,2) NOT NULL,
  `payment_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `payment_type` varchar(50) NOT NULL,
  PRIMARY KEY (`ledger_id`),
  KEY `booking_id` (`booking_id`),
  CONSTRAINT `tbl_payment_ledger_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `tbl_bookings` (`booking_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_payment_ledger`
--

LOCK TABLES `tbl_payment_ledger` WRITE;
/*!40000 ALTER TABLE `tbl_payment_ledger` DISABLE KEYS */;
INSERT INTO `tbl_payment_ledger` VALUES (1,4,132000.00,'2026-07-13 13:13:01','Balance Settlement'),(2,5,66000.00,'2026-07-13 13:14:43','Initial Advance'),(3,6,275.00,'2026-07-13 13:44:05','Initial Advance'),(4,7,66000.00,'2026-07-13 14:05:40','Initial Advance'),(5,7,66000.00,'2026-07-13 14:06:24','Balance Settlement'),(6,8,440.00,'2026-07-13 14:54:36','Initial Advance'),(7,8,440.00,'2026-07-13 14:58:23','Balance Settlement');
/*!40000 ALTER TABLE `tbl_payment_ledger` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_users`
--

DROP TABLE IF EXISTS `tbl_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(100) NOT NULL,
  `associated_role` enum('Admin Manager','Reception Operator') NOT NULL,
  `display_name` varchar(100) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_users`
--

LOCK TABLES `tbl_users` WRITE;
/*!40000 ALTER TABLE `tbl_users` DISABLE KEYS */;
INSERT INTO `tbl_users` VALUES (1,'manager','manager123','Admin Manager','Mr. Perera (Director)'),(2,'reception','reception123','Reception Operator','Nethmi (Front Desk Console)');
/*!40000 ALTER TABLE `tbl_users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-13 20:38:19
