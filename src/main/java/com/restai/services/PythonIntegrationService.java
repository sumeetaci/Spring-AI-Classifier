package com.restai.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class PythonIntegrationService {
	private static final Logger logger = LoggerFactory.getLogger(PythonIntegrationService.class);
	
	// private final RestClient restClient;
	public record PythonResponse(String result, String status) {}
	public record PythonRequest(String data) {}
	public record DirectoryRequest(String path, String message) {}
	
	// New for interactive chat
	public record SessionDirectoryRequest(String path, String message, String conversationId) {}

	private final RestClient fastApiClient;

    public PythonIntegrationService(RestClient fastApiClient) {
        this.fastApiClient = fastApiClient;
    }

    public String processPromptRequest(String folderName, String message) {
      try {
    	// Sends the "pointer" path to the FastAPI endpoint
        return fastApiClient.post()
                .uri("/process-directory")
                .contentType(MediaType.APPLICATION_JSON) 
                .body(new DirectoryRequest(folderName, message))
                .retrieve()
                .body(String.class);
      	}
        catch (WebClientResponseException e) {
            // This will print the EXACT reason FastAPI rejected the data
            logger.error("Error Body: " + e.getResponseBodyAsString());
            throw e;
        }
    }
    
    
 // METHOD 2: The New Interactive Method
    public String processInteractiveRequest(String folderName, String message, String conversationId) {
        try {
            return fastApiClient.post()
                    .uri("/process-interactive") // New FastAPI endpoint
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new SessionDirectoryRequest(folderName, message, conversationId))
                    .retrieve()
                    .body(String.class);
        } catch (WebClientResponseException e) {
            logger.error("Interactive API Error: {}", e.getResponseBodyAsString());
            throw e;
        }
    }
    
}
