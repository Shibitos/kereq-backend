package com.kereq.common.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public class AuditableEntity implements Serializable { //TODO: search criteria?

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "AUDIT_CD")
    private Date auditCD;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "AUDIT_MD")
    private Date auditMD;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "AUDIT_RD")
    private Date auditRD;

//    @CreatedBy
//    @Column(updatable = false)
//    @JsonIgnore
//    private String createdBy;
//
//    @LastModifiedBy
//    @JsonIgnore
//    private String updatedBy;
}
