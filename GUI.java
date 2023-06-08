package ataxx;// Optional Task: The GUI for the Ataxx Game

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * GUI
 */
class GUI extends JFrame implements View, CommandSource, Reporter {
    /**
     * Contains the drawing logic for the Ataxx model.
     */
    private BoardWidget _widget;
    /**
     * The model of the game.
     */
    private Board _board;

    /**
     * top
     */
    private JPanel top;

    /**
     * bottom
     */
    private JPanel bottom;

    /**
     * new button
     */
    private Button newButton;

    /**
     * block button
     */
    private JRadioButton blockButton;

    /**
     * move button
     */
    private JRadioButton moveButton;

    /**
     * quit button
     */
    private Button quitButton;

    /**
     * red ai
     */
    private JRadioButton redAiButton;

    /**
     * red manual
     */
    private JRadioButton redManualButton;

    /**
     * blue ai
     */
    private JRadioButton blueAiButton;

    /**
     * blue manual
     */
    private JRadioButton blueManualButton;

    /**
     * stateLabel
     */
    private JLabel stateLabel;

    /**
     * passButton
     */
    private Button passButton;

    /**
     * Queue for commands going to the controlling Game.
     */
    private final ArrayBlockingQueue<String> _commandQueue =
            new ArrayBlockingQueue<>(5);

    // Complete the codes here
    GUI(String ataxx) {
        super(ataxx);
        addTop();
        addWidget();
        addBottom();
    }

    private void addBottom() {
        bottom = new JPanel();
        add(bottom, BorderLayout.SOUTH);
        addStateLabel();
        addPassButton();
    }

    private void addTop() {
        top = new JPanel();
        add(top, BorderLayout.NORTH);
        addMenuButton();
        addBlockAndMoveButton();
        addQuitButton();
        addAIAndManualSelect();
    }

    private void addStateLabel() {
        stateLabel = new JLabel("Red to move");

        bottom.add(stateLabel);
    }

    private void addPassButton() {
        passButton = new Button("Pass");

        passButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (_board.moveLegal(Move.pass())) {
                    send("-");
                }
            }
        });
        bottom.add(passButton);
    }


    private void addAIAndManualSelect() {
        ButtonGroup redGroup = new ButtonGroup();
        ButtonGroup blueGroup = new ButtonGroup();


        redAiButton = new JRadioButton("Red AI");
        redManualButton = new JRadioButton("Red Manual");
        blueAiButton = new JRadioButton("Blue AI");
        blueManualButton = new JRadioButton("Blue Manual");

        redAiButton.setSelected(false);
        blueAiButton.setSelected(true);

        ActionListener redListener = e -> {
            if (redAiButton.isSelected()) {
                send("ai red");
            } else {
                send("manual red");
            }
        };
        ActionListener blueListener = e -> {
            if (blueAiButton.isSelected()) {
                send("ai blue");
            } else {
                send("manual blue");
            }
        };
        redAiButton.addActionListener(redListener);
        redManualButton.addActionListener(redListener);

        blueAiButton.addActionListener(blueListener);
        blueManualButton.addActionListener(blueListener);


        redGroup.add(redAiButton);
        redGroup.add(redManualButton);

        blueGroup.add(blueAiButton);
        blueGroup.add(blueManualButton);

        top.add(redAiButton);
        top.add(redManualButton);
        top.add(blueAiButton);
        top.add(blueManualButton);
    }

    private void addQuitButton() {
        quitButton = new Button("Quit");
        quitButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send("quit");
            }
        });
        top.add(quitButton);
    }

    private void addMenuButton() {
        newButton = new Button("New Game");
        newButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send("new");
                _widget.setBlockMode(false);
            }
        });
        top.add(newButton);
    }

    private void addWidget() {
        _widget = new BoardWidget(_commandQueue);
        add(_widget, BorderLayout.CENTER);
    }

    private void addBlockAndMoveButton() {
        ButtonGroup group = new ButtonGroup();

        blockButton = new JRadioButton("Block Mode");
        moveButton = new JRadioButton("Move Mode");

        moveButton.setSelected(true);

        ActionListener listener = e -> {
            if (blockButton.isSelected()) {
                _widget.setBlockMode(true);
            } else {
                _widget.setBlockMode(false);
            }
        };

        blockButton.addActionListener(listener);
        moveButton.addActionListener(listener);

        group.add(blockButton);
        group.add(moveButton);

        top.add(blockButton);
        top.add(moveButton);
    }


    /**
     * Add the command described by FORMAT, ARGS (as for String.format) to
     * the queue of waiting commands returned by getCommand.
     */
    private void send(String format, Object... args) {
        _commandQueue.offer(String.format(format, args));
    }

    // These methods could be modified
    @Override
    public void update(Board board) {
        if (board == _board) {
            updateLabel();
        }
        _board = board;
        _widget.update(board);
    }


    /**
     * Set label indicating board state.
     */
    private void updateLabel() {
        String label;
        int red = _board.getColorNums(PieceState.RED);
        int blue = _board.getColorNums(PieceState.BLUE);
        if (_board.getWinner() != null) {
            if (red > blue) {
                label = String.format("Red wins (%d-%d)", red, blue);
            } else if (red < blue) {
                label = String.format("Blue wins (%d-%d)", red, blue);
            } else {
                label = "Drawn game";
            }
        } else {
            label = String.format("%s to move", _board.nextMove());
        }
        stateLabel.setText(label);
    }

    @Override
    public String getCommand(String prompt) {
        try {
            return _commandQueue.take();
        } catch (InterruptedException excp) {
            throw new Error("unexpected interrupt");
        }
    }

    @Override
    public void announceWinner(PieceState state) {

    }

    @Override
    public void announceMove(Move move, PieceState player) {

    }

    @Override
    public void message(String format, Object... args) {

    }

    @Override
    public void error(String format, Object... args) {

    }

    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    public void pack() {
        super.pack();
    }
}
