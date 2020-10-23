// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count.steps;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Step;
import game.types.board.RelationType;
import game.types.board.SiteType;
import game.util.moves.From;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.Move;

@Hide
public final class CountSteps extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private SiteType type;
    private final RelationType relation;
    private final IntFunction site1Fn;
    private final IntFunction site2Fn;
    private final Step stepMove;
    
    public CountSteps(@Opt final SiteType type, @Opt final RelationType relation, @Opt final Step stepMove, final IntFunction site1, final IntFunction site2) {
        this.type = type;
        this.site1Fn = site1;
        this.site2Fn = site2;
        this.relation = ((relation == null) ? RelationType.Adjacent : relation);
        this.stepMove = stepMove;
    }
    
    @Override
    public int eval(final Context context) {
        final SiteType realType = (this.type == null) ? context.board().defaultSite() : this.type;
        final int site1 = this.site1Fn.eval(context);
        final int site2 = this.site2Fn.eval(context);
        if (site1 < 0 || site2 < 0) {
            return 0;
        }
        if (this.stepMove == null) {
            return context.board().topology().distancesToOtherSite(realType)[site1][site2];
        }
        if (site1 == site2) {
            return 0;
        }
        int numSteps = 1;
        final Moves moves = this.stepMove.eval(context);
        final TIntArrayList currList = new TIntArrayList();
        for (final Move m : moves.moves()) {
            final int to = m.toNonDecision();
            if (!currList.contains(to)) {
                currList.add(to);
            }
        }
        final TIntArrayList nextList = new TIntArrayList();
        final TIntArrayList sitesChecked = new TIntArrayList();
        sitesChecked.add(site1);
        sitesChecked.addAll(currList);
        while (!currList.isEmpty() && !currList.contains(site2)) {
            for (int i = 0; i < currList.size(); ++i) {
                final int newSite = currList.get(i);
                final Moves newStep = new Step(new From(realType, null, new IntConstant(newSite), null, null), this.stepMove.directions(), this.stepMove.toRule(), null, null);
                final Moves newStepMoves = newStep.eval(context);
                for (final Move j : newStepMoves.moves()) {
                    final int to2 = j.toNonDecision();
                    if (!sitesChecked.contains(to2) && !nextList.contains(to2)) {
                        nextList.add(to2);
                    }
                }
            }
            sitesChecked.addAll(currList);
            currList.clear();
            currList.addAll(nextList);
            nextList.clear();
            ++numSteps;
        }
        return numSteps;
    }
    
    @Override
    public boolean isStatic() {
        return this.stepMove == null && this.site1Fn.isStatic() && this.site2Fn.isStatic();
    }
    
    @Override
    public String toString() {
        return "CountSteps()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.site1Fn.gameFlags(game) | this.site2Fn.gameFlags(game);
        if (this.stepMove == null) {
            switch (this.relation) {
                case Adjacent: {
                    gameFlags |= 0x400000000L;
                    break;
                }
                case All: {
                    gameFlags |= 0x4000000000L;
                    break;
                }
                case Diagonal: {
                    gameFlags |= 0x1000000000L;
                    break;
                }
                case OffDiagonal: {
                    gameFlags |= 0x2000000000L;
                    break;
                }
                case Orthogonal: {
                    gameFlags |= 0x800000000L;
                    break;
                }
            }
        }
        else {
            gameFlags |= this.stepMove.gameFlags(game);
        }
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.site1Fn.preprocess(game);
        this.site2Fn.preprocess(game);
    }
}
