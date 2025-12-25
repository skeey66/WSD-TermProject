# BlogTP (WSD Term Project) - Spring Boot API

> 전북대학교 웹서비스설계(WSD) 텀프로젝트  
> Spring Boot 기반 **블로그 API 서버** (Docker Compose로 MySQL/Redis 포함 실행)  
> Swagger 문서 + Postman 컬렉션으로 기능 검증

---

## 1) 프로젝트 개요

### 문제 정의
수동으로 게시글/댓글/좋아요/북마크를 관리하던 과정을 API 서버로 제공하여,
사용자가 로그인 후 게시글을 작성/발행하고, 댓글/좋아요/북마크 기능을 사용할 수 있도록 한다.
관리자는 회원을 조회하고 권한/상태를 변경할 수 있다.

### 주요 기능
- 인증/인가: JWT 기반 Access/Refresh 토큰 로그인, 로그아웃, 토큰 재발급
- 게시글: 작성/조회/수정/삭제, 발행/발행취소, 내 게시글 조회
- 댓글: 작성/조회/수정/삭제
- 좋아요: 좋아요/취소, 좋아요 수 조회
- 북마크: 추가/삭제, 내 북마크 목록
- 카테고리: 목록 조회, (관리자) 생성/수정/삭제
- 통계: 일자별 게시글 수, 상위 작성자
- 운영: Health 체크, Rate Limit(옵션)

---

## 2) 실행 방법

### 2-1. 로컬 실행 (Docker Compose)
로컬에서 MySQL/Redis/App을 한 번에 실행합니다.

```bash
# 1) 프로젝트 루트로 이동
cd blogtp

# 2) 환경변수 파일 생성 (.env)
cp .env.example .env
# 또는 직접 .env 생성 후 값 입력

# 3) 실행 (빌드 포함)
docker compose up -d --build

# 4) 로그 확인
docker compose logs -f app

# 5) 종료
docker compose down
```

### 2-2. 로컬 확인 URL
- Base URL: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/index.html`
- Health: `http://localhost:8080/health`

---

## 3) 환경변수 설명 (.env.example 와 매칭)

`.env.example` → `.env`로 복사한 뒤 값만 채우면 됩니다.

### App
- `SERVER_PORT` : 서버 포트 (기본 8080)
- `CORS_ALLOWED_ORIGINS` : CORS 허용 Origin (예: http://localhost:3000)
- `RATE_LIMIT_ENABLED` : 레이트리밋 사용 여부 (true/false)
- `RATE_LIMIT_PER_MINUTE` : 분당 요청 제한 수

### JWT
- `JWT_SECRET` : JWT 서명 키 (반드시 32자 이상 권장)
- `JWT_ISSUER` : issuer 값
- `JWT_ACCESS_MINUTES` : Access Token 만료(분)
- `JWT_REFRESH_DAYS` : Refresh Token 만료(일)

### MySQL
- `DB_HOST` : MySQL 호스트 (docker compose면 `mysql`)
- `DB_PORT` : MySQL 포트 (컨테이너 내부 3306)
- `DB_NAME` : DB명
- `DB_USERNAME` : 계정
- `DB_PASSWORD` : 비밀번호
- `DB_URL` : JDBC URL (예: `jdbc:mysql://mysql:3306/blogtp?...`)

### Redis
- `REDIS_HOST` : Redis 호스트 (docker compose면 `redis`)
- `REDIS_PORT` : Redis 포트 (6379)

### Social Login (옵션)
- `GOOGLE_CLIENT_ID` : 구글 클라이언트 ID (tokeninfo 검증용)
- `FIREBASE_ENABLED` : Firebase 로그인 활성화 여부
- `FIREBASE_CREDENTIALS_BASE64` : serviceAccountKey.json base64 값

---

## 4) 배포 주소

✅ 아래 값은 예시입니다. 제출 시 본인 배포 주소로 수정하세요.

- Base URL: `http://113.198.66.68:10062`
- Swagger URL: `http://113.198.66.68:10062/swagger-ui/index.html`
- Health URL: `http://113.198.66.68:10062/health`

---

## 5) 인증 플로우 설명 (JWT)

### 5-1. 로그인
1) `POST /api/auth/login` 요청  
2) 서버가 Access/Refresh 토큰 발급  
3) 이후 요청은 헤더에 Access 토큰 포함

예시:
```
Authorization: Bearer <ACCESS_TOKEN>
```

### 5-2. 토큰 재발급
- Access 토큰 만료 시 `POST /api/auth/refresh` 호출
- Refresh 토큰이 유효하면 새 Access(및 필요 시 Refresh) 발급

### 5-3. 로그아웃
- `POST /api/auth/logout`
- Refresh 토큰 무효화(저장소에서 제거/폐기)

---

## 6) 역할/권한표 (ROLE 기반 접근 정책)

기본 정책(권장):
- Public(비로그인): 조회성 API 일부 허용
- ROLE_USER: 작성/수정/삭제 등 개인 기능 허용
- ROLE_ADMIN: 사용자 관리 + 시스템 관리 기능 허용

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

---

## 7) 예제 계정

✅ 시드 데이터 기준 (프로젝트 설정에 따라 다를 수 있음)

- USER: `user1@example.com / P@ssw0rd!`
- ADMIN: `admin@example.com / P@ssw0rd!`  
  - 관리자 권한으로 사용자 권한/상태 변경 가능

---

## 8) DB 연결 정보(테스트용)

Docker Compose 환경 기준

- Host: `localhost`
- Port: `3307` (호스트) → 컨테이너 내부 `3306`
- DB: `blogtp`
- User: `blogtp`
- Password: `blogtp`
- 권한 범위: 테스트용 계정(해당 DB 스키마 내 CRUD)

---

## 9) 엔드포인트 요약표

✅ 아래 표는 Swagger 기준 “요약”입니다.  
자세한 Request/Response 스키마와 예시는 Swagger 문서를 기준으로 합니다.

### Admin (관리자 API)
| Method | URL | 설명 | 권한 |
|---|---|---|---|
| GET | /api/admin/users | 회원 목록 조회 | ROLE_ADMIN |
| PATCH | /api/admin/users/{userId}/role | 회원 권한 변경 | ROLE_ADMIN |
| PATCH | /api/admin/users/{userId}/status | 회원 상태 변경 | ROLE_ADMIN |

### Auth (인증 API)
| Method | URL | 설명 | 인증 |
|---|---|---|---|
| POST | /api/auth/login | 로그인 | ❌ |
| POST | /api/auth/login/firebase | Firebase 소셜 로그인 | ❌ |
| POST | /api/auth/login/google | 구글 소셜 로그인 | ❌ |
| POST | /api/auth/logout | 로그아웃 | ✅ (Refresh 필요) |
| POST | /api/auth/refresh | 토큰 재발급 | ❌ |
| POST | /api/auth/signup | 회원가입 | ❌ |

### Bookmarks (북마크 API)
| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | /api/bookmarks/me | 내 북마크 목록 | ✅ |
| POST | /api/posts/{postId}/bookmarks | 북마크 추가 | ✅ |
| DELETE | /api/posts/{postId}/bookmarks | 북마크 삭제 | ✅ |

### Categories (카테고리 API)
| Method | URL | 설명 | 권한 |
|---|---|---|---|
| GET | /api/categories | 카테고리 목록 조회 | ❌ |
| POST | /api/categories | 카테고리 생성 | ROLE_ADMIN |
| PUT | /api/categories/{id} | 카테고리 수정 | ROLE_ADMIN |
| DELETE | /api/categories/{id} | 카테고리 삭제 | ROLE_ADMIN |

### Comments (댓글 API)
| Method | URL | 설명 | 인증 |
|---|---|---|---|
| PUT | /api/comments/{commentId} | 댓글 수정 | ✅ (본인/관리자) |
| DELETE | /api/comments/{commentId} | 댓글 삭제 | ✅ (본인/관리자) |
| GET | /api/posts/{postId}/comments | 댓글 목록 조회 | ❌ |
| POST | /api/posts/{postId}/comments | 댓글 작성 | ✅ |

### Health (헬스체크 API)
| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | /health | 헬스 체크 | ❌ |

### Likes (좋아요 API)
| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | /api/posts/{postId}/likes | 좋아요 수 조회 | ❌ |
| POST | /api/posts/{postId}/likes | 좋아요 | ✅ |
| DELETE | /api/posts/{postId}/likes | 좋아요 취소 | ✅ |

### Posts (게시글 API)
| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | /api/posts | 게시글 목록 조회 | ❌ |
| POST | /api/posts | 게시글 작성 | ✅ |
| GET | /api/posts/{id} | 게시글 상세 조회 | ❌ |
| PUT | /api/posts/{id} | 게시글 수정 | ✅ (본인/관리자) |
| DELETE | /api/posts/{id} | 게시글 삭제 | ✅ (본인/관리자) |
| POST | /api/posts/{id}/publish | 게시글 발행 | ✅ (본인/관리자) |
| POST | /api/posts/{id}/unpublish | 게시글 발행 취소 | ✅ (본인/관리자) |
| GET | /api/posts/me | 내 게시글 목록 | ✅ |

### Stats (통계 API)
| Method | URL | 설명 | 권한 |
|---|---|---|---|
| GET | /api/stats/daily-posts | 일자별 게시글 수 | ✅(선택) |
| GET | /api/stats/top-authors | 상위 작성자 | ✅(선택) |

### Users (회원 API)
| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | /api/users/me | 내 정보 조회 | ✅ |
| DELETE | /api/users/me | 회원 비활성화 | ✅ |
| PATCH | /api/users/me/password | 비밀번호 변경 | ✅ |

---

## 10) 성능/보안 고려사항
- JWT 기반 Stateless 인증 구조로 서버 세션 저장 최소화
- Refresh 토큰 별도 저장 및 로그아웃 시 폐기 처리(보안 강화)
- Rate Limit 옵션 제공(분당 요청 제한)으로 abuse 방지
- DB 인덱스(예: post_id, user_id, created_at 등) 적용 가능
- CORS Origin 제한 환경변수로 관리

---

## 11) API 서버 에러 응답 규격

API 서버는 모든 오류 상황에 대해 **일관된 JSON 형식의 응답**을 반환합니다.

### 11-1. 에러 응답 공통 포맷
```json
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
```

| 필드명 | 설명 |
| --- | --- |
| `timestamp` | 에러 발생 시각 (ISO 8601) |
| `path` | 요청 경로 |
| `status` | HTTP 상태 코드 |
| `code` | 시스템 내부 에러 코드 (대문자+언더스코어) |
| `message` | 사용자에게 전달할 짧은 에러 메시지 |
| `details` | (선택) 필드별 오류나 내부 상세 사유 |

### 11-2. 표준 에러 코드 정의 (최소 12종 이상)
| HTTP 코드 | 에러 코드 | 설명 |
| --- | --- | --- |
| 400 | `BAD_REQUEST` | 요청 형식이 올바르지 않음 |
| 400 | `VALIDATION_FAILED` | 필드 유효성 검사 실패 |
| 400 | `INVALID_QUERY_PARAM` | 쿼리 파라미터 값이 잘못됨 |
| 401 | `UNAUTHORIZED` | 인증 토큰 없음 또는 잘못된 토큰 |
| 401 | `TOKEN_EXPIRED` | 토큰 만료 |
| 403 | `FORBIDDEN` | 접근 권한 없음 (Role 불일치 등) |
| 404 | `RESOURCE_NOT_FOUND` | 요청한 리소스가 존재하지 않음 |
| 404 | `USER_NOT_FOUND` | 사용자 ID가 존재하지 않음 |
| 409 | `DUPLICATE_RESOURCE` | 중복 데이터 존재(이메일 중복 등) |
| 409 | `STATE_CONFLICT` | 리소스 상태 충돌(이미 삭제된 항목 등) |
| 422 | `UNPROCESSABLE_ENTITY` | 처리할 수 없는 요청 내용(논리적 오류) |
| 429 | `TOO_MANY_REQUESTS` | 요청 한도 초과 (rate limiting) |
| 500 | `INTERNAL_SERVER_ERROR` | 서버 내부 오류 |
| 500 | `DATABASE_ERROR` | DB 연동 오류 |
| 500 | `UNKNOWN_ERROR` | 알 수 없는 오류 (최종 fallback) |

### 11-3. 에러 처리 규칙
1. 모든 예외는 위 표의 코드 중 하나로 매핑 (필요 시 코드 추가 가능)
2. Swagger 문서의 각 엔드포인트에 최소 400/401/403/404/422/500 응답 예시 포함(권장)
3. Postman 테스트에서 대표 에러 케이스 검증 요청 3개 이상 포함(권장)
4. Validation 실패 시 `details`에 필드별 오류 포함

---

## 12) 한계와 개선 계획
- (개선) 게시글 검색/정렬/페이지네이션 고도화 및 인덱스 튜닝
- (개선) 통계 API 캐싱 전략(Redis) 명확화 및 배치/스케줄링 도입
- (개선) 소셜 로그인(Firebase/Google) 실
제 운영 키 기반 검증 강화
- (개선) 관리자 기능(리포트/차단/감사 로그) 확장

## 13)헬스 체크 스크린샷
<img width="408" height="270" alt="11" src="https://github.com/user-attachments/assets/a7ab2f5d-3c7d-4be2-8706-38e7d987309d" />
