package com.restai.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

// import org.simdjson.JsonValue;
// import org.simdjson.SimdJsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;



public class Utils {
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	
	public static final String  FullDateWithTimePattern = "yyyy-MM-dd HH:mm:ss"; 
	public static final String  BASE_PATH = "src/main/resources/";
	public static final String  DOCKER_BASE_PATH = "classpath:";
	public static final String  PYTHON_PATH =  BASE_PATH+"python/"; 
	public static final String  RULES_PATH =  BASE_PATH+"rules/"; 
	
	public static final String REQUEST_PROMPT_PATH = "REQUEST_PROMPT_PATH";
	public static final String QUERY_MESSAGE = "message";
	
	public static final String RESPONSE_PROMPT_FILE_OLD = "output_response.txt";
	public static  String RESPONSE_PROMPT_FILE;
	public static String PROMPT_DIR;
	
	
	public static void setPromptDirAndOutputFile(String promptDir, String outputFile) {
        Utils.PROMPT_DIR = promptDir;
        Utils.RESPONSE_PROMPT_FILE = outputFile;
     // Optional: Create the directory as soon as the value is injected
        File directory = new File(Utils.PROMPT_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
	
	public static byte[] getByteContentFromResource(ResourceLoader resourceLoader, String resolvedPathString) {
		if (resolvedPathString == null) {
    	    throw new IllegalStateException("Resolved path string cannot be null");
    	}
		Resource resource = resourceLoader.getResource(resolvedPathString);
        try (InputStream is = resource.getInputStream()) {
            byte[] jsonBytes = is.readAllBytes();
            logger.debug("Got content for resource: "+resolvedPathString);
            return jsonBytes;
        } catch (IOException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Exception occured: "+resolvedPathString); 
		}
        return null;
	}
    
	public static final DateTimeFormatter FullDateWithtimeFormatter = DateTimeFormatter.ofPattern(FullDateWithTimePattern);
	
	public String getDateFromUnixEpoch(long unixTimestamp) {
	// 1. Convert seconds to an Instant
    Instant instant = Instant.ofEpochSecond(unixTimestamp);

    // 2. Define a Time Zone (e.g., UTC or System Default)
    ZonedDateTime dateTime = instant.atZone(ZoneId.of("UTC"));

    // 3. Format the output
    //  DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FullDateWithTimePattern);
    logger.debug("Formatted Date: " + dateTime.format(FullDateWithtimeFormatter));
    return dateTime.format(FullDateWithtimeFormatter);
	}
	
	public static String resolvePath(String path, String filename) {
	    // This example assumes your scripts are in a specific directory 
	    // relative to your project's root, e.g., "src/main/resources/" 
	    // or "src/test/resources/". Adjust the base path as necessary.
	    File file = new File(path + filename); 
	    return file.getAbsolutePath();
	}
    
	
	
}
