package com.example.WebLogin.controller;


import com.example.WebLogin.filesControl.ReadConfigPath;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class LoginController {

    @GetMapping("/")
    public String login() {
        System.out.println("Estamos en el login");
        return "login";
    }

    @GetMapping("/logout")
    public String logOff() throws Exception {
        System.out.println("HACIENDO LOGOUT");
        return "redirect:/";
    }
}
