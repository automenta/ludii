// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start.set.players;

import annotations.Hide;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.rules.start.StartRule;
import game.types.play.RoleType;
import util.Context;
import util.Move;
import util.action.state.ActionAddPlayerToTeam;

@Hide
public final class SetTeam extends StartRule
{
    private static final long serialVersionUID = 1L;
    final IntFunction team;
    final IntFunction[] players;
    
    public SetTeam(final IntFunction team, final RoleType[] roles) {
        this.team = team;
        this.players = new IntFunction[roles.length];
        for (int i = 0; i < roles.length; ++i) {
            final RoleType role = roles[i];
            this.players[i] = new Id(null, role);
        }
    }
    
    @Override
    public void eval(final Context context) {
        final int teamId = this.team.eval(context);
        for (final IntFunction player : this.players) {
            final ActionAddPlayerToTeam actionTeam = new ActionAddPlayerToTeam(teamId, player.eval(context));
            actionTeam.apply(context, true);
            context.trial().moves().add(new Move(actionTeam));
            context.trial().addInitPlacement();
        }
    }
    
    @Override
    public boolean isStatic() {
        for (final IntFunction player : this.players) {
            if (!player.isStatic()) {
                return false;
            }
        }
        return this.team.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.team.gameFlags(game) | 0x40000L;
        for (final IntFunction player : this.players) {
            gameFlags |= player.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.team.preprocess(game);
        for (final IntFunction player : this.players) {
            player.preprocess(game);
        }
    }
    
    @Override
    public String toString() {
        final String str = "(SetTeam)";
        return "(SetTeam)";
    }
}
