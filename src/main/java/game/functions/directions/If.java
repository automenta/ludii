// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.directions;

import game.Game;
import game.equipment.component.Component;
import game.functions.booleans.BooleanFunction;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.directions.DirectionFacing;
import topology.TopologyElement;
import util.Context;

import java.util.List;

public class If extends DirectionsFunction
{
    final DirectionsFunction directionFunctionOk;
    final DirectionsFunction directionFunctionNotOk;
    final BooleanFunction condition;
    
    public If(final BooleanFunction condition, final Direction directionsOk, final Direction directionsNotOk) {
        this.directionFunctionOk = directionsOk.directionsFunctions();
        this.directionFunctionNotOk = directionsNotOk.directionsFunctions();
        this.condition = condition;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        gameFlags |= this.condition.gameFlags(game);
        gameFlags |= this.directionFunctionOk.gameFlags(game);
        gameFlags |= this.directionFunctionNotOk.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return this.condition.isStatic() && this.directionFunctionOk.isStatic() && this.directionFunctionNotOk.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        this.condition.preprocess(game);
        this.directionFunctionOk.preprocess(game);
        this.directionFunctionNotOk.preprocess(game);
    }
    
    @Override
    public String toString() {
        return "";
    }
    
    @Override
    public List<AbsoluteDirection> convertToAbsolute(final SiteType graphType, final TopologyElement element, final Component newComponent, final DirectionFacing newFacing, final Integer newRotation, final Context context) {
        if (this.condition.eval(context)) {
            return this.directionFunctionOk.convertToAbsolute(graphType, element, newComponent, newFacing, newRotation, context);
        }
        return this.directionFunctionNotOk.convertToAbsolute(graphType, element, newComponent, newFacing, newRotation, context);
    }
}
