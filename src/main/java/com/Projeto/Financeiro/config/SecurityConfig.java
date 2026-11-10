package com.Projeto.Financeiro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import com.Projeto.Financeiro.security.JwtAuthenticationFilter;

@Configuration 
public class SecurityConfig {
    
    @Bean 
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http) throws Exception {

     http
         .csrf(csrf -> csrf.disable())

         .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                HttpMethod.POST,
                "/auth/register",
                "/auth/login"
            ).permitAll()

            .anyRequest().authenticated()
        )

        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable())

        .addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
            }

    
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter>
            jwFilterRegistration(
                    JwtAuthenticationFilter filter) {
            FilterRegistrationBean<JwtAuthenticationFilter> registration = 
                    new FilterRegistrationBean<>(filter);

            registration.setEnabled(false);

            return registration;
        }
    
}