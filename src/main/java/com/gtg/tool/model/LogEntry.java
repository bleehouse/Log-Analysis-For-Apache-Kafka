package com.gtg.tool.model;

import java.time.LocalDateTime;

public class LogEntry {
    
    private int lineNumber;
    private String originalLine;
    private LocalDateTime timestamp;
    private String severity;
    private String category;
    private String message;
    private String matchedPattern;
    private String patternDescription;
    
    public LogEntry() {}
    
    public LogEntry(int lineNumber, String originalLine, String matchedPattern) {
        this.lineNumber = lineNumber;
        this.originalLine = originalLine;
        this.matchedPattern = matchedPattern;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    public String getOriginalLine() {
        return originalLine;
    }
    
    public void setOriginalLine(String originalLine) {
        this.originalLine = originalLine;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getMatchedPattern() {
        return matchedPattern;
    }
    
    public void setMatchedPattern(String matchedPattern) {
        this.matchedPattern = matchedPattern;
    }
    
    public String getPatternDescription() {
        return patternDescription;
    }
    
    public void setPatternDescription(String patternDescription) {
        this.patternDescription = patternDescription;
    }
}