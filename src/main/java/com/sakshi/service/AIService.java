package com.sakshi.service;

import com.sakshi.config.Config;
import okhttp3.*;

import java.io.IOException;

public class AIService {

    private static final OkHttpClient client = new OkHttpClient();

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

        try (Response response = client.newCall(request).execute()) {

            if (response.body() == null) {
                return "No response received.";
            }

            return response.body().string();
        }
    }
}