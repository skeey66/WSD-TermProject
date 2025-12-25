package kr.ac.jbnu.ksh.blogtp.post.domain;

import jakarta.persistence.*;
import kr.ac.jbnu.ksh.blogtp.category.domain.Category;
import kr.ac.jbnu.ksh.blogtp.common.jpa.BaseTimeEntity;
import kr.ac.jbnu.ksh.blogtp.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "posts",
        indexes = {
                @Index(name = "idx_posts_created_at", columnList = "createdAt"),
                @Index(name = "idx_posts_author", columnList = "author_id"),
                @Index(name = "idx_posts_category", columnList = "category_id")
        })
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", foreignKey = @ForeignKey(name = "fk_posts_author"))
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_posts_category"))
    private Category category;

    @Column(nullable = false, length = 120)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean published;

    public Post(User author, Category category, String title, String content) {
        this.author = author;
        this.category = category;
        this.title = title;
        this.content = content;
        this.published = false;
    }

    public void update(Category category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
    }

    public void publish() {
        this.published = true;
    }

    public void unpublish() {
        this.published = false;
    }
}
