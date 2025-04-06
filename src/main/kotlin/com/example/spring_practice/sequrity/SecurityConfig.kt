package com.example.spring_practice.sequrity

import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain{

        return httpSecurity
            .csrf{ csrf -> csrf.disable()}
            .sessionManagement{it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)}
            .authorizeHttpRequests{ auth ->
                auth.requestMatchers("/auth/**")
                    .permitAll()
                    .dispatcherTypeMatchers(
                        DispatcherType.ERROR,
                        DispatcherType.FORWARD
                    )
                    .permitAll()
                    .anyRequest()
                    .authenticated()

            }
            .build()

    }
}