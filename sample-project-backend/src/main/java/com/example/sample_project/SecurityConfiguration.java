package com.example.sample_project;

/* */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
 public class SecurityConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") //http://localhost:5173
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
                //.allowCredentials(false);
    }

    @Bean
    @SneakyThrows
    public SecurityFilterChain authenticatedEndpointFilterChain (HttpSecurity http) {
        log.info ("Configuring web security");

        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults())
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            );


        return http.build();

    }
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http.csrf(AbstractHttpConfigurer::disable)
    //       .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
    //               authorizationManagerRequestMatcherRegistry.requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")
    //                       .requestMatchers("/admin/**").hasAnyRole("ADMIN")
    //                       .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
    //                       .requestMatchers("/login/**").permitAll()
    //                       .anyRequest().authenticated())
    //       .httpBasic(Customizer.withDefaults())
    //       .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    //     return http.build();
    // }

}
