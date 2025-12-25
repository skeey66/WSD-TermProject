# BlogTP DB Schema

이 문서는 **BlogTP API 서버**의 MySQL 스키마를 정리한 문서입니다.

## 개요

- DBMS: MySQL 8.x
- DB 이름: `blogtp`
- Charset/Collation: `utf8mb4` / `utf8mb4_unicode_ci`
- 스키마 생성/마이그레이션: Flyway (`src/main/resources/db/migration`)

## 테이블 목록

- `users`
- `refresh_tokens`
- `categories`
- `posts`
- `comments`
- `post_likes`
- `bookmarks`

## 관계(FOREIGN KEY)

| from_table | fk_column | to_table | to_column |
|---|---|---|---|
| refresh_tokens | user_id | users | id |
| posts | author_id | users | id |
| posts | category_id | categories | id |
| comments | post_id | posts | id |
| comments | author_id | users | id |
| post_likes | post_id | posts | id |
| post_likes | user_id | users | id |
| bookmarks | post_id | posts | id |
| bookmarks | user_id | users | id |


## users

### 컬럼

| column | type | null | default | extra |
|---|---|---|---|---|
| id | BIGINT | (default) |  | PK AUTO_INCREMENT |
| email | VARCHAR(200) | NO |  |  |
| password_hash | VARCHAR(200) | NO |  |  |
| role | VARCHAR(20) | NO |  |  |
| status | VARCHAR(20) | NO |  |  |
| created_at | DATETIME(6) | NO |  |  |
| updated_at | DATETIME(6) | NO |  |  |


### 제약조건

- `CONSTRAINT uk_users_email UNIQUE (email)`

### DDL

```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(200) NOT NULL,
  password_hash VARCHAR(200) NOT NULL,
  role VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## refresh_tokens

### 컬럼

| column | type | null | default | extra |
|---|---|---|---|---|
| id | BIGINT | (default) |  | PK AUTO_INCREMENT |
| user_id | BIGINT | NO |  |  |
| token | VARCHAR(200) | NO |  |  |
| expires_at | DATETIME(6) | NO |  |  |
| revoked | TINYINT(1) | NO | 0 |  |
| created_at | DATETIME(6) | NO |  |  |
| updated_at | DATETIME(6) | NO |  |  |


### 제약조건

- `CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE`
- `CONSTRAINT idx_refresh_tokens_token UNIQUE (token)`

### 인덱스

- `CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);`

### DDL

```sql
CREATE TABLE refresh_tokens (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  token VARCHAR(200) NOT NULL,
  expires_at DATETIME(6) NOT NULL,
  revoked TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT idx_refresh_tokens_token UNIQUE (token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## categories

### 컬럼

| column | type | null | default | extra |
|---|---|---|---|---|
| id | BIGINT | (default) |  | PK AUTO_INCREMENT |
| name | VARCHAR(80) | NO |  |  |
| created_at | DATETIME(6) | NO |  |  |
| updated_at | DATETIME(6) | NO |  |  |


### 제약조건

- `CONSTRAINT uk_categories_name UNIQUE (name)`

### DDL

```sql
CREATE TABLE categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(80) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  CONSTRAINT uk_categories_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## posts

### 컬럼

| column | type | null | default | extra |
|---|---|---|---|---|
| id | BIGINT | (default) |  | PK AUTO_INCREMENT |
| author_id | BIGINT | NO |  |  |
| category_id | BIGINT | YES |  |  |
| title | VARCHAR(120) | NO |  |  |
| content | LONGTEXT | NO |  |  |
| published | TINYINT(1) | NO | 0 |  |
| created_at | DATETIME(6) | NO |  |  |
| updated_at | DATETIME(6) | NO |  |  |


### 제약조건

- `CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE`
- `CONSTRAINT fk_posts_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL`

### 인덱스

- `CREATE INDEX idx_posts_created_at ON posts(created_at);`
- `CREATE INDEX idx_posts_author ON posts(author_id);`
- `CREATE INDEX idx_posts_category ON posts(category_id);`

### DDL

```sql
CREATE TABLE posts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  author_id BIGINT NOT NULL,
  category_id BIGINT NULL,
  title VARCHAR(120) NOT NULL,
  content LONGTEXT NOT NULL,
  published TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_posts_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## comments

### 컬럼

| column | type | null | default | extra |
|---|---|---|---|---|
| id | BIGINT | (default) |  | PK AUTO_INCREMENT |
| post_id | BIGINT | NO |  |  |
| author_id | BIGINT | NO |  |  |
| content | LONGTEXT | NO |  |  |
| created_at | DATETIME(6) | NO |  |  |
| updated_at | DATETIME(6) | NO |  |  |


### 제약조건

- `CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE`
- `CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE`

### 인덱스

- `CREATE INDEX idx_comments_post ON comments(post_id);`
- `CREATE INDEX idx_comments_author ON comments(author_id);`

### DDL

```sql
CREATE TABLE comments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  author_id BIGINT NOT NULL,
  content LONGTEXT NOT NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
  CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## post_likes

### 컬럼

| column | type | null | default | extra |
|---|---|---|---|---|
| id | BIGINT | (default) |  | PK AUTO_INCREMENT |
| post_id | BIGINT | NO |  |  |
| user_id | BIGINT | NO |  |  |
| created_at | DATETIME(6) | NO |  |  |
| updated_at | DATETIME(6) | NO |  |  |


### 제약조건

- `CONSTRAINT fk_post_likes_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE`
- `CONSTRAINT fk_post_likes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE`
- `CONSTRAINT uk_post_likes_post_user UNIQUE (post_id, user_id)`

### 인덱스

- `CREATE INDEX idx_post_likes_post ON post_likes(post_id);`
- `CREATE INDEX idx_post_likes_user ON post_likes(user_id);`

### DDL

```sql
CREATE TABLE post_likes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  CONSTRAINT fk_post_likes_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
  CONSTRAINT fk_post_likes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT uk_post_likes_post_user UNIQUE (post_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## bookmarks

### 컬럼

| column | type | null | default | extra |
|---|---|---|---|---|
| id | BIGINT | (default) |  | PK AUTO_INCREMENT |
| post_id | BIGINT | NO |  |  |
| user_id | BIGINT | NO |  |  |
| created_at | DATETIME(6) | NO |  |  |
| updated_at | DATETIME(6) | NO |  |  |


### 제약조건

- `CONSTRAINT fk_bookmarks_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE`
- `CONSTRAINT fk_bookmarks_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE`
- `CONSTRAINT uk_bookmarks_post_user UNIQUE (post_id, user_id)`

### 인덱스

- `CREATE INDEX idx_bookmarks_post ON bookmarks(post_id);`
- `CREATE INDEX idx_bookmarks_user ON bookmarks(user_id);`

### DDL

```sql
CREATE TABLE bookmarks (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  CONSTRAINT fk_bookmarks_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
  CONSTRAINT fk_bookmarks_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT uk_bookmarks_post_user UNIQUE (post_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## 추가 인덱스(전체)

```sql
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX idx_posts_created_at ON posts(created_at);
CREATE INDEX idx_posts_author ON posts(author_id);
CREATE INDEX idx_posts_category ON posts(category_id);
CREATE INDEX idx_comments_post ON comments(post_id);
CREATE INDEX idx_comments_author ON comments(author_id);
CREATE INDEX idx_post_likes_post ON post_likes(post_id);
CREATE INDEX idx_post_likes_user ON post_likes(user_id);
CREATE INDEX idx_bookmarks_post ON bookmarks(post_id);
CREATE INDEX idx_bookmarks_user ON bookmarks(user_id);
```
