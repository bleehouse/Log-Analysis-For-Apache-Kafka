package com.bleehouse.tool.output.impl;

import com.bleehouse.tool.model.LogEntry;
import com.bleehouse.tool.model.ParseResult;
import com.bleehouse.tool.output.OutputFormatter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class TextOutputFormatter implements OutputFormatter {
    
    private static final String SEPARATOR = "*".repeat(160);
    private static final String LINE_SEPARATOR = "-".repeat(80);
    
    @Override
    public String format(List<ParseResult> results) {
        StringBuilder sb = new StringBuilder();
        
        // 헤더
        sb.append(SEPARATOR).append("\n");
        sb.append(String.format("%10s%s%10s%s\n", 
            " ", getCurrentDateTime(), " ", "범용 로그 분석 리포트"));
        sb.append(SEPARATOR).append("\n\n");
        
        // 각 파일별 결과
        for (ParseResult result : results) {
            formatFileResult(sb, result);
        }
        
        // 전체 요약
        formatSummary(sb, results);
        
        return sb.toString();
    }
    
    private void formatFileResult(StringBuilder sb, ParseResult result) {
        sb.append(String.format("\n[%s] 분석 결과\n", result.getFileName()));
        
        if (result.getMatchedEntries().isEmpty()) {
            sb.append(" ".repeat(21)).append("매칭된 항목 없음\n");
            return;
        }
        
        // 매칭된 로그 엔트리들
        for (LogEntry entry : result.getMatchedEntries()) {
            sb.append(String.format("[%d] : %s\n", entry.getLineNumber(), entry.getOriginalLine()));
        }
        
        sb.append(LINE_SEPARATOR).append("\n");
        
        // 패턴별 카운트
        int patternNo = 1;
        for (Map.Entry<String, Integer> patternCount : result.getPatternCounts().entrySet()) {
            if (patternCount.getValue() > 0) {
                sb.append(String.format(" Pattern%d (%s): %d\n", 
                    patternNo++, patternCount.getKey(), patternCount.getValue()));
            }
        }
        
        sb.append(String.format(" 총 처리 라인: %d, 처리 시간: %dms\n", 
            result.getTotalLines(), result.getProcessingTimeMs()));
        sb.append(LINE_SEPARATOR).append("\n");
    }
    
    private void formatSummary(StringBuilder sb, List<ParseResult> results) {
        sb.append("\n").append(SEPARATOR).append("\n");
        sb.append("전체 요약\n");
        sb.append(SEPARATOR).append("\n");
        
        int totalFiles = results.size();
        int totalMatches = results.stream()
            .mapToInt(r -> r.getMatchedEntries().size())
            .sum();
        long totalProcessingTime = results.stream()
            .mapToLong(ParseResult::getProcessingTimeMs)
            .sum();
        
        sb.append(String.format("처리된 파일 수: %d\n", totalFiles));
        sb.append(String.format("총 매칭된 항목: %d\n", totalMatches));
        sb.append(String.format("총 처리 시간: %dms\n", totalProcessingTime));
        
        sb.append(SEPARATOR).append("\n");
    }
    
    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    @Override
    public String getFormatType() {
        return "TEXT";
    }
    
    @Override
    public String getFileExtension() {
        return ".log";
    }
}