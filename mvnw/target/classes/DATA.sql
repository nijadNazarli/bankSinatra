INSERT INTO Crypto (symbol, description, name) VALUES
('BTC', 'De eerste, bekendste cryptomunt.', 'Bitcoin'),
('ETH', 'Een cryptomunt.', 'Ethereum'),
('ADA', 'Een cryptomunt.', 'Cardano'),
('BNB', 'Een cryptomunt.', 'Binance Coin'),
('USDT', 'Een 1 op 1 aan de dollar gelinkte cryptomunt.', 'Tether'),
('XRP', 'Een cryptomunt.', 'XRP'),
('DOGE', 'Een cryptomunt.', 'Dogecoin'),
('SOL', 'Een cryptomunt.', 'Solana'),
('DOT', 'Een cryptomunt.', 'Polkadot'),
('USDC', 'Een 1 op 1 aan de dollar gelinkte cryptomunt.', 'USD Coin'),
('UNI', 'Een cryptomunt.', 'Uniswap'),
('LINK', 'Een cryptomunt.', 'Chainlink'),
('LUNA', 'Een cryptomunt.', 'Terra'),
('BCH', 'Een cryptomunt.', 'Bitcoin Cash'),
('BUSD', 'Een 1 op 1 aan de dollar gelinkte cryptomunt.', 'Binance USD'),
('LTC', 'Een cryptomunt.', 'Litecoin'),
('ICP', 'Een cryptomunt.', 'Internet Computer'),
('WBTC', 'Een cryptomunt.', 'Wrapped Bitcoin'),
('MATIC', 'Een cryptomunt.', 'Polygon'),
('VET', 'Een cryptomunt.', 'VeChain');

INSERT INTO `Asset` VALUES (1,'ADA',3617.10729350, 3617.10729350, 0),(1,'BCH',3237.97825746, 3237.97825746, 0),(1,'BNB',4446.80050983, 4446.80050983, 0),
                           (1,'BTC',1467.46154488, 1467.46154488, 0),(1,'BUSD',2907.71188934, 2907.71188934, 0),(1,'DOGE',4165.35024824, 4165.35024824, 0),
                           (1,'DOT',4299.78222044, 4299.78222044, 0),(1,'ETH',4953.59009790, 4953.59009790, 0),(1,'ICP',4204.44776219, 4204.44776219, 0),
                           (1,'LINK',3270.64077791, 3270.64077791, 0),(1,'LTC',4452.54613758, 4452.54613758, 0),(1,'LUNA',2664.79656423, 2664.79656423, 0),
                           (1,'MATIC',4202.83711257, 4202.83711257, 0),(1,'SOL',4443.27804342, 4443.27804342, 0),(1,'UNI',3251.06460353, 3251.06460353, 0),
                           (1,'USDC',1795.14359490, 1795.14359490, 0),(1,'USDT',1520.26752852, 1520.26752852, 0),(1,'VET',1989.28679912, 1989.28679912, 0),
                           (1,'WBTC',1514.80989533, 1514.80989533, 0),(1,'XRP',3150.50238923, 3150.50238923, 0);

INSERT INTO BankingFee (percentage) VALUES (0.01);

-- Add crypto base prices
INSERT INTO CryptoPrice (symbol, cryptoPrice, dateRetrieved) VALUES ('BTC', 43516.86, current_timestamp());
INSERT INTO CryptoPrice (symbol, cryptoPrice, dateRetrieved) VALUES ('BTC', 42162.03, DATE_ADD(current_timestamp, INTERVAL -1 HOUR));
INSERT INTO CryptoPrice (symbol, cryptoPrice, dateRetrieved) VALUES ('BTC', 42100.03, DATE_ADD(current_timestamp, INTERVAL -1 DAY));
INSERT INTO CryptoPrice (symbol, cryptoPrice, dateRetrieved) VALUES ('BTC', 34100.01, DATE_ADD(current_timestamp, INTERVAL -1 MONTH));
INSERT INTO CryptoPrice (symbol, cryptoPrice, dateRetrieved) VALUES ('ETH', 3184.29, current_timestamp());
INSERT INTO CryptoPrice (symbol, cryptoPrice, dateRetrieved) VALUES ('ETH', 3500.68, DATE_ADD(current_timestamp, INTERVAL -1 HOUR));
INSERT INTO CryptoPrice (symbol, cryptoPrice, dateRetrieved) VALUES ('ETH', 3400.90, DATE_ADD(current_timestamp, INTERVAL -1 DAY));
INSERT INTO CryptoPrice (symbol, cryptoPrice, dateRetrieved) VALUES ('ETH', 2900.25, DATE_ADD(current_timestamp, INTERVAL -1 MONTH));
INSERT INTO CryptoPrice (symbol, cryptoPrice, dateRetrieved) VALUES ('ADA', 2.56, current_timestamp());
INSERT INTO CryptoPrice (symbol, cryptoPrice, dateRetrieved) VALUES ('ADA', 2.00, DATE_ADD(current_timestamp, INTERVAL -1 HOUR));
INSERT INTO CryptoPrice (symbol, cryptoPrice, dateRetrieved) VALUES ('ADA', 1.99, DATE_ADD(current_timestamp, INTERVAL -1 DAY));
INSERT INTO CryptoPrice (symbol, cryptoPrice, dateRetrieved) VALUES ('ADA', 1.78, DATE_ADD(current_timestamp, INTERVAL -1 MONTH));

-- Create two dummy clients and accounts
INSERT INTO User (email, password, salt, userRole, isBlocked, firstName, prefix, lastName, street, houseNumber, houseNumberExtension, zipCode, city, bsn, dateOfBirth)
VALUES ('test1@test.com', 'e4e58a62d41c2bb6688470bcb8cac026dcb51fab3ba9c94f999b36a30ebff523', 'e9442010705a4ae29d3d57f83810fe40', 'client', '0', 'Abra', 'van', 'Cadabra', 'Bibbedie', '10', 'E', '1102AB', 'Amsterdam', '123456782', '2000-08-11');
INSERT INTO Account (accountID, IBAN, balance, userID) VALUES ('2','NL67BSIN3890518477','10000.00','2');
INSERT INTO User (email, password, salt, userRole, isBlocked, firstName, prefix, lastName, street, houseNumber, houseNumberExtension, zipCode, city, bsn, dateOfBirth)
VALUES ('test2@test.com', '7f69c1d1c98be26fd9acf05acb7faa06bdb1aaefc976c29a3fffe8d309f690fe', '484a083720c3bd538100669378d5460d', 'client', '0', 'lala', 'van', 'BoB', 'plein 1945', '10', 'E', '1107AW', 'Amsterdam', '405886408', '2000-08-11');
INSERT INTO Account (accountID, IBAN, balance, userID) VALUES ('3', 'NL24BSIN5459628412', '10000.00', '3');

-- Give dummy client some assets to test
INSERT INTO Asset VALUES (2, 'ADA', 1000, 0, 0);
INSERT INTO Asset VALUES (2, 'BTC', 9, 0, 0);
INSERT INTO Asset VALUES (2, 'ETH', 10.25, 0, 0);

-- Dummy user 2 buys
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 3, 3000.0, 10.0, 2, 3, 'BTC');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 4, 3000.0, 10.0, 2, 3, 'BTC');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 5, 3000.0, 10.0, 2, 3, 'BTC');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 10.25, 2900.0, 10.0, 2, 3, 'ETH');
-- Maand geleden gekocht
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp, INTERVAL -15 DAY), 3, 25.34, 10.0, 2, 3, 'BTC');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp, INTERVAL -2 MONTH), 3, 25.34, 10.0, 2, 3, 'BTC');

-- Dummy user 2 sells
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 2, 2500.0, 10.0, 3, 2, 'BTC');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 1, 2500.0, 10.0, 3, 2, 'BTC');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 3, 2500.0, 10.0, 3, 2, 'BTC');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 1000, 2.00, 10.0, 3, 2, 'ADA');
-- Maand geleden verkocht
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp(), INTERVAL -15 DAY), 1000, 2.00, 10.0, 3, 2, 'ADA');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp(), INTERVAL -15 DAY), 20, 2900.00, 10.0, 3, 2, 'ETH');