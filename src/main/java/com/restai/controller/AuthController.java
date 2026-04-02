package com.restai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restai.services.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public record LoginRequest(String username, String password) {}
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody LoginRequest request) {
        try {
    	// 1. Create an unauthenticated token from the request data
        var authRequest = new UsernamePasswordAuthenticationToken(
            request.username(), 
            request.password()
        );
        logger.info("Received login request for user: {}", request.username());
        // 2. Delegate authentication to the Manager (checks DB and PasswordEncoder)
        // If credentials are wrong, this throws a BadCredentialsException (401)
        Authentication authResponse = authenticationManager.authenticate(authRequest);
        logger.info("Received authResponse for user: {}", authResponse.getName());

        // 3. If successful, generate the JWT for this user
        String token = jwtService.generateToken(authResponse.getName());
        logger.info("Received authResponse for user: {}, generated token: {}", authResponse.getName(), token);

        // 4. Return the token to the client
        return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("Invalid username or password");
        }
    }
}
