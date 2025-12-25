package kr.ac.jbnu.ksh.blogtp.user.repository;

import kr.ac.jbnu.ksh.blogtp.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
