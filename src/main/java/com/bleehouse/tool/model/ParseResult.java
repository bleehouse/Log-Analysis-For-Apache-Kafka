package com.bleehouse.tool.model;

import java.util.List;
import java.util.Map;

public class ParseResult {
    
    private List<LogEntry> matchedEntries;
    private Map<String, Integer> patternCounts;
    private String fileName;
    private int totalLines;
    private long processingTimeMs;
    
    public ParseResult() {}
    
    public ParseResult(List<LogEntry> matchedEntries, Map<String, Integer> patternCounts) {
        this.matchedEntries = matchedEntries;
        this.patternCounts = patternCounts;
    }
    
    public List<LogEntry> getMatchedEntries() {
        return matchedEntries;
    }
    
    public void setMatchedEntries(List<LogEntry> matchedEntries) {
        this.matchedEntries = matchedEntries;
    }
    
    public Map<String, Integer> getPatternCounts() {
        return patternCounts;
    }
    
    public void setPatternCounts(Map<String, Integer> patternCounts) {
        this.patternCounts = patternCounts;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public int getTotalLines() {
        return totalLines;
    }
    
    public void setTotalLines(int totalLines) {
        this.totalLines = totalLines;
    }
    
    public long getProcessingTimeMs() {
        return processingTimeMs;
    }
    
    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
}