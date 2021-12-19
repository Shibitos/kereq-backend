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
@Table(name = "COMMENTS_STATISTICS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentStatisticsData extends BaseEntity {

    private static final long serialVersionUID = 7233435208764441318L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_COSTAT_ID")
    @SequenceGenerator(name = "SEQ_COSTAT_ID", sequenceName = "SEQ_COSTAT_ID", allocationSize = 50)
    @Column(name = "COSTAT_ID")
    private Long id;
    
    @Column(name = "COSTAT_COMM_ID")
    //@NotNull
    private Long commentId;

    @OneToOne(targetEntity = PostData.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "COSTAT_COMM_ID", insertable = false, updatable = false)
    private PostData comment;

    @Column(name = "COSTAT_LIKES_COUNT")
    @NotNull
    private int likesCount;

    @Column(name = "COSTAT_DISLIKES_COUNT")
    @NotNull
    private int dislikesCount;

    @Transient
    private Integer userLikeType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CommentStatisticsData that = (CommentStatisticsData) o;
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
                "commentId = " + commentId + ", " +
                "likesCount = " + likesCount + ", " +
                "dislikesCount = " + dislikesCount + ")";
    }
}
