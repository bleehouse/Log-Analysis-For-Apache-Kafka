package com.bleehouse.tool.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class DateTimeUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);
    
    /**
     * 여러 날짜 형식을 시도하여 문자열을 LocalDateTime으로 파싱합니다.
     */
    public static Optional<LocalDateTime> parseDateTime(String dateStr, List<String> dateFormats) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return Optional.empty();
        }
        
        for (String format : dateFormats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                return Optional.of(LocalDateTime.parse(dateStr.trim(), formatter));
            } catch (DateTimeParseException e) {
                // 다음 형식 시도
            }
        }
        
        logger.debug("날짜 파싱 실패: {}", dateStr);
        return Optional.empty();
    }
    
    /**
     * 로그 라인에서 날짜 부분을 추출합니다.
     */
    public static Optional<String> extractDateFromLine(String line) {
        if (line == null || line.length() < 19) {
            return Optional.empty();
        }
        
        // 일반적인 날짜 패턴들을 시도
        String[] possibleDateParts = {
            line.substring(0, Math.min(19, line.length())), // yyyy-MM-dd HH:mm:ss
            line.substring(1, Math.min(20, line.length())), // [yyyy-MM-dd HH:mm:ss
            extractBetweenBrackets(line),
            extractFirstTimestamp(line)
        };
        
        for (String datePart : possibleDateParts) {
            if (datePart != null && !datePart.isEmpty()) {
                return Optional.of(datePart);
            }
        }
        
        return Optional.empty();
    }
    
    private static String extractBetweenBrackets(String line) {
        int start = line.indexOf('[');
        int end = line.indexOf(']');
        if (start >= 0 && end > start) {
            return line.substring(start + 1, end);
        }
        return null;
    }
    
    private static String extractFirstTimestamp(String line) {
        // 정규식을 사용하여 타임스탬프 패턴 찾기
        String timestampPattern = "\\d{4}[-/]\\d{2}[-/]\\d{2}[T\\s]\\d{2}:\\d{2}:\\d{2}";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(timestampPattern);
        java.util.regex.Matcher matcher = pattern.matcher(line);
        
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}