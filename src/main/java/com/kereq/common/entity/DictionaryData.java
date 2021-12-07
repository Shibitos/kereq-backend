package com.kereq.common.entity;

import com.kereq.main.entity.AuditableEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "DICTIONARIES")
@AttributeOverride(name = "auditCD", column = @Column(name = "DICT_AUDIT_CD"))
@AttributeOverride(name = "auditMD", column = @Column(name = "DICT_AUDIT_MD"))
@AttributeOverride(name = "auditRD", column = @Column(name = "DICT_AUDIT_RD"))
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
}
