// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.operators.foreach.player;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
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
public final class ForEachPlayer extends Operator
{
    private static final long serialVersionUID = 1L;
    protected final Moves specificMoves;
    protected final IntFunction player;
    protected final ContainerId containerId;
    protected final BooleanFunction topFn;
    SiteType type;
    protected int[][] compIndicesPerPlayer;
    
    public ForEachPlayer(@Opt @Name @Or final IntFunction container, @Opt @Or final String containerName, @Opt final Moves specificMoves, @Opt @Or final RoleType role, @Opt @Or final Player player, @Opt @Name final BooleanFunction top, @Opt final SiteType type, @Opt final Then then) {
        super(then);
        this.specificMoves = specificMoves;
        this.player = ((player == null && role == null) ? new Mover() : ((role != null) ? new Id(null, role) : player.index()));
        this.containerId = new ContainerId(container, containerName, null, null, null);
        this.topFn = ((top == null) ? BooleanConstant.construct(false) : top);
        this.type = type;
    }
    
    @Override
    public Iterator<Move> movesIterator(final Context context) {
        return new Iterator<>() {
            private final int specificPlayer = ForEachPlayer.this.player.eval(context);
            private final Owned owned = context.state().owned();
            private final List<? extends Location>[] ownedComponents = this.owned.positions(this.specificPlayer);
            private final Component[] components = context.components();
            private final int cont = ForEachPlayer.this.containerId.eval(context);
            private final ContainerState cs = context.containerState(this.cont);
            private final int minIndex = context.game().equipment().sitesFrom()[this.cont];
            private final int maxIndex = this.minIndex + context.containers()[this.cont].numSites();
            private final boolean top = ForEachPlayer.this.topFn.eval(context);
            private final int[] moverCompIndices = ForEachPlayer.this.compIndicesPerPlayer[this.specificPlayer];
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
                if (ForEachPlayer.this.then() != null) {
                    ret.then().add(ForEachPlayer.this.then().moves());
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
                        final List<? extends Location> positions = this.ownedComponents[this.owned.mapCompIndex(this.specificPlayer, componentId)];
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
                                        if (css.sizeStack(location, ForEachPlayer.this.type) != level + 1) {
                                            continue;
                                        }
                                    }
                                    final int origFrom = context.from();
                                    final int origLevel = context.level();
                                    context.setFrom(location);
                                    context.setLevel(level);
                                    if (ForEachPlayer.this.specificMoves == null) {
                                        if (this.specificPlayer == context.state().mover()) {
                                            final int owner = component.owner();
                                            if (owner < 1) {
                                                continue;
                                            }
                                            if (owner > context.game().players().count()) {
                                                continue;
                                            }
                                            this.pieceMoves = context.players().get(owner).generate(context);
                                        } else {
                                            final Context newContext = new Context(context);
                                            newContext.state().setPrev(context.state().mover());
                                            newContext.state().setMover(this.specificPlayer);
                                            newContext.state().setNext(context.state().mover());
                                            final int owner2 = component.owner();
                                            if (owner2 < 1) {
                                                continue;
                                            }
                                            if (owner2 > context.game().players().count()) {
                                                continue;
                                            }
                                            this.pieceMoves = context.players().get(owner2).generate(context);
                                        }
                                    } else if (this.specificPlayer == context.state().mover()) {
                                        this.pieceMoves = ForEachPlayer.this.specificMoves.eval(context);
                                    } else {
                                        final Context newContext = new Context(context);
                                        newContext.state().setPrev(context.state().mover());
                                        newContext.state().setMover(this.specificPlayer);
                                        newContext.state().setNext(context.state().mover());
                                        this.pieceMoves = ForEachPlayer.this.specificMoves.eval(newContext);
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
            return gameFlags |= this.specificMoves.gameFlags(game);
        }
        final Component[] components = game.equipment().components();
        for (int e = 1; e < components.length; ++e) {
            final Moves generator = components[e].generator();
            if (generator != null) {
                gameFlags |= generator.gameFlags(game);
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
        for (final game.players.Player p : game.players().players()) {
            if (p != null && p.generator() != null) {
                p.generator().preprocess(game);
            }
        }
        final Component[] comps = game.equipment().components();
        this.compIndicesPerPlayer = new int[game.players().count() + 1][];
        for (int p2 = 1; p2 <= game.players().count(); ++p2) {
            final TIntArrayList compIndices = new TIntArrayList();
            for (int e = 1; e < comps.length; ++e) {
                final Component comp = comps[e];
                if (comp.owner() == p2) {
                    compIndices.add(e);
                }
            }
            this.compIndicesPerPlayer[p2] = compIndices.toArray();
        }
    }
    
    public Moves specificMoves() {
        return this.specificMoves;
    }
    
    @Override
    public String toEnglish() {
        return "ByPlayer";
    }
}
