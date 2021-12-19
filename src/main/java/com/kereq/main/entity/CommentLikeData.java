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
@Table(name = "COMMENTS_LIKES")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentLikeData extends BaseEntity {

    private static final long serialVersionUID = -1892019946733699323L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_COLK_ID")
    @SequenceGenerator(name = "SEQ_COLK_ID", sequenceName = "SEQ_COLK_ID", allocationSize = 50)
    @Column(name = "COLK_ID")
    private Long id;

    @Column(name = "COLK_USER_ID")
    //@NotNull
    private Long userId;

    @OneToOne(targetEntity = UserData.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "COLK_USER_ID", insertable = false, updatable = false)
    private UserData user;

    @Column(name = "COLK_COMM_ID")
    //@NotNull
    private Long commentId;

    @OneToOne(targetEntity = PostData.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "COLK_COMM_ID", insertable = false, updatable = false)
    private PostData comment;

    @Column(name = "COLK_TYPE")
    @NotNull
    private int type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CommentLikeData that = (CommentLikeData) o;
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
                "userId = " + userId + ", " +
                "commentId = " + commentId + ", " +
                "type = " + type + ")";
    }
}
