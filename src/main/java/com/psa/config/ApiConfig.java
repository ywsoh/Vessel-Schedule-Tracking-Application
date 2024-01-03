package com.psa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api")
public class ApiConfig {
    private String vesselBerthingUrl;
    private int vesselBerthingQueryInterval;
    private String predictionUrl;
    private int predictionQueryInterval;
    private String apiKey;
    private boolean refreshVesselTableOnStartup;
    private boolean refreshPredictionTableOnStartup;

    public String getVesselBerthingUrl() {
        return vesselBerthingUrl;
    }

    public void setVesselBerthingUrl(String vesselBerthingUrl) {
        this.vesselBerthingUrl = vesselBerthingUrl;
    }

    public int getVesselBerthingQueryInterval() {
        return vesselBerthingQueryInterval;
    }

    public void setVesselBerthingQueryInterval(int vesselBerthingQueryInterval) {
        this.vesselBerthingQueryInterval = vesselBerthingQueryInterval;
    }

    public String getPredictionUrl() {
        return predictionUrl;
    }

    public void setPredictionUrl(String predictionUrl) {
        this.predictionUrl = predictionUrl;
    }

    public int getPredictionQueryInterval() {
        return predictionQueryInterval;
    }

    public void setPredictionQueryInterval(int predictionQueryInterval) {
        this.predictionQueryInterval = predictionQueryInterval;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public boolean isRefreshVesselTableOnStartup() {
        return refreshVesselTableOnStartup;
    }

    public void setRefreshVesselTableOnStartup(boolean refreshVesselTableOnStartup) {
        this.refreshVesselTableOnStartup = refreshVesselTableOnStartup;
    }

    public boolean isRefreshPredictionTableOnStartup() {
        return refreshPredictionTableOnStartup;
    }

    public void setRefreshPredictionTableOnStartup(boolean refreshPredictionTableOnStartup) {
        this.refreshPredictionTableOnStartup = refreshPredictionTableOnStartup;
    }

}
