package edu.boun.iris.ontology;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RespectfulHttpClient {
    
    private static final Logger logger = LoggerFactory.getLogger(RespectfulHttpClient.class);
    
    private static final int CONNECTION_TIMEOUT_MS = 30000;
    private static final int SOCKET_TIMEOUT_MS = 60000;
    private static final long RATE_LIMIT_DELAY_MS = 2000;
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 1000;
    
    private static final String USER_AGENT = "IRIS-Protege-Plugin/2.1.0 (Bogazici University; https://github.com/erkinalp/iris2.1)";
    
    private final Map<String, Long> lastRequestTimeByDomain = new ConcurrentHashMap<>();
    private final Map<String, Boolean> robotsTxtAllowedCache = new ConcurrentHashMap<>();
    
    private final CloseableHttpClient httpClient;
    
    public RespectfulHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECTION_TIMEOUT_MS)
                .setSocketTimeout(SOCKET_TIMEOUT_MS)
                .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
                .build();
        
        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setUserAgent(USER_AGENT)
                .build();
    }
    
    public HttpResponse executeGet(String url, Map<String, String> headers) throws IOException, OntologyRetrievalException {
        URI uri = URI.create(url);
        String domain = uri.getHost();
        
        if (!isAllowedByRobotsTxt(url)) {
            throw new OntologyRetrievalException("Access to " + url + " is disallowed by robots.txt");
        }
        
        enforceRateLimit(domain);
        
        HttpGet httpGet = new HttpGet(url);
        
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }
        
        return executeWithRetry(httpGet);
    }
    
    private void enforceRateLimit(String domain) {
        Long lastRequestTime = lastRequestTimeByDomain.get(domain);
        if (lastRequestTime != null) {
            long timeSinceLastRequest = System.currentTimeMillis() - lastRequestTime;
            if (timeSinceLastRequest < RATE_LIMIT_DELAY_MS) {
                long sleepTime = RATE_LIMIT_DELAY_MS - timeSinceLastRequest;
                try {
                    logger.debug("Rate limiting: sleeping for {} ms before request to {}", sleepTime, domain);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Rate limit sleep interrupted", e);
                }
            }
        }
        lastRequestTimeByDomain.put(domain, System.currentTimeMillis());
    }
    
    private HttpResponse executeWithRetry(HttpGet httpGet) throws IOException, OntologyRetrievalException {
        int attempt = 0;
        IOException lastException = null;
        
        while (attempt < MAX_RETRIES) {
            try {
                HttpResponse response = httpClient.execute(httpGet);
                int statusCode = response.getStatusLine().getStatusCode();
                
                if (statusCode >= 200 && statusCode < 300) {
                    return response;
                } else if (statusCode == 429 || statusCode >= 500) {
                    logger.warn("Received status code {} on attempt {}, will retry", statusCode, attempt + 1);
                    if (attempt < MAX_RETRIES - 1) {
                        long backoffTime = INITIAL_BACKOFF_MS * (long) Math.pow(2, attempt);
                        try {
                            Thread.sleep(backoffTime);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new OntologyRetrievalException("Retry interrupted", e);
                        }
                    }
                } else {
                    throw new OntologyRetrievalException("HTTP request failed with status code: " + statusCode);
                }
            } catch (IOException e) {
                lastException = e;
                logger.warn("Request failed on attempt {}: {}", attempt + 1, e.getMessage());
                if (attempt < MAX_RETRIES - 1) {
                    long backoffTime = INITIAL_BACKOFF_MS * (long) Math.pow(2, attempt);
                    try {
                        Thread.sleep(backoffTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new OntologyRetrievalException("Retry interrupted", ie);
                    }
                }
            }
            attempt++;
        }
        
        if (lastException != null) {
            throw lastException;
        }
        throw new OntologyRetrievalException("Failed to execute request after " + MAX_RETRIES + " attempts");
    }
    
    private boolean isAllowedByRobotsTxt(String url) {
        try {
            URI uri = URI.create(url);
            String domain = uri.getHost();
            String cacheKey = domain + uri.getPath();
            
            if (robotsTxtAllowedCache.containsKey(cacheKey)) {
                return robotsTxtAllowedCache.get(cacheKey);
            }
            
            String robotsTxtUrl = uri.getScheme() + "://" + domain + "/robots.txt";
            
            try {
                HttpGet httpGet = new HttpGet(robotsTxtUrl);
                HttpResponse response = httpClient.execute(httpGet);
                
                if (response.getStatusLine().getStatusCode() == 200) {
                    InputStream content = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    
                    boolean isUserAgentSection = false;
                    boolean isAllowed = true;
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        
                        if (line.toLowerCase().startsWith("user-agent:")) {
                            String userAgent = line.substring(11).trim();
                            isUserAgentSection = userAgent.equals("*") || USER_AGENT.contains(userAgent);
                        } else if (isUserAgentSection && line.toLowerCase().startsWith("disallow:")) {
                            String disallowedPath = line.substring(9).trim();
                            if (!disallowedPath.isEmpty() && uri.getPath().startsWith(disallowedPath)) {
                                isAllowed = false;
                                break;
                            }
                        }
                    }
                    
                    reader.close();
                    robotsTxtAllowedCache.put(cacheKey, isAllowed);
                    return isAllowed;
                } else {
                    robotsTxtAllowedCache.put(cacheKey, true);
                    return true;
                }
            } catch (IOException e) {
                logger.debug("Could not fetch robots.txt for {}, assuming allowed", domain);
                robotsTxtAllowedCache.put(cacheKey, true);
                return true;
            }
        } catch (Exception e) {
            logger.warn("Error checking robots.txt, assuming allowed", e);
            return true;
        }
    }
    
    public void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }
}
