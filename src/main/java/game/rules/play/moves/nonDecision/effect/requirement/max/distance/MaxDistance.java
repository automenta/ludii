// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.requirement.max.distance;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.equipment.container.board.Track;
import game.functions.ints.board.Id;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.types.play.RoleType;
import util.Context;
import util.Move;
import util.TempContext;

@Hide
public final class MaxDistance extends Effect
{
    private static final long serialVersionUID = 1L;
    private final Moves moves;
    private final String trackName;
    private final RoleType owner;
    
    public MaxDistance(@Opt final String trackName, @Opt final RoleType owner, final Moves moves, @Opt final Then then) {
        super(then);
        this.moves = moves;
        this.trackName = trackName;
        this.owner = owner;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves returnMoves = new BaseMoves(super.then());
        Track track = null;
        final int who = (this.owner == null) ? -1 : new Id(null, this.owner).eval(context);
        for (final Track t : context.tracks()) {
            if (this.trackName == null || (who == -1 && t.name().equals(this.trackName)) || (who != -1 && t.owner() == who && t.name().contains(this.trackName))) {
                track = t;
                break;
            }
        }
        if (track == null) {
            return this.moves;
        }
        final Moves movesToEval = this.moves.eval(context);
        final int[] distanceCount = new int[movesToEval.moves().size()];
        for (int i = 0; i < movesToEval.moves().size(); ++i) {
            final Move m = movesToEval.moves().get(i);
            int indexFrom = -1;
            int indexTo = -1;
            for (int j = 0; j < track.elems().length; ++j) {
                if (track.elems()[j].site == m.fromNonDecision()) {
                    indexFrom = j;
                }
                else if (track.elems()[j].site == m.toNonDecision()) {
                    indexTo = j;
                }
                if (indexFrom != -1 && indexTo != -1) {
                    break;
                }
            }
            final int distance = Math.abs(indexFrom - indexTo);
            distanceCount[i] = (context.recursiveCalled() ? distance : this.getDistanceCount(context, track, context.state().mover(), m, distance));
        }
        int max = 0;
        for (final int sizeDistance : distanceCount) {
            if (sizeDistance > max) {
                max = sizeDistance;
            }
        }
        for (int k = 0; k < movesToEval.moves().size(); ++k) {
            if (distanceCount[k] == max) {
                returnMoves.moves().add(movesToEval.moves().get(k));
            }
        }
        return this.moves.eval(context);
    }
    
    private int getDistanceCount(final Context context, final Track track, final int mover, final Move m, final int distance) {
        if (m.isPass() || m.toNonDecision() == m.fromNonDecision()) {
            return distance;
        }
        final Context newContext = new TempContext(context);
        newContext.setRecursiveCalled(true);
        newContext.game().apply(newContext, m);
        if (mover != newContext.state().mover()) {
            return distance;
        }
        final Moves legalMoves = newContext.game().moves(newContext);
        final int[] distanceCount = new int[legalMoves.moves().size()];
        for (int i = 0; i < legalMoves.moves().size(); ++i) {
            final Move newMove = legalMoves.moves().get(i);
            int indexFrom = -1;
            int indexTo = -1;
            for (int j = 0; j < track.elems().length; ++j) {
                if (track.elems()[j].site == m.fromNonDecision()) {
                    indexFrom = j;
                }
                else if (track.elems()[j].site == m.toNonDecision()) {
                    indexTo = j;
                }
                if (indexFrom != -1 && indexTo != -1) {
                    break;
                }
            }
            final int newDistance = Math.abs(indexFrom - indexTo);
            distanceCount[i] = this.getDistanceCount(newContext, track, mover, newMove, distance + newDistance);
        }
        int max = 0;
        for (final int sizeDistance : distanceCount) {
            if (sizeDistance > max) {
                max = sizeDistance;
            }
        }
        return max;
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long gameFlags = this.moves.gameFlags(game) | super.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        final boolean isStatic = this.moves.isStatic();
        return isStatic;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.moves.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "MaxDistance";
    }
}
