package com.bleehouse.tool.output.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.bleehouse.tool.model.ParseResult;
import com.bleehouse.tool.output.OutputFormatter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JsonOutputFormatter implements OutputFormatter {
    
    private final ObjectMapper objectMapper;
    
    public JsonOutputFormatter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    @Override
    public String format(List<ParseResult> results) {
        try {
            Map<String, Object> report = new HashMap<>();
            report.put("reportGeneratedAt", LocalDateTime.now());
            report.put("totalFiles", results.size());
            report.put("totalMatches", results.stream().mapToInt(r -> r.getMatchedEntries().size()).sum());
            report.put("totalProcessingTimeMs", results.stream().mapToLong(ParseResult::getProcessingTimeMs).sum());
            report.put("results", results);
            
            return objectMapper.writeValueAsString(report);
        } catch (Exception e) {
            throw new RuntimeException("JSON 포맷팅 실패", e);
        }
    }
    
    @Override
    public String getFormatType() {
        return "JSON";
    }
    
    @Override
    public String getFileExtension() {
        return ".json";
    }
}