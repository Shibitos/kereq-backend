package com.kereq.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name="USERS")
@AttributeOverride(name = "USER_AUDIT_CD", column = @Column(name = "AUDIT_CD"))
@AttributeOverride(name = "USER_AUDIT_MD", column = @Column(name = "AUDIT_MD"))
@AttributeOverride(name = "USER_AUDIT_RD", column = @Column(name = "AUDIT_RD"))
@Getter
@Setter
public class UserData extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="USER_ID")
    private Long id;

    @Column(name="USER_LOGIN")
    private String login;

    @Column(name="USER_FIRST_NAME")
    private String firstName;

    @Column(name="USER_LAST_NAME")
    private String lastName;

    @Column(name="USER_EMAIL")
    private String email;

    @Column(name="USER_PASSWORD")
    private String password;

    @Column(name="USER_ENABLED")
    private boolean enabled;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "ROLES", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private Set<RoleData> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getCode()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
