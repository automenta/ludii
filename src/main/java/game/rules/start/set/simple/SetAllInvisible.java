// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start.set.simple;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.rules.start.StartRule;
import game.types.board.SiteType;
import util.Context;
import util.Move;
import util.action.BaseAction;
import util.action.hidden.ActionSetInvisible;

@Hide
public final class SetAllInvisible extends StartRule
{
    private static final long serialVersionUID = 1L;
    protected SiteType type;
    
    public SetAllInvisible(@Opt final SiteType type) {
        this.type = type;
    }
    
    @Override
    public void eval(final Context context) {
        final int numPlayers = context.game().players().count();
        for (int idCont = 0; idCont < context.game().equipment().containers().length; ++idCont) {
            for (int numSite = context.game().equipment().containers()[idCont].numSites(), index = 0; index < numSite; ++index) {
                for (int idPlayer = 1; idPlayer <= numPlayers; ++idPlayer) {
                    final BaseAction actionAtomic = new ActionSetInvisible(this.type, index, -1, idPlayer);
                    actionAtomic.apply(context, true);
                    context.trial().moves().add(new Move(actionAtomic));
                    context.trial().addInitPlacement();
                }
            }
        }
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 8L;
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.type == null) {
            this.type = game.board().defaultSite();
        }
    }
    
    @Override
    public String toString() {
        final String str = "(AllInvisible)";
        return "(AllInvisible)";
    }
}
