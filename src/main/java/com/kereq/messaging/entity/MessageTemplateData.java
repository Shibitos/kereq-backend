package com.kereq.messaging.entity;

import com.kereq.common.entity.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "MESSAGE_TEMPLATES")
@AttributeOverride(name = "auditCD", column = @Column(name = "MSG_TMP_AUDIT_CD"))
@AttributeOverride(name = "auditMD", column = @Column(name = "MSG_TMP_AUDIT_MD"))
@AttributeOverride(name = "auditRD", column = @Column(name = "MSG_TMP_AUDIT_RD"))
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
}
