package edu.boun.iris.ontology;

import java.io.Serializable;

public class OntologySource implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String url;
    private String format;
    private int updateFrequencyDays;
    private boolean enabled;
    private String description;
    
    public OntologySource(String name, String url, String format, int updateFrequencyDays, boolean enabled, String description) {
        this.name = name;
        this.url = url;
        this.format = format;
        this.updateFrequencyDays = updateFrequencyDays;
        this.enabled = enabled;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public int getUpdateFrequencyDays() {
        return updateFrequencyDays;
    }
    
    public void setUpdateFrequencyDays(int updateFrequencyDays) {
        this.updateFrequencyDays = updateFrequencyDays;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "OntologySource{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", format='" + format + '\'' +
                ", updateFrequencyDays=" + updateFrequencyDays +
                ", enabled=" + enabled +
                ", description='" + description + '\'' +
                '}';
    }
}
