# WSD Term Project - BlogTP (Spring Boot)

## 1) 프로젝트 개요

### 문제 정의
- 블로그 서비스에서 자주 필요한 기능(게시글/댓글/카테고리/좋아요/북마크/통계)과 인증(JWT)을 **REST API**로 구현하고,
- **MySQL + Redis** 기반으로 데이터/세션(토큰) 처리를 구성한 뒤,
- **Docker Compose로 손쉽게 실행/배포** 가능하도록 만드는 것이 목표입니다.

### 주요 기능 목록
- JWT 기반 인증/인가 (Access/Refresh)
- 게시글 CRUD + 목록 조회(페이지네이션/정렬/검색)
- 댓글 CRUD
- 게시글 좋아요(Like)
- 카테고리 관리
- 북마크(Bookmark)
- 통계(예: 기간별 게시글 수, 상위 작성자 등) *(Swagger/구현 범위에 따라 포함/제외 가능)*
- Rate Limit(요청 제한) 옵션 지원
- Actuator Health 체크 제공

---

## 2) 실행 방법

### 2-1. 로컬 실행 (권장: Docker Compose)
> 로컬 PC에서 MySQL/Redis 따로 설치 안 하고도 바로 실행 가능

#### 1) 환경 변수 준비
cp .env.example .env
2) 실행
bash
코드 복사
docker compose up -d --build
3) 정상 동작 확인
bash
코드 복사
# Health
curl -i http://localhost:8080/actuator/health

# Swagger
# 브라우저: http://localhost:8080/swagger-ui/index.html
4) 종료
bash
코드 복사
docker compose down
2-2. 로컬 실행 (Gradle 직접 실행)
이 방식은 MySQL/Redis가 필요하므로, 보통은 DB/Redis만 docker로 올리고 앱은 로컬에서 띄우는 것을 추천

1) MySQL/Redis만 실행
bash
코드 복사
docker compose up -d mysql redis
2) (선택) .env 로드
IntelliJ Run Config에 env 주입하거나

터미널에서 export로 환경변수 지정

3) 앱 실행
bash
코드 복사
./gradlew clean bootRun
4) 확인
bash
코드 복사
curl -i http://localhost:8080/actuator/health
3) 환경변수 설명 (.env.example와 매칭)
변수	설명	예시
SERVER_PORT	서버 포트	8080
CORS_ALLOWED_ORIGINS	CORS 허용 Origin	http://localhost:3000
RATE_LIMIT_ENABLED	레이트리밋 사용 여부	true/false
RATE_LIMIT_PER_MINUTE	분당 허용 요청 수	120
JWT_SECRET	JWT 서명 키(32자 이상 권장)	CHANGE_ME...
JWT_ISSUER	JWT issuer	blogtp
JWT_ACCESS_MINUTES	Access 토큰 만료(분)	30
JWT_REFRESH_DAYS	Refresh 토큰 만료(일)	14
DB_HOST	DB 호스트(도커에서는 mysql)	mysql
DB_PORT	DB 포트	3306
DB_NAME	DB 이름	blogtp
DB_USERNAME	DB 계정	blogtp
DB_PASSWORD	DB 비번	blogtp
DB_URL	JDBC URL	jdbc:mysql://mysql:3306/blogtp?...
REDIS_HOST	Redis 호스트(도커에서는 redis)	redis
REDIS_PORT	Redis 포트	6379
GOOGLE_CLIENT_ID	(선택) 구글 로그인 검증용	(empty)
FIREBASE_ENABLED	(선택) Firebase 로그인 사용	false
FIREBASE_CREDENTIALS_BASE64	(선택) firebase key base64	(empty)

⚠️ .env는 민감정보이므로 Git에 올리지 말고, .env.example만 커밋하세요.

4) 배포 주소
Base URL: http://113.198.66.68:10062

Swagger URL: http://113.198.66.68:10062/swagger-ui/index.html

Health URL: http://113.198.66.68:10062/actuator/health

5) 인증 플로우 설명 (JWT Access/Refresh)
사용자가 POST /auth/login 요청 (email/password)

서버가 Access Token + Refresh Token 발급

클라이언트는 Access Token을 Authorization: Bearer <token> 형태로 API 요청에 포함

Access Token 만료 시 POST /auth/refresh로 Refresh Token을 제출하여 Access Token 재발급

로그아웃 시 POST /auth/logout로 Refresh Token을 무효화(저장소에서 제거/블랙리스트 처리)

6) 역할/권한표
실제 엔드포인트는 Swagger를 기준으로 제출합니다.

구분	권한	설명
ROLE_USER	기본 사용자	게시글/댓글/좋아요/북마크 등 일반 기능
ROLE_ADMIN	관리자	사용자/콘텐츠 관리성 기능(관리자 전용 API)

접근 권한 예시
API 그룹	USER	ADMIN
Auth (login/refresh/logout)	✅	✅
Posts 조회	✅	✅
Posts 작성/수정/삭제	✅ (본인)	✅
Comments	✅	✅
Categories 관리	❌/✅(정책에 따라)	✅
Stats	✅/❌(정책에 따라)	✅

7) 예제 계정
일반 사용자: user1@example.com / P@ssw0rd!

관리자: admin@example.com / P@ssw0rd!

⚠️ 관리자 계정은 데이터 변경/삭제 가능 기능이 포함될 수 있으니 테스트 시 주의

8) DB 연결 정보(테스트용)
Docker Compose 내부 네트워크 기준
Host: mysql

Port: 3306

DB: blogtp

User: blogtp

Password: blogtp

권한 범위: 테스트/개발용 (제출용)

외부(로컬 PC)에서 DB 접속 시
Host: localhost

Port: 3307 (compose 포트 매핑 기준)

## 9) 엔드포인트 요약표

> ✅ 아래 표는 Swagger 기준으로 정리한 “요약”입니다.  
> 자세한 Request/Response 스키마와 예시는 Swagger 문서를 기준으로 합니다.

### Auth (인증 API)
| Method | URL | 설명 | 인증 |
|---|---|---|---|
| POST | /api/auth/signup | 회원가입 | ❌ |
| POST | /api/auth/login | 로그인 | ❌ |
| POST | /api/auth/refresh | 토큰 재발급 | ❌ |
| POST | /api/auth/logout | 로그아웃 | ✅ (Refresh 필요) |
| POST | /api/auth/login/google | 구글 소셜 로그인 | ❌ |
| POST | /api/auth/login/firebase | Firebase 소셜 로그인 | ❌ |

### Users (회원 API)
| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | /api/users/me | 내 정보 조회 | ✅ |
| PATCH | /api/users/me/password | 비밀번호 변경 | ✅ |
| DELETE | /api/users/me | 회원 비활성화 | ✅ |

### Admin (관리자 API)
| Method | URL | 설명 | 권한 |
|---|---|---|---|
| GET | /api/admin/users | 회원 목록 조회 | ROLE_ADMIN |
| PATCH | /api/admin/users/{userId}/role | 회원 권한 변경 | ROLE_ADMIN |
| PATCH | /api/admin/users/{userId}/status | 회원 상태 변경 | ROLE_ADMIN |

### Posts (게시글 API)
| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | /api/posts | 게시글 목록 조회 | ❌ |
| GET | /api/posts/{id} | 게시글 상세 조회 | ❌ |
| GET | /api/posts/me | 내 게시글 목록 | ✅ |
| POST | /api/posts | 게시글 작성 | ✅ |
| PUT | /api/posts/{id} | 게시글 수정 | ✅ (본인/관리자) |
| DELETE | /api/posts/{id} | 게시글 삭제 | ✅ (본인/관리자) |
| POST | /api/posts/{id}/publish | 게시글 발행 | ✅ (본인/관리자) |
| POST | /api/posts/{id}/unpublish | 게시글 발행 취소 | ✅ (본인/관리자) |

### Comments (댓글 API)
| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | /api/posts/{postId}/comments | 댓글 목록 조회 | ❌ |
| POST | /api/posts/{postId}/comments | 댓글 작성 | ✅ |
| PUT | /api/comments/{commentId} | 댓글 수정 | ✅ (본인/관리자) |
| DELETE | /api/comments/{commentId} | 댓글 삭제 | ✅ (본인/관리자) |

### Categories (카테고리 API)
| Method | URL | 설명 | 권한 |
|---|---|---|---|
| GET | /api/categories | 카테고리 목록 조회 | ❌ |
| POST | /api/categories | 카테고리 생성 | ROLE_ADMIN *(또는 정책에 따라 USER 제한 가능)* |
| PUT | /api/categories/{id} | 카테고리 수정 | ROLE_ADMIN |
| DELETE | /api/categories/{id} | 카테고리 삭제 | ROLE_ADMIN |

### Likes (좋아요 API)
| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | /api/posts/{postId}/likes | 좋아요 수 조회 | ❌ |
| POST | /api/posts/{postId}/likes | 좋아요 | ✅ |
| DELETE | /api/posts/{postId}/likes | 좋아요 취소 | ✅ |

### Bookmarks (북마크 API)
| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | /api/bookmarks/me | 내 북마크 목록 | ✅ |
| POST | /api/posts/{postId}/bookmarks | 북마크 추가 | ✅ |
| DELETE | /api/posts/{postId}/bookmarks | 북마크 삭제 | ✅ |

### Stats (통계 API)
| Method | URL | 설명 | 권한 |
|---|---|---|---|
| GET | /api/stats/daily-posts | 일자별 게시글 수 | ❌ *(또는 ADMIN만)* |
| GET | /api/stats/top-authors | 상위 작성자 | ❌ *(또는 ADMIN만)* |

### Health (헬스체크 API)
| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | /health | 헬스 체크 | ❌ |

---

## 6) 역할/권한표 (ROLE 기반 접근 정책)

> 기본 정책(권장):
- **Public(비로그인)**: 조회성 API 위주 허용
- **ROLE_USER**: 작성/수정/삭제 등 개인 기능 허용
- **ROLE_ADMIN**: 사용자 관리 + 시스템 관리 기능 허용

### 6-1. 권한별 접근 가능 API 요약

| API 그룹 | Public | ROLE_USER | ROLE_ADMIN |
|---|:---:|:---:|:---:|
| Auth (signup/login/refresh) | ✅ | ✅ | ✅ |
| Posts 조회 (/api/posts, /api/posts/{id}) | ✅ | ✅ | ✅ |
| Posts 작성/내글목록 | ❌ | ✅ | ✅ |
| Posts 수정/삭제/발행 | ❌ | ✅(본인) | ✅ |
| Comments 목록 조회 | ✅ | ✅ | ✅ |
| Comments 작성 | ❌ | ✅ | ✅ |
| Comments 수정/삭제 | ❌ | ✅(본인) | ✅ |
| Likes 수 조회 | ✅ | ✅ | ✅ |
| Likes 추가/취소 | ❌ | ✅ | ✅ |
| Bookmarks | ❌ | ✅ | ✅ |
| Categories 목록 | ✅ | ✅ | ✅ |
| Categories 생성/수정/삭제 | ❌ | ❌ | ✅ |
| Stats | ✅(선택) | ✅(선택) | ✅ |
| Admin (/api/admin/**) | ❌ | ❌ | ✅ |

> ⚠️ Categories/Stats의 “Public 허용 여부”는 과제 요구사항/교수님 기준에 맞춰 조정 가능합니다.

10) 에러 응답 공통 포맷
모든 오류 상황에 대해 일관된 JSON 형식의 응답을 반환합니다.

json
코드 복사
{
  "timestamp": "2025-03-05T12:34:56Z",
  "path": "/api/posts/1",
  "status": 400,
  "code": "VALIDATION_FAILED",
  "message": "요청 값이 올바르지 않습니다.",
  "details": {
    "title": "게시글 제목은 1~100자 이내여야 합니다."
  }
}
필드명	설명
timestamp	에러 발생 시각 (ISO 8601)
path	요청 경로
status	HTTP 상태 코드
code	시스템 내부 에러 코드 (대문자+언더스코어)
message	사용자에게 전달할 짧은 메시지
details	(선택) 필드별 오류/상세 사유

11) 표준 에러 코드 정의 (최소 12종 이상)
HTTP 코드	에러 코드	설명
400	BAD_REQUEST	요청 형식이 올바르지 않음
400	VALIDATION_FAILED	필드 유효성 검사 실패
400	INVALID_QUERY_PARAM	쿼리 파라미터 값이 잘못됨
401	UNAUTHORIZED	인증 토큰 없음 또는 잘못된 토큰
401	TOKEN_EXPIRED	토큰 만료
403	FORBIDDEN	권한 없음(Role 불일치 등)
404	RESOURCE_NOT_FOUND	요청 리소스가 존재하지 않음
404	USER_NOT_FOUND	사용자 없음
409	DUPLICATE_RESOURCE	중복 데이터(이메일 중복 등)
409	STATE_CONFLICT	상태 충돌(이미 처리됨 등)
422	UNPROCESSABLE_ENTITY	형식은 맞지만 논리적으로 처리 불가
429	TOO_MANY_REQUESTS	요청 한도 초과(rate limit)
500	INTERNAL_SERVER_ERROR	서버 내부 오류
500	DATABASE_ERROR	DB 연동 오류
500	UNKNOWN_ERROR	알 수 없는 오류 (fallback)

12) 에러 처리 규칙
모든 예외는 위 표의 코드 중 하나로 매핑합니다.

Swagger 문서에서 각 엔드포인트에 대표 응답 예시를 포함합니다. (400/401/403/404/422/500 등)

Postman 테스트에서 대표 에러케이스를 실제로 검증하는 요청을 포함합니다.

Validation 실패 시 details에 필드별 오류를 포함합니다.

13) 성능/보안 고려사항
JWT Secret 32자 이상 강제/권장

Refresh 토큰 저장소(예: Redis) 기반으로 세션 무효화 가능

입력값 검증(Validation) + 표준 에러 포맷

페이지네이션 적용으로 대량 조회 성능 고려

필요 시 인덱스 적용(예: posts.created_at, posts.author_id, category_id 등)

Rate Limit 옵션으로 과도한 요청 방지

14) 한계와 개선 계획
통계/검색 기능 고도화(기간/조건 다양화, 캐싱 적용)

관리자 기능 강화(유저/콘텐츠 관리 API 확장)

테스트 커버리지 강화(MockMvc/통합테스트 확대)

CI 적용(GitHub Actions로 빌드/테스트 자동화)

운영 관점 로깅/모니터링(메트릭, APM) 추가
