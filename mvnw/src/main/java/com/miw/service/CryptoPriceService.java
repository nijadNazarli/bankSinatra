package com.miw.service;

import com.google.gson.*;
import com.miw.database.RootRepository;
import com.miw.model.Asset;
import com.miw.model.Bank;
import com.miw.service.authentication.RegistrationService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 *  @Author: elbertvw
 *  This service ensures recent prices are saved in the database periodically by calling the CoinMarketCap API.
 *
 *  The updatePrices-method is the primary method of the service. It defines the parameters of the API call to CoinMarketCap,
 *  and then calls the makeAPICall method with those parameters and immediately feeds its response, a large JSON string,
 *  to a parser method.
 *  The parser method, parseAndSave, handles the extraction of relevant data from the JSON and immediately updates crypto
 *  prices in the database.
 *
 *  The service is scheduled to operate at a frequency defined as CALL_FREQUENCY, and executes for the first time after
 *  application launch after the specified INITIAL_DELAY.
 */

@Service
public class CryptoPriceService {

    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    private final String apiKey = "89b44b8d-1f46-4e3c-9b3b-d4c9a84d80d6"; // dit is ook top secret! :)
    private final String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";
    private final String CMC_CRYPTO_IDS = "1,1027,2010,1839,825,52,5426,74,6636,3408,7083,1975,2,1831,4172,4687,8916,3077,3890,3717";
    // Coinmarketcap ids of: btc,eth,ada,bnb,usdt,xrp,sol,doge,dot,usdc,uni,link,ltc,bch,luna,busd,icp,vet,matic,wbtc
    // CoinMarketCap unfortunately does not allow API calls by symbol, so using their internal crypto ID's is necessary.

    private final int TIMEZONE_OFFSET   = 2;
    private final int CALL_FREQUENCY    = 30 * 60 * 1000; // in milliseconds, i.e. 30 minutes
    private final int INITIAL_DELAY     = 10 * 60 * 1000; // i.e. 10 minutes

    private RootRepository rootRepository;

    @Autowired
    public CryptoPriceService(RootRepository rootRepository) {
        this.rootRepository = rootRepository;
    }

    @Scheduled(fixedRate = CALL_FREQUENCY, initialDelay = INITIAL_DELAY)
    private void updatePrices() {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", CMC_CRYPTO_IDS)); // string contains specific crypto-ids (CMC-ids != our ids!!)

        try {
            parseAndSave(makeAPICall(uri, params));
            List<Asset> assets = rootRepository.getAssets(Bank.BANK_ID);  // TODO: kijken of dit beter in de klasse verwerkt kan worden
            assets.forEach(asset -> rootRepository.marketAsset(asset.getUnits(), asset.getCrypto().getCryptoPrice(),asset.getCrypto().getSymbol(),Bank.BANK_ID));
            logger.info("Crypto prices updated successfully!");
        } catch (IOException e) {
            logger.info("Error: cannot access content - " + e.toString());
        } catch (URISyntaxException e) {
            logger.info("Error: Invalid URL " + e.toString());
        }
    }

    private void parseAndSave(String responseContent) throws IOException {
        JsonObject convertedObject = new Gson().fromJson(responseContent, JsonObject.class);
        JsonArray cryptos = new JsonArray();

        // Generate list of CoinMarketCap crypto-IDs to select from the JSON, and fill the JsonArray cryptos with
        // JsonObjects corresponding to those IDs:
        List<String> CMCCryptoIds = Arrays.asList(CMC_CRYPTO_IDS.split(","));
        for (String cmcCryptoId : CMCCryptoIds) { // ... en elk element uit die list ophalen als jsonobject.
            cryptos.add(convertedObject.get("data").getAsJsonObject().get(cmcCryptoId).getAsJsonObject());
        }
        LocalDateTime timestamp = correctTimestampFormatting(convertedObject.get("status").getAsJsonObject().get("timestamp").toString());

        // Parsing every crypto-JsonObject in the JsonArray generated above, and saving the data to the database:
        for (JsonElement crypto : cryptos) {
            String symbolRaw = crypto.getAsJsonObject().get("symbol").toString();
            String symbol = symbolRaw.substring(1, (symbolRaw.length() - 1)); // cleanup quotation marks from JSON crypto symbol
            double price =  crypto.getAsJsonObject()
                    .get("quote").getAsJsonObject()
                    .get("USD").getAsJsonObject()
                    .get("price").getAsDouble(); // key-value pair price is nested deep in the JSON object provided by the API.
            rootRepository.saveCryptoPriceBySymbol(symbol, price, timestamp);
        }
    }

    // (The code below is derived from the CoinMarketCap-documentation, with some small alterations.)
    private String makeAPICall(String uri, List<NameValuePair> params) throws URISyntaxException, IOException {
        String responseContent = "";
        // Prepare parameters and specify uri for the API call
        URIBuilder query = new URIBuilder(uri);
        query.addParameters(params);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());
        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", apiKey);
        // The actual API call happens below, and a responseContent is generated and returned.
        CloseableHttpResponse response = client.execute(request);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        return responseContent;
    }

    // Auxiliary method to convert the API-provided timestamp to a format compatable with the SQL DateTime format.
    private LocalDateTime correctTimestampFormatting (String timestampRaw) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = (timestampRaw.substring(1, 11) + " " + timestampRaw.substring(12, 20));
        return LocalDateTime.parse(timestamp, formatter).plusHours(TIMEZONE_OFFSET); // + timezone adjustment to NL time
    }
}