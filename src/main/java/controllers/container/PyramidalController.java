// 
// Decompiled by Procyon v0.5.36
// 

package controllers.container;

import bridge.Bridge;
import controllers.BaseController;
import game.equipment.container.Container;
import game.rules.play.moves.Moves;
import gnu.trove.list.array.TIntArrayList;
import topology.Cell;
import util.Context;
import util.Move;
import util.WorldLocation;
import util.action.Action;
import util.action.ActionType;
import util.locations.FullLocation;
import util.locations.Location;
import view.container.ContainerStyle;

import java.awt.*;
import java.util.ArrayList;

public class PyramidalController extends BaseController
{
    public PyramidalController(final Container container) {
        super(container);
    }
    
    @Override
    protected Location translateClicktoSite(final Point pt, final Context context, final ArrayList<WorldLocation> allLocations) {
        Location location = super.translateClicktoSite(pt, context, allLocations);
        final Moves legal = context.game().moves(context);
        if (location.site() != -1) {
            final ContainerStyle containerStyle = Bridge.getContainerStyle(this.container.index());
            int newCid = -1;
            final Cell selectedVertex = containerStyle.drawnCells().get(location.site());
            final TIntArrayList possibleCid = new TIntArrayList();
            for (final Cell vertex : containerStyle.drawnCells()) {
                if (Math.abs(vertex.centroid().getX() - selectedVertex.centroid().getX()) < 0.001 && Math.abs(vertex.centroid().getY() - selectedVertex.centroid().getY()) < 0.001) {
                    possibleCid.add(vertex.index());
                }
            }
            for (int m = 0; m < legal.moves().size(); ++m) {
                Action decisionAction = null;
                final Move move = legal.moves().get(m);
                for (int a = 0; a < move.actions().size(); ++a) {
                    if (move.actions().get(a).isDecision()) {
                        decisionAction = move.actions().get(a);
                        break;
                    }
                }
                if (decisionAction.actionType() == ActionType.Add) {
                    for (int i = 0; i < possibleCid.size(); ++i) {
                        final int moveIndex = possibleCid.getQuick(i);
                        if (decisionAction.from() == moveIndex && decisionAction.to() == moveIndex) {
                            newCid = moveIndex;
                            break;
                        }
                    }
                    if (newCid != -1) {
                        location = new FullLocation(newCid, location.level(), location.siteType());
                        break;
                    }
                }
            }
        }
        return location;
    }
}
