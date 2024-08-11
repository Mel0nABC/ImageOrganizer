package com.example.WebLogin.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class LoginController {

    @GetMapping("/")
    public String dashboard() {
        return "dashboard";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }


}
