CREATE DATABASE  IF NOT EXISTS `db_SharedEffort` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `db_SharedEffort`;
-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: 54.38.183.226    Database: db_SharedEffort
-- ------------------------------------------------------
-- Server version	5.7.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `DateEvenement`
--

DROP TABLE IF EXISTS `DateEvenement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DateEvenement` (
  `idDateEvenement` int(11) NOT NULL AUTO_INCREMENT,
  `dateEvenement` datetime NOT NULL,
  `idEvenement` int(11) DEFAULT NULL,
  PRIMARY KEY (`idDateEvenement`),
  KEY `idEvenement` (`idEvenement`),
  CONSTRAINT `DateEvenement_ibfk_1` FOREIGN KEY (`idEvenement`) REFERENCES `Evenement` (`idEvenement`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DateEvenement`
--

LOCK TABLES `DateEvenement` WRITE;
/*!40000 ALTER TABLE `DateEvenement` DISABLE KEYS */;
INSERT INTO `DateEvenement` VALUES (1,'2018-08-07 19:36:00',1),(2,'2018-08-08 13:31:00',2),(3,'2018-08-15 19:03:00',3),(4,'2018-08-15 19:05:00',4),(5,'2018-08-15 19:12:00',5),(6,'2018-08-10 17:59:00',6),(7,'2018-08-10 17:59:00',7);
/*!40000 ALTER TABLE `DateEvenement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `DateTache`
--

DROP TABLE IF EXISTS `DateTache`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DateTache` (
  `idDateTache` int(11) NOT NULL AUTO_INCREMENT,
  `dateTache` datetime NOT NULL,
  `idTache` int(11) NOT NULL,
  `statutTache` int(11) DEFAULT '0',
  PRIMARY KEY (`idDateTache`),
  UNIQUE KEY `idDateTache` (`idDateTache`),
  KEY `idTache` (`idTache`),
  CONSTRAINT `DateTache_ibfk_1` FOREIGN KEY (`idTache`) REFERENCES `Tache` (`idTache`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DateTache`
--

LOCK TABLES `DateTache` WRITE;
/*!40000 ALTER TABLE `DateTache` DISABLE KEYS */;
INSERT INTO `DateTache` VALUES (2,'2018-08-06 19:00:00',2,1),(3,'2018-08-08 00:00:00',3,0),(4,'2017-08-08 13:42:00',4,0),(5,'2018-08-10 16:10:00',5,0),(6,'2018-08-10 19:01:00',6,0),(7,'2018-08-10 19:03:00',7,0),(8,'2018-08-16 20:28:00',8,0),(9,'2018-08-10 20:34:00',9,0),(10,'2018-08-11 20:36:00',10,0),(11,'2018-08-11 20:46:00',11,0),(12,'2018-08-11 21:03:00',12,0),(13,'2018-08-11 21:10:00',13,0),(14,'2018-08-11 17:44:00',14,1),(15,'2018-08-11 17:45:00',15,1),(16,'2018-08-12 18:24:00',16,1),(17,'2018-08-12 00:00:00',17,0),(18,'2018-08-12 00:00:00',18,1),(20,'2018-08-09 17:25:00',20,2);
/*!40000 ALTER TABLE `DateTache` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Evenement`
--

DROP TABLE IF EXISTS `Evenement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Evenement` (
  `idEvenement` int(11) NOT NULL AUTO_INCREMENT,
  `nomEvenement` varchar(500) NOT NULL,
  `descriptionEvenement` varchar(2000) DEFAULT NULL,
  `idFamille` int(11) DEFAULT NULL,
  `idPersonne` int(11) DEFAULT NULL,
  `estRecurrent` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`idEvenement`),
  KEY `Evenement_Famille` (`idFamille`),
  KEY `Evemement_Personne` (`idPersonne`),
  CONSTRAINT `Evemement_Personne` FOREIGN KEY (`idPersonne`) REFERENCES `Personne` (`idPersonne`),
  CONSTRAINT `Evenement_Famille` FOREIGN KEY (`idFamille`) REFERENCES `Famille` (`idFamille`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Evenement`
--

LOCK TABLES `Evenement` WRITE;
/*!40000 ALTER TABLE `Evenement` DISABLE KEYS */;
INSERT INTO `Evenement` VALUES (1,'Event','',1,NULL,0),(2,'eventF','',1,2,0),(3,'is work?','salut',1,2,0),(4,'dgh','ng',1,2,0),(5,'test pour voir si ca disparait','',1,2,0),(6,'tecouleur','',4,NULL,0),(7,'tecouleur','',4,NULL,0);
/*!40000 ALTER TABLE `Evenement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Famille`
--

DROP TABLE IF EXISTS `Famille`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Famille` (
  `idFamille` int(11) NOT NULL AUTO_INCREMENT,
  `nomFamille` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`idFamille`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Famille`
--

LOCK TABLES `Famille` WRITE;
/*!40000 ALTER TABLE `Famille` DISABLE KEYS */;
INSERT INTO `Famille` VALUES (1,'test'),(3,'Nouvelle famille'),(4,'B');
/*!40000 ALTER TABLE `Famille` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Personne`
--

DROP TABLE IF EXISTS `Personne`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Personne` (
  `idPersonne` int(11) NOT NULL AUTO_INCREMENT,
  `nomPersonne` varchar(255) NOT NULL,
  `prenomPersonne` varchar(255) NOT NULL,
  `dateNaiss` date DEFAULT NULL,
  `nbPoints` int(11) DEFAULT '0',
  `idFamille` int(11) DEFAULT NULL,
  `mdp` varchar(100) DEFAULT NULL,
  `estAdmin` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`idPersonne`),
  KEY `Personne_Famille` (`idFamille`),
  CONSTRAINT `Personne_Famille` FOREIGN KEY (`idFamille`) REFERENCES `Famille` (`idFamille`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Personne`
--

LOCK TABLES `Personne` WRITE;
/*!40000 ALTER TABLE `Personne` DISABLE KEYS */;
INSERT INTO `Personne` VALUES (1,'1','membre','2018-08-06',80,1,'bitebite',0),(2,',jnhfy','fghj','2018-08-06',0,1,'fgh',0),(3,'Dambois','Pierre','2018-08-10',0,1,'bitebite',1),(4,'MEMBRE','1ER','2018-08-11',0,4,'bitebite',1),(5,'test','autre admin','2018-08-11',0,1,'bitebite',1),(6,'ien','jul','2018-08-11',720,4,'bitebite',0),(7,'rard','Ge','2018-08-11',0,4,'bitebite',0),(8,'Phie','So','2018-08-11',0,4,'bitebite',0);
/*!40000 ALTER TABLE `Personne` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Tache`
--

DROP TABLE IF EXISTS `Tache`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Tache` (
  `idTache` int(11) NOT NULL AUTO_INCREMENT,
  `nomTache` varchar(500) NOT NULL,
  `nbPointsTache` int(11) NOT NULL,
  `nbPointsTransfert` int(11) NOT NULL,
  `estRecurrente` tinyint(1) DEFAULT NULL,
  `idPersonne` int(11) DEFAULT NULL,
  `idFamille` int(11) DEFAULT NULL,
  PRIMARY KEY (`idTache`),
  KEY `Tache_Personne` (`idPersonne`),
  KEY `idFamille` (`idFamille`),
  CONSTRAINT `Tache_Personne` FOREIGN KEY (`idPersonne`) REFERENCES `Personne` (`idPersonne`),
  CONSTRAINT `Tache_ibfk_1` FOREIGN KEY (`idFamille`) REFERENCES `Famille` (`idFamille`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Tache`
--

LOCK TABLES `Tache` WRITE;
/*!40000 ALTER TABLE `Tache` DISABLE KEYS */;
INSERT INTO `Tache` VALUES (2,'aze',0,0,0,1,1),(3,'testPourTruc',1,1,0,2,1),(4,'tacheLibre',0,0,0,1,1),(5,'steak',0,0,0,1,1),(6,'Stp',15,80,0,1,1),(7,'Re',10,20,0,1,1),(8,'testLibre',10,20,0,1,1),(9,'relibre',0,0,0,1,1),(10,'libre',0,0,0,1,1),(11,'popol',0,0,0,1,1),(12,'librelibre',0,0,0,1,1),(13,'free',0,0,0,1,1),(14,'Test pour transfert',20,10,0,7,4),(15,'Test sans transfert',0,2000,0,6,4),(16,'Steaksteaktransfer',0,0,0,8,4),(17,'TestForTransfer',10,10,0,7,4),(18,'useThis',10,100,0,8,4),(20,'Tagliate',0,0,0,6,4);
/*!40000 ALTER TABLE `Tache` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Utilisateur`
--

DROP TABLE IF EXISTS `Utilisateur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Utilisateur` (
  `idUtilisateur` int(11) NOT NULL AUTO_INCREMENT,
  `adresseMail` varchar(255) NOT NULL,
  `mdpUtilisateur` blob NOT NULL,
  `idFamille` int(11) DEFAULT NULL,
  PRIMARY KEY (`idUtilisateur`),
  UNIQUE KEY `adresseMail` (`adresseMail`),
  KEY `idFamille` (`idFamille`),
  CONSTRAINT `Utilisateur_ibfk_1` FOREIGN KEY (`idFamille`) REFERENCES `Famille` (`idFamille`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Utilisateur`
--

LOCK TABLES `Utilisateur` WRITE;
/*!40000 ALTER TABLE `Utilisateur` DISABLE KEYS */;
INSERT INTO `Utilisateur` VALUES (1,'steffi@baugnies.be','$2b$12$tszZRJ9mtNqUEvVp7lU7ku1DdklYeZYc8zSdPpAR91Jk1gsCcn03K',1),(3,'a@a.a','$2b$12$NHhKwqtwkghUegzeq2sI8.ywEj2.XzHvKjXLF5V5IhwXv8/dWzM.W',3),(4,'b@b.b','$2b$12$OUzNDXXroRXZO9ZOzs1hnOEXnIMrk8rFP2NWI6ZatwjQbM.CHFWni',4);
/*!40000 ALTER TABLE `Utilisateur` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'db_SharedEffort'
--

--
-- Dumping routines for database 'db_SharedEffort'
--
/*!50003 DROP PROCEDURE IF EXISTS `proc_addEvent` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_addEvent`(IN eventName varchar(100), IN eventDesc varchar(300), IN eventDate datetime, IN famId int, IN persId int, IN recu tinyint)
BEGIN
	INSERT INTO Evenement(nomEvenement, descriptionEvenement, idFamille, idPersonne, estRecurrent)
    VALUES(eventName, eventDesc, famId, persId, recu); 
    
    set @id = LAST_INSERT_ID(); 
        
        INSERT INTO DateEvenement(dateEvenement, idEvenement)
        VALUES(eventDate, @id); 
        
	select ROW_COUNT(); 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_addFamilyMember` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_addFamilyMember`(IN fname varchar(50), IN lname varchar(50), IN bd datetime, IN pswd varchar(50), IN isAdmin tinyint, IN famId int)
BEGIN
	INSERT INTO Personne(prenomPersonne, nomPersonne, dateNaiss, mdp, estAdmin, idFamille)
    VALUES(fname, lname, bd, pswd, isAdmin, famId); 

	SELECT ROW_COUNT(); 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_addTask` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_addTask`(IN tname varchar(50), IN points int, IN tpoints int,IN isRecu tinyint, IN persId int, IN famId int, IN tdate datetime)
BEGIN
	insert into Tache(nomTache, nbPointsTache, nbPointsTransfert, estRecurrente, idPersonne, idFamille) 
	values(tname, points, tpoints, isRecu, persId, famId);

	set @id = LAST_INSERT_ID(); 

	insert into DateTache(dateTache, idTache)
	values(tdate, @id); 
    
    select ROW_COUNT();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_checkAdminPassword` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_checkAdminPassword`(IN famId int, IN pswd varchar(100))
BEGIN
	SELECT * from Famille 
    where idFamille = famId 
    and mdpAdmin = pswd; 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_claimTask` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_claimTask`(IN persId int, IN taskId int)
BEGIN
	UPDATE Tache
    SET idPersonne = persId
    WHERE idTache = taskId;
	SELECT ROW_COUNT(); 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_deleteTask` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_deleteTask`(IN taskId int)
BEGIN
	DELETE FROM DateTache 
    WHERE idTache = taskId; 
    
    DELETE FROM Tache 
    WHERE idTache = taskId; 
    
    SELECT ROW_COUNT(); 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_getAllEventsFromFamily` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_getAllEventsFromFamily`(IN famId int)
BEGIN
    SELECT e.idEvenement, nomEvenement, descriptionEvenement, e.idPersonne, estRecurrent, dateEvenement
    From Evenement as e join DateEvenement as de on e.idEvenement = de.idEvenement
    WHERE idFamille = famId; 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_getAllTasksFromFamily` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_getAllTasksFromFamily`(IN famId int)
BEGIN
	SELECT t.idTache, nomTache, nbPointsTache, nbPointsTransfert, estRecurrente, t.idPersonne, dateTache, statutTache
	FROM Tache as t join DateTache as dt on t.idTache = dt.idTache
    WHERE idFamille = famId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_getEventsFromFamily` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_getEventsFromFamily`(IN famId int)
BEGIN
	SELECT e.idEvenement, nomEvenement, descriptionEvenement, idPersonne, estRecurrent, dateEvenement
    FROM Evenement as e join DateEvenement as d on e.idEvenement = d.idEvenement
    WHERE idFamille = famId; 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_getEventsFromPerson` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_getEventsFromPerson`(IN famId int, IN fname varchar(100))
BEGIN
	SELECT e.idEvenement, nomEvenement, descriptionEvenement, idPersonne, estRecurrent, dateEvenement
    FROM Evenement as e
    join DateEvenement as d on e.idEvenement = d.idEvenement
    WHERE idFamille = famId
    AND idPersonne = (select idPersonne from Personne where idFamille = famId AND prenomPersonne = fname); 
  
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_getFamilyMembersInfo` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_getFamilyMembersInfo`(IN famId int)
BEGIN
	SELECT idPersonne, prenomPersonne, nomPersonne, dateNaiss, nbPoints, mdp, estAdmin 
    FROM Personne 
	WHERE idFamille = famId; 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_getFreeTasks` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_getFreeTasks`(IN famId int)
BEGIN
	SELECT t.idTache, nomTache, nbPointsTache, nbPointsTransfert, estRecurrente, idPersonne, dateTache, statutTache 
    FROM Tache as t 
    join DateTache as d on t.idTache = d.idTache
    WHERE idFamille = famId
    and idPersonne is null; 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_getTasksFromFamily` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_getTasksFromFamily`(IN famId int)
BEGIN
	SELECT t.idTache, nomTache, nbPointsTache, nbPointsTransfert, estRecurrente, idPersonne, dateTache, statutTache
    FROM Tache as t join DateTache as d on t.idTache = d.idTache
    WHERE idFamille = famId; 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_getTasksFromPerson` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_getTasksFromPerson`(IN famId int, IN fName varchar(100))
BEGIN
	SELECT t.idTache, nomTache, nbPointsTache, nbPointsTransfert, estRecurrente, idPersonne, dateTache, statutTache 
    FROM Tache as t 
    join DateTache as d on t.idTache = d.idTache 
    WHERE idFamille = famId
    AND idPersonne in (select idPersonne from Personne where idFamille = famId AND prenomPersonne = fname) 
    AND t.idTache = d.idTache; 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_getUserPasswordHash` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_getUserPasswordHash`(IN userMailAddress varchar(60))
BEGIN
	SELECT  idUtilisateur, mdpUtilisateur, idFamille
    FROM Utilisateur  natural join Famille 
    WHERE adresseMail = userMailAddress;
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_register` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_register`(IN mail varchar(60), IN pswd varchar(120), IN familyName varchar(100))
BEGIN
    INSERT INTO Famille(nomFamille)
    VALUES(familyName);
    
    set @id = LAST_INSERT_ID(); 
    
	INSERT INTO Utilisateur(adresseMail, mdpUtilisateur, idFamille)
    VALUES (mail, pswd, @id); 

    SELECT ROW_COUNT(); 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_requestValidation` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_requestValidation`(IN taskId int)
BEGIN
	UPDATE DateTache 
    SET statutTache = 1
    WHERE idTache = taskId; 
    
	SELECT ROW_COUNT(); 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_transferTask` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_transferTask`(IN taskId int, IN transferorId int, IN transfereeId int, IN substractPoints int)
BEGIN
	UPDATE Tache
    SET idPersonne = transfereeId
    WHERE idTache = taskId; 
    
    UPDATE Personne
    SET nbPoints = nbPoints - substractPoints
    WHERE idPersonne = transferorId; 
    
	SELECT ROW_COUNT(); 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_validateTask` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`steffi`@`%` PROCEDURE `proc_validateTask`(IN taskId int, IN pointsToAdd int, IN persId int)
BEGIN
    UPDATE Personne 
    SET nbPoints = nbPoints + pointsToAdd
    WHERE idPersonne = persId; 

	UPDATE DateTache 
    SET statutTache = 2
    WHERE idTache = taskId; 
    
    
    SELECT ROW_COUNT(); 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-08-14 16:30:58

