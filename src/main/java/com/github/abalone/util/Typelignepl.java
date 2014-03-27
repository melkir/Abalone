package com.github.abalone.util;

import java.util.ArrayList;

/**
 * @author melkir
 */
public enum Typelignepl {

    DIAGONAL1, // => "/"
    DIAGONAL2, // => "\"
    HORIZONTAL, // =>"--"
    NONADJACENT;

    public static ArrayList<Direction> lesDirectionPerpendiculaire(Typelignepl t) {
        ArrayList<Direction> directions = new ArrayList<Direction>();
        switch (t) {
            case DIAGONAL1:
                directions.add(Direction.LEFT);
                directions.add(Direction.RIGHT);
                directions.add(Direction.DOWNRIGHT);
                directions.add(Direction.UPLEFT);
                break;
            case DIAGONAL2:
                directions.add(Direction.DOWNLEFT);
                directions.add(Direction.LEFT);
                directions.add(Direction.RIGHT);
                directions.add(Direction.UPRIGHT);
                break;
            case HORIZONTAL:
                directions.add(Direction.UPLEFT);
                directions.add(Direction.UPRIGHT);
                directions.add(Direction.DOWNRIGHT);
                directions.add(Direction.DOWNLEFT);
                break;
        }
        return directions;

    }
}
