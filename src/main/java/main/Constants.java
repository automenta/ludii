// 
// Decompiled by Procyon v0.5.36
// 

package main;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public final class Constants
{
    public static final String LUDEME_VERSION = "1.0.8";
    public static final String DATE = "18/9/2020";
    public static final String PREFERENCES_VERSION = "1";
    public static final String DEFAULT_GAME_PATH = "/lud/board/war/other/Surakarta.lud";
    public static final int MAX_MOVES = 1000;
    public static final int MAX_SCORE = 1000000000;
    public static final int MIN_SCORE = -1000000000;
    public static final int MAX_PLAYERS = 16;
    public static final int MAX_DIMENSIONS = 8;
    public static final int MAX_PIECE_COUNT = 255;
    public static final int MAX_CONSECUTIVES_TURNS = 16;
    public static final int MAX_PLAYER_SWITCHES = 1000;
    public static final int MAX_PLAYER_TEAM = 16;
    public static final int DEFAULT_PIECE_VALUE = 1;
    public static final int MAX_PHASES = 16;
    public static final int OFF = -1;
    public static final int UNDEFINED = -1;
    public static final int END = -2;
    public static final int Infinity = 1000000000;
    public static final int NegativeInfinity = -1000000000;
    public static int UNUSED;
    public static final int MAX_DISTANCE = 1000;
    public static final int NOBODY = 0;
    public static final int NO_PIECE = 0;
    public static final int DEFAULT_TURN_LIMIT = 1250;
    public static final int DEFAULT_MOVES_LIMIT = 10000;
    public static final int MAX_STACK_HEIGHT = 32;
    public static final int VISIBLE = 0;
    public static final int MASK = 1;
    public static final int INVISIBLE = 2;
    public static final int DEFAULT_NUM_PLAYERS = 2;
    public static final int DEFAULT_ROTATION = 0;
    public static final int DEFAULT_STATE = 0;
    public static final int GROUND_LEVEL = 0;
    public static final int SIZE_HEX_BOARDLESS = 21;
    public static final int SIZE_BOARDLESS = 41;
    public static final int MAX_PITS_DOMINOES = 16;
    public static final boolean USE_UF = false;
    public static final int MAX_ARGS = 20;
    public static final int MIN_IMAGE_SIZE = 2;
    public static List<BitSet>[][] combos;
    public static String FAIL_SAFE_GAME_DESCRIPTION;
    public static String BASIC_GAME_DESCRIPTION;
    
    public static void createCombos() {
        Constants.combos = (List<BitSet>[][])new ArrayList[21][21];
        for (int m = 0; m < 21; ++m) {
            for (int n = 0; n < 21; ++n) {
                Constants.combos[m][n] = new ArrayList<>();
            }
        }
        for (int seed = 0; seed < 1048576; ++seed) {
            int n2;
            for (int on = n2 = Integer.bitCount(seed); n2 < 21; ++n2) {
                if (seed < 1 << n2) {
                    Constants.combos[on][n2].add(BitSet.valueOf(new long[] { seed }));
                }
            }
        }
    }
    
    public static void dumpCombos() {
        for (int m = 0; m < 21; ++m) {
            for (int n = 0; n < 21; ++n) {
                System.out.println("\nm=" + m + ", n=" + n + ":");
                if (!Constants.combos[m][n].isEmpty()) {
                    for (final BitSet bits : Constants.combos[m][n]) {
                        int index = 0;
                        for (int b = 0; b < n; ++b) {
                            System.out.print((bits.get(b) ? Integer.valueOf(++index) : "-") + " ");
                        }
                        System.out.println();
                    }
                }
            }
        }
    }
    
    static {
        Constants.UNUSED = -1;
        Constants.combos = null;
        Constants.FAIL_SAFE_GAME_DESCRIPTION = "(game \"Tic-Tac-Toe\"  \n    (players 2)  \n    (equipment { \n        (board (square 3)) \n        (piece \"Disc\" P1) \n        (piece \"Cross\" P2) \n    })  \n    (rules \n        (play (move Add (to (sites Empty))))\n        (end (if (is Line 3) (result Mover Win)))\n    )\n)";
        Constants.BASIC_GAME_DESCRIPTION = "(game \"Name\"  \n    (players 2)  \n    (equipment { \n        (board (square 3)) \n        (piece \"Ball\" Each) \n    })  \n    (rules \n        (play (move Add (to (sites Empty))))\n        (end (if (no Moves Next) (result Mover Win)))\n    )\n)";
    }
}
