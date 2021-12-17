package com.kereq.main.entity;

import com.kereq.common.entity.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "COMMENTS")
@AttributeOverride(name = "auditCD", column = @Column(name = "COMM_AUDIT_CD"))
@AttributeOverride(name = "auditMD", column = @Column(name = "COMM_AUDIT_MD"))
@AttributeOverride(name = "auditRD", column = @Column(name = "COMM_AUDIT_RD"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentData extends AuditableEntity {

    private static final long serialVersionUID = 6222860611561442860L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_COMM_ID")
    @SequenceGenerator(name = "SEQ_COMM_ID", sequenceName = "SEQ_COMM_ID", allocationSize = 50)
    @Column(name = "COMM_ID")
    private Long id;

    @OneToOne(targetEntity = UserData.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "COMM_USER_ID")
    private UserData user;

    @Column(name = "COMM_POST_ID")
    //@NotNull
    private Long postId;

    @OneToOne(targetEntity = PostData.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "COMM_POST_ID", insertable = false, updatable = false)
    private PostData post;

    @Column(name = "COMM_CONTENT", length = 1000)
    @NotNull
    private String content;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CommentData that = (CommentData) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + getId() + ", " +
                "auditCD = " + getAuditCD() + ", " +
                "auditMD = " + getAuditMD() + ", " +
                "auditRD = " + getAuditRD() + ", " +
                "userId = " + getUser().getId() + ", " +
                "postId = " + getPostId() + ", " +
                "content = " + getContent() + ")";
    }
}
