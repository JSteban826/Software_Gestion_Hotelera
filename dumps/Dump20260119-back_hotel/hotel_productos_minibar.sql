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
-- Table structure for table `productos_minibar`
--

DROP TABLE IF EXISTS `productos_minibar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos_minibar` (
  `id_producto` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,
  `nombre_producto` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `valor_unitario` decimal(10,2) NOT NULL,
  `cantidad` int NOT NULL,
  PRIMARY KEY (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos_minibar`
--

LOCK TABLES `productos_minibar` WRITE;
/*!40000 ALTER TABLE `productos_minibar` DISABLE KEYS */;
INSERT INTO `productos_minibar` VALUES ('N001','Agua mineral 500ml',4500.00,50),('N002','Gaseosa Coca-Cola 350ml',5500.00,50),('N003','Gaseosa Sprite 350ml',5500.00,50),('N004','Jugo Hit 250ml',4800.00,45),('N005','Cerveza Club Colombia 330ml',6500.00,50),('N006','Cerveza Corona 355ml',7500.00,50),('N007','Whisky miniatura (50ml)',18000.00,50),('N008','Ron Medellín miniatura (50ml)',15000.00,44),('N009','Papas Margarita 30g',3500.00,50),('N010','Maní salado 50g',3200.00,50),('N011','Galletas Festival',2800.00,50),('N012','Chocolatina Jet',2500.00,50),('N013','Barra de cereal',4200.00,50),('N014','Snickers mini',3800.00,50),('N015','Café instantáneo (sobre)',3000.00,50),('N016','Té aromática (sobre)',2800.00,50),('N017','Agua con gas 500ml',3800.00,50),('N018','Refresco en polvo (sobre)',1500.00,50),('N019','Vino tinto mini (187ml)',16000.00,50),('N020','Botella de agua grande 1L',5500.00,50);
/*!40000 ALTER TABLE `productos_minibar` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-19 16:47:17
