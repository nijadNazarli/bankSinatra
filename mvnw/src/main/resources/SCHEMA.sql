-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema banksinatra
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `banksinatra` ;

-- -----------------------------------------------------
-- Schema banksinatra
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `banksinatra` ;
USE `banksinatra` ;

-- -----------------------------------------------------
-- Table `banksinatra`.`Role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banksinatra`.`Role` (
  `userRole` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`userRole`));


-- -----------------------------------------------------
-- Table `banksinatra`.`User`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banksinatra`.`User` (
  `userID` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `salt` VARCHAR(100) NOT NULL,
  `userRole` VARCHAR(45) NOT NULL,
  `isBlocked` TINYINT NOT NULL,
  `firstName` VARCHAR(45) NOT NULL,
  `prefix` VARCHAR(25) NULL DEFAULT NULL,
  `lastName` VARCHAR(100) NOT NULL,
  `street` VARCHAR(100) NULL DEFAULT NULL,
  `houseNumber` INT NULL DEFAULT NULL,
  `houseNumberExtension` VARCHAR(25) NULL DEFAULT NULL,
  `zipCode` VARCHAR(25) NULL DEFAULT NULL,
  `city` VARCHAR(45) NULL DEFAULT NULL,
  `bsn` INT NULL DEFAULT NULL,
  `dateOfBirth` DATE NULL DEFAULT NULL,
  PRIMARY KEY (`userID`),
  INDEX `fk_User_Role1_idx` (`userRole` ASC) VISIBLE,
  CONSTRAINT `fk_User_Role1`
    FOREIGN KEY (`userRole`)
    REFERENCES `banksinatra`.`Role` (`userRole`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `banksinatra`.`Account`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banksinatra`.`Account` (
  `accountID` INT NOT NULL AUTO_INCREMENT,
  `IBAN` VARCHAR(45) NOT NULL,
  `balance` DECIMAL(16,2) NOT NULL,
  `userID` INT NOT NULL,
  PRIMARY KEY (`accountID`),
  INDEX `fk_Account_User1_idx` (`userID` ASC) VISIBLE,
  CONSTRAINT `fk_Account_User1`
    FOREIGN KEY (`userID`)
    REFERENCES `banksinatra`.`User` (`userID`));


-- -----------------------------------------------------
-- Table `banksinatra`.`BankingFee`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banksinatra`.`BankingFee` (
  `percentage` DECIMAL(6,2) NOT NULL,
  PRIMARY KEY (`percentage`));


-- -----------------------------------------------------
-- Table `banksinatra`.`Crypto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banksinatra`.`Crypto` (
  `symbol` VARCHAR(10) NOT NULL,
  `description` VARCHAR(150) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`symbol`));


-- -----------------------------------------------------
-- Table `banksinatra`.`CryptoPrice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banksinatra`.`CryptoPrice` (
  `symbol` VARCHAR(10) NOT NULL,
  `cryptoPrice` DECIMAL(12,2) NOT NULL,
  `dateRetrieved` DATETIME NOT NULL,
  PRIMARY KEY (`symbol`, `dateRetrieved`),
  INDEX `fk_CryptoPrice_Crypto1_idx` (`symbol` ASC) VISIBLE,
  CONSTRAINT `fk_CryptoPrice_Crypto1`
    FOREIGN KEY (`symbol`)
    REFERENCES `banksinatra`.`Crypto` (`symbol`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- -----------------------------------------------------
-- Table `banksinatra`.`Transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banksinatra`.`Transaction` (
  `transactionID` INT NOT NULL AUTO_INCREMENT,
  `date` DATETIME NOT NULL,
  `units` DECIMAL(20,8) NOT NULL,
  `transactionPrice` DECIMAL(12,2) NOT NULL,
  `bankingFee` DECIMAL(6,2) NOT NULL,
  `accountID_buyer` INT NOT NULL,
  `accountID_seller` INT NOT NULL,
  `symbol` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`transactionID`),
  INDEX `fk_Transaction_Account1_idx` (`accountID_buyer` ASC) VISIBLE,
  INDEX `fk_Transaction_Account2_idx` (`accountID_seller` ASC) VISIBLE,
  INDEX `fk_Transaction_Crypto1_idx` (`symbol` ASC) VISIBLE,
  CONSTRAINT `fk_Transaction_Account1`
    FOREIGN KEY (`accountID_buyer`)
    REFERENCES `banksinatra`.`Account` (`accountID`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_Transaction_Account2`
    FOREIGN KEY (`accountID_seller`)
    REFERENCES `banksinatra`.`Account` (`accountID`),
  CONSTRAINT `fk_Transaction_Crypto1`
    FOREIGN KEY (`symbol`)
    REFERENCES `banksinatra`.`Crypto` (`symbol`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `banksinatra`.`Asset`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banksinatra`.`Asset` (
  `accountID` INT NOT NULL,
  `symbol` VARCHAR(10) NOT NULL,
  `units` DECIMAL(20,8) NOT NULL,
  `unitsForSale` DECIMAL (20,8) ,
  `salePrice` DECIMAL (12,2),
  PRIMARY KEY (`accountID`, `symbol`),
  INDEX `fk_Account_Crypto_Crypto1_idx` (`symbol` ASC) VISIBLE,
  INDEX `fk_Account_Crypto_Account1_idx` (`accountID` ASC) VISIBLE,
  CONSTRAINT `fk_Account_Crypto_Account1`
    FOREIGN KEY (`accountID`)
    REFERENCES `banksinatra`.`Account` (`accountID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Account_Crypto_Crypto1`
    FOREIGN KEY (`symbol`)
    REFERENCES `banksinatra`.`Crypto` (`symbol`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- Vaste basisrollen instellen
INSERT INTO Role VALUES('client');
INSERT INTO Role VALUES('admin');
INSERT INTO Role VALUES('bank');

-- Vaste Bank User instellen
INSERT INTO User(email, password, salt, userRole, isBlocked, firstName, prefix, lastName,
                 street, houseNumber, houseNumberExtension, zipCode, city, bsn, dateOfBirth)
VALUES ('', '', '', 'bank', 0, 'Bank', '', 'Sinatra', '', 0, '', '', '', 0, '2021-09-01');
INSERT INTO Account VALUES (1,'NL91BSIN9826496343',5000000.00,1);


-- Gebruiker definiëren en toegang verlenen
CREATE USER 'cursist'@'localhost' IDENTIFIED BY 'cohort';
GRANT ALL PRIVILEGES ON `banksinatra` . * TO 'cursist'@'localhost';

