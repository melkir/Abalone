package com.github.abalone.model;

import com.github.abalone.util.Color;
import com.github.abalone.util.Coords;
import com.github.abalone.util.Direction;

import java.io.Serializable;

/**
 * @author melkir
 */
public class Ball implements Serializable {

    private Color color;
    private Coords coords;

    public Ball(Color color, Integer row, Integer col) {
        this.color = color;
        this.coords = new Coords(row, col);
    }

    public Ball(Color color, Coords coords) {
        this.color = color;
        this.coords = coords;
    }

    public Ball(Ball b) {
        this.color = b.color;
        this.coords = new Coords(b.coords);
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Coords getCoords() {
        return this.coords;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ball other = (Ball) obj;
        return this.color == other.color && !(this.coords != other.coords && (this.coords == null || !this.coords.equals(other.coords)));
    }

    public void move(Direction direction) {
        coords = coords.moveTo(direction);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.color != null ? this.color.hashCode() : 0);
        hash = 43 * hash + (this.coords != null ? this.coords.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Ball (" + this.color.name() + "): " + this.coords.toString();
    }

}
