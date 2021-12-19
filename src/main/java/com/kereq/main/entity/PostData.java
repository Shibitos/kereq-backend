package com.kereq.main.entity;

import com.kereq.common.entity.AuditableEntity;
import com.kereq.common.entity.DictionaryItemData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "POSTS")
@AttributeOverride(name = "auditCD", column = @Column(name = "POST_AUDIT_CD"))
@AttributeOverride(name = "auditMD", column = @Column(name = "POST_AUDIT_MD"))
@AttributeOverride(name = "auditRD", column = @Column(name = "POST_AUDIT_RD"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostData extends AuditableEntity {

    private static final long serialVersionUID = -8304076675272976647L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_POST_ID")
    @SequenceGenerator(name = "SEQ_POST_ID", sequenceName = "SEQ_POST_ID", allocationSize = 50)
    @Column(name = "POST_ID")
    private Long id;

    @OneToOne(targetEntity = UserData.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "POST_USER_ID")
    private UserData user;

    @Column(name = "POST_CONTENT", length = 1000)
    @NotNull
    private String content;

    @OneToMany(targetEntity = CommentData.class, mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CommentData> comments;

    @Transient
    private PostStatisticsData statistics;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PostData postData = (PostData) o;
        return id != null && Objects.equals(id, postData.id);
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
                "body = " + getContent() + ")";
    }
}
