// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views;

import app.DesktopApp;
import bridge.Bridge;
import manager.Manager;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import util.Context;
import util.PlaneType;
import util.SettingsVC;

import java.awt.*;

public final class BoardView extends View
{
    private final double boardToSizeRatio = 1.0;
    
    public BoardView() {
        final int boardSize = Math.min(DesktopApp.view().getHeight(), (int)(DesktopApp.view().getWidth() * 1.0));
        DesktopApp.view().setResolution(boardSize);
        this.placement = new Rectangle(0, 0, boardSize, boardSize);
    }
    
    @Override
    public void paint(final Graphics2D g2d) {
        final Context context = ContextSnapshot.getContext();
        Bridge.getContainerStyle(context.board().index()).setPlacement(context, this.placement);
        if (SettingsManager.showBoard || context.board().isBoardless()) {
            Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.BOARD, context);
        }
        if (SettingsManager.showGraph) {
            Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.GRAPH, context);
        }
        if (SettingsManager.showConnections) {
            Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.CONNECTIONS, context);
        }
        if (SettingsManager.showAxes) {
            Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.AXES, context);
        }
        if (SettingsManager.showPieces) {
            drawBoardState(g2d);
        }
        if (context.game().isDeductionPuzzle()) {
            Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.HINTS, context);
        }
        if (SettingsVC.showCandidateValues && context.game().isDeductionPuzzle()) {
            Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.CANDIDATES, context);
        }
        Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.TRACK, context);
        Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.PREGENERATION, context);
        Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.INDICES, context);
        Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.POSSIBLEMOVES, context);
        Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.COSTS, context);
        this.paintDebug(g2d, Color.CYAN);
    }
    
    static void drawBoardState(final Graphics2D g2d) {
        Context context = ContextSnapshot.getContext();
        if (SettingsVC.SelectingConsequenceMove && Manager.ref().intermediaryContext() != null) {
            context = Manager.ref().intermediaryContext();
        }
        Bridge.getContainerStyle(context.board().index()).draw(g2d, PlaneType.COMPONENTS, context);
    }
    
    @Override
    public int containerIndex() {
        return ContextSnapshot.getContext().board().index();
    }
}
