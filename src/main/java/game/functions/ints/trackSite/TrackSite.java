// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.trackSite;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.trackSite.move.TrackSiteMove;
import game.functions.ints.trackSite.position.TrackSiteEndTrack;
import game.types.play.RoleType;
import game.util.moves.Player;
import util.Context;

public final class TrackSite extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    
    public static IntFunction construct(final TrackSiteType trackSiteType, @Or @Opt final Player player, @Or @Opt final RoleType role, @Opt final String name) {
        switch (trackSiteType) {
            case EndSite: {
                return new TrackSiteEndTrack(player, role, name);
            }
            default: {
                throw new IllegalArgumentException("TrackSite(): A TrackSiteType is not implemented.");
            }
        }
    }
    
    public static IntFunction construct(final TrackSiteMoveType trackSiteType, @Opt @Name final IntFunction from, @Opt @Or final RoleType role, @Opt @Or final Player player, @Opt @Or final String name, @Name final IntFunction steps) {
        switch (trackSiteType) {
            case Move: {
                return new TrackSiteMove(from, role, player, name, steps);
            }
            default: {
                throw new IllegalArgumentException("TrackSite(): A TrackSiteMoveType is not implemented.");
            }
        }
    }
    
    private TrackSite() {
    }
    
    @Override
    public int eval(final Context context) {
        throw new UnsupportedOperationException("TrackSite.eval(): Should never be called directly.");
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
