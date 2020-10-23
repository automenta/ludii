// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.size.connection;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.directions.AbsoluteDirection;
import game.util.graph.Step;
import game.util.moves.Player;
import gnu.trove.list.array.TIntArrayList;
import main.Constants;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.BitSet;
import java.util.List;

@Hide
public final class SizeTerritory extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction indexPlayer;
    private final AbsoluteDirection direction;
    private SiteType type;
    
    public SizeTerritory(@Opt final SiteType type, @Or final RoleType role, @Or final Player player, @Opt final AbsoluteDirection direction) {
        this.direction = ((direction == null) ? AbsoluteDirection.Orthogonal : direction);
        this.indexPlayer = ((player != null) ? player.index() : new Id(null, role));
        this.type = type;
    }
    
    @Override
    public int eval(final Context context) {
        final List<? extends TopologyElement> elements = context.game().graphPlayElements();
        final ContainerState state = context.state().containerStates()[0];
        final int whoSiteId = this.indexPlayer.eval(context);
        final int totalElements = elements.size();
        final int[] localParent = new int[totalElements];
        final int[] rank = new int[totalElements];
        final BitSet[] localItemWithOrth = new BitSet[totalElements];
        int sizeTerritory = 0;
        final TIntArrayList nList = new TIntArrayList();
        final Topology topology = context.topology();
        for (int i = 0; i < totalElements; ++i) {
            localItemWithOrth[i] = new BitSet(totalElements);
            localParent[i] = Constants.UNUSED;
            rank[i] = 0;
        }
        for (int k = 0; k < totalElements; ++k) {
            if (state.who(k, this.type) == 0) {
                localParent[k] = k;
                localItemWithOrth[k].set(k);
                final List<Step> steps = topology.trajectories().steps(this.type, k, this.type, this.direction);
                for (final Step step : steps) {
                    nList.add(step.to().id());
                }
                for (int j = 0; j < nList.size(); ++j) {
                    localItemWithOrth[k].set(nList.getQuick(j));
                }
                for (int j = 0; j < nList.size(); ++j) {
                    final int ni = nList.getQuick(j);
                    boolean connect = true;
                    if (state.who(ni, this.type) == 0 && ni < k) {
                        for (int l = j + 1; l < nList.size(); ++l) {
                            final int nj = nList.getQuick(l);
                            if (state.who(nj, this.type) == 0 && this.connected(ni, nj, localParent)) {
                                connect = false;
                                break;
                            }
                        }
                        if (connect) {
                            final int rootP = this.find(ni, localParent);
                            final int rootQ = this.find(k, localParent);
                            if (rank[rootP] < rank[rootQ]) {
                                localParent[rootP] = rootQ;
                                localItemWithOrth[rootQ].or(localItemWithOrth[rootP]);
                            }
                            else {
                                localParent[rootQ] = rootP;
                                localItemWithOrth[rootP].or(localItemWithOrth[rootQ]);
                                if (rank[rootP] == rank[rootQ]) {
                                    final int[] array = rank;
                                    final int n = rootP;
                                    ++array[n];
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < totalElements; ++i) {
            if (i == localParent[i]) {
                boolean flagTerritory = true;
                int count = 0;
                for (int m = localItemWithOrth[i].nextSetBit(0); m >= 0; m = localItemWithOrth[i].nextSetBit(m + 1)) {
                    if (state.who(m, this.type) == 0) {
                        ++count;
                    }
                    if (state.who(m, this.type) != whoSiteId && state.who(m, this.type) != 0) {
                        flagTerritory = false;
                    }
                }
                if (flagTerritory) {
                    sizeTerritory += count;
                }
            }
        }
        return sizeTerritory;
    }
    
    private boolean connected(final int position1, final int position2, final int[] parent) {
        final int root1 = this.find(position1, parent);
        final int root2 = this.find(position2, parent);
        return root1 == root2;
    }
    
    private int find(final int position, final int[] parent) {
        final int parentId = parent[position];
        if (parentId == Constants.UNUSED) {
            return position;
        }
        if (parentId == position) {
            return position;
        }
        return this.find(parentId, parent);
    }
    
    public static TIntArrayList validPositionAll(final List<? extends TopologyElement> verticesList) {
        final int verticesListSz = verticesList.size();
        final TIntArrayList integerVerticesList = new TIntArrayList(verticesListSz);
        for (TopologyElement topologyElement : verticesList) {
            integerVerticesList.add(topologyElement.index());
        }
        return integerVerticesList;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        flags |= SiteType.stateFlags(this.type);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.indexPlayer.preprocess(game);
        this.type = SiteType.use(this.type, game);
    }
}
