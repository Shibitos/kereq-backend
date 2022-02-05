package com.kereq.main.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kereq.common.constant.Dictionary;
import com.kereq.common.constant.Gender;
import com.kereq.common.entity.AuditableEntity;
import com.kereq.common.validation.annotation.AllowedStrings;
import com.kereq.common.validation.annotation.DictionaryValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "USERS")
@AttributeOverride(name = "auditCD", column = @Column(name = "USER_AUDIT_CD"))
@AttributeOverride(name = "auditCU", column = @Column(name = "USER_AUDIT_CU"))
@AttributeOverride(name = "auditMD", column = @Column(name = "USER_AUDIT_MD"))
@AttributeOverride(name = "auditMU", column = @Column(name = "USER_AUDIT_MU"))
@AttributeOverride(name = "auditRD", column = @Column(name = "USER_AUDIT_RD"))
@AttributeOverride(name = "version", column = @Column(name = "USER_VERSION"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserData extends AuditableEntity implements UserDataInfo {

    private static final long serialVersionUID = 4675228760392277493L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USER_ID")
    @SequenceGenerator(name = "SEQ_USER_ID", sequenceName = "SEQ_USER_ID", allocationSize = 50)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "USER_FIRST_NAME", length = 35)
    @NotNull
    @Size(min = 2, max = 35)
    private String firstName;

    @Column(name = "USER_LAST_NAME", length = 40)
    @NotNull
    @Size(min = 2, max = 40)
    private String lastName;

    @Column(name = "USER_EMAIL", length = 50, unique = true)
    @Size(min = 8, max = 50)
    private String email;

    @Column(name = "USER_BIOGRAPHY", length = 200)
    @Size(max = 200)
    private String biography; //TODO: lazy? another entity like profile?

    @JsonIgnore
    @Column(name = "USER_PASSWORD", length = 72)
    @NotNull
    private String password;

    @JsonIgnore
    @Column(name = "USER_ACTIVATED")
    @NotNull
    private boolean activated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "USER_BIRTH_DATE")
    @NotNull
    private Date birthDate;

    @Column(name = "USER_GENDER")
    @AllowedStrings(allowedValues = {Gender.MALE, Gender.FEMALE})
    private String gender;

    @Column(name = "USER_COUNTRY", length = 30)
    @DictionaryValue(code = Dictionary.COUNTRIES)
    private String country;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "USERS_ROLES",
            joinColumns = {@JoinColumn(name = "UR_USER_ID", referencedColumnName = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "UR_ROLE_ID", referencedColumnName = "ROLE_ID")})
    private Set<RoleData> roles;

    @OneToOne(targetEntity = PhotoData.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_PROFILE_PHOTO_ID")
    private PhotoData profilePhoto;

//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "FRIENDSHIPS",
//            joinColumns = {@JoinColumn(name = "FRS_USER_ID", referencedColumnName = "USER_ID")},
//            inverseJoinColumns = {@JoinColumn(name = "FRS_FRIEND_ID", referencedColumnName = "USER_ID")})
//    @WhereJoinTable(clause =  "FRS_STATUS = '" + FriendshipData.FriendshipStatus.ACCEPTED + "'")
//    private Set<UserData> friends;
//
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "FRIENDSHIPS",
//            joinColumns = {@JoinColumn(name = "FRS_FRIEND_ID", referencedColumnName = "USER_ID")},
//            inverseJoinColumns = {@JoinColumn(name = "FRS_USER_ID", referencedColumnName = "USER_ID")})
//    @WhereJoinTable(clause = "FRS_STATUS = '" + FriendshipData.FriendshipStatus.INVITED + "'")
//    private Set<UserData> invitations;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getCode()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserData userData = (UserData) o;
        return id != null && Objects.equals(id, userData.id);
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
                "firstName = " + getFirstName() + ", " +
                "lastName = " + getLastName() + ", " +
                "email = " + getEmail() + ", " +
                "activated = " + isActivated() + ", " +
                "birthDate = " + getBirthDate() + ", " +
                "gender = " + getGender() + ", " +
                "country = " + getCountry() + ")";
    }
}
