package com.github.abalone.ai;

import com.github.abalone.config.Config;
import com.github.abalone.controller.Move;
import com.github.abalone.model.Board;
import com.github.abalone.model.Game;
import com.github.abalone.util.Color;

import java.util.Set;

/**
 * @author melkir
 */
public class AI {

    private static final Integer MAX_DEPTH = Integer.valueOf((String) Config.get("max_depth"));
    private static final String ALGO = (String) Config.get("algo");
    private static final Integer INF = 1000;

    private static AI instance;
    private final Game game;
    private final Color selfColor;

    private AI(Game game, Color selfColor) {
        this.game = game;
        this.selfColor = selfColor;
    }

    public static void init(Game game, Color AIColor) {
        if (AI.instance == null)
            AI.instance = new AI(game, AIColor);
    }

    public static AI getInstance() {
        return AI.instance;
    }

    public Move getBestMove(Color current) {
        Move bestMove = null;
        Board board = new Board(this.game.getBoard());
        Integer best = -100;
        Set<Move> moves = board.getPossibleMoves(current);
        for (Move m : moves) {
            board.apply(m);
            Integer score = getScoreByAlgo(board);
            if (score > best) {
                best = score;
                bestMove = m;
            }
            board.revert(m);
        }
        return bestMove;
    }

    Integer getScoreByAlgo(Board board) {
        Integer score;
        if (ALGO.equals("MiniMax")) {
            // (* Initial call for maximizing player *)
            // minimax(origin, depth, TRUE)
            score = minimax(board, MAX_DEPTH - 1, selfColor.other());
        } else if (ALGO.equals("AlphaBeta")) {
            // (* Initial call for maximizing player *)
            // alphabeta(origin, depth, -∞, +∞, TRUE)
            score = alphabeta(board, MAX_DEPTH - 1, -INF, +INF, selfColor.other());
        } else if (ALGO.equals("NegaScout")) {
            // (* Initial call for maximizing player *)
            // negascout(origin, depth, -∞, +∞, TRUE)
            score = negaScout(board, MAX_DEPTH - 1, -INF, +INF, selfColor.other());
        } else {
            score = null;
            System.err.println("Algorithme inconnu");
        }
        return score;
    }

    /*
    function pvs(node, depth, α, β, color)
    if node is a terminal node or depth = 0
        return color × the heuristic value of node
    for each child of node
        if child is not first child
            score := -pvs(child, depth-1, -α-1, -α, -color)       (* search with a null window *)
            if α < score < β                                      (* if it failed high,
                score := -pvs(child, depth-1, -β, -score, -color)        do a full re-search *)
        else
            score := -pvs(child, depth-1, -β, -α, -color)
        α := max(α, score)
        if α ≥ β
            break                                            (* beta cut-off *)
    return α
    */

    private Integer negaScout(Board board, Integer depth, Integer alpha, Integer beta, Color current) {
        if (depth == 0)
            return ((current == selfColor) ? 1 : -1) * this.evaluateBoard(board, current);

        int i = 0;
        for (Move m : board.getPossibleMoves(current)) {
            board.apply(m);
            Integer score;
            if (++i == 1) {
                score = -negaScout(board, depth - 1, -alpha - 1, -alpha, current.other());
                if (alpha < score && score < beta)
                    score = -negaScout(board, depth - 1, -beta, -score, current.other());
            } else {
                score = -negaScout(board, depth - 1, -beta, -alpha, current.other());
            }

            alpha = Math.max(alpha, score);
            if (alpha >= beta)
                return alpha; // cut-off
            board.revert(m);
        }
        return alpha;
    }

    /**
     * Determine si le coup est avantageux ou non
     * @param board Le plateau de jeu
     * @param player Le joueur courant
     * @return Valeur positive si c'est avantageux, négative autrement
     */
    private Integer evaluateBoard(Board board, Color player) {
        return board.ballsCount(player) - board.ballsCount(player.other());
    }

    public Color getColor() {
        return selfColor;
    }

    /*
    function minimax(node, depth, maximizingPlayer)
        if depth = 0 or node is a terminal node
            return the heuristic value of node
        if maximizingPlayer
            bestValue := -∞
            for each child of node
                val := minimax(child, depth - 1, FALSE))
                bestValue := max(bestValue, val);
            return bestValue
        else
            bestValue := +∞
            for each child of node
                val := minimax(child, depth - 1, TRUE))
                bestValue := min(bestValue, val);
            return bestValue
    */

    private Integer minimax(Board board, Integer depth, Color current) {
        Integer bestValue;
        if (0 == depth)
            return ((current == selfColor) ? 1 : -1) * this.evaluateBoard(board, current);

        Integer val;
        if (current != selfColor) { // Maximize player
            bestValue = -INF;
            for (Move m : board.getPossibleMoves(current)) {
                board.apply(m);
                val = minimax(board, depth - 1, selfColor);
                bestValue = Math.max(bestValue, val);
                board.revert(m);
            }
            return bestValue;
        } else { // Minimize IA
            bestValue = INF;
            for (Move m : board.getPossibleMoves(current)) {
                board.apply(m);
                val = minimax(board, depth - 1, selfColor.other());
                bestValue = Math.min(bestValue, val);
                board.revert(m);
            }
            return bestValue;
        }
    }

    /*
    function alphabeta(node, depth, α, β, maximizingPlayer)
        if depth = 0 or node is a terminal node
            return the heuristic value of node
        if maximizingPlayer
            for each child of node
                α := max(α, alphabeta(child, depth - 1, α, β, FALSE))
                if β ≤ α
                    break (* β cut-off *)
            return α
        else
            for each child of node
                β := min(β, alphabeta(child, depth - 1, α, β, TRUE))
                if β ≤ α
                    break (* α cut-off *)
            return β
    */

    private Integer alphabeta(Board board, Integer depth, Integer alpha, Integer beta, Color current) {
        if (0 == depth)
            return ((current == selfColor) ? 1 : -1) * this.evaluateBoard(board, current);
        if (selfColor != current) { // Maximizing player
            for (Move m : board.getPossibleMoves(current)) {
                board.apply(m);
                alpha = Math.max(alpha, alphabeta(board, depth - 1, alpha, beta, selfColor.other()));
                if (beta <= alpha) break; // beta cut-off
                board.revert(m);
            }
            return alpha;
        } else { // Minimize IA
            for (Move m : board.getPossibleMoves(current)) {
                board.apply(m);
                beta = Math.min(beta, alphabeta(board, depth - 1, alpha, beta, selfColor));
                if (beta <= alpha) break; // alpha cut-off
                board.revert(m);
            }
            return beta;
        }
    }
}
