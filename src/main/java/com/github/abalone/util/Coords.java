package com.github.abalone.util;

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
        if (obj == null || getClass() != obj.getClass()) return false;
        final Coords other = (Coords) obj;
        return !(!this.row.equals(other.row) && (this.row == null || !this.row.equals(other.row))) && !(!this.col.equals(other.col) && (this.col == null || !this.col.equals(other.col)));
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
                if (--destination.row < 0) --destination.col;
                break;
            case UPRIGHT:
                if (--destination.row > -1) ++destination.col;
                break;
            case LEFT:
                --destination.col;
                break;
            case RIGHT:
                ++destination.col;
                break;
            case DOWNLEFT:
                if (++destination.row > 0) --destination.col;
                break;
            case DOWNRIGHT:
                if (++destination.row < 1) ++destination.col;
                break;
        }
        return destination;
    }

    public Typelignepl LignePl(Coords c) {
        return moveTo(Direction.UPLEFT).equals(c)
                || c.equals(moveTo(Direction.DOWNRIGHT)) ? Typelignepl.DIAGONAL2 : moveTo(Direction.UPRIGHT).equals(c)
                || c.equals(moveTo(Direction.DOWNLEFT)) ? Typelignepl.DIAGONAL1 : moveTo(Direction.RIGHT).equals(c)
                || c.equals(moveTo(Direction.LEFT)) ? Typelignepl.HORIZONTAL : Typelignepl.NONADJACENT;
    }

    @Override
    public int compareTo(Coords o) {
        return 10 * this.row - o.row + this.col - o.col;
    }

    @Override
    public String toString() {
        return String.format("%s: [row=%d,col=%d]", getClass().getName(), this.row, this.col);
    }
}
