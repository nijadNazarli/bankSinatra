package com.miw.service;

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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/*
 * Dit is nog geen complete service! moet nog methoden inbouwen om het naar csv te exporteren, en die csv
 * kan dan vanuit waar dan ook gelezen worden. methode om de csv te lezen (mss een methode retrieveCryptoStats oid)
 * kan hier ook staan!
 */

public class ExchangeRateService {

    private static String apiKey = "89b44b8d-1f46-4e3c-9b3b-d4c9a84d80d6"; // dit is ook top secret! :)

    public static void main(String[] args) { // main methode hoeft uiteindelijk hier niet dus, t is een service.

        // dit zet de juiste parameters voor demo call eronder
        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("start","1"));
        params.add(new BasicNameValuePair("limit","25"));
        params.add(new BasicNameValuePair("convert","EUR"));

        // hieronder wordt bij wijze van demo een voorbeeldcall naar de console geprint, maar moet eigenlijk naar de csv.
        try {
            System.out.println(makeAPICall(uri, params));
        } catch (IOException e) {
            System.out.println("Error: cannot access content - " + e.toString());
        } catch (URISyntaxException e) {
            System.out.println("Error: Invalid URL " + e.toString());
        }
    }

    // onderstaande code komt uit coinmarketcap-documentatie...
    public static String makeAPICall(String uri, List<NameValuePair> params) throws URISyntaxException, IOException {
        String response_content = "";

        URIBuilder query = new URIBuilder(uri);
        query.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", apiKey);

        CloseableHttpResponse response = client.execute(request);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            response_content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        return response_content;
    }
}