package edu.boun.iris.ontology;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GenericOntologyRetriever implements OntologyRetriever {
    
    private static final Logger logger = LoggerFactory.getLogger(GenericOntologyRetriever.class);
    
    private final RespectfulHttpClient httpClient;
    private final OntologyCacheManager cacheManager;
    
    public GenericOntologyRetriever() {
        this.httpClient = new RespectfulHttpClient();
        this.cacheManager = new OntologyCacheManager();
    }
    
    @Override
    public InputStream retrieveOntology(OntologySource source) throws OntologyRetrievalException {
        if (!source.isEnabled()) {
            throw new OntologyRetrievalException("Ontology source is disabled: " + source.getName());
        }
        
        if (cacheManager.isCached(source) && !cacheManager.isUpdateNeeded(source)) {
            logger.info("Using cached ontology for: {}", source.getName());
            try {
                return cacheManager.getCachedOntology(source);
            } catch (IOException e) {
                logger.warn("Failed to read cached ontology, will fetch from network", e);
            }
        }
        
        logger.info("Fetching ontology from network: {} ({})", source.getName(), source.getUrl());
        
        try {
            Map<String, String> headers = new HashMap<>();
            
            String acceptHeader = getAcceptHeaderForFormat(source.getFormat());
            headers.put("Accept", acceptHeader);
            
            String etag = cacheManager.getETag(source);
            if (etag != null) {
                headers.put("If-None-Match", etag);
            }
            
            String lastModified = cacheManager.getLastModified(source);
            if (lastModified != null) {
                headers.put("If-Modified-Since", lastModified);
            }
            
            HttpResponse response = httpClient.executeGet(source.getUrl(), headers);
            int statusCode = response.getStatusLine().getStatusCode();
            
            if (statusCode == 304) {
                logger.info("Ontology not modified, using cached version: {}", source.getName());
                return cacheManager.getCachedOntology(source);
            }
            
            if (statusCode >= 200 && statusCode < 300) {
                InputStream content = response.getEntity().getContent();
                
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[8192];
                int bytesRead;
                while ((bytesRead = content.read(data)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }
                
                byte[] ontologyData = buffer.toByteArray();
                
                String responseEtag = null;
                if (response.getFirstHeader("ETag") != null) {
                    responseEtag = response.getFirstHeader("ETag").getValue();
                }
                
                String responseLastModified = null;
                if (response.getFirstHeader("Last-Modified") != null) {
                    responseLastModified = response.getFirstHeader("Last-Modified").getValue();
                }
                
                try {
                    cacheManager.cacheOntology(source, new ByteArrayInputStream(ontologyData), responseEtag, responseLastModified);
                } catch (IOException e) {
                    logger.warn("Failed to cache ontology, continuing with network data", e);
                }
                
                return new ByteArrayInputStream(ontologyData);
            } else {
                throw new OntologyRetrievalException("Failed to retrieve ontology: HTTP " + statusCode);
            }
            
        } catch (IOException e) {
            logger.error("Network error while retrieving ontology: {}", source.getName(), e);
            
            if (cacheManager.isCached(source)) {
                logger.info("Falling back to cached version due to network error");
                try {
                    return cacheManager.getCachedOntology(source);
                } catch (IOException cacheError) {
                    throw new OntologyRetrievalException("Failed to retrieve from network and cache", e);
                }
            }
            
            throw new OntologyRetrievalException("Failed to retrieve ontology from network", e);
        }
    }
    
    @Override
    public boolean isUpdateAvailable(OntologySource source) throws OntologyRetrievalException {
        if (!source.isEnabled()) {
            return false;
        }
        
        return cacheManager.isUpdateNeeded(source);
    }
    
    @Override
    public long getLastRetrievalTime(OntologySource source) {
        return cacheManager.getLastRetrievalTime(source);
    }
    
    @Override
    public void clearCache(OntologySource source) {
        cacheManager.clearCache(source);
    }
    
    private String getAcceptHeaderForFormat(String format) {
        if (format == null) {
            return "application/rdf+xml, text/turtle, application/ld+json, application/owl+xml, */*";
        }
        
        switch (format.toLowerCase()) {
            case "rdf/xml":
            case "rdfxml":
                return "application/rdf+xml, application/xml, text/xml";
            case "turtle":
            case "ttl":
                return "text/turtle, application/x-turtle";
            case "json-ld":
            case "jsonld":
                return "application/ld+json, application/json";
            case "owl/xml":
            case "owlxml":
                return "application/owl+xml, application/xml";
            default:
                return "application/rdf+xml, text/turtle, application/ld+json, application/owl+xml, */*";
        }
    }
    
    public void close() {
        try {
            httpClient.close();
        } catch (IOException e) {
            logger.error("Error closing HTTP client", e);
        }
    }
}
