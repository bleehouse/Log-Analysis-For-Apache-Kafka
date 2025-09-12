package com.gtg.tool.config;

import java.util.List;
import java.util.Map;

public class LogPatternConfig {
    
    private Map<String, PatternGroup> patterns;
    private List<String> dateFormats;
    private List<String> outputFormats;
    
    public Map<String, PatternGroup> getPatterns() {
        return patterns;
    }
    
    public void setPatterns(Map<String, PatternGroup> patterns) {
        this.patterns = patterns;
    }
    
    public List<String> getDateFormats() {
        return dateFormats;
    }
    
    public void setDateFormats(List<String> dateFormats) {
        this.dateFormats = dateFormats;
    }
    
    public List<String> getOutputFormats() {
        return outputFormats;
    }
    
    public void setOutputFormats(List<String> outputFormats) {
        this.outputFormats = outputFormats;
    }
    
    public static class PatternGroup {
        private String name;
        private List<PatternDefinition> patterns;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public List<PatternDefinition> getPatterns() {
            return patterns;
        }
        
        public void setPatterns(List<PatternDefinition> patterns) {
            this.patterns = patterns;
        }
    }
    
    public static class PatternDefinition {
        private String pattern;
        private String severity;
        private String category;
        private String description;
        
        public String getPattern() {
            return pattern;
        }
        
        public void setPattern(String pattern) {
            this.pattern = pattern;
        }
        
        public String getSeverity() {
            return severity;
        }
        
        public void setSeverity(String severity) {
            this.severity = severity;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
    }
}