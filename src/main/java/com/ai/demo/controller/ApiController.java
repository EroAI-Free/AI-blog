package com.ai.demo.controller;

import com.ai.demo.OkhttpGptChatDemo;
import com.ai.demo.bean.ChatRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@RestController
public class ApiController {

    static Set<String> tokens = new HashSet<>();

    static {
        tokens.add("sk-123");
    }

    /**
     * @param req
     * @param response
     * @throws IOException
     */
    @PostMapping("/v1/chat/completions")
    public void chat(@RequestBody ChatRequest req, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (req.getStream()) {
            response.setContentType("text/event-stream");
            // 针对Nginx代理设置不允许缓存
            response.setHeader("X-Accel-Buffering", "no");
        } else {
            response.setContentType("application/json");
        }
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        String token = request.getHeader("Authorization");
        token = token.replace("Bearer", "").trim();
        if (!tokens.contains(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token error");
            return;
        }
        if (req.getStream()) {
            OkhttpGptChatDemo.sendStreamAPi(req, response);
        } else {
            OkhttpGptChatDemo.sendNoStreamApi(req, response);
        }
    }


}
