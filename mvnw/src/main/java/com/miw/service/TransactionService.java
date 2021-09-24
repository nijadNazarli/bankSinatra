package com.miw.service;

import com.miw.database.RootRepository;
import com.miw.model.*;
import com.miw.service.authentication.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransactionService {

    private RootRepository rootRepository;
    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    private int accountBank;
    private final double HALF = 0.5;

    @Autowired
    public TransactionService(RootRepository rootRepository) {
        super();
        this.rootRepository = rootRepository;
        accountBank = Bank.BANK_ID;
        logger.info("New TransactionService");
    }

    public void registerTransaction(Transaction transaction){
        rootRepository.saveNewTransaction(transaction);
    }

    public Transaction setTransactionPrice(Transaction transaction){
        double price;
        if (transaction.getBuyer() == accountBank){
            price = rootRepository.getLatestPriceBySymbol(transaction.getCrypto().getSymbol());
        } else {
            price = rootRepository.getAssetBySymbol(transaction.getSeller(),
                    transaction.getCrypto().getSymbol()).getSalePrice();
        }
        transaction.setTransactionPrice(price * transaction.getUnits());
        return transaction;
    }

    public Transaction setBankCosts(Transaction transaction){
        transaction.setBankCosts(rootRepository.getBankCosts() * transaction.getTransactionPrice());
        return transaction;
    }

    public boolean checkSufficientBalance(int seller, int buyer, double transactionPrice, double bankCosts){
        double buyerBalance = rootRepository.getAccountById(buyer).getBalance();
        if (seller == accountBank){
            return buyerBalance >= transactionPrice + bankCosts;
        } else if (buyer == accountBank){
            return buyerBalance >= transactionPrice;
        } else {
            return buyerBalance >= transactionPrice + (HALF * bankCosts);
        }
    }

    public boolean checkSufficientCrypto(int buyer, int seller, Crypto crypto, double units){
        if (buyer == accountBank){
            return rootRepository.getAssetBySymbol(seller, crypto.getSymbol()).getUnits() >= units;
        } else {
            return rootRepository.getAssetBySymbol(seller, crypto.getSymbol()).getUnitsForSale() >= units;
        }
    }

    public void transferBalance(int seller, int buyer, double transactionPrice){
        rootRepository.updateBalance(rootRepository.getAccountById(seller).getBalance() + transactionPrice, seller);
        rootRepository.updateBalance(rootRepository.getAccountById(buyer).getBalance() - transactionPrice, buyer);
    }

    public void transferCrypto(int seller, int buyer, Crypto crypto, double units){
       if (buyer == accountBank){
            updateSellerUnitsBankBuyer(seller, crypto, units);
       } else {
           updateSellerUnits(seller, crypto, units);
       }
       updateBuyerUnits(buyer, crypto, units);
    }

    private void updateSellerUnits(int seller, Crypto crypto, double units){
        double newSellerAssetsForSale = rootRepository.getAssetBySymbol(seller, crypto.getSymbol()).getUnitsForSale() - units;
        double newSellerTotalAssets = rootRepository.getAssetBySymbol(seller, crypto.getSymbol()).getUnits() - units;

        rootRepository.updateAssetForSale(newSellerAssetsForSale, crypto.getSymbol(), seller);

        if (!(newSellerTotalAssets == 0)){
            rootRepository.updateAsset(newSellerTotalAssets, crypto.getSymbol(), seller);
        } else {
            rootRepository.deleteAsset(crypto.getSymbol(), seller);
        }
    }

    private void updateSellerUnitsBankBuyer(int seller, Crypto crypto, double units){
        double newSellerTotalAssets = rootRepository.getAssetBySymbol(seller, crypto.getSymbol()).getUnits() - units;
        if (!(newSellerTotalAssets == 0)){
            rootRepository.updateAsset(newSellerTotalAssets, crypto.getSymbol(), seller);
        } else {
            rootRepository.deleteAsset(crypto.getSymbol(), seller);
        }
    }

    private void updateBuyerUnits(int buyer, Crypto crypto, double units){
        if(rootRepository.getAssetBySymbol(buyer, crypto.getSymbol()) == null){
            rootRepository.saveAsset(buyer, crypto.getSymbol(), units);
        } else {
            double newBuyerAssets = rootRepository.getAssetBySymbol(buyer, crypto.getSymbol()).getUnits() + units;
            rootRepository.updateAsset(newBuyerAssets, crypto.getSymbol(), buyer);
            if (buyer == accountBank){
                rootRepository.updateAssetForSale(newBuyerAssets, crypto.getSymbol(), buyer);
            }
        }
    }

    public void transferBankCosts(int seller, int buyer, double bankCosts){
        double sellerBalance = rootRepository.getAccountById(seller).getBalance();
        double buyerBalance = rootRepository.getAccountById(buyer).getBalance();
        double bankBalance = rootRepository.getAccountById(accountBank).getBalance();

        if (seller == accountBank){
            rootRepository.updateBalance(sellerBalance + bankCosts, seller);
            rootRepository.updateBalance(buyerBalance - bankCosts, buyer);
        } else if (buyer == accountBank){
            rootRepository.updateBalance(sellerBalance - bankCosts, seller);
            rootRepository.updateBalance(buyerBalance + bankCosts, buyer);
        } else {
            double share = bankCosts * HALF;
            rootRepository.updateBalance(sellerBalance - share, seller);
            rootRepository.updateBalance(buyerBalance - share, buyer);
            rootRepository.updateBalance(bankBalance + bankCosts, accountBank);
        }
    }

    public List<Transaction> getTransactionsBuyer(int userId){
        return rootRepository.getTransactionsByUserIdBuyer(userId);
    }

    public List<Transaction> getTransactionsSeller(int userId){
        return rootRepository.getTransactionsByUserIdSeller(userId);
    }

    public Account getAccountByUserId (int userId) {
        return rootRepository.getAccountByUserId(userId);
    }
}