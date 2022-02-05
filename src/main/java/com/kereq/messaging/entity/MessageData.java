package com.kereq.messaging.entity;

import com.kereq.common.entity.AuditableEntity;
import com.kereq.common.validation.annotation.AllowedStrings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "MESSAGES")
@AttributeOverride(name = "auditCD", column = @Column(name = "MSG_AUDIT_CD"))
@AttributeOverride(name = "auditCU", column = @Column(name = "MSG_AUDIT_CU"))
@AttributeOverride(name = "auditMD", column = @Column(name = "MSG_AUDIT_MD"))
@AttributeOverride(name = "auditMU", column = @Column(name = "MSG_AUDIT_MU"))
@AttributeOverride(name = "auditRD", column = @Column(name = "MSG_AUDIT_RD"))
@AttributeOverride(name = "version", column = @Column(name = "MSG_VERSION"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageData extends AuditableEntity {

    private static final long serialVersionUID = -2049230680668747742L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MSG_ID")
    @SequenceGenerator(name = "SEQ_MSG_ID", sequenceName = "SEQ_MSG_ID", allocationSize = 50)
    @Column(name = "MSG_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MSG_MSG_TMP_ID", referencedColumnName = "MSG_TMP_ID")
    private MessageTemplateData template;

    @Column(name = "MSG_SUBJECT", length = 77)
    @Size(min = 8, max = 77)
    private String subject;

    @Column(name = "MSG_BODY")
    private String body;

    @Column(name = "MSG_FROM", length = 50)
    @Size(min = 8, max = 50)
    private String from;

    @Column(name = "MSG_TO", length = 50)
    @Size(min = 8, max = 50)
    private String to;

    @Column(name = "MSG_STATUS")
    @AllowedStrings(allowedValues = Status.ALL, delimiter = "|")
    private String status;

    @Column(name = "MSG_RETRY_COUNT")
    private Integer retryCount;

    @Column(name = "MSG_ERROR_MESSAGE", length = 100)
    private String errorMessage;

    public interface Status {
        String PENDING = "P";
        String SENT = "S";
        String FAILED = "F";

        String ALL = "P|S|F";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MessageData that = (MessageData) o;
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
                "subject = " + getSubject() + ", " +
                "body = " + getBody() + ", " +
                "from = " + getFrom() + ", " +
                "to = " + getTo() + ", " +
                "status = " + getStatus() + ", " +
                "retryCount = " + getRetryCount() + ")";
    }
}
