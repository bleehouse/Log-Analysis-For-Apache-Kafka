package com.bleehouse.tool.service;

import com.bleehouse.tool.model.ParseResult;
import com.bleehouse.tool.parser.LogParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class LogAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(LogAnalysisService.class);
    
    @Autowired
    private List<LogParser> logParsers;
    
    /**
     * 단일 파일을 분석합니다.
     */
    public ParseResult analyzeFile(String filePath, String patternGroup, LocalDateTime startDate) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다: " + filePath);
        }
        
        LogParser parser = selectParser(file.getName(), filePath);
        
        try (FileInputStream fis = new FileInputStream(file)) {
            ParseResult result = parser.parseLog(fis, patternGroup, startDate);
            result.setFileName(file.getName());
            return result;
        } catch (IOException e) {
            logger.error("파일 분석 중 오류 발생: {}", filePath, e);
            throw new RuntimeException("파일 분석 실패: " + filePath, e);
        }
    }
    
    /**
     * 디렉토리를 재귀적으로 분석합니다.
     */
    public List<ParseResult> analyzeDirectory(String directoryPath, String patternGroup, LocalDateTime startDate) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("디렉토리를 찾을 수 없습니다: " + directoryPath);
        }
        
        List<ParseResult> results = new ArrayList<>();
        analyzeDirectoryRecursive(directory, patternGroup, startDate, results);
        
        logger.info("디렉토리 분석 완료 - 처리된 파일 수: {}", results.size());
        return results;
    }
    
    private void analyzeDirectoryRecursive(File directory, String patternGroup, LocalDateTime startDate, List<ParseResult> results) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            if (file.isDirectory()) {
                analyzeDirectoryRecursive(file, patternGroup, startDate, results);
            } else if (file.isFile() && isLogFile(file)) {
                try {
                    ParseResult result = analyzeFile(file.getAbsolutePath(), patternGroup, startDate);
                    if (!result.getMatchedEntries().isEmpty()) {
                        results.add(result);
                    }
                } catch (Exception e) {
                    logger.warn("파일 처리 중 오류 발생, 건너뜀: {}", file.getAbsolutePath(), e);
                }
            }
        }
    }
    
    private boolean isLogFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".log") || 
               name.endsWith(".txt") || 
               name.contains("log") ||
               name.endsWith(".out");
    }
    
    private LogParser selectParser(String fileName, String filePath) {
        // 우선순위가 높은 파서부터 시도
        return logParsers.stream()
            .sorted(Comparator.comparingInt(LogParser::getPriority).reversed())
            .filter(parser -> {
                try (FileInputStream fis = new FileInputStream(filePath)) {
                    return parser.canParse(fileName, fis);
                } catch (IOException e) {
                    logger.warn("파서 선택 중 오류: {}", fileName, e);
                    return false;
                }
            })
            .findFirst()
            .orElseThrow(() -> new RuntimeException("적합한 파서를 찾을 수 없습니다: " + fileName));
    }
    
    /**
     * 사용 가능한 패턴 그룹 목록을 반환합니다.
     */
    public List<String> getAvailablePatternGroups() {
        return logParsers.stream()
            .flatMap(parser -> parser.getSupportedPatternGroups().stream())
            .distinct()
            .sorted()
            .toList();
    }
}