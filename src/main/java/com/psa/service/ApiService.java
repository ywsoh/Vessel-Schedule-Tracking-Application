package com.psa.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.TimeUnit;

import com.psa.config.ApiConfig;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("apiCallService")
public class ApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiService.class);

    @Autowired
    VesselService vesselService;

    @Autowired
    ApiConfig apiConfig;

    /**
     * Generates a HttpRequest object for HTTP POST
     * 
     * @param uri      the uri string of the API
     * @param apiKey   the API Key string
     * @param dateFrom A string of the start date of query (inclusive) yyyy-MM-dd
     * @param dateTo   A string of end date of query (inclusive) yyyy-MM-dd
     * @return The HttpRequest object for HTTP POST
     */
    public HttpRequest createHttpPostRequest(String uri, String apiKey, String dateFrom, String dateTo) {
        JSONObject query = new JSONObject();
        query.put("dateFrom", dateFrom);
        query.put("dateTo", dateTo);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).header("accept", "application/json")
                .header("Apikey", apiKey).header("content-type", "application/json")
                .POST(BodyPublishers.ofString(query.toString())).build();

        return request;
    }

    /**
     * Generates a HttpRequest object for HTTP GET
     * 
     * @param uri    The uri string of the API
     * @param apiKey The API Key string
     * @param vslvoy The string of vessel and voyage number to be queried <Vessel's
     *               Long Name without whitespace><incoming voyage number>
     * @return The HttpRequest object for HTTP GET
     */
    public HttpRequest createHttpGetRequest(String uri, String apiKey, String vslvoy) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri + vslvoy))
                .header("accept", "application/json").header("Apikey", apiKey).GET().build();

        return request;
    }

    /**
     * Sends a HTTP Request to remote server
     * 
     * @param httpRequest The HTTP Request to be made
     * @return A JSON Object containing the response body
     */
    public JSONObject sendRequestAndReturnJson(HttpRequest httpRequest) {
        HttpClient client = HttpClient.newHttpClient();
        String responseBody = "";
        try {
            HttpResponse<String> response = client.send(httpRequest, BodyHandlers.ofString());
            responseBody = response.body();
            return new JSONObject(response.body());

        } catch (Exception e) {
            LOGGER.error("HTTP GET/POST error: " + e);
            LOGGER.error(responseBody);
            return null;
        }
    }

    /**
     * Sends a HTTP Request to the retrieveByBerthingDate API
     * 
     * @param dateFrom The starting date of query in yyyy-dd-mm format
     * @param dateTo   The end date of query in yyyy-dd-mm format
     * @return A JSON Array containing the Results
     */
    public JSONArray retrieveVesselRecord(String dateFrom, String dateTo) {

        HttpRequest postRequest = createHttpPostRequest(apiConfig.getVesselBerthingUrl(), apiConfig.getApiKey(),
                dateFrom, dateTo);
        JSONObject vesselResponse = sendRequestAndReturnJson(postRequest);

        if (vesselResponse == null) {
            LOGGER.error("HTTP Post has failed as there are no response");
            return null;
        } else if (vesselResponse.has("errors")) {
            if (!vesselResponse.isNull("errors")) {
                LOGGER.error("HTTP Post has failed: {}",
                        vesselResponse.getJSONArray("errors").getJSONObject(0).getString("message"));
                return null;
            }
        }
        return vesselResponse.getJSONArray("results");
    }

    /**
     * Sends a HTTP Request to the predicted_btr API
     * 
     * @param vslvoy The string of vessel and voyage number to be queried <Vessel's
     *               Long Name without whitespace><incoming voyage number>
     * @return A JSON Object containing the response body
     */
    public JSONObject retrievePredictionRecord(String vslvoy) {
        HttpRequest getRequest = createHttpGetRequest(apiConfig.getPredictionUrl(), apiConfig.getApiKey(), vslvoy);
        JSONObject predictionResponse = sendRequestAndReturnJson(getRequest);
        
        try {
            LOGGER.info("Mandatory 1 second pause");
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
        
        if (predictionResponse == null) {
            LOGGER.error("HTTP Post has failed as there are no response");
            return null;
        } else if (predictionResponse.has("Error")) {
            String error = predictionResponse.getString("Error");
            if(!error.contains("No records for vsl_voy")){
                LOGGER.info("HTTP Post has failed: {}", predictionResponse.getString("Error"));
            }
            return null;
        }
        return predictionResponse;
    }
}
