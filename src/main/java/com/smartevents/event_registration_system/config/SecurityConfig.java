package com.smartevents.event_registration_system.config;

import com.smartevents.event_registration_system.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/", "/home", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/register", "/login", "/process-register").permitAll()
                
                // User endpoints
                .requestMatchers("/events/list", "/events/view/**", "/registrations/register/**", 
                               "/registrations/my-registrations", "/feedback/submit/**").hasAnyRole("USER", "ADMIN")
                
                // Admin-only endpoints
                .requestMatchers("/events/new", "/events/create", "/events/edit/**", "/events/update/**", 
                               "/events/delete/**", "/registrations/all", "/registrations/event/**",
                               "/registrations/cancel/**", "/registrations/delete/**", "/feedback/all",
                               "/feedback/approve/**", "/feedback/delete/**", "/feedback/pending").hasRole("ADMIN")
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home") // Redirect to home after login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}