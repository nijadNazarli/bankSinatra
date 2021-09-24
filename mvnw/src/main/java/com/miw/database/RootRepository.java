/**
 * @Author: Johnny Chan, MIW student 500878034.
 * Deze class is de Repository welke diverse DAO's aanroept om
 * informatie uit de database te verkrijgen en te combineren.
 */
package com.miw.database;

import com.miw.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class RootRepository {

    private final Logger logger = LoggerFactory.getLogger(RootRepository.class);
    private ClientDao clientDAO;
    private JdbcAccountDao jdbcAccountDao; // TODO: interface aanroepen ipv jdbcAccountDAO zelf
    private JdbcAdminDao jdbcAdminDao; // TODO: interface aanroepen ipv jdbcAdminDao zelf
    private JdbcAssetDao jdbcAssetDao;
    private JdbcTransactionDao jdbcTransactionDao; //TODO: interface aanroepen ipv JdbcTransactionDao zelf
    private JdbcCryptoDao jdbcCryptoDao;
    private JdbcUserDao jdbcUserDao;

    @Autowired
    public RootRepository(ClientDao clientDAO, JdbcAccountDao jdbcAccountDao,
                          JdbcAdminDao jdbcAdminDao, JdbcAssetDao jdbcAssetDao,
                          JdbcTransactionDao jdbcTransactionDao, JdbcCryptoDao jdbcCryptoDao,
                          JdbcUserDao jdbcUserDao) { // TODO: interface aanroepen ipv jdbcAccountDao, jdbcAdminDao en jdbcTransactionDao zelf
        super();
        this.clientDAO = clientDAO;
        this.jdbcAccountDao = jdbcAccountDao;
        this.jdbcAdminDao = jdbcAdminDao;
        this.jdbcAssetDao = jdbcAssetDao;
        this.jdbcTransactionDao = jdbcTransactionDao;
        this.jdbcCryptoDao = jdbcCryptoDao;
        this.jdbcUserDao = jdbcUserDao;
        logger.info("New RootRepository-object created");
    }

    public Client saveNewClient(Client client) {
        client = clientDAO.save(client); //client in table User opslaan
        //client's account in table Account opslaan en PK accountId verkrijgen en opslaan in account-object
        Account updatedAccount = jdbcAccountDao.save(client.getAccount(), client.getUserId());
        client.getAccount().setAccountId(updatedAccount.getAccountId());
        return client;
    }

    public Administrator saveNewAdmin(Administrator admin) {
        jdbcAdminDao.save(admin);
        return admin;
    }

    public Transaction saveNewTransaction(Transaction transaction){
        jdbcTransactionDao.save(transaction);
        return transaction;
    }

    public Client findClientByEmail(String email) {
        Client client = clientDAO.findByEmail(email);
        if (client != null) {
            client.setAccount(getAccountByEmail(email));
        }
        return client;
    }

    public Client findClientByBsn(int bsn){
        Client client = clientDAO.findByBsn(bsn);
        if (client != null){
            client.setAccount(getAccountByBsn(bsn));
        }
        return client;
    }

    public Administrator findAdminByEmail(String email) {
        return jdbcAdminDao.findByEmail(email);
    }

    public Account getAccountById(int accountId){
        return jdbcAccountDao.getAccountByID(accountId);
    }

    public Account getAccountByUserId(int userId) {
        return jdbcAccountDao.getAccountByUserID(userId);
    }

    public Account getAccountByEmail(String email){
        return jdbcAccountDao.getAccountByEmail(email);
    }

    public Account getAccountByBsn(int bsn){
        return jdbcAccountDao.getAccountByBsn(bsn);
    }

    public void updateBalance(double newBalance, int accountId){
        jdbcAccountDao.updateBalance(newBalance, accountId);
    }

    public List<Asset> getAssets(int accountId) {
        return jdbcAssetDao.getAssets(accountId);
    }

    public Asset getAssetBySymbol(int accountId, String symbol){
        return jdbcAssetDao.getAssetBySymbol(accountId, symbol);
    }

    public List<Asset> getAllAssetsForSaleBySymbol(String symbol, int accountId){
        return jdbcAssetDao.getAllAssetsForSaleBySymbol(symbol, accountId);
    }

    public List<String> getAllCryptosOwned(int accountId) {
        return jdbcTransactionDao.getAllCryptosOwned(accountId);
    }

    public LocalDateTime getDateTimeOfFirstTransaction(int accountId) {
        return jdbcTransactionDao.getDateTimeOfFirstTransaction(accountId);
    }

    public Double getSymbolUnitsAtDateTime(int accountId, String symbol, LocalDateTime dateTime) {
        return jdbcAssetDao.getSymbolUnitsAtDateTime(accountId, symbol, dateTime);
    }

    public void updateAsset(double newUnits, String symbol, int accountId){
        jdbcAssetDao.updateAsset(newUnits, symbol, accountId);
    }

    public void updateAssetForSale(double newUnits, String symbol, int accountId){
        jdbcAssetDao.updateAssetForSale(newUnits, symbol, accountId);
    }

    public void saveAsset(int buyer, String symbol, double units){
        jdbcAssetDao.saveAsset(buyer, symbol, units);
    }

    public double getSumOfUnitsPurchasedAndSold(int accountId, LocalDateTime dateTime, String symbol) {
        return jdbcTransactionDao.getSumOfUnitsPurchasedAndSold(accountId, dateTime, symbol);
    }

    public double getBankCosts(){
        return jdbcTransactionDao.getBankCosts();
    }

    public double getLatestPriceBySymbol(String symbol){
        return jdbcCryptoDao.getLatestPriceBySymbol(symbol);
    }

    public void saveAccount(Account account, int userId) {
        jdbcAccountDao.save(account, userId);
    }

    public void deleteAsset(String symbol, int seller) {
        jdbcAssetDao.deleteAsset(symbol, seller);
    }

    public List<Crypto> getCryptoOverview() {
        return jdbcCryptoDao.getAllCryptos();
    }

    public List<Transaction> getTransactionsByUserIdSeller(int userId){
        return jdbcTransactionDao.getTransactionsByUserIdSeller(userId);
    }

    public List<Transaction> getTransactionsByUserIdBuyer(int userId){
        return jdbcTransactionDao.getTransactionsByUserIdBuyer(userId);
    }

    public int getUserIDbyEmail(String email) {
        return jdbcUserDao.getIDByEmail(email);
    }

    public String getUserRoleByEmail(String email) {
        return jdbcUserDao.getRoleByEmail(email);
    }

    public String getFirstNameById(int userId) { return jdbcUserDao.getFirstNameById(userId);}

    public User getUserByEmail(String email) {
        return jdbcUserDao.getUserByEmail(email);
    }

    public Client findByAccountId(int accountId){ return clientDAO.findByAccountId(accountId);}

    public void toggleBlock(boolean blockUnblock, int id) {
        jdbcUserDao.toggleBlock(blockUnblock, id);
    }

    public double getBalanceByEmail(String email) {
        return jdbcAccountDao.getBalanceByEmail(email);
    }

    public void marketAsset(double unitsForSale, double salePrice, String symbol, int accountId) {
        jdbcAssetDao.putAssetOnSale(unitsForSale, salePrice, symbol, accountId);
    }

    public void saveCryptoPriceBySymbol(String symbol, double price, LocalDateTime time) {
        jdbcCryptoDao.saveCryptoPriceBySymbol(symbol, price, time);
    }

    public Map<String, Double> getPriceDeltas(LocalDateTime dateTime) {
        return jdbcCryptoDao.getPriceDeltas(dateTime);
    }

    public void updateBankCosts(double fee) {
        jdbcTransactionDao.updateBankCosts(fee);
    }

    public List<Crypto> getAllCryptos() {
        return jdbcCryptoDao.getAllCryptos();
    }

    public LocalDateTime getLatestAPICallTime() {
        return jdbcCryptoDao.getLatestAPICallTime();
    }

    public Map<Double, Double> getUnitsForSaleWithPrice (String symbol, int accountId) {
        return jdbcAssetDao.getUnitsForSaleAndPrice(symbol, accountId);
    }

    public double getAssetDeltaPct(int accountId, String symbol, LocalDateTime dateTime) {
        return jdbcAssetDao.getAssetDeltaPct(accountId, symbol, dateTime);
    }
}
