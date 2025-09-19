package com.ai.demo.bean;

import lombok.Data;

import java.util.LinkedList;

@Data
public class ChatRequest {

    private String model;

    private LinkedList<ChatMessage> messages;

    private Double temperature;

    private Integer max_tokens;

    private Boolean stream = false;
}
