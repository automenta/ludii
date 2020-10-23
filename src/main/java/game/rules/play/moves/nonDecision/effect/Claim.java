// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.state.Mover;
import game.functions.region.RegionFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.util.equipment.Region;
import game.util.moves.Piece;
import game.util.moves.To;
import util.Context;
import util.Move;
import util.action.Action;
import util.action.move.ActionAdd;

import java.util.Arrays;

public final class Claim extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction localState;
    private final IntFunction[] components;
    private final RegionFunction region;
    private final IntFunction site;
    private final BooleanFunction test;
    private SiteType type;
    private Move[][][][] actionCache;
    private boolean allowCacheUse;
    
    public Claim(@Opt final Piece what, final To to, @Opt final Then then) {
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
        this.type = to.type();
    }
    
    @Override
    public Moves eval(final Context context) {
        final BaseMoves moves = new BaseMoves(super.then());
        final int origFrom = context.from();
        final int origTo = context.to();
        final int mover = context.state().mover();
        for (final IntFunction compomentFn : this.components) {
            final int componentId = compomentFn.eval(context);
            if (this.site != null) {
                final int siteEval = this.site.eval(context);
                context.setTo(siteEval);
                if (this.test == null || this.test.eval(context)) {
                    final int state = (this.localState == null) ? -1 : this.localState.eval(context);
                    final Action actionAdd = new ActionAdd(this.type, siteEval, componentId, 1, state, -1, null, null, null);
                    if (this.isDecision()) {
                        actionAdd.setDecision(true);
                    }
                    final Move action = new Move(actionAdd);
                    if (this.type.equals(SiteType.Edge)) {
                        action.setFromNonDecision(siteEval);
                        action.setToNonDecision(siteEval);
                        action.setEdgeMove(siteEval);
                        action.setOrientedMove(false);
                    }
                    else {
                        action.setFromNonDecision(siteEval);
                        action.setToNonDecision(siteEval);
                    }
                    if (this.then() != null) {
                        final int fromOrigCsq = context.from();
                        final int toOrigCsq = context.to();
                        context.setFrom(action.fromNonDecision());
                        context.setTo(action.toNonDecision());
                        final Moves m = this.then().moves().eval(context);
                        context.setFrom(fromOrigCsq);
                        context.setTo(toOrigCsq);
                        for (final Move mCsq : m.moves()) {
                            for (final Action a : mCsq.actions()) {
                                action.actions().add(a);
                            }
                        }
                    }
                    moves.moves().add(action);
                    context.setFrom(origFrom);
                    context.setTo(origTo);
                    return moves;
                }
            }
            final Move[][] compActionCache = this.actionCache[mover][componentId];
            final Region sites = this.region.eval(context);
            for (int toSite = sites.bitSet().nextSetBit(0); toSite >= 0; toSite = sites.bitSet().nextSetBit(toSite + 1)) {
                context.setTo(toSite);
                if (this.test == null || this.test.eval(context)) {
                    final int state2 = (this.localState == null) ? -1 : this.localState.eval(context);
                    Move action2;
                    if (compActionCache[state2 + 1][toSite] == null) {
                        final Action actionAdd2 = new ActionAdd(this.type, toSite, componentId, 1, state2, -1, null, null, null);
                        if (this.isDecision()) {
                            actionAdd2.setDecision(true);
                        }
                        action2 = new Move(actionAdd2);
                        if (this.type.equals(SiteType.Edge)) {
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
        context.setTo(origTo);
        context.setFrom(origFrom);
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game);
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
        return (this.region == null || this.region.isStatic()) && (this.test == null || this.test.isStatic()) && (this.test == null || this.test.isStatic()) && (this.localState == null || this.localState.isStatic()) && (this.site == null || this.site.isStatic());
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
        if (this.type.equals(SiteType.Cell)) {
            this.actionCache = new Move[game.players().count() + 1][][][];
            for (int p = 1; p < this.actionCache.length; ++p) {
                this.actionCache[p] = new Move[game.numComponents() + 1][maxNumStates + 2][game.equipment().totalDefaultSites()];
            }
        }
        else if (this.type.equals(SiteType.Edge)) {
            this.actionCache = new Move[game.players().count() + 1][][][];
            for (int p = 1; p < this.actionCache.length; ++p) {
                this.actionCache[p] = new Move[game.players().count() + 1][maxNumStates + 2][game.board().topology().edges().size()];
            }
        }
        else if (this.type.equals(SiteType.Vertex)) {
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
    
    public SiteType type() {
        return this.type;
    }
    
    public void disableActionCache() {
        this.allowCacheUse = false;
    }
    
    @Override
    public String toString() {
        if (this.components.length == 1) {
            return "[Colour: " + this.components[0] + ", " + this.region + ", " + this.site + ", " + this.then() + "]";
        }
        return "[Colour: " + Arrays.toString(this.components) + ", " + this.region + ", " + this.site + ", " + this.then() + "]";
    }
}
