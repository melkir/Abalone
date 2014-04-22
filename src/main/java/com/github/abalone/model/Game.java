package com.github.abalone.model;

import com.github.abalone.controller.Move;
import com.github.abalone.util.Color;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author melkir
 */
public class Game implements Serializable {

    private final Integer timeLeft;
    private Board board;
    private Color turn;
    private Integer turnsLeft;
    private ArrayList<Move> history;

    public Game(Color turn, Integer time, Integer turns) {
        this.board = Board.getInstance();
        this.turn = turn;
        this.timeLeft = time;
        this.turnsLeft = turns;
        this.history = new ArrayList<Move>();
    }

    public ArrayList<Move> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<Move> history) {
        this.history = history;
    }

    public void addToHistory(Move move) {
        this.history.add(move);
    }

    public Integer getTurnsLeft() {
        return turnsLeft;
    }

    public Integer getTimeLeft() {
        return timeLeft;
    }

    public Board getBoard() {
        return this.board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Color getTurn() {
        return this.turn;
    }

    public Color getNextTurn() {
        if (!this.turnsLeft.equals(-1)) {
            --this.turnsLeft;
        }
        if (this.turnsLeft.equals(0)) {
            this.turn = Color.NONE;
        } else {
            this.turn = this.turn.other();
        }
        return this.turn;
    }

    public void getPreviousTurn() {
        if (!this.turnsLeft.equals(-1)) ++this.turnsLeft;
        this.turn = this.turnsLeft.equals(0) ? Color.NONE : this.turn.other();
    }

    public Boolean over() {
        return (this.getBoard().loose(Color.WHITE) || this.getBoard().loose(Color.BLACK));
    }
}
