# 과제 설계 구현 요약

- 리소스: users / categories / posts / comments / likes / bookmarks (+ stats)
- 공통 목록 규격: page, size, sort + keyword/categoryId/authorId 등의 필터 제공
- 에러 포맷 통일: timestamp, path, status, code, message, details
- 인증/인가: JWT(Access/Refresh) + ROLE_USER/ROLE_ADMIN
- 소셜 로그인:
  - Google: tokeninfo 엔드포인트 기반 검증(클라이언트ID 일치 확인)
  - Firebase: Firebase Admin SDK로 ID Token verify (서비스 계정 base64 필요)
