-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: database-hotel.mysql.database.azure.com    Database: hotel
-- ------------------------------------------------------
-- Server version	8.0.42-azure

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `acompanantes`
--

DROP TABLE IF EXISTS `acompanantes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `acompanantes` (
  `id_acompanante` int NOT NULL AUTO_INCREMENT,
  `Documento` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `id_check_in` int DEFAULT NULL,
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `apellido` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `telefono` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `parentesco` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `edad` int NOT NULL,
  PRIMARY KEY (`id_acompanante`),
  UNIQUE KEY `Documento` (`Documento`,`id_check_in`),
  KEY `id_check_in` (`id_check_in`),
  CONSTRAINT `acompanantes_ibfk_1` FOREIGN KEY (`id_check_in`) REFERENCES `check_in` (`id_check_in`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acompanantes`
--

LOCK TABLES `acompanantes` WRITE;
/*!40000 ALTER TABLE `acompanantes` DISABLE KEYS */;
INSERT INTO `acompanantes` VALUES (1,'1223455678',1,'alex','rodriguez','1234567810','hermano',35),(2,'5254565879',1,'jorge','rodriguez','1234567810','hermano',22),(5,'1213141516',1,'esteban','parra','2233445566','hermano',35),(6,'1213141517',1,'esteban','parra','2233445566','hermano',35),(7,'1235566',2,'juan','gonzales','3323','padre',81),(8,'12345',1,'Carlos','Pérez','3015559999','Hermano',25);
/*!40000 ALTER TABLE `acompanantes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `check_in`
--

DROP TABLE IF EXISTS `check_in`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `check_in` (
  `id_check_in` int NOT NULL AUTO_INCREMENT,
  `id_cliente` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `id_reserva` int DEFAULT NULL,
  `llave` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `habitacion` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `dias_estancia` int DEFAULT NULL,
  `numero_acompanantes` int DEFAULT NULL,
  `servicios_adicionales` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`id_check_in`),
  KEY `fk_checkin_clientes` (`id_cliente`),
  CONSTRAINT `fk_checkin_clientes` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`Cedula`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `check_in`
--

LOCK TABLES `check_in` WRITE;
/*!40000 ALTER TABLE `check_in` DISABLE KEYS */;
INSERT INTO `check_in` VALUES (1,'1070464473',4,'1001','Isla',5,1,'cama doble mas toalla adicional'),(2,'1069714447',1,'1002','Brisa del Mar',2,1,'cama extra');
/*!40000 ALTER TABLE `check_in` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `check_out`
--

DROP TABLE IF EXISTS `check_out`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `check_out` (
  `id_check_out` int NOT NULL AUTO_INCREMENT,
  `id_cliente` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `correo` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `servicios_adicionales` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `valor_total` decimal(10,2) DEFAULT NULL,
  `Dias_Estancia` int DEFAULT NULL,
  PRIMARY KEY (`id_check_out`),
  KEY `fk_cliente_checkin` (`id_cliente`),
  CONSTRAINT `fk_cliente_checkin` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`Cedula`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1070464474 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `check_out`
--

LOCK TABLES `check_out` WRITE;
/*!40000 ALTER TABLE `check_out` DISABLE KEYS */;
/*!40000 ALTER TABLE `check_out` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `Cedula` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `apellido` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `correo_electronico` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `telefono` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`Cedula`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES ('','Maria','Lopez','maria@test.com','3009876543'),('1001','Pau','Acosta','pau@test.com','3001234567'),('1060598042','Carena','Gidman','cgidman17@163.com','461 160 5815'),('1069714447','Paula','Mora','dayismora1804@gmail.com','3207183439'),('1070464166','luisa','rojas','luisarojas@gmail.com','3026111140'),('1070464473','Juan Esteban','Parra','juanrodriguez2608@gmail.com','3127301706'),('1070589457','Edward','Villalba','parradosamuel35@gmail.com','3219428168'),('1098','Pau','Acosta','pau@test.com','3001234567'),('1099','Pau','Acosta','pau@test.com','3001234567'),('1217438914','Francisco','Gaddas','fgaddas2j@disqus.com','665 609 3211'),('12314','pedro','perez','pedro@gmail.com','335734'),('123456','Pau','Acosta','pau@test.com','3001234567'),('1264632175','Egor','Baine','ebaine1w@live.com','764 493 9495'),('1303865166','Breena','Folland','bfollands@dion.ne.jp','259 886 2210'),('1414','lola','perez','gemarofe@hotmail.com','3184241322'),('1527880463','Temp','Peregrine','tperegrinex@liveinternet.ru','158 813 0931'),('1528208926','Almire','Bilbie','abilbie0@jiathis.com','523 809 8296'),('1913488922','Paddy','Jeannequin','pjeannequin13@nba.com','403 289 6303'),('199','Pau','Acosta','pau@test.com','3001234567'),('2001','Juan','Perez','juan@test.com','3112233445'),('2021','Juan','Perez','juan@test.com','3112233445'),('2030504046','Diana','Traske','dtraske27@washington.edu','214 316 9692'),('2031','Juan','Perez','juan@test.com','3112233445'),('2057311506','Sandra','Carlett','scarlettb@irs.gov','679 245 7777'),('2083','luis','garcia','dayis@gmail.com','321'),('2157333169','Sol','Thody','sthody2c@biblegateway.com','799 292 7616'),('2233','Pau','Acosta','pau@test.com','3001234567'),('2234','Pau','Acosta','pau@test.com','3001234567'),('2304238048','Fernando','Quakley','fquakley1e@shutterfly.com','711 265 3974'),('23233','Pau','Acosta','pau@test.com','3001234567'),('23333333','Pau','Acosta','pau@test.com','3001234567'),('2458222595','Ania','Batts','abatts1a@cnbc.com','206 149 7222'),('2614288010','Blakelee','Olifard','bolifard1p@answers.com','298 392 0308'),('2738516945','Bary','Windsor','bwindsor12@buzzfeed.com','869 908 7059'),('2996491065','Giovanni','Chewter','gchewter11@examiner.com','353 941 3236'),('3001','','Martinez','martinez@test.com','3125556677'),('3076830229','Melisenda','Sirett','msirett20@ucla.edu','598 623 1127'),('3082295844','Helen','Ainsbury','hainsbury2o@craigslist.org','394 265 4585'),('3109805493','Cosetta','Pett','cpettw@virginia.edu','584 178 3697'),('3330407497','Jessy','Stollwerk','jstollwerk26@yale.edu','620 501 6175'),('340841205','Fianna','Gomm','fgommz@sogou.com','806 197 2670'),('34254','Pau','Acosta','pau@test.com','3001234567'),('3426545992','Yvor','Ornillos','yornillos2d@mashable.com','857 776 0667'),('3488826445','Maryanne','Dupey','mdupey1r@prnewswire.com','971 194 6749'),('3528007488','Leonardo','Banks','lbanksl@livejournal.com','531 540 1693'),('3565034168','Abey','Ebbings','aebbings25@feedburner.com','615 443 8894'),('3646129490','Harwilll','Nosworthy','hnosworthyd@istockphoto.com','834 782 3232'),('377143265','Wini','Origin','worigine@cnbc.com','574 623 2360'),('3866548509','Jakob','Jorcke','jjorckei@dyndns.org','618 864 2480'),('3953862625','Colas','O\'Rafferty','corafferty10@prlog.org','290 560 1678'),('3966722706','Care','Kopke','ckopkey@unesco.org','339 955 2048'),('3988728484','Robina','Needham','rneedham1u@nba.com','729 413 4091'),('4001','Carlos','Gomez','correo_invalido','3204455667'),('4066376687','Sterling','Heppner','sheppner1v@sfgate.com','490 348 8107'),('4135716956','Albina','Trustrie','atrustrie1q@wufoo.com','677 438 8367'),('4270688912','Libbie','Mullin','lmullin2@dion.ne.jp','301 402 6891'),('4422445537','Cecil','Kalisch','ckalisch1d@kickstarter.com','431 530 7597'),('4509390905','Suzanna','O\'Riordan','soriordan1j@a8.net','580 578 6426'),('4514027534','Mellisent','Bernon','mbernon2p@chicagotribune.com','700 337 7458'),('4692297950','Maddie','Pridie','mpridie2b@bloomberg.com','577 948 4792'),('4696078988','Zabrina','Tomicki','ztomicki2e@china.com.cn','215 405 6576'),('4798152365','Mariellen','Beton','mbetong@chicagotribune.com','291 254 7273'),('4929164832','Clay','Ashbridge','cashbridgea@bbc.co.uk','806 870 7223'),('5021951600','Evonne','Blune','eblune1f@dedecms.com','377 382 8902'),('5059537056','Sharline','Lanahan','slanahan1k@ucla.edu','550 956 5063'),('5336184382','Illa','Reeks','ireeks1@google.com.hk','373 698 1723'),('5458020169','Terri','Hanmore','thanmorec@merriam-webster.com','692 276 9667'),('5600172343','Valentino','Plumridege','vplumridege28@ezinearticles.co','600 328 6792'),('5676590450','Coral','Christy','cchristy2f@networkadvertising.','822 244 3950'),('5705978511','Harry','MacCoughen','hmaccoughen5@sphinn.com','747 388 3918'),('5857815261','Starla','Mitchenson','smitchenson2q@plala.or.jp','468 800 6909'),('5865315273','Jacquetta','Barfoot','jbarfoot1t@adobe.com','871 515 7326'),('5931204054','Paulie','Arnoldi','parnoldi1z@omniture.com','349 624 3461'),('6031971044','Domenico','Fidal','dfidalk@g.co','259 962 2312'),('6137854859','Elyn','Vasyanin','evasyanin1s@wiley.com','337 817 3737'),('6252293545','Modestine','Greville','mgreville1l@tripod.com','940 478 2893'),('6261041980','Floyd','Cullinan','fcullinan1i@posterous.com','792 416 8380'),('6289144137','Onfre','Phittiplace','ophittiplace1m@twitter.com','504 226 2193'),('6367700719','Padraic','Grigoli','pgrigolif@pagesperso-orange.fr','622 407 6456'),('6401273000','Pernell','Cordel','pcordel16@php.net','867 487 2189'),('6407950356','Mirna','Guillard','mguillard2m@mail.ru','802 761 8498'),('6431277650','Easter','Feehery','efeehery4@amazon.com','740 816 2832'),('6486985395','Lily','Coot','lcoot21@unicef.org','940 989 5632'),('6582405432','Giulia','Wyllis','gwyllis22@ftc.gov','479 673 3672'),('6724175105','Xaviera','Apark','xaparkt@latimes.com','772 765 7699'),('6792529787','Pennie','Meas','pmeasu@about.me','902 652 6064'),('6801795569','Linoel','Groucutt','lgroucuttr@hibu.com','660 303 6861'),('695713586','Susanne','MacCaughey','smaccaughey2a@de.vu','563 411 6693'),('6982757101','Katherine','Blencowe','kblencowe19@edublogs.org','343 174 7358'),('7020897085','Lyssa','Rasmus','lrasmuso@trellian.com','302 195 5519'),('7195717545','Germana','Raysdale','graysdale2k@umn.edu','270 392 5518'),('7236507243','Morten','Mays','mmays9@addthis.com','699 512 2811'),('7311008090','Berget','Albutt','balbutt24@parallels.com','844 271 0832'),('7387071629','Donnie','Surman','dsurman2r@privacy.gov.au','842 306 5951'),('7442211611','Bevan','Artinstall','bartinstallp@adobe.com','788 298 4237'),('746969079','Kippy','MacLaverty','kmaclaverty1o@samsung.com','926 285 2925'),('7485471551','Wilhelm','Woolner','wwoolner2g@facebook.com','922 208 6847'),('7498535987','Thelma','Brunini','tbrunini1b@cnet.com','490 784 6089'),('7564373338','Salomi','Hinks','shinks1n@nationalgeographic.co','173 790 4007'),('7569203060','Isaak','Fenne','ifennen@slate.com','596 315 3251'),('7857177842','Romeo','Showalter','rshowalter15@elpais.com','403 610 5186'),('7907199772','Rosette','Brimilcombe','rbrimilcombej@disqus.com','959 556 1834'),('7952870833','Cindy','Mitchenson','cmitchenson14@macromedia.com','313 292 9431'),('7961041578','Vince','Strange','vstrange8@boston.com','590 970 0270'),('7974699083','Noni','Scotter','nscotter1c@dyndns.org','670 343 9967'),('8359445484','Aymer','Watkinson','awatkinson1x@sun.com','180 491 6826'),('8528745194','Arda','Radclyffe','aradclyffe1y@soup.io','420 340 1455'),('8537538071','Teddi','Sivil','tsivil1h@china.com.cn','409 677 5568'),('8548943042','Alida','Pargetter','apargetter2l@businesswire.com','540 383 1024'),('8555222135','Drusi','Andrivel','dandrivel7@godaddy.com','632 120 9847'),('8579907778','Vale','Ough','vough23@freewebs.com','627 529 4498'),('8600972092','Gavrielle','Woolvett','gwoolvettq@google.de','475 427 6219'),('8638140532','Freeman','Canero','fcanerov@about.me','733 279 4884'),('8673110574','Hilliard','Feavearyear','hfeavearyear29@sakura.ne.jp','167 498 0970'),('8828471989','Teena','Rizziello','trizziello3@reverbnation.com','344 218 9964'),('8853170720','Traci','Rodmell','trodmellm@furl.net','828 941 9283'),('8899674496','Tish','Harnetty','tharnettyh@vkontakte.ru','325 694 1828'),('9003482438','Thorny','Fransoni','tfransoni2n@t.co','711 778 4393'),('9022065426','Barbee','Couvert','bcouvert18@4shared.com','280 482 2527'),('9067946232','De','Gregol','dgregol6@goo.ne.jp','630 898 2197'),('9237403483','Myles','Zimmermanns','mzimmermanns2h@ox.ac.uk','137 812 6309'),('944412599','Talyah','Hundy','thundy2i@51.la','436 837 7493'),('9608747541','Scottie','Newvell','snewvell1g@businessweek.com','724 960 7923'),('alberto','paula','mora','paulam@gmail.com','1234567898');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `habitaciones`
--

DROP TABLE IF EXISTS `habitaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `habitaciones` (
  `id_habitacion` int NOT NULL,
  `tipo_habitacion` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nombre_habitacion` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `precio_noche` decimal(10,2) DEFAULT NULL,
  `estado` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id_habitacion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `habitaciones`
--

LOCK TABLES `habitaciones` WRITE;
/*!40000 ALTER TABLE `habitaciones` DISABLE KEYS */;
INSERT INTO `habitaciones` VALUES (1,'suit de lujo','Sueño Maritimo',800000.00,'Libre'),(2,'suit de lujo','Brisa del Mar',950000.00,'ocupado'),(3,'habitacion','Bahia',300000.00,'Libre'),(4,'habitacion','Arena Dorada',450000.00,'Libre'),(5,'habitacion','Oasis Azul',600000.00,'Libre'),(6,'habitacion','Encanto Coral',400000.00,'ocupado'),(7,'habitacion','Isla',250000.00,'ocupado'),(8,'habitacion','Oceanica',180000.00,'Libre'),(9,'habitacion','Coralina',350000.00,'ocupado'),(10,'habitacion','Paraiso Tropical',600000.00,'Libre'),(11,'habitacion','Arrecife',700000.00,'Libre'),(12,'habitacion','Perla Azul',300000.00,'Libre');
/*!40000 ALTER TABLE `habitaciones` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historial`
--

LOCK TABLES `historial` WRITE;
/*!40000 ALTER TABLE `historial` DISABLE KEYS */;
INSERT INTO `historial` VALUES (1,NULL,'1070464473',10,'2025-04-20','2025-04-30',6000000.00,'finalizada'),(2,NULL,'1070464473',6,'2025-04-20','2025-04-30',4000000.00,'finalizada'),(3,NULL,'1070589457',8,'2025-04-15','2025-04-20',900000.00,'finalizada'),(4,NULL,'1070464473',8,'2025-04-15','2025-04-20',900000.00,'finalizada'),(5,NULL,'1070589457',5,'2025-04-18','2025-04-22',2400000.00,'finalizada'),(6,NULL,'1070589457',2,'2025-04-18','2025-04-20',1900000.00,'finalizada'),(7,NULL,'1070464473',5,'2025-04-22','2025-04-30',4800000.00,'finalizada'),(8,NULL,'1070464473',5,'2025-04-22','2025-04-28',3600000.00,'finalizada'),(10,NULL,'1070464166',4,'2025-04-28','2025-04-30',900000.00,'finalizada'),(11,1,'1070464473',4,'2025-05-05','2025-05-10',2250000.00,'finalizada'),(12,NULL,'1414',7,'2025-05-06','2025-05-09',750000.00,'activa'),(13,2,'1070464166',12,'2025-05-10','2025-05-11',300000.00,'finalizada'),(14,3,'1070589457',7,'2025-05-10','2025-05-11',250000.00,'finalizada'),(15,3,'1070589457',2,'2025-05-10','2025-05-11',950000.00,'finalizada'),(16,4,'1217438914',3,'2025-05-10','2025-05-11',300000.00,'finalizada'),(18,5,'1528208926',7,'2025-05-10','2025-05-11',250000.00,'finalizada'),(19,6,'1060598042',11,'2025-05-10','2025-05-11',700000.00,'finalizada'),(20,1,'1070464473',2,'2025-05-12','2025-05-13',950000.00,'finalizada'),(21,1,'1070464473',4,'2025-05-22','2025-05-25',1350000.00,'finalizada'),(22,2,'1060598042',2,'2025-05-25','2025-05-30',4750000.00,'finalizada'),(23,3,'1070464473',5,'2025-05-29','2025-06-05',4200000.00,'finalizada'),(24,1,'1070464473',10,'2025-09-16','2025-09-21',3000000.00,'finalizada'),(25,2,'1070589457',1,'2025-09-23','2025-09-25',1600000.00,'finalizada'),(26,1,'1069714447',2,'2025-10-04','2025-10-06',1900000.00,'activa'),(27,2,'1069714447',6,'2025-10-16','2025-10-24',3200000.00,'activa'),(28,3,'1069714447',9,'2025-10-23','2025-10-25',700000.00,'activa'),(29,4,'1070464473',7,'2025-10-05','2025-10-10',1250000.00,'activa');
/*!40000 ALTER TABLE `historial` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historial_acciones`
--

DROP TABLE IF EXISTS `historial_acciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historial_acciones` (
  `id` int NOT NULL AUTO_INCREMENT,
  `accion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=497 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historial_acciones`
--

LOCK TABLES `historial_acciones` WRITE;
/*!40000 ALTER TABLE `historial_acciones` DISABLE KEYS */;
INSERT INTO `historial_acciones` VALUES (1,'Ingreso de usuario: juan','2025-04-07 20:53:43'),(2,'Ingreso de Usuario: juan','2025-04-07 21:18:53'),(3,'Ingreso a Habitaciones ','2025-04-07 21:19:00'),(4,'Ingreso a Clientes ','2025-04-07 21:19:05'),(5,'Ingreso a Reservas','2025-04-07 21:19:19'),(6,'Resgistro de Reserva: 3','2025-04-07 21:20:03'),(7,'Modificaion de Reserva: 3','2025-04-07 21:20:32'),(8,'Ingreso a Reservas','2025-04-07 21:25:18'),(9,'Resgistro de Reserva: 4','2025-04-07 21:26:18'),(10,'Ingreso a Reservas','2025-04-11 13:18:25'),(11,'Ingreso a Reservas','2025-04-11 13:23:05'),(12,'Resgistro de Reserva: 5','2025-04-11 13:23:38'),(13,'Eliminacion de Reserva: 3','2025-04-11 13:27:24'),(14,'Eliminacion de Reserva: 5','2025-04-11 13:27:48'),(15,'Eliminacion de Reserva: 4','2025-04-11 13:28:12'),(16,'Ingreso a Habitaciones ','2025-04-11 13:28:23'),(17,'Ingreso a Reservas','2025-04-11 13:29:11'),(18,'Resgistro de Reserva: 3','2025-04-11 13:30:06'),(19,'Resgistro de Reserva: 4','2025-04-11 13:33:03'),(20,'Ingreso a Reservas','2025-04-11 13:33:13'),(21,'Resgistro de Reserva: 5','2025-04-11 13:33:39'),(22,'Ingreso a Reservas','2025-04-11 13:33:55'),(23,'Resgistro de Reserva: 6','2025-04-11 13:34:19'),(24,'Ingreso a Reservas','2025-04-11 13:40:23'),(25,'Ingreso a Clientes ','2025-04-11 13:40:37'),(26,'Resgistro de Reserva: 6','2025-04-11 13:41:17'),(27,'Ingreso a Reservas','2025-04-11 13:50:14'),(28,'Eliminacion de Reserva: 3','2025-04-11 13:50:31'),(29,'Eliminacion de Reserva: 4','2025-04-11 13:50:47'),(30,'Eliminacion de Reserva: 4','2025-04-11 13:51:05'),(31,'Eliminacion de Reserva: 5','2025-04-11 13:51:17'),(32,'Eliminacion de Reserva: 6','2025-04-11 13:51:31'),(33,'Ingreso a Reservas','2025-04-11 13:51:37'),(34,'Resgistro de Reserva: 3','2025-04-11 13:51:59'),(35,'Resgistro de Reserva: 4','2025-04-11 13:52:44'),(36,'Ingreso a Clientes ','2025-04-11 14:00:38'),(37,'Ingreso a Reservas','2025-04-13 16:48:08'),(38,'Registro de Reserva: 1','2025-04-13 16:49:37'),(39,'Ingreso a Reservas','2025-04-13 16:52:13'),(40,'Registro de Reserva: 1','2025-04-13 16:52:38'),(41,'Ingreso a Reservas','2025-04-13 16:54:10'),(42,'Ingreso a Reservas','2025-04-16 17:14:50'),(43,'Ingreso a Reservas','2025-04-16 19:37:25'),(44,'Registro de Reserva: 2','2025-04-16 19:37:53'),(45,'Eliminacion de Reserva: 2','2025-04-16 19:38:25'),(46,'Eliminacion de Reserva: 2','2025-04-16 19:38:35'),(47,'Ingreso a Reservas','2025-04-16 19:39:30'),(48,'Ingreso a Reservas','2025-04-16 19:46:25'),(49,'Registro de Reserva: 2','2025-04-16 19:46:53'),(50,'Ingreso a Reservas','2025-04-16 23:27:42'),(51,'Ingreso a Habitaciones ','2025-04-17 17:20:33'),(52,'Ingreso a Reservas','2025-04-17 17:21:26'),(53,'Ingreso a Habitaciones ','2025-04-17 17:22:34'),(54,'Ingreso a Habitaciones ','2025-04-17 17:26:37'),(55,'Ingreso a Habitaciones ','2025-04-17 17:29:05'),(56,'Ingreso a Habitaciones ','2025-04-17 17:30:17'),(57,'Ingreso a Reservas','2025-04-17 17:30:29'),(58,'Ingreso a Habitaciones ','2025-04-17 17:32:36'),(59,'Habitación marcada en mantenimiento: 1','2025-04-17 17:32:42'),(60,'Ingreso a Habitaciones ','2025-04-17 17:35:45'),(61,'Habitación marcada como Libre: 1','2025-04-17 17:35:51'),(62,'Ingreso a Reservas','2025-04-20 22:22:14'),(63,'Ingreso a Reservas','2025-04-21 16:44:58'),(64,'Ingreso a Reservas','2025-04-21 16:45:32'),(65,'Ingreso a Reservas','2025-04-21 22:05:05'),(66,'Ingreso a Reservas','2025-04-21 22:10:38'),(67,'Registro de Reserva: 3','2025-04-21 22:11:23'),(68,'Eliminacion de Reserva: 3','2025-04-21 22:11:45'),(69,'Ingreso a Reservas','2025-04-21 22:13:46'),(70,'Ingreso a Reservas','2025-04-21 23:06:19'),(71,'Registro de Reserva: 1','2025-04-21 23:06:38'),(72,'Registro de Reserva: 1','2025-04-21 23:07:14'),(73,'Ingreso a Check out ','2025-04-21 23:08:00'),(74,'Ingreso a Reservas','2025-04-21 23:26:42'),(75,'Ingreso a Registro Clientes','2025-04-21 23:26:47'),(76,'Ingreso a Clientes ','2025-04-21 23:26:51'),(77,'Ingreso a Registro Clientes','2025-04-21 23:27:48'),(78,'Ingreso a Clientes ','2025-04-21 23:27:51'),(79,'Datos del cliente con cédula: 1070464473 modificados.','2025-04-21 23:28:01'),(80,'Ingreso a Registro Clientes','2025-04-21 23:29:36'),(81,'Ingreso a Clientes ','2025-04-21 23:29:39'),(82,'Datos del cliente con cédula: 1070464473 modificados.','2025-04-21 23:29:47'),(83,'Datos del cliente con cédula: 1070464473 modificados.','2025-04-21 23:29:58'),(84,'Ingreso a Registro Clientes','2025-04-21 23:31:48'),(85,'Ingreso a Clientes ','2025-04-21 23:31:51'),(86,'Datos del cliente con cédula: 1070464166 modificados.','2025-04-21 23:32:00'),(87,'Ingreso a Registro Clientes','2025-04-21 23:32:21'),(88,'Ingreso a Clientes ','2025-04-21 23:32:24'),(89,'Datos del cliente con cédula: 1070464473 modificados.','2025-04-21 23:32:33'),(90,'Ingreso a Registro Clientes','2025-04-22 17:59:51'),(91,'Ingreso a Clientes ','2025-04-22 17:59:55'),(92,'Datos del cliente con cédula: 1070464473 modificados.','2025-04-22 18:00:23'),(93,'Registro de Usuario: paula','2025-04-26 14:38:45'),(94,'Ingreso de Usuario: paula','2025-04-26 14:39:08'),(95,'Ingreso a Habitaciones ','2025-04-26 14:39:43'),(96,'Ingreso a Registro Clientes','2025-04-26 14:39:49'),(97,'Ingreso a Clientes ','2025-04-26 14:40:04'),(98,'Ingreso a Clientes ','2025-04-26 14:40:14'),(99,'Datos del cliente con cédula: 1070464473 modificados.','2025-04-26 14:40:28'),(100,'Registro de Reserva: 2','2025-04-27 20:00:55'),(102,'Registro de Acompañante: pepito perez','2025-05-03 03:43:28'),(103,'Registro de Acompañante: andres perez','2025-05-03 03:44:02'),(104,'Registro de Usuario: pablo','2025-05-04 00:33:51'),(105,'Registro de Usuario: jose','2025-05-04 00:33:59'),(106,'Registro de Usuario: esteban','2025-05-04 00:34:09'),(107,'Registro de Usuario: dayana','2025-05-04 00:34:17'),(108,'Registro de Usuario: samuel','2025-05-04 00:34:23'),(109,'Registro de Usuario: sandra','2025-05-04 00:34:33'),(110,'Registro de Usuario: antonio','2025-05-04 00:34:45'),(111,'Registro de Usuario: paola','2025-05-04 00:35:04'),(112,'Registro de Usuario: daniel','2025-05-04 00:35:11'),(113,'Ingreso de Usuario: juan','2025-05-04 00:35:25'),(114,'Ingreso a Politica','2025-05-04 00:35:32'),(115,'Ingreso a Registro Clientes','2025-05-04 00:35:33'),(116,'Ingreso a Habitaciones ','2025-05-04 00:37:05'),(117,'Ingreso a Reservas','2025-05-04 00:37:11'),(118,'Ingreso a Reservas','2025-05-04 00:38:19'),(119,'Ingreso a Politica','2025-05-04 00:39:03'),(120,'Ingreso a Acompañantes','2025-05-04 00:39:04'),(121,'Ingreso a Check In','2025-05-04 00:39:10'),(122,'Ingreso a Check out ','2025-05-04 00:39:20'),(123,'Ingreso a Politica','2025-05-04 00:57:23'),(124,'Ingreso a Clientes ','2025-05-04 00:57:33'),(125,'Ingreso a Politica','2025-05-04 17:47:21'),(126,'Ingreso a Check In','2025-05-04 17:47:42'),(127,'Ingreso a Check In','2025-05-04 17:49:42'),(128,'Ingreso a Politica','2025-05-04 17:52:11'),(129,'Ingreso a Politica','2025-05-04 18:21:50'),(130,'Ingreso del cliente: Juan Esteban Rodriguez al hotel','2025-05-04 22:49:46'),(131,'Factura del cliente Juan Esteban Rodriguez generada','2025-05-04 22:59:23'),(132,'Factura del cliente: Juan Esteban Rodriguez generada','2025-05-04 23:04:08'),(133,'Pago del cliente: Juan Esteban Rodriguez enviado al correo','2025-05-04 23:04:21'),(134,'Registro de Reserva: 1','2025-05-04 23:04:32'),(135,'Pago del cliente: Juan Esteban Rodriguez enviado al correo','2025-05-04 23:04:43'),(136,'Ingreso a Politica','2025-05-04 23:12:47'),(137,'Ingreso a Historial Reservas','2025-05-05 12:47:30'),(138,'Registro de Usuario: jrambo','2025-05-05 12:56:50'),(139,'Ingreso de Usuario: jrambo','2025-05-05 12:57:05'),(140,'Ingreso a Habitaciones ','2025-05-05 12:57:27'),(141,'Ingreso a Politica','2025-05-05 12:57:45'),(142,'Ingreso a Registro Clientes','2025-05-05 12:57:47'),(143,'Ingreso a Clientes ','2025-05-05 12:59:38'),(144,'Datos del cliente con cédula: 1070464473 modificados.','2025-05-05 13:00:18'),(145,'Registro de Cliente: lola perez','2025-05-05 13:02:26'),(146,'Registro de Cliente: lola perez','2025-05-05 13:03:19'),(147,'Ingreso a Reservas','2025-05-05 13:03:24'),(148,'Registro de Reserva: 2','2025-05-05 13:04:05'),(149,'Pago del cliente:   enviado al correo','2025-05-05 13:04:27'),(150,'Registro de Usuario: pilar','2025-05-07 00:43:57'),(151,'Ingreso de Usuario: pilar','2025-05-07 00:44:51'),(164,'Ingreso de Recepcionista: juan','2025-05-07 01:22:30'),(165,'Intento de inicio de sesión [Éxito] - Usuario: juan - Tipo: Recepcionista','2025-05-07 01:22:31'),(166,'Ingreso a Politica','2025-05-07 22:11:16'),(167,'Ingreso de Recepcionista: juan','2025-05-07 22:44:14'),(168,'Intento de inicio de sesión [Éxito] - Usuario: juan - Tipo: Recepcionista','2025-05-07 22:44:15'),(169,'Ingreso a Politica','2025-05-07 22:44:17'),(170,'Ingreso a Registro Clientes','2025-05-07 22:44:19'),(171,'Ingreso a Politica','2025-05-07 22:53:35'),(172,'Ingreso a Registro Clientes','2025-05-07 22:53:36'),(173,'Ingreso a Politica','2025-05-07 22:54:19'),(174,'Ingreso a Registro Clientes','2025-05-07 22:54:20'),(175,'Ingreso a Politica','2025-05-07 22:55:36'),(176,'Ingreso a Registro Clientes','2025-05-07 22:55:37'),(177,'Ingreso a Check In','2025-05-07 22:56:20'),(178,'Ingreso a Politica','2025-05-07 22:56:29'),(179,'Ingreso a Acompañantes','2025-05-07 22:56:30'),(180,'Ingreso a Politica','2025-05-07 22:56:40'),(181,'Ingreso a Registro Clientes','2025-05-07 22:56:42'),(182,'Ingreso a Habitaciones ','2025-05-07 22:57:59'),(183,'Ingreso a Reservas','2025-05-07 22:58:06'),(184,'Ingreso a Politica','2025-05-07 22:58:09'),(185,'Ingreso a Registro Clientes','2025-05-07 22:58:10'),(186,'Ingreso a Reservas','2025-05-07 22:58:17'),(187,'Ingreso a Politica','2025-05-07 22:58:53'),(188,'Ingreso a Registro Clientes','2025-05-07 22:58:54'),(189,'Ingreso a Politica','2025-05-07 22:59:01'),(190,'Ingreso a Acompañantes','2025-05-07 22:59:02'),(191,'Ingreso a Reservas','2025-05-07 22:59:07'),(192,'Ingreso a Politica','2025-05-08 22:07:06'),(193,'Registro de Acompañante: alex rodriguez','2025-05-08 22:08:17'),(194,'Registro de Acompañante: jorge rodriguez','2025-05-08 22:08:42'),(195,'Registro de Usuario: administrador','2025-05-10 19:07:32'),(196,'Registro de Usuario: administrador','2025-05-10 19:08:32'),(197,'Registro de Usuario: adminHotel','2025-05-10 19:32:34'),(198,'Ingreso de Administrador: adminHotel','2025-05-10 19:32:48'),(199,'Intento de inicio de sesión [Éxito] - Usuario: adminHotel - Tipo: Administrador','2025-05-10 19:32:49'),(200,'Ingreso a Historial Reservas','2025-05-10 19:32:55'),(201,'Ingreso a Historial Acciones','2025-05-10 19:33:02'),(202,'Ingreso a Politica','2025-05-10 20:07:43'),(203,'Ingreso a Registro Clientes','2025-05-10 20:07:45'),(204,'Ingreso a Clientes ','2025-05-10 20:07:48'),(205,'Ingreso a Habitaciones ','2025-05-10 20:12:07'),(206,'Ingreso a Reservas','2025-05-10 20:12:23'),(207,'Ingreso a Habitaciones ','2025-05-10 20:14:02'),(208,'Ingreso a Politica','2025-05-10 20:59:52'),(209,'Ingreso a Clientes ','2025-05-10 20:59:56'),(210,'Ingreso a Politica','2025-05-10 21:01:50'),(211,'Ingreso a Clientes ','2025-05-10 21:01:53'),(212,'Ingreso a Politica','2025-05-10 21:03:09'),(213,'Ingreso a Politica','2025-05-10 21:04:48'),(214,'Ingreso a Clientes ','2025-05-10 21:04:51'),(215,'Registro de Reserva: 2','2025-05-10 21:33:48'),(216,'Pago del cliente: luisa rojas enviado al correo','2025-05-10 21:34:08'),(217,'Registro de Reserva: 3','2025-05-10 21:34:37'),(218,'Pago del cliente: Edward Villalba enviado al correo','2025-05-10 21:34:49'),(219,'Registro de Reserva: 3','2025-05-10 21:37:28'),(220,'Ingreso a Habitaciones ','2025-05-10 21:38:29'),(221,'Habitación marcada como Libre: 7','2025-05-10 21:38:39'),(222,'Registro de Reserva: 4','2025-05-11 00:30:14'),(223,'Pago del cliente: Francisco Gaddas enviado al correo','2025-05-11 00:30:27'),(224,'Registro de Pago: 1 con estado: Pendiente','2025-05-11 00:30:47'),(225,'Registro de Reserva: 4','2025-05-11 00:31:27'),(226,'Registro de Reserva: 5','2025-05-11 00:39:35'),(227,'Pago del cliente: Almire Bilbie enviado al correo','2025-05-11 00:39:42'),(228,'Registro de Pago: 1 con estado: Pagado','2025-05-11 00:39:59'),(229,'Registro de Reserva: 6','2025-05-11 00:41:24'),(230,'Pago del cliente: Carena Gidman enviado al correo','2025-05-11 00:41:34'),(231,'Registro de Pago: 1 con estado: Pagada','2025-05-11 00:41:50'),(232,'Ingreso a Check out ','2025-05-12 13:05:22'),(233,'Pago del cliente: Juan  Parra enviado al correo','2025-05-12 13:05:43'),(234,'Ingreso a Reservas','2025-05-12 13:06:16'),(235,'Registro de Reserva: 1','2025-05-12 13:07:09'),(236,'Pago del cliente: Juan  Parra enviado al correo','2025-05-12 13:07:17'),(237,'Registro de Pago: 2 con estado: Pagada','2025-05-12 13:07:24'),(238,'Ingreso a Habitaciones ','2025-05-12 13:07:51'),(239,'Ingreso a Habitaciones ','2025-05-12 13:08:09'),(240,'Ingreso a Politica','2025-05-12 13:08:29'),(241,'Ingreso a Registro Clientes','2025-05-12 13:08:35'),(242,'Ingreso a Clientes ','2025-05-12 13:08:38'),(243,'Ingreso a Habitaciones ','2025-05-12 13:09:45'),(244,'Ingreso a Reservas','2025-05-12 13:12:51'),(245,'Ingreso a Habitaciones ','2025-05-12 13:13:01'),(246,'Habitación marcada como Libre: 1','2025-05-12 13:13:11'),(247,'Ingreso a Reservas','2025-05-12 13:13:39'),(248,'Ingreso a Politica','2025-05-12 13:14:08'),(249,'Ingreso a Registro Clientes','2025-05-12 13:14:10'),(250,'Ingreso a Clientes ','2025-05-12 13:14:16'),(251,'Ingreso a Reservas','2025-05-12 13:14:35'),(252,'Ingreso a Historial Reservas','2025-05-12 13:28:48'),(253,'Ingreso a Historial Acciones','2025-05-12 13:29:15'),(254,'Ingreso a Historial Acciones','2025-05-12 13:31:45'),(255,'Ingreso a Historial Reservas','2025-05-12 13:31:49'),(256,'Ingreso de Recepcionista: juan','2025-05-12 19:57:48'),(257,'Intento de inicio de sesión [Fallo] - Usuario: juan - Tipo: Desconocido','2025-05-19 12:57:25'),(258,'Ingreso de Recepcionista: juan','2025-05-19 12:57:32'),(259,'Intento de inicio de sesión [Éxito] - Usuario: juan - Tipo: Recepcionista','2025-05-19 12:57:32'),(260,'Intento de inicio de sesión [Fallo] - Usuario: dd - Tipo: Desconocido','2025-05-19 13:02:37'),(261,'Ingreso de Recepcionista: juan','2025-05-19 13:02:49'),(262,'Intento de inicio de sesión [Éxito] - Usuario: juan - Tipo: Recepcionista','2025-05-19 13:02:49'),(263,'Intento de inicio de sesión [Fallo] - Usuario: juan - Tipo: Desconocido','2025-05-19 13:12:38'),(264,'Intento de inicio de sesión [Fallo] - Usuario: jjkjk - Tipo: Desconocido','2025-05-20 12:43:48'),(265,'Ingreso de Recepcionista: juan','2025-05-20 12:44:09'),(266,'Intento de inicio de sesión [Éxito] - Usuario: juan - Tipo: Recepcionista','2025-05-20 12:44:09'),(267,'Ingreso a Politica','2025-05-20 12:46:18'),(268,'Ingreso a Registro Clientes','2025-05-20 12:46:18'),(269,'Registro de Cliente: german mauricio rodriguez fernandez de rondon','2025-05-20 12:57:35'),(270,'Registro de Cliente: german mauricio rodriguez fernandez de rondon','2025-05-20 12:58:18'),(271,'Registro de Cliente: paula mora','2025-05-20 12:59:01'),(272,'Ingreso de Recepcionista: juan','2025-05-21 20:52:27'),(273,'Intento de inicio de sesión [Éxito] - Usuario: juan - Tipo: Recepcionista','2025-05-21 20:52:27'),(274,'Ingreso a Politica','2025-05-21 20:53:33'),(275,'Ingreso a Registro Clientes','2025-05-21 20:53:33'),(276,'Ingreso a Politica','2025-05-21 20:55:49'),(277,'Ingreso a Registro Clientes','2025-05-21 20:55:49'),(278,'Ingreso a Clientes ','2025-05-21 20:55:51'),(279,'Ingreso a Reservas','2025-05-21 20:56:38'),(280,'Ingreso a Habitaciones ','2025-05-21 20:57:43'),(281,'Ingreso a Check In','2025-05-21 20:58:26'),(282,'Ingreso a Politica','2025-05-21 20:59:21'),(283,'Ingreso a Check out ','2025-05-21 21:00:00'),(284,'Ingreso a Politica','2025-05-21 21:00:39'),(285,'Ingreso a Acompañantes','2025-05-21 21:00:39'),(286,'Ingreso a Reservas','2025-05-21 21:00:47'),(287,'Intento de inicio de sesión [Fallo] - Usuario: adminHotel - Tipo: Desconocido','2025-05-21 21:00:57'),(288,'Ingreso de Administrador: adminHotel','2025-05-21 21:08:39'),(289,'Intento de inicio de sesión [Éxito] - Usuario: adminHotel - Tipo: Administrador','2025-05-21 21:08:39'),(290,'Ingreso a Historial Reservas','2025-05-21 21:09:02'),(291,'Ingreso a Historial Reservas','2025-05-21 21:09:30'),(292,'Ingreso a Historial Acciones','2025-05-21 21:09:40'),(293,'Ingreso a Historial Pagos','2025-05-21 21:10:40'),(294,'Ingreso a Historial Politica de Datos','2025-05-21 21:11:22'),(295,'Ingreso a Politica','2025-05-21 21:19:44'),(296,'Ingreso a Clientes ','2025-05-21 21:19:49'),(297,'Datos del cliente con cédula: 1070464473 modificados.','2025-05-21 21:21:13'),(298,'Intento de inicio de sesión [Fallo] - Usuario: ju - Tipo: Desconocido','2025-05-21 21:22:53'),(299,'Ingreso a Politica','2025-05-21 21:44:33'),(300,'Ingreso a Clientes ','2025-05-21 21:44:35'),(301,'Ingreso a Historial Pagos','2025-05-21 22:55:15'),(302,'El pago ha sido marcado como \'Pendiente\'.1','2025-05-21 22:55:21'),(303,'El pago ha sido marcado como \'Pagada\'.1','2025-05-21 22:55:25'),(304,'Registro de Reserva: 1','2025-05-22 01:51:55'),(305,'Pago del cliente: Juan Esteban Parra enviado al correo','2025-05-22 01:55:45'),(306,'Registro de Pago (Reserva): 3 con estado: Pagada','2025-05-22 01:55:51'),(307,'Registro de Reserva: 2','2025-05-22 02:43:48'),(308,'Pago del cliente: Carena Gidman enviado al correo','2025-05-22 02:43:56'),(309,'Registro de Pago (Reserva): 4 con estado: Pagada','2025-05-22 02:44:00'),(310,'Ingreso a Habitaciones ','2025-05-22 02:56:59'),(311,'Habitación marcada en mantenimiento: 3','2025-05-22 02:58:01'),(312,'Ingreso de Recepcionista: juan','2025-05-22 22:23:37'),(313,'Intento de inicio de sesión [Éxito] - Usuario: juan - Tipo: Recepcionista','2025-05-22 22:23:37'),(314,'Ingreso a Politica','2025-05-22 22:23:40'),(315,'Ingreso a Registro Clientes','2025-05-22 22:23:40'),(316,'Ingreso a Clientes ','2025-05-22 22:23:43'),(317,'Registro de Usuario: nikolay','2025-05-29 14:30:24'),(318,'Ingreso de Recepcionista: nikolay','2025-05-29 14:30:46'),(319,'Intento de inicio de sesión [Éxito] - Usuario: nikolay - Tipo: Recepcionista','2025-05-29 14:30:46'),(320,'Ingreso a Politica','2025-05-29 14:31:46'),(321,'Ingreso a Registro Clientes','2025-05-29 14:31:46'),(322,'Ingreso a Reservas','2025-05-29 14:32:11'),(323,'Registro de Reserva: 3','2025-05-29 14:33:09'),(324,'Ingreso a Habitaciones ','2025-05-29 14:33:20'),(325,'Pago del cliente: Juan Esteban Parra enviado al correo','2025-05-29 14:34:27'),(326,'Registro de Pago (Reserva): 5 con estado: Pagada','2025-05-29 14:34:44'),(327,'Ingreso a Check In','2025-05-29 14:35:05'),(328,'Ingreso de Usuario: juan','2025-08-18 20:53:32'),(329,'Ingreso a Reservas','2025-08-18 20:53:39'),(330,'Ingreso de Recepcionista: juan','2025-08-18 20:56:28'),(331,'Intento de inicio de sesión [Éxito] - Usuario: juan - Tipo: Recepcionista','2025-08-18 20:56:28'),(332,'Ingreso a Reservas','2025-08-18 20:56:31'),(333,'Ingreso a Habitaciones ','2025-08-18 20:56:50'),(334,'Habitación marcada como Libre: 3','2025-08-18 20:56:55'),(335,'Ingreso a Politica','2025-08-18 20:57:14'),(336,'Ingreso a Registro Clientes','2025-08-18 20:57:14'),(337,'Ingreso a Clientes ','2025-08-18 20:57:18'),(338,'Registro de Usuario: juan','2025-09-15 16:18:48'),(339,'Ingreso de Recepcionista: juan','2025-09-15 16:19:03'),(340,'Intento de inicio de sesión [Éxito] - Usuario: juan - Tipo: Recepcionista','2025-09-15 16:19:03'),(341,'Ingreso a Politica','2025-09-15 16:19:09'),(342,'Ingreso a Registro Clientes','2025-09-15 16:19:09'),(343,'Ingreso a Clientes ','2025-09-15 16:19:14'),(344,'Ingreso a Politica','2025-09-17 02:21:52'),(345,'Ingreso a Registro Clientes','2025-09-17 02:21:54'),(346,'Ingreso a Clientes ','2025-09-17 02:21:59'),(347,'Ingreso de Recepcionista: juan','2025-09-17 03:42:34'),(348,'Intento de inicio de sesión [Éxito] - Usuario: juan - Tipo: Recepcionista','2025-09-17 03:42:35'),(349,'Ingreso a Habitaciones ','2025-09-17 03:42:41'),(350,'Ingreso a Reservas','2025-09-17 03:42:49'),(351,'Registro de Reserva: 1','2025-09-17 03:43:39'),(352,'Pago del cliente: Juan Esteban Parra enviado al correo','2025-09-17 03:43:52'),(353,'Registro de Pago (Reserva): 6 con estado: Pagada','2025-09-17 03:44:03'),(354,'Ticket del cliente: Juan Esteban Parra generada','2025-09-17 03:44:16'),(355,'Ingreso de Recepcionista: juan','2025-09-18 16:12:16'),(356,'Intento de inicio de sesión [Éxito] - Usuario: juan - Tipo: Recepcionista','2025-09-18 16:12:17'),(357,'Ingreso a Politica','2025-09-18 16:12:40'),(358,'Ingreso a Registro Clientes','2025-09-18 16:12:42'),(359,'Ingreso a Clientes ','2025-09-18 16:12:46'),(360,'Ingreso a Habitaciones ','2025-09-23 01:02:24'),(361,'Ingreso a Check In','2025-09-23 01:03:10'),(362,'Ingreso a Check In','2025-09-23 01:03:14'),(363,'Ingreso a Check In','2025-09-23 01:03:18'),(364,'Ingreso a Check out ','2025-09-23 01:04:04'),(365,'Ingreso a Reservas','2025-09-23 01:06:52'),(366,'Ingreso a Politica','2025-09-23 01:07:37'),(367,'Ingreso a Registro Clientes','2025-09-23 01:07:38'),(368,'Ingreso a Clientes ','2025-09-23 01:07:42'),(369,'Registro de Reserva: 2','2025-09-23 01:09:29'),(370,'Ingreso a Reservas','2025-09-23 01:09:47'),(371,'Ingreso de Administrador: adminHotel','2025-09-23 01:11:09'),(372,'Intento de inicio de sesión [Éxito] - Usuario: adminHotel - Tipo: Administrador','2025-09-23 01:11:10'),(373,'Ingreso a Check out ','2025-09-23 01:50:15'),(374,'Ticket del cliente: Juan Esteban Parra generada','2025-09-23 01:50:45'),(375,'Ingreso a Politica','2025-09-25 02:47:40'),(376,'Ingreso a Registro Clientes','2025-09-25 02:47:42'),(377,'Ingreso a Clientes ','2025-09-25 02:47:45'),(378,'Ingreso a Reservas','2025-09-25 02:48:16'),(379,'Pago en EFECTIVO del cliente: luisa rojas','2025-09-25 02:48:39'),(380,'Registro de Pago (Reserva): 7 con estado: Pagada','2025-09-25 02:48:49'),(381,'Pago TRANSACCIÓN del cliente: luisa rojas enviado al correo','2025-09-25 02:49:02'),(382,'Registro de Pago (Reserva): 8 con estado: Pagada','2025-09-25 02:49:16'),(383,'Ingreso de Recepcionista: juan','2025-09-25 17:18:14'),(384,'Intento de inicio de sesión [Éxito] - Usuario: juan - Tipo: Recepcionista','2025-09-25 17:18:16'),(385,'Ingreso a Reservas','2025-09-25 17:18:51'),(386,'Ingreso de Administrador: adminHotel','2025-09-25 17:19:10'),(387,'Intento de inicio de sesión [Éxito] - Usuario: adminHotel - Tipo: Administrador','2025-09-25 17:19:12'),(388,'Ingreso a Historial Acciones','2025-09-25 17:19:47'),(389,'Ingreso a Politica','2025-09-27 02:32:52'),(390,'Ingreso a Registro Clientes','2025-09-27 02:32:53'),(391,'Ingreso a Habitaciones ','2025-09-27 02:32:58'),(392,'Ingreso a Politica','2025-09-27 02:33:25'),(393,'Ingreso a Registro Clientes','2025-09-27 02:33:26'),(394,'Registro de Cliente: pedro perez','2025-09-27 02:33:45'),(395,'Ticket del cliente: Juan Esteban Parra generada','2025-09-28 05:25:45'),(396,'Intento de inicio de sesión [Fallo] - Usuario: Ingrese su nombre de usuario - Tipo: Desconocido','2025-09-28 22:38:44'),(397,'Registro de Usuario: dayis','2025-09-28 22:45:04'),(398,'Ingreso de Recepcionista: dayis','2025-09-28 22:45:13'),(399,'Intento de inicio de sesión [Éxito] - Usuario: dayis - Tipo: Recepcionista','2025-09-28 22:45:15'),(400,'Intento de inicio de sesión [Fallo] - Usuario: null - Tipo: Desconocido','2025-10-02 16:38:17'),(401,'Intento de inicio de sesión [Fallo] - Usuario: admin - Tipo: Desconocido','2025-10-02 16:38:25'),(402,'Ingreso de Recepcionista: dayis','2025-10-02 16:38:32'),(403,'Intento de inicio de sesión [Éxito] - Usuario: dayis - Tipo: Recepcionista','2025-10-02 16:38:35'),(404,'Intento de inicio de sesión [Fallo] - Usuario: usuarioFalso - Tipo: Desconocido','2025-10-02 16:38:48'),(405,'Intento de inicio de sesión [Fallo] - Usuario: null - Tipo: Desconocido','2025-10-02 16:41:00'),(406,'Ingreso de Administrador: adminHotel','2025-10-02 16:41:03'),(407,'Intento de inicio de sesión [Éxito] - Usuario: adminHotel - Tipo: Administrador','2025-10-02 16:41:06'),(408,'Ingreso de Recepcionista: dayis','2025-10-02 16:41:14'),(409,'Intento de inicio de sesión [Éxito] - Usuario: dayis - Tipo: Recepcionista','2025-10-02 16:41:22'),(410,'Intento de inicio de sesión [Fallo] - Usuario: usuarioFalso - Tipo: Desconocido','2025-10-02 16:41:38'),(411,'Intento de inicio de sesión [Fallo] - Usuario: null - Tipo: Desconocido','2025-10-02 16:42:55'),(412,'Intento de inicio de sesión [Fallo] - Usuario: null - Tipo: Desconocido','2025-10-02 16:45:27'),(413,'Ingreso de Administrador: adminHotel','2025-10-02 16:45:34'),(414,'Intento de inicio de sesión [Éxito] - Usuario: adminHotel - Tipo: Administrador','2025-10-02 16:45:40'),(415,'Ingreso de Recepcionista: dayis','2025-10-02 16:45:53'),(416,'Intento de inicio de sesión [Éxito] - Usuario: dayis - Tipo: Recepcionista','2025-10-02 16:45:55'),(417,'Intento de inicio de sesión [Fallo] - Usuario: pedro - Tipo: Desconocido','2025-10-02 16:46:06'),(418,'Ingreso de Recepcionista: dayis','2025-10-02 16:47:54'),(419,'Intento de inicio de sesión [Éxito] - Usuario: dayis - Tipo: Recepcionista','2025-10-02 16:48:06'),(420,'Intento de inicio de sesión [Fallo] - Usuario: pedro - Tipo: Desconocido','2025-10-02 16:48:51'),(421,'Registro de Usuario: hola','2025-10-03 00:46:03'),(422,'Ingreso a Politica','2025-10-03 00:52:16'),(423,'Ingreso a Registro Clientes','2025-10-03 00:52:18'),(424,'Ingreso a Politica','2025-10-03 00:53:02'),(425,'Ingreso a Politica','2025-10-03 01:06:33'),(426,'Registro de Cliente: luis garcia','2025-10-03 01:07:10'),(427,'Intento de inicio de sesión [Fallo] - Usuario: null - Tipo: Desconocido','2025-10-03 16:14:57'),(428,'Ingreso de Administrador: adminHotel','2025-10-03 16:14:59'),(429,'Intento de inicio de sesión [Éxito] - Usuario: adminHotel - Tipo: Administrador','2025-10-03 16:15:00'),(430,'Ingreso de Recepcionista: dayis','2025-10-03 16:15:02'),(431,'Intento de inicio de sesión [Éxito] - Usuario: dayis - Tipo: Recepcionista','2025-10-03 16:15:03'),(432,'Intento de inicio de sesión [Fallo] - Usuario: pedro - Tipo: Desconocido','2025-10-03 16:15:05'),(433,'Ingreso a Politica','2025-10-03 16:49:30'),(434,'Ingreso a Acompañantes','2025-10-03 16:49:31'),(435,'Ingreso a Politica','2025-10-03 16:49:35'),(436,'Ingreso a Acompañantes','2025-10-03 16:49:39'),(437,'Ingreso a Politica','2025-10-03 16:49:42'),(438,'Ingreso a Acompañantes','2025-10-03 16:49:45'),(439,'Ingreso a Politica','2025-10-03 16:49:53'),(440,'Ingreso a Acompañantes','2025-10-03 16:49:58'),(441,'Ingreso a Politica','2025-10-03 16:50:01'),(442,'Ingreso a Acompañantes','2025-10-03 16:50:26'),(443,'Ingreso a Politica','2025-10-03 16:50:29'),(444,'Ingreso a Acompañantes','2025-10-03 16:50:31'),(445,'Ingreso a Politica','2025-10-04 19:14:57'),(446,'Ingreso a Reservas','2025-10-04 19:27:12'),(447,'Ingreso a Reservas','2025-10-04 19:27:15'),(448,'Ingreso a Reservas','2025-10-04 19:27:18'),(449,'Ingreso a Reservas','2025-10-04 19:27:21'),(450,'Ingreso a Politica','2025-10-04 19:29:14'),(451,'Ingreso a Registro Clientes','2025-10-04 19:29:15'),(452,'Ingreso a Reservas','2025-10-04 19:29:47'),(453,'Registro de Cliente: Paula Mora','2025-10-04 19:29:52'),(454,'Registro de Reserva: 1','2025-10-04 19:30:33'),(455,'Ingreso a Politica','2025-10-04 19:31:37'),(456,'Ingreso a Acompañantes','2025-10-04 19:31:38'),(457,'Ingreso a Politica','2025-10-04 19:33:13'),(458,'Ingreso a Politica','2025-10-04 19:38:09'),(459,'Ingreso a Politica','2025-10-04 19:41:47'),(460,'Ingreso a Politica','2025-10-04 19:44:21'),(461,'Ingreso a Politica','2025-10-04 19:47:08'),(462,'Registro de Reserva: 2','2025-10-04 19:48:15'),(463,'Registro de Reserva: 3','2025-10-04 19:50:59'),(464,'Pago en EFECTIVO del cliente: Paula Mora','2025-10-04 19:51:10'),(465,'Registro de Pago (Reserva): 9 con estado: Pagada','2025-10-04 19:51:20'),(466,'Ingreso a Politica','2025-10-04 19:57:45'),(467,'Pago en EFECTIVO del cliente: Paula Mora','2025-10-04 20:05:00'),(468,'Registro de Pago (Reserva): 10 con estado: Pagada','2025-10-04 20:05:13'),(469,'Ticket del cliente: Paula Mora generada','2025-10-04 20:05:20'),(470,'Ingreso a Politica','2025-10-04 20:11:26'),(471,'Registro de Acompañante: dayana acosta','2025-10-04 20:15:48'),(472,'Registro de Acompañante: dayana acosta','2025-10-04 20:15:56'),(473,'Registro de Acompañante: dayana acosta','2025-10-04 20:17:48'),(474,'Registro de Acompañante: dayana acosta','2025-10-04 20:18:19'),(475,'Ingreso a Politica','2025-10-04 20:40:57'),(476,'Registro de Acompañante: esteban parra','2025-10-04 20:42:09'),(477,'Ingreso a Reservas','2025-10-04 20:52:09'),(478,'Registro de Reserva: 4','2025-10-04 20:52:57'),(479,'Pago TRANSACCIÓN del cliente: Juan Esteban Parra enviado al correo','2025-10-04 20:53:21'),(480,'Registro de Pago (Reserva): 11 con estado: Pagada','2025-10-04 20:53:31'),(481,'Ingreso a Check In','2025-10-04 20:53:48'),(482,'Ingreso a Politica','2025-10-04 20:54:25'),(483,'Ingreso del cliente: Juan Esteban Parra al hotel','2025-10-04 20:57:19'),(484,'Ingreso a Politica','2025-10-04 20:57:23'),(485,'Registro de Acompañante: esteban parra','2025-10-04 20:57:56'),(486,'Registro de Acompañante: esteban parra','2025-10-04 21:01:46'),(487,'Ingreso del cliente: Paula Mora al hotel','2025-10-06 16:55:14'),(488,'Ingreso a Politica','2025-10-06 16:55:22'),(489,'Ingreso a Politica','2025-10-06 16:55:26'),(490,'Intento de inicio de sesión [Fallo] - Usuario: null - Tipo: Desconocido','2025-10-09 16:44:04'),(491,'Ingreso de Administrador: adminHotel','2025-10-09 16:44:16'),(492,'Intento de inicio de sesión [Éxito] - Usuario: adminHotel - Tipo: Administrador','2025-10-09 16:44:21'),(493,'Ingreso de Recepcionista: dayis','2025-10-09 16:44:26'),(494,'Intento de inicio de sesión [Éxito] - Usuario: dayis - Tipo: Recepcionista','2025-10-09 16:44:27'),(495,'Intento de inicio de sesión [Fallo] - Usuario: pedro - Tipo: Desconocido','2025-10-09 16:44:31'),(496,'Intento de inicio de sesión [Fallo] - Usuario: null - Tipo: Desconocido','2025-10-09 16:45:18');
/*!40000 ALTER TABLE `historial_acciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historial_pagos`
--

DROP TABLE IF EXISTS `historial_pagos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historial_pagos` (
  `id_pago` int NOT NULL AUTO_INCREMENT,
  `id_cliente` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `valor_pago` decimal(10,2) DEFAULT NULL,
  `estado_pago` enum('Pendiente','Pagada','Cancelado') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha_pago` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_pago`),
  KEY `id_cliente` (`id_cliente`),
  CONSTRAINT `historial_pagos_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`Cedula`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historial_pagos`
--

LOCK TABLES `historial_pagos` WRITE;
/*!40000 ALTER TABLE `historial_pagos` DISABLE KEYS */;
INSERT INTO `historial_pagos` VALUES (1,'1060598042',700000.00,'Pagada','2025-05-21 22:55:24'),(2,'1070464473',950000.00,'Pagada','2025-05-12 13:07:23'),(3,'1070464473',2400000.00,'Pagada','2025-05-22 01:55:50'),(4,'1060598042',4750000.00,'Pagada','2025-05-22 02:43:59'),(5,'1070464473',5600000.00,'Pagada','2025-05-29 14:34:40'),(6,'1070464473',3000000.00,'Pagada','2025-09-17 03:44:01'),(7,'1070464166',300000.00,'Pagada','2025-09-25 02:48:47'),(8,'1070464166',300000.00,'Pagada','2025-09-25 02:49:13'),(9,'1069714447',700000.00,'Pagada','2025-10-04 19:51:17'),(10,'1069714447',2500000.00,'Pagada','2025-10-04 20:05:11'),(11,'1070464473',1250000.00,'Pagada','2025-10-04 20:53:28');
/*!40000 ALTER TABLE `historial_pagos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personas_hospedaje`
--

DROP TABLE IF EXISTS `personas_hospedaje`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `personas_hospedaje` (
  `id` int NOT NULL AUTO_INCREMENT,
  `documento_identidad` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `es_cliente_principal` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `politica_aceptada` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `aceptado_por_tercero` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha_aceptacion` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `personas_hospedaje`
--

LOCK TABLES `personas_hospedaje` WRITE;
/*!40000 ALTER TABLE `personas_hospedaje` DISABLE KEYS */;
INSERT INTO `personas_hospedaje` VALUES (1,'1069','Sí','Sí','Sí','2025-05-03'),(2,'1069','Sí','Sí','No','2025-05-04'),(3,'1070464473','Sí','Sí','Sí','2025-05-07'),(4,'1223455678','Sí','Sí','Sí','2025-05-08'),(5,'5254565879','Sí','Sí','Sí','2025-05-08'),(6,'1070464473','Sí','Sí','Sí','2025-05-20');
/*!40000 ALTER TABLE `personas_hospedaje` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos`
--

DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos` (
  `id_producto` varchar(10) NOT NULL,
  `nombre_producto` varchar(100) NOT NULL,
  `valor_unitario` decimal(10,2) NOT NULL,
  `cantidad` int NOT NULL,
  PRIMARY KEY (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos`
--

LOCK TABLES `productos` WRITE;
/*!40000 ALTER TABLE `productos` DISABLE KEYS */;
INSERT INTO `productos` VALUES ('N001','Agua mineral 500ml',4500.00,50),('N002','Gaseosa Coca-Cola 350ml',5500.00,50),('N003','Gaseosa Sprite 350ml',5500.00,50),('N004','Jugo Hit 250ml',4800.00,50),('N005','Cerveza Club Colombia 330ml',6500.00,50),('N006','Cerveza Corona 355ml',7500.00,50),('N007','Whisky miniatura (50ml)',18000.00,50),('N008','Ron Medellín miniatura (50ml)',15000.00,50),('N009','Papas Margarita 30g',3500.00,50),('N010','Maní salado 50g',3200.00,50),('N011','Galletas Festival',2800.00,50),('N012','Chocolatina Jet',2500.00,50),('N013','Barra de cereal',4200.00,50),('N014','Snickers mini',3800.00,50),('N015','Café instantáneo (sobre)',3000.00,50),('N016','Té aromática (sobre)',2800.00,50),('N017','Agua con gas 500ml',3800.00,50),('N018','Refresco en polvo (sobre)',1500.00,50),('N019','Vino tinto mini (187ml)',16000.00,50),('N020','Botella de agua grande 1L',5500.00,50);
/*!40000 ALTER TABLE `productos` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`Juanes`@`%`*/ /*!50003 TRIGGER `generar_id_producto` BEFORE INSERT ON `productos` FOR EACH ROW BEGIN
    DECLARE nuevo_id VARCHAR(10);
    DECLARE ultimo_num INT;

    -- Obtener el número más alto de los IDs existentes (parte numérica)
    SELECT IFNULL(MAX(CAST(SUBSTRING(id_producto, 2) AS UNSIGNED)), 0) + 1
    INTO ultimo_num
    FROM productos;

    -- Crear el nuevo ID con formato N001, N002, etc.
    SET nuevo_id = CONCAT('N', LPAD(ultimo_num, 3, '0'));

    -- Asignarlo al nuevo registro
    SET NEW.id_producto = nuevo_id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `reservas`
--

DROP TABLE IF EXISTS `reservas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservas` (
  `id_reserva` int NOT NULL,
  `id_cliente` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `id_habitacion` int DEFAULT NULL,
  `fecha_entrada` date DEFAULT NULL,
  `fecha_salida` date DEFAULT NULL,
  `total_precio` decimal(10,2) DEFAULT NULL,
  `dias_estancia` int DEFAULT NULL,
  PRIMARY KEY (`id_reserva`),
  KEY `id_cliente` (`id_cliente`),
  KEY `id_habitacion` (`id_habitacion`),
  CONSTRAINT `reservas_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`Cedula`),
  CONSTRAINT `reservas_ibfk_2` FOREIGN KEY (`id_habitacion`) REFERENCES `habitaciones` (`id_habitacion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservas`
--

LOCK TABLES `reservas` WRITE;
/*!40000 ALTER TABLE `reservas` DISABLE KEYS */;
INSERT INTO `reservas` VALUES (1,'1069714447',2,'2025-10-04','2025-10-06',1900000.00,2),(2,'1069714447',6,'2025-10-16','2025-10-24',3200000.00,8),(3,'1069714447',9,'2025-10-23','2025-10-25',700000.00,2),(4,'1070464473',7,'2025-10-05','2025-10-10',1250000.00,5);
/*!40000 ALTER TABLE `reservas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `descripcion` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'Administrador','Control total del sistema'),(2,'Recepcionista','Gestión de reservas y clientes');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

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
INSERT INTO `usuarios` VALUES ('adminHotel','admin1234',1,'2025-05-10 19:32:33'),('antonio','boupojp',2,'2025-05-06 22:57:04'),('daniel','ebojfm',2,'2025-05-06 22:57:04'),('dayana','ebzbob',2,'2025-05-06 22:57:04'),('dayis','ebzjt',2,'2025-09-28 22:45:01'),('esteban','ftufcbo',2,'2025-05-06 22:57:04'),('hola','ipmb',2,'2025-10-03 00:46:00'),('jose','kptf',2,'2025-05-06 22:57:04'),('jrambo','ksbncp',2,'2025-05-06 22:57:04'),('juan','kvbo',2,'2025-05-06 22:57:04'),('nikolay','ojlpmbz',2,'2025-05-29 14:30:22'),('pablo','qbcmp',2,'2025-05-06 22:57:04'),('paola','qbpmb',2,'2025-05-06 22:57:04'),('paula','qbvmb',2,'2025-05-06 22:57:04'),('pilar','qjmbs',2,'2025-05-07 00:43:55'),('samuel','tbnvfm',2,'2025-05-06 22:57:04'),('sandra','tboesb',2,'2025-05-06 22:57:04'),('testUser','12345',2,'2025-10-03 00:43:43'),('testUser_1759452617364','12345',2,'2025-10-03 00:50:22'),('testUser_1759508000899','12345',2,'2025-10-03 16:13:24'),('testUser_1759508144169','12345',2,'2025-10-03 16:15:46'),('testUser_1759508174044','12345',2,'2025-10-03 16:16:15'),('testUser_1759508240320','12345',2,'2025-10-03 16:17:22');
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

-- Dump completed on 2025-10-09 11:50:16
