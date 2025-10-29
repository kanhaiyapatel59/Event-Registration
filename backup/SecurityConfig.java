package com.smartevents.event_registration_system.config;

import com.smartevents.event_registration_system.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // Public routes - anyone can access
                .requestMatchers(
                    "/",
                    "/home",
                    "/css/**",
                    "/js/**", 
                    "/images/**",
                    "/register/**",
                    "/login",
                    "/about"
                ).permitAll()
                
                // User routes - only authenticated users
                .requestMatchers(
                    "/events/list",
                    "/events/view/**",
                    "/registrations/register/**",
                    "/registrations/my-registrations",
                    "/feedback/submit/**"
                ).hasAnyRole("USER", "ADMIN")
                
                // Admin only routes
                .requestMatchers(
                    "/events/new",
                    "/events/edit/**",
                    "/events/delete/**",
                    "/registrations/all",
                    "/registrations/delete/**", 
                    "/registrations/cancel/**",
                    "/feedback/all",
                    "/feedback/delete/**",
                    "/feedback/approve/**",
                    "/feedback/pending"
                ).hasRole("ADMIN")
                
                // Any other request requires authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }
}