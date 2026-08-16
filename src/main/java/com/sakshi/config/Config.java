package com.sakshi.config;

public class Config {

    private Config() {
        // Prevent object creation
    }

    public static final String API_KEY = System.getenv("OPENROUTER_API_KEY");

    public static final String MODEL = "nvidia/nemotron-3-ultra-550b-a55b:free";

    public static final String URL = "https://openrouter.ai/api/v1/chat/completions";
}