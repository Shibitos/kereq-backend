package com.kereq.authorization.entity;


import com.kereq.common.entity.BaseEntity;
import com.kereq.common.validation.annotation.AllowedStrings;
import com.kereq.main.entity.UserData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "USER_TOKENS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TokenData extends BaseEntity {

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
    @AllowedStrings(allowedValues = TokenType.ALL, delimiter = "|")
    private String type;

    @OneToOne(targetEntity = UserData.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "TKN_USER_ID")
    private UserData user;

    public interface TokenType {
        String VERIFICATION = "V";
        String PASSWORD_RESET = "R";

        String ALL = "V|R";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TokenData tokenData = (TokenData) o;
        return id != null && Objects.equals(id, tokenData.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "value = " + value + ", " +
                "expireDate = " + expireDate + ", " +
                "lastSendDate = " + lastSendDate + ", " +
                "type = " + type + ", " +
                "user = " + user.getId() + ")";
    }
}
