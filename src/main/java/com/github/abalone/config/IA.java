package com.github.abalone.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by melkir on 05/04/14.
 */
public class IA extends ConstraintValue<String> {

    private static HashSet<String> list;

    public IA() {
        super("Algorithme", "NegaScout");
    }

    @Override
    protected void initConstraint() {
        if (IA.list != null) return;
        IA.list = new HashSet<String>();
        IA.list.add("MiniMax");
        IA.list.add("AlphaBeta");
        IA.list.add("NegaScout");
    }

    @Override
    protected Boolean check(String value) {
        return IA.list.contains(value);
    }

    @Override
    public Set<String> getList() {
        return Collections.unmodifiableSet(IA.list);
    }
}
