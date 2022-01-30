package com.kereq.main.entity;

import com.kereq.common.entity.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
}
