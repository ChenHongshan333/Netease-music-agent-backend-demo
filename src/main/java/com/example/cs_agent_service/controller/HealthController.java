package com.example.cs_agent_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RestController：告诉 Spring：这个类是 Controller，
// 并且返回的内容直接当作响应（比如字符串/JSON)
public class HealthController {

    @GetMapping("/health")
//    @GetMapping：告诉 Spring：
//    当有人用 GET 请求访问某个路径时，执行这个方法。
    public String health() {
        return "OK";
    }
}
