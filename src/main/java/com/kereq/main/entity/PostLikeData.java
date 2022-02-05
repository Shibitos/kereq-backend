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
@Table(name = "POSTS_LIKES")
@AttributeOverride(name = "version", column = @Column(name = "POLK_VERSION"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostLikeData extends BaseEntity {

    private static final long serialVersionUID = -636856885118586196L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_POLK_ID")
    @SequenceGenerator(name = "SEQ_POLK_ID", sequenceName = "SEQ_POLK_ID", allocationSize = 50)
    @Column(name = "POLK_ID")
    private Long id;

    @Column(name = "POLK_USER_ID")
    //@NotNull
    private Long userId;

    @OneToOne(targetEntity = UserData.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "POLK_USER_ID", insertable = false, updatable = false)
    private UserData user;

    @Column(name = "POLK_POST_ID")
    //@NotNull
    private Long postId;

    @OneToOne(targetEntity = PostData.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "POLK_POST_ID", insertable = false, updatable = false)
    private PostData post;

    @Column(name = "POLK_TYPE")
    @NotNull
    private int type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PostLikeData that = (PostLikeData) o;
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
                "postId = " + postId + ", " +
                "type = " + type + ")";
    }
}
