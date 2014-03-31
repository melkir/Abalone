package com.github.abalone.ai;

import com.github.abalone.controller.Move;
import com.github.abalone.model.Board;
import com.github.abalone.model.Game;
import com.github.abalone.util.Color;

import java.util.Set;

/**
 * @author melkir
 */
public class AI {

    private static final Integer MAX_DEPTH = 3;
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
        Board board = new Board(this.game.getBoard());
        Move bestMove = null;
        Integer best = -100;
        Set<Move> moves = board.getPossibleMoves(current);
        for (Move m : moves) {
            board.apply(m);
            Integer score = negaScout(board, current.other(), MAX_DEPTH - 1, -INF, +INF);
            if (score > best) {
                best = score;
                bestMove = m;
            }
            board.revert(m);
        }
        return bestMove;
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

    private Integer negaScout(Board board, Color current, Integer depth,
                              Integer alpha, Integer beta) {
        if (depth > 0) {
            Integer best = beta;
            for (Move m : board.getPossibleMoves(current)) {
                board.apply(m);
                Integer score = -negaScout(board, current.other(), depth - 1, -best, -alpha);
                if (alpha < score && score < beta)
                    score = -negaScout(board, current.other(), depth - 1, -beta, -alpha);
                alpha = Math.max(alpha, score);
                if (alpha >= beta) return alpha; // cut-off
                best = alpha + 1;
                board.revert(m);
            }
            return best;
        } else {
            return ((current == selfColor) ? 1 : -1) * this.evaluateBoard(board, current);
        }
    }

    private Integer evaluateBoard(Board board, Color player) {
        Integer good = board.ballsCount(player);
        Integer bad = board.ballsCount(player.other());
        return good - bad;
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

    (* Initial call for maximizing player *)
    minimax(origin, depth, TRUE)
     */

    private Integer minimax (Board board, Integer depth, Color current, Boolean maximizingPlayer) {
        Integer bestValue = null;
        if (0 == depth || null == board.getPossibleMoves(current))
            return this.evaluateBoard(board, current);
        Integer val;
        if (maximizingPlayer) {
            bestValue = -INF;
            for (Move m : board.getPossibleMoves(current)) {
                val = minimax(board, depth - 1, current, Boolean.FALSE);
                bestValue = Math.max(bestValue, val);
            }
            return bestValue;
        } else {
            bestValue = INF;
            for (Move m : board.getPossibleMoves(current)) {
                val = minimax(board, depth - 1, current, Boolean.TRUE);
                bestValue = Math.min(bestValue, val);
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
    (* Initial call *)
    alphabeta(origin, depth, -∞, +∞, TRUE)
     */

    private Integer alphabeta (Board board, Integer depth, Integer alpha, Integer beta, Color current, Boolean maximizingPlayer) {
        if (0 == depth || null == board.getPossibleMoves(current))
            return this.evaluateBoard(board, current);
        if (maximizingPlayer) {
            for (Move m : board.getPossibleMoves(current)) {
                alpha = Math.max(alpha, alphabeta(board, depth -1,  alpha, beta, current, Boolean.FALSE));
                if (beta <= alpha) break; // beta cut-off
            }
            return alpha;
        } else {
            for (Move m : board.getPossibleMoves(current)) {
                beta = Math.min(beta, alphabeta(board, depth -1,  alpha, beta, current, Boolean.TRUE));
                if (beta <= alpha) break; // alpha cut-off
            }
            return beta;
        }
    }
}
