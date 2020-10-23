// 
// Decompiled by Procyon v0.5.36
// 

package manager.utils;

import game.equipment.component.Component;
import game.rules.end.End;
import gnu.trove.map.hash.TObjectIntHashMap;
import options.UserSelections;
import util.Move;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public final class SettingsManager
{
    public static PuzzleSelectionType puzzleDialogOption;
    public static boolean showBoard;
    public static boolean showPieces;
    public static boolean showGraph;
    public static boolean showConnections;
    public static boolean showAxes;
    public static boolean showAIDistribution;
    public static boolean showLastMove;
    public static boolean showEndingMove;
    public static boolean autoMoveFrom;
    public static boolean autoMoveTo;
    public static boolean swapRule;
    public static boolean noRepetition;
    public static boolean noRepetitionWithinTurn;
    public static boolean hideAiMoves;
    public static boolean showAnimation;
    public static boolean moveSoundEffect;
    public static boolean saveHeuristics;
    public static boolean devMode;
    public static boolean agentsPaused;
    public static double tickLength;
    public static boolean canSelectLocalState;
    public static boolean canSelectCount;
    public static boolean canSelectRotation;
    public static End priorSandboxEndRules;
    public static ArrayList<Move> possibleConsequenceMoves;
    public static TObjectIntHashMap<String> turnLimits;
    public static Component dragComponent;
    public static int dragComponentState;
    public static Point oldMousePoint;
    public static JDialog openDialog;
    public static boolean illegalMovesValid;
    public static String lastErrorMessage;
    public static boolean nextFrameIsAnimated;
    public static boolean canSendToDatabase;
    public static final UserSelections userSelections;
    public static boolean editorAutocomplete;
    
    public static int getTurnLimit(final String gameName) {
        if (SettingsManager.turnLimits.contains(gameName)) {
            return SettingsManager.turnLimits.get(gameName);
        }
        return 1250;
    }
    
    public static void setTurnLimit(final String gameName, final int turnLimit) {
        SettingsManager.turnLimits.put(gameName, turnLimit);
    }
    
    static {
        SettingsManager.puzzleDialogOption = PuzzleSelectionType.Automatic;
        SettingsManager.showBoard = true;
        SettingsManager.showPieces = true;
        SettingsManager.showGraph = false;
        SettingsManager.showConnections = false;
        SettingsManager.showAxes = false;
        SettingsManager.showAIDistribution = false;
        SettingsManager.showLastMove = false;
        SettingsManager.showEndingMove = true;
        SettingsManager.autoMoveFrom = true;
        SettingsManager.autoMoveTo = false;
        SettingsManager.swapRule = false;
        SettingsManager.noRepetition = false;
        SettingsManager.noRepetitionWithinTurn = false;
        SettingsManager.hideAiMoves = true;
        SettingsManager.showAnimation = false;
        SettingsManager.moveSoundEffect = false;
        SettingsManager.saveHeuristics = false;
        SettingsManager.devMode = false;
        SettingsManager.agentsPaused = true;
        SettingsManager.tickLength = 0.1;
        SettingsManager.canSelectLocalState = false;
        SettingsManager.canSelectCount = false;
        SettingsManager.canSelectRotation = false;
        SettingsManager.possibleConsequenceMoves = new ArrayList<>();
        SettingsManager.turnLimits = new TObjectIntHashMap<>();
        SettingsManager.dragComponent = null;
        SettingsManager.dragComponentState = 1;
        SettingsManager.oldMousePoint = new Point(0, 0);
        SettingsManager.openDialog = null;
        SettingsManager.illegalMovesValid = false;
        SettingsManager.lastErrorMessage = "";
        SettingsManager.nextFrameIsAnimated = false;
        SettingsManager.canSendToDatabase = true;
        userSelections = new UserSelections(new ArrayList<>());
        SettingsManager.editorAutocomplete = true;
    }
}
