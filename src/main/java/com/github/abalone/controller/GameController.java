package com.github.abalone.controller;

import com.github.abalone.ai.AI;
import com.github.abalone.config.Config;
import com.github.abalone.model.Ball;
import com.github.abalone.model.Board;
import com.github.abalone.model.Game;
import com.github.abalone.util.Color;
import com.github.abalone.util.Coords;
import com.github.abalone.util.Direction;
import com.github.abalone.util.GameState;
import com.github.abalone.view.Window;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author melkir
 */
public class GameController {
    // TODO Désactiver bestMove en pvp, mettre IA dans un tread, terminer la partie quand 6 boules sont sorties
    private static GameController singleton;
    private Window window;
    private Game game;
    private Move currentBestMove;

    private GameController() {
    }

    public static GameController getInstance() {
        if (GameController.singleton == null) {
            GameController.singleton = new GameController();
        }
        return GameController.singleton;
    }

    /** Launch a new game */
    public void launch() {
        Board.getInstance().fill(null);
        this.game = new Game(Color.WHITE, -1, -1);
        AI.init(this.game, (Boolean) Config.get("AI") ? Color.BLACK
                : Color.NONE);
        //TODO Calculer le bestMove dans un thread
//        this.currentBestMove = AI.getInstance().getBestMove(this.game.getTurn());
        //
        this.window.updateBoard(this.game.getTurn());
    }

    /** Save the game */
    public void save() {
        ObjectOutputStream oos = null;
        File file;
        try {
            file = new File("abalone.save");
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(this.game);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != oos) oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Load the saved game */
    public Boolean load() {
        ObjectInputStream ois = null;
        File file;
        try {
            file = new File("abalone.save");
            ois = new ObjectInputStream(new FileInputStream(file));
            Game loadedGame = (Game) ois.readObject();
            this.game = new Game(loadedGame.getTurn(),
                    loadedGame.getTimeLeft(), loadedGame.getTurnsLeft());
            this.game.setHistory(loadedGame.getHistory());
            this.game.setBoard(Board.getInstance());
            this.game.getBoard().fill(loadedGame);
/*            AI.init(this.game, ((Boolean) Config.get("AI")) ? Color.BLACK
                    : Color.NONE);
            this.currentBestMove = AI.getInstance().getBestMove(
                    this.game.getTurn());*/
            this.window.updateBoard(this.game.getTurn());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Boolean.FALSE;
        } finally {
            try {
                if (ois != null) ois.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        this.window.updateBoard(this.game.getTurn());
        return Boolean.TRUE;
    }

    /** Quit the game */
    public void quit() {
        System.exit(0);
    }

    public Set<Direction> validDirections(Set<Coords> selectedBallsCoords) {
        Set<Direction> answer = new HashSet<Direction>();
        Color color = this.game.getTurn();
        Board board = this.game.getBoard();
        Set<Ball> balls = board.getLineColorBallsAt(selectedBallsCoords, color);
        if (balls != null) {
            for (Direction d : Direction.values()) {
                Move move = new Move(balls, d, color);
                move.compute(board);
                if (move.isValid()) {
                    answer.add(d);
                }
            }
        }
        return answer;
    }

    GameState doMove(Move move) {
        Color current = this.game.getTurn();
        if (current == Color.NONE) {
            return GameState.OUTOFTURNS;
        } else if (this.game.over()) {
            System.out.println("Partie terminé");
            return GameState.WON;
        }
        this.game.getBoard().apply(move);
        this.game.addToHistory(move);
        Color next = this.game.getNextTurn();
        //TODO Mettre l' IA dans un thread
/*        AI ai = AI.getInstance();
        Move bestMove = ai.getBestMove(next);
        if (next.equals(ai.getColor())) {
            return this.doMove(bestMove);
        } else {
            this.currentBestMove = bestMove;
        }*/
        //
        this.window.updateBoard(this.game.getTurn());
        return GameState.RUNNING;
    }

    public GameState move(Set<Coords> selectedBallsCoords, Direction direction) {
        Color turn = this.game.getTurn();
        Board board = this.game.getBoard();
        Set<Ball> balls = board.getLineColorBallsAt(selectedBallsCoords, turn);
        if (balls == null)
            throw new RuntimeException("This one should never throw");
        Move move = new Move(balls, direction, turn);
        move.compute(board);
        return doMove(move);
    }

    public Move getCurrentBestMove() {
        return currentBestMove;
    }

    private void doGoBack() {
        int lastIndex = this.game.getHistory().size() - 1;
        if (lastIndex != -1) {
            this.game.getPreviousTurn();
            Move move = this.game.getHistory().get(lastIndex);
            this.game.getHistory().remove(move);
            this.game.getBoard().revert(move);
            this.window.updateBoard(this.game.getTurn());
        }
    }

    public void goBack() {
        if (this.game == null) {
            return;
        }
        doGoBack();
        if (AI.getInstance().getColor() != Color.NONE) {
            doGoBack();
        }
    }

    public void setWindow(Window window) {
        this.window = window;
    }
}
