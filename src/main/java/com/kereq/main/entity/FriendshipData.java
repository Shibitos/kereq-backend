package com.kereq.main.entity;

import com.kereq.common.entity.AuditableEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "FRIENDSHIPS")
@AttributeOverride(name = "auditCD", column = @Column(name = "FRS_AUDIT_CD"))
@AttributeOverride(name = "auditMD", column = @Column(name = "FRS_AUDIT_MD"))
@AttributeOverride(name = "auditRD", column = @Column(name = "FRS_AUDIT_RD"))
@NoArgsConstructor
@Getter
@Setter
public class FriendshipData extends AuditableEntity {

    private static final long serialVersionUID = 5309937945164911089L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FRS_ID")
    @SequenceGenerator(name = "SEQ_FRS_ID", sequenceName = "SEQ_FRS_ID", allocationSize = 50)
    @Column(name = "FRS_ID")
    private Long id;

    @Column(name = "FRS_USER_ID")
    //@NotNull
    private Long userId;

    @Column(name = "FRS_FRIEND_ID")
    //@NotNull
    private Long friendId;

    @OneToOne(targetEntity = UserData.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "FRS_USER_ID", insertable = false, updatable = false)
    private UserData user;

    @OneToOne(targetEntity = UserData.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "FRS_FRIEND_ID", insertable = false, updatable = false)
    private UserData friend;

    @Column(name = "FRS_STATUS")
    @NotNull
    private String status;

    public interface FriendshipStatus { //TODO: validation?
        String INVITED = "I";
        String DECLINED = "D";
        String ACCEPTED = "A";

        String ALL = "I|D|A";
    }

    public FriendshipData(UserData user, boolean isFriend, Date auditMD) {
        if (isFriend) {
            this.friend = user;
        } else {
            this.user = user;
        }
        this.setAuditMD(auditMD);
    }
}
