package com.ga.DroneCoordinatesGenerator.security;

import com.ga.DroneCoordinatesGenerator.exception.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestClient;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration{
    private MyUserDetailsService myUserDetailsService;
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    public void setMyUserDetailsService(MyUserDetailsService myUserDetailsService, CustomAccessDeniedHandler customAccessDeniedHandler){
        this.myUserDetailsService = myUserDetailsService;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public JWTRequestFilter authenticationJwtTokenFilter(){
        return new JWTRequestFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(
                                "/auth/users/login",
                                "/auth/users/register",
                                "/auth/users/verify",
                                "/auth/users/forget-password",
                                "/auth/users/reset-password"
                        ).permitAll().anyRequest().authenticated()
                        ).exceptionHandling(exception ->
                            exception.accessDeniedHandler(customAccessDeniedHandler)
                        );
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception{
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public RestClient restClient(){
        return RestClient.builder().build();
    }
}
