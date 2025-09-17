package com.ai.demo;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class GptChatDemo {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GptChatRequest {
        private String model;
        private List<Message> messages;
        private boolean stream;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }

    private static String url = "https://www.apiplus.online/v1/chat/completions";
    private static String token = "sk-aZ8OTonNtI7jo5bDQtwcbTsg0z4giYiH9Ax6wleLpRGlX4NP"; // 替换为实际的API令牌


    public static void main(String[] args) throws IOException {
        Message message = new Message();
        message.setRole("user");
        message.setContent("你好");
        GptChatRequest gptChatRequest = new GptChatRequest();
        gptChatRequest.setModel("gpt-4o-mini");
        gptChatRequest.setStream(false);
        gptChatRequest.setMessages(Collections.singletonList(message));

        String response = sendNoStreamRequest(url, token, gptChatRequest);
        System.out.println(response);

        gptChatRequest.setStream(true);
        sendStreamRequest(url, token, gptChatRequest);
    }


    private static void sendStreamRequest(String url, String apiKey, GptChatRequest gptChatRequest) throws IOException {
        // 实现发送请求的逻辑
        String jsonString = JSONObject.toJSONString(gptChatRequest);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
        Request.Builder builder = new Request.Builder().url(url).method("POST", body)
                .addHeader("Authorization", "Bearer " + apiKey);
        Request request = builder.build();
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(8000, 300, TimeUnit.SECONDS))
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        Response execute = client.newCall(request).execute();
        ResponseBody responseBody = execute.body();
        Reader reader = Objects.requireNonNull(responseBody).charStream();
        BufferedReader bufferedReader = new BufferedReader(reader);

        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
    }


    private static String sendNoStreamRequest(String url, String apiKey, GptChatRequest gptChatRequest) throws IOException {
        // 实现发送请求的逻辑
        String jsonString = JSONObject.toJSONString(gptChatRequest);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
        Request.Builder builder = new Request.Builder().url(url).method("POST", body)
                .addHeader("Authorization", "Bearer " + apiKey);
        Request request = builder.build();
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(8000, 300, TimeUnit.SECONDS))
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
