// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.operators.foreach.value;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.ints.IntFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import main.collections.FastArrayList;
import util.Context;
import util.Move;

@Hide
public final class ForEachValue extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction minFn;
    private final IntFunction maxFn;
    private final Moves generator;
    
    public ForEachValue(@Name final IntFunction min, @Name final IntFunction max, final Moves generator, @Opt final Then then) {
        super(then);
        this.minFn = min;
        this.maxFn = max;
        this.generator = generator;
    }
    
    @Override
    public Moves eval(final Context context) {
        final int min = this.minFn.eval(context);
        final int max = this.maxFn.eval(context);
        final Moves moves = new BaseMoves(super.then());
        for (int to = min; to <= max; ++to) {
            context.setValue("value", to);
            final FastArrayList<Move> generatedMoves = this.generator.eval(context).moves();
            moves.moves().addAll(generatedMoves);
        }
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long flags = this.minFn.gameFlags(game) | this.maxFn.gameFlags(game) | this.generator.gameFlags(game) | super.gameFlags(game);
        return flags;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.minFn.preprocess(game);
        this.maxFn.preprocess(game);
        this.generator.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return this.getClass().getSimpleName();
    }
}
