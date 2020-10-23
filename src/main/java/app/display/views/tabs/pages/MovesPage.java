// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tabs.pages;

import app.display.views.tabs.TabPage;
import app.display.views.tabs.TabView;
import app.utils.SettingsDesktop;
import bridge.Bridge;
import game.types.play.ModeType;
import manager.Manager;
import manager.utils.TrialUtil;
import util.ContainerUtil;
import util.Context;
import util.Move;
import util.Trial;
import util.action.Action;
import util.locations.Location;
import util.state.State;
import util.state.containerState.ContainerState;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MovesPage extends TabPage
{
    public MovesPage(final Rectangle rect, final String title, final String text, final int pageIndex, final TabView parent) {
        super(rect, title, text, pageIndex, parent);
    }
    
    @Override
    public void updatePage(final Context context) {
        this.clear();
        final int trialStartPoint = TrialUtil.getInstanceStartIndex(context);
        final int trialEndPoint = TrialUtil.getInstanceEndIndex(context);
        for (int i = trialStartPoint; i < context.trial().numMoves(); ++i) {
            this.addText(getMoveStringToDisplay(context, i));
        }
        if (Manager.savedTrial() != null) {
            for (int i = context.trial().numMoves(); i < trialEndPoint; ++i) {
                this.addFadedText(getMoveStringToDisplay(context, i));
            }
        }
    }
    
    private static String getMoveStringToDisplay(final Context context, final int moveNumber) {
        Trial longestTrial = context.trial();
        if (Manager.savedTrial() != null) {
            longestTrial = Manager.savedTrial();
        }
        final Move lastMove = longestTrial.moves().get(moveNumber);
        final String settingMoveFormat = SettingsDesktop.moveFormat;
        if (settingMoveFormat == null || settingMoveFormat.equals("Move")) {
            if (context.game().mode().mode() == ModeType.Simultaneous) {
                String moveToPrint = "";
                for (final Action action : lastMove.actions()) {
                    if (action.isDecision()) {
                        moveToPrint = moveToPrint + action.toMoveFormat(context.currentInstanceContext()) + ", ";
                    }
                }
                if (!moveToPrint.isEmpty()) {
                    final int moveNumberToPrint = moveNumber - TrialUtil.getInstanceStartIndex(context) + 1;
                    return moveNumberToPrint + ". " + moveToPrint.substring(0, moveToPrint.length() - 2) + "\n";
                }
                return ".\n";
            }
            else {
                if (context.game().mode().mode() != ModeType.Simulation) {
                    for (final Action action2 : lastMove.actions()) {
                        if (action2.isDecision()) {
                            final int moveNumberToPrint2 = moveNumber - TrialUtil.getInstanceStartIndex(context) + 1;
                            return moveNumberToPrint2 + ". " + action2.toMoveFormat(context.currentInstanceContext()) + "\n";
                        }
                    }
                    return ".\n";
                }
                String moveToPrint = "";
                for (final Action action : lastMove.actions()) {
                    moveToPrint = moveToPrint + action.toMoveFormat(context.currentInstanceContext()) + ", ";
                }
                if (!moveToPrint.isEmpty()) {
                    final int moveNumberToPrint = moveNumber - TrialUtil.getInstanceStartIndex(context) + 1;
                    return moveNumberToPrint + ". " + moveToPrint.substring(0, moveToPrint.length() - 2) + "\n";
                }
                return ".\n";
            }
        }
        else if (settingMoveFormat.equals("Short")) {
            if (context.game().mode().mode() == ModeType.Simultaneous) {
                String moveToPrint = "";
                for (final Action action : lastMove.actions()) {
                    if (action.isDecision()) {
                        moveToPrint = moveToPrint + action.toTurnFormat(context.currentInstanceContext()) + ", ";
                    }
                }
                if (!moveToPrint.isEmpty()) {
                    final int moveNumberToPrint = moveNumber - TrialUtil.getInstanceStartIndex(context) + 1;
                    return moveNumberToPrint + ". " + moveToPrint.substring(0, moveToPrint.length() - 2) + "\n";
                }
                return ".\n";
            }
            else {
                if (context.game().mode().mode() != ModeType.Simulation) {
                    for (final Action action2 : lastMove.actions()) {
                        if (action2.isDecision()) {
                            final int moveNumberToPrint2 = moveNumber - TrialUtil.getInstanceStartIndex(context) + 1;
                            return moveNumberToPrint2 + ". " + action2.toTurnFormat(context.currentInstanceContext()) + "\n";
                        }
                    }
                    return ".\n";
                }
                String moveToPrint = "";
                for (final Action action : lastMove.actions()) {
                    moveToPrint = moveToPrint + action.toTurnFormat(context.currentInstanceContext()) + ", ";
                }
                if (!moveToPrint.isEmpty()) {
                    final int moveNumberToPrint = moveNumber - TrialUtil.getInstanceStartIndex(context) + 1;
                    return moveNumberToPrint + ". " + moveToPrint.substring(0, moveToPrint.length() - 2) + "\n";
                }
                return ".\n";
            }
        }
        else {
            final List<Action> actionsToPrint = new ArrayList<>(lastMove.actions());
            final StringBuilder completeActionLastMove = new StringBuilder();
            for (final Action a : actionsToPrint) {
                if (!completeActionLastMove.isEmpty()) {
                    completeActionLastMove.append(", ");
                }
                completeActionLastMove.append(a.toMoveFormat(context.currentInstanceContext()));
            }
            if (actionsToPrint.size() > 1) {
                completeActionLastMove.insert(0, '[');
                completeActionLastMove.append(']');
            }
            final int moverToPrint;
            final int mover = moverToPrint = lastMove.mover();
            final int playerMoverId = Bridge.graphicsRenderer().getSingleHumanMover(moverToPrint, context);
            boolean keepSecret = false;
            if (longestTrial.lastMove() != null && playerMoverId != moverToPrint && !longestTrial.lastMove().isPass() && !longestTrial.lastMove().isSwap()) {
                final State state = context.state();
                final Location locationFrom = lastMove.getFromLocation();
                final int containerIdFrom = ContainerUtil.getContainerId(context, locationFrom.site(), locationFrom.siteType());
                final Location locationTo = lastMove.getToLocation();
                final int containerIdTo = ContainerUtil.getContainerId(context, locationTo.site(), locationTo.siteType());
                if (containerIdFrom != -1 && containerIdTo != -1) {
                    ContainerState cs = state.containerStates()[containerIdFrom];
                    if (cs.isInvisible(locationFrom.site(), locationFrom.level(), playerMoverId, locationFrom.siteType())) {
                        keepSecret = true;
                    }
                    if (cs.isMasked(locationFrom.site(), locationFrom.level(), playerMoverId, locationFrom.siteType())) {
                        keepSecret = true;
                    }
                    cs = state.containerStates()[containerIdTo];
                    if (cs.isInvisible(locationTo.site(), locationTo.level(), playerMoverId, locationTo.siteType())) {
                        keepSecret = true;
                    }
                    if (cs.isMasked(locationTo.site(), locationTo.level(), playerMoverId, locationTo.siteType())) {
                        keepSecret = true;
                    }
                }
            }
            if (keepSecret) {
                return "";
            }
            final int moveNumberToPrint3 = moveNumber - TrialUtil.getInstanceStartIndex(context) + 1;
            if (moverToPrint > 0) {
                return moveNumberToPrint3 + ". (" + moverToPrint + ") " + completeActionLastMove + "\n";
            }
            return moveNumberToPrint3 + ". " + completeActionLastMove + "\n";
        }
    }
    
    @Override
    public void reset() {
        this.clear();
    }
}
