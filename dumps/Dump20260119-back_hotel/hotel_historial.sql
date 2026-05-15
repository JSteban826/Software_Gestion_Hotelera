-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: database-hotel.mysql.database.azure.com    Database: hotel
-- ------------------------------------------------------
-- Server version	8.0.42-azure

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
-- Table structure for table `historial`
--

DROP TABLE IF EXISTS `historial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historial` (
  `id_historial` int NOT NULL AUTO_INCREMENT,
  `id_reserva` int DEFAULT NULL,
  `id_cliente` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `id_habitacion` int DEFAULT NULL,
  `fecha_entrada` date DEFAULT NULL,
  `fecha_salida` date DEFAULT NULL,
  `total_precio` decimal(10,2) DEFAULT NULL,
  `estado_reserva` enum('activa','eliminada','modificada','finalizada') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id_historial`),
  KEY `id_cliente` (`id_cliente`),
  KEY `id_habitacion` (`id_habitacion`),
  KEY `historial_ibfk_1` (`id_reserva`),
  CONSTRAINT `historial_ibfk_2` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`Cedula`),
  CONSTRAINT `historial_ibfk_3` FOREIGN KEY (`id_habitacion`) REFERENCES `habitaciones` (`id_habitacion`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historial`
--

LOCK TABLES `historial` WRITE;
/*!40000 ALTER TABLE `historial` DISABLE KEYS */;
INSERT INTO `historial` VALUES (1,NULL,'1070464473',10,'2025-04-20','2025-04-30',6000000.00,'finalizada'),(2,NULL,'1070464473',6,'2025-04-20','2025-04-30',4000000.00,'finalizada'),(3,NULL,'1070589457',8,'2025-04-15','2025-04-20',900000.00,'finalizada'),(4,NULL,'1070464473',8,'2025-04-15','2025-04-20',900000.00,'finalizada'),(5,NULL,'1070589457',5,'2025-04-18','2025-04-22',2400000.00,'finalizada'),(6,NULL,'1070589457',2,'2025-04-18','2025-04-20',1900000.00,'finalizada'),(7,NULL,'1070464473',5,'2025-04-22','2025-04-30',4800000.00,'finalizada'),(8,NULL,'1070464473',5,'2025-04-22','2025-04-28',3600000.00,'finalizada'),(10,NULL,'1070464166',4,'2025-04-28','2025-04-30',900000.00,'finalizada'),(11,1,'1070464473',4,'2025-05-05','2025-05-10',2250000.00,'finalizada'),(12,NULL,'1414',7,'2025-05-06','2025-05-09',750000.00,'activa'),(13,2,'1070464166',12,'2025-05-10','2025-05-11',300000.00,'finalizada'),(14,3,'1070589457',7,'2025-05-10','2025-05-11',250000.00,'finalizada'),(15,3,'1070589457',2,'2025-05-10','2025-05-11',950000.00,'finalizada'),(16,4,'1217438914',3,'2025-05-10','2025-05-11',300000.00,'finalizada'),(18,5,'1528208926',7,'2025-05-10','2025-05-11',250000.00,'finalizada'),(19,6,'1060598042',11,'2025-05-10','2025-05-11',700000.00,'finalizada'),(20,1,'1070464473',2,'2025-05-12','2025-05-13',950000.00,'finalizada'),(21,1,'1070464473',4,'2025-05-22','2025-05-25',1350000.00,'finalizada'),(22,2,'1060598042',2,'2025-05-25','2025-05-30',4750000.00,'finalizada'),(23,3,'1070464473',5,'2025-05-29','2025-06-05',4200000.00,'finalizada'),(24,1,'1070464473',10,'2025-09-16','2025-09-21',3000000.00,'finalizada'),(25,2,'1070589457',1,'2025-09-23','2025-09-25',1600000.00,'finalizada'),(26,1,'1069714447',2,'2025-10-04','2025-10-06',1900000.00,'finalizada'),(27,2,'1069714447',6,'2025-10-16','2025-10-24',3200000.00,'finalizada'),(28,3,'1069714447',9,'2025-10-23','2025-10-25',700000.00,'finalizada'),(29,4,'1070464473',7,'2025-10-05','2025-10-10',1250000.00,'finalizada'),(30,4,'1070464473',7,'2025-10-20','2025-10-30',2500000.00,'finalizada'),(31,5,'1070464473',3,'2025-10-15','2025-10-20',1500000.00,'finalizada'),(32,6,'1070464473',2,'2025-10-16','2025-10-21',4750000.00,'finalizada'),(33,7,'1070464473',4,'2025-10-17','2025-10-22',2250000.00,'finalizada'),(34,8,'1070464473',3,'2025-10-21','2025-10-22',300000.00,'finalizada');
/*!40000 ALTER TABLE `historial` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-19 16:47:13
