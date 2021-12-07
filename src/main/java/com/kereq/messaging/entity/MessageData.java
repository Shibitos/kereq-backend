package com.kereq.messaging.entity;

import com.kereq.common.entity.AuditableEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "MESSAGES")
@AttributeOverride(name = "auditCD", column = @Column(name = "MSG_AUDIT_CD"))
@AttributeOverride(name = "auditMD", column = @Column(name = "MSG_AUDIT_MD"))
@AttributeOverride(name = "auditRD", column = @Column(name = "MSG_AUDIT_RD"))
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
    private String status;

    @Column(name = "MSG_RETRY_COUNT")
    private Integer retryCount;

    public interface Status {
        String PENDING = "P";
        String SENT = "S";
        String FAILED = "F";

        String ALL = "P|S|F";
    }
}
