// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.operators.foreach.site;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import collections.FastArrayList;
import game.Game;
import game.functions.region.RegionFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.util.equipment.Region;
import util.Context;
import util.Move;

@Hide
public final class ForEachSite extends Effect
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction regionFn;
    private final Moves generator;
    private final Moves elseMoves;
    
    public ForEachSite(final RegionFunction regionFn, final Moves generator, @Opt @Name final Moves noMoveYet, @Opt final Then then) {
        super(then);
        this.regionFn = regionFn;
        this.generator = generator;
        this.elseMoves = noMoveYet;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Region sites = this.regionFn.eval(context);
        final Moves moves = new BaseMoves(super.then());
        final int savedFrom = context.from();
        final int savedTo = context.to();
        for (int site = sites.bitSet().nextSetBit(0); site >= 0; site = sites.bitSet().nextSetBit(site + 1)) {
            context.setFrom(site);
            context.setTo(site);
            context.setValue("site", context.to());
            final FastArrayList<Move> generatedMoves = this.generator.eval(context).moves();
            moves.moves().addAll(generatedMoves);
        }
        if (moves.moves().isEmpty() && this.elseMoves != null) {
            return this.elseMoves.eval(context);
        }
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        context.setFrom(savedFrom);
        context.setTo(savedTo);
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = this.regionFn.gameFlags(game) | this.generator.gameFlags(game) | super.gameFlags(game);
        if (this.elseMoves != null) {
            flags |= this.elseMoves.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.regionFn.preprocess(game);
        this.generator.preprocess(game);
        if (this.elseMoves != null) {
            this.elseMoves.preprocess(game);
        }
    }
    
    @Override
    public String toEnglish() {
        return this.getClass().getSimpleName();
    }
}
