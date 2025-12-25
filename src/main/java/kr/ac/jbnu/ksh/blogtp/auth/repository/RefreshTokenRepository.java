package kr.ac.jbnu.ksh.blogtp.auth.repository;

import kr.ac.jbnu.ksh.blogtp.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    long deleteByToken(String token);
    long deleteByUser_Id(Long userId);
}
