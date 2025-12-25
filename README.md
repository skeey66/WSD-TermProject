# BlogTP (Spring Boot 4 + Java 21) — 과제 제출용 템플릿

- Swagger UI: **`/swagger-ui`**
- API Docs(JSON): `/v3/api-docs`
- Health(간단): **`/health`**
- Actuator Health: `/actuator/health`

## 1) 실행 (Docker Compose 권장)

```bash
cp .env.example .env
# .env에서 JWT_SECRET 등 반드시 수정
docker compose up -d --build
```

- 서버: `http://localhost:8080/swagger-ui`
- DB: MySQL `localhost:3306` (컨테이너: mysql)
- Redis: `localhost:6379` (컨테이너: redis)

## 2) 실행 (로컬 Gradle)

```bash
# 로컬에 MySQL/Redis가 있어야 함
export DB_URL="jdbc:mysql://localhost:3306/blogtp?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USERNAME="blogtp"
export DB_PASSWORD="blogtp"
export JWT_SECRET="CHANGE_ME__AT_LEAST_32_CHARS_LONG"

./gradlew bootRun
```

## 3) 시드 계정 (Flyway V2)

- ADMIN  
  - email: `admin@blogtp.local`  
  - password: `Admin!234`
- USER (예: user1~user5)  
  - email: `user1@blogtp.local`  
  - password: `User!2345`

## 4) 주요 엔드포인트

### Auth
- `POST /api/auth/signup`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `POST /api/auth/login/google` (tokeninfo 기반)
- `POST /api/auth/login/firebase` (Firebase Admin 기반, 설정 필요)

### Users
- `GET /api/users/me`
- `PATCH /api/users/me/password`
- `DELETE /api/users/me` (비활성화)

### Admin
- `GET /api/admin/users`
- `PATCH /api/admin/users/{id}/role`
- `PATCH /api/admin/users/{id}/status`

### Categories
- `GET /api/categories`
- `POST /api/categories` (ADMIN)
- `PUT /api/categories/{id}` (ADMIN)
- `DELETE /api/categories/{id}` (ADMIN)

### Posts
- `POST /api/posts` (로그인)
- `GET /api/posts` (공개, 검색/필터/페이지네이션)
- `GET /api/posts/{id}` (공개: publish만)
- `PUT /api/posts/{id}` (작성자/ADMIN)
- `DELETE /api/posts/{id}` (작성자/ADMIN)
- `POST /api/posts/{id}/publish`
- `POST /api/posts/{id}/unpublish`
- `GET /api/posts/me` (내 글 목록)

### Comments
- `POST /api/posts/{postId}/comments` (로그인)
- `GET /api/posts/{postId}/comments` (공개)
- `PUT /api/comments/{commentId}` (작성자/ADMIN)
- `DELETE /api/comments/{commentId}` (작성자/ADMIN)

### Likes / Bookmarks
- `GET /api/posts/{postId}/likes` (공개)
- `POST /api/posts/{postId}/likes` (로그인)
- `DELETE /api/posts/{postId}/likes` (로그인)
- `POST /api/posts/{postId}/bookmarks` (로그인)
- `DELETE /api/posts/{postId}/bookmarks` (로그인)
- `GET /api/bookmarks/me` (로그인)

### Stats
- `GET /api/stats/daily-posts?days=7`
- `GET /api/stats/top-authors?days=30&limit=5`

## 5) JCloud 배포(예시)

1) 서버에 Docker / Docker Compose 설치  
2) 레포 clone 후 `.env` 작성  
3) 실행:

```bash
./scripts/jcloud-deploy.sh
```

외부 포트 리다이렉션 설정 후:
- `http://<server-ip>:<port>/health`
- `http://<server-ip>:<port>/swagger-ui`

## 6) 주의사항

- Flyway 적용 후 **V1/V2 파일 수정 금지** (체크섬 mismatch 발생)
- `JWT_SECRET`는 32자 이상 필수
