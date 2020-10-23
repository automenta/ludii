// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.operators.foreach.die;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import collections.FastArrayList;
import game.Game;
import game.equipment.container.other.Dice;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Then;
import game.rules.play.moves.nonDecision.operator.Operator;
import util.Context;
import util.Move;
import util.action.die.ActionUpdateDice;
import util.action.die.ActionUseDie;
import util.action.state.ActionSetTemp;

@Hide
public final class ForEachDie extends Operator
{
    private static final long serialVersionUID = 1L;
    private final IntFunction handDiceIndexFn;
    private final BooleanFunction combined;
    private final BooleanFunction replayDoubleFn;
    private final BooleanFunction rule;
    private final Moves moves;
    
    public ForEachDie(@Opt final IntFunction handDiceIndex, @Opt @Name final BooleanFunction combined, @Opt @Name final BooleanFunction replayDouble, @Opt @Name final BooleanFunction If, final Moves moves, @Opt final Then then) {
        super(then);
        this.handDiceIndexFn = ((handDiceIndex == null) ? new IntConstant(0) : handDiceIndex);
        this.rule = ((If == null) ? BooleanConstant.construct(true) : If);
        this.combined = ((combined == null) ? BooleanConstant.construct(false) : combined);
        this.replayDoubleFn = ((replayDouble == null) ? BooleanConstant.construct(false) : replayDouble);
        this.moves = moves;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves returnMoves = new BaseMoves(super.then());
        final int handDiceIndex = this.handDiceIndexFn.eval(context);
        if (context.state().currentDice() == null) {
            return returnMoves;
        }
        final int[] dieValues = context.state().currentDice(handDiceIndex);
        final int containerIndex = context.game().getHandDice(handDiceIndex).index();
        boolean replayDouble = this.replayDoubleFn.eval(context);
        if (replayDouble) {
            final int firstDieValue = dieValues[0];
            for (int dieValue : dieValues) {
                if (dieValue != firstDieValue) {
                    replayDouble = false;
                    break;
                }
            }
        }
        final int origDieValue = context.pipCount();
        for (int i = 0; i < dieValues.length; ++i) {
            final int pipCount = dieValues[i];
            context.setPipCount(pipCount);
            if (this.rule.eval(context)) {
                final Moves computedMoves = this.moves.eval(context);
                final FastArrayList<Move> moveList = computedMoves.moves();
                final int site = context.sitesFrom()[containerIndex] + i;
                final ActionUseDie action = new ActionUseDie(handDiceIndex, i, site);
                for (final Move m : moveList) {
                    m.actions().add(action);
                }
                if (replayDouble && context.state().temp() == -1) {
                    final ActionSetTemp setTemp = new ActionSetTemp(pipCount);
                    for (final Move j : moveList) {
                        j.actions().add(setTemp);
                    }
                }
                else if (replayDouble) {
                    final ActionSetTemp setTemp = new ActionSetTemp(-1);
                    for (final Move j : moveList) {
                        j.actions().add(setTemp);
                    }
                }
                else if (context.state().temp() != -1) {
                    for (final Dice dice : context.game().handDice()) {
                        if (context.state().temp() - 1 < dice.getNumFaces()) {
                            for (int loc = context.sitesFrom()[dice.index()]; loc < context.sitesFrom()[dice.index()] + dice.numLocs(); ++loc) {
                                final ActionUpdateDice actionState = new ActionUpdateDice(loc, context.state().temp() - 1);
                                for (final Move k : moveList) {
                                    k.actions().add(actionState);
                                }
                            }
                        }
                    }
                }
                returnMoves.moves().addAll(computedMoves.moves());
            }
        }
        if (this.combined.eval(context)) {
            if (dieValues.length == 2) {
                final int dieValue1 = dieValues[0];
                final int dieValue2 = dieValues[1];
                if (dieValue1 != 0 && dieValue2 != 0) {
                    context.setPipCount(dieValue1 + dieValue2);
                    if (this.rule.eval(context)) {
                        final Moves computedMoves = this.moves.eval(context);
                        final FastArrayList<Move> moveList = computedMoves.moves();
                        final int siteFrom = context.sitesFrom()[containerIndex];
                        final ActionUseDie actionDie1 = new ActionUseDie(handDiceIndex, 0, siteFrom);
                        final ActionUseDie actionDie2 = new ActionUseDie(handDiceIndex, 1, siteFrom + 1);
                        for (final Move j : moveList) {
                            j.actions().add(actionDie1);
                            j.actions().add(actionDie2);
                        }
                        returnMoves.moves().addAll(computedMoves.moves());
                    }
                }
            }
            else if (dieValues.length == 3) {
                final int dieValue1 = dieValues[0];
                final int dieValue2 = dieValues[1];
                final int dieValue3 = dieValues[2];
                if (dieValue1 != 0 && dieValue2 != 0 && dieValue3 != 0) {
                    context.setPipCount(dieValue1 + dieValue2 + dieValue3);
                    if (this.rule.eval(context)) {
                        final Moves computedMoves2 = this.moves.eval(context);
                        final FastArrayList<Move> moveList2 = computedMoves2.moves();
                        final int siteFrom2 = context.sitesFrom()[containerIndex];
                        final ActionUseDie actionDie3 = new ActionUseDie(handDiceIndex, 0, siteFrom2);
                        final ActionUseDie actionDie4 = new ActionUseDie(handDiceIndex, 1, siteFrom2 + 1);
                        final ActionUseDie actionDie5 = new ActionUseDie(handDiceIndex, 2, siteFrom2 + 2);
                        for (final Move l : moveList2) {
                            l.actions().add(actionDie3);
                            l.actions().add(actionDie4);
                            l.actions().add(actionDie5);
                        }
                        returnMoves.moves().addAll(computedMoves2.moves());
                    }
                }
                for (int i2 = 0; i2 < 2; ++i2) {
                    for (int j2 = i2 + 1; j2 < 3; ++j2) {
                        final int d1 = dieValues[i2];
                        final int d2 = dieValues[j2];
                        if (d1 != 0 && d2 != 0) {
                            context.setPipCount(d1 + d2);
                            if (this.rule.eval(context)) {
                                final Moves computedMoves3 = this.moves.eval(context);
                                final FastArrayList<Move> moveList3 = computedMoves3.moves();
                                final int siteFrom3 = context.sitesFrom()[containerIndex];
                                final ActionUseDie actionDie6 = new ActionUseDie(handDiceIndex, i2, siteFrom3 + i2);
                                final ActionUseDie actionDie7 = new ActionUseDie(handDiceIndex, j2, siteFrom3 + j2);
                                for (final Move m2 : moveList3) {
                                    m2.actions().add(actionDie6);
                                    m2.actions().add(actionDie7);
                                }
                                returnMoves.moves().addAll(computedMoves3.moves());
                            }
                        }
                    }
                }
            }
        }
        context.setPipCount(origDieValue);
        if (this.then() != null) {
            for (int j3 = 0; j3 < returnMoves.moves().size(); ++j3) {
                returnMoves.moves().get(j3).then().add(this.then().moves());
            }
        }
        return returnMoves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.handDiceIndexFn.gameFlags(game) | super.gameFlags(game);
        gameFlags |= this.rule.gameFlags(game);
        gameFlags |= this.moves.gameFlags(game);
        gameFlags |= this.replayDoubleFn.gameFlags(game);
        gameFlags |= this.combined.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return this.handDiceIndexFn.isStatic() && this.rule.isStatic() && this.moves.isStatic() && this.replayDoubleFn.isStatic() && this.combined.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.handDiceIndexFn.preprocess(game);
        this.rule.preprocess(game);
        this.moves.preprocess(game);
        this.replayDoubleFn.gameFlags(game);
        this.combined.gameFlags(game);
    }
    
    @Override
    public String toEnglish() {
        return "ForEachDie";
    }
}
