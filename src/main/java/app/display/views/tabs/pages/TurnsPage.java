// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tabs.pages;

import app.display.views.tabs.TabPage;
import app.display.views.tabs.TabView;
import game.types.play.ModeType;
import manager.Manager;
import manager.utils.TrialUtil;
import util.Context;
import util.Move;
import util.Trial;
import util.action.Action;

import java.awt.*;

public class TurnsPage extends TabPage
{
    public static int turnNumber;
    public static int lastMover;
    
    public TurnsPage(final Rectangle rect, final String title, final String text, final int pageIndex, final TabView parent) {
        super(rect, title, text, pageIndex, parent);
    }
    
    @Override
    public void updatePage(final Context context) {
        TurnsPage.lastMover = -100;
        TurnsPage.turnNumber = 0;
        this.clear();
        final int trialStartPoint = TrialUtil.getInstanceStartIndex(context);
        final int trialEndPoint = TrialUtil.getInstanceEndIndex(context);
        for (int i = trialStartPoint; i < context.trial().numMoves(); ++i) {
            this.addText(getTurnStringToDisplay(context, i));
        }
        if (Manager.savedTrial() != null) {
            for (int i = context.trial().numMoves(); i < trialEndPoint; ++i) {
                this.addFadedText(getTurnStringToDisplay(context, i));
            }
        }
    }
    
    private static String getTurnStringToDisplay(final Context context, final int moveNumber) {
        Trial longestTrial = context.trial();
        if (Manager.savedTrial() != null) {
            longestTrial = Manager.savedTrial();
        }
        final Move lastMove = longestTrial.moves().get(moveNumber);
        String stringMove = ". ";
        if (context.game().mode().mode() == ModeType.Simultaneous) {
            for (final Action action : lastMove.actions()) {
                if (action.isDecision()) {
                    stringMove = stringMove + action.toTurnFormat(context.currentInstanceContext()) + ", ";
                }
            }
            if (stringMove.length() > 0) {
                stringMove = stringMove.substring(0, stringMove.length() - 2);
            }
        }
        else if (context.game().mode().mode() == ModeType.Simulation) {
            for (final Action action : lastMove.actions()) {
                stringMove = stringMove + action.toTurnFormat(context.currentInstanceContext()) + ", ";
            }
            if (stringMove.length() > 0) {
                stringMove = stringMove.substring(0, stringMove.length() - 2);
            }
        }
        else {
            for (final Action action : lastMove.actions()) {
                if (action.isDecision()) {
                    stringMove = action.toTurnFormat(context.currentInstanceContext());
                    break;
                }
            }
        }
        String textToAdd = "";
        if (lastMove.mover() != TurnsPage.lastMover) {
            ++TurnsPage.turnNumber;
            if (TurnsPage.turnNumber != 1) {
                textToAdd += "\n";
            }
            textToAdd = textToAdd + "Turn " + TurnsPage.turnNumber + ". " + stringMove;
        }
        else {
            textToAdd = textToAdd + ", " + stringMove;
        }
        TurnsPage.lastMover = lastMove.mover();
        return textToAdd;
    }
    
    @Override
    public void reset() {
        this.clear();
    }
    
    static {
        TurnsPage.turnNumber = 0;
        TurnsPage.lastMover = -100;
    }
}
