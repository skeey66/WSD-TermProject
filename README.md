# BlogTP (Blog API Server)

Spring Boot 기반의 **블로그 API 서버**입니다.  
JWT 인증(Access/Refresh) + Role 기반 권한(RBAC) + Flyway 마이그레이션/시드 + Redis 기반 Rate Limiting + Swagger(OpenAPI) 문서를 제공합니다.

---

## 1) 프로젝트 개요

### 문제 정의
- 게시글/카테고리/댓글/좋아요/북마크를 제공하는 블로그 API를 구현하고,
- JWT 기반 인증/인가 + 관리자 기능 + 일관된 에러 응답 + 문서/테스트(Postman)까지 포함해 **배포 가능한 형태**로 구성하는 것이 목표입니다.

### 주요 기능
- **인증(Auth)**
  - 회원가입 / 로그인 / 토큰 재발급(Refresh) / 로그아웃(Refresh 토큰 폐기)
- **게시글(Post)**
  - 작성/조회(단건/목록)/수정/삭제
- **카테고리(Category)**
  - 생성/조회/수정/삭제
- **댓글(Comment)**
  - 작성/목록 조회/삭제
- **좋아요(Like)**
  - 토글(좋아요/취소)
- **북마크(Bookmark)**
  - 추가/삭제/내 북마크 목록
- **관리자(Admin)**
  - 유저 목록 조회, 유저 Role 변경, 유저 비활성화
- **통계(Stats, Public)**
  - 요약/일자별 게시글 수/상위 작성자(옵션)
- **레이트리밋**
  - Redis 기반 요청 제한(분당 N회)

---

## 2) 실행 방법

## A. Docker Compose로 실행(권장)

### 준비물
- Docker + Docker Compose Plugin
  - 확인: `docker --version`, `docker compose version`

### 실행 순서
```bash
# 1) 환경변수 준비
cp .env.example .env

# 2) 컨테이너 빌드 + 실행
docker compose up -d --build

# 3) 로그 확인
docker compose logs -f app
접속 확인(로컬)
bash
코드 복사
curl -i http://127.0.0.1:8080/actuator/health
# Swagger
# http://127.0.0.1:8080/swagger-ui/index.html
DB 마이그레이션/시드는 앱 시작 시 Flyway가 자동 실행합니다.
(V1__init.sql → V2__seed.sql)

B. 로컬(직접 실행, 개발용)
준비물
Java 21+

MySQL 8 + Redis 7 (로컬 또는 Docker)

환경변수 .env (또는 OS env로 주입)

bash
코드 복사
# (선택) DB/Redis만 docker로 띄우고 앱은 로컬에서 실행 가능
docker compose up -d mysql redis

# 앱 실행
./gradlew bootRun
3) 환경변수 설명 (.env.example와 매칭)
구분	변수	설명	예시/기본값
App	SERVER_PORT	서버 포트	8080
App	CORS_ALLOWED_ORIGINS	CORS 허용 Origin	http://localhost:3000
RateLimit	RATE_LIMIT_ENABLED	레이트리밋 on/off	true
RateLimit	RATE_LIMIT_PER_MINUTE	분당 허용 요청 수	120
JWT	JWT_SECRET	JWT 서명키(32자 이상 권장)	CHANGE_ME...
JWT	JWT_ISSUER	발급자(issuer)	blogtp
JWT	JWT_ACCESS_MINUTES	Access 토큰 만료(분)	30
JWT	JWT_REFRESH_DAYS	Refresh 토큰 만료(일)	14
MySQL	DB_HOST	DB 호스트(도커 서비스명)	mysql
MySQL	DB_PORT	DB 포트	3306
MySQL	DB_NAME	DB 이름	blogtp
MySQL	DB_USERNAME	DB 유저	blogtp
MySQL	DB_PASSWORD	DB 비밀번호	blogtp
MySQL	DB_URL	JDBC URL	jdbc:mysql://mysql:3306/blogtp?...
Redis	REDIS_HOST	Redis 호스트(도커 서비스명)	redis
Redis	REDIS_PORT	Redis 포트	6379
Social	GOOGLE_CLIENT_ID	(옵션) Google Login	공란 가능
Social	FIREBASE_ENABLED	Firebase on/off	false
Social	FIREBASE_CREDENTIALS_BASE64	(옵션) 서비스계정 base64	공란 가능

4) 배포 주소 (예시: JCloud)
아래는 “외부 포트포워딩이 적용된 최종 접근 URL” 기준으로 적으세요.
(너 상황에 맞게 포트만 바꾸면 됨)

text
코드 복사
Base URL  : http://113.198.66.68:10062
Swagger   : http://113.198.66.68:10062/swagger-ui/index.html
Health    : http://113.198.66.68:10062/actuator/health
OpenAPI   : http://113.198.66.68:10062/v3/api-docs
5) 인증 플로우 설명 (JWT Access + Refresh)
회원가입 POST /api/auth/signup

로그인 POST /api/auth/login

응답으로 accessToken, refreshToken 반환

이후 요청 헤더에 Access 토큰 첨부

Authorization: Bearer <accessToken>

Access 만료 시 재발급

POST /api/auth/refresh (+ refreshToken)

Refresh 토큰은 DB에 저장되며, 재발급 시 로테이션(새 refresh 발급 + 기존 폐기) 합니다.

로그아웃

POST /api/auth/logout → 해당 refresh 토큰을 DB에서 폐기

6) 역할/권한표 (ROLE_USER / ROLE_ADMIN)
기본 정책: /api/**는 인증 필요
예외: /api/auth/**, /api/stats/**, Swagger, Health 는 공개

구분	ROLE_USER	ROLE_ADMIN	Public
Swagger/Docs/Health	-	-	✅
Auth (signup/login/refresh/logout)	✅(로그인 후 일부)	✅	✅(signup/login/refresh/logout 접근은 허용)
Posts/Comments/Likes/Bookmarks/Categories	✅	✅	❌
Admin User 관리 (/api/admin/**)	❌	✅	❌
Stats (/api/stats/**)	-	-	✅

7) 예제 계정 (시드 데이터)
Flyway 시드(V2__seed.sql)로 아래 계정이 자동 생성됩니다.

text
코드 복사
USER  : user1@blogtp.local / P@ssw0rd!
ADMIN : admin@blogtp.local / P@ssw0rd!
ADMIN 계정은 관리자 API 호출 및 권한 변경 기능이 가능하므로 외부 공개 환경에서는 비밀번호 변경 권장.

8) DB 연결 정보(테스트용)
Docker Compose 기준 (로컬):

text
코드 복사
Host: localhost
Port: 3306
DB  : blogtp
User: blogtp
Pass: blogtp
권한 범위:

blogtp 유저는 blogtp DB에 대한 일반 CRUD 권한을 가정합니다.

운영 환경에서는 root 계정 노출 금지 권장

9) 엔드포인트 요약표
Base Path: /api

Auth
Method	URL	설명	권한
POST	/api/auth/signup	회원가입	Public
POST	/api/auth/login	로그인(토큰 발급)	Public
POST	/api/auth/refresh	토큰 재발급(로테이션)	Public
POST	/api/auth/logout	로그아웃(Refresh 폐기)	USER/ADMIN

Users
Method	URL	설명	권한
GET	/api/users/me	내 프로필 조회	USER/ADMIN

Admin
Method	URL	설명	권한
GET	/api/admin/users	유저 목록 조회	ADMIN
PATCH	`/api/admin/users/{id}/role?role=ADMIN	USER`	유저 권한 변경
PATCH	/api/admin/users/{id}/deactivate	유저 비활성화	ADMIN

Categories
Method	URL	설명	권한
POST	/api/categories	카테고리 생성	USER/ADMIN
GET	/api/categories	카테고리 목록	USER/ADMIN
PATCH	/api/categories/{id}	카테고리 수정	USER/ADMIN
DELETE	/api/categories/{id}	카테고리 삭제	USER/ADMIN

Posts
Method	URL	설명	권한
POST	/api/posts	게시글 작성	USER/ADMIN
GET	/api/posts	게시글 목록(페이지네이션)	USER/ADMIN
GET	/api/posts/{id}	게시글 단건 조회	USER/ADMIN
PATCH	/api/posts/{id}	게시글 수정	USER/ADMIN
DELETE	/api/posts/{id}	게시글 삭제	USER/ADMIN

Comments
Method	URL	설명	권한
POST	/api/posts/{postId}/comments	댓글 작성	USER/ADMIN
GET	/api/posts/{postId}/comments	댓글 목록 조회	USER/ADMIN
DELETE	/api/comments/{id}	댓글 삭제	USER/ADMIN

Likes
Method	URL	설명	권한
POST	/api/posts/{postId}/likes	좋아요 토글	USER/ADMIN

Bookmarks
Method	URL	설명	권한
POST	/api/posts/{postId}/bookmarks	북마크 추가	USER/ADMIN
DELETE	/api/posts/{postId}/bookmarks	북마크 삭제	USER/ADMIN
GET	/api/bookmarks/me	내 북마크 목록	USER/ADMIN

Stats (Public)
Method	URL	설명	권한
GET	/api/stats/summary	통계 요약	Public
GET	/api/stats/daily-posts?days=7	일자별 게시글 수	Public
GET	/api/stats/top-authors?limit=5	상위 작성자(옵션)	Public

10) 에러 응답 공통 포맷 (일관된 JSON)
json
코드 복사
{
  "timestamp": "2025-12-24T12:34:56Z",
  "path": "/api/posts/1",
  "status": 400,
  "code": "VALIDATION_FAILED",
  "message": "요청 값이 유효하지 않습니다.",
  "details": {
    "title": "must not be blank"
  }
}
필드명	설명
timestamp	에러 발생 시각 (ISO 8601)
path	요청 경로
status	HTTP 상태 코드
code	시스템 내부 에러 코드 (대문자+언더스코어)
message	사용자용 에러 메시지
details	(선택) 필드별 오류/추가 정보

11) 표준 에러 코드 정의 (12종 이상)
HTTP	code	설명
400	BAD_REQUEST	요청 형식이 올바르지 않음
400	VALIDATION_FAILED	유효성 검사 실패
400	INVALID_QUERY_PARAM	쿼리 파라미터 오류
401	UNAUTHORIZED	인증 토큰 없음/오류
401	TOKEN_EXPIRED	토큰 만료
403	FORBIDDEN	권한 없음
404	RESOURCE_NOT_FOUND	리소스 없음(공통)
404	USER_NOT_FOUND	유저 없음
404	POST_NOT_FOUND	게시글 없음
404	COMMENT_NOT_FOUND	댓글 없음
404	CATEGORY_NOT_FOUND	카테고리 없음
409	DUPLICATE_RESOURCE	중복 데이터
409	STATE_CONFLICT	상태 충돌
422	UNPROCESSABLE_ENTITY	논리적 처리 불가
429	TOO_MANY_REQUESTS	레이트리밋 초과
500	INTERNAL_SERVER_ERROR	서버 오류
500	DATABASE_ERROR	DB 오류
500	UNKNOWN_ERROR	최종 fallback

12) Postman 컬렉션(JSON) 실행
제출 파일:

BlogTP.postman_collection.json

BlogTP.postman_environment.json

사용 방법:

Postman → Import → Collection/Environment 각각 Import

Environment 선택 후, 변수 baseUrl 확인

Runner로 컬렉션 실행

Pre-request/Test 스크립트가 토큰 저장/주입 및 응답 검증을 수행합니다(최소 5개 이상)

컬렉션은 (1) 랜덤 유저 회원가입 → (2) 로그인 → (3) 글 작성/댓글/좋아요/북마크 → (4) 내 프로필 조회 → (5) 통계 조회 흐름으로 구성되어 있습니다.

13) 성능/보안 고려사항
JWT + Refresh 토큰 로테이션

Refresh 토큰은 DB에 저장/폐기 처리 → 탈취 시 피해 최소화

BCrypt 해시

비밀번호는 평문 저장하지 않고 BCrypt로 해시 저장

Rate Limiting (Redis)

IP 기반 분당 요청 제한 (환경변수로 조절)

DB 인덱스

게시글/댓글 조회 성능을 위한 인덱스 적용(작성자/생성일 등)

CORS

허용 Origin을 환경변수로 관리

14) 한계와 개선 계획
(개선) Posts/Comments의 검색 조건 확장(키워드, 카테고리 필터 등)

(개선) Stats 결과 캐싱(Redis) 및 집계 성능 최적화

(개선) 운영 환경에서 Secret/DB 계정 권한 최소화, HTTPS 적용

(개선) 소셜 로그인(Firebase/Google) 활성화 시 보안 검증 강화
