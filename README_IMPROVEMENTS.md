# 로그 파싱 로직 개선 사항

## 개선된 기능

### 1. 설정 기반 패턴 관리
- **기존**: 하드코딩된 패턴 배열
- **개선**: YAML 설정 파일을 통한 유연한 패턴 관리
- **파일**: `src/main/resources/log-patterns.yml`

### 2. 플러그인 아키텍처
- **LogParser 인터페이스**: 다양한 로그 형식 지원
- **GenericLogParser**: 범용 로그 파서 구현
- **확장성**: 새로운 파서를 쉽게 추가 가능

### 3. 향상된 날짜 처리
- **DateTimeUtil**: 다양한 날짜 형식 자동 감지
- **유연한 파싱**: 여러 날짜 형식 동시 지원
- **LocalDateTime 사용**: 최신 Java 시간 API 활용

### 4. 다양한 출력 형식
- **TEXT**: 기존 텍스트 형식 (개선됨)
- **JSON**: 구조화된 JSON 출력
- **확장 가능**: 새로운 포맷터 쉽게 추가

### 5. 성능 최적화
- **스트림 처리**: 메모리 효율적인 파일 처리
- **패턴 컴파일**: 정규식 패턴 사전 컴파일
- **병렬 처리 준비**: 향후 병렬 처리 지원 기반

## 사용법

### 기본 사용법
```bash
java -jar app.jar <로그경로> <시작날짜> [패턴그룹] [출력형식]
```

### 예시
```bash
# Kafka 로그 분석 (기본)
java -jar app.jar /path/to/kafka/logs 2024-01-01

# Apache 로그 분석, JSON 출력
java -jar app.jar /path/to/apache/logs 2024-01-01 apache JSON

# 단일 파일 분석
java -jar app.jar /path/to/server.log 2024-01-01 kafka TEXT
```

## 설정 파일 구조

### log-patterns.yml
```yaml
patterns:
  kafka:
    name: "Kafka/Confluent Patterns"
    patterns:
      - pattern: "Starting controlled shutdown"
        severity: "INFO"
        category: "BROKER"
        description: "Broker shutdown initiated"
      # ... 더 많은 패턴들

dateFormats:
  - "yyyy-MM-dd HH:mm:ss"
  - "yyyy-MM-dd HH:mm:ss,SSS"
  # ... 더 많은 날짜 형식들

outputFormats:
  - "TEXT"
  - "JSON"
  - "CSV"
```

## 새로운 패턴 그룹 추가

1. `log-patterns.yml`에 새 패턴 그룹 추가
2. 필요시 전용 파서 구현 (`LogParser` 인터페이스)
3. 애플리케이션 재시작

## 새로운 출력 형식 추가

1. `OutputFormatter` 인터페이스 구현
2. `@Component` 어노테이션 추가
3. 자동으로 사용 가능한 형식에 포함됨

## 주요 클래스

- **LogAnalysisService**: 로그 분석 핵심 서비스
- **ConfigService**: 설정 파일 관리
- **ReportService**: 리포트 생성 및 저장
- **GenericLogParser**: 범용 로그 파서
- **DateTimeUtil**: 날짜/시간 유틸리티

## 이전 버전과의 호환성

기존 명령행 인터페이스는 유지되며, 새로운 기능들이 추가되었습니다.
기존 사용자는 동일한 방식으로 사용할 수 있습니다.

## 향후 개선 계획

1. **실시간 로그 모니터링**: 파일 변경 감지 및 실시간 분석
2. **웹 인터페이스**: REST API 및 웹 UI 제공
3. **데이터베이스 연동**: 분석 결과 영구 저장
4. **알림 시스템**: 특정 패턴 감지시 알림 발송
5. **성능 모니터링**: 메트릭 수집 및 대시보드