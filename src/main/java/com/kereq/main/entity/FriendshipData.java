package com.kereq.main.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "FRIENDSHIPS")
@AttributeOverride(name = "auditCD", column = @Column(name = "FRS_AUDIT_CD"))
@AttributeOverride(name = "auditMD", column = @Column(name = "FRS_AUDIT_MD"))
@AttributeOverride(name = "auditRD", column = @Column(name = "FRS_AUDIT_RD"))
@Getter
@Setter
public class FriendshipData extends AuditableEntity {

    private static final long serialVersionUID = 5309937945164911089L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FRS_ID")
    private Long id;

    @OneToOne(targetEntity = UserData.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "FRS_USER_ID")
    private Long userId;

    @OneToOne(targetEntity = UserData.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "FRS_FRIEND_ID")
    private Long friendId;

    @Column(name = "FRS_STATUS")
    @NotNull
    private String status;

    public interface FriendshipStatus { //TODO: validation?
        String INVITED = "I";
        String DECLINED = "D";
        String ACCEPTED = "A";

        String ALL = "I|D|A";
    }
}
