// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.wip;

import annotations.*;
import game.Game;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.ints.last.LastTo;
import game.types.board.SiteType;
import game.types.play.RoleType;
import topology.Edge;
import topology.Topology;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.BitSet;

@Hide
public final class Tree extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction who;
    private final SiteType indexType;
    private final BooleanFunction caterpillarTreeFn;
    private final BooleanFunction maxPathSizeFn;
    private final BooleanFunction leafSizeFn;
    private final BooleanFunction degreeSFn;
    private final BooleanFunction degreePFn;
    private final IntFunction componentSize;
    
    @Hide
    public Tree(final SiteType type, @Or final IntFunction who, @Or final RoleType role, @Opt @Name final BooleanFunction caterpillarTree, @Opt @Name final IntFunction length, @Opt @Or2 @Name final BooleanFunction maxPathSize, @Opt @Or2 @Name final BooleanFunction leafSize, @Opt @Or2 @Name final BooleanFunction internalDegreeSum, @Opt @Or2 @Name final BooleanFunction degreeProduct) {
        int numNonNull = 0;
        if (who != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Only one Or parameter must be non-null.");
        }
        int numNonNull2 = 0;
        if (maxPathSize != null) {
            ++numNonNull2;
        }
        if (leafSize != null) {
            ++numNonNull2;
        }
        if (internalDegreeSum != null) {
            ++numNonNull2;
        }
        if (degreeProduct != null) {
            ++numNonNull2;
        }
        if (numNonNull2 != 1) {
            throw new IllegalArgumentException("Only one Or2 parameter must be non-null.");
        }
        this.indexType = type;
        this.who = ((who == null) ? new Id(null, role) : who);
        this.maxPathSizeFn = ((maxPathSize == null) ? BooleanConstant.construct(false) : maxPathSize);
        this.leafSizeFn = ((leafSize == null) ? BooleanConstant.construct(false) : leafSize);
        this.degreeSFn = ((internalDegreeSum == null) ? BooleanConstant.construct(false) : internalDegreeSum);
        this.degreePFn = ((degreeProduct == null) ? BooleanConstant.construct(false) : degreeProduct);
        this.componentSize = ((length == null) ? new IntConstant(0) : length);
        this.caterpillarTreeFn = ((caterpillarTree == null) ? BooleanConstant.construct(false) : caterpillarTree);
    }
    
    @Override
    public int eval(final Context context) {
        final int siteId = new LastTo(null).eval(context);
        if (siteId == -1) {
            return 0;
        }
        final Topology graph = context.topology();
        final int cid = context.containerId()[0];
        final ContainerState state = context.state().containerStates()[cid];
        final int whoSiteId = this.who.eval(context);
        final boolean returnMaxPathSize = this.maxPathSizeFn.eval(context);
        final boolean returnLeafSize = this.leafSizeFn.eval(context);
        final boolean caterpillarTreeFlag = this.caterpillarTreeFn.eval(context);
        final boolean internaldegreeSumFlag = this.degreeSFn.eval(context);
        final boolean degreeProductFlag = this.degreePFn.eval(context);
        final int length = this.componentSize.eval(context);
        final int totalVertices = graph.vertices().size();
        final int totalEdges = graph.edges().size();
        final int[] localParent = new int[totalVertices];
        final BitSet[] degreeInfo = new BitSet[totalVertices];
        for (int i = 0; i < totalVertices; ++i) {
            degreeInfo[localParent[i] = i] = new BitSet(totalEdges);
        }
        int existingEdges = 0;
        for (int k = 0; k < totalEdges; ++k) {
            final Edge kEdge = graph.edges().get(k);
            if (state.who(kEdge.index(), this.indexType) == whoSiteId) {
                ++existingEdges;
                final int vA = kEdge.vA().index();
                final int vB = kEdge.vB().index();
                degreeInfo[vA].set(vB);
                degreeInfo[vB].set(vA);
                final int vARoot = this.find(vA, localParent);
                final int vBRoot = this.find(vB, localParent);
                if (vARoot == vBRoot) {
                    return 0;
                }
                localParent[vARoot] = vBRoot;
            }
        }
        int count = 0;
        for (int j = 0; j < totalVertices; ++j) {
            if (localParent[j] == j) {
                ++count;
            }
        }
        if (internaldegreeSumFlag && existingEdges == totalVertices - 1) {
            int sum = 0;
            for (int l = 0; l < totalVertices; ++l) {
                if (degreeInfo[l].cardinality() != 1) {
                    sum += degreeInfo[l].cardinality();
                }
            }
            return sum;
        }
        if (degreeProductFlag && existingEdges == totalVertices - 1) {
            int product = 1;
            for (int l = 0; l < totalVertices; ++l) {
                product *= degreeInfo[l].cardinality();
            }
            return product;
        }
        if (caterpillarTreeFlag && length == 0 && count != 1) {
            return 0;
        }
        final BitSet caterpillarBackbone = new BitSet(totalEdges);
        for (int m = 0; m < totalEdges; ++m) {
            final Edge kEdge2 = graph.edges().get(m);
            if (state.who(kEdge2.index(), this.indexType) == whoSiteId) {
                final int kEdgevA = kEdge2.vA().index();
                int degree1 = 0;
                for (int ka = 0; ka < totalEdges; ++ka) {
                    final Edge kaEdge = graph.edges().get(ka);
                    if (state.who(kaEdge.index(), this.indexType) == whoSiteId && (kEdgevA == kaEdge.vA().index() || kEdgevA == kaEdge.vB().index()) && ++degree1 > 1) {
                        break;
                    }
                }
                if (degree1 >= 2) {
                    final int kEdgevB = kEdge2.vB().index();
                    int degree2 = 0;
                    for (int kb = 0; kb < totalEdges; ++kb) {
                        final Edge kbEdge = graph.edges().get(kb);
                        if (state.who(kbEdge.index(), this.indexType) == whoSiteId && (kEdgevB == kbEdge.vA().index() || kEdgevB == kbEdge.vB().index()) && ++degree2 > 1) {
                            break;
                        }
                    }
                    if (degree1 > 1 && degree2 > 1) {
                        caterpillarBackbone.set(kEdge2.index());
                    }
                }
            }
        }
        final Edge kEdge3 = graph.edges().get(caterpillarBackbone.nextSetBit(0));
        final int v1 = kEdge3.vA().index();
        final int v2 = kEdge3.vB().index();
        final BitSet depthBitset1 = new BitSet(totalVertices);
        final BitSet depthBitset2 = new BitSet(totalVertices);
        final BitSet visitedEdge = new BitSet(totalEdges);
        final int componentSz = totalVertices;
        this.dfsMinPathEdge(context, graph, kEdge3, caterpillarBackbone, visitedEdge, 0, v1, v2, componentSz, depthBitset1, whoSiteId);
        this.dfsMinPathEdge(context, graph, kEdge3, caterpillarBackbone, visitedEdge, 0, v2, v1, componentSz, depthBitset2, whoSiteId);
        final int pathLength = depthBitset1.cardinality() - 1 + (depthBitset2.cardinality() - 1) + 1;
        final BitSet leafBitset = new BitSet(totalVertices);
        leafBitset.set(0, totalVertices, true);
        for (int i2 = caterpillarBackbone.nextSetBit(0); i2 >= 0; i2 = caterpillarBackbone.nextSetBit(i2 + 1)) {
            final Edge kEdge4 = graph.edges().get(i2);
            final int v3 = kEdge4.vA().index();
            final int v4 = kEdge4.vB().index();
            leafBitset.clear(v3);
            leafBitset.clear(v4);
        }
        if (caterpillarTreeFlag && returnMaxPathSize && length == 0) {
            if (pathLength == caterpillarBackbone.cardinality()) {
                return pathLength;
            }
        }
        else {
            if (!caterpillarTreeFlag && returnMaxPathSize && length == 0) {
                return pathLength;
            }
            if (caterpillarTreeFlag && returnLeafSize && length == 0) {
                if (pathLength == caterpillarBackbone.cardinality()) {
                    return leafBitset.cardinality();
                }
            }
            else {
                if (!caterpillarTreeFlag && returnLeafSize && length == 0) {
                    return leafBitset.cardinality();
                }
                if (caterpillarTreeFlag && returnLeafSize && length != 0) {
                    if (pathLength == caterpillarBackbone.cardinality() && pathLength == length) {
                        return leafBitset.cardinality();
                    }
                }
                else if (returnLeafSize && length != 0) {
                    return leafBitset.cardinality();
                }
            }
        }
        return 0;
    }
    
    private int dfsMinPathEdge(final Context context, final Topology graph, final Edge kEdge, final BitSet edgeBitset, final BitSet visitedEdge, final int index, final int presentVertex, final int parent, final int mincomponentsz, final BitSet depthBitset, final int whoSiteId) {
        if (index == mincomponentsz * 2) {
            return index;
        }
        for (int i = edgeBitset.nextSetBit(0); i >= 0; i = edgeBitset.nextSetBit(i + 1)) {
            final Edge nEdge = graph.edges().get(i);
            if (nEdge != kEdge) {
                final int nVA = nEdge.vA().index();
                final int nVB = nEdge.vB().index();
                if (nVA == presentVertex) {
                    visitedEdge.set(i);
                    this.dfsMinPathEdge(context, graph, nEdge, edgeBitset, visitedEdge, index + 1, nVB, nVA, mincomponentsz, depthBitset, whoSiteId);
                }
                else if (nVB == presentVertex) {
                    visitedEdge.set(i);
                    this.dfsMinPathEdge(context, graph, nEdge, edgeBitset, visitedEdge, index + 1, nVA, nVB, mincomponentsz, depthBitset, whoSiteId);
                }
            }
        }
        depthBitset.set(index);
        return index;
    }
    
    private int find(final int position, final int[] parent) {
        final int parentId = parent[position];
        if (parentId == position) {
            return position;
        }
        return this.find(parentId, parent);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 8388608L;
        flags |= SiteType.stateFlags(this.indexType);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
