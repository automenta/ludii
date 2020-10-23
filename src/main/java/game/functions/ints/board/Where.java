// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.board;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.component.Component;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.types.board.SiteType;
import game.types.play.RoleType;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.ArrayList;

public final class Where extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final String namePiece;
    private final IntFunction playerFn;
    private final IntFunction whatFn;
    private final IntFunction localStateFn;
    private SiteType type;
    private final ArrayList<Component> matchingNameComponents;
    
    public Where(final String namePiece, @Or final IntFunction indexPlayer, @Or final RoleType role, @Opt @Name final IntFunction state, @Opt final SiteType type) {
        this.namePiece = namePiece;
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
            this.playerFn = indexPlayer;
        }
        else {
            this.playerFn = new Id(null, role);
        }
        this.type = type;
        this.whatFn = null;
        this.localStateFn = state;
        this.matchingNameComponents = new ArrayList<>();
    }
    
    public Where(final IntFunction what, @Opt final SiteType type) {
        this.playerFn = null;
        this.type = type;
        this.whatFn = what;
        this.namePiece = null;
        this.localStateFn = null;
        this.matchingNameComponents = null;
    }
    
    @Override
    public int eval(final Context context) {
        int what = -1;
        if (this.whatFn != null) {
            what = this.whatFn.eval(context);
            if (what <= 0) {
                return -1;
            }
            final int numSite = context.board().numSites();
            final ContainerState cs = context.containerState(0);
            final int localState = (this.localStateFn != null) ? this.localStateFn.eval(context) : -1;
            for (int site = 0; site < numSite; ++site) {
                if (cs.what(site, this.type) == what && (localState == -1 || cs.state(site, this.type) == localState)) {
                    return site;
                }
            }
        }
        else {
            final int playerId = this.playerFn.eval(context);
            for (final Component c : this.matchingNameComponents) {
                if (c.owner() == playerId) {
                    what = c.index();
                    break;
                }
            }
            if (what == -1) {
                return -1;
            }
            final int numSite2 = context.board().numSites();
            final ContainerState cs2 = context.containerState(0);
            final int localState2 = (this.localStateFn != null) ? this.localStateFn.eval(context) : -1;
            final TIntArrayList sites = context.state().owned().sites(playerId, what);
            for (int i = 0; i < sites.size(); ++i) {
                final int site2 = sites.getQuick(i);
                if (site2 < numSite2 && cs2.what(site2, this.type) == what && (localState2 == -1 || cs2.state(site2, this.type) == localState2)) {
                    return site2;
                }
            }
        }
        return -1;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        if (this.playerFn != null) {
            gameFlags |= this.playerFn.gameFlags(game);
        }
        if (this.whatFn != null) {
            gameFlags |= this.whatFn.gameFlags(game);
        }
        if (this.localStateFn != null) {
            gameFlags |= (this.localStateFn.gameFlags(game) | 0x2L);
        }
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        if (this.playerFn != null) {
            this.playerFn.preprocess(game);
        }
        if (this.whatFn != null) {
            this.whatFn.preprocess(game);
        }
        if (this.localStateFn != null) {
            this.localStateFn.preprocess(game);
        }
        if (this.namePiece != null) {
            for (final Component c : game.equipment().components()) {
                if (c != null && c.name().contains(this.namePiece)) {
                    this.matchingNameComponents.add(c);
                }
            }
            this.matchingNameComponents.trimToSize();
        }
    }
}
