package ataxx;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * CASE 1. manual red vs manual blue without block
 * ---------------------------------------------------------
 * 1. manual blue
 * 2. manual red
 * 3. new
 * 4. board
 * 5. score
 * 6. board_on
 * 7. g1-f2
 * 8. board_off
 * 9. g7-f7
 * 10. board_on
 * 11. board
 * 12. f2-f3 (clone)
 * 13. a1-c3 (jump)
 * 14. f3-d4 (jump)
 * 15. f7-d5 (jump)
 * 16. c3-c4
 * 17. g7-g6
 * 18. d5-f6
 * result: Red wins!
 * 19. score
 *
 *
 *
 *
 *
 *
 *
 * CASE 2. manual red vs manual blue with block
 * -----------------------------------------------------
 * 1. manual blue
 * 2. manual red
 * 3. new
 * 4. board_on
 * 5. board
 * 6. block c5
 * 7. block d7
 * 8. block a4
 * 9. block b2
 * 10. g1-g2
 * 11. a1-c1
 * 12. a7-c6
 * 13. g7-e6
 * 14. c6-d5
 * 15. c1-d1
 * 16. c1-e2
 * 17. g1-e1
 * result: Red wins!
 * 18. score
 *
 *
 *
 *
 *
 *
 * CASE 3: manual red vs random AI blue
 * -----------------------------------------------------
 * 1. ai blue
 * 2. manual red
 * 3. new
 * 4. board
 * 5. board_on
 * 6. a7-c5
 * 7. g1-e3
 * For other steps, please try by yourself.
 */

/** The main program for Ataxx. */
public class Main {

    /** Run Ataxx getAtaxxGame.  Options (in ARGS0):
     *       --display: Use GUI.
     *  Trailing arguments are input files; the standard input is the
     *  default.
     */
    public static void main(String[] args0) {
        CommandArgs args =
            new CommandArgs("--display{0,1}", args0);

        Game game;
        if (args.contains("--display")) {
            GUI display = new GUI("Ataxx");
            game = new Game(display, display, display);
            display.pack();
            display.setVisible(true);
        } else {
            ArrayList<Reader> inReaders = new ArrayList<>();
            if (args.get("--").isEmpty()) {
                inReaders.add(new InputStreamReader(System.in));
            } else {
                for (String name : args.get("--")) {
                    if (name.equals("-")) {
                        inReaders.add(new InputStreamReader(System.in));
                    } else {
                        try {
                            inReaders.add(new FileReader(name));
                        } catch (IOException excp) {
                            System.err.printf("Could not open %s", name);
                            System.exit(1);
                        }
                    }
                }
            }
            game = new Game(new TextSource(inReaders),
                            (b) -> { }, new TextReporter());
        }
        System.exit(game.play());
    }
}
