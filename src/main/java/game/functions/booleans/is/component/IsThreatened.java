// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.component;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Where;
import game.functions.region.RegionFunction;
import game.functions.region.sites.Sites;
import game.functions.region.sites.occupied.SitesOccupied;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.types.play.RoleType;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.Move;
import util.TempContext;
import util.action.Action;
import util.action.move.ActionRemove;
import util.state.containerState.ContainerState;

import java.util.List;

@Hide
public final class IsThreatened extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction what;
    private final RegionFunction region;
    private SiteType type;
    private final Moves specificMoves;
    private static ThreadLocal<Boolean> autoFail;
    
    public IsThreatened(@Opt final IntFunction what, @Opt final SiteType type, @Opt @Or final IntFunction site, @Opt @Or final RegionFunction sites, @Opt final Moves specificMoves) {
        this.region = ((site == null) ? ((sites == null) ? ((what == null) ? new SitesOccupied(null, RoleType.Shared, null, null, null, null, null, null, null) : Sites.construct(new IntFunction[] { new Where(what, null) })) : sites) : Sites.construct(new IntFunction[] { site }));
        this.what = what;
        this.type = type;
        this.specificMoves = specificMoves;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (this.what != null) {
            if (this.what.eval(context) < 1) {
                return false;
            }
            final int ownerWhat = context.components()[this.what.eval(context)].owner();
            if (context.recursiveCalled()) {
                return false;
            }
            final int[] sites3;
            final int[] sites = sites3 = this.region.eval(context).sites();
            for (final int site : sites3) {
                if (site != -1) {
                    final Context newContext = new TempContext(context);
                    newContext.state().setPrev(ownerWhat);
                    newContext.containerState(newContext.containerId()[site]).setSite(newContext.state(), site, ownerWhat, this.what.eval(newContext), -1, -1, -1, -1, ((this.type != null && this.type == SiteType.Cell) || (this.type == null && context.game().board().defaultSite() != SiteType.Vertex)) ? SiteType.Vertex : SiteType.Cell);
                    IsThreatened.autoFail.set(Boolean.TRUE);
                    final TIntArrayList enemies = context.game().players().players().get(ownerWhat).enemies();
                    for (int i = 0; i < enemies.size(); ++i) {
                        final int enemyId = enemies.getQuick(i);
                        newContext.state().setMover(enemyId);
                        newContext.setRecursiveCalled(true);
                        final int enemyPhase = newContext.state().currentPhase(enemyId);
                        final Moves moves = (this.specificMoves == null) ? newContext.game().rules().phases()[enemyPhase].play().moves() : this.specificMoves;
                        if (moves.canMoveTo(newContext, site)) {
                            IsThreatened.autoFail.set(Boolean.FALSE);
                            return true;
                        }
                    }
                    IsThreatened.autoFail.set(Boolean.FALSE);
                }
            }
        }
        else {
            final int[] sites2 = this.region.eval(context).sites();
            if (sites2.length == 0) {
                return false;
            }
            final ContainerState cs = context.containerState(context.containerId()[sites2[0]]);
            for (final int site : sites2) {
                final int idPiece = cs.what(site, this.type);
                if (idPiece > 0) {
                    if (site != -1) {
                        final int ownerWhat2 = context.components()[idPiece].owner();
                        final int nextPlayer = context.state().next();
                        if (0 == nextPlayer || nextPlayer > context.game().players().count()) {
                            return false;
                        }
                        final Context newContext2 = new TempContext(context);
                        newContext2.state().setPrev(ownerWhat2);
                        newContext2.containerState(newContext2.containerId()[site]).setSite(newContext2.state(), site, ownerWhat2, idPiece, -1, -1, -1, -1, ((this.type != null && this.type == SiteType.Cell) || (this.type == null && context.game().board().defaultSite() != SiteType.Vertex)) ? SiteType.Vertex : SiteType.Cell);
                        final TIntArrayList enemies2 = newContext2.game().players().players().get(ownerWhat2).enemies();
                        for (int j = 0; j < enemies2.size(); ++j) {
                            newContext2.state().setMover(enemies2.getQuick(j));
                            newContext2.state().setNext(ownerWhat2);
                            final int enemyPhase2 = newContext2.state().currentPhase(enemies2.getQuick(j));
                            final Moves moves2 = (this.specificMoves == null) ? newContext2.game().rules().phases()[enemyPhase2].play().moves() : this.specificMoves.eval(newContext2);
                            for (final Move m : moves2.moves()) {
                                final List<Action> actions = m.getAllBaseActions(newContext2);
                                for (final Action action : actions) {
                                    if (action instanceof ActionRemove && action.to() == site) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Threatened(" + this.what + "," + this.region + ")";
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        if (this.type != null) {
            if (this.type == SiteType.Edge || this.type == SiteType.Vertex) {
                gameFlags |= 0x800000L;
            }
            if (this.type == SiteType.Edge) {
                gameFlags |= 0x4000000L;
            }
            if (this.type == SiteType.Vertex) {
                gameFlags |= 0x1000000L;
            }
            if (this.type == SiteType.Cell) {
                gameFlags |= 0x2000000L;
            }
        }
        else if (game.board().defaultSite() == SiteType.Vertex) {
            gameFlags |= 0x1000000L;
        }
        else {
            gameFlags |= 0x2000000L;
        }
        if (this.what != null) {
            gameFlags |= this.what.gameFlags(game);
        }
        if (this.specificMoves != null) {
            gameFlags |= this.specificMoves.gameFlags(game);
        }
        gameFlags |= this.region.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        if (this.what != null) {
            this.what.preprocess(game);
        }
        this.region.preprocess(game);
        if (this.specificMoves != null) {
            this.specificMoves.preprocess(game);
        }
    }
    
    @Override
    public boolean autoFails() {
        return IsThreatened.autoFail.get();
    }
    
    static {
        IsThreatened.autoFail = ThreadLocal.withInitial(() -> Boolean.FALSE);
    }
}
