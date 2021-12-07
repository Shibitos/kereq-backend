package com.kereq.authorization.entity;


import com.kereq.main.entity.UserData;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "USER_TOKENS")
@Getter
@Setter
public class TokenData {

    private static final long serialVersionUID = -3364805981914373200L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TKN_ID")
    @SequenceGenerator(name = "SEQ_TKN_ID", sequenceName = "SEQ_TKN_ID", allocationSize = 50)
    @Column(name = "TKN_ID")
    private Long id;

    @Column(name = "TKN_VALUE", length = 36)
    @NotNull
    private String value;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TKN_EXPIRE_DATE")
    @NotNull
    private Date expireDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TKN_LAST_SEND_DATE")
    private Date lastSendDate;

    @Column(name = "TKN_TYPE", length = 1)
    @NotNull
    private String type;

    @OneToOne(targetEntity = UserData.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "TKN_USER_ID")
    private UserData user;

    public interface TokenType { //TODO: validation?
        String VERIFICATION = "V";
        String PASSWORD_RESET = "R";

        String ALL = "V|R";
    }
}
