package com.kereq.messaging.entity;

import com.kereq.common.entity.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "MESSAGE_TEMPLATES")
@AttributeOverride(name = "auditCD", column = @Column(name = "MSG_TMP_AUDIT_CD"))
@AttributeOverride(name = "auditCU", column = @Column(name = "MSG_TMP_AUDIT_CU"))
@AttributeOverride(name = "auditMD", column = @Column(name = "MSG_TMP_AUDIT_MD"))
@AttributeOverride(name = "auditMU", column = @Column(name = "MSG_TMP_AUDIT_MU"))
@AttributeOverride(name = "auditRD", column = @Column(name = "MSG_TMP_AUDIT_RD"))
@AttributeOverride(name = "version", column = @Column(name = "MSG_TMP_VERSION"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageTemplateData extends AuditableEntity {

    private static final long serialVersionUID = 3920250129891131537L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MSG_TMP_ID")
    @SequenceGenerator(name = "SEQ_MSG_TMP_ID", sequenceName = "SEQ_MSG_TMP_ID", allocationSize = 50)
    @Column(name = "MSG_TMP_ID")
    private Long id;

    @Column(name = "MSG_TMP_CODE", unique = true)
    @NotNull
    private String code;

    @Column(name = "MSG_TMP_SUBJECT", length = 100)
    @Size(min = 8, max = 100)
    @NotNull
    private String subject;

    @Column(name = "MSG_TMP_BODY")
    @NotNull
    private String body;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MessageTemplateData that = (MessageTemplateData) o;
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
                "code = " + getCode() + ", " +
                "subject = " + getSubject() + ", " +
                "body = " + getBody() + ")";
    }
}
