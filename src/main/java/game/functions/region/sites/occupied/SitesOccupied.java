// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.occupied;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.component.Component;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.region.BaseRegionFunction;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.equipment.Region;
import game.util.moves.Player;
import gnu.trove.list.array.TIntArrayList;
import util.ContainerId;
import util.Context;
import util.state.containerStackingState.BaseContainerStateStacking;
import util.state.containerState.ContainerState;

@Hide
public final class SitesOccupied extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction who;
    private final RoleType role;
    private final ContainerId containerId;
    private final String containerName;
    private final IntFunction containerFn;
    private final String[] kindComponents;
    private final IntFunction component;
    private final boolean top;
    
    public SitesOccupied(@Or final Player who, @Or final RoleType role, @Opt @Or @Name final IntFunction container, @Opt @Or final String containerName, @Opt @Name final IntFunction componentFn, @Opt @Name final String component, @Opt @Name final String[] components, @Opt @Name final Boolean top, @Opt final SiteType type) {
        this.who = ((who == null) ? new Id(null, role) : who.index());
        this.containerId = new ContainerId(container, containerName, (containerName != null && containerName.contains("Hand")) ? role : null, null, null);
        this.containerName = containerName;
        this.containerFn = container;
        this.kindComponents = ((components != null) ? components : ((component == null) ? new String[0] : new String[] { component }));
        this.component = componentFn;
        this.type = type;
        this.top = (top == null || top);
        this.role = role;
    }
    
    @Override
    public Region eval(final Context context) {
        final TIntArrayList sitesOwned = new TIntArrayList();
        final int whoId = this.who.eval(context);
        final TIntArrayList idSpecificComponents = new TIntArrayList();
        if (this.kindComponents.length != 0) {
            for (int indexComponent = 1; indexComponent < context.components().length; ++indexComponent) {
                final Component comp = context.components()[indexComponent];
                for (final String kindComponent : this.kindComponents) {
                    if (comp.name().contains(kindComponent) && (comp.owner() == whoId || this.role == RoleType.All)) {
                        idSpecificComponents.add(comp.index());
                    }
                }
            }
        }
        if (whoId == context.players().size() && this.containerFn == null) {
            for (int pid = 1; pid <= context.players().size(); ++pid) {
                sitesOwned.addAll(context.state().owned().sites(pid));
            }
            if (!idSpecificComponents.isEmpty()) {
                for (int i = 0; i < sitesOwned.size(); ++i) {
                    final int site = sitesOwned.get(i);
                    final int what = context.containerState(context.containerId()[site]).what(site, this.type);
                    if (!idSpecificComponents.contains(what)) {
                        sitesOwned.removeAt(i);
                        --i;
                    }
                }
            }
            return new Region(sitesOwned.toArray());
        }
        if (whoId == context.players().size()) {
            final int cid = this.containerFn.eval(context);
            for (int pid2 = 1; pid2 <= context.players().size(); ++pid2) {
                final TIntArrayList sites = context.state().owned().sites(pid2);
                for (int j = 0; j < sites.size(); ++j) {
                    if (context.containerId()[sites.getQuick(j)] == cid) {
                        sitesOwned.add(sites.getQuick(j));
                    }
                }
            }
            return new Region(sitesOwned.toArray());
        }
        final int indexPlayer = whoId;
        if (this.containerFn == null && this.containerName == null && this.kindComponents.length == 0) {
            if (indexPlayer != -1) {
                sitesOwned.addAll(context.state().owned().sites(indexPlayer));
            }
            else if (this.role == RoleType.NonMover || this.role == RoleType.Enemy) {
                for (int pid2 = 1; pid2 <= context.players().size(); ++pid2) {
                    if (pid2 != context.state().mover()) {
                        sitesOwned.addAll(context.state().owned().sites(pid2));
                    }
                }
            }
        }
        else {
            final int cid2 = this.containerId.eval(context);
            final int sitesFrom = context.sitesFrom()[cid2];
            final int sitesTo = sitesFrom + context.containers()[cid2].numSites();
            if (idSpecificComponents.isEmpty()) {
                final TIntArrayList ownedList = new TIntArrayList();
                if (indexPlayer != -1) {
                    ownedList.addAll(context.state().owned().sites(indexPlayer));
                }
                else if (this.role == RoleType.NonMover || this.role == RoleType.Enemy) {
                    for (int pid3 = 1; pid3 <= context.players().size(); ++pid3) {
                        if (pid3 != context.state().mover()) {
                            ownedList.addAll(context.state().owned().sites(pid3));
                        }
                    }
                }
                for (int k = 0; k < ownedList.size(); ++k) {
                    final int owned = ownedList.getQuick(k);
                    if (owned >= sitesFrom && owned < sitesTo) {
                        sitesOwned.add(owned);
                    }
                }
            }
            else {
                for (int index = 0; index < idSpecificComponents.size(); ++index) {
                    final int idSpecificComponent = idSpecificComponents.getQuick(index);
                    final TIntArrayList ownedList2 = context.state().owned().sites(indexPlayer, idSpecificComponent);
                    for (int l = 0; l < ownedList2.size(); ++l) {
                        final int owned2 = ownedList2.getQuick(l);
                        if (owned2 >= sitesFrom && owned2 < sitesTo) {
                            sitesOwned.add(owned2);
                        }
                    }
                }
            }
            if (context.game().hasLargePiece() && cid2 == 0) {
                final TIntArrayList sitesToReturn = new TIntArrayList(sitesOwned);
                final ContainerState cs = context.containerState(0);
                for (int m = 0; m < sitesOwned.size(); ++m) {
                    final int site2 = sitesOwned.get(m);
                    final int what2 = cs.what(site2, this.type);
                    if (what2 != 0) {
                        final Component piece = context.equipment().components()[what2];
                        if (piece.isLargePiece()) {
                            final int localState = cs.state(site2, this.type);
                            final TIntArrayList locs = piece.locs(context, site2, localState, context.topology());
                            for (int j2 = 0; j2 < locs.size(); ++j2) {
                                if (!sitesToReturn.contains(locs.get(j2))) {
                                    sitesToReturn.add(locs.get(j2));
                                }
                            }
                        }
                    }
                }
                return new Region(sitesToReturn.toArray());
            }
        }
        if (this.top && context.game().isStacking()) {
            for (int i2 = 0; i2 < sitesOwned.size(); ++i2) {
                final int site3 = sitesOwned.getQuick(i2);
                final BaseContainerStateStacking state = (BaseContainerStateStacking)context.state().containerStates()[context.containerId()[site3]];
                if (state.what(site3, this.type) == 0 || context.components()[state.what(site3, this.type)].owner() != indexPlayer) {
                    sitesOwned.remove(site3);
                    --i2;
                }
            }
        }
        return new Region(sitesOwned.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        if (this.type != null) {
            switch (this.type) {
                case Vertex: {
                    flags |= 0x1000000L;
                    flags |= 0x800000L;
                    break;
                }
                case Edge: {
                    flags |= 0x4000000L;
                    flags |= 0x800000L;
                    break;
                }
                case Cell: {
                    flags |= 0x2000000L;
                    break;
                }
            }
        }
        flags |= this.who.gameFlags(game);
        if (this.component != null) {
            flags |= this.component.gameFlags(game);
        }
        if (this.containerFn != null) {
            flags |= this.containerFn.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.who.preprocess(game);
        if (this.component != null) {
            this.component.preprocess(game);
        }
        if (this.containerFn != null) {
            this.containerFn.preprocess(game);
        }
    }
    
    public IntFunction who() {
        return this.who;
    }
}
