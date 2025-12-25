package kr.ac.jbnu.ksh.blogtp.bookmark.domain;

import jakarta.persistence.*;
import kr.ac.jbnu.ksh.blogtp.common.jpa.BaseTimeEntity;
import kr.ac.jbnu.ksh.blogtp.post.domain.Post;
import kr.ac.jbnu.ksh.blogtp.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "bookmarks",
        uniqueConstraints = {@UniqueConstraint(name = "uk_bookmarks_post_user", columnNames = {"post_id", "user_id"})},
        indexes = {
                @Index(name = "idx_bookmarks_post", columnList = "post_id"),
                @Index(name = "idx_bookmarks_user", columnList = "user_id")
        })
public class Bookmark extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "fk_bookmarks_post"))
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_bookmarks_user"))
    private User user;

    public Bookmark(Post post, User user) {
        this.post = post;
        this.user = user;
    }
}
