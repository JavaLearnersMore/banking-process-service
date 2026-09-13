package com.example.account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth.antMatchers("/core/api/v1/accounts").hasAnyRole("SERVICE", "OPS")
                .anyRequest()
                .authenticated()
            )

            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsManager users() {

        UserDetails serviceUser =
                User.withUsername("service")
                        .password("{noop}service123")
                        .roles("SERVICE")
                        .build();

        UserDetails opsUser =
                User.withUsername("ops")
                        .password("{noop}ops123")
                        .roles("OPS")
                        .build();

        return new InMemoryUserDetailsManager(
                serviceUser,
                opsUser
        );
    }
}