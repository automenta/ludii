// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.directions;

import game.Game;
import game.equipment.component.Component;
import game.types.board.RelationType;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.directions.DirectionFacing;
import topology.TopologyElement;
import util.Context;

import java.util.ArrayList;
import java.util.List;

public class Difference extends DirectionsFunction
{
    final DirectionsFunction originalDirection;
    final DirectionsFunction removedDirection;
    
    public Difference(final Direction directions, final Direction directionsToRemove) {
        this.originalDirection = directions.directionsFunctions();
        this.removedDirection = directionsToRemove.directionsFunctions();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        gameFlags |= this.originalDirection.gameFlags(game);
        gameFlags |= this.removedDirection.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return this.originalDirection.isStatic() && this.removedDirection.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        this.originalDirection.preprocess(game);
        this.removedDirection.preprocess(game);
    }
    
    @Override
    public String toString() {
        return "";
    }
    
    @Override
    public List<AbsoluteDirection> convertToAbsolute(final SiteType graphType, final TopologyElement element, final Component newComponent, final DirectionFacing newFacing, final Integer newRotation, final Context context) {
        final List<AbsoluteDirection> directionsReturned = new ArrayList<>();
        final List<AbsoluteDirection> originalDir = this.originalDirection.convertToAbsolute(graphType, element, newComponent, newFacing, newRotation, context);
        final List<AbsoluteDirection> originalAfterConv = new ArrayList<>();
        final List<AbsoluteDirection> directionsToRemove = this.removedDirection.convertToAbsolute(graphType, element, newComponent, newFacing, newRotation, context);
        final List<AbsoluteDirection> removedAfterConv = new ArrayList<>();
        for (final AbsoluteDirection dir : originalDir) {
            final RelationType relation = AbsoluteDirection.converToRelationType(dir);
            if (relation != null) {
                final List<DirectionFacing> directionsFacing = context.board().topology().supportedDirections(relation, context.board().defaultSite());
                for (final DirectionFacing dirFacing : directionsFacing) {
                    if (!originalAfterConv.contains(dirFacing.toAbsolute())) {
                        originalAfterConv.add(dirFacing.toAbsolute());
                    }
                }
            }
            else {
                originalAfterConv.add(dir);
            }
        }
        for (final AbsoluteDirection dir : directionsToRemove) {
            final RelationType relation = AbsoluteDirection.converToRelationType(dir);
            if (relation != null) {
                final List<DirectionFacing> directionsFacing = context.board().topology().supportedDirections(relation, context.board().defaultSite());
                for (final DirectionFacing dirFacing : directionsFacing) {
                    if (!removedAfterConv.contains(dirFacing.toAbsolute())) {
                        removedAfterConv.add(dirFacing.toAbsolute());
                    }
                }
            }
            else {
                removedAfterConv.add(dir);
            }
        }
        for (final AbsoluteDirection dir : originalAfterConv) {
            if (!removedAfterConv.contains(dir)) {
                directionsReturned.add(dir);
            }
        }
        return directionsReturned;
    }
}
