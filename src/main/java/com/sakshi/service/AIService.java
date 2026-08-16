package com.sakshi.service;

import com.sakshi.config.Config;
import okhttp3.*;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

public class AIService {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(300, TimeUnit.SECONDS)
            .build();

    public static String generateResponse(String prompt) throws IOException {

        String json = """
                {
                  "model": "%s",
                  "messages": [
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ]
                }
                """.formatted(
                Config.MODEL,
                prompt.replace("\"", "\\\"")
        );

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(Config.URL)
                .addHeader("Authorization", "Bearer " + Config.API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {

            try (Response response = client.newCall(request).execute()) {

                if (response.body() == null) {
                    System.out.println("ERROR: AI service returned an empty response.");
                    continue;
                }

                String responseBody = response.body().string();

                if (response.isSuccessful()) {
                    return responseBody;
                }

                int statusCode = response.code();

                System.out.println(
                        "AI request failed. Attempt "
                                + attempt + "/" + maxAttempts
                                + " | HTTP " + statusCode
                );

                if (statusCode == 401) {
                    throw new IOException(
                            "Authentication failed. Please check your OpenRouter API key."
                    );
                }

                if (statusCode == 429) {
                    System.out.println(
                            "Rate limit reached. Retrying..."
                    );
                }

                else if (statusCode == 502 || statusCode == 503) {
                    System.out.println(
                            "AI provider is temporarily unavailable. Retrying..."
                    );
                }

                else {
                    System.out.println(
                            "OpenRouter error response: " + responseBody
                    );
                }

            } catch (SocketTimeoutException e) {

                System.out.println(
                        "AI request timed out. Attempt "
                                + attempt + "/" + maxAttempts
                );

            } catch (IOException e) {

                if (attempt == maxAttempts) {
                    throw e;
                }

                System.out.println(
                        "Network error while calling AI service."
                );
            }

            if (attempt < maxAttempts) {

                System.out.println("Retrying in 3 seconds...");

                try {
                    Thread.sleep(3000);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    throw new IOException(
                            "Retry interrupted.",
                            e
                    );
                }
            }
        }

        throw new IOException(
                "AI request failed after "
                        + maxAttempts
                        + " attempts."
        );
    }
}