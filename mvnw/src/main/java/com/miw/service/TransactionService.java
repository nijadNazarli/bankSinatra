package miw.service;

import com.miw.database.RootRepository;
import com.miw.model.Crypto;
import com.miw.model.Transaction;
import com.miw.service.RegistrationService;
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
        double salePrice = rootRepository.getAssetBySymbol(transaction.getSeller(),
                transaction.getCrypto().getSymbol()).getSalePrice();
        transaction.setTransactionPrice(salePrice * transaction.getUnits());
        return transaction;
    }

    public Transaction setBankCosts(Transaction transaction){
        transaction.setBankCosts(rootRepository.getBankCosts() * transaction.getTransactionPrice());
        return transaction;
    }

    public boolean checkSufficientBalance(int seller, int buyer, double transactionPrice, double bankCosts){
        double buyerBalance = rootRepository.getAccountById(buyer).getBalance();
        if (seller == accountBank){
            return buyerBalance >= transactionPrice + (transactionPrice * bankCosts);
        } else if (buyer == accountBank){
            return buyerBalance >= transactionPrice;
        } else {
            return buyerBalance >= transactionPrice + (HALF * (transactionPrice * bankCosts));
        }
    }

    public boolean checkSufficientCrypto(int seller, Crypto crypto, double units){
        return rootRepository.getAssetBySymbol(seller, crypto.getSymbol()).getUnitsForSale() >= units;
    }

    public void transferBalance(int seller, int buyer, double transactionPrice){
        rootRepository.updateBalance(rootRepository.getAccountById(seller).getBalance() + transactionPrice, seller);
        rootRepository.updateBalance(rootRepository.getAccountById(buyer).getBalance() - transactionPrice, buyer);
    }

    public void transferCrypto(int seller, int buyer, Crypto crypto, double units){
       double newSellerAssetsForSale = rootRepository.getAssetBySymbol(seller, crypto.getSymbol()).getUnitsForSale() - units;
       double newSellerTotalAssets = rootRepository.getAssetBySymbol(seller, crypto.getSymbol()).getUnits() - units;
       double newBuyerAssets = rootRepository.getAssetBySymbol(buyer, crypto.getSymbol()).getUnits() + units;

       rootRepository.updateAssetForSale(newSellerAssetsForSale, crypto.getSymbol(), seller);

       if (!(newSellerTotalAssets == 0)){
           rootRepository.updateAsset(newSellerTotalAssets, crypto.getSymbol(), seller);
       } else {
           rootRepository.deleteAsset(crypto.getSymbol(), seller);
       }

       if(rootRepository.getAssetBySymbol(buyer, crypto.getSymbol()) == null){
           rootRepository.saveAsset(buyer, crypto.getSymbol(), units);
       } else {
           rootRepository.updateAsset(newBuyerAssets, crypto.getSymbol(), buyer);
           if (buyer == accountBank){
               rootRepository.updateAssetForSale(newBuyerAssets, crypto.getSymbol(), buyer);
           }
       }
    }

    public void transferBankCosts(int seller, int buyer, double transactionPrice, double bankCostsPercentage){
        double bankCosts = bankCostsPercentage * transactionPrice;

        if (seller == accountBank){ //seller is bank, buyer is client
            rootRepository.updateBalance(rootRepository.getAccountById(seller).getBalance() + bankCosts, seller);
            rootRepository.updateBalance(rootRepository.getAccountById(buyer).getBalance() - bankCosts, buyer);
        } else if (buyer == accountBank){ //seller is client, buyer is bank
            rootRepository.updateBalance(rootRepository.getAccountById(seller).getBalance() - bankCosts, seller);
            rootRepository.updateBalance(rootRepository.getAccountById(buyer).getBalance() + bankCosts, buyer);
        } else { //both seller and buyer are clients
            double share = bankCosts * HALF;
            rootRepository.updateBalance(rootRepository.getAccountById(seller).getBalance() - share, seller);
            rootRepository.updateBalance(rootRepository.getAccountById(buyer).getBalance() - share, buyer);
            rootRepository.updateBalance(rootRepository.getAccountById(accountBank).getBalance() + bankCosts, accountBank);
        }
    }

    public List<Crypto> getCryptoOverview() {
        //Get list of all cryptos
        //Obtain prices minus 1day/1month/3months/year/start
        //Calculate delta values. Hiervoor is een map in Crypto nodig.
        List<Crypto> cryptoOverview = rootRepository.getCryptoOverview();

        return cryptoOverview;
    }
}