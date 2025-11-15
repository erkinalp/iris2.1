package edu.boun.iris.ontology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class OntologySourceLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(OntologySourceLoader.class);
    
    private static final String CONFIG_FILE = "/ontology-sources.properties";
    
    public static List<OntologySource> loadOntologySources() {
        List<OntologySource> sources = new ArrayList<>();
        
        try (InputStream input = OntologySourceLoader.class.getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                logger.error("Unable to find ontology sources configuration file: {}", CONFIG_FILE);
                return sources;
            }
            
            Properties prop = new Properties();
            prop.load(input);
            
            Map<String, Map<String, String>> sourceMap = new HashMap<>();
            
            for (String key : prop.stringPropertyNames()) {
                if (key.startsWith("source.")) {
                    String[] parts = key.split("\\.", 3);
                    if (parts.length == 3) {
                        String sourceId = parts[1];
                        String propertyName = parts[2];
                        String value = prop.getProperty(key);
                        
                        sourceMap.putIfAbsent(sourceId, new HashMap<>());
                        sourceMap.get(sourceId).put(propertyName, value);
                    }
                }
            }
            
            for (Map.Entry<String, Map<String, String>> entry : sourceMap.entrySet()) {
                Map<String, String> properties = entry.getValue();
                
                String name = properties.get("name");
                String url = properties.get("url");
                String format = properties.get("format");
                String updateFrequencyStr = properties.get("updateFrequencyDays");
                String enabledStr = properties.get("enabled");
                String description = properties.get("description");
                
                if (name != null && url != null) {
                    int updateFrequency = 7;
                    if (updateFrequencyStr != null) {
                        try {
                            updateFrequency = Integer.parseInt(updateFrequencyStr);
                        } catch (NumberFormatException e) {
                            logger.warn("Invalid updateFrequencyDays for {}, using default: 7", name);
                        }
                    }
                    
                    boolean enabled = true;
                    if (enabledStr != null) {
                        enabled = Boolean.parseBoolean(enabledStr);
                    }
                    
                    OntologySource source = new OntologySource(
                        name,
                        url,
                        format != null ? format : "rdf/xml",
                        updateFrequency,
                        enabled,
                        description != null ? description : ""
                    );
                    
                    sources.add(source);
                    logger.debug("Loaded ontology source: {}", name);
                }
            }
            
            logger.info("Loaded {} ontology sources from configuration", sources.size());
            
        } catch (IOException e) {
            logger.error("Error loading ontology sources configuration", e);
        }
        
        return sources;
    }
}
