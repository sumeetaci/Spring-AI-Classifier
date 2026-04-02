package com.restai.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ImageService {
	   private final Path rootLocation = Paths.get("/data/downloads");

	    public List<String> getDownloadedImages() {
	        try {
	            if (!Files.exists(rootLocation)) return List.of();
	            
	            return Files.walk(this.rootLocation, 1)
	                    .filter(path -> !path.equals(this.rootLocation))
	                    .map(path -> path.getFileName().toString())
	                    .limit(10)
	                    .collect(Collectors.toList());
	        } catch (IOException e) {
	            return List.of();
	        }
	    }
}
