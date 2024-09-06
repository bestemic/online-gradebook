package com.pawlik.przemek.onlinegradebook.config;

import com.pawlik.przemek.onlinegradebook.config.filter.JWTTokenValidatorFilter;
import com.pawlik.przemek.onlinegradebook.handler.CustomAccessDeniedHandler;
import com.pawlik.przemek.onlinegradebook.handler.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Collections;
import java.util.List;

@Configuration
public class SecurityConfig {

    private static final String[] WHITE_LIST_URL = {
            "/api/v1/users/login",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    private final HandlerExceptionResolver exceptionResolver;

    public SecurityConfig(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Bean
    public JWTTokenValidatorFilter jwtTokenValidatorFilter() {
        return new JWTTokenValidatorFilter(exceptionResolver);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsConfigurer -> corsConfigurer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
                    config.setMaxAge(3600L);
                    return config;
                })).csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenValidatorFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((request) -> request
                        .requestMatchers(WHITE_LIST_URL).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/roles").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "api/v1/users", "api/v1/users/{userId}/password/reset", "api/v1/users/password/reset/bulk").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/subjects").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/subjects/{subjectId}").hasAnyRole("TEACHER", "STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/v1/subjects").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/classes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/classes/{classId}").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/v1/classes", "/api/v1/classes/{classId}/students/{userId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/classes/{classId}/students/{userId}").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/lessons", "/api/v1/lessons/{lessonId}").hasAnyRole("TEACHER", "STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/v1/lessons").hasRole("TEACHER")

                        .requestMatchers(HttpMethod.GET, "/api/v1/attendances/lesson/{lessonId}").hasAnyRole("TEACHER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/attendances/lesson/{lessonId}/student/{studentId}").hasAnyRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/v1/attendances").hasRole("TEACHER")

                        .requestMatchers(HttpMethod.GET, "/api/v1/grades/subject/{subjectId}").hasAnyRole("TEACHER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/grades/subject/{subjectId}/student/{studentId}").hasAnyRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/v1/grades").hasRole("TEACHER")

                        .requestMatchers(HttpMethod.GET, "/api/v1/materials/subject/{subjectId}", "/api/v1/materials/{materialId}/file").hasAnyRole("TEACHER", "STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/v1/materials").hasRole("TEACHER")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> {
                    e.authenticationEntryPoint(new CustomAuthenticationEntryPoint(exceptionResolver));
                    e.accessDeniedHandler(new CustomAccessDeniedHandler(exceptionResolver));
                });
        return http.build();
    }
}
