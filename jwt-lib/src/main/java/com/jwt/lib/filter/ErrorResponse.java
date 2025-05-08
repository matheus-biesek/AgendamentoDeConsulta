package com.jwt.lib.filter;

import jakarta.json.bind.annotation.JsonbProperty;

public class ErrorResponse {
    private String error;
    private String message;
    private boolean shouldRefresh;
    private String refreshEndpoint;

    public ErrorResponse(String error, String message, boolean shouldRefresh, String refreshEndpoint) {
        this.error = error;
        this.message = message;
        this.shouldRefresh = shouldRefresh;
        this.refreshEndpoint = refreshEndpoint;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonbProperty("shouldRefresh")
    public boolean isShouldRefresh() {
        return shouldRefresh;
    }

    public void setShouldRefresh(boolean shouldRefresh) {
        this.shouldRefresh = shouldRefresh;
    }

    @JsonbProperty("refreshEndpoint")
    public String getRefreshEndpoint() {
        return refreshEndpoint;
    }

    public void setRefreshEndpoint(String refreshEndpoint) {
        this.refreshEndpoint = refreshEndpoint;
    }
} 