-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        8.0.38 - MySQL Community Server - GPL
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- 导出 hrl_transaction 的数据库结构
CREATE DATABASE IF NOT EXISTS `hrl_transaction` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `hrl_transaction`;

-- 导出  表 hrl_transaction.order 结构
CREATE TABLE IF NOT EXISTS `order` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `orderId` bigint NOT NULL,
  `clientOrderId` varchar(256) NOT NULL,
  `symbol` varchar(256) NOT NULL,
  `cumQuote` decimal(45,20) DEFAULT NULL,
  `executedQty` decimal(45,20) DEFAULT NULL,
  `avgPrice` decimal(45,20) DEFAULT NULL,
  `origQty` decimal(45,20) DEFAULT NULL,
  `price` decimal(45,20) DEFAULT NULL,
  `reduceOnly` int DEFAULT NULL,
  `side` varchar(50) NOT NULL,
  `positionSide` varchar(50) DEFAULT NULL,
  `status` varchar(50) NOT NULL,
  `stopPrice` decimal(45,20) DEFAULT NULL,
  `closePosition` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `timeInForce` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `origType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `activationPrice` decimal(45,20) DEFAULT NULL,
  `priceRate` decimal(45,20) DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `workingType` varchar(50) DEFAULT NULL,
  `priceProtect` varchar(50) DEFAULT NULL,
  `lastExecutedQty` decimal(45,20) DEFAULT NULL,
  `platform` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `symbolThirdAvgPriceWhenCreatedOrder` decimal(45,20) NOT NULL,
  `symbolThird` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `symbolThirdPlatform` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `symbolOpposite` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `symbolOppositePlatform` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `isParent` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `parentOrderId` bigint NOT NULL,
  `gridRatio` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 正在导出表  hrl_transaction.order 的数据：~3 rows (大约)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
