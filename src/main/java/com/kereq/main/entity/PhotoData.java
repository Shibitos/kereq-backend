package com.kereq.main.entity;

import com.kereq.common.entity.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "PHOTOS")
@AttributeOverride(name = "auditCD", column = @Column(name = "PHT_AUDIT_CD"))
@AttributeOverride(name = "auditCU", column = @Column(name = "PHT_AUDIT_CU"))
@AttributeOverride(name = "auditMD", column = @Column(name = "PHT_AUDIT_MD"))
@AttributeOverride(name = "auditMU", column = @Column(name = "PHT_AUDIT_MU"))
@AttributeOverride(name = "auditRD", column = @Column(name = "PHT_AUDIT_RD"))
@AttributeOverride(name = "version", column = @Column(name = "PHT_VERSION"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PhotoData extends AuditableEntity {

    private static final long serialVersionUID = -402252243134782957L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PHT_ID")
    @SequenceGenerator(name = "SEQ_PHT_ID", sequenceName = "SEQ_PHT_ID", allocationSize = 50)
    @Column(name = "PHT_ID")
    private Long id;

    @Column(name = "PHT_USER_ID")
    @NotNull
    private Long userId;

    @OneToOne(targetEntity = UserData.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "PHT_USER_ID", insertable = false, updatable = false)
    private UserData user;

    @Column(name = "PHT_UUID", unique = true)
    @NotNull
    private UUID uuid;

    @Column(name = "PHT_TYPE")
    @NotNull
    private String type;

    public interface PhotoType {
        String PHOTO = "P";
        String PROFILE = "R";

        String ALL = "P|R";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PhotoData photoData = (PhotoData) o;
        return id != null && Objects.equals(id, photoData.id);
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
                "auditCU = " + getAuditCU() + ", " +
                "auditMD = " + getAuditMD() + ", " +
                "auditMU = " + getAuditMU() + ", " +
                "auditRD = " + getAuditRD() + ", " +
                "version = " + getVersion() + ", " +
                "userId = " + getUserId() + ", " +
                "uuid = " + getUuid() + ", " +
                "type = " + getType() + ")";
    }
}
