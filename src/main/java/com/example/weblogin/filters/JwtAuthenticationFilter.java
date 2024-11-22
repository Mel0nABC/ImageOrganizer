package com.example.weblogin.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.connector.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.weblogin.persistence.entity.UserEntity;
import com.example.weblogin.persistence.repository.UserRepository;
import com.example.weblogin.jwt.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private JwtUtils jwtUtils;
    private UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    // Intentar authentificarse.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        UserDetails userEntity = null;
        String username = "";
        String password = "";
        try {
            // userEntity = new
            // ObjectMapper().readValue(request.getInputStream(),UserEntity.class);

            userEntity = userDetailsService.loadUserByUsername(request.getParameter("username"));
            System.out.println("INFO -> " + userEntity.getUsername());
            username = userEntity.getUsername();
            password = userEntity.getPassword();

            System.out.println("USUARIO: " + username + " - PASSWORTD: " + password);
        } catch (Exception e) {
            System.out.println("Tenemos un fallo al obtener el usuario");
        }

        // Una vez obtenido el usuario, intentamos authenticarlo. Para ello, necesitamos
        // llamar a la siguiente clase:

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);
        // El método getAuthenticationManager(), es de una de las interfaces que
        // implementamos y sirve para authenticar al usuario.
        return getAuthenticationManager().authenticate(authenticationToken);
    }

    // Generamos el token.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        // authResult, es el objeto que nos trae todos los detalles del usuario
        // authentificado.
        User user = (User) authResult.getPrincipal();
        String token = jwtUtils.generateAccessToken(user.getUsername());

        response.addHeader("Authorization", token);

        @SuppressWarnings({ "unchecked", "rawtypes" })
        Map<String, Object> httpResponse = new HashMap();
        httpResponse.put("token", token);
        httpResponse.put("message", "Authenticacion correcta");
        httpResponse.put("Username", user.getUsername());
        httpResponse.put("response", "/probando");
        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Nos aseguramos que todo se escriba correctamente.
        response.getWriter().flush();
        super.successfulAuthentication(request, response, chain, authResult);
        response.sendRedirect("http://www.google.es");
    }

}
