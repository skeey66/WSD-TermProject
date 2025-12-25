package kr.ac.jbnu.ksh.blogtp.comment.domain;

import jakarta.persistence.*;
import kr.ac.jbnu.ksh.blogtp.common.jpa.BaseTimeEntity;
import kr.ac.jbnu.ksh.blogtp.post.domain.Post;
import kr.ac.jbnu.ksh.blogtp.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "comments",
        indexes = {
                @Index(name = "idx_comments_post", columnList = "post_id"),
                @Index(name = "idx_comments_author", columnList = "author_id")
        })
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "fk_comments_post"))
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", foreignKey = @ForeignKey(name = "fk_comments_author"))
    private User author;

    @Lob
    @Column(nullable = false)
    private String content;

    public Comment(Post post, User author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
    }

    public void edit(String content) {
        this.content = content;
    }
}
