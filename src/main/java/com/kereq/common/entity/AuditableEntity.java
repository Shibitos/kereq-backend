package com.kereq.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class AuditableEntity extends BaseEntity {

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
                "auditCD = " + auditCD + ", " +
                "auditMD = " + auditMD + ", " +
                "auditRD = " + auditRD + ")";
    }
}
