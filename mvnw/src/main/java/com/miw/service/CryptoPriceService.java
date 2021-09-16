package miw.service;

import com.google.gson.*;
import com.miw.database.RootRepository;
import com.miw.model.Asset;
import com.miw.model.Bank;
import com.miw.service.RegistrationService;
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

@Service
public class CryptoPriceService {

    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    private final String apiKey = "89b44b8d-1f46-4e3c-9b3b-d4c9a84d80d6"; // dit is ook top secret! :)
    private final String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";
    private final String CMC_CRYPTO_IDS = "1,1027,2010,1839,825,52,5426,74,6636,3408,7083,1975,2,1831,4172,4687,8916,3077,3890,3717";
    // Coinmarketcap ids of: btc,eth,ada,bnb,usdt,xrp,sol,doge,dot,usdc,uni,link,ltc,bch,luna,busd,icp,vet,matic,wbtc

    private final int TIMEZONE_OFFSET   = 2;
    private final int CALL_FREQUENCY    = 30 * 60 * 1000; // in milliseconds, i.e. 30 minutes
    private final int INITIAL_DELAY     = 10 * 60 * 1000; // i.e. 10 minutes (delay before first call after app launch)

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
            List<Asset> assets = rootRepository.getAssets(Bank.BANK_ID);
            assets.forEach(asset -> rootRepository.marketAsset(asset.getUnits(), asset.getCrypto().getCryptoPrice(),asset.getCrypto().getSymbol(),Bank.BANK_ID));
            logger.info("Crypto prices updated successfully!");
        } catch (IOException e) {
            logger.info("Error: cannot access content - " + e.toString());
        } catch (URISyntaxException e) {
            logger.info("Error: Invalid URL " + e.toString());
        }
    }

    private void parseAndSave(String responseContent) throws IOException {
        // String omzetten in een JsonObject, en daaruit een JsonArray halen waarin de relevante koersdata zit
        JsonObject convertedObject = new Gson().fromJson(responseContent, JsonObject.class);
        JsonArray cryptos = new JsonArray();
        List<String> CMCCryptoIds = Arrays.asList(CMC_CRYPTO_IDS.split(",")); // id-string hier gebruiken als list
        for (String cmcCryptoId : CMCCryptoIds) { // ... en elk element uit die list ophalen als jsonobject.
            cryptos.add(convertedObject.get("data").getAsJsonObject().get(cmcCryptoId).getAsJsonObject());
        }

        // hieronder het omzetten van de timestamp uit de API (!= sql-compatibel) naar een sql-compatibele localdatetime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestampRaw = (convertedObject.get("status").getAsJsonObject().get("timestamp").toString());
        String timestamp = (timestampRaw.substring(1, 11) + " " + timestampRaw.substring(12, 20)); // verwijder "T" uit midden string
        LocalDateTime time = LocalDateTime.parse(timestamp, formatter).plusHours(TIMEZONE_OFFSET); // ad-hoc adjustment voor timezone

        // parsen van de prijs per crypto in de JsonArray, en doorsturen naar de dao
        for (JsonElement crypto : cryptos) {
            String symbolRaw = crypto.getAsJsonObject().get("symbol").toString();
            String symbol = symbolRaw.substring(1, (symbolRaw.length() - 1)); // opruimen overbodige aanhalingstekens uit json
            double price =  crypto.getAsJsonObject()
                    .get("quote").getAsJsonObject() // prijs bevindt zich diep in de json, onder "quote" ...
                    .get("USD").getAsJsonObject()  // ... binnen "quote" moeten we naar de sectie "usd" ...
                    .get("price").getAsDouble(); // ... en binnen "usd" bereiken we pas de property "price".
            rootRepository.saveCryptoPriceBySymbol(symbol, price, time);
        }
    }

    /* deze code komt uit de API-documentatie, enige kleine aanpassingen daargelaten, en zorgt met behulp van gespecificeerde
       parameters voor een succesvolle API-call, waarop de functie een String met daarin de data teruggeeft */
    private String makeAPICall(String uri, List<NameValuePair> params) throws URISyntaxException, IOException {

        String responseContent = "";

        // Klaarzetten query met parameters + alle toeters en bellen daaromheen (api sleutel, headers etc.)
        URIBuilder query = new URIBuilder(uri);
        query.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", apiKey);

        // Uitvoering request en teruggeven respone
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

}