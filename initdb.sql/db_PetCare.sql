-- MySQL dump 10.13  Distrib 8.0.13, for Win64 (x86_64)
--
-- Host: localhost    Database: pet_care
-- ------------------------------------------------------
-- Server version	8.0.13

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `addresses`
--

DROP TABLE IF EXISTS `addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `addresses` (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                             `user_id` bigint(20) NOT NULL,
                             `recipient_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `recipient_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `city` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `ward` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `address_detail` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `is_default` tinyint(1) NOT NULL DEFAULT '0',
                             `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                             `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             KEY `user_id` (`user_id`),
                             CONSTRAINT `addresses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
INSERT INTO `addresses` VALUES (1,1,'Phạm Nguyên Vũ','0911000111','Thành phố Hà Nội','Phường Dịch Vọng Hậu','Số 144 Xuân Thủy, Quận Cầu Giấy',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(2,3,'Phạm Văn Tùng','0922000222','Thành phố Đà Nẵng','Phường Hòa Hải','Số 52 Ngũ Hành Sơn',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(3,4,'Đỗ Văn Tuấn','0933000333','Thành phố Hồ Chí Minh','Phường Đa Kao','105/15 đường Nguyễn Đình Chiểu, Quận 1',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(10,15,'Tuan Tien Ty','0985026020','Thanh Pho HN','Ha Dong','111 Mo Lao Ha Dong',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(11,16,'Lê Minh Trí','0912345678','Thành phố Hà Nội','Phường Trung Hòa','Số 18, ngõ 5, đường Trần Duy Hưng',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(12,17,'Trần Ngọc Trang','0912345679','Thành phố Hồ Chí Minh','Phường 10','Chung cư The Art, đường Đỗ Xuân Hợp',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(13,18,'Hoàng Văn Long','0912345680','Thành phố Đà Nẵng','Phường An Hải Bắc','330 Trần Hưng Đạo',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(14,19,'Nguyễn An Nhiên','0912345681','Thành phố Hà Nội','Phường Láng Hạ','55 Thái Hà',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(15,20,'Bùi Thảo Phương','0912345682','Tỉnh Quảng Ninh','Phường Bãi Cháy','Số 22, đường Hạ Long',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(16,21,'Đặng Quốc Hùng','0912345683','Thành phố Hà Nội','Phường Mỹ Đình 1','Tòa nhà Sông Đà',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(17,22,'Phạm Thị Hoa','0987654321','Thành phố Hải Phòng','Phường Lạch Tray','123 Lạch Tray',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(18,23,'Vũ Đức Đam','0987654322','Thành phố Cần Thơ','Phường Ninh Kiều','456 Ninh Kiều',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(19,24,'Lý Hùng Cường','0987654323','Tỉnh Bình Dương','Phường Dĩ An','789 Dĩ An',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(20,25,'Trương Mỹ Lan','0987654324','Tỉnh Đồng Nai','Phường Biên Hòa','321 Biên Hòa',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(21,26,'Đoàn Văn Hậu','0987654325','Tỉnh Thái Bình','Xã Hưng Hà','Xóm 1 Hưng Hà',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(22,27,'Nguyễn Quang Hải','0987654326','Thành phố Hà Nội','Xã Đông Anh','Thôn 3 Đông Anh',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(23,28,'Lương Xuân Trường','0987654327','Tỉnh Tuyên Quang','Phường Minh Xuân','Tổ 5 Minh Xuân',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(24,29,'Nguyễn Công Phượng','0987654328','Tỉnh Nghệ An','Xã Đô Lương','Xóm 4 Đô Lương',1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(25,30,'Park Hang Seo','0987654329','Thành phố Hà Nội','Khu đô thị Ciputra','P101 Ciputra',1,'2025-11-24 17:20:49','2025-11-24 17:20:49');
/*!40000 ALTER TABLE `addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `appointments` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `customer_id` bigint(20) NOT NULL,
                                `pet_id` bigint(20) NOT NULL,
                                `service_id` bigint(20) NOT NULL,
                                `staff_id` bigint(20) DEFAULT NULL,
                                `scheduled_at` datetime NOT NULL,
                                `status` enum('BOOKED','CANCELLED','CHECKED_IN','DONE') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `note` text COLLATE utf8mb4_unicode_ci,
                                `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`),
                                KEY `service_id` (`service_id`),
                                KEY `staff_id` (`staff_id`),
                                KEY `idx_appt_customer_time` (`customer_id`,`scheduled_at`),
                                KEY `idx_appt_pet_time` (`pet_id`,`scheduled_at`),
                                CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                                CONSTRAINT `appointments_ibfk_2` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE,
                                CONSTRAINT `appointments_ibfk_3` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`) ON DELETE RESTRICT,
                                CONSTRAINT `appointments_ibfk_4` FOREIGN KEY (`staff_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (1,16,1,1,15,'2025-11-20 09:00:00','BOOKED','Chó Poodle 3kg, hơi nhát.','2025-11-24 17:20:49','2025-11-24 17:20:49'),(2,17,3,3,19,'2025-11-20 10:00:00','BOOKED','Tỉa lông gọn gàng.','2025-11-24 17:20:49','2025-11-24 17:20:49'),(3,18,4,5,15,'2025-11-20 14:00:00','BOOKED','Mèo Ba Tư lông dài, tắm spa.','2025-11-24 17:20:49','2025-11-24 17:20:49'),(4,20,5,8,3,'2025-11-21 09:00:00','BOOKED','Khám sức khỏe, chó có vẻ biếng ăn.','2025-11-24 17:20:49','2025-11-24 17:20:49'),(5,16,2,2,19,'2025-11-21 11:00:00','BOOKED','Mèo Anh lông ngắn.','2025-11-24 17:20:49','2025-11-24 17:20:49'),(6,15,6,4,15,'2025-11-22 13:00:00','BOOKED','Tỉa lông cho mèo Munchkin.','2025-11-24 17:20:49','2025-11-24 17:20:49'),(7,3,7,10,19,'2025-11-25 08:00:00','BOOKED','Trông giữ 3 ngày (25, 26, 27)','2025-11-24 17:20:49','2025-11-24 17:20:49'),(8,4,8,9,3,'2025-11-23 10:00:00','DONE','Đã tiêm phòng dại (Khách vãng lai)','2025-11-10 03:00:00','2025-11-10 03:15:00'),(9,17,10,6,19,'2025-11-23 15:00:00','CANCELLED','Khách báo bận, dời lịch.','2025-11-24 17:20:49','2025-11-24 17:20:49'),(10,16,1,6,15,'2025-11-24 16:00:00','BOOKED','Vệ sinh tai móng cho Milo.','2025-11-24 17:20:49','2025-11-24 17:20:49'),(11,22,11,11,3,'2025-12-01 09:00:00','BOOKED','Cạo vôi răng','2025-11-24 17:20:49','2025-11-24 17:20:49'),(12,23,12,12,19,'2025-12-01 14:00:00','BOOKED','Tắm trị liệu','2025-11-24 17:20:49','2025-11-24 17:20:49'),(13,24,13,13,15,'2025-12-02 08:00:00','BOOKED','Khách sạn 2 ngày','2025-11-24 17:20:49','2025-11-24 17:20:49'),(14,25,14,14,3,'2025-12-03 17:00:00','BOOKED','Dắt đi dạo','2025-11-24 17:20:49','2025-11-24 17:20:49'),(15,26,15,15,19,'2025-12-04 10:00:00','BOOKED','Huấn luyện','2025-11-24 17:20:49','2025-11-24 17:20:49'),(16,27,16,16,15,'2025-12-05 11:00:00','BOOKED','Massage','2025-11-24 17:20:49','2025-11-24 17:20:49'),(17,28,17,17,3,'2025-12-06 09:00:00','BOOKED','Triệt sản mèo','2025-11-24 17:20:49','2025-11-24 17:20:49'),(18,29,18,18,19,'2025-12-07 14:00:00','BOOKED','Triệt sản mèo cái','2025-11-24 17:20:49','2025-11-24 17:20:49'),(19,30,19,20,15,'2025-12-08 15:00:00','BOOKED','Tư vấn dinh dưỡng','2025-11-24 17:20:49','2025-11-24 17:20:49');
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_logs`
--

DROP TABLE IF EXISTS `audit_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `audit_logs` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `user_id` bigint(20) NOT NULL,
                              `action` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                              `target_table` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `target_id` bigint(20) DEFAULT NULL,
                              `description` text COLLATE utf8mb4_unicode_ci,
                              `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`),
                              KEY `user_id` (`user_id`),
                              CONSTRAINT `audit_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_logs`
--

LOCK TABLES `audit_logs` WRITE;
/*!40000 ALTER TABLE `audit_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `audit_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `cart_items` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `cart_id` bigint(20) NOT NULL,
                              `product_id` bigint(20) NOT NULL,
                              `quantity` int(11) NOT NULL DEFAULT '1',
                              `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                              `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`),
                              KEY `fk_cart_items_cart` (`cart_id`),
                              KEY `fk_cart_items_product` (`product_id`),
                              CONSTRAINT `fk_cart_items_cart` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`) ON DELETE CASCADE,
                              CONSTRAINT `fk_cart_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
INSERT INTO `cart_items` VALUES (1,1,1,2,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(2,1,3,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(3,2,5,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(4,3,2,5,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(5,3,4,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(6,4,6,2,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(7,5,7,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(8,6,8,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(9,6,9,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(10,7,10,3,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(11,8,1,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(12,8,11,2,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(13,9,12,10,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(14,10,15,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(15,1,16,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(16,2,17,3,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(17,3,18,2,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(18,4,19,5,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(19,5,20,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(20,6,13,2,'2025-11-24 17:20:49','2025-11-24 17:20:49');
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carts`
--

DROP TABLE IF EXISTS `carts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `carts` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `user_id` bigint(20) NOT NULL,
                         `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `unique_user_cart` (`user_id`),
                         CONSTRAINT `fk_carts_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carts`
--

LOCK TABLES `carts` WRITE;
/*!40000 ALTER TABLE `carts` DISABLE KEYS */;
INSERT INTO `carts` VALUES (1,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(2,3,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(3,4,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(4,15,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(5,16,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(6,17,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(7,18,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(8,19,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(9,20,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(10,22,'2025-11-24 17:20:49','2025-11-24 17:20:49');
/*!40000 ALTER TABLE `carts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `categories` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (8,'Áo & Phụ kiện'),(11,'Balo & Túi vận chuyển'),(18,'Bỉm & Tã lót'),(6,'Cát vệ sinh & Khay'),(17,'Cỏ mèo & Catnip'),(9,'Dụng cụ Grooming'),(19,'Dụng cụ huấn luyện'),(16,'Đài phun nước cho thú cưng'),(3,'Đồ chơi'),(7,'Lồng & Chuồng'),(15,'Máy cho ăn tự động'),(12,'Nhà & Nệm'),(13,'Pate & Súp thưởng'),(20,'Sách & Tài liệu chăm sóc'),(10,'Sữa tắm & Vệ sinh'),(1,'Thức ăn cho chó'),(2,'Thức ăn cho mèo'),(5,'Thuốc & Vitamin'),(4,'Vòng cổ & Dây dắt'),(14,'Xương gặm sạch răng');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coupons`
--

DROP TABLE IF EXISTS `coupons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `coupons` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT,
                           `code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                           `type` enum('percent','fixed') COLLATE utf8mb4_unicode_ci NOT NULL,
                           `value` decimal(15,2) NOT NULL,
                           `min_order_value` decimal(15,2) DEFAULT '0.00',
                           `starts_at` datetime DEFAULT NULL,
                           `ends_at` datetime DEFAULT NULL,
                           `usage_limit` int(11) DEFAULT NULL,
                           `active` tinyint(1) DEFAULT '1',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coupons`
--

LOCK TABLES `coupons` WRITE;
/*!40000 ALTER TABLE `coupons` DISABLE KEYS */;
INSERT INTO `coupons` VALUES (1,'GIAM50K','fixed',50000.00,300000.00,'2025-01-01 00:00:00','2025-12-31 00:00:00',1000,1),(2,'GIAM100K','fixed',100000.00,500000.00,'2025-01-01 00:00:00','2025-12-31 00:00:00',500,1),(3,'NEWPET10','percent',10.00,200000.00,'2025-01-01 00:00:00','2025-12-31 00:00:00',2000,1),(4,'SPA15','percent',15.00,400000.00,'2025-01-01 00:00:00','2025-12-31 00:00:00',500,1),(5,'BLACKFRIDAY','percent',30.00,1000000.00,'2024-11-20 00:00:00','2024-11-30 00:00:00',100,0),(6,'FIXED20K','fixed',20000.00,150000.00,'2025-01-01 00:00:00','2025-12-31 00:00:00',1000,1),(7,'PETFOOD5','percent',5.00,200000.00,'2025-01-01 00:00:00','2025-12-31 00:00:00',1000,1),(8,'FREESHIP','fixed',30000.00,100000.00,'2025-01-01 00:00:00','2025-12-31 00:00:00',5000,1),(9,'XMAS2024','percent',20.00,500000.00,'2024-12-20 00:00:00','2024-12-25 00:00:00',300,0),(10,'WELCOME','fixed',100000.00,500000.00,'2025-01-01 00:00:00','2025-12-31 00:00:00',1000,1),(11,'TET2025','percent',25.00,1000000.00,'2025-01-20 00:00:00','2025-02-05 00:00:00',500,1),(12,'SUMMER10','percent',10.00,300000.00,'2025-06-01 00:00:00','2025-08-31 00:00:00',1000,1),(13,'CATLOVER','fixed',40000.00,250000.00,'2025-01-01 00:00:00','2025-12-31 00:00:00',1000,1),(14,'DOGLOVER','fixed',40000.00,250000.00,'2025-01-01 00:00:00','2025-12-31 00:00:00',1000,1),(15,'VIPMEMBER','percent',20.00,0.00,'2025-01-01 00:00:00','2030-12-31 00:00:00',100,1),(16,'FLASH2H','percent',50.00,200000.00,'2025-03-03 12:00:00','2025-03-03 14:00:00',50,0),(17,'WOMENDAY','fixed',83000.00,830000.00,'2025-03-08 00:00:00','2025-03-08 00:00:00',830,1),(18,'LOYALTY','fixed',50000.00,0.00,'2025-01-01 00:00:00','2025-12-31 00:00:00',500,1),(19,'SORRY50','percent',50.00,0.00,'2025-01-01 00:00:00','2025-01-31 00:00:00',10,0),(20,'FREESHIP_VIP','fixed',50000.00,0.00,'2025-01-01 00:00:00','2025-12-31 00:00:00',100,1);
/*!40000 ALTER TABLE `coupons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_coupons`
--

DROP TABLE IF EXISTS `order_coupons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `order_coupons` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `order_id` bigint(20) NOT NULL,
                                 `coupon_id` bigint(20) NOT NULL,
                                 `discount_value` decimal(15,2) NOT NULL,
                                 `applied_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 KEY `order_id` (`order_id`),
                                 KEY `coupon_id` (`coupon_id`),
                                 CONSTRAINT `order_coupons_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
                                 CONSTRAINT `order_coupons_ibfk_2` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_coupons`
--

LOCK TABLES `order_coupons` WRITE;
/*!40000 ALTER TABLE `order_coupons` DISABLE KEYS */;
INSERT INTO `order_coupons` VALUES (1,1,2,100000.00,'2025-11-24 17:20:49'),(2,2,3,28500.00,'2025-11-24 17:20:49'),(3,4,8,30000.00,'2025-11-24 17:20:49'),(4,7,1,50000.00,'2025-11-24 17:20:49'),(5,10,7,13500.00,'2025-11-24 17:20:49'),(6,11,6,20000.00,'2025-11-24 17:20:49'),(7,13,4,180000.00,'2025-11-24 17:20:49'),(8,16,1,50000.00,'2025-11-24 17:20:49'),(9,19,3,15000.00,'2025-11-24 17:20:49');
/*!40000 ALTER TABLE `order_coupons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_details`
--

DROP TABLE IF EXISTS `order_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `order_details` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `order_id` bigint(20) NOT NULL,
                                 `item_type` enum('product','service') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `product_id` bigint(20) DEFAULT NULL,
                                 `service_id` bigint(20) DEFAULT NULL,
                                 `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `qty` int(11) NOT NULL DEFAULT '1',
                                 `unit_price` decimal(15,2) NOT NULL,
                                 `discount` decimal(15,2) NOT NULL DEFAULT '0.00',
                                 `line_total` decimal(38,2) DEFAULT NULL,
                                 PRIMARY KEY (`id`),
                                 KEY `product_id` (`product_id`),
                                 KEY `service_id` (`service_id`),
                                 KEY `idx_order_details_order` (`order_id`),
                                 CONSTRAINT `order_details_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
                                 CONSTRAINT `order_details_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT,
                                 CONSTRAINT `order_details_ibfk_3` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_details`
--

LOCK TABLES `order_details` WRITE;
/*!40000 ALTER TABLE `order_details` DISABLE KEYS */;
INSERT INTO `order_details` VALUES (1,1,'product',1,NULL,'Hạt Royal Canin Mini (Chó)',2,450000.00,0.00,NULL),(2,1,'product',5,NULL,'Vitamin Nutri-plus Gel',1,150000.00,0.00,NULL),(3,2,'product',2,NULL,'Hạt Whiskas (Mèo)',1,180000.00,0.00,NULL),(4,2,'product',3,NULL,'Cần câu mèo (lông vũ)',3,35000.00,0.00,NULL),(5,3,'service',NULL,1,'Tắm gội cơ bản (Chó)',1,150000.00,0.00,NULL),(6,4,'product',6,NULL,'Cát vệ sinh America Litter (10L)',2,220000.00,0.00,NULL),(7,5,'product',8,NULL,'Áo len Giáng Sinh (Chó)',1,90000.00,0.00,NULL),(8,6,'product',10,NULL,'Sữa tắm Fay (Hương Phấn)',1,110000.00,0.00,NULL),(9,7,'product',9,NULL,'Lược chải lông Furminator',1,250000.00,0.00,NULL),(10,8,'product',7,NULL,'Lồng vận chuyển (size S)',1,350000.00,0.00,NULL),(11,9,'service',NULL,8,'Khám sức khỏe tổng quát',1,200000.00,0.00,NULL),(12,10,'product',5,NULL,'Vitamin Nutri-plus Gel',1,150000.00,0.00,NULL),(13,10,'service',NULL,9,'Tiêm phòng dại',1,120000.00,0.00,NULL),(14,11,'product',11,NULL,'Hạt SmartHeart',1,300000.00,0.00,NULL),(15,12,'product',12,NULL,'Pate Me-O',2,150000.00,0.00,NULL),(16,13,'product',15,NULL,'Máy cho ăn Xiaomi',1,1200000.00,0.00,NULL),(17,14,'product',16,NULL,'Đài phun nước',1,800000.00,0.00,NULL),(18,15,'product',18,NULL,'Súp thưởng',5,25000.00,0.00,NULL),(19,16,'service',NULL,5,'Spa cao cấp',1,500000.00,0.00,NULL),(20,17,'product',20,NULL,'Balo phi hành gia',1,350000.00,0.00,NULL),(21,18,'service',NULL,11,'Cạo vôi răng',1,500000.00,0.00,NULL),(22,19,'product',13,NULL,'Bóng cao su',2,50000.00,0.00,NULL),(23,19,'service',NULL,6,'Vệ sinh tai móng',1,50000.00,0.00,NULL),(24,20,'product',1,NULL,'Hạt Royal Canin Mini (Chó)',2,450000.00,0.00,NULL),(25,21,'product',2,NULL,'Hạt Whiskas (Mèo)',4,180000.00,0.00,NULL),(26,22,'product',1,NULL,'Hạt Royal Canin Mini (Chó)',2,450000.00,0.00,NULL),(27,22,'product',2,NULL,'Hạt Whiskas (Mèo)',4,180000.00,0.00,NULL);
/*!40000 ALTER TABLE `order_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `orders` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `customer_id` bigint(20) NOT NULL,
                          `channel` enum('POS','WEB') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `status` enum('PENDING','PAID','CANCELLED','REFUNDED','SHIPPING','DELIVERED','COMPLETED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
                          `payment_method` enum('CARD','COD','E_WALLET','TRANSFER') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `shipping_address` text COLLATE utf8mb4_unicode_ci,
                          `note` text COLLATE utf8mb4_unicode_ci,
                          `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                          `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          `total_amount` decimal(15,2) NOT NULL DEFAULT '0.00',
                          PRIMARY KEY (`id`),
                          KEY `idx_orders_customer` (`customer_id`,`created_at`),
                          CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,16,'WEB','PAID','CARD','Số 18, ngõ 5, đường Trần Duy Hưng','Giao giờ hành chính','2025-11-24 17:20:49','2025-11-24 17:20:49',950000.00),(2,17,'WEB','PAID','E_WALLET','Chung cư The Art, đường Đỗ Xuân Hợp',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49',256500.00),(3,18,'POS','PAID','CARD','330 Trần Hưng Đạo','Mua tại quầy','2025-11-24 17:20:49','2025-11-24 17:20:49',150000.00),(4,20,'WEB','PENDING','COD','Số 22, đường Hạ Long','Gọi trước khi giao','2025-11-24 17:20:49','2025-11-24 17:20:49',410000.00),(5,15,'WEB','CANCELLED','TRANSFER','111 Mo Lao Ha Dong','Khách hủy','2025-11-24 17:20:49','2025-11-24 17:20:49',90000.00),(6,3,'POS','PAID','COD','Địa chỉ Staff','Mua tại quầy','2025-11-24 17:20:49','2025-11-24 17:20:49',110000.00),(7,16,'WEB','PAID','CARD','Số 18, ngõ 5, đường Trần Duy Hưng',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49',200000.00),(8,17,'WEB','PENDING','COD','Chung cư The Art, đường Đỗ Xuân Hợp','Giao gấp','2025-11-24 17:20:49','2025-11-24 17:20:49',350000.00),(9,4,'WEB','PAID','TRANSFER','Địa chỉ Tuấn',NULL,'2025-11-24 17:20:49','2025-11-25 09:05:43',200000.00),(10,20,'WEB','PAID','E_WALLET','Số 22, đường Hạ Long',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49',256500.00),(11,22,'WEB','PAID','COD','123 Lạch Tray',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49',280000.00),(12,23,'WEB','PAID','CARD','456 Ninh Kiều',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49',300000.00),(13,24,'WEB','PENDING','TRANSFER','789 Dĩ An','Giao cuối tuần','2025-11-24 17:20:49','2025-11-24 17:20:49',1020000.00),(14,25,'WEB','CANCELLED','COD','321 Biên Hòa','Đổi ý','2025-11-24 17:20:49','2025-11-24 17:20:49',800000.00),(15,26,'POS','PAID','COD','Tại quầy','Mua tại quầy (Sửa từ cash->cod)','2025-11-24 17:20:49','2025-11-24 17:20:49',125000.00),(16,27,'WEB','PAID','E_WALLET','Thôn 3 Đông Anh',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49',450000.00),(17,28,'WEB','PAID','COD','Tổ 5 Minh Xuân',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49',350000.00),(18,29,'WEB','PENDING','CARD','Xóm 4 Đô Lương',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49',500000.00),(19,30,'WEB','PAID','TRANSFER','P101 Ciputra','Giao cho bảo vệ','2025-11-24 17:20:49','2025-11-24 17:20:49',135000.00),(20,4,'WEB','CANCELLED','COD','105/15 đường Nguyễn Đình Chiểu, Quận 1, Phường Đa Kao, Thành phố Hồ Chí Minh','string','2025-11-24 11:00:57','2025-11-25 00:15:36',850000.00),(21,4,'WEB','PENDING','COD','105/15 đường Nguyễn Đình Chiểu, Quận 1, Phường Đa Kao, Thành phố Hồ Chí Minh','string','2025-11-24 11:04:27','2025-11-24 11:04:27',670000.00),(22,4,'WEB','COMPLETED','COD','105/15 đường Nguyễn Đình Chiểu, Quận 1, Phường Đa Kao, Thành phố Hồ Chí Minh','string','2025-11-24 11:08:49','2025-11-25 23:43:22',1570000.00);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `payments` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `order_id` bigint(20) NOT NULL,
                            `amount` decimal(15,2) NOT NULL,
                            `method` enum('cod','cash','card','transfer','e_wallet') COLLATE utf8mb4_unicode_ci NOT NULL,
                            `paid_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                            `note` text COLLATE utf8mb4_unicode_ci,
                            PRIMARY KEY (`id`),
                            KEY `order_id` (`order_id`),
                            CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,1,950000.00,'card','2025-11-24 17:20:49','Thanh toan VCB'),(2,2,256500.00,'e_wallet','2025-11-24 17:20:49','Thanh toan MoMo'),(3,3,150000.00,'cash','2025-11-24 17:20:49','Thanh toan tai quay'),(4,4,410000.00,'cod','2025-11-24 17:20:49','Thu COD'),(5,5,0.00,'transfer','2025-11-24 17:20:49','Huy don'),(6,6,110000.00,'cash','2025-11-24 17:20:49','Thanh toan tai quay'),(7,7,200000.00,'card','2025-11-24 17:20:49','Thanh toan VCB'),(8,8,350000.00,'cod','2025-11-24 17:20:49','Thu COD'),(9,9,200000.00,'transfer','2025-11-24 17:20:49','CK'),(10,10,256500.00,'e_wallet','2025-11-24 17:20:49','Thanh toan ZaloPay'),(11,11,280000.00,'cod','2025-11-24 17:20:49',NULL),(12,12,300000.00,'card','2025-11-24 17:20:49',NULL),(13,13,1020000.00,'transfer','2025-11-24 17:20:49',NULL),(14,14,0.00,'cod',NULL,'Chua thanh toan'),(15,15,125000.00,'cash','2025-11-24 17:20:49',NULL),(16,16,450000.00,'e_wallet','2025-11-24 17:20:49',NULL),(17,17,350000.00,'cod','2025-11-24 17:20:49',NULL),(18,18,500000.00,'card',NULL,'Pending'),(19,19,135000.00,'transfer','2025-11-24 17:20:49',NULL),(20,21,670000.00,'cod',NULL,'string'),(21,9,670000.00,'transfer',NULL,'200000'),(22,9,670000.00,'transfer',NULL,'200000'),(23,9,670000.00,'transfer',NULL,'200000'),(24,9,670000.00,'transfer',NULL,'200000'),(25,9,670000.00,'transfer',NULL,'200000'),(26,9,200000.00,'e_wallet','2025-11-25 16:05:43','VNPay Success. TransRef: 9_1764086480641');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pets`
--

DROP TABLE IF EXISTS `pets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `pets` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `owner_id` bigint(20) NOT NULL,
                        `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                        `species` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                        `breed` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                        `color` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                        `sex` enum('male','female','unknown') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                        `birth_date` date DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        KEY `idx_pets_owner` (`owner_id`),
                        CONSTRAINT `pets_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pets`
--

LOCK TABLES `pets` WRITE;
/*!40000 ALTER TABLE `pets` DISABLE KEYS */;
INSERT INTO `pets` VALUES (1,16,'Milo','Chó','Poodle','Nâu đỏ','male','2023-01-15'),(2,16,'Kiki','Mèo','Anh lông ngắn','Xám xanh','female','2022-05-20'),(3,17,'Vàng','Chó','Corgi','Vàng trắng','male','2023-03-10'),(4,18,'Bông','Mèo','Ba Tư','Trắng','female','2021-11-01'),(5,20,'Đen','Chó','Phú Quốc','Đen tuyền','male','2022-08-30'),(6,15,'Lulu','Mèo','Munchkin','Vàng','female','2023-07-07'),(7,3,'Rex','Chó','Husky','Trắng đen','male','2022-02-14'),(8,4,'Luna','Mèo','Mèo ta','Vằn vện','female','2020-01-01'),(9,16,'Cà Phê','Chó','Chihuahua','Vàng kem','male','2023-10-02'),(10,17,'Miu','Mèo','Mèo ta','Tam thể','female','2021-06-12'),(11,22,'Lu','Chó','Becgie','Đen nâu','male','2020-05-05'),(12,23,'Misa','Mèo','Xiêm','Trắng kem','female','2022-09-09'),(13,24,'Bull','Chó','Bulldog','Trắng','male','2021-12-12'),(14,25,'Nana','Mèo','Scottish Fold','Xám','female','2023-02-02'),(15,26,'Tèo','Chó','Cỏ','Vàng','male','2019-01-01'),(16,27,'Sóc','Chó','Pomeranian','Trắng','male','2023-05-05'),(17,28,'Nhím','Mèo','Sphynx','Hồng','female','2022-08-08'),(18,29,'Gấu','Chó','Alaska','Xám trắng','male','2021-03-03'),(19,30,'Kimchi','Chó','Jindo','Trắng','female','2020-10-10');
/*!40000 ALTER TABLE `pets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_images`
--

DROP TABLE IF EXISTS `product_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `product_images` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                  `product_id` bigint(20) NOT NULL,
                                  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
                                  PRIMARY KEY (`id`),
                                  KEY `product_id` (`product_id`),
                                  CONSTRAINT `product_images_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_images`
--

LOCK TABLES `product_images` WRITE;
/*!40000 ALTER TABLE `product_images` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_reviews`
--

DROP TABLE IF EXISTS `product_reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `product_reviews` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                   `product_id` bigint(20) NOT NULL,
                                   `user_id` bigint(20) NOT NULL,
                                   `rating` tinyint(4) DEFAULT NULL,
                                   `comment` text COLLATE utf8mb4_unicode_ci,
                                   `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                   `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   `parent_id` bigint(20) DEFAULT NULL,
                                   PRIMARY KEY (`id`),
                                   KEY `user_id` (`user_id`),
                                   KEY `fk_review_parent` (`parent_id`),
                                   KEY `idx_product_id` (`product_id`),
                                   CONSTRAINT `fk_review_parent` FOREIGN KEY (`parent_id`) REFERENCES `product_reviews` (`id`) ON DELETE CASCADE,
                                   CONSTRAINT `fk_reviews_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
                                   CONSTRAINT `product_reviews_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_reviews`
--

LOCK TABLES `product_reviews` WRITE;
/*!40000 ALTER TABLE `product_reviews` DISABLE KEYS */;
INSERT INTO `product_reviews` VALUES (1,1,16,5,'Hạt tốt, chó nhà mình rất thích ăn!','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(2,2,17,4,'Mèo ăn ổn, mùi hơi nồng tí.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(3,1,18,5,'Giao hàng nhanh, đóng gói cẩn thận. 5 sao.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(4,3,17,5,'Cần câu chắc chắn, mèo nhà mình chơi cả ngày.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(5,5,20,4,'Gel dinh dưỡng hiệu quả, bé kén ăn cũng chịu.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(6,6,17,3,'Cát vón tốt nhưng khử mùi không tốt lắm.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(7,10,16,5,'Sữa tắm thơm, giữ mùi lâu.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(8,9,15,5,'Lược chải ra rất nhiều lông rụng, nên mua.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(9,7,18,4,'Lồng chắc chắn, nhưng hơi nặng.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(10,4,16,5,'Vòng cổ da đẹp, mềm.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(11,11,22,4,'Hạt hơi cứng, ngâm nước thì ok.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(12,12,23,5,'Mèo nhà mình nghiện món này.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(13,13,24,3,'Bóng hơi nhỏ so với chó lớn.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(14,14,25,5,'Nệm êm, ấm áp.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(15,15,26,5,'Máy xịn, app dễ dùng.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(16,16,27,4,'Đài phun nước đẹp nhưng lọc hơi đắt.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(17,17,28,5,'Cỏ tươi xanh.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(18,18,29,5,'Súp thưởng ngon.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(19,19,30,4,'Xương thơm.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(20,20,16,5,'Balo đẹp, thoáng khí.','2025-11-24 17:20:49','2025-11-24 17:20:49',NULL),(21,1,4,4,'Tạm Ổn','2025-11-25 23:47:14','2025-11-25 23:47:14',NULL),(23,1,4,NULL,'Trên cả tuyệt vời','2025-11-26 00:01:06','2025-11-26 00:01:06',21),(24,1,1,NULL,'Cảm ơn thượng dế','2025-11-26 00:06:58','2025-11-26 00:06:58',23);
/*!40000 ALTER TABLE `product_reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `products` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `category_id` bigint(20) DEFAULT NULL,
                            `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `description` text COLLATE utf8mb4_unicode_ci,
                            `price` decimal(15,2) NOT NULL DEFAULT '0.00',
                            `stock` int(11) NOT NULL DEFAULT '0',
                            `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `average_rating` double DEFAULT '0',
                            `review_count` int(11) DEFAULT '0',
                            PRIMARY KEY (`id`),
                            KEY `idx_products_category` (`category_id`),
                            CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,1,'Hạt Royal Canin Mini (Chó)','Thức ăn hạt cho chó giống nhỏ (2kg).',450000.00,98,'2025-11-24 17:20:49','2025-11-25 00:15:36',0,0),(2,2,'Hạt Whiskas (Mèo)','Thức ăn hạt cho mèo trưởng thành, vị cá biển (1.2kg).',180000.00,142,'2025-11-24 17:20:49','2025-11-24 11:08:49',0,0),(3,3,'Cần câu mèo (lông vũ)','Đồ chơi cần câu gắn lông vũ.',35000.00,300,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(4,4,'Vòng cổ da (size M)','Vòng cổ bằng da thật, màu nâu.',120000.00,80,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(5,5,'Vitamin Nutri-plus Gel','Gel dinh dưỡng bổ sung vitamin.',150000.00,200,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(6,6,'Cát vệ sinh America Litter (10L)','Cát đậu nành hương trà xanh.',220000.00,100,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(7,7,'Lồng vận chuyển (size S)','Lồng nhựa cứng, cửa sắt, dùng cho máy bay.',350000.00,50,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(8,8,'Áo len Giáng Sinh (Chó)','Áo len họa tiết tuần lộc, size L.',90000.00,70,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(9,9,'Lược chải lông Furminator','Lược chuyên dụng gỡ lông rối, giảm rụng.',250000.00,60,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(10,10,'Sữa tắm Fay (Hương Phấn)','Sữa tắm khử mùi, lưu hương phấn em bé.',110000.00,120,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(11,1,'Hạt SmartHeart (Chó)','Thức ăn hạt vị bò nướng (3kg).',300000.00,80,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(12,2,'Pate Me-O (Mèo)','Pate cá ngừ (85g x 12 gói).',150000.00,200,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(13,3,'Bóng cao su (Chó)','Đồ chơi bóng nảy siêu bền.',50000.00,150,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(14,12,'Nệm tròn lông nhung','Nệm êm ái cho chó mèo dưới 5kg.',250000.00,40,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(15,15,'Máy cho ăn tự động Xiaomi','Điều khiển qua App, dung tích 3L.',1200000.00,20,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(16,16,'Đài phun nước Petkit','Lọc nước tự động, chạy êm.',800000.00,30,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(17,17,'Cỏ mèo tươi (Combo 3 chậu)','Giúp tiêu búi lông.',45000.00,50,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(18,13,'Súp thưởng Ciao Churu','Gói 4 thanh, vị gà cá.',25000.00,500,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(19,14,'Xương gặm Orgo','Làm sạch răng, thơm miệng.',60000.00,100,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0),(20,11,'Balo phi hành gia','Balo vận chuyển mèo, mặt kính trong suốt.',350000.00,60,'2025-11-24 17:20:49','2025-11-24 17:20:49',0,0);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `roles` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `name` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (3,'ADMIN'),(1,'CUSTOMER'),(2,'STAFF');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services`
--

DROP TABLE IF EXISTS `services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `services` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `description` text COLLATE utf8mb4_unicode_ci,
                            `image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `price` decimal(15,2) NOT NULL DEFAULT '0.00',
                            `duration_min` int(11) DEFAULT NULL,
                            `active` tinyint(1) DEFAULT '1',
                            `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            KEY `idx_services_active` (`active`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services`
--

LOCK TABLES `services` WRITE;
/*!40000 ALTER TABLE `services` DISABLE KEYS */;
INSERT INTO `services` VALUES (1,'Tắm gội cơ bản (Chó)','Tắm sạch...',NULL,150000.00,60,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(2,'Tắm gội cơ bản (Mèo)','Tắm sạch...',NULL,180000.00,60,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(3,'Cắt tỉa lông (Chó)','Tạo kiểu...',NULL,300000.00,90,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(4,'Cắt tỉa lông (Mèo)','Cạo lông...',NULL,350000.00,90,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(5,'Gói Spa cao cấp (Chó/Mèo)','Tắm thảo dược...',NULL,500000.00,120,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(6,'Vệ sinh tai, móng','Gói vệ sinh...',NULL,50000.00,20,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(7,'Nhuộm lông (An toàn)','Nhuộm tai...',NULL,400000.00,120,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(8,'Khám sức khỏe','Khám lâm sàng...',NULL,200000.00,45,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(9,'Tiêm phòng dại','Tiêm vắc-xin...',NULL,120000.00,15,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(10,'Trông giữ thú cưng','Theo ngày...',NULL,250000.00,1440,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(11,'Cạo vôi răng','Làm sạch...',NULL,500000.00,45,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(12,'Tắm trị liệu da','Trị nấm...',NULL,250000.00,75,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(13,'Khách sạn thú cưng','Phòng riêng...',NULL,500000.00,1440,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(14,'Dắt chó đi dạo','1 tiếng...',NULL,100000.00,60,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(15,'Huấn luyện cơ bản','Dạy lệnh...',NULL,300000.00,60,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(16,'Massage thư giãn','Massage...',NULL,150000.00,30,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(17,'Triệt sản (Mèo đực)','Phẫu thuật...',NULL,800000.00,120,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(18,'Triệt sản (Mèo cái)','Phẫu thuật...',NULL,1200000.00,120,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(19,'Cấp cứu 24/7','Ngoài giờ...',NULL,500000.00,60,1,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(20,'Tư vấn dinh dưỡng','Thực đơn...',NULL,100000.00,30,1,'2025-11-24 17:20:49','2025-11-24 17:20:49');
/*!40000 ALTER TABLE `services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token`
--

DROP TABLE IF EXISTS `token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `token` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `username` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `access_token` text COLLATE utf8mb4_unicode_ci,
                         `refresh_token` text COLLATE utf8mb4_unicode_ci,
                         `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `device_token` text COLLATE utf8mb4_unicode_ci,
                         `platform` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `version_app` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token`
--

LOCK TABLES `token` WRITE;
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
INSERT INTO `token` VALUES (1,'tuan','eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjpbeyJhdXRob3JpdHkiOiJST0xFX0NVU1RPTUVSIn1dLCJ1c2VySWQiOjQsInN1YiI6InR1YW4iLCJpYXQiOjE3NjQxNDA1MDAsImV4cCI6MTc2NDE0NDEwMH0.EdtyWPO_85QSBOnQo4SZTnFkt0CWtru9td4CqF2uipY','eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjpbeyJhdXRob3JpdHkiOiJST0xFX0NVU1RPTUVSIn1dLCJ1c2VySWQiOjQsInN1YiI6InR1YW4iLCJpYXQiOjE3NjQxNDA1MDAsImV4cCI6MTc2NTM1MDEwMH0.8TcuY4PyS0ruMd2j0o39TKXixS57tbCM5P1ldE7uT6k','2025-11-24 10:24:42','2025-11-26 00:01:40','x-token','WEB',NULL),(2,'admin_vu','eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjpbeyJhdXRob3JpdHkiOiJST0xFX0FETUlOIn1dLCJ1c2VySWQiOjEsInN1YiI6ImFkbWluX3Z1IiwiaWF0IjoxNzY0MjIyMzg5LCJleHAiOjE3NjQyMjU5ODl9.nqi800Idb-oMZPdPl7sXZIuvvxpZynw50u4Afy6rMrc','eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjpbeyJhdXRob3JpdHkiOiJST0xFX0FETUlOIn1dLCJ1c2VySWQiOjEsInN1YiI6ImFkbWluX3Z1IiwiaWF0IjoxNzY0MjIyMzg5LCJleHAiOjE3NjU0MzE5ODl9.5H6gmSS9r7LOMgwhvFxC9m7L7JJ3FdHzpsobdXGS1Iw','2025-11-25 23:34:20','2025-11-26 22:46:30','x-token','WEB',NULL);
/*!40000 ALTER TABLE `token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `user_roles` (
                              `user_id` bigint(20) NOT NULL,
                              `role_id` bigint(20) NOT NULL,
                              PRIMARY KEY (`user_id`,`role_id`),
                              KEY `role_id` (`role_id`),
                              CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                              CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (4,1),(15,1),(16,1),(17,1),(18,1),(20,1),(22,1),(23,1),(24,1),(25,1),(26,1),(27,1),(28,1),(29,1),(30,1),(3,2),(19,2),(1,3),(21,3);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
SET character_set_client = utf8mb4 ;
CREATE TABLE `users` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `first_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `last_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `phone` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `avatar_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `username` (`username`),
                         UNIQUE KEY `phone` (`phone`),
                         UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Phạm Nguyên','Vũ','admin_vu','ACTIVE','admin@petshop.com','0911000111','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(3,'Phạm Văn','Tùng','staff_tung','ACTIVE','tung.staff@petshop.com','0922000222','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(4,'Đỗ Văn','Tuấn','tuan','ACTIVE','tuan@gmail.com','0933000333','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(15,'Tuan Tu Do','Do','tuam123','ACTIVE','longvuong01cs@gmail.com','0123456788','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(16,'Lê Minh','Trí','leminhtri','ACTIVE','tri.le@example.com','0912345678','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(17,'Trần Ngọc','Trang','trangtran','ACTIVE','trang.tran@example.com','0912345679','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(18,'Hoàng Văn','Long','hoanglong','ACTIVE','long.hoang@example.com','0912345680','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(19,'Nguyễn An','Nhiên','annhien','ACTIVE','nhien.an@petshop.com','0912345681','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(20,'Bùi Thảo','Phương','thaophuong','ACTIVE','phuong.bui@example.com','0912345682','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(21,'Đặng Quốc','Hùng','danghung_admin','ACTIVE','hung.dang@petshop.com','0912345683','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(22,'Phạm Thị','Hoa','phamhoa','ACTIVE','hoa.pham@example.com','0987654321','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(23,'Vũ Đức','Đam','vudam','ACTIVE','dam.vu@example.com','0987654322','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(24,'Lý Hùng','Cường','lycuong','ACTIVE','cuong.ly@example.com','0987654323','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(25,'Trương Mỹ','Lan','mylan','ACTIVE','lan.truong@example.com','0987654324','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(26,'Đoàn Văn','Hậu','vanhau','ACTIVE','hau.doan@example.com','0987654325','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(27,'Nguyễn Quang','Hải','quanghai','ACTIVE','hai.nguyen@example.com','0987654326','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(28,'Lương Xuân','Trường','xuantruong','ACTIVE','truong.luong@example.com','0987654327','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(29,'Nguyễn Công','Phượng','congphuong','ACTIVE','phuong.nguyen@example.com','0987654328','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49'),(30,'Park','Hang Seo','parkhangseo','ACTIVE','seo.park@example.com','0987654329','$2a$10$/RUbuT9KIqk6f8enaTQiLOXzhnUkiwEJRdtzdrMXXwU7dgnLKTCYG',NULL,'2025-11-24 17:20:49','2025-11-24 17:20:49');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-27 12:59:40
