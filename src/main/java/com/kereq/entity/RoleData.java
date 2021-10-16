package com.kereq.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="ROLES")
@AttributeOverride(name = "ROLE_AUDIT_CD", column = @Column(name = "AUDIT_CD"))
@AttributeOverride(name = "ROLE_AUDIT_MD", column = @Column(name = "AUDIT_MD"))
@AttributeOverride(name = "ROLE_AUDIT_RD", column = @Column(name = "AUDIT_RD"))
@Getter
@Setter
public class RoleData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="ROLE_ID")
    private Long id;

    @Column(name="ROLE_CODE")
    private String code;

    @Column(name="ROLE_NAME")
    private String name;
}
