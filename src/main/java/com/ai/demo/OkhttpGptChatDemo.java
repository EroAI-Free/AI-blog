package com.ai.demo;

import com.ai.demo.bean.ChatMessage;
import com.ai.demo.bean.ChatRequest;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OkhttpGptChatDemo {

    private static String url = "https://www.apiplus.online/v1/chat/completions";
    private static String token = "sk-aZ8OTonNtI7jo5bDQtwcbTsg0z4giYiH9Ax6wleLpRGlX4NP"; // 替换为实际的API令牌


    public static void main(String[] args) throws IOException {
        ChatMessage message = new ChatMessage();
        message.setRole("user");
        message.setContent("你好");
        ChatRequest gptChatRequest = new ChatRequest();
        gptChatRequest.setModel("gpt-4o-mini");
        gptChatRequest.setStream(false);

        LinkedList<ChatMessage> chatMessages = new LinkedList<>();
        chatMessages.add(message);
        gptChatRequest.setMessages(chatMessages);

        String response = sendNoStreamRequest(url, token, gptChatRequest);
        System.out.println(response);
        gptChatRequest.setStream(true);
        sendStreamRequest(url, token, gptChatRequest);
    }


    /**
     * 发送流式请求
     */
    public static void sendStreamAPi(ChatRequest gptChatRequest, HttpServletResponse response) throws IOException {
        // 实现发送请求的逻辑
        String jsonString = JSONObject.toJSONString(gptChatRequest);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
        Request.Builder builder = new Request.Builder().url(url).method("POST", body)
                .addHeader("Authorization", "Bearer " + token);
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
            if (line.startsWith("data:")) {
                response.getOutputStream().write((line + "\n\n").getBytes());
                response.getOutputStream().flush();
            }
        }
    }


    private static void sendStreamRequest(String url, String apiKey, ChatRequest gptChatRequest) throws IOException {
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

    public static void sendNoStreamApi(ChatRequest gptChatRequest, HttpServletResponse httpServletResponse) throws IOException {
        // 实现发送请求的逻辑
        String jsonString = JSONObject.toJSONString(gptChatRequest);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
        Request.Builder builder = new Request.Builder().url(url).method("POST", body)
                .addHeader("Authorization", "Bearer " + token);
        Request request = builder.build();
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(8000, 300, TimeUnit.SECONDS))
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            httpServletResponse.getOutputStream().write(string.getBytes());
            httpServletResponse.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String sendNoStreamRequest(String url, String apiKey, ChatRequest gptChatRequest) throws IOException {
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
