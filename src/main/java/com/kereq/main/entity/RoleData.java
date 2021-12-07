package com.kereq.main.entity;

import com.kereq.common.entity.AuditableEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ROLES")
@AttributeOverride(name = "auditCD", column = @Column(name = "ROLE_AUDIT_CD"))
@AttributeOverride(name = "auditMD", column = @Column(name = "ROLE_AUDIT_MD"))
@AttributeOverride(name = "auditRD", column = @Column(name = "ROLE_AUDIT_RD"))
@Getter
@Setter
public class RoleData extends AuditableEntity {

    private static final long serialVersionUID = -5210912422426143296L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ROLE_ID")
    @SequenceGenerator(name = "SEQ_ROLE_ID", sequenceName = "SEQ_ROLE_ID", allocationSize = 50)
    @Column(name = "ROLE_ID")
    private Long id;

    @Column(name = "ROLE_CODE", length = 20, unique = true)
    @NotNull
    private String code;

    @Column(name = "ROLE_NAME", length = 50)
    @NotNull
    private String name;
}
