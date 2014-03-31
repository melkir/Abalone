package com.github.abalone.util;

import com.sun.istack.internal.NotNull;

import java.io.Serializable;

/**
 * @author melkir
 */
public class Coords implements Serializable, Comparable<Coords> {

    private Integer row, col;

    public Coords() {
        this.row = null;
        this.col = null;
    }

    public Coords(Integer row, Integer col) {
        this.row = row;
        this.col = col;
    }

    public Coords(Coords other) {
        this.row = other.row;
        this.col = other.col;
    }

    public Integer getRow() {
        return this.row;
    }

    public Integer getCol() {
        return this.col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Coords other = (Coords) obj;
        if (!this.row.equals(other.row) && (this.row == null
                || !this.row.equals(other.row))) {
            return false;
        }
        if (!this.col.equals(other.col) && (this.col == null
                || !this.col.equals(other.col))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.row != null ? this.row.hashCode() : 0);
        hash = 83 * hash + (this.col != null ? this.col.hashCode() : 0);
        return hash;
    }

    public Coords moveTo(Direction direction) {
        Coords destination = new Coords(this);

        switch (direction) {
            case UPLEFT:
                if (--destination.row < 0) {
                    --destination.col;
                }
                break;
            case UPRIGHT:
                if (--destination.row > -1) {
                    ++destination.col;
                }
                break;
            case LEFT:
                --destination.col;
                break;
            case RIGHT:
                ++destination.col;
                break;
            case DOWNLEFT:
                if (++destination.row > 0) {
                    --destination.col;
                }
                break;
            case DOWNRIGHT:
                if (++destination.row < 1) {
                    ++destination.col;
                }
                break;
        }
        return destination;
    }

    public Typelignepl LignePl(Coords c) {
        if (this.moveTo(Direction.UPLEFT).equals(c)
                || c.equals(this.moveTo(Direction.DOWNRIGHT))) {
            return Typelignepl.DIAGONAL2;
        } else if (this.moveTo(Direction.UPRIGHT).equals(c)
                || c.equals(this.moveTo(Direction.DOWNLEFT))) {
            return Typelignepl.DIAGONAL1;
        } else if (this.moveTo(Direction.RIGHT).equals(c)
                || c.equals(this.moveTo(Direction.LEFT))) {
            return Typelignepl.HORIZONTAL;
        } else {
            return Typelignepl.NONADJACENT;
        }
    }

    @Override
    public int compareTo(@NotNull Coords o) {
        return 10 * this.row - o.row + this.col - o.col;
    }

    @Override
    public String toString() {
        return String.format("%s: [row=%d,col=%d]", getClass().getName(),
                this.row, this.col);
    }
}
