// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.functions.region.sites.Sites;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.types.play.WhenType;
import util.Context;
import util.Move;
import util.action.move.ActionRemove;

public final class Remove extends Effect
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction regionFunction;
    private final IntFunction countFn;
    private SiteType type;
    private final WhenType when;
    
    public Remove(@Opt final SiteType type, @Or final IntFunction locationFunction, @Or final RegionFunction regionFunction, @Opt @Name final WhenType at, @Opt @Name final IntFunction count, @Opt final Then then) {
        super(then);
        int numNonNull = 0;
        if (locationFunction != null) {
            ++numNonNull;
        }
        if (regionFunction != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Remove(): Only one of locationFunction or regionFunction has to be non-null.");
        }
        this.regionFunction = ((regionFunction != null) ? regionFunction : Sites.construct(new IntFunction[] { locationFunction }));
        this.type = type;
        this.when = at;
        this.countFn = ((count == null) ? new IntConstant(1) : count);
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int[] locs = this.regionFunction.eval(context).sites();
        final int count = this.countFn.eval(context);
        for (final int loc : locs) {
            int numToRemove = count;
            if (loc >= 0) {
                if (context.state().containerStates()[(this.type == SiteType.Edge) ? 0 : context.containerId()[loc]].what(loc, this.type) > 0) {
                    final boolean applyNow = this.when != WhenType.EndOfTurn;
                    final ActionRemove actionRemove = new ActionRemove(this.type, loc, applyNow);
                    if (this.isDecision()) {
                        actionRemove.setDecision(true);
                    }
                    final Move move = new Move(actionRemove);
                    --numToRemove;
                    while (numToRemove > 0) {
                        move.actions().add(new ActionRemove(this.type, loc, applyNow));
                        --numToRemove;
                    }
                    move.setMover(context.state().mover());
                    moves.moves().add(move);
                    if (this.then() != null) {
                        move.then().add(this.then().moves());
                    }
                }
            }
        }
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game);
        if (this.then() != null) {
            gameFlags |= this.then().gameFlags(game);
        }
        gameFlags |= SiteType.stateFlags(this.type);
        if (this.when != null) {
            gameFlags |= 0x40008000L;
        }
        return gameFlags | this.regionFunction.gameFlags(game) | this.countFn.gameFlags(game);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        super.preprocess(game);
        this.regionFunction.preprocess(game);
        this.countFn.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "Remove";
    }
}
