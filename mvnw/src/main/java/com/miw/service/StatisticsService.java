package miw.service;

import com.miw.database.JdbcAssetDao;
import com.miw.database.JdbcCryptoDao;
import com.miw.database.JdbcTransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {
    private final JdbcAssetDao jdbcAssetDao;
    private final JdbcTransactionDao jdbcTransactionDao;
    private final JdbcCryptoDao jdbcCryptoDao;

    @Autowired
    public StatisticsService(JdbcAssetDao jdbcAssetDao, JdbcTransactionDao jdbcTransactionDao, JdbcCryptoDao jdbcCryptoDao) {
        this.jdbcAssetDao = jdbcAssetDao;
        this.jdbcTransactionDao = jdbcTransactionDao;
        this.jdbcCryptoDao = jdbcCryptoDao;
    }

    // TODO: Method to get list of crypto values with certain interval (base case: each day of the last week):
    // DAO Method can imeadiatly be integrated in endpoint?

    // TODO: Method to get list of total portfolio value with certain interval (base case: each day of the last week):
            // getBuyerTransactions op basis van betrefende datum en cryptosymbol
        //            jdbcTransactionDao.getAssetsUntill(date);
            // getTransactionsAsSeller op basis van betrefende datum
        //            jdbcTransactionDao.getTransactionsSellerUntillDate(date);
            // double totaalPortfolioWaarde;
            // foreach loop
            // ga transactie na, per transactie
            // haal koerswaarde op van cryptosymbol op datum x. vermenigvuldig aandeel keer koerswaarde
            // totaalportfoliowaarde =+ deze koerswaarde
            // cryptoStats.add totaalportfoliowaarde

}
