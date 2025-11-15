package edu.boun.iris.ontology;

public class OntologyRetrievalException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public OntologyRetrievalException(String message) {
        super(message);
    }
    
    public OntologyRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public OntologyRetrievalException(Throwable cause) {
        super(cause);
    }
}
