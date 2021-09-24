INSERT INTO Crypto (symbol, description, name) VALUES
('BTC', 'Bitcoin (₿) is a decentralized digital currency, invented in 2008 by an unknown person or group of people using the name Satoshi Nakamoto.', 'Bitcoin'),
('ETH', 'Ether (ETH) is the native cryptocurrency of the Ethereum platform. Amongst cryptocurrencies, it is second only to Bitcoin in market capitalization.', 'Ethereum'),
('ADA', 'Cardano was founded in 2015 by Ethereum co-founder Charles Hoskinson.It is the largest coin to use a proof-of-stake blockchain (greener alternative)', 'Cardano'),
('BNB', 'Cryptocurrency exchange founded by Changpeng Zhao, is currently the largest exchange in the world on daily trading volume of cryptocurrencies.', 'Binance Coin'),
('USDT', 'Tether (USDT) is an Ethereum token that is pegged to the value of a U.S. dollar (also known as a stablecoin).', 'Tether'),
('XRP', 'XRP is the cryptocurrency used by the Ripple payment network. Built for enterprise use, it aims to be fast, cost-efficient for cross-border payments.', 'XRP'),
('DOGE', 'DOGE was created in 2013 as a lighthearted alternative to traditional cryptocurrencies. The Dogecoin name and Shiba Inu logo are based on a meme.', 'Dogecoin'),
('SOL', 'Solana is a coin that claims to be able to support 50,000 transactions per second without sacrificing decentralization', 'Solana'),
('DOT', 'By uniting multiple blockchains, Polkadot is a coin that aims to achieve high degrees of security and scalability', 'Polkadot'),
('USDC', 'USD Coin is a stablecoin redeemable on a 1:1 basis for US dollars, backed by dollar denominated assets held in US regulated financial institutions.', 'USD Coin'),
('UNI', 'Uniswap is an Ethereum token that powers Uniswap, an automated liquidity provider that’s designed to make it easy to exchange Ethereum tokens.', 'Uniswap'),
('LINK', 'Chainlink (LINK) is an Ethereum token that powers the Chainlink decentralized oracle network.', 'Chainlink'),
('LUNA', 'Terra stablecoins offer instant settlements, low fees and seamless cross-border exchange - loved by millions of users and merchants.', 'Terra'),
('BCH', 'Bitcoin Cash is a fork of Bitcoin that seeks to add more transaction capacity to the network in order to be useful for everyday transactions.', 'Bitcoin Cash'),
('BUSD', 'Highly regulated 1:1 USD-backed crypto stablecoin. These are digitised US Dollars and are always purchased and redeemed at 1 BUSD for 1 US dollar.', 'Binance USD'),
('LTC', 'Litecoin is a cryptocurrency that uses a faster payment confirmation schedule and a different cryptographic algorithm than Bitcoin.', 'Litecoin'),
('ICP', 'Internet Computer is a utility token allowing users to participate in & govern the IC blockchain network. It aims to help developers create websites.', 'Internet Computer'),
('WBTC', 'Wrapped Bitcoin (WBTC) is an Ethereum token that is intended to represent Bitcoin (BTC) on the Ethereum blockchain.', 'Wrapped Bitcoin'),
('MATIC', 'Polygon is an Ethereum token powering the Polygon Network, a scaling solution for Ethereum. Polygon aims to provide faster and cheaper transactions.', 'Polygon'),
('VET', 'VeChain is a cryptocurrency and smart contracts platform focused on supply chain management.', 'VeChain');

-- Create two dummy clients and accounts
INSERT INTO User (email, password, salt, userRole, isBlocked, firstName, prefix, lastName, street, houseNumber, houseNumberExtension, zipCode, city, bsn, dateOfBirth)
VALUES ('test1@test.com', 'e4e58a62d41c2bb6688470bcb8cac026dcb51fab3ba9c94f999b36a30ebff523', 'e9442010705a4ae29d3d57f83810fe40', 'client', '0', 'Sammy', null, 'Davis Jr.', 'Bibbedie', '10', 'E', '1102AB', 'Amsterdam', '123456782', '2000-08-11');
INSERT INTO Account (accountID, IBAN, balance, userID) VALUES ('2','NL67BSIN3890518477','10000.00','2');
INSERT INTO User (email, password, salt, userRole, isBlocked, firstName, prefix, lastName, street, houseNumber, houseNumberExtension, zipCode, city, bsn, dateOfBirth)
VALUES ('test2@test.com', '7f69c1d1c98be26fd9acf05acb7faa06bdb1aaefc976c29a3fffe8d309f690fe', '484a083720c3bd538100669378d5460d', 'client', '0', 'Dean', null, 'Martin', 'plein 1945', '10', 'E', '1107AW', 'Amsterdam', '405886408', '2000-08-11');
INSERT INTO Account (accountID, IBAN, balance, userID) VALUES ('3', 'NL24BSIN5459628412', '10000.00', '3');
INSERT INTO User (email, password, salt, userRole, isBlocked, firstName, prefix, lastName, street, houseNumber, houseNumberExtension, zipCode, city, bsn, dateOfBirth)
VALUES ('klant4@klant.nl', '93fae017c7cc7de3f940b5a2f6bfca48b863b5b2dedb48545d711ec90bc7b662', 'b05b3b8680f866b4df5b6b91da60780b', 'client', '0', 'Kees', 'de', 'Broek', 'Van der Sande Bakhuyzenstraat', '116', '', '1223CT', 'Hilversum', '252828793', '1979-07-16');
INSERT INTO Account (accountID, IBAN, balance, userID) VALUES ('4', 'NL85BSIN9900448665', '10000.00', '4');
INSERT INTO User (email, password, salt, userRole, isBlocked, firstName, prefix, lastName, street, houseNumber, houseNumberExtension, zipCode, city, bsn, dateOfBirth)
VALUES ('klant5@klant.nl', 'db0e29a20d16e4f6394fa7eb5dffdb5e29facb23a99b68597486d422d0bc823c', '61de76563dbdac64215a932e2ecef23c', 'client', '0', 'Mieke', '', 'Heemskerk', 'Nijenheim', '97', '', '3704TH', 'Hilversum', '218217754', '1986-12-02');
INSERT INTO Account (accountID, IBAN, balance, userID) VALUES ('5', 'NL65BSIN5827895780', '10000.00', '5');
INSERT INTO User (email, password, salt, userRole, isBlocked, firstName, prefix, lastName, street, houseNumber, houseNumberExtension, zipCode, city, bsn, dateOfBirth)
VALUES ('admin@banksinatra.nl', 'e43534598dbe58ccb1fff0ded11be408690876526a3e42dfa973fed07aee3863', 'fa32ca55a9e8e1d893457e58db12cf42', 'admin', '0', 'Frank', '', 'Sinatra', '', 0, '', '', '', 0, null);


INSERT INTO `Asset` VALUES (1,'ADA',3617.10729350, 3617.10729350, 0),(1,'BCH',3237.97825746, 3237.97825746, 0),(1,'BNB',4446.80050983, 4446.80050983, 0),
                           (1,'BTC',1467.46154488, 1467.46154488, 0),(1,'BUSD',2907.71188934, 2907.71188934, 0),(1,'DOGE',4165.35024824, 4165.35024824, 0),
                           (1,'DOT',4299.78222044, 4299.78222044, 0),(1,'ETH',4953.59009790, 4953.59009790, 0),(1,'ICP',4204.44776219, 4204.44776219, 0),
                           (1,'LINK',3270.64077791, 3270.64077791, 0),(1,'LTC',4452.54613758, 4452.54613758, 0),(1,'LUNA',2664.79656423, 2664.79656423, 0),
                           (1,'MATIC',4202.83711257, 4202.83711257, 0),(1,'SOL',4443.27804342, 4443.27804342, 0),(1,'UNI',3251.06460353, 3251.06460353, 0),
                           (1,'USDC',1795.14359490, 1795.14359490, 0),(1,'USDT',1520.26752852, 1520.26752852, 0),(1,'VET',1989.28679912, 1989.28679912, 0),
                           (1,'WBTC',1514.80989533, 1514.80989533, 0),(1,'XRP',3150.50238923, 3150.50238923, 0),(2, 'ADA', 20.00000000, 18.00000000, 3.00),
                           (2, 'ETH', 43.68000000, 40.00000000, 3500.00), (2, 'LINK', 32.56000000, 10.00000000, 25.00), (2, 'LTC', 344.34000000, 150.00000000, 165.00),
                           (2, 'LUNA', 33.05000000, 10.00000000, 35.00),(2, 'UNI', 1.45000000, 1.45000000, 25.00),
                           (2, 'USDC', 35.23000000, 0.00000000, 0.00),(3, 'BCH', 10.05000000, 5.00000000, 550.00),
                           (3, 'DOGE', 296.00000000, 100.00000000, 1.00), (3, 'DOT', 432.06000000, 200.00000000, 33.00),
                           (3, 'ICP', 20.98000000, 10.00000000, 55.00),(3, 'WBTC', 12.00000000, 2.00000000, 45356.00),
                           (3, 'XRP', 2.00000000, 2.00000000, 1.60),(4, 'BUSD', 2.05700000, 0.00000000, 0.00),
                           (4, 'MATIC', 345.00000000, 300.00000000, 2.00),(4, 'USDT', 53.00000000, 0.00000000, 0.00),
                           (5, 'BNB', 4446.80050983, 200.00000000, 385.00),(5, 'BTC', 10.46100000, 2.00000000, 45000.00),
                           (5, 'SOL', 12.72000000, 0.00000000, 0.00),(5, 'VET', 18.29000000, 0.00000000, 0.00);

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
VALUES (current_timestamp(), 2, 2500.0, 10.0, 3, 2, 'LTC');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 1, 2500.0, 10.0, 3, 2, 'BTC');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 3, 2500.0, 10.0, 3, 2, 'DOGE');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 1000, 2.00, 10.0, 3, 2, 'ADA');
-- Maand geleden verkocht
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp(), INTERVAL -15 DAY), 1000, 2.00, 10.0, 3, 2, 'ADA');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp(), INTERVAL -15 DAY), 20, 2900.00, 10.0, 3, 2, 'DOGE');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp, INTERVAL -2 MONTH), 3, 25.34, 10.0, 2, 3, 'BTC');

-- Dummy user 3 buys
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 1, 30400.0, 10.0, 3, 2, 'BTC');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 24.56, 3530.0, 10.0, 3, 5, 'XRP');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 54.56, 1.46, 10.0, 3, 4, 'DOGE');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 27.25, 1564.34, 10.0, 3, 2, 'ETH');
-- Maand geleden gekocht
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp, INTERVAL -15 DAY), 12.05, 257.78, 10.0, 3, 2, 'LINK');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp, INTERVAL -2 MONTH), 11.567, 256.34, 10.0, 3, 4, 'BUSD');

-- Dummy user 3 sells
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 46, 2500.0, 10.0, 2, 3, 'LTC');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 4, 13.0, 10.0, 4, 3, 'VET');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 66, 78.560, 10.0, 5, 3, 'DOGE');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 123, 342.00, 10.0, 2, 3, 'ADA');
-- Maand geleden verkocht
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp(), INTERVAL -15 DAY), 2, 4.00, 10.0, 4, 3, 'DOGE');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp(), INTERVAL -15 DAY), 19, 29.00, 10.0, 4, 3, 'XRP');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp, INTERVAL -2 MONTH), 0.012, 5665.34, 10.0, 5, 3, 'BTC');

-- Dummy user 4 buys
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 1, 30400.0, 10.0, 4, 3, 'BTC');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 24.56, 3530.0, 10.0, 4, 2, 'XRP');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 54.56, 1.46, 10.0, 4, 5, 'DOGE');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 27.25, 1564.34, 10.0, 4, 1, 'ETH');
-- Maand geleden gekocht
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp, INTERVAL -15 DAY), 12.05, 257.78, 10.0, 4, 2, 'LINK');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp, INTERVAL -2 MONTH), 11.567, 256.34, 10.0, 4, 3, 'BUSD');

-- Dummy user 4 sells
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 46, 2500.0, 10.0, 2, 4, 'LTC');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 4, 13.0, 10.0, 3, 4, 'VET');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 66, 78.560, 10.0, 5, 4, 'DOGE');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (current_timestamp(), 123, 342.00, 10.0, 3, 4, 'ADA');
-- Maand geleden verkocht
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp(), INTERVAL -15 DAY), 2, 4.00, 10.0, 3, 4, 'DOGE');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp(), INTERVAL -15 DAY), 19, 29.00, 10.0, 5, 4, 'XRP');
INSERT INTO `Transaction` (date, units, transactionPrice, bankingFee, accountID_buyer, accountID_seller, symbol)
VALUES (DATE_ADD(current_timestamp, INTERVAL -2 MONTH), 0.012, 5665.34, 10.0, 2, 4, 'BTC');
