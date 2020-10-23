// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.directions;

import game.Game;
import game.equipment.component.Component;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.directions.DirectionFacing;
import game.util.directions.RelativeDirection;
import topology.TopologyElement;
import util.BaseLudeme;
import util.Context;

import java.util.List;

public abstract class DirectionsFunction extends BaseLudeme implements Direction
{
    public RelativeDirection[] getRelativeDirections() {
        return null;
    }
    
    public abstract List<AbsoluteDirection> convertToAbsolute(final SiteType graphType, final TopologyElement element, final Component newComponent, final DirectionFacing newFacing, final Integer newRotation, final Context context);
    
    public abstract long gameFlags(final Game game);
    
    public abstract boolean isStatic();
    
    public abstract void preprocess(final Game game);
    
    @Override
    public DirectionsFunction directionsFunctions() {
        return this;
    }
}
