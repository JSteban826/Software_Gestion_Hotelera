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
-- Table structure for table `consumo_minibar`
--

DROP TABLE IF EXISTS `consumo_minibar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consumo_minibar` (
  `id_consumo` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,
  `id_cliente` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,
  `id_check` int NOT NULL,
  `total_cuenta` decimal(10,2) NOT NULL DEFAULT '0.00',
  `fecha_consumo` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_consumo`),
  KEY `id_cliente` (`id_cliente`),
  KEY `id_check` (`id_check`),
  CONSTRAINT `consumo_minibar_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`Cedula`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `consumo_minibar_ibfk_2` FOREIGN KEY (`id_check`) REFERENCES `check_in` (`id_check_in`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consumo_minibar`
--

LOCK TABLES `consumo_minibar` WRITE;
/*!40000 ALTER TABLE `consumo_minibar` DISABLE KEYS */;
INSERT INTO `consumo_minibar` VALUES ('C0001','1070464473',1,114000.00,'2025-10-21 04:19:53');
/*!40000 ALTER TABLE `consumo_minibar` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-19 16:47:25
