package com.example.WebLogin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.WebLogin.persistence.entity.UserEntity;
import com.example.WebLogin.service.UserDetailServiceImpl;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private UserDetailServiceImpl userDetailsService;
    private static String ROOT = "root";

    public LoginController(UserDetailServiceImpl users) {
        this.userDetailsService = users;
    }

    
    @GetMapping("/")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        Iterable<UserEntity> userList = userDetailsService.getAllUsers();
        for (UserEntity user : userList) {
            if (user.getUsername().equals(ROOT)) {
                return "redirect:/showSetAdminUser";
            }
        }
        return "login";
    }

    @GetMapping("/showSetAdminUser")
    public String showSetAdminUser() {

        UserEntity root = userDetailsService.getUserByUsername(ROOT);
        if (root == null) {
            return "login";
        }
        return "showSetAdminUser";
    }

    /**
     * Sólo para el inicio de la aplicación.
     * 
     * En el primer inicio, se solicitará al usuario que cambie el usuario y
     * contraseña para el usuario administrador.
     * 
     * @param username usuario en el login
     * @param password passwd en el login
     * @return
     */
    @PostMapping("/setAdminUser")
    public String setAdminUser(@RequestParam("username") String username, @RequestParam("password") String password) {

        UserEntity root = userDetailsService.getUserByUsername(ROOT);
        if (root == null) {
            return "login";
        }

        if (username.equals("") || username.isBlank()) {
            return "redirect:/showSetAdminUser?error=El nombre de usuario no puede estar en blanco.";
        }

        if (username.equals(ROOT)) {
            return "redirect:/showSetAdminUser?error=Debe cambiar el usuario root a otro diferente.";
        }

        if (password.equals("") || password.isBlank()) {
            return "redirect:/showSetAdminUser?error=El password no puede estar en blanco.";
        }

        if (!userDetailsService.setAdminNewSession(username, password)) {
            return "redirect:/showSetAdminUser?error=Ha habido algún error inesperado y no se ha podido cambiar el usuario, vuelve a intentarlo.";
        }
        return "redirect:/";
    }

    /**
     * Hace logout de la aplicación
     * 
     * @param session
     */
    @GetMapping("/logout")
    public void logout(HttpSession session) {
        System.out.println("LOGOUT");
    }

}
