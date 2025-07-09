# 콘서트 예약 시스템 
---

## 1. 프로젝트 개요

Concert Mania는 공연 예매 시스템으로, 사용자는 회원가입과 로그인을 통해 공연 좌석을 임시 점유하고 예약할 수 있습니다.  
관리자는 공연(콘서트)을 등록하고 좌석을 관리하며, 결제 API를 통해 예약 상태를 ‘결제 완료’로 변경하는 기능을 제공합니다.

주요 특징
- JWT 기반 인증 및 권한 관리
- Redis 분산 락을 활용한 좌석 동시성 제어
- Spring Boot + JPA 기반 안정적인 백엔드 구현
- Swagger를 통한 API 문서화 및 테스트 지원

---

## 2. 기술 스택

| 구분          | 기술/라이브러리                   |
|---------------|---------------------------------|
| 언어          | Java 17                         |
| 프레임워크    | Spring Boot 3.x, Spring Security (JWT) |
| 데이터베이스  | MySQL 8.x                      |
| 캐시/분산락   | Redis                          |
| 빌드 및 실행  | Gradle                         |
| API 문서화    | Swagger                       |
| 테스트        | JUnit 5, Mockito               |
| 로깅 및 모니터링 | Spring Boot Logging, Micrometer |

---

## 3. 실행 방법

### 3.1. 사전 준비
- Docker 및 Docker Compose 설치 (MySQL, Redis 컨테이너 실행 권장)
- `application-dev.yml` 파일에서 DB 및 Redis 설정 확인 및 수정

### 3.2. 실행
```bash
./gradlew bootRun
```

### 3.3. Swagger 접속

> http://localhost:8080/swagger-ui/index.html


---

### 4. API 사용 가이드

#### 4.1. 회원가입 및 로그인

- **회원가입**  
  `POST /api/users/signup`  
  사용자 및 관리자 역할별 등록 가능 (관리자 이메일은 별도 권한 부여 필요)


- **로그인**  
  `POST /api/users/login`  
  로그인 후 JWT 토큰 발급

#### 4.2. 관리자 권한 콘서트 등록

- `POST /api/admin/concerts`

- 요청 예시
```json
{
  "title": "2025 여름 페스티벌",
  "venue": "서울 올림픽 경기장",
  "concertDate": "2025-09-01T19:00:00",
  "openAt": "2025-07-10T10:00:00",
  "closeAt": "2025-08-31T23:59:59",
  "seatGroups": [
    {"grade": "VIP", "count": 100, "price": 200000},
    {"grade": "R석", "count": 500, "price": 100000},
    {"grade": "S석", "count": 1000, "price": 50000}
  ]
}
```


#### 4.3. 좌석 임시 점유 및 예약 생성
- `POST /api/reservations/seat/{seatId}/user/{userId}/reserve`  
-  사용자 및 관리자 역할별 등록 가능 (관리자 이메일은 별도 권한 부여 필요)

#### 4.4. 예약 결제 처리
- `POST /api/reservations/{reservationId}/pay`
- 임시 점유된 예약에 대해 결제 API 호출 시 예약 상태를 ‘결제 완료’로 변경합니다.

---

### 5. 테스트
- 단위 테스트 및 통합 테스트 포함
- 테스트 실행 명령어

```
bash
./gradlew test
```

### 6. 모니터링 및 로깅
- Spring Boot 기본 로깅 기능 사용
- Micrometer를 이용한 예약 및 좌석 점유 카운터 수집

### 7. 참고사항
- 좌석 임시 점유 후 10분 내 결제하지 않으면 자동으로 좌석이 해제됩니다 (스케줄러 적용)
- 관리자 권한 부여는 회원가입 후 직접 DB 또는 관리자 전용 API로 권한 변경 필요
- JWT 토큰은 각 요청 헤더 Authorization: Bearer {token} 에 포함해야 합니다