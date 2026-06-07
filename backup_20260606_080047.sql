-- MySQL dump 10.13  Distrib 5.6.25, for Win32 (x86)
--
-- Host: localhost    Database: gestion_psicologia
-- ------------------------------------------------------
-- Server version	5.6.25

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
-- Table structure for table `historia_clinica`
--

DROP TABLE IF EXISTS `historia_clinica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `historia_clinica` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `paciente_id` bigint(20) DEFAULT NULL,
  `fecha_creacion` date NOT NULL,
  `resumen_clinico` longtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `paciente_id` (`paciente_id`),
  CONSTRAINT `fk_historia_paciente` FOREIGN KEY (`paciente_id`) REFERENCES `pacientes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historia_clinica`
--

LOCK TABLES `historia_clinica` WRITE;
/*!40000 ALTER TABLE `historia_clinica` DISABLE KEYS */;
INSERT INTO `historia_clinica` VALUES (1,3,'2026-05-05','<p class=\"ql-align-justify\">Historia&nbsp;Clínica&nbsp;Psicológica&nbsp;Identificación:&nbsp;Paciente&nbsp;adulto,&nbsp;género&nbsp;no&nbsp;especificado,&nbsp;consulta&nbsp;por&nbsp;sintomatología&nbsp;ansiosa.&nbsp;Motivo&nbsp;de&nbsp;Consulta:&nbsp;Síndrome&nbsp;de&nbsp;ansiedad&nbsp;generalizada,&nbsp;caracterizado&nbsp;por&nbsp;tensión&nbsp;persistente&nbsp;e&nbsp;impaciencia&nbsp;exacerbada&nbsp;ante&nbsp;situaciones&nbsp;de&nbsp;incertidumbre&nbsp;o&nbsp;variabilidad&nbsp;en&nbsp;eventos&nbsp;cotidianos.&nbsp;Refiere&nbsp;estrés&nbsp;elevado&nbsp;al&nbsp;momento&nbsp;de&nbsp;la&nbsp;consulta.&nbsp;Evaluación&nbsp;Inicial:&nbsp;-&nbsp;Síntomas&nbsp;Emocionales:&nbsp;Predomina&nbsp;la&nbsp;hipervigilancia&nbsp;y&nbsp;anticipación&nbsp;catastrófica,&nbsp;con&nbsp;dificultad&nbsp;para&nbsp;modular&nbsp;respuestas&nbsp;ante&nbsp;estímulos&nbsp;ambiguos.&nbsp;-&nbsp;Síntomas&nbsp;Conductuales:&nbsp;Impaciencia&nbsp;marcada,&nbsp;posible&nbsp;evitación&nbsp;sutil&nbsp;de&nbsp;contextos&nbsp;impredecibles.&nbsp;-&nbsp;Patrones&nbsp;Detectados:&nbsp;Reactividad&nbsp;emocional&nbsp;desproporcionada&nbsp;frente&nbsp;a&nbsp;cambios&nbsp;mínimos&nbsp;en&nbsp;el&nbsp;entorno,&nbsp;sugiriendo&nbsp;rigidez&nbsp;cognitiva&nbsp;y&nbsp;baja&nbsp;tolerancia&nbsp;a&nbsp;la&nbsp;incertidumbre.&nbsp;Evolución:&nbsp;El&nbsp;cuadro&nbsp;se&nbsp;mantiene&nbsp;estable&nbsp;en&nbsp;intensidad,&nbsp;sin&nbsp;exacerbaciones&nbsp;agudas&nbsp;pero&nbsp;con&nbsp;cronificación&nbsp;de&nbsp;la&nbsp;sintomatología.&nbsp;No&nbsp;se&nbsp;observan&nbsp;mecanismos&nbsp;adaptativos&nbsp;consolidados;&nbsp;la&nbsp;tensión&nbsp;persiste&nbsp;como&nbsp;estado&nbsp;basal.&nbsp;Observaciones&nbsp;Clínicas:&nbsp;La&nbsp;ansiedad&nbsp;parece&nbsp;vinculada&nbsp;a&nbsp;expectativas&nbsp;rígidas&nbsp;sobre&nbsp;el&nbsp;control&nbsp;ambiental.&nbsp;La&nbsp;ausencia&nbsp;de&nbsp;variabilidad&nbsp;sintomá<strong>tica.&nbsp;Además&nbsp;le&nbsp;molesta&nbsp;la&nbsp;impuntualidad&nbsp;de&nbsp;la&nbsp;gente.&nbsp;Además&nbsp;refiere&nbsp;a&nbsp;situaciones&nbsp;complicadas&nbsp;socialmente.</strong></p><p class=\"ql-align-justify\"></p><p class=\"ql-align-justify\"></p><p class=\"ql-align-justify\"></p><p class=\"ql-align-justify\"></p>'),(6,2,'2026-05-07','<p>Historia&nbsp;Clínica&nbsp;Psicológica&nbsp;<strong><em>Identificación:</em></strong>&nbsp;Consulta&nbsp;por&nbsp;primera&nbsp;vez&nbsp;por&nbsp;sintomatología&nbsp;ansiosa.&nbsp;Motivo&nbsp;de&nbsp;Consulta:&nbsp;El&nbsp;paciente&nbsp;refiere&nbsp;&quot;sentirme&nbsp;constantemente&nbsp;nerviosa,&nbsp;con&nbsp;preocupaciones&nbsp;excesivas&nbsp;y&nbsp;dificultad&nbsp;para&nbsp;controlarlas&quot;.&nbsp;Describe&nbsp;tensión&nbsp;muscular,&nbsp;insomnio&nbsp;de&nbsp;conciliación&nbsp;y&nbsp;fatiga&nbsp;persistente.&nbsp;Los&nbsp;síntomas&nbsp;se&nbsp;exacerban&nbsp;en&nbsp;contextos&nbsp;laborales&nbsp;y&nbsp;sociales.&nbsp;Evaluación&nbsp;Inicial:&nbsp;-&nbsp;Cognitivo:&nbsp;Patrón&nbsp;de&nbsp;pensamiento&nbsp;rumiativo,&nbsp;focalizado&nbsp;en&nbsp;errores&nbsp;pasados&nbsp;y&nbsp;anticipación&nbsp;catastrófica&nbsp;del&nbsp;futuro.&nbsp;-&nbsp;Emocional:&nbsp;Labilidad&nbsp;afectiva,&nbsp;con&nbsp;episodios&nbsp;de&nbsp;irritabilidad&nbsp;y&nbsp;llanto&nbsp;espontáneo.&nbsp;Menciona&nbsp;sentimientos&nbsp;de&nbsp;impotencia.&nbsp;-&nbsp;Conductual:&nbsp;Evitación&nbsp;progresiva&nbsp;de&nbsp;reuniones&nbsp;sociales&nbsp;y&nbsp;sobrecompensación&nbsp;mediante&nbsp;perfeccionismo&nbsp;laboral.&nbsp;-&nbsp;Fisiológico:&nbsp;Taquicardia&nbsp;episódica,&nbsp;sudoración&nbsp;palmar&nbsp;y&nbsp;cefaleas&nbsp;tensionales.&nbsp;Evolución:&nbsp;En&nbsp;seguimiento&nbsp;a&nbsp;3&nbsp;meses,&nbsp;persiste&nbsp;la&nbsp;sintomatología&nbsp;ansiosa,&nbsp;aunque&nbsp;con&nbsp;mayor&nbsp;conciencia&nbsp;de&nbsp;sus&nbsp;desencadenantes.&nbsp;Se&nbsp;observa&nbsp;leve&nbsp;mejoría&nbsp;en&nbsp;la&nbsp;regulación&nbsp;emocional&nbsp;mediante&nbsp;técnicas&nbsp;de&nbsp;grounding,&nbsp;pero&nbsp;dificultad&nbsp;para&nbsp;implementar&nbsp;estrategias&nbsp;a.</p>'),(7,4,'2026-05-12','<p><strong><em><u>Historia&nbsp;Clínica&nbsp;</u></em></strong>Psicológica&nbsp;Identificación:&nbsp;Paciente&nbsp;femenina,&nbsp;edad&nbsp;no&nbsp;especificada,&nbsp;acude&nbsp;a&nbsp;consulta&nbsp;por&nbsp;primera&nbsp;vez.&nbsp;Motivo&nbsp;de&nbsp;Consulta:&nbsp;La&nbsp;paciente&nbsp;refiere&nbsp;sintomatología&nbsp;asociada&nbsp;a&nbsp;estrés&nbsp;permanente,&nbsp;caracterizado&nbsp;por&nbsp;insomnio&nbsp;y&nbsp;cansancio&nbsp;crónico.&nbsp;No&nbsp;se&nbsp;describen&nbsp;antecedentes&nbsp;médicos&nbsp;o&nbsp;psiquiátricos&nbsp;previos.&nbsp;Evaluación&nbsp;Inicial:&nbsp;Se&nbsp;observa&nbsp;un&nbsp;patrón&nbsp;de&nbsp;activación&nbsp;psicofisiológica&nbsp;persistente,&nbsp;compatible&nbsp;con&nbsp;estrés&nbsp;crónico.&nbsp;Los&nbsp;indicadores&nbsp;de&nbsp;insomnio&nbsp;sugieren&nbsp;alteraciones&nbsp;en&nbsp;el&nbsp;ritmo&nbsp;sueño-vigilia,&nbsp;posiblemente&nbsp;vinculadas&nbsp;a&nbsp;hiperactivación&nbsp;cognitiva&nbsp;o&nbsp;emocional.&nbsp;El&nbsp;cansancio&nbsp;crónico&nbsp;puede&nbsp;ser&nbsp;secundario&nbsp;a&nbsp;la&nbsp;falta&nbsp;de&nbsp;descanso&nbsp;reparador&nbsp;y/o&nbsp;a&nbsp;la&nbsp;sobrecarga&nbsp;psíquica.&nbsp;No&nbsp;se&nbsp;reportan&nbsp;síntomas&nbsp;depresivos&nbsp;o&nbsp;ansiosos&nbsp;adicionales&nbsp;en&nbsp;esta&nbsp;fase.&nbsp;Evolución&nbsp;Emocional&nbsp;y&nbsp;Conductual:&nbsp;La&nbsp;paciente&nbsp;presenta&nbsp;dificultad&nbsp;en&nbsp;la&nbsp;regulación&nbsp;emocional,&nbsp;con&nbsp;predominio&nbsp;de&nbsp;tensión&nbsp;sostenida.&nbsp;La&nbsp;ausencia&nbsp;de&nbsp;estrategias&nbsp;adaptativas&nbsp;eficaces&nbsp;podría&nbsp;perpetuar&nbsp;el&nbsp;ciclo&nbsp;estrés-insomnio-fatiga.&nbsp;No&nbsp;se&nbsp;identifican&nbsp;patrones&nbsp;de&nbsp;evitación&nbsp;o&nbsp;conductas&nbsp;compulsivas,&nbsp;pero&nbsp;la&nbsp;sintomatología&nbsp;actual&nbsp;sugiere&nbsp;un&nbsp;deterioro&nbsp;funcional&nbsp;significativo.&nbsp;Conclusiones&nbsp;C<strong>línicas.&nbsp;kdkkdkd</strong></p>');
/*!40000 ALTER TABLE `historia_clinica` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pacientes`
--

DROP TABLE IF EXISTS `pacientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pacientes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `dni` varchar(20) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `telefono` varchar(50) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `dni` (`dni`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pacientes`
--

LOCK TABLES `pacientes` WRITE;
/*!40000 ALTER TABLE `pacientes` DISABLE KEYS */;
INSERT INTO `pacientes` VALUES (2,'Adolfo','Perez','45678210',NULL,NULL,NULL,NULL),(3,'Juanjo','Conte','29292821','juanjo@mail.com','23892822',NULL,NULL),(4,'Carla','Andrade','34598366','sdfsdfsdf@jkl.lkj','332244',NULL,NULL),(5,'Luis','Zena','123456578','zzena@gmail.com','3777543891','Velazco 111','1956-05-12');
/*!40000 ALTER TABLE `pacientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `respaldos`
--

DROP TABLE IF EXISTS `respaldos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `respaldos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre_archivo` varchar(255) NOT NULL,
  `ruta_archivo` varchar(500) DEFAULT NULL,
  `tamano_archivo` bigint(20) DEFAULT NULL,
  `fecha_backup` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `usuario_id` bigint(20) DEFAULT NULL,
  `tipo_operacion` enum('BACKUP','RESTORE') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_respaldo_usuario` (`usuario_id`),
  CONSTRAINT `fk_respaldo_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `respaldos`
--

LOCK TABLES `respaldos` WRITE;
/*!40000 ALTER TABLE `respaldos` DISABLE KEYS */;
/*!40000 ALTER TABLE `respaldos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `turnos`
--

DROP TABLE IF EXISTS `turnos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `turnos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `paciente_id` bigint(20) NOT NULL,
  `fecha_hora` datetime NOT NULL,
  `estado` enum('AGENDADO','ASISTIO','AUSENTE','CANCELADO') DEFAULT NULL,
  `motivo` text,
  PRIMARY KEY (`id`),
  KEY `fk_turno_paciente` (`paciente_id`),
  CONSTRAINT `fk_turno_paciente` FOREIGN KEY (`paciente_id`) REFERENCES `pacientes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `turnos`
--

LOCK TABLES `turnos` WRITE;
/*!40000 ALTER TABLE `turnos` DISABLE KEYS */;
INSERT INTO `turnos` VALUES (2,4,'2026-04-30 13:07:00','ASISTIO',NULL),(7,3,'2026-05-05 11:10:00','ASISTIO',NULL),(11,4,'2026-05-03 09:00:00','ASISTIO',NULL),(12,3,'2026-05-04 09:00:00','ASISTIO',NULL),(13,2,'2026-05-11 09:00:00','AGENDADO',NULL),(14,2,'2026-05-27 09:00:00','AGENDADO',NULL);
/*!40000 ALTER TABLE `turnos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `rol` varchar(45) NOT NULL,
  `activo` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'admin','$2a$10$Y/8i5VX.TMllu8QFJPxXNucjBnPPu488NoUtZ1YCXMG/sKSpb4VWy','ADMIN',1),(2,'feli','$2a$10$6K3QMmwZngrbE.oSMjqeRuyv5nDnw925Ss374ots1lr3Jbj.kmLlS','PROFESIONAL',1),(3,'op1','$2a$10$mTR2JU8E93SRJwem4gIoIOEDaWNAZQMT2Ez0.53GKhPVlA0kcz6p6','OPERADOR',1),(6,'opp','$2a$10$gGlMCwbO8ezyavy0w2PeW.SNPTSSVK0xV78pUMr6UwaTRVYTOavnq','OPERADOR',1),(7,'admin2','$2a$10$DuUxN4/fylNnFVV9NyQTSOpyUxbnArJ6IQ8xqcIVm5VEy5EAEgRBe','ADMIN',0),(8,'admin3','$2a$10$mSTtW.cSJs8e70YHLva1ReytMBBB5wJnW8i7fr6DOIsiKkjeuoG4m','ADMIN',0),(9,'admin5','$2a$10$s5jcsCMNPDFn5PcxU0nZ1OswD3avXWk.hrsCXPEb.F187fjGhYglK','ADMIN',1),(10,'admin6','$2a$10$6LS1Z.CQoUAz9ZBpLseoIezJ5wjWGm.gghIgMAHL7fxyXVhOgdjmO','ADMIN',1),(11,'op2','$2a$10$B2Uqhl.xRtOQNK3wbybwb.FF4sWWr.ziQXT4Xt02qm.HkM1mUzs0O','OPERADOR',0);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `visitas`
--

DROP TABLE IF EXISTS `visitas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visitas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `historia_id` bigint(20) NOT NULL,
  `turno_id` bigint(20) DEFAULT NULL,
  `fecha` date NOT NULL,
  `observacion` longtext COLLATE utf8mb4_unicode_ci,
  `resumen_ia` longtext COLLATE utf8mb4_unicode_ci,
  `estado_ia` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo_visita` enum('PRIMERA_ENTREVISTA','SEGUIMIENTO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SEGUIMIENTO',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_visita_turno` (`turno_id`),
  KEY `fk_visita_historia` (`historia_id`),
  CONSTRAINT `fk_visita_historia` FOREIGN KEY (`historia_id`) REFERENCES `historia_clinica` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `visitas`
--

LOCK TABLES `visitas` WRITE;
/*!40000 ALTER TABLE `visitas` DISABLE KEYS */;
INSERT INTO `visitas` VALUES (35,7,2,'2026-04-30','<p>se&nbsp;presenta&nbsp;con&nbsp;indicadores&nbsp;de&nbsp;insomnio&nbsp;y&nbsp;cansancio&nbsp;crónico</p>','Insomnio y fatiga persistente.','OK','SEGUIMIENTO'),(49,1,12,'2026-05-04','<p>llega&nbsp;con&nbsp;alto&nbsp;nivel&nbsp;de&nbsp;estrés</p>','Estrés elevado manifiesto.','OK','SEGUIMIENTO'),(61,1,7,'2026-05-05','<p>sindrome&nbsp;de&nbsp;ansiedad&nbsp;generalizada,&nbsp;se&nbsp;manifiesta&nbsp;en&nbsp;estado&nbsp;de&nbsp;tensión&nbsp;y&nbsp;mucha&nbsp;impaciencia&nbsp;ante&nbsp;eventos&nbsp;que&nbsp;no&nbsp;siempre&nbsp;se&nbsp;presentan&nbsp;iguales.</p>','Trastorno de ansiedad con tensión persistente.','OK','SEGUIMIENTO'),(62,7,11,'2026-05-03','Esta es la primera visita de la paciente en la cual manifiesta una sintomatología de estrés permanente ','Síntomas de estrés persistente.','OK','SEGUIMIENTO');
/*!40000 ALTER TABLE `visitas` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-06  8:00:47
