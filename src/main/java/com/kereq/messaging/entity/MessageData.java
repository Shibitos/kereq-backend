package com.kereq.messaging.entity;

import com.kereq.main.entity.AuditableEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="MESSAGES")
@AttributeOverride(name = "auditCD", column = @Column(name = "MSG_AUDIT_CD"))
@AttributeOverride(name = "auditMD", column = @Column(name = "MSG_AUDIT_MD"))
@AttributeOverride(name = "auditRD", column = @Column(name = "MSG_AUDIT_RD"))
@Getter
@Setter
public class MessageData extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MSG_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MSG_MSG_TMP_ID", referencedColumnName = "MSG_TMP_ID")
    private MessageTemplateData template;

    @Column(name="MSG_SUBJECT")
    private String subject;

    @Column(name="MSG_BODY")
    private String body;

    @Column(name="MSG_FROM")
    private String from;

    @Column(name="MSG_TO")
    private String to;

    @Column(name="MSG_STATUS")
    private String status;

    @Column(name="MSG_RETRY_COUNT")
    private Integer retryCount;

    public interface Status {
        String PENDING = "P";
        String SENT = "S";
        String FAILED = "F";

        String ALL = "P|S|F";
    }
}
