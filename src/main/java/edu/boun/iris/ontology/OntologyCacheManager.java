package edu.boun.iris.ontology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class OntologyCacheManager {
    
    private static final Logger logger = LoggerFactory.getLogger(OntologyCacheManager.class);
    
    private static final String CACHE_DIR_NAME = ".iris";
    private static final String ONTOLOGY_CACHE_SUBDIR = "ontology-cache";
    private static final String METADATA_SUFFIX = ".metadata";
    
    private final Path cacheDirectory;
    
    public OntologyCacheManager() {
        String userHome = System.getProperty("user.home");
        this.cacheDirectory = Paths.get(userHome, CACHE_DIR_NAME, ONTOLOGY_CACHE_SUBDIR);
        initializeCacheDirectory();
    }
    
    private void initializeCacheDirectory() {
        try {
            if (!Files.exists(cacheDirectory)) {
                Files.createDirectories(cacheDirectory);
                logger.info("Created cache directory: {}", cacheDirectory);
            }
        } catch (IOException e) {
            logger.error("Failed to create cache directory: {}", cacheDirectory, e);
        }
    }
    
    public boolean isCached(OntologySource source) {
        Path cacheFile = getCacheFilePath(source);
        return Files.exists(cacheFile);
    }
    
    public InputStream getCachedOntology(OntologySource source) throws IOException {
        Path cacheFile = getCacheFilePath(source);
        if (!Files.exists(cacheFile)) {
            throw new FileNotFoundException("Cache file not found for: " + source.getName());
        }
        return Files.newInputStream(cacheFile);
    }
    
    public void cacheOntology(OntologySource source, InputStream ontologyData, String etag, String lastModified) throws IOException {
        Path cacheFile = getCacheFilePath(source);
        
        try (OutputStream out = Files.newOutputStream(cacheFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = ontologyData.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        
        saveMetadata(source, etag, lastModified);
        logger.info("Cached ontology for: {}", source.getName());
    }
    
    public long getLastRetrievalTime(OntologySource source) {
        Path metadataFile = getMetadataFilePath(source);
        if (!Files.exists(metadataFile)) {
            return 0;
        }
        
        try (InputStream in = Files.newInputStream(metadataFile)) {
            Properties metadata = new Properties();
            metadata.load(in);
            String timestamp = metadata.getProperty("timestamp");
            if (timestamp != null) {
                return Long.parseLong(timestamp);
            }
        } catch (IOException | NumberFormatException e) {
            logger.warn("Failed to read metadata for: {}", source.getName(), e);
        }
        
        return 0;
    }
    
    public String getETag(OntologySource source) {
        return getMetadataProperty(source, "etag");
    }
    
    public String getLastModified(OntologySource source) {
        return getMetadataProperty(source, "lastModified");
    }
    
    private String getMetadataProperty(OntologySource source, String propertyName) {
        Path metadataFile = getMetadataFilePath(source);
        if (!Files.exists(metadataFile)) {
            return null;
        }
        
        try (InputStream in = Files.newInputStream(metadataFile)) {
            Properties metadata = new Properties();
            metadata.load(in);
            return metadata.getProperty(propertyName);
        } catch (IOException e) {
            logger.warn("Failed to read metadata property {} for: {}", propertyName, source.getName(), e);
        }
        
        return null;
    }
    
    private void saveMetadata(OntologySource source, String etag, String lastModified) throws IOException {
        Path metadataFile = getMetadataFilePath(source);
        
        Properties metadata = new Properties();
        metadata.setProperty("timestamp", String.valueOf(System.currentTimeMillis()));
        if (etag != null) {
            metadata.setProperty("etag", etag);
        }
        if (lastModified != null) {
            metadata.setProperty("lastModified", lastModified);
        }
        
        try (OutputStream out = Files.newOutputStream(metadataFile)) {
            metadata.store(out, "Ontology cache metadata for: " + source.getName());
        }
    }
    
    public boolean isUpdateNeeded(OntologySource source) {
        if (!isCached(source)) {
            return true;
        }
        
        long lastRetrievalTime = getLastRetrievalTime(source);
        if (lastRetrievalTime == 0) {
            return true;
        }
        
        long currentTime = System.currentTimeMillis();
        long daysSinceLastRetrieval = (currentTime - lastRetrievalTime) / (1000 * 60 * 60 * 24);
        
        return daysSinceLastRetrieval >= source.getUpdateFrequencyDays();
    }
    
    public void clearCache(OntologySource source) {
        Path cacheFile = getCacheFilePath(source);
        Path metadataFile = getMetadataFilePath(source);
        
        try {
            if (Files.exists(cacheFile)) {
                Files.delete(cacheFile);
            }
            if (Files.exists(metadataFile)) {
                Files.delete(metadataFile);
            }
            logger.info("Cleared cache for: {}", source.getName());
        } catch (IOException e) {
            logger.error("Failed to clear cache for: {}", source.getName(), e);
        }
    }
    
    private Path getCacheFilePath(OntologySource source) {
        String fileName = sanitizeFileName(source.getName()) + ".owl";
        return cacheDirectory.resolve(fileName);
    }
    
    private Path getMetadataFilePath(OntologySource source) {
        String fileName = sanitizeFileName(source.getName()) + METADATA_SUFFIX;
        return cacheDirectory.resolve(fileName);
    }
    
    private String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
    
    public Path getCacheDirectory() {
        return cacheDirectory;
    }
}
