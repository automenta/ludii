// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.util;

import metadata.MetadataItem;

public enum ContainerStyleType implements MetadataItem
{
    Board, 
    Hand, 
    Deck, 
    Dice, 
    Boardless, 
    ConnectiveGoal, 
    Mancala, 
    PenAndPaper, 
    Shibumi, 
    Spiral, 
    Isometric, 
    Puzzle, 
    Agon, 
    Backgammon, 
    Chess, 
    ChineseCheckers, 
    Connect4, 
    Goose, 
    Go, 
    Graph, 
    HoundsAndJackals, 
    Janggi, 
    Lasca, 
    Pachisi, 
    Ploy, 
    Scripta, 
    Shogi, 
    SnakesAndLadders, 
    Surakarta, 
    Table, 
    Tafl, 
    Xiangqi, 
    UltimateTicTacToe, 
    Futoshiki, 
    Hashi, 
    Kakuro, 
    Sudoku;
    
    public static ContainerStyleType fromName(final String value) {
        try {
            return valueOf(value);
        }
        catch (Exception e) {
            return ContainerStyleType.Board;
        }
    }
}
