package com.restai.config;

//import org.springframework.security.config.Customizer;
import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.unit.DataSize;

import com.restai.components.JwtFilter;

import jakarta.servlet.MultipartConfigElement;
// import com.restai.components.RequestDirectoryFilter;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	private final JwtFilter jwtAuthFilter; // Inject your JWT filter

    public SecurityConfig(JwtFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }
	
	 @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	        return authenticationConfiguration.getAuthenticationManager();
	    }
	 	
	 @Bean
	 public MultipartConfigElement multipartConfigElement() {
	     MultipartConfigFactory factory = new MultipartConfigFactory();
	     factory.setMaxFileSize(DataSize.ofMegabytes(10));
	     factory.setMaxRequestSize(DataSize.ofMegabytes(10));
	     return factory.createMultipartConfig();
	 }
	 
	// Chain 0: Permit UI and Static Files (Highest Priority)
		 @Bean
		 @Order(0)
		 public SecurityFilterChain uiFilterChain(HttpSecurity http) throws Exception {
		     http
		         //.securityMatcher("/ui/**", "/files/**", "/fragments/**")
		     	 .securityMatcher("/ui", "/ui/**", "/files/**", "/js/**", "/css/**")
		     	 .csrf(csrf -> csrf.disable()) // Disable for HTMX uploads
		         .authorizeHttpRequests(auth -> auth
		        		 .requestMatchers("/ui/**").permitAll()
		                 .requestMatchers("/files/**").permitAll()
		             .anyRequest().permitAll() // Allow the UI to load without JWT
		         )
		         // Do NOT add jwtAuthFilter here; the browser doesn't have the token in the header
		         .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
	
		     return http.build();
		 }
	    // Chain 1: Specifically for the User Controller (/users/**)
	    @Bean
	    @Order(1) // Higher priority to ensure it matches first
	    public SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
	    	http
	        .securityMatcher("/api/users/**")
	        .csrf(csrf -> csrf.disable()) // Keep disabled for REST APIs
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/api/users/register").permitAll() // Allow public registration
	            .anyRequest().authenticated()
	        )
	     // 1. First, validate the JWT
	         .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
	         .addFilterAfter( jwtAuthFilter,UsernamePasswordAuthenticationFilter.class);
	    	
	        
	    return http.build();
	    }

	    // Chain 2: For everything else (Files, Search, etc.)
	    @Bean
	    @Order(2)
	    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
	        http
	        // 1. THIS IS THE MISSING PIECE
	        .csrf(csrf -> csrf.disable()) 
	        
	        // 2. Ensure session is stateless for JWT
	        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        
	            .authorizeHttpRequests(auth -> auth
	            		.requestMatchers("/api/auth/login").permitAll() 
	            		.requestMatchers("/ai/**").hasRole("USER")
	            		//.requestMatchers("/ai/ask/**", "/ai/*/*", "/ai/search/**").hasRole("USER")
	                .anyRequest().permitAll()
	            )
	            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
	            // .httpBasic(withDefaults());

	        return http.build();
	    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
