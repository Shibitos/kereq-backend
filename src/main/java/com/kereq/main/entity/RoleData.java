package com.kereq.main.entity;

import com.kereq.common.entity.AuditableEntity;
import com.kereq.common.entity.CodeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "ROLES")
@AttributeOverride(name = "auditCD", column = @Column(name = "ROLE_AUDIT_CD"))
@AttributeOverride(name = "auditMD", column = @Column(name = "ROLE_AUDIT_MD"))
@AttributeOverride(name = "auditRD", column = @Column(name = "ROLE_AUDIT_RD"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoleData extends AuditableEntity implements CodeEntity {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RoleData roleData = (RoleData) o;
        return id != null && Objects.equals(id, roleData.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + getId() + ", " +
                "auditCD = " + getAuditCD() + ", " +
                "auditMD = " + getAuditMD() + ", " +
                "auditRD = " + getAuditRD() + ", " +
                "code = " + getCode() + ", " +
                "name = " + getName() + ")";
    }
}
