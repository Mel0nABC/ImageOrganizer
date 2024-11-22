package com.example.weblogin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.weblogin.filters.JwtAuthenticationFilter;
import com.example.weblogin.filters.JwtAuthorizationFilter;
import com.example.weblogin.jwt.JwtUtils;
import com.example.weblogin.persistence.repository.UserRepository;
import com.example.weblogin.service.UserDetailServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        JwtAuthorizationFilter jwtAuthorizationFilter;

        @Autowired
        JwtUtils jwtUtils;

        @Autowired
        UserDetailsService userDetailsService;

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity httpsSecurity, AuthenticationManager authenticationManager)
                        throws Exception {
                JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils,
                                userDetailsService);
                jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
                // Se puede especificar cuál es la url para authentificarse.
                // jwtAuthenticationFilter.setFilterProcessesUrl("/login");
                return httpsSecurity
                                .headers(head -> head
                                                .contentTypeOptions(Customizer.withDefaults()))
                                .formLogin(form -> form
                                                .loginPage("/login").permitAll()
                                                .defaultSuccessUrl("/galeria", true))
                                .authorizeHttpRequests(
                                                auth -> auth
                                                                .requestMatchers(HttpMethod.GET, "/images/**",
                                                                                "/css/**", "/webfonts/**")
                                                                .permitAll()
                                                                .requestMatchers("/showSetAdminUser").permitAll()
                                                                .requestMatchers("/setAdminUser").permitAll()
                                                                .requestMatchers("/logout").permitAll()
                                                                .requestMatchers("/uploadImg").authenticated()
                                                                // .requestMatchers("/login").permitAll()
                                                                .anyRequest().authenticated())
                                // .sessionManagement(session -> {
                                // session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                                // })
                                // .addFilter(jwtA      uthenticationFilter)
                                .csrf(t -> t.disable())
                                .build();

        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public AuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailService) {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
                provider.setPasswordEncoder(passwordEncoder());
                provider.setUserDetailsService(userDetailService);
                return provider;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

}
