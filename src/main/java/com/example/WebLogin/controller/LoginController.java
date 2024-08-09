package com.example.WebLogin.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class LoginController {

    @GetMapping("/")
    public String login() {
        System.out.println("SIGNIN");
        return "login";
    }

    @GetMapping("/logout")
    public String logOff() throws Exception {
        System.out.println("LOGOUT");
        return "redirect:/";
    }
}
