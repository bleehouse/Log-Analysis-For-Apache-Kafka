# 범용 로그 분석 도구 (Universal Log Analysis Tool)

## 개요

이 프로젝트는 다양한 형태의 로그 파일을 분석하여 특정 패턴을 찾고 리포트를 생성하는 범용 로그 분석 도구입니다. 
기존의 Kafka/Confluent 로그 분석에서 시작하여 Apache, Nginx 등 다양한 로그 형식을 지원하도록 확장되었습니다.

## 주요 특징

- 🔧 **설정 기반 패턴 관리**: YAML 파일을 통한 유연한 패턴 설정
- 🔌 **플러그인 아키텍처**: 새로운 로그 형식을 쉽게 추가 가능
- 📅 **스마트 날짜 처리**: 다양한 날짜 형식 자동 감지 및 파싱
- 📊 **다양한 출력 형식**: TEXT, JSON 등 다양한 리포트 형식 지원
- ⚡ **고성능**: 스트림 처리와 정규식 사전 컴파일로 최적화
- 📁 **재귀 디렉토리 처리**: 디렉토리 전체를 재귀적으로 분석

## 기술 스택

- **Java 11**
- **Spring Boot 2.5.2**
- **Maven**
- **Jackson (YAML/JSON 처리)**
- **SLF4J + Logback**

## 프로젝트 구조

```
src/main/java/com/bleehouse/tool/
├── ToolApplication.java              # Spring Boot 메인 애플리케이션
├── config/
│   └── LogPatternConfig.java         # 패턴 설정 모델
├── file/                             # 레거시 파일 처리 (호환성)
│   ├── LogProcessing.java
│   └── Pattern.java
├── model/                            # 데이터 모델
│   ├── LogEntry.java                 # 로그 엔트리 모델
│   └── ParseResult.java              # 파싱 결과 모델
├── output/                           # 출력 포맷터
│   ├── OutputFormatter.java         # 출력 포맷터 인터페이스
│   └── impl/
│       ├── JsonOutputFormatter.java # JSON 출력 구현
│       └── TextOutputFormatter.java # 텍스트 출력 구현
├── parser/                           # 로그 파서
│   ├── LogParser.java               # 파서 인터페이스
│   └── impl/
│       └── GenericLogParser.java    # 범용 파서 구현
├── runner/
│   └── BrokerAnalysis.java          # 메인 실행 클래스
├── service/                         # 비즈니스 로직
│   ├── ConfigService.java           # 설정 관리 서비스
│   ├── LogAnalysisService.java      # 로그 분석 서비스
│   └── ReportService.java           # 리포트 생성 서비스
└── util/
    └── DateTimeUtil.java            # 날짜/시간 유틸리티
```

## 설치 및 빌드

### 사전 요구사항
- Java 11 이상
- Maven 3.6 이상

### 빌드
```bash
# 프로젝트 클론
git clone <repository-url>
cd log-analysis-tool

# Maven 빌드
mvn clean package

# 실행 가능한 JAR 파일 생성
# target/maintenance-0.0.4-SNAPSHOT.jar
```

## 사용법

### 기본 사용법
```bash
java -jar target/maintenance-0.0.4-SNAPSHOT.jar <로그경로> <시작날짜> [패턴그룹] [출력형식]
```

### 매개변수 설명
- **로그경로**: 분석할 로그 파일 또는 디렉토리 경로
- **시작날짜**: `yyyy-MM-dd` 형식 (예: 2024-01-01)
- **패턴그룹**: 사용할 패턴 그룹 (기본값: kafka)
- **출력형식**: 리포트 출력 형식 (기본값: TEXT)

### 사용 예시

#### 1. 기본 Kafka 로그 분석
```bash
java -jar target/maintenance-0.0.4-SNAPSHOT.jar /path/to/kafka/logs 2024-01-01
```

#### 2. Apache 로그 분석 (JSON 출력)
```bash
java -jar target/maintenance-0.0.4-SNAPSHOT.jar /path/to/apache/logs 2024-01-01 apache JSON
```

#### 3. 단일 파일 분석
```bash
java -jar target/maintenance-0.0.4-SNAPSHOT.jar /path/to/server.log 2024-01-01 kafka TEXT
```

#### 4. 디렉토리 전체 분석
```bash
java -jar target/maintenance-0.0.4-SNAPSHOT.jar /var/log 2024-01-01 kafka JSON
```

## 설정 파일

### log-patterns.yml
로그 패턴과 출력 형식을 정의하는 YAML 설정 파일입니다.

```yaml
patterns:
  kafka:
    name: "Kafka/Confluent Patterns"
    patterns:
      - pattern: "Starting controlled shutdown"
        severity: "INFO"
        category: "BROKER"
        description: "Broker shutdown initiated"
      - pattern: "Fatal error"
        severity: "FATAL"
        category: "BROKER"
        description: "Fatal error occurred"
      # ... 더 많은 패턴들

  apache:
    name: "Apache HTTP Server Patterns"
    patterns:
      - pattern: "\\[error\\]"
        severity: "ERROR"
        category: "HTTP"
        description: "HTTP server error"
      # ... 더 많은 패턴들

dateFormats:
  - "yyyy-MM-dd HH:mm:ss"
  - "yyyy-MM-dd HH:mm:ss,SSS"
  - "MMM dd HH:mm:ss"
  - "yyyy/MM/dd HH:mm:ss"
  - "dd/MMM/yyyy:HH:mm:ss Z"

outputFormats:
  - "TEXT"
  - "JSON"
  - "CSV"
```

## 출력 형식

### TEXT 형식
```
********************************************************************************************************
          2024-01-15 14:30:25           범용 로그 분석 리포트
********************************************************************************************************

[server.log] 분석 결과
[1] : [2024-01-15 10:30:15,123] INFO Starting controlled shutdown
[5] : [2024-01-15 10:30:19,345] WARN Attempt to heartbeat failed since group is rebalancing
--------------------------------------------------------------------------------
 Pattern1 (Starting controlled shutdown): 1
 Pattern3 (Attempt to heartbeat failed since group is rebalancing): 1
 총 처리 라인: 100, 처리 시간: 45ms
--------------------------------------------------------------------------------
```

### JSON 형식
```json
{
  "reportGeneratedAt": "2024-01-15T14:30:25.123",
  "totalFiles": 1,
  "totalMatches": 2,
  "totalProcessingTimeMs": 45,
  "results": [
    {
      "fileName": "server.log",
      "totalLines": 100,
      "processingTimeMs": 45,
      "matchedEntries": [
        {
          "lineNumber": 1,
          "originalLine": "[2024-01-15 10:30:15,123] INFO Starting controlled shutdown",
          "severity": "INFO",
          "category": "BROKER",
          "matchedPattern": "Starting controlled shutdown",
          "patternDescription": "Broker shutdown initiated",
          "timestamp": "2024-01-15T10:30:15.123"
        }
      ],
      "patternCounts": {
        "Starting controlled shutdown": 1,
        "Fatal error": 0
      }
    }
  ]
}
```

## 확장 방법

### 새로운 패턴 그룹 추가

1. `src/main/resources/log-patterns.yml` 파일에 새 패턴 그룹 추가:
```yaml
patterns:
  nginx:
    name: "Nginx Patterns"
    patterns:
      - pattern: "\\[error\\]"
        severity: "ERROR"
        category: "WEB"
        description: "Nginx error"
```

2. 애플리케이션 재시작

### 새로운 출력 형식 추가

1. `OutputFormatter` 인터페이스를 구현하는 새 클래스 생성:
```java
@Component
public class CsvOutputFormatter implements OutputFormatter {
    @Override
    public String format(List<ParseResult> results) {
        // CSV 형식으로 변환 로직
    }
    
    @Override
    public String getFormatType() {
        return "CSV";
    }
    
    @Override
    public String getFileExtension() {
        return ".csv";
    }
}
```

2. `@Component` 어노테이션으로 Spring에 등록하면 자동으로 사용 가능

### 새로운 로그 파서 추가

1. `LogParser` 인터페이스를 구현하는 새 클래스 생성:
```java
@Component
public class CustomLogParser implements LogParser {
    @Override
    public ParseResult parseLog(InputStream inputStream, String patternGroup, LocalDateTime startDate) {
        // 커스텀 파싱 로직
    }
    
    @Override
    public boolean canParse(String fileName, InputStream inputStream) {
        // 이 파서가 처리할 수 있는 파일인지 판단
    }
    
    @Override
    public int getPriority() {
        return 10; // 높은 우선순위
    }
}
```

## 성능 최적화

### 메모리 사용량 최적화
- 스트림 기반 파일 처리로 대용량 파일 지원
- 정규식 패턴 사전 컴파일로 CPU 사용량 감소

### 처리 속도 향상
- 패턴 매칭 시 첫 번째 매치에서 중단
- 날짜 파싱 캐싱 (향후 구현 예정)

## 문제 해결

### 일반적인 문제들

#### 1. 메모리 부족 오류
```bash
# 힙 메모리 증가
java -Xmx2g -jar target/maintenance-0.0.4-SNAPSHOT.jar ...
```

#### 2. 날짜 파싱 실패
- `log-patterns.yml`의 `dateFormats` 섹션에 새로운 날짜 형식 추가

#### 3. 패턴 매칭 안됨
- 정규식 특수문자는 이스케이프 처리 필요 (`\\[`, `\\]` 등)

### 로그 레벨 조정
```bash
# DEBUG 레벨로 실행
java -Dlogging.level.com.bleehouse.tool=DEBUG -jar target/maintenance-0.0.4-SNAPSHOT.jar ...
```

## 개발 환경 설정

### IDE 설정
1. Java 11 SDK 설정
2. Maven 프로젝트로 import
3. Spring Boot 플러그인 설치 권장

### 테스트 실행
```bash
# 단위 테스트 실행
mvn test

# 통합 테스트 실행
mvn integration-test

# 샘플 로그로 테스트
java -jar target/maintenance-0.0.4-SNAPSHOT.jar src/test/resources/sample-kafka.log 2024-01-01
```

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 기여하기

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 변경 이력

### v0.0.4-SNAPSHOT
- 패키지명을 `com.gtg`에서 `com.bleehouse`로 변경
- 범용 로그 파서 아키텍처 도입
- YAML 기반 설정 시스템 구현
- JSON 출력 형식 추가
- 성능 최적화 및 메모리 사용량 개선

### 이전 버전
- Kafka/Confluent 로그 전용 분석 도구

## 지원 및 문의

- 이슈 리포트: GitHub Issues
- 기능 요청: GitHub Discussions
- 이메일: [연락처]

---

**참고**: 이 도구는 대용량 로그 파일 처리를 위해 설계되었으며, 프로덕션 환경에서의 사용을 권장합니다.