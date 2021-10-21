package com.kereq.main.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name="USERS")
@AttributeOverride(name = "auditCD", column = @Column(name = "USER_AUDIT_CD"))
@AttributeOverride(name = "auditMD", column = @Column(name = "USER_AUDIT_MD"))
@AttributeOverride(name = "auditRD", column = @Column(name = "USER_AUDIT_RD"))
@Getter
@Setter
public class UserData extends AuditableEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="USER_ID")
    private Long id;

    @Column(name="USER_LOGIN", length = 25, unique = true)
    @NotNull
    @Size(min = 4, max = 25)
    private String login;

    @Column(name="USER_FIRST_NAME", length = 25)
    @NotNull
    @Size(min = 4, max = 25)
    private String firstName;

    @Column(name="USER_LAST_NAME", length = 25)
    @NotNull
    @Size(min = 4, max = 25)
    private String lastName;

    @Column(name="USER_EMAIL", length = 50, unique = true)
    @NotNull
    @Size(min = 8, max = 50)
    private String email;

    @JsonIgnore
    @Column(name="USER_PASSWORD", length = 72)
    @NotNull
    private String password;

    @JsonIgnore
    @Column(name="USER_ACTIVATED")
    @NotNull
    private boolean activated;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "USERS_ROLES",
            joinColumns = {@JoinColumn(name = "UR_USER_ID", referencedColumnName = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "UR_ROLE_ID", referencedColumnName = "ROLE_ID")})
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
        return activated;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }
}
