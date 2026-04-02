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

import com.restai.services.ImageService;

@Controller
@RequestMapping("/ui")
public class WebUIController {
	
	private static final Logger logger = LoggerFactory.getLogger(WebUIController.class);
	
	
	private ImageService imageService;
	
	@Value("${fastapi.base-url}")
    private String fastApiBaseUrl;
	
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
	    // 1. Process the file (e.g., save to /data/downloads)
	    // 2. Add attributes for the UI
	    model.addAttribute("message", "File uploaded successfully!");
	    
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
	    Map<String, String> response = restTemplate.getForObject(url, Map.class); 
	    
	    model.addAttribute("classification", response.get("result"));
	    return "fragments/classification-results"; 
	    
	   //  model.addAttribute("classification", result);
	  //  return "fragments/classification-result"; // A small <div> to show the text
	}
}
