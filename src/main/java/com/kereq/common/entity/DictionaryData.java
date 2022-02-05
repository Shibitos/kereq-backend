package com.kereq.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "DICTIONARIES")
@AttributeOverride(name = "auditCD", column = @Column(name = "DICT_AUDIT_CD"))
@AttributeOverride(name = "auditCU", column = @Column(name = "DICT_AUDIT_CU"))
@AttributeOverride(name = "auditMD", column = @Column(name = "DICT_AUDIT_MD"))
@AttributeOverride(name = "auditMU", column = @Column(name = "DICT_AUDIT_MU"))
@AttributeOverride(name = "auditRD", column = @Column(name = "DICT_AUDIT_RD"))
@AttributeOverride(name = "version", column = @Column(name = "DICT_VERSION"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DictionaryData extends AuditableEntity {

    private static final long serialVersionUID = 8416881672476204258L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DICT_ID")
    @SequenceGenerator(name = "SEQ_DICT_ID", sequenceName = "SEQ_DICT_ID", allocationSize = 50)
    @Column(name = "DICT_ID")
    private Long id;

    @Column(name = "DICT_CODE", length = 25)
    @NotNull
    @Size(min = 4, max = 25)
    private String code;

    @OneToMany(targetEntity = DictionaryItemData.class, mappedBy = "dictionary",
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<DictionaryItemData> items;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DictionaryData that = (DictionaryData) o;
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
                "code = " + getCode() + ")";
    }
}
