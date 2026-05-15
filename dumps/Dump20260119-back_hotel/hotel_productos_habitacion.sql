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
-- Table structure for table `productos_habitacion`
--

DROP TABLE IF EXISTS `productos_habitacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos_habitacion` (
  `id_producto` varchar(10) NOT NULL,
  `nombre_producto` varchar(100) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `valor_reposicion` decimal(10,2) NOT NULL,
  `estado` enum('Disponible','No disponible','En reparación') DEFAULT 'Disponible',
  `cantidad` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos_habitacion`
--

LOCK TABLES `productos_habitacion` WRITE;
/*!40000 ALTER TABLE `productos_habitacion` DISABLE KEYS */;
INSERT INTO `productos_habitacion` VALUES ('PH001','Toalla de baño','Toalla blanca grande de algodón',25000.00,'Disponible',50),('PH002','Toalla de manos','Toalla mediana blanca para manos',15000.00,'Disponible',50),('PH003','Sábana superior','Sábana blanca de 400 hilos',40000.00,'Disponible',50),('PH004','Sábana inferior','Sábana ajustable para colchón',35000.00,'Disponible',50),('PH005','Funda de almohada','Funda blanca de algodón',12000.00,'Disponible',50),('PH006','Cobija','Cobija térmica ligera',55000.00,'Disponible',50),('PH007','Almohada','Almohada hipoalergénica estándar',35000.00,'Disponible',50),('PH008','Cojín decorativo','Cojín pequeño decorativo de cama',18000.00,'Disponible',50),('PH009','Cortina de baño','Cortina plástica resistente al agua',30000.00,'Disponible',50),('PH010','Tapete de baño','Tapete antideslizante absorbente',20000.00,'Disponible',50),('PH011','Jabón de tocador','Jabón neutro de 50g',3000.00,'Disponible',50),('PH012','Champú','Botella de champú 30ml',5000.00,'Disponible',50),('PH013','Acondicionador','Botella de acondicionador 30ml',5000.00,'Disponible',50),('PH014','Papel higiénico','Rollo doble hoja 250m',6000.00,'Disponible',50),('PH015','Vaso de vidrio','Vaso transparente 250ml',8000.00,'Disponible',50),('PH016','Control remoto','Control remoto universal para TV',25000.00,'Disponible',50),('PH017','Botella de agua cortesía','Botella 500ml agua natural',3000.00,'Disponible',50),('PH018','Secador de cabello','Secador de 1800W con cable retráctil',90000.00,'Disponible',50),('PH019','Perchero','Gancho de madera para ropa',10000.00,'Disponible',50),('PH020','Alfombra de habitación','Pequeña alfombra decorativa',45000.00,'Disponible',50);
/*!40000 ALTER TABLE `productos_habitacion` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-19 16:47:45
