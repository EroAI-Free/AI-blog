package com.ai.demo;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.*;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import java.io.IOException;

public class LangChain4jChatDemo {

    private static String url = "https://www.apiplus.online/v1";
    private static String token = "sk-aZ8OTonNtI7jo5bDQtwcbTsg0z4giYiH9Ax6wleLpRGlX4NP"; // 替换为实际的API令牌

    public static void main(String[] args) throws IOException {
        noStreamChat();

        streamChat();
        // 流式输出是异步的 需要堵塞线程
        System.in.read();
    }


    /**
     * 流式输出
     */
    public static void streamChat() {
        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(token)
                .baseUrl(url)
                .modelName("gpt-4o-mini") // 这里可以填入任何支持openai格式的模型
                .build();
        String userMessage = "你好";
        model.chat(userMessage, new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.println("流式输出: " + partialResponse);
            }

            @Override
            public void onPartialThinking(PartialThinking partialThinking) {
                // 思考的响应
                System.out.println("onPartialThinking: " + partialThinking);
            }

            @Override
            public void onPartialToolCall(PartialToolCall partialToolCall) {
                // 函数调用相关的
                System.out.println("onPartialToolCall: " + partialToolCall);
            }

            @Override
            public void onCompleteToolCall(CompleteToolCall completeToolCall) {
                // 函数调用相关的
                System.out.println("onCompleteToolCall: " + completeToolCall);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                System.out.println("完整的AI回答内容: " + completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }

    /**
     * 非流
     */
    public static void noStreamChat() {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(token)
                .modelName("gpt-4o-mini")
                .baseUrl(url)
                .build();
        String response = model.chat("你好呀");
        System.out.println(response);
    }


}
