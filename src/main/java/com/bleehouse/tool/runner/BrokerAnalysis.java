package com.bleehouse.tool.runner;

import com.bleehouse.tool.model.ParseResult;
import com.bleehouse.tool.service.LogAnalysisService;
import com.bleehouse.tool.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BrokerAnalysis implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BrokerAnalysis.class);
    
    @Autowired
    private LogAnalysisService logAnalysisService;
    
    @Autowired
    private ReportService reportService;
    
    @Override
    public void run(String... args) throws Exception {
        
        if (args.length < 2) {
            printUsage();
            return;
        }
        
        logger.info("범용 로그 분석 시작");
        
        String logPath = args[0];
        String startDateStr = args[1];
        String patternGroup = args.length > 2 ? args[2] : "kafka"; // 기본값: kafka
        String outputFormat = args.length > 3 ? args[3] : "TEXT"; // 기본값: TEXT
        
        try {
            // 시작 날짜 파싱
            LocalDateTime startDate = parseStartDate(startDateStr);
            
            // 로그 분석 수행
            List<ParseResult> results = analyzeLogPath(logPath, patternGroup, startDate);
            
            if (results.isEmpty()) {
                logger.info("분석할 로그 파일이 없거나 매칭된 패턴이 없습니다.");
                return;
            }
            
            // 리포트 생성
            String reportFileName = reportService.generateReport(results, outputFormat, "log_analysis_report");
            
            logger.info("분석 완료! 리포트 파일: reports/{}", reportFileName);
            printSummary(results);
            
        } catch (Exception e) {
            logger.error("로그 분석 중 오류 발생", e);
            throw e;
        }
    }
    
    private void printUsage() {
        System.out.println("사용법: java -jar app.jar <로그경로> <시작날짜> [패턴그룹] [출력형식]");
        System.out.println("  로그경로: 분석할 로그 파일 또는 디렉토리 경로");
        System.out.println("  시작날짜: yyyy-MM-dd 형식 (예: 2024-01-01)");
        System.out.println("  패턴그룹: " + logAnalysisService.getAvailablePatternGroups() + " (기본값: kafka)");
        System.out.println("  출력형식: " + reportService.getAvailableFormats() + " (기본값: TEXT)");
    }
    
    private LocalDateTime parseStartDate(String startDateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDateTime.parse(startDateStr + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다. yyyy-MM-dd 형식을 사용하세요: " + startDateStr, e);
        }
    }
    
    private List<ParseResult> analyzeLogPath(String logPath, String patternGroup, LocalDateTime startDate) {
        java.io.File file = new java.io.File(logPath);
        
        if (file.isFile()) {
            ParseResult result = logAnalysisService.analyzeFile(logPath, patternGroup, startDate);
            return List.of(result);
        } else if (file.isDirectory()) {
            return logAnalysisService.analyzeDirectory(logPath, patternGroup, startDate);
        } else {
            throw new IllegalArgumentException("존재하지 않는 파일 또는 디렉토리: " + logPath);
        }
    }
    
    private void printSummary(List<ParseResult> results) {
        int totalFiles = results.size();
        int totalMatches = results.stream().mapToInt(r -> r.getMatchedEntries().size()).sum();
        long totalTime = results.stream().mapToLong(ParseResult::getProcessingTimeMs).sum();
        
        logger.info("=== 분석 요약 ===");
        logger.info("처리된 파일 수: {}", totalFiles);
        logger.info("총 매칭된 항목: {}", totalMatches);
        logger.info("총 처리 시간: {}ms", totalTime);
    }
}