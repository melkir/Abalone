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

import javax.swing.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author melkir
 */
public class GameController {
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

    /**
     * Launch a new game
     */
    public void launch() {
        Board.getInstance().fill(null);
        this.game = new Game(Color.WHITE, -1, -1);
        AI.init(this.game, (Boolean) Config.get("AI") ? Color.BLACK : Color.NONE);
        //TODO Calculer le bestMove dans un thread
//        this.currentBestMove = AI.getInstance().getBestMove(this.game.getTurn());
        this.window.updateBoard();
    }

    /**
     * Save the game
     */
    public void save() {
        ObjectOutputStream oos = null;
        File file;
        try {
            file = new File("abalone.save");
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(this.game);
            System.out.println("Partie sauvegardé");
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

    /**
     * Load the saved game
     */
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
            AI.init(this.game, ((Boolean) Config.get("AI")) ? Color.BLACK
                    : Color.NONE);
            this.currentBestMove = AI.getInstance().getBestMove(
                    this.game.getTurn());
            this.window.updateBoard();
            System.out.println("Partie restauré");
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
        this.window.updateBoard();
        return Boolean.TRUE;
    }

    /**
     * Quit the game
     */
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
            this.window.setStatusText("Partie terminé");
            return GameState.WON;
        }
        // On applique le mouvement
        this.game.getBoard().apply(move);
        this.game.addToHistory(move);
        this.window.updateBoard();
        // On passe au tour suivant
        Color next = this.game.getNextTurn();
        // Si le prochain a jouer est l'IA
        if (next == AI.getInstance().getColor()) {
            // On applique le mouvement de l'IA
            doIAMove();
            return GameState.RUNNING;
        }
        if (current != AI.getInstance().getColor()) this.window.reverseBoard();
        this.window.setStatusText("C'est au tour des billes " + next.toString());
        return GameState.RUNNING;
    }

    private void doIAMove() {
        final AI ai = AI.getInstance();
        final Color current = this.game.getTurn();
        this.window.setStatusText("IA : Calcul du meilleur mouvement");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                doMove(ai.getBestMove(current));
            }
        });
    }

    public GameState move(Set<Coords> selectedBallsCoords, Direction direction) {
        Color turn = this.game.getTurn();
        Board board = this.game.getBoard();
        Set<Ball> balls = board.getLineColorBallsAt(selectedBallsCoords, turn);
        assert balls != null : "This one should never throw";
        Move move = new Move(balls, direction, turn);
        move.compute(board);
        doMove(move);
        return GameState.RUNNING;
    }

    public Move getCurrentBestMove() {
        return currentBestMove;
    }

    private void doGoBack() {
        int lastIndex = this.game.getHistory().size() - 1;
        if (lastIndex == -1) return;
        this.game.getPreviousTurn();
        Move move = this.game.getHistory().get(lastIndex);
        this.game.getHistory().remove(move);
        this.game.getBoard().revert(move);
        this.window.updateBoard();
    }

    public void goBack() {
        if (this.game == null) return;
        doGoBack();
        if (AI.getInstance().getColor() != Color.NONE) doGoBack();
    }

    public void setWindow(Window window) {
        this.window = window;
    }
}
