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
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `usuario` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `contraseña` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `rol_id` int DEFAULT NULL,
  `fecha_registro` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `intentos_fallidos` int DEFAULT '0',
  `bloqueado_hasta` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`usuario`),
  KEY `fk_rol` (`rol_id`),
  CONSTRAINT `fk_rol` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES ('adminHotel','admin1234',1,'2025-05-10 19:32:33',0,NULL),('antonio','boupojp',2,'2025-05-06 22:57:04',0,NULL),('daniel','ebojfm',2,'2025-05-06 22:57:04',0,NULL),('dayana','ebzbob',2,'2025-05-06 22:57:04',0,NULL),('dayis','ebzjt',2,'2025-09-28 22:45:01',0,NULL),('esteban','ftufcbo',2,'2025-05-06 22:57:04',0,NULL),('hola','ipmb',2,'2025-10-03 00:46:00',0,NULL),('jose','kptf',2,'2025-05-06 22:57:04',0,NULL),('jrambo','ksbncp',2,'2025-05-06 22:57:04',0,NULL),('juan','kvbo',2,'2025-05-06 22:57:04',0,NULL),('nikolay','ojlpmbz',2,'2025-05-29 14:30:22',0,NULL),('pablo','qbcmp',2,'2025-05-06 22:57:04',0,NULL),('paola','qbpmb',2,'2025-05-06 22:57:04',0,NULL),('paula','qbvmb',2,'2025-05-06 22:57:04',0,NULL),('pilar','qjmbs',2,'2025-05-07 00:43:55',0,NULL),('samuel','tbnvfm',2,'2025-05-06 22:57:04',0,NULL),('sandra','tboesb',2,'2025-05-06 22:57:04',0,NULL),('testUser','12345',2,'2025-10-03 00:43:43',0,NULL),('testUser_1759452617364','12345',2,'2025-10-03 00:50:22',0,NULL),('testUser_1759508000899','12345',2,'2025-10-03 16:13:24',0,NULL),('testUser_1759508144169','12345',2,'2025-10-03 16:15:46',0,NULL),('testUser_1759508174044','12345',2,'2025-10-03 16:16:15',0,NULL),('testUser_1759508240320','12345',2,'2025-10-03 16:17:22',0,NULL);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-19 16:47:32
