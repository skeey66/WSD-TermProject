package kr.ac.jbnu.ksh.blogtp.auth.domain;

import jakarta.persistence.*;
import kr.ac.jbnu.ksh.blogtp.common.jpa.BaseTimeEntity;
import kr.ac.jbnu.ksh.blogtp.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_tokens_token", columnList = "token", unique = true),
                @Index(name = "idx_refresh_tokens_user", columnList = "user_id")
        })
public class RefreshToken extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_refresh_tokens_user"))
    private User user;

    @Column(nullable = false, length = 200)
    private String token;

    @Column(nullable = false)
    private OffsetDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    public RefreshToken(User user, String token, OffsetDateTime expiresAt) {
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
        this.revoked = false;
    }

    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(expiresAt);
    }

    public void revoke() {
        this.revoked = true;
    }
}
