// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.end;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.types.board.TrackType;
import game.types.play.RoleType;
import util.Context;

public final class ForEach extends EndRule
{
    private static final long serialVersionUID = 1L;
    private final RoleType type;
    private final TrackType trackType;
    private final BooleanFunction cond;
    
    public ForEach(@Opt @Or final RoleType type, @Opt @Or final TrackType trackType, @Name final BooleanFunction If, final Result result) {
        super(result);
        int numNonNull = 0;
        if (type != null) {
            ++numNonNull;
        }
        if (trackType != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("ForEach(): one of RoleType or trackType has to be null.");
        }
        this.type = ((type == null) ? RoleType.Shared : type);
        this.cond = If;
        this.trackType = trackType;
    }
    
    @Override
    public EndRule eval(final Context context) {
        final int numPlayers = context.game().players().count();
        if (this.trackType != null) {
            final int originalTrackIndex = context.track();
            for (int trackIndex = 0; trackIndex < context.board().tracks().size() && context.active(); ++trackIndex) {
                context.setTrack(trackIndex);
                if (this.cond.eval(context)) {
                    End.applyResult(this.result(), context);
                }
            }
            context.setTrack(originalTrackIndex);
        }
        else {
            for (int pid = 1; pid <= numPlayers; ++pid) {
                if (this.type == RoleType.NonMover) {
                    final int mover = context.state().mover();
                    if (pid == mover) {
                        continue;
                    }
                }
                if (context.active(pid)) {
                    context.setIterator(pid);
                    if (this.cond.eval(context)) {
                        End.applyResult(this.result(), context);
                    }
                }
            }
        }
        context.resetIterator();
        return new BaseEndRule(null);
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        if (this.cond != null) {
            gameFlags |= this.cond.gameFlags(game);
        }
        if (this.result() != null) {
            gameFlags |= this.result().gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.cond != null) {
            this.cond.preprocess(game);
        }
        if (this.result() != null) {
            this.result().preprocess(game);
        }
    }
}
