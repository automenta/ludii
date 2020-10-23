// 
// Decompiled by Procyon v0.5.36
// 

package manager.referee;

import game.Game;
import game.equipment.container.Container;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import manager.Manager;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import topology.TopologyElement;
import topology.Vertex;
import util.ContainerUtil;
import util.Context;
import util.Move;
import util.SettingsVC;
import util.locations.FullLocation;
import util.locations.Location;

import java.awt.*;
import java.util.ArrayList;

public class MoveUtil
{
    public static boolean moveMatchesLocation(final Move move, final Location fromInfo, final Location toInfo, final Context context) {
        return checkVertexMoveForEdge(move, fromInfo, toInfo, context) || (move.matchesUserMove(fromInfo.site(), fromInfo.level(), fromInfo.siteType(), toInfo.site(), toInfo.level(), toInfo.siteType()) && moveMatchesDraggedPieceRotation(move, fromInfo));
    }
    
    private static boolean checkVertexMoveForEdge(final Move move, final Location fromInfo, final Location toInfo, final Context context) {
        if (move.fromType() == SiteType.Edge && move.toType() == SiteType.Edge && move.getFromLocation().equalsLoc(move.getToLocation())) {
            if (fromInfo.siteType() != SiteType.Vertex || fromInfo.siteType() != SiteType.Vertex || fromInfo.site() == toInfo.site()) {
                SettingsVC.selectedLocation = new FullLocation(-1);
                return false;
            }
            if (move.from() == move.to()) {
                final int edgeIndex = move.from();
                final Vertex va = context.board().topology().edges().get(edgeIndex).vA();
                final Vertex vb = context.board().topology().edges().get(edgeIndex).vB();
                if (va.index() == fromInfo.site() && vb.index() == toInfo.site()) {
                    return true;
                }
                return !move.isOrientedMove() && vb.index() == fromInfo.site() && va.index() == toInfo.site();
            }
        }
        return false;
    }
    
    public static String getLocationCoordinate(final Location location, final Context context) {
        for (final Container container : context.equipment().containers()) {
            for (final TopologyElement graphElement : container.topology().getAllGraphElements()) {
                if (graphElement.index() == location.site() && graphElement.elementType() == location.siteType()) {
                    return graphElement.label();
                }
            }
        }
        return "NULL";
    }
    
    public static boolean moveMatchesDraggedPieceRotation(final Move move, final Location fromInfo) {
        final Context context = ContextSnapshot.getContext();
        if (context.game().hasLargePiece()) {
            final int containerId = ContainerUtil.getContainerId(context, fromInfo.site(), fromInfo.siteType());
            final int componentIndex = context.containerState(containerId).whatCell(fromInfo.site());
            return move.what() != 0 || !context.components()[componentIndex].isLargePiece() || move.state() == SettingsManager.dragComponentState;
        }
        return true;
    }
    
    public static boolean animatePieceMovement(final Move move) {
        final Context context = ContextSnapshot.getContext();
        final Game game = context.game();
        return move != null && !SettingsVC.noAnimation && !game.isDeductionPuzzle() && !game.hasLargePiece() && move.from() != -1 && move.to() != -1 && SettingsManager.showAnimation;
    }
    
    public static void applyDirectionMove(final AbsoluteDirection direction) {
        final Context context = Manager.ref().context();
        final Moves legal = context.game().moves(context);
        final ArrayList<Move> validMovesfound = new ArrayList<>();
        for (final Move m : legal.moves()) {
            if (direction == m.direction(context)) {
                validMovesfound.add(m);
            }
        }
        if (validMovesfound.size() == 1) {
            Manager.ref().applyHumanMoveToGame(validMovesfound.get(0));
        }
        else if (validMovesfound.isEmpty()) {
            Manager.app.setVolatileMessage("No valid moves found for Direction " + direction.name());
            EventQueue.invokeLater(() -> Manager.app.repaint());
        }
        else {
            Manager.app.setVolatileMessage("Too many valid moves found for Direction " + direction.name());
            EventQueue.invokeLater(() -> Manager.app.repaint());
        }
    }
}
