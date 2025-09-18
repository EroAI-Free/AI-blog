package com.ai.demo;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.*;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

import java.io.IOException;
import java.util.List;

public class LangChain4jFunctionCallDemo {

    private static String url = "https://www.apiplus.online/v1";
    private static String token = "sk-aZ8OTonNtI7jo5bDQtwcbTsg0z4giYiH9Ax6wleLpRGlX4NP"; // 替换为实际的API令牌


    interface AIAskService {
        @SystemMessage("你是一个智能助手，你的任务是回答用户的问题")
        String ask(String question);
    }


    // 定义一个fc的提供者
    static class ToolCall {

        @Tool
        double add(int a, int b) {
            System.out.printf("通过AI的FC调用到这里 a=%s;b=%s%n", a, b);
            return a + b;
        }
    }


    public static void fcDemo2(){
        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(token)
                .modelName("gpt-4o-mini")
                .baseUrl(url)
                .build();
        List<ToolSpecification> toolSpecifications = ToolSpecifications.toolSpecificationsFrom(ToolCall.class);
        ChatRequest request = ChatRequest.builder()
                .messages(UserMessage.from("1+2等于多少？"))
                .toolSpecifications(toolSpecifications)
                .build();

        model.chat(request, new StreamingChatResponseHandler() {

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
                System.out.println("部分函数调用: " + partialToolCall);
            }

            @Override
            public void onCompleteToolCall(CompleteToolCall completeToolCall) {
                // 函数调用相关的
                ToolExecutionRequest toolExecutionRequest = completeToolCall.toolExecutionRequest();
                String arguments = toolExecutionRequest.arguments();
                String name = toolExecutionRequest.name();
                // 命中的函数调用
                System.out.println("命中的函数调用: " + name + "(" + arguments + ")");

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


    public static void fcDemo1() {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(token)
                .baseUrl(url)
                .modelName("gpt-4o-mini")
                .build();
        // fc的提供者
        ToolCall toolCall = new ToolCall();
        AIAskService aiAskService = AiServices.builder(AIAskService.class)
                .chatModel(model)
                .tools(toolCall)
                .build();
        String answer = aiAskService.ask("1+2等于多少？");
        System.out.println(answer);
    }


    public static void main(String[] args) throws IOException {
        fcDemo1();
        fcDemo2();
        // 流式输出是异步的 需要堵塞线程
        System.in.read();
    }

}
