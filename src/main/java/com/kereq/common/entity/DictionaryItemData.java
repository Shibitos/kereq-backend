package com.kereq.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "DICTIONARY_ITEMS")
@AttributeOverride(name = "auditCD", column = @Column(name = "DICT_ITEM_AUDIT_CD"))
@AttributeOverride(name = "auditMD", column = @Column(name = "DICT_ITEM_AUDIT_MD"))
@AttributeOverride(name = "auditRD", column = @Column(name = "DICT_ITEM_AUDIT_RD"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DictionaryItemData extends AuditableEntity implements CodeEntity {

    private static final long serialVersionUID = 2929483954401454121L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DICT_ITEM_ID")
    @SequenceGenerator(name = "SEQ_DICT_ITEM_ID", sequenceName = "SEQ_DICT_ITEM_ID", allocationSize = 50)
    @Column(name = "DICT_ITEM_ID")
    private Long id;

    @Column(name = "DICT_ITEM_CODE", length = 25)
    @NotNull
    @Size(min = 4, max = 25)
    private String code;

    @Column(name = "DICT_ITEM_VALUE", length = 50)
    @NotNull
    @Size(min = 4, max = 50)
    private String value;

    @ManyToOne
    @JoinColumn(name="DICT_ITEM_DICT_ID", nullable=false)
    private DictionaryData dictionary;
}
