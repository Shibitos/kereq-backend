package com.kereq.common.entity;

import java.io.Serializable;
import java.util.Objects;

public abstract class BaseEntity implements Serializable {

    public abstract Long getId();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity other = (BaseEntity) o;
        return Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + getId() +
                '}';
    }
}
