package com.restai.services;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class RequestForwardService {
	
	// @Value("${spring.ai.openai.api-key}")
	@Value("${spring.ai.openai.api-key:missing_key}")
	private String openAiKey;
	
	//@Value("${spring.ai.ollama.base-url}")
	@Value("${spring.ai.ollama.base-url:http://localhost:11434}")
	private String OllamaURL;
	
	
	private static final Logger logger = LoggerFactory.getLogger(RequestForwardService.class);
	
	private final PythonIntegrationService pythonService;
	
	
	RequestForwardService(PythonIntegrationService pythonService){
		this.pythonService = pythonService;
	}
	
	@Async("pythonTaskExecutor") 
	public CompletableFuture<String>  gatherFindingsAsyncByFastAPI(String prompt, String prompt_dir) {
		// prompt_dir is where all the files uploaded by user request
		try {
			String output = pythonService.processPromptRequest(prompt_dir, prompt);
			logger.debug("Python Output: " + output);
            return CompletableFuture.completedFuture("Task completed");
        } catch (Exception e) {
            logger.error("Python execution failed", e);
            throw new RuntimeException("Python script failed", e); // Throwing allows error handling in the chain
        }
	}
	
	
	public String  getResultByFastAPI(String prompt, String prompt_dir) {
		// prompt_dir is where all the files uploaded by user request
		try {
			String output = pythonService.processPromptRequest(prompt_dir, prompt);
			logger.debug("Python Output: " + output);
            return output;
        } catch (Exception e) {
            logger.error("Python execution failed", e);
            throw new RuntimeException("Python script failed", e); // Throwing allows error handling in the chain
        }
	}
	
	
}
