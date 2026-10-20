package com.sakshi.config;

public class Config {

    private Config() {
        // Prevent object creation
    }

    public static final String API_KEY = AppConfig.getOpenRouterApiKey();

    public static final String MODEL = AppConfig.getOpenRouterModel();

    public static final String URL = AppConfig.getOpenRouterBaseUrl() + "/chat/completions";
}