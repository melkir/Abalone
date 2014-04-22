package com.github.abalone.util;

import java.io.Serializable;

/**
 *
 * @author melkir
 */

/**
 * The color of a Ball
 */
public enum Color implements Serializable {

    /**
     * A while Ball
     */
    WHITE,
    /**
     * A black Ball
     */
    BLACK,
    /**
     * No Ball
     */
    NONE,
    /**
     * Out of the Board
     */
    INVALID;

    public Boolean isPlayer() {
        return (this == Color.WHITE || this == Color.BLACK);
    }

    public Color other() {
        switch (this) {
            case WHITE:
                return BLACK;
            case BLACK:
                return WHITE;
            case NONE:
                return NONE;
            case INVALID:
                return INVALID;
            default:
                return this;
        }
    }

    @Override
    public String toString() {
        return this == Color.BLACK ? "noires" : "blanches";
    }
}
