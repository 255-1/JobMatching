create database if not exists ipproxy;
create database if not exists job;
use job;
CREATE TABLE IF NOT EXISTS`jobinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `jobName` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `company` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `address` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `salary` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `date` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `exp` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `edu` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `offerNumber` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `jobInfo` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `companyType` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `staffNumber` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `companyOrientation` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `jobURL` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `unifyName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `jobName_company_date_uniq` (`jobName`,`company`,`date`)
) ENGINE=InnoDB AUTO_INCREMENT=112625 DEFAULT CHARSET=utf8;
use ipproxy;
CREATE TABLE if not exists `proxypool` (
  `IPAddress` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `IPPort` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `IPType` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `IPSpeed` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`IPAddress`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
