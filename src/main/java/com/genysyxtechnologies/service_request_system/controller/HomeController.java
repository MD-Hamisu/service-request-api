package com.genysyxtechnologies.service_request_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping("login-doc")
    public String loginDoc() {
        return "index";
    }
}
