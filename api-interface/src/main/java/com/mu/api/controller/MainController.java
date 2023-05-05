package com.mu.api.controller;

import com.mu.api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MainController {
    @Autowired
    private AuthService authService;

    /**
     * 请求转发
     *
     * @param request
     */
    @RequestMapping("/main")
    public String MainRedirect(HttpServletRequest request) {
        return authService.mainRedirect(request);
    }

}
