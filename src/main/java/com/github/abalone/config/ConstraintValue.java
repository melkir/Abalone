package com.github.abalone.config;

import java.util.Set;

/**
 * @author sardemff7
 */
public abstract class ConstraintValue<T> extends Value<T> {
    protected ConstraintValue(String description, T value) {
        super(description, value);
    }

    abstract protected void initConstraint();

    abstract public Set<T> getList();

    abstract protected Boolean check(T value);

    @Override
    final public void set(T value) {
        this.initConstraint();
        if (this.check(value))
            super.set(value);
    }
}
