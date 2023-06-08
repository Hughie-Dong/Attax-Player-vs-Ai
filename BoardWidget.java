package ataxx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * board widget
 * todo
 */
public class BoardWidget extends JPanel {
    /**
     * Length of side of one square, in pixels.
     */
    static final int SQDIM = 100;
    /**
     * Number of squares on a side.
     */
    static final int SIDE = 7;
    /**
     * Radius of circle representing a piece.
     */
    static final int PIECE_RADIUS = 36;
    /**
     * Dimension of a block.
     */
    static final int BLOCK_WIDTH = 40;

    /**
     * Color of red pieces.
     */
    private static final Color RED_COLOR = Color.RED;
    /**
     * Color of blue pieces.
     */
    private static final Color BLUE_COLOR = Color.BLUE;
    /**
     * Color of painted lines.
     */
    private static final Color LINE_COLOR = Color.BLACK;
    /**
     * Color of blank squares.
     */
    private static final Color BLANK_COLOR = Color.WHITE;
    /**
     * Color of selected squared.
     */
    private static final Color SELECTED_COLOR = new Color(150, 150, 150);
    /**
     * Color of blocks.
     */
    private static final Color BLOCK_COLOR = Color.BLACK;

    /**
     * Stroke for lines.
     */
    private static final BasicStroke LINE_STROKE = new BasicStroke(1.0f);
    /**
     * Stroke for blocks.
     */
    private static final BasicStroke BLOCK_STROKE = new BasicStroke(5.0f);

    /**
     * A new widget sending commands resulting from mouse clicks
     * to COMMANDQUEUE.
     */
    BoardWidget(ArrayBlockingQueue<String> commandQueue) {
        _commandQueue = commandQueue;

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent where) {
                int x = where.getX(), y = where.getY();
                char mouseCol, mouseRow;
                if (where.getButton() == MouseEvent.BUTTON1) {
                    mouseCol = (char) (x / SQDIM + 'a');
                    mouseRow = (char) ((SQDIM * SIDE - y) / SQDIM + '1');
                    if (mouseCol >= 'a' && mouseCol <= 'g'
                            && mouseRow >= '1' && mouseRow <= '7') {
                        if (_blockMode) {
                            _commandQueue.offer(String.format("block %s%s", mouseCol, mouseRow));
                        } else {
                            if (_selectedCol != 0) {
                                //FIXME
                                _commandQueue.offer(String.format("%s%s-%s%s", _selectedCol, _selectedRow, mouseCol, mouseRow));
                                _selectedRow = _selectedCol = 0;
                            } else {
                                _selectedCol = mouseCol;
                                _selectedRow = mouseRow;
                            }
                        }
                    }
                }
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        _dim = SQDIM * SIDE;
        _blockMode = false;
        setPreferredSize(new Dimension(_dim, _dim));
        setMinimumSize(new Dimension(_dim, _dim));
    }

    @Override
    public synchronized void paintComponent(Graphics g) {
        if (_model == null) {
            return;
        }

        g.setColor(BLANK_COLOR);
        g.fillRect(0, 0, _dim, _dim);

        for (int c = 2; c < SIDE + 2; c++) {
            for (int r = 2; r < SIDE + 2; r++) {
                char col0 = (char) (c + 'a' - 2);
                char row0 = (char) (r + '1' - 2);
                int sq = Board.index(col0, row0);
                int cx = (c - 2) * SQDIM;
                int cy = (Math.abs(8 - r)) * SQDIM;
                if (_model.getContent(sq) == PieceState.BLOCKED) {
                    drawBlock(g, cx, cy);
                } else if (_model.getContent(sq) == PieceState.RED) {
                    g.setColor(RED_COLOR);
                    g.fillOval(cx + 7, cy + 7, PIECE_RADIUS * 2, PIECE_RADIUS * 2);
                } else if (_model.getContent(sq) == PieceState.BLUE) {
                    g.setColor(BLUE_COLOR);
                    g.fillOval(cx + 7, cy + 7, PIECE_RADIUS * 2, PIECE_RADIUS * 2);
                }
            }
        }
        g.setColor(BLOCK_COLOR);
        g.drawLine(0, _dim - 1, _dim - 1, _dim - 1);
        g.drawLine(_dim - 1, 0, _dim - 1, _dim - 1);
        for (int i = 0; i < 7; i++) {
            g.drawLine(0, i * SQDIM, _dim, i * SQDIM);
            g.drawLine(i * SQDIM, 0, i * SQDIM, _dim);
        }
    }

    /**
     * Draw a block centered at (CX, CY) on G.
     */
    void drawBlock(Graphics g, int cx, int cy) {
        g.setColor(BLOCK_COLOR);
        g.fillRect(cx, cy, SQDIM, SQDIM);
    }

    public synchronized void update(Board board) {
        _model = new Board(board);
        repaint();
    }

    /** Set block mode on iff ON. */
    void setBlockMode(boolean on) {
        _blockMode = on;
    }

    /**
     * Dimension of current drawing surface in pixels.
     */
    private int _dim;

    /**
     * Model being displayed.
     */
    private static Board _model;

    /**
     * Coordinates of currently selected square, or '\0' if no selection.
     */
    private char _selectedCol, _selectedRow;

    /**
     * True iff in block mode.
     */
    private boolean _blockMode;

    /**
     * Destination for commands derived from mouse clicks.
     */
    private ArrayBlockingQueue<String> _commandQueue;
}
