package ataxx;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

// Final Project Part A.2 Ataxx AI Player (A group project)

/**
 * A Player that computes its own moves.
 */
class AIPlayer extends Player {
    /**
     * winner value
     */
    private static final int WINNER_VALUE = Integer.MAX_VALUE - 20;

    /**
     * INFTY
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * search max depth
     */
    private static final int MAX_DEPTH = 5;

    /**
     * Pseudo random generator for calculating the next move
     */
    private Random random;

    /**
     * A new AIPlayer for GAME that will play MYCOLOR.
     * SEED is used to initialize a random-number generator,
     * increase the value of SEED would make the AIPlayer move automatically.
     * Identical seeds produce identical behaviour.
     */
    AIPlayer(Game game, PieceState myColor, long seed) {
        super(game, myColor);
        // todo 加入随机数种子
        random = new Random(seed);
    }

    @Override
    boolean isAuto() {
        return true;
    }

    @Override
    String getAtaxxMove() {
        Move move = findMove();
        getAtaxxGame().reportMove(move, getMyState());
        return move.toString();
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = new Board(getAtaxxBoard());
        lastFoundMove = null;

        // Here we just have the simple AI to randomly move.
        // However, it does not meet with the requirements of Part A.2.
        // Therefore, the following codes should be modified
        // in order to meet with the requirements of Part A.2.
        // You can create add your own method and put your method here.
        ArrayList<Move> bestMoves = new ArrayList<>();
        // todo
        //  使用minimax博弈算法，得出最优下一步。
        //  MAX_DEPTH 值越大则越精准，但同时耗时越高。
        //  算法时间复杂度为O(b^m),其中b是游戏树的分支因子,m是树的最大深度，
        //  有兴趣的话可以找一下minimax 和 alpha剪枝的论文。
        if (getMyState() == PieceState.RED) {
            minMax(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            minMax(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }

        // Please do not change the codes below
        if (lastFoundMove == null) {
            lastFoundMove = Move.pass();
        }
        return lastFoundMove;
    }

    private int minMax(Board board, int depth, boolean saveMove, int sense,
                       int alpha, int beta) {
        /* We use WINNING_VALUE + depth as the winning value so as to favor
         * wins that happen sooner rather than later (depth is larger the
         * fewer moves have been made. */
        if (depth == 0 || board.getWinner() != null) {
            return getScore(board, WINNER_VALUE + depth);
        }
        Move best;
        best = null;
        int bestScore = 0;
        ArrayList<Move> allPossibleMoves = new ArrayList<>();
        if (sense == 1) {
            if (board.moveLegal(Move.pass())) {
                allPossibleMoves.add(Move.pass());
            } else {
                bestScore = -INFTY;
                allPossibleMoves = possibleMoves(board, PieceState.RED);
                for (Move possible : allPossibleMoves) {
                    Board copy = new Board(board);
                    copy.createMove(possible);
                    int response = minMax(copy, depth - 1, false,
                            -1, alpha, beta);
                    if (response > bestScore) {
                        best = possible;
                        bestScore = response;
                        alpha = max(alpha, bestScore);
                        if (alpha >= beta) {
                            break;
                        }
                    }
                }
            }
        } else if (sense == -1) {
            if (board.moveLegal(Move.pass())) {
                allPossibleMoves.add(Move.pass());
            } else {
                bestScore = INFTY;
                allPossibleMoves = possibleMoves(board, PieceState.BLUE);
                for (Move possible : allPossibleMoves) {
                    Board copy = new Board(board);
                    copy.createMove(possible);
                    int response = minMax(copy, depth - 1, false,
                            1, alpha, beta);
                    if (response < bestScore) {
                        bestScore = response;
                        best = possible;
                        beta = min(beta, bestScore);
                        if (alpha >= beta) {
                            break;
                        }
                    }
                }
            }
        }
        if (saveMove) {
            lastFoundMove = best;
        }
        return bestScore;
    }

    /**
     * Return a heuristic value for BOARD.  This value is +- WINNINGVALUE in
     * won positions, and 0 for ties.
     */
    private int getScore(Board board, int winnerValue) {
        PieceState winner = board.getWinner();
        if (winner != null) {
            return switch (winner) {
                case RED -> winnerValue;
                case BLUE -> -winnerValue;
                default -> 0;
            };
        }
        if (board.nextMove() == PieceState.RED) {
            return board.getColorNums(PieceState.RED) - board.getColorNums(PieceState.BLUE);
        } else {
            return board.getColorNums(PieceState.BLUE) - board.getColorNums(PieceState.RED);
        }
    }


    /**
     * The move found by the last call to the findMove method above.
     */
    private Move lastFoundMove;


    /**
     * Return all possible moves for a color.
     *
     * @param board   the current board.
     * @param myColor the specified color.
     * @return an ArrayList of all possible moves for the specified color.
     */
    private ArrayList<Move> possibleMoves(Board board, PieceState myColor) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        for (char row = '7'; row >= '1'; row--) {
            for (char col = 'a'; col <= 'g'; col++) {
                int index = Board.index(col, row);
                if (board.getContent(index) == myColor) {
                    ArrayList<Move> addMoves
                            = assistPossibleMoves(board, row, col);
                    possibleMoves.addAll(addMoves);
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Returns an Arraylist of legal moves.
     *
     * @param board the board for testing
     * @param row   the row coordinate of the center
     * @param col   the col coordinate of the center
     */
    private ArrayList<Move>
    assistPossibleMoves(Board board, char row, char col) {
        ArrayList<Move> assistPossibleMoves = new ArrayList<>();
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (i != 0 || j != 0) {
                    char row2 = (char) (row + j);
                    char col2 = (char) (col + i);
                    Move currMove = Move.move(col, row, col2, row2);
                    if (board.moveLegal(currMove)) {
                        assistPossibleMoves.add(currMove);
                    }
                }
            }
        }
        return assistPossibleMoves;
    }
}
