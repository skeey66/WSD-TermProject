# BlogTP 아키텍처

- **API 서버**: Spring Boot (JWT Access/Refresh + RBAC)
- **DB**: MySQL (Flyway 마이그레이션)
- **Redis**: 전역 Rate-limit(분당 요청 제한) 및 토큰/캐시 확장 포인트
- **배포**: Docker Compose (app + mysql + redis)

## 실행 흐름

1) 클라이언트 → `POST /api/auth/login` → Access/Refresh 발급  
2) 이후 요청은 `Authorization: Bearer <ACCESS_TOKEN>`  
3) Access 만료 시 `POST /api/auth/refresh` 로 재발급  
4) Rate-limit 필터가 Redis에 카운팅(인증 시 user 기준, 비인증 시 IP 기준)

## 컨테이너 구성

- `blogtp-app` : 8080
- `blogtp-mysql` : 3306
- `blogtp-redis` : 6379
