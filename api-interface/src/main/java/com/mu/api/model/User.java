package com.mu.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private String username;
    public User() {
        // 空构造方法
    }
    public User(String json) {
        // 使用 Jackson 库将 JSON 字符串转换为 User 对象
        ObjectMapper mapper = new ObjectMapper();
        try {
            User user = mapper.readValue(json, User.class);
            this.username = user.getUsername();
        } catch (JsonProcessingException e) {
            // 处理异常
        }
    }
}
