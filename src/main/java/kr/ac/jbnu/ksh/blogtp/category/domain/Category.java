package kr.ac.jbnu.ksh.blogtp.category.domain;

import jakarta.persistence.*;
import kr.ac.jbnu.ksh.blogtp.common.jpa.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "categories",
        uniqueConstraints = {@UniqueConstraint(name = "uk_categories_name", columnNames = "name")})
public class Category extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    public Category(String name) {
        this.name = name;
    }

    public void rename(String name) {
        this.name = name;
    }
}
