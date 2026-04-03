package com.restai.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.restai.common.ControllerUtils;
import com.restai.common.Utils;
import com.restai.models.ClassificationResult;
import com.restai.services.ImageService;

@Controller
@RequestMapping("/ui")
public class WebUIController {
	
	private static final Logger logger = LoggerFactory.getLogger(WebUIController.class);
	
	
	private ImageService imageService;
	
	@Value("${fastapi.base-url}")
    private String fastApiBaseUrl;
	
	@Value("${app.prompt-dir}")
	private String prompt_dir;
	
	@Value("${app.prompt-output-file}")
	private String prompt_response_file;
	
	@Value("${app.prompt-query}") 
	private String prompt_query;
	
	private final RestTemplate restTemplate;
	WebUIController(ImageService imageService, RestTemplate restTemplate) {
		this.imageService = imageService;
		this.restTemplate = restTemplate;
	}

	@GetMapping({"", "/"}) // Matches /ui and /ui/
    public String index(Model model) {
		logger.info("Accessing the main UI page");
        model.addAttribute("message", "Hello from Spring Boot!");
        return "index"; // This looks for src/main/resources/templates/index.html
    }
	
	
	@GetMapping("/generate-tags")
    public String generateTags(Model model) {
        // Simulate calling your FastAPI service or Logic
        List<String> tags = List.of("Vintage", "Denim", "Casual", "Summer-Vibes");
        
        model.addAttribute("tags", tags);
        
        // Returns 'fragments/tag-list.html' instead of index.html
        return "fragments/tag-list"; 
    }
	
	@PostMapping("/upload")
	public String handleUpload(@RequestParam("file") MultipartFile file, Model model) {
		logger.info("Received file upload: " + file.getOriginalFilename());
		String username = "ANONYMUS"; // In a real app, get this from the authenticated session
		ControllerUtils controllerUtils = new ControllerUtils();
	    // 1. Process the file (e.g., save to /data/downloads)
	    // 2. Add attributes for the UI
		try {
			MultipartFile[] files = new MultipartFile[] {file}; // Wrap single file in an array for reuse of existing logic
		String userDirPathString = controllerUtils.createUniqueDirectoryAndSetPermissions(username, prompt_dir,prompt_response_file );
		controllerUtils.uploadFiles(files, userDirPathString);
   	 	// String outputFilePath = Utils.resolvePath(userDirPathString, Utils.RESPONSE_PROMPT_FILE);
        // File file = new File(outputFilePath);
        // String message = query_message != null ? prompt_query.concat(query_message) : prompt_query;
		model.addAttribute("message", "File uploaded successfully!");
		}catch(Exception e) {
			logger.error("Error processing file upload: " + e.getMessage());
			model.addAttribute("message", "Failed to process the uploaded file.");
			return "fragments/classification-results"; // Show error in the same fragment
		}
	    // 3. Return the fragment name
	    return "fragments/classification-results"; 
	}
	
	@GetMapping("/gallery")
	public String showGallery(Model model) {
		logger.info("Accessing the image gallery");
	    List<String> images = imageService.getDownloadedImages();
	    model.addAttribute("images", images);
	    return "fragments/image-gallery"; // A new fragment
	}
	
	@GetMapping("/classify")
	public String classify(@RequestParam String filename, Model model) {
		logger.info("Classifying image: " + filename);
	    String url = fastApiBaseUrl+"/classify?filename=" + filename;
	    // String result = restTemplate.getForObject(url, String.class);
	 // Extract as a Map to get the "result" key specifically
	    // Map<String, String> response = restTemplate.getForObject(url, Map.class); 
	    ClassificationResult data = restTemplate.getForObject(url, ClassificationResult.class);
	    String responseText = data.getResult().getResponse();
	    
	    logger.info("Received classification result: " + responseText);
	    // logger.info("Classifying image by provider: " + response.get("provider") + " with result: " + response.get("result"));
	    model.addAttribute("classification", responseText);
	    return "fragments/classification-results"; 
	    
	   //  model.addAttribute("classification", result);
	  //  return "fragments/classification-result"; // A small <div> to show the text
	}
}
