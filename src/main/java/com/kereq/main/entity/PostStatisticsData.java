package com.kereq.main.entity;

import com.kereq.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "POSTS_STATISTICS")
@AttributeOverride(name = "version", column = @Column(name = "POSTAT_VERSION"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostStatisticsData extends BaseEntity {

    private static final long serialVersionUID = 7233435208764441318L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_POSTAT_ID")
    @SequenceGenerator(name = "SEQ_POSTAT_ID", sequenceName = "SEQ_POSTAT_ID", allocationSize = 50)
    @Column(name = "POSTAT_ID")
    private Long id;
    
    @Column(name = "POSTAT_POST_ID")
    //@NotNull
    private Long postId;

    @OneToOne(targetEntity = PostData.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "POSTAT_POST_ID", insertable = false, updatable = false)
    private PostData post;

    @Column(name = "POSTAT_LIKES_COUNT")
    @NotNull
    private int likesCount;

    @Column(name = "POSTAT_DISLIKES_COUNT")
    @NotNull
    private int dislikesCount;

    @Column(name = "POSTAT_COMMENTS_COUNT")
    @NotNull
    private int commentsCount;

    @Transient
    private Integer userLikeType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PostStatisticsData that = (PostStatisticsData) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "postId = " + postId + ", " +
                "likesCount = " + likesCount + ", " +
                "dislikesCount = " + dislikesCount + ", " +
                "commentsCount = " + commentsCount + ")";
    }
}
