// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count.component;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.container.other.Dice;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.types.play.RoleType;
import util.Context;

@Hide
public final class CountPips extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction whoFn;
    
    public CountPips(@Opt @Or final RoleType role, @Opt @Or @Name final IntFunction of) {
        this.whoFn = ((of != null) ? of : ((role != null) ? new Id(null, role) : new Id(null, RoleType.Shared)));
    }
    
    @Override
    public int eval(final Context context) {
        final int pid = this.whoFn.eval(context);
        for (int i = 0; i < context.game().handDice().size(); ++i) {
            final Dice dice = context.game().handDice().get(i);
            if (pid == dice.owner()) {
                return context.state().sumDice(i);
            }
        }
        return 0;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public String toString() {
        return "Pips()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0x40L | this.whoFn.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.whoFn.preprocess(game);
    }
}
