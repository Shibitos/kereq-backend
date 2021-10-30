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
public class TokenData { //TODO: purge tokens task

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TKN_ID")
    private Long id;

    @Column(name = "TKN_VALUE", length = 36)
    @NotNull
    private String value; //TODO: UUID?

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
