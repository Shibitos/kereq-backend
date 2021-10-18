package com.kereq.main.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="ROLES")
@AttributeOverride(name = "auditCD", column = @Column(name = "ROLE_AUDIT_CD"))
@AttributeOverride(name = "auditMD", column = @Column(name = "ROLE_AUDIT_MD"))
@AttributeOverride(name = "auditRD", column = @Column(name = "ROLE_AUDIT_RD"))
@Getter
@Setter
public class RoleData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ROLE_ID")
    private Short id;

    @Column(name="ROLE_CODE", length = 20, unique = true)
    @NotNull
    private String code;

    @Column(name="ROLE_NAME", length = 50)
    @NotNull
    private String name;
}
