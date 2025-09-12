package com.gtg.tool.parser.impl;

import com.gtg.tool.config.LogPatternConfig;
import com.gtg.tool.model.LogEntry;
import com.gtg.tool.model.ParseResult;
import com.gtg.tool.parser.LogParser;
import com.gtg.tool.service.ConfigService;
import com.gtg.tool.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class GenericLogParser implements LogParser {
    
    private static final Logger logger = LoggerFactory.getLogger(GenericLogParser.class);
    
    @Autowired
    private ConfigService configService;
    
    @Override
    public ParseResult parseLog(InputStream inputStream, String patternGroup, LocalDateTime startDate) {
        long startTime = System.currentTimeMillis();
        
        LogPatternConfig.PatternGroup group = configService.getPatternGroup(patternGroup);
        if (group == null) {
            throw new IllegalArgumentException("패턴 그룹을 찾을 수 없습니다: " + patternGroup);
        }
        
        List<LogEntry> matchedEntries = new ArrayList<>();
        Map<String, Integer> patternCounts = new HashMap<>();
        
        // 패턴을 미리 컴파일
        Map<String, Pattern> compiledPatterns = group.getPatterns().stream()
            .collect(Collectors.toMap(
                LogPatternConfig.PatternDefinition::getPattern,
                p -> Pattern.compile(p.getPattern(), Pattern.CASE_INSENSITIVE)
            ));
        
        // 패턴 카운트 초기화
        group.getPatterns().forEach(p -> patternCounts.put(p.getPattern(), 0));
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int lineNumber = 0;
            int totalLines = 0;
            
            while ((line = reader.readLine()) != null) {
                totalLines++;
                lineNumber++;
                
                // 각 패턴에 대해 매칭 확인
                for (LogPatternConfig.PatternDefinition patternDef : group.getPatterns()) {
                    Pattern compiledPattern = compiledPatterns.get(patternDef.getPattern());
                    
                    if (compiledPattern.matcher(line).find()) {
                        // 날짜 필터링
                        if (startDate != null && !isAfterStartDate(line, startDate)) {
                            continue;
                        }
                        
                        LogEntry entry = createLogEntry(lineNumber, line, patternDef);
                        matchedEntries.add(entry);
                        
                        patternCounts.merge(patternDef.getPattern(), 1, Integer::sum);
                        break; // 첫 번째 매칭된 패턴만 처리
                    }
                }
            }
            
            ParseResult result = new ParseResult(matchedEntries, patternCounts);
            result.setTotalLines(totalLines);
            result.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            
            logger.info("로그 파싱 완료 - 총 라인: {}, 매칭된 항목: {}, 처리 시간: {}ms", 
                       totalLines, matchedEntries.size(), result.getProcessingTimeMs());
            
            return result;
            
        } catch (IOException e) {
            logger.error("로그 파싱 중 오류 발생", e);
            throw new RuntimeException("로그 파싱 실패", e);
        }
    }
    
    private LogEntry createLogEntry(int lineNumber, String line, LogPatternConfig.PatternDefinition patternDef) {
        LogEntry entry = new LogEntry(lineNumber, line, patternDef.getPattern());
        entry.setSeverity(patternDef.getSeverity());
        entry.setCategory(patternDef.getCategory());
        entry.setPatternDescription(patternDef.getDescription());
        
        // 타임스탬프 추출 시도
        DateTimeUtil.extractDateFromLine(line)
            .flatMap(dateStr -> DateTimeUtil.parseDateTime(dateStr, configService.getConfig().getDateFormats()))
            .ifPresent(entry::setTimestamp);
        
        return entry;
    }
    
    private boolean isAfterStartDate(String line, LocalDateTime startDate) {
        return DateTimeUtil.extractDateFromLine(line)
            .flatMap(dateStr -> DateTimeUtil.parseDateTime(dateStr, configService.getConfig().getDateFormats()))
            .map(timestamp -> timestamp.isAfter(startDate) || timestamp.isEqual(startDate))
            .orElse(true); // 날짜를 파싱할 수 없으면 포함
    }
    
    @Override
    public List<String> getSupportedPatternGroups() {
        return new ArrayList<>(configService.getConfig().getPatterns().keySet());
    }
    
    @Override
    public boolean canParse(String fileName, InputStream inputStream) {
        // 범용 파서이므로 모든 파일을 처리할 수 있음
        return true;
    }
    
    @Override
    public int getPriority() {
        return 0; // 가장 낮은 우선순위 (기본 파서)
    }
}