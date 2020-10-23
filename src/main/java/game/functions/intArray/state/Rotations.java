// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.intArray.state;

import annotations.Or;
import game.Game;
import game.functions.intArray.BaseIntArrayFunction;
import game.types.board.RelationType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.DirectionFacing;
import gnu.trove.list.array.TIntArrayList;
import util.Context;

import java.util.List;

public class Rotations extends BaseIntArrayFunction
{
    private static final long serialVersionUID = 1L;
    private int[] precomputedDirection;
    final AbsoluteDirection[] directionsOfRotation;
    
    public Rotations(@Or final AbsoluteDirection directionOfRotation, @Or final AbsoluteDirection[] directionsOfRotation) {
        int numNonNull = 0;
        if (directionOfRotation != null) {
            ++numNonNull;
        }
        if (directionsOfRotation != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Only one Or should be non-null.");
        }
        this.directionsOfRotation = ((directionsOfRotation != null) ? directionsOfRotation : new AbsoluteDirection[] { directionOfRotation });
    }
    
    @Override
    public int[] eval(final Context context) {
        if (this.precomputedDirection != null) {
            return this.precomputedDirection;
        }
        final TIntArrayList directions = new TIntArrayList();
        final int numEdges = context.topology().numEdges();
        final int ratio = context.topology().supportedDirections(context.board().defaultSite()).size() / numEdges;
        for (final AbsoluteDirection absoluteDirection : this.directionsOfRotation) {
            final DirectionFacing direction = AbsoluteDirection.convert(absoluteDirection);
            if (direction != null) {
                final int rotation = AbsoluteDirection.convert(absoluteDirection).index() / ratio;
                if (!directions.contains(rotation)) {
                    directions.add(rotation);
                }
            }
            else {
                final RelationType relation = AbsoluteDirection.converToRelationType(absoluteDirection);
                if (relation != null) {
                    final List<DirectionFacing> directionsAbsolute = context.topology().supportedDirections(relation, context.board().defaultSite());
                    for (final DirectionFacing eqDirection : directionsAbsolute) {
                        final AbsoluteDirection absDirection = eqDirection.toAbsolute();
                        final int rotation2 = AbsoluteDirection.convert(absDirection).index() / ratio;
                        if (!directions.contains(rotation2)) {
                            directions.add(rotation2);
                        }
                    }
                }
            }
        }
        return directions.toArray();
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long gameFlags = 131072L;
        return 131072L;
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.isStatic()) {
            this.precomputedDirection = this.eval(new Context(game, null));
        }
    }
    
    @Override
    public String toString() {
        final String str = "Rotations";
        return "Rotations";
    }
}
