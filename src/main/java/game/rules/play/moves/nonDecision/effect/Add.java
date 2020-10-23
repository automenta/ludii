// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.equipment.component.Component;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.ints.state.Mover;
import game.functions.region.RegionFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.util.equipment.Region;
import game.util.moves.Piece;
import game.util.moves.To;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.Move;
import util.action.Action;
import util.action.move.ActionAdd;
import util.action.move.ActionInsert;
import util.state.containerState.ContainerState;

import java.util.Arrays;

public final class Add extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction localState;
    private final IntFunction[] components;
    private final RegionFunction region;
    private final IntFunction site;
    private final IntFunction level;
    private final BooleanFunction test;
    private final IntFunction countFn;
    private final boolean onStack;
    private SiteType type;
    private Move[][][][] actionCache;
    private boolean allowCacheUse;
    
    public Add(@Opt final Piece what, final To to, @Opt @Name final IntFunction count, @Opt @Name final Boolean stack, @Opt final Then then) {
        super(then);
        this.actionCache = null;
        this.allowCacheUse = true;
        if (what != null && what.components() == null) {
            if (what.component() == null) {
                this.components = new IntFunction[] { new Mover() };
            }
            else {
                this.components = new IntFunction[] { what.component() };
            }
        }
        else {
            this.components = ((what == null) ? new IntFunction[] { new Mover() } : what.components());
        }
        this.localState = ((what == null) ? null : ((what.state() == null) ? null : what.state()));
        this.site = to.loc();
        this.region = to.region();
        this.test = to.cond();
        this.onStack = (stack != null && stack);
        this.type = to.type();
        this.level = to.level();
        this.countFn = ((count == null) ? new IntConstant(1) : count);
    }
    
    @Override
    public Moves eval(final Context context) {
        final BaseMoves moves = new BaseMoves(super.then());
        final int origFrom = context.from();
        final int origTo = context.to();
        final int mover = context.state().mover();
        final int count = this.countFn.eval(context);
        for (final IntFunction componentFn : this.components) {
            final int componentId = componentFn.eval(context);
            if (componentId != -1) {
                final Component component = context.components()[componentId];
                if (component != null && component.isLargePiece()) {
                    final Moves movesLargePiece = this.evalLargePiece(context, component);
                    moves.moves().addAll(movesLargePiece.moves());
                }
                else {
                    if (this.site != null) {
                        final int siteEval = this.site.eval(context);
                        context.setTo(siteEval);
                        if (this.test == null || this.test.eval(context)) {
                            final int state = (this.localState == null) ? -1 : this.localState.eval(context);
                            final Action actionToAdd = (this.level == null) ? new ActionAdd(this.type, siteEval, componentId, 1, state, -1, null, null, null) : new ActionInsert(this.type, siteEval, this.level.eval(context), componentId, state);
                            if (this.isDecision()) {
                                actionToAdd.setDecision(true);
                            }
                            final Move action = new Move(actionToAdd);
                            for (int remainingCount = count - 1; remainingCount > 0; --remainingCount) {
                                final Action actionToAddAgain = (this.level == null) ? new ActionAdd(this.type, siteEval, componentId, 1, state, -1, null, null, null) : new ActionInsert(this.type, siteEval, this.level.eval(context), componentId, state);
                                action.actions().add(actionToAddAgain);
                            }
                            if (this.type == SiteType.Edge) {
                                action.setFromNonDecision(siteEval);
                                action.setToNonDecision(siteEval);
                                action.setEdgeMove(siteEval);
                                action.setOrientedMove(false);
                            }
                            else {
                                action.setFromNonDecision(siteEval);
                                action.setToNonDecision(siteEval);
                            }
                            moves.moves().add(action);
                            context.setFrom(origFrom);
                            context.setTo(origTo);
                            if (this.then() != null) {
                                for (int j = 0; j < moves.moves().size(); ++j) {
                                    moves.moves().get(j).then().add(this.then().moves());
                                }
                            }
                            return moves;
                        }
                    }
                    final Move[][] compActionCache = this.actionCache[mover][componentId];
                    if (this.region == null) {
                        return moves;
                    }
                    final Region sites = this.region.eval(context);
                    for (int toSite = sites.bitSet().nextSetBit(0); toSite >= 0; toSite = sites.bitSet().nextSetBit(toSite + 1)) {
                        context.setTo(toSite);
                        if (this.test == null || this.test.eval(context)) {
                            final int state2 = (this.localState == null) ? -1 : this.localState.eval(context);
                            Move action2;
                            if (compActionCache[state2 + 1][toSite] == null) {
                                final Action actionToAdd2 = (this.level == null) ? new ActionAdd(this.type, toSite, componentId, 1, state2, -1, null, null, null) : new ActionInsert(this.type, toSite, this.level.eval(context), componentId, state2);
                                actionToAdd2.setDecision(this.isDecision());
                                action2 = new Move(actionToAdd2);
                                for (int remainingCount2 = count - 1; remainingCount2 > 0; --remainingCount2) {
                                    final Action actionToAddAgain2 = (this.level == null) ? new ActionAdd(this.type, toSite, componentId, 1, state2, -1, null, null, null) : new ActionInsert(this.type, toSite, this.level.eval(context), componentId, state2);
                                    action2.actions().add(actionToAddAgain2);
                                }
                                if (this.type == SiteType.Edge) {
                                    action2.setFromNonDecision(toSite);
                                    action2.setToNonDecision(toSite);
                                    action2.setEdgeMove(toSite);
                                    action2.setOrientedMove(false);
                                }
                                else {
                                    action2.setFromNonDecision(toSite);
                                    action2.setToNonDecision(toSite);
                                }
                                if (this.then() != null) {
                                    action2.then().add(this.then().moves());
                                }
                                action2.setMover(mover);
                                if (this.allowCacheUse) {
                                    compActionCache[state2 + 1][toSite] = action2;
                                }
                            }
                            else {
                                action2 = compActionCache[state2 + 1][toSite];
                            }
                            moves.moves().add(action2);
                        }
                    }
                }
            }
        }
        context.setTo(origTo);
        context.setFrom(origFrom);
        return moves;
    }
    
    private Moves evalLargePiece(final Context context, final Component largePiece) {
        final BaseMoves moves = new BaseMoves(super.then());
        final int largePieceId = largePiece.index();
        final int nbPossibleStates = largePiece.walk().length * 4;
        final int localStateToAdd = (this.localState == null) ? -1 : this.localState.eval(context);
        final int mover = context.state().mover();
        if (this.site != null) {
            final int siteEval = this.site.eval(context);
            final ContainerState cs = context.containerState(context.containerId()[siteEval]);
            for (int state = 0; state < nbPossibleStates; ++state) {
                if (localStateToAdd == -1 || localStateToAdd == state) {
                    final TIntArrayList locsLargePiece = largePiece.locs(context, siteEval, state, context.topology());
                    if (locsLargePiece != null) {
                        if (!locsLargePiece.isEmpty()) {
                            boolean valid = true;
                            for (int i = 0; i < locsLargePiece.size(); ++i) {
                                final int siteToCheck = locsLargePiece.get(i);
                                if (!cs.isEmpty(siteToCheck, this.type)) {
                                    valid = false;
                                    break;
                                }
                            }
                            if (valid) {
                                final Action actionAdd = new ActionAdd(this.type, siteEval, largePieceId, 1, state, -1, null, null, null);
                                actionAdd.setDecision(this.isDecision());
                                final Move move = new Move(actionAdd);
                                move.setFromNonDecision(siteEval);
                                move.setToNonDecision(siteEval);
                                move.setMover(mover);
                                move.setStateNonDecision(state);
                                moves.moves().add(move);
                            }
                        }
                    }
                }
            }
            return moves;
        }
        final Region sites = this.region.eval(context);
        for (int toSite = sites.bitSet().nextSetBit(0); toSite >= 0; toSite = sites.bitSet().nextSetBit(toSite + 1)) {
            final ContainerState cs2 = context.containerState(context.containerId()[toSite]);
            for (int state2 = 0; state2 < nbPossibleStates; ++state2) {
                if (localStateToAdd == -1 || localStateToAdd == state2) {
                    final TIntArrayList locsLargePiece2 = largePiece.locs(context, toSite, state2, context.topology());
                    if (locsLargePiece2 != null) {
                        if (!locsLargePiece2.isEmpty()) {
                            boolean valid2 = true;
                            for (int j = 0; j < locsLargePiece2.size(); ++j) {
                                final int siteToCheck2 = locsLargePiece2.get(j);
                                if (!cs2.isEmpty(siteToCheck2, this.type)) {
                                    valid2 = false;
                                    break;
                                }
                            }
                            if (valid2) {
                                final Action actionAdd2 = new ActionAdd(this.type, toSite, largePieceId, 1, state2, -1, null, null, null);
                                actionAdd2.setDecision(this.isDecision());
                                final Move move2 = new Move(actionAdd2);
                                move2.setFromNonDecision(toSite);
                                move2.setToNonDecision(toSite);
                                move2.setMover(mover);
                                move2.setStateNonDecision(state2);
                                moves.moves().add(move2);
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game);
        if (this.onStack) {
            gameFlags |= 0x10L;
        }
        gameFlags |= SiteType.stateFlags(this.type);
        if (this.region != null) {
            gameFlags |= this.region.gameFlags(game);
        }
        if (this.test != null) {
            gameFlags |= this.test.gameFlags(game);
        }
        for (final IntFunction comp : this.components) {
            gameFlags |= comp.gameFlags(game);
        }
        if (this.site != null) {
            gameFlags |= this.site.gameFlags(game);
        }
        if (this.level != null) {
            gameFlags |= this.level.gameFlags(game);
        }
        if (this.then() != null) {
            gameFlags |= this.then().gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        for (final IntFunction comp : this.components) {
            if (!comp.isStatic()) {
                return false;
            }
        }
        return (this.region == null || this.region.isStatic()) && (this.test == null || this.test.isStatic()) && (this.test == null || this.test.isStatic()) && (this.localState == null || this.localState.isStatic()) && (this.site == null || this.site.isStatic()) && (this.level == null || this.level.isStatic());
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.type == null) {
            this.type = game.board().defaultSite();
        }
        super.preprocess(game);
        if (this.region != null) {
            this.region.preprocess(game);
        }
        if (this.test != null) {
            this.test.preprocess(game);
        }
        if (this.localState != null) {
            this.localState.preprocess(game);
        }
        for (final IntFunction comp : this.components) {
            comp.preprocess(game);
        }
        if (this.site != null) {
            this.site.preprocess(game);
        }
        int maxNumStates;
        if (game.requiresLocalState()) {
            maxNumStates = game.maximalLocalStates();
        }
        else {
            maxNumStates = 0;
        }
        if (this.type == SiteType.Cell) {
            this.actionCache = new Move[game.players().count() + 1][][][];
            for (int p = 1; p < this.actionCache.length; ++p) {
                this.actionCache[p] = new Move[game.numComponents() + 1][maxNumStates + 2][game.equipment().totalDefaultSites()];
            }
        }
        else if (this.type == SiteType.Edge) {
            this.actionCache = new Move[game.players().count() + 1][][][];
            for (int p = 1; p < this.actionCache.length; ++p) {
                this.actionCache[p] = new Move[game.players().count() + 1][maxNumStates + 2][game.board().topology().edges().size()];
            }
        }
        else if (this.type == SiteType.Vertex) {
            this.actionCache = new Move[game.players().count() + 1][][][];
            for (int p = 1; p < this.actionCache.length; ++p) {
                this.actionCache[p] = new Move[game.numComponents() + 1][maxNumStates + 2][game.board().topology().vertices().size()];
            }
        }
    }
    
    public IntFunction[] components() {
        return this.components;
    }
    
    public RegionFunction region() {
        return this.region;
    }
    
    public IntFunction site() {
        return this.site;
    }
    
    public BooleanFunction legal() {
        return this.test;
    }
    
    public boolean onStack() {
        return this.onStack;
    }
    
    public SiteType type() {
        return this.type;
    }
    
    public void disableActionCache() {
        this.allowCacheUse = false;
    }
    
    @Override
    public String toString() {
        if (this.components.length == 1) {
            return "[Add: " + this.components[0] + ", " + this.region + ", " + this.site + ", " + this.then() + "]";
        }
        return "[Add: " + Arrays.toString(this.components) + ", " + this.region + ", " + this.site + ", " + this.then() + "]";
    }
}
