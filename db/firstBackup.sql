CREATE DATABASE  IF NOT EXISTS `proyectolibro` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */;
USE `proyectolibro`;
-- MySQL dump 10.13  Distrib 8.0.13, for Win64 (x86_64)
--
-- Host: localhost    Database: proyectolibro
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
-- Table structure for table `amistad`
--

DROP TABLE IF EXISTS `amistad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `amistad` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `lectorID1` int(11) NOT NULL,
  `lectorID2` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `amistad_lectorID1_idx` (`lectorID1`),
  KEY `amistad_lectorID2_idx` (`lectorID2`),
  CONSTRAINT `amistad_lectorID1` FOREIGN KEY (`lectorID1`) REFERENCES `lector` (`id`),
  CONSTRAINT `amistad_lectorID2` FOREIGN KEY (`lectorID2`) REFERENCES `lector` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `amistad`
--

LOCK TABLES `amistad` WRITE;
/*!40000 ALTER TABLE `amistad` DISABLE KEYS */;
/*!40000 ALTER TABLE `amistad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `foto`
--

DROP TABLE IF EXISTS `foto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `foto` (
  `ID` int(11) NOT NULL,
  `URL` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `foto`
--

LOCK TABLES `foto` WRITE;
/*!40000 ALTER TABLE `foto` DISABLE KEYS */;
/*!40000 ALTER TABLE `foto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lector`
--

DROP TABLE IF EXISTS `lector`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `lector` (
  `ID` int(11) NOT NULL,
  `Username` varchar(45) NOT NULL,
  `FirstName` varchar(45) NOT NULL,
  `LastName` varchar(45) NOT NULL,
  `Nivel` int(11) DEFAULT NULL,
  `Email` varchar(45) DEFAULT NULL,
  `Password` varchar(45) DEFAULT NULL,
  `PermiteGeolocalizarlo` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lector`
--

LOCK TABLES `lector` WRITE;
/*!40000 ALTER TABLE `lector` DISABLE KEYS */;
/*!40000 ALTER TABLE `lector` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lectorlibro`
--

DROP TABLE IF EXISTS `lectorlibro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `lectorlibro` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `lectorID` int(11) NOT NULL,
  `libroID` int(11) NOT NULL,
  `geolocalizacion` int(11) DEFAULT NULL,
  `antiguedad` int(11) DEFAULT NULL,
  `tapa` int(11) DEFAULT NULL,
  `estado` int(11) DEFAULT NULL,
  `descripcion` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `lectorLibro_lectorID_idx` (`lectorID`),
  KEY `lectorLibro_libroID_idx` (`libroID`),
  CONSTRAINT `lectorLibro_lectorID` FOREIGN KEY (`lectorID`) REFERENCES `lector` (`id`),
  CONSTRAINT `lectorLibro_libroID` FOREIGN KEY (`libroID`) REFERENCES `libro` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lectorlibro`
--

LOCK TABLES `lectorlibro` WRITE;
/*!40000 ALTER TABLE `lectorlibro` DISABLE KEYS */;
/*!40000 ALTER TABLE `lectorlibro` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lectorlibrofoto`
--

DROP TABLE IF EXISTS `lectorlibrofoto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `lectorlibrofoto` (
  `ID` int(11) NOT NULL,
  `lectorlibroID` int(11) NOT NULL,
  `fotoID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `lectorlibrofoto_lectorlibroID_idx` (`lectorlibroID`),
  KEY `lectorlibrofoto_fotoID_idx` (`fotoID`),
  CONSTRAINT `lectorlibrofoto_fotoID` FOREIGN KEY (`fotoID`) REFERENCES `foto` (`id`),
  CONSTRAINT `lectorlibrofoto_lectorlibroID` FOREIGN KEY (`lectorlibroID`) REFERENCES `lectorlibro` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lectorlibrofoto`
--

LOCK TABLES `lectorlibrofoto` WRITE;
/*!40000 ALTER TABLE `lectorlibrofoto` DISABLE KEYS */;
/*!40000 ALTER TABLE `lectorlibrofoto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `libro`
--

DROP TABLE IF EXISTS `libro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `libro` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `titulo` varchar(45) NOT NULL,
  `ISBN` varchar(13) NOT NULL,
  `autores` varchar(45) DEFAULT NULL,
  `editorial` varchar(45) DEFAULT NULL,
  `año` int(11) DEFAULT NULL,
  `fechaEdicion` datetime DEFAULT NULL,
  `numeroEdicion` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `libro`
--

LOCK TABLES `libro` WRITE;
/*!40000 ALTER TABLE `libro` DISABLE KEYS */;
/*!40000 ALTER TABLE `libro` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `librofoto`
--

DROP TABLE IF EXISTS `librofoto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `librofoto` (
  `ID` int(11) NOT NULL,
  `libroID` int(11) NOT NULL,
  `fotoID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `librofoto_libroID_idx` (`libroID`),
  KEY `librofoto_fotoID_idx` (`fotoID`),
  CONSTRAINT `librofoto_fotoID` FOREIGN KEY (`fotoID`) REFERENCES `foto` (`id`),
  CONSTRAINT `librofoto_libroID` FOREIGN KEY (`libroID`) REFERENCES `libro` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `librofoto`
--

LOCK TABLES `librofoto` WRITE;
/*!40000 ALTER TABLE `librofoto` DISABLE KEYS */;
/*!40000 ALTER TABLE `librofoto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mensaje`
--

DROP TABLE IF EXISTS `mensaje`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `mensaje` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `contenido` varchar(45) NOT NULL,
  `fecha` datetime NOT NULL,
  `emisorID` int(11) NOT NULL,
  `receptorID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `mensaje_emisorID` (`emisorID`),
  KEY `mensaje_receptorID` (`receptorID`),
  CONSTRAINT `mensaje_emisorID` FOREIGN KEY (`emisorID`) REFERENCES `lector` (`id`),
  CONSTRAINT `mensaje_receptorID` FOREIGN KEY (`receptorID`) REFERENCES `lector` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mensaje`
--

LOCK TABLES `mensaje` WRITE;
/*!40000 ALTER TABLE `mensaje` DISABLE KEYS */;
/*!40000 ALTER TABLE `mensaje` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notificacion`
--

DROP TABLE IF EXISTS `notificacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `notificacion` (
  `ID` int(11) NOT NULL,
  `lectorID` int(11) NOT NULL,
  `contenido` varchar(45) DEFAULT NULL,
  `leido` tinyint(1) NOT NULL,
  `tipo` int(11) DEFAULT NULL,
  `pedidointercambioID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `notificacion_pedidointercambioID_idx` (`pedidointercambioID`),
  KEY `notificacion_lectorID_idx` (`lectorID`),
  CONSTRAINT `notificacion_lectorID` FOREIGN KEY (`lectorID`) REFERENCES `lector` (`id`),
  CONSTRAINT `notificacion_pedidointercambioID` FOREIGN KEY (`pedidointercambioID`) REFERENCES `pedidointercambio` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notificacion`
--

LOCK TABLES `notificacion` WRITE;
/*!40000 ALTER TABLE `notificacion` DISABLE KEYS */;
/*!40000 ALTER TABLE `notificacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidointercambio`
--

DROP TABLE IF EXISTS `pedidointercambio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `pedidointercambio` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `fecha` datetime NOT NULL,
  `lectorQuePideID1` int(11) NOT NULL,
  `lectorQueRecibePedidoID2` int(11) NOT NULL,
  `lectorLibroPedidoID1` int(11) NOT NULL,
  `lectorLibroEntregadoID2` int(11) DEFAULT NULL,
  `mensajeID` int(11) NOT NULL,
  `estado` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `intercambio_lectorID1_idx` (`lectorQuePideID1`),
  KEY `intercambio_lectorID2_idx` (`lectorQueRecibePedidoID2`),
  KEY `intercambio_libroID1_idx` (`lectorLibroPedidoID1`),
  KEY `intercambio_libroID2_idx` (`lectorLibroEntregadoID2`),
  KEY `pedidointercambio_mensajeID_idx` (`mensajeID`),
  CONSTRAINT `pedidointercambio_lectorID1` FOREIGN KEY (`lectorQuePideID1`) REFERENCES `lector` (`id`),
  CONSTRAINT `pedidointercambio_lectorID2` FOREIGN KEY (`lectorQueRecibePedidoID2`) REFERENCES `lector` (`id`),
  CONSTRAINT `pedidointercambio_libroID1` FOREIGN KEY (`lectorLibroPedidoID1`) REFERENCES `libro` (`id`),
  CONSTRAINT `pedidointercambio_libroID2` FOREIGN KEY (`lectorLibroEntregadoID2`) REFERENCES `libro` (`id`),
  CONSTRAINT `pedidointercambio_mensajeID` FOREIGN KEY (`mensajeID`) REFERENCES `mensaje` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidointercambio`
--

LOCK TABLES `pedidointercambio` WRITE;
/*!40000 ALTER TABLE `pedidointercambio` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedidointercambio` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-01-19 13:01:24
