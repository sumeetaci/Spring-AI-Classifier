package com.restai.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import com.restai.common.Utils;

@Service
public class DirectoryCleanupService {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryCleanupService.class); 
    
    @Scheduled(initialDelay = 5000, fixedRate = 900000) // Runs every 15 minutes after 5 seconds initial delay
    public void cleanupOldDirectories() {
    	if(Utils.PROMPT_DIR == null) {
        	logger.debug("Directory for prompt files not existing.");
        }
        else {
        	Path rootPath = Paths.get(Utils.PROMPT_DIR);
	        logger.debug("Directory for prompt files getting cleaned :"+rootPath.toAbsolutePath().toString());
	        if (!Files.exists(rootPath)) return;
	
	        Instant cutoff = Instant.now().minus(15, ChronoUnit.MINUTES);
	        AtomicLong totalBytesDeleted = new AtomicLong(0);
	        AtomicLong folderCount = new AtomicLong(0);
	
	        try (Stream<Path> paths = Files.list(rootPath)) {
	            paths.filter(Files::isDirectory)
	                 .filter(path -> isOlderThan(path, cutoff))
	                 .forEach(path -> {
	                     long size = calculateDirectorySize(path);
	                     if (deleteDirectory(path)) {
	                         totalBytesDeleted.addAndGet(size);
	                         folderCount.incrementAndGet();
	                     }
	                 });
	        } catch (IOException e) {
	            logger.error("Cleanup process failed", e);
	        }
	
	        // Log the final summary in MB
	        double totalMB = totalBytesDeleted.get() / (1024.0 * 1024.0);
	        logger.info("Cleanup Finished: Removed {} folders and freed {:.2f} MB of disk space.", 
	                    folderCount.get(), totalMB);
	        }
        }

    private long calculateDirectorySize(Path path) {
        try (Stream<Path> walk = Files.walk(path)) {
            return walk.filter(Files::isRegularFile)
                       .mapToLong(p -> {
                           try { return Files.size(p); } 
                           catch (IOException e) { return 0L; }
                       }).sum();
        } catch (IOException e) {
            return 0L;
        }
    }

    private boolean deleteDirectory(Path path) {
        try {
            return FileSystemUtils.deleteRecursively(path);
        } catch (IOException e) {
            logger.error("Could not delete: " + path, e);
            return false;
        }
    }

    private boolean isOlderThan(Path path, Instant cutoff) {
        try {
            return Files.readAttributes(path, BasicFileAttributes.class)
                        .lastModifiedTime().toInstant().isBefore(cutoff);
        } catch (IOException e) {
            return false;
        }
    }
}
