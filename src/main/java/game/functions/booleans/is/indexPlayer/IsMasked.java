// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.indexPlayer;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.types.board.SiteType;
import game.types.play.RoleType;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public final class IsMasked extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    protected SiteType type;
    private final IntFunction locationId;
    private final IntFunction playerId;
    
    public IsMasked(@Opt final SiteType type, final IntFunction locn, @Or final IntFunction indexPlayer, @Or final RoleType role) {
        this.locationId = (locn);
        int numNonNull = 0;
        if (indexPlayer != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Exactly one Or parameter must be non-null.");
        }
        if (indexPlayer != null) {
            this.playerId = indexPlayer;
        }
        else {
            this.playerId = new Id(null, role);
        }
        this.type = type;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (!context.game().hiddenInformation()) {
            return true;
        }
        final int site = this.locationId.eval(context);
        final ContainerState cs = context.state().containerStates()[context.containerId()[site]];
        return cs.isMasked(site, this.playerId.eval(context), this.type);
    }
    
    @Override
    public String toString() {
        return "IsMasked(" + this.playerId + "," + this.locationId + ")";
    }
    
    @Override
    public boolean isStatic() {
        return this.locationId.isStatic() & this.playerId.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.locationId.gameFlags(game) | this.playerId.gameFlags(game);
        if (this.type != null && (this.type.equals(SiteType.Edge) || this.type.equals(SiteType.Vertex))) {
            gameFlags |= 0x800000L;
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.playerId.preprocess(game);
        this.locationId.preprocess(game);
    }
}
