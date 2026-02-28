package com.eventhub.eventhub_api.dto;

public class HealthResponse {
    private String status;
    private String version;
    private String appName;

    public HealthResponse() {}

    public HealthResponse(String status, String version, String appName) {
        this.status = status;
        this.version = version;
        this.appName = appName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}