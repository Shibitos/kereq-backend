package com.kereq.main.entity;

import com.kereq.common.entity.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "FIND_FRIEND_ADS")
@AttributeOverride(name = "auditCD", column = @Column(name = "FFA_AUDIT_CD"))
@AttributeOverride(name = "auditMD", column = @Column(name = "FFA_AUDIT_MD"))
@AttributeOverride(name = "auditRD", column = @Column(name = "FFA_AUDIT_RD"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FindFriendData extends AuditableEntity {

    private static final long serialVersionUID = 6034153620373743304L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FFA_ID")
    @SequenceGenerator(name = "SEQ_FFA_ID", sequenceName = "SEQ_FFA_ID", allocationSize = 50)
    @Column(name = "FFA_ID")
    private Long id;

    @Column(name = "FFA_MIN_AGE")
    private Integer minAge;

    @Column(name = "FFA_MAX_AGE")
    private Integer maxAge;

    @Column(name = "FFA_GENDER")
    private String gender;

    @Column(name = "FFA_DESCRIPTION")
    private String description;

    @OneToOne(targetEntity = UserData.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "FFA_USER_ID")
    private UserData user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FindFriendData that = (FindFriendData) o;
        return id != null && Objects.equals(id, that.id);
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
                "minAge = " + getMinAge() + ", " +
                "maxAge = " + getMaxAge() + ", " +
                "gender = " + getGender() + ", " +
                "description = " + getDescription() + ", " +
                "user = " + getUser() + ")";
    }
}
