// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.requirement;

import annotations.Opt;
import game.Game;
import game.equipment.container.board.Track;
import game.functions.ints.board.Id;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.types.play.RoleType;
import util.Context;

public final class FirstMoveOnTrack extends Effect
{
    private static final long serialVersionUID = 1L;
    private final Moves moves;
    private final String trackName;
    private final RoleType owner;
    
    public FirstMoveOnTrack(@Opt final String trackName, @Opt final RoleType owner, final Moves moves, @Opt final Then then) {
        super(then);
        this.moves = moves;
        this.trackName = trackName;
        this.owner = owner;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves returnMoves = new BaseMoves(super.then());
        final int who = (this.owner == null) ? -1 : new Id(null, this.owner).eval(context);
        Track track = null;
        for (final Track t : context.tracks()) {
            if (this.trackName == null || (who == -1 && t.name().equals(this.trackName)) || (who != -1 && t.owner() == who && t.name().contains(this.trackName))) {
                track = t;
                break;
            }
        }
        if (track == null) {
            return this.moves;
        }
        for (int i = 0; i < track.elems().length; ++i) {
            final int site = track.elems()[i].site;
            if (site >= 0) {
                context.setValue("site", site);
                final Moves movesComputed = this.moves.eval(context);
                if (!movesComputed.moves().isEmpty()) {
                    returnMoves.moves().addAll(movesComputed.moves());
                    break;
                }
            }
        }
        if (this.then() != null) {
            for (int j = 0; j < this.moves.moves().size(); ++j) {
                this.moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return returnMoves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long gameFlags = this.moves.gameFlags(game) | super.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        final boolean isStatic = this.moves.isStatic();
        return isStatic;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.moves.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "FirstMoveOnTrack";
    }
}
