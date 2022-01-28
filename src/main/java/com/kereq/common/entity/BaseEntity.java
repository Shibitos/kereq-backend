package com.kereq.common.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity implements Serializable {

    @Version
    @Column(name = "version")
    private Long version;

    public abstract Long getId();

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + getId() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getVersion(), that.getVersion());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
