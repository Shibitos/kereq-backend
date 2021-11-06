package com.kereq.messaging.entity;

import com.kereq.main.entity.AuditableEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "MESSAGE_TEMPLATES")
@AttributeOverride(name = "auditCD", column = @Column(name = "MSG_TMP_AUDIT_CD"))
@AttributeOverride(name = "auditMD", column = @Column(name = "MSG_TMP_AUDIT_MD"))
@AttributeOverride(name = "auditRD", column = @Column(name = "MSG_TMP_AUDIT_RD"))
@Getter
@Setter
public class MessageTemplateData extends AuditableEntity {

    private static final long serialVersionUID = 3920250129891131537L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MSG_TMP_ID")
    private Long id;

    @Column(name = "MSG_TMP_CODE", unique = true)
    @NotNull
    private String code;

    @Column(name = "MSG_TMP_SUBJECT")
    @NotNull
    private String subject;

    @Column(name = "MSG_TMP_BODY")
    @NotNull
    private String body;
}
