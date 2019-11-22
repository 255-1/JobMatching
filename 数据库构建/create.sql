use big_data;

CREATE TABLE `jobinfo` (
  `jobName` char(6) NOT NULL,
  `company` varchar(30) NOT NULL,
  `address` char(10) DEFAULT NULL,
  `salary` char(10) DEFAULT NULL,
  `date` char(10) DEFAULT NULL,
  `exp` char(10) DEFAULT NULL,
  `edu` char(2) DEFAULT NULL,
  `offerNumber` char(4) DEFAULT NULL,
  `jobInfo` char(50) DEFAULT NULL,
  `companyType` char(10) DEFAULT NULL,
  `staffNumber` char(10) DEFAULT NULL,
  `companyOrientation` char(5) DEFAULT NULL,
  PRIMARY KEY (`jobName`,`company`)
) 