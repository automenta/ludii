// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Or;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.start.deductionPuzzle.Set;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.Move;
import util.MoveUtilities;
import util.action.puzzle.ActionSet;
import util.action.puzzle.ActionToggle;
import util.state.puzzleState.ContainerDeductionPuzzleState;

public class Satisfy extends Effect
{
    private static final long serialVersionUID = 1L;
    protected final BooleanFunction[] constraints;
    
    public Satisfy(@Or final BooleanFunction constraint, @Or final BooleanFunction[] constraints) {
        super(null);
        int numNonNull = 0;
        if (constraint != null) {
            ++numNonNull;
        }
        if (constraints != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Exactly one Or parameter must be non-null.");
        }
        if (constraints != null) {
            this.constraints = constraints;
        }
        else {
            (this.constraints = new BooleanFunction[1])[0] = constraint;
        }
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final SiteType type = context.board().defaultSite();
        final int min = context.board().getRange(type).min();
        final int max = context.board().getRange(type).max();
        final TIntArrayList varsConstraints = context.game().constraintVariables();
        final TIntArrayList initLoc = new TIntArrayList();
        if (context.game().rules().start() != null && context.game().rules().start().rules()[0].isSet()) {
            final Set startRule = (Set)context.game().rules().start().rules()[0];
            final Integer[] vars;
            final Integer[] init = vars = startRule.vars();
            for (final Integer in : vars) {
                initLoc.add(in);
            }
        }
        final TIntArrayList sites = new TIntArrayList();
        for (int i = 0; i < varsConstraints.size(); ++i) {
            final int var = varsConstraints.getQuick(i);
            if (!initLoc.contains(var)) {
                sites.add(var);
            }
        }
        for (int i = 0; i < sites.size(); ++i) {
            final int site = sites.getQuick(i);
            for (int index = min; index <= max; ++index) {
                final ActionSet actionSet = new ActionSet(type, site, index);
                actionSet.setDecision(true);
                final Move moveSet = new Move(actionSet);
                moveSet.setFromNonDecision(site);
                moveSet.setToNonDecision(site);
                final Context newContext = new Context(context);
                newContext.game().apply(newContext, moveSet);
                boolean constraintOK = true;
                if (this.constraints != null) {
                    for (final BooleanFunction constraint : this.constraints) {
                        if (!constraint.eval(newContext)) {
                            constraintOK = false;
                            break;
                        }
                    }
                }
                if (constraintOK) {
                    final int saveFrom = context.from();
                    final int saveTo = context.to();
                    context.setFrom(site);
                    context.setTo(-1);
                    MoveUtilities.chainRuleCrossProduct(context, moves, null, moveSet, false);
                    context.setTo(saveTo);
                    context.setFrom(saveFrom);
                }
                final ActionToggle actionToggle = new ActionToggle(type, site, index);
                actionToggle.setDecision(true);
                final Move moveToggle = new Move(actionToggle);
                moveToggle.setFromNonDecision(site);
                moveToggle.setToNonDecision(site);
                final ContainerDeductionPuzzleState ps = (ContainerDeductionPuzzleState)context.state().containerStates()[0];
                if (!ps.isResolved(site, type) || !ps.bit(site, index, type)) {
                    final int saveFrom2 = context.from();
                    final int saveTo2 = context.to();
                    context.setFrom(site);
                    context.setTo(-1);
                    MoveUtilities.chainRuleCrossProduct(context, moves, null, moveToggle, false);
                    context.setTo(saveTo2);
                    context.setFrom(saveFrom2);
                }
            }
        }
        for (final Move m : moves.moves()) {
            m.setMover(1);
        }
        return moves;
    }
    
    public BooleanFunction[] constraints() {
        return this.constraints;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game);
        for (final BooleanFunction constraint : this.constraints) {
            gameFlags |= constraint.gameFlags(game);
        }
        return gameFlags | 0x80L;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        for (final BooleanFunction constraint : this.constraints) {
            constraint.preprocess(game);
        }
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public boolean isConstraintsMoves() {
        return true;
    }
}
