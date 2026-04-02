package com.restai.common;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restai.entity.ImageMetadata;
import com.restai.repositories.ImageRepository;

public class ControllerUtils {
	private static final Logger logger = LoggerFactory.getLogger(ControllerUtils.class);

	private boolean isValidImage(String contentType) {
        return contentType.equals("image/jpeg") || contentType.equals("image/png");
    }
    public boolean isFileSafe(MultipartFile file) throws IOException {
        Tika tika = new Tika();
        String detectedType = tika.detect(file.getInputStream());
        return detectedType.equals("image/jpeg") || detectedType.equals("image/png");
    }
    public  String createUniqueDirectoryAndSetPermissions(String username, String prompt_dir, String prompt_response_file) throws IOException {
		logger.debug("Creating directory for prompt files.");
    	Utils.setPromptDirAndOutputFile(prompt_dir, prompt_response_file); 		
		logger.debug("PATH is :"+Utils.PROMPT_DIR); 
		
        Path path = Paths.get(Utils.PROMPT_DIR, username); 
        // It creates it if missing, and does nothing if it already exists.
		Files.createDirectories(path);
		String uuid_dir_pathString = path.toAbsolutePath().toString(); 
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
	    Files.setPosixFilePermissions(path, perms);
	    logger.debug("PATH uuid_dir_pathString is :::::"+uuid_dir_pathString); 
         // If you need the trailing slash (Python sometimes prefers it):
	     if (!uuid_dir_pathString.endsWith("/")) {
	         uuid_dir_pathString += "/";
	     }
		return uuid_dir_pathString;
	}

/**
 * Handles the file response logic for both existing and missing output file.
 * If the file exists and is readable, it returns it as content of the file.
 * If the file is missing, it wraps the provided message in a Resource and returns it as plain text.
 */
public  ResponseEntity<Resource> handleFileResponse(Resource resource, String message, ImageRepository imageRepository) throws IOException {	
	if(resource == null) {
		logger.debug("Resource is null, returning message as plain text.");
		return handleExceptionResponse("Resource is null. " + message);
	}
	if (resource.exists() || resource.isReadable()) {
		logger.debug("Resource exists.");
		List<String> saveFiles = saveFilesToDB(resource, imageRepository); // Pass your ImageRepository instance here
		ContentDisposition contentDisposition = ContentDisposition.attachment()
			    .filename(resource.getFilename())
			    .build();
		return ResponseEntity.ok()
			.header("X-Saved-Files", String.join(", ", saveFiles)) 
            //.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
			.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .body(resource);
	    } else {
	        // Handle case where path is returned but file is missing
	    	// 2. Wrap the message in a Resource
	    	logger.debug("Resource not exists");
	        Resource resourceWithoutContent = new ByteArrayResource(message.getBytes(StandardCharsets.UTF_8));
	        return ResponseEntity.status(HttpStatus.OK) // or HttpStatus.NOT_FOUND
	                .contentType(MediaType.TEXT_PLAIN)
	                .body(resourceWithoutContent);
	    }
	}


public  ResponseEntity<Resource> handleExceptionResponse(String message) {		
        // Handle case where path is returned but file is missing
    	// 2. Wrap the message in a Resource
        Resource resourceWithErrorContent = new ByteArrayResource(message.getBytes(StandardCharsets.UTF_8));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // or HttpStatus.NOT_FOUND
                .contentType(MediaType.TEXT_PLAIN)
                .body(resourceWithErrorContent);
    }


public  void uploadFiles(MultipartFile[] files, String userDirPathString) throws IOException {
	for (MultipartFile file : files) {
         // Logic to save the file (e.g., to a local directory or cloud storage)
       	 if(file.isEmpty()) {
       		 logger.debug("Skipping empty file: " + file.getOriginalFilename());
				 continue;
       	 }
       	 if(file.getContentType() == null || !isValidImage(file.getContentType())) {
       		 logger.debug("Skipping non-image file: " + file.getOriginalFilename() + " with content type: " + file.getContentType());
       	 }
            byte[] bytes = file.getBytes();
            Path path = Paths.get(userDirPathString + file.getOriginalFilename());
            Files.write(path, bytes);
            logger.debug("File uploaded successfully: " + file.getOriginalFilename());
            
        
    }
}

/*
 * This saving can be optimized by async process
 */
public List<String> saveFilesToDB(Resource resource, ImageRepository repository) throws IOException { 
	// Implement your logic to save the file to the database here
	// This could involve converting the file to a byte array and storing it in a BLOB column
	// Or you could store metadata about the file and save  the actual file to disk or cloud storage
	// String username, String category, List<String> tags,
	ObjectMapper mapper = new ObjectMapper();
	List<String> savedFileNames = new ArrayList<>();
	String username = SecurityContextHolder.getContext().getAuthentication().getName();
	// Read the resource stream into a Map
	List<Map<String, Object>> entries = mapper.readValue(
		    resource.getInputStream(), 
		    new TypeReference<List<Map<String, Object>>>() {}
		);
	Path resourcePath = Paths.get(resource.getURI());
    // 2. Get the parent directory (one level up)
    Path parentPath = resourcePath.getParent();
	for (Map<String, Object> entry : entries) {
		ImageMetadata imgMetadata = new ImageMetadata();
		String filename = (String) entry.get("fileName");
		String category = (String) entry.get("category");
		if(filename == null || filename.isEmpty()) {
			logger.info("Skipping entry with missing fileName.");
			continue;
		}
		byte[] imageBytes = getImageBytes(parentPath, filename);
		if(imageBytes == null) {
			logger.info("Skipping entry due to missing file for fileName: " + filename);
			continue;
		}
		imgMetadata.setFileData(imageBytes);
		if(category == null || category.isEmpty()) {
			logger.info("Assigning category with missing category for file: Uncategorized " );
			category = "Uncategorized";
		}
		List<String> tags =  new ArrayList<>();
		imgMetadata.setFileName((String) entry.get("image_name"));
		imgMetadata.setFileCategory((String) entry.get("category"));
		imgMetadata.setModelProvider(entry.get("provider") != null ? (String) entry.get("provider") : "Unknown");
		imgMetadata.setModelName(entry.get("model") != null ? (String) entry.get("modelName") : "Unknown");
		Object tagList = entry.get("tags");
	    if (tagList != null && tagList instanceof List<?> list) {
	    	tags.addAll(tags);
	    }else {
	    	logger.info("No tags found for file: " + filename);
	    }
	    
	    imgMetadata.setFileTags(tags);
	    imgMetadata.setUploadedBy(username); 
	    imgMetadata.setFileCreatedAt(LocalDateTime.now());
	    
	    repository.save(imgMetadata);
	    savedFileNames.add(filename);
	}
	return savedFileNames;
	 
}
	private byte[] getImageBytes(Path parentPath, String filename) throws IOException {
		// Implement your logic to save the file to the database here
		// This could involve converting the file to a byte array and storing it in a BLOB column
		// Or you could store metadata about the file and save  the actual file to disk or cloud storage
		Path imageFilePath = parentPath.resolve(filename);
		if (!Files.exists(imageFilePath)) {
			logger.error("File not found for metadata saving: " + imageFilePath.toAbsolutePath().toString());
			return null;
		}
		byte[] imageBytes = Files.readAllBytes(imageFilePath);
	    return imageBytes;
	    
	}
}