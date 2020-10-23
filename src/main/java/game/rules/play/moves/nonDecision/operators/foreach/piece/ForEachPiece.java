// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.operators.foreach.piece;

import annotations.*;
import game.Game;
import game.equipment.component.Component;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.ints.state.Mover;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Then;
import game.rules.play.moves.nonDecision.operator.Operator;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.moves.Player;
import gnu.trove.list.array.TIntArrayList;
import util.ContainerId;
import util.Context;
import util.Move;
import util.locations.Location;
import util.state.containerStackingState.BaseContainerStateStacking;
import util.state.containerState.ContainerState;
import util.state.owned.Owned;

import java.util.Iterator;
import java.util.List;

@Hide
public final class ForEachPiece extends Operator
{
    private static final long serialVersionUID = 1L;
    protected final Moves specificMoves;
    protected final String[] items;
    protected final IntFunction player;
    protected final ContainerId containerId;
    protected final BooleanFunction topFn;
    protected SiteType type;
    protected RoleType role;
    protected int[][] compIndicesPerPlayer;
    
    public ForEachPiece(@Opt @Or final String item, @Opt @Or final String[] items, @Opt @Or2 @Name final IntFunction container, @Opt @Or2 final String containerName, @Opt final Moves specificMoves, @Opt @Or2 final Player player, @Opt @Or2 final RoleType role, @Opt @Name final BooleanFunction top, @Opt final SiteType type, @Opt final Then then) {
        super(then);
        if (items != null) {
            this.items = items;
        }
        else {
            this.items = ((item == null) ? new String[0] : new String[] { item });
        }
        this.specificMoves = specificMoves;
        this.player = ((player == null) ? ((role == null) ? new Mover() : new Id(null, role)) : player.index());
        this.containerId = new ContainerId(container, containerName, null, null, null);
        this.topFn = ((top == null) ? BooleanConstant.construct(false) : top);
        this.type = type;
        this.role = role;
    }
    
    @Override
    public Iterator<Move> movesIterator(final Context context) {
        return new Iterator<>() {
            private final int specificPlayer = ForEachPiece.this.player.eval(context);
            private final Owned owned = context.state().owned();
            private final List<? extends Location>[] ownedComponents = this.owned.positions(this.specificPlayer);
            private final Component[] components = context.components();
            private final int cont = ForEachPiece.this.containerId.eval(context);
            private final ContainerState cs = context.containerState(this.cont);
            private final int minIndex = context.game().equipment().sitesFrom()[this.cont];
            private final int maxIndex = this.minIndex + context.containers()[this.cont].numSites();
            private final boolean top = ForEachPiece.this.topFn.eval(context);
            private final int[] moverCompIndices = ForEachPiece.this.compIndicesPerPlayer[this.specificPlayer];
            private int compIdx = 0;
            private int locIdx = 0;
            private int moveIdx = 0;
            private Moves pieceMoves = null;
            private Move nextMove = context.trial().over() ? null : this.computeNextMove();

            @Override
            public boolean hasNext() {
                return this.nextMove != null;
            }

            @Override
            public Move next() {
                final Move ret = this.nextMove;
                if (ForEachPiece.this.then() != null) {
                    ret.then().add(ForEachPiece.this.then().moves());
                }
                this.nextMove = this.computeNextMove();
                ret.setMover(this.specificPlayer);
                return ret;
            }

            private Move computeNextMove() {
                while (true) {
                    if (this.pieceMoves != null) {
                        if (this.moveIdx < this.pieceMoves.moves().size()) {
                            return this.pieceMoves.moves().get(this.moveIdx++);
                        }
                        this.pieceMoves = null;
                    } else {
                        if (this.compIdx >= this.moverCompIndices.length) {
                            return null;
                        }
                        final int componentId = this.moverCompIndices[this.compIdx];
                        List<? extends Location> positions = null;
                        if (ForEachPiece.this.role != RoleType.All) {
                            positions = this.ownedComponents[this.owned.mapCompIndex(this.specificPlayer, componentId)];
                        } else {
                            final int ownerComponent = context.components()[componentId].owner();
                            final List<? extends Location>[] ownedCurrentComponent = this.owned.positions(ownerComponent);
                            positions = ownedCurrentComponent[this.owned.mapCompIndex(ownerComponent, componentId)];
                        }
                        if (positions != null && !positions.isEmpty()) {
                            final Component component = this.components[componentId];
                            if (this.locIdx < positions.size()) {
                                final int location = positions.get(this.locIdx).site();
                                if (location >= this.minIndex && location < this.maxIndex) {
                                    final int level = positions.get(this.locIdx).level();
                                    this.moveIdx = 0;
                                    ++this.locIdx;
                                    if (this.top) {
                                        final BaseContainerStateStacking css = (BaseContainerStateStacking) this.cs;
                                        if (css.sizeStack(location, ForEachPiece.this.type) != level + 1) {
                                            continue;
                                        }
                                    }
                                    final int origFrom = context.from();
                                    final int origLevel = context.level();
                                    context.setFrom(location);
                                    context.setLevel(level);
                                    if (ForEachPiece.this.specificMoves == null) {
                                        if (this.specificPlayer == context.state().mover()) {
                                            this.pieceMoves = component.generate(context);
                                        } else {
                                            final Context newContext = new Context(context);
                                            newContext.state().setPrev(context.state().mover());
                                            newContext.state().setMover(this.specificPlayer);
                                            newContext.state().setNext(context.state().mover());
                                            this.pieceMoves = component.generate(newContext);
                                        }
                                    } else if (this.specificPlayer == context.state().mover()) {
                                        this.pieceMoves = ForEachPiece.this.specificMoves.eval(context);
                                    } else {
                                        final Context newContext = new Context(context);
                                        newContext.state().setPrev(context.state().mover());
                                        newContext.state().setMover(this.specificPlayer);
                                        newContext.state().setNext(context.state().mover());
                                        this.pieceMoves = ForEachPiece.this.specificMoves.eval(newContext);
                                    }
                                    context.setFrom(origFrom);
                                    context.setLevel(origLevel);
                                } else {
                                    this.moveIdx = 0;
                                    ++this.locIdx;
                                }
                            } else {
                                this.moveIdx = 0;
                                this.locIdx = 0;
                                ++this.compIdx;
                            }
                        } else {
                            this.moveIdx = 0;
                            this.locIdx = 0;
                            ++this.compIdx;
                        }
                    }
                }
            }
        };
    }
    
    @Override
    public Moves eval(final Context context) {
        final Iterator<Move> it = this.movesIterator(context);
        final Moves moves = new BaseMoves(super.then());
        while (it.hasNext()) {
            moves.moves().add(it.next());
        }
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.player.gameFlags(game) | super.gameFlags(game);
        gameFlags |= this.topFn.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.type);
        if (this.specificMoves != null) {
            gameFlags |= this.specificMoves.gameFlags(game);
        }
        else {
            final Component[] components = game.equipment().components();
            for (int e = 1; e < components.length; ++e) {
                final Moves generator = components[e].generator();
                if (generator != null) {
                    gameFlags |= generator.gameFlags(game);
                }
            }
        }
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        boolean isStatic = this.player.isStatic();
        isStatic = (isStatic && this.topFn.isStatic());
        return this.specificMoves != null && isStatic && this.specificMoves.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.type = SiteType.use(this.type, game);
        if (this.specificMoves != null) {
            this.specificMoves.preprocess(game);
        }
        final Component[] comps = game.equipment().components();
        for (int e = 1; e < comps.length; ++e) {
            final Component comp = comps[e];
            if (comp.generator() != null) {
                comp.generator().preprocess(game);
            }
        }
        final boolean allPlayers = this.role == RoleType.All;
        this.compIndicesPerPlayer = new int[game.players().size() + 1][];
        for (int p = 0; p <= game.players().size(); ++p) {
            final TIntArrayList compIndices = new TIntArrayList();
            for (int e2 = 1; e2 < comps.length; ++e2) {
                final Component comp2 = comps[e2];
                if (comp2.owner() == p || (allPlayers && p == game.players().size())) {
                    if (this.items.length == 0) {
                        compIndices.add(e2);
                    }
                    else {
                        for (final String item : this.items) {
                            if (comp2.getNameWithoutNumber() != null && comp2.getNameWithoutNumber().equals(item)) {
                                compIndices.add(e2);
                                break;
                            }
                        }
                    }
                }
            }
            this.compIndicesPerPlayer[p] = compIndices.toArray();
        }
    }
    
    public Moves specificMoves() {
        return this.specificMoves;
    }
    
    public String[] items() {
        return this.items;
    }
    
    @Override
    public String toEnglish() {
        return "ByPiece";
    }
}
