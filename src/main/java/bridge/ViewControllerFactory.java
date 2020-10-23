// 
// Decompiled by Procyon v0.5.36
// 

package bridge;

import controllers.BaseController;
import controllers.container.BasicController;
import controllers.container.PyramidalController;
import game.equipment.component.Component;
import game.equipment.container.Container;
import metadata.graphics.util.ComponentStyleType;
import metadata.graphics.util.ContainerStyleType;
import metadata.graphics.util.ControllerType;
import util.Context;
import view.component.ComponentStyle;
import view.component.custom.*;
import view.container.ContainerStyle;
import view.container.styles.BoardStyle;
import view.container.styles.HandStyle;
import view.container.styles.board.*;
import view.container.styles.board.graph.GraphStyle;
import view.container.styles.board.graph.PenAndPaperStyle;
import view.container.styles.board.puzzle.*;
import view.container.styles.hand.DeckStyle;
import view.container.styles.hand.DiceStyle;

public class ViewControllerFactory
{
    public static ContainerStyle createStyle(final Container container, final ContainerStyleType type, final Context context) {
        if (type == null) {
            return new BoardStyle(container);
        }
        switch (type) {
            case Board -> {
                return new BoardStyle(container);
            }
            case Hand -> {
                return new HandStyle(container);
            }
            case Deck -> {
                return new DeckStyle(container);
            }
            case Dice -> {
                return new DiceStyle(container);
            }
            case Puzzle -> {
                return new PuzzleStyle(container, context);
            }
            case Sudoku -> {
                return new SudokuStyle(container, context);
            }
            case Kakuro -> {
                return new KakuroStyle(container, context);
            }
            case Futoshiki -> {
                return new FutoshikiStyle(container, context);
            }
            case Hashi -> {
                return new HashiStyle(container, context);
            }
            case Graph -> {
                return new GraphStyle(container, context);
            }
            case PenAndPaper -> {
                return new PenAndPaperStyle(container, context);
            }
            case Agon -> {
                return new AgonStyle(container);
            }
            case Backgammon -> {
                return new BackgammonStyle(container);
            }
            case Boardless -> {
                return new BoardlessStyle(container);
            }
            case Chess -> {
                return new ChessStyle(container, context);
            }
            case ChineseCheckers -> {
                return new ChineseCheckersStyle(container);
            }
            case ConnectiveGoal -> {
                return new ConnectiveGoalStyle(container);
            }
            case Go -> {
                return new GoStyle(container);
            }
            case Goose -> {
                return new GooseStyle(container);
            }
            case HoundsAndJackals -> {
                return new HoundsAndJackalsStyle(container);
            }
            case Janggi -> {
                return new JanggiStyle(container);
            }
            case Lasca -> {
                return new LascaStyle(container);
            }
            case Mancala -> {
                return new MancalaStyle(container);
            }
            case Pachisi -> {
                return new PachisiStyle(container);
            }
            case Ploy -> {
                return new PloyStyle(container);
            }
            case Scripta -> {
                return new ScriptaStyle(container);
            }
            case Shibumi -> {
                return new ShibumiStyle(container);
            }
            case Shogi -> {
                return new ShogiStyle(container);
            }
            case SnakesAndLadders -> {
                return new SnakesAndLaddersStyle(container);
            }
            case Tafl -> {
                return new TaflStyle(container);
            }
            case Xiangqi -> {
                return new XiangqiStyle(container);
            }
            case Connect4 -> {
                return new Connect4Style(container);
            }
            case Spiral -> {
                return new SpiralStyle(container);
            }
            case Surakarta -> {
                return new SurakartaStyle(container);
            }
            case UltimateTicTacToe -> {
                return new UltimateTicTacToeStyle(container);
            }
            case Isometric -> {
                return new IsometricStyle(container);
            }
            case Table -> {
                return new TableStyle(container);
            }
            default -> {
                return new BoardStyle(container);
            }
        }
    }
    
    public static ComponentStyle createStyle(final Component component, final ComponentStyleType type) {
        if (type == null) {
            return new PieceStyle(component);
        }
        switch (type) {
            case Piece -> {
                return new PieceStyle(component);
            }
            case Card -> {
                return new CardStyle(component);
            }
            case Die -> {
                return new DieStyle(component);
            }
            case Domino -> {
                return new DominoStyle(component);
            }
            case Tile -> {
                return new TileStyle(component);
            }
            case LargePiece -> {
                return new LargePieceStyle(component);
            }
            case ExtendedShogi -> {
                return new ExtendedShogiStyle(component);
            }
            case ExtendedXiangqi -> {
                return new ExtendedXiangqiStyle(component);
            }
            case NativeAmericanDice -> {
                return new NativeAmericanDiceStyle(component);
            }
            default -> {
                return new PieceStyle(component);
            }
        }
    }
    
    public static BaseController createController(final Container container, final ControllerType type) {
        if (type == null) {
            return new BasicController(container);
        }
        switch (type) {
            case BasicController -> {
                return new BasicController(container);
            }
            case PyramidalController -> {
                return new PyramidalController(container);
            }
            default -> {
                return new BasicController(container);
            }
        }
    }
}
