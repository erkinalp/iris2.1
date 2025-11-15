package edu.boun.iris.ontology;

import java.io.InputStream;

public interface OntologyRetriever {
    
    InputStream retrieveOntology(OntologySource source) throws OntologyRetrievalException;
    
    boolean isUpdateAvailable(OntologySource source) throws OntologyRetrievalException;
    
    long getLastRetrievalTime(OntologySource source);
    
    void clearCache(OntologySource source);
}
