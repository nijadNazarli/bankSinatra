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
-- Table `banksinatra`.`BankingFee`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banksinatra`.`BankingFee` (
    `percentage` DECIMAL(15,0) NOT NULL,
    PRIMARY KEY (`percentage`));


-- -----------------------------------------------------
-- Table `banksinatra`.`Crypto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banksinatra`.`Crypto` (
    `cryptoID` INT NOT NULL,
    `symbol` VARCHAR(10) NOT NULL,
    `exchangeRate` DECIMAL(25) NOT NULL,
    `description` VARCHAR(150) NOT NULL,
    `name` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`cryptoID`));


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
    `role` VARCHAR(45) NOT NULL,
    `isBlocked` TINYINT NOT NULL,
    `firstName` VARCHAR(45) NOT NULL,
    `prefix` VARCHAR(25),
    `lastName` VARCHAR(100) NOT NULL,
    `street` VARCHAR(100) NULL,
    `houseNumber` INT NULL,
    `houseNumberExtension` VARCHAR(25) NULL,
    `zipCode` VARCHAR(25) NULL,
    `city` VARCHAR(45) NULL,
    `bsn` INT NULL,
    `dateOfBirth` DATE NULL,
    PRIMARY KEY (`userID`));


-- -----------------------------------------------------
-- Table `banksinatra`.`Account`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banksinatra`.`Account` (
    `accountID` INT NOT NULL AUTO_INCREMENT,
    `IBAN` VARCHAR(45) NOT NULL,
    `balance` DECIMAL(10,0) NOT NULL,
    `userID` INT NOT NULL,
    PRIMARY KEY (`accountID`),
    INDEX `fk_Account_User1_idx` (`userID` ASC) VISIBLE,
    CONSTRAINT `fk_Account_User1`
    FOREIGN KEY (`userID`)
    REFERENCES `banksinatra`.`User` (`userID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `banksinatra`.`Transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banksinatra`.`Transaction` (
    `transactionID` INT NOT NULL,
    `date` DATETIME NOT NULL,
    `units` DECIMAL(25,0) NOT NULL,
    `exchangeRate` DECIMAL(25) NOT NULL,
    `bankingFee` DECIMAL(15,0) NOT NULL,
    `accountID_buyer` INT NOT NULL,
    `accountID_seller` INT NOT NULL,
    `cryptoID` INT NOT NULL,
    PRIMARY KEY (`transactionID`),
    INDEX `fk_Transaction_Account1_idx` (`accountID_buyer` ASC) VISIBLE,
    INDEX `fk_Transaction_Account2_idx` (`accountID_seller` ASC) VISIBLE,
    INDEX `fk_Transaction_Crypto1_idx` (`cryptoID` ASC) VISIBLE,
    CONSTRAINT `fk_Transaction_Account1`
    FOREIGN KEY (`accountID_buyer`)
    REFERENCES `banksinatra`.`Account` (`accountID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_Transaction_Account2`
    FOREIGN KEY (`accountID_seller`)
    REFERENCES `banksinatra`.`Account` (`accountID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_Transaction_Crypto1`
    FOREIGN KEY (`cryptoID`)
    REFERENCES `banksinatra`.`Crypto` (`cryptoID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `banksinatra`.`Asset`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banksinatra`.`Asset` (
    `accountID` INT NOT NULL,
    `cryptoID` INT NOT NULL,
    `units` DECIMAL(10) NOT NULL,
    PRIMARY KEY (`accountID`, `cryptoID`),
    INDEX `fk_Account_has_Crypto_Crypto1_idx` (`cryptoID` ASC) VISIBLE,
    INDEX `fk_Account_has_Crypto_Account1_idx` (`accountID` ASC) VISIBLE,
    CONSTRAINT `fk_Account_has_Crypto_Account1`
    FOREIGN KEY (`accountID`)
    REFERENCES `banksinatra`.`Account` (`accountID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_Account_has_Crypto_Crypto1`
    FOREIGN KEY (`cryptoID`)
    REFERENCES `banksinatra`.`Crypto` (`cryptoID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


CREATE TABLE IF NOT EXISTS `banksinatra`.`Token` (
    `token` VARCHAR(60) NOT NULL,
    `dateTime` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`token`));

-- Gebruiker definiëren en toegang verlenen
CREATE USER 'cursist'@'localhost' IDENTIFIED BY 'cohort';
GRANT ALL PRIVILEGES ON `banksinatra` . * TO 'cursist'@'localhost';