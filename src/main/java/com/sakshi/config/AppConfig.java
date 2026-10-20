package com.sakshi.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration loader for application properties.
 */
public class AppConfig {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                PROPS.load(input);
            } else {
                System.err.println("Warning: application.properties not found.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getOpenRouterApiKey() {
        String environmentKey = System.getenv("OPENROUTER_API_KEY");
        if (environmentKey != null && !environmentKey.isBlank()) {
            return environmentKey;
        }
        return PROPS.getProperty("openrouter.api.key");
    }

    public static String getOpenRouterBaseUrl() {
        return PROPS.getProperty("openrouter.api.base.url", "https://openrouter.ai/api/v1");
    }

    public static String getOpenRouterModel() {
        return PROPS.getProperty("openrouter.api.model");
    }

    public static String getDefaultInputPath() {
        return PROPS.getProperty("app.input.default.path");
    }

    public static String getOutputDir() {
        return PROPS.getProperty("app.output.dir");
    }

    public static String getLogLevel() {
        return PROPS.getProperty("app.log.level");
    }
}