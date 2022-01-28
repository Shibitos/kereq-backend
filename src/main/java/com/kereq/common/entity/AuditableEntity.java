package com.kereq.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity extends BaseEntity {

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "AUDIT_CD", nullable = false, updatable = false)
    private Date auditCD;

    @CreatedBy
    @Column(name = "AUDIT_CU", updatable = false)
    private Long auditCU;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "AUDIT_MD")
    private Date auditMD;

    @LastModifiedBy
    @Column(name = "AUDIT_MU")
    private Long auditMU;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "AUDIT_RD")
    private Date auditRD;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "auditCD = " + getAuditCD() + ", " +
                "auditCU = " + getAuditCU() + ", " +
                "auditMD = " + getAuditMD() + ", " +
                "auditMU = " + getAuditMU() + ", " +
                "auditRD = " + getAuditRD() + ", " +
                "version = " + getVersion() + ")";
    }
}
