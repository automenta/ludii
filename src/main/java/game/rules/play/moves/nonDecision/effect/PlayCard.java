// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import game.Game;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import util.Context;
import util.Move;
import util.action.Action;
import util.action.move.ActionMove;

public final class PlayCard extends Effect
{
    private static final long serialVersionUID = 1L;
    
    public PlayCard(@Opt final Then then) {
        super(then);
    }
    
    @Override
    public Moves eval(final Context context) {
        final BaseMoves moves = new BaseMoves(super.then());
        for (int cid = 1; cid < context.containers().length; ++cid) {
            if (context.containers()[cid].owner() == context.state().mover()) {
                int site;
                for (int siteFrom = site = context.sitesFrom()[cid]; site < context.containers()[cid].numSites() + siteFrom; ++site) {
                    for (int level = 0; level < context.containerState(cid).sizeStackCell(site); ++level) {
                        final int to = context.state().mover() - 1;
                        final Action actionMove = new ActionMove(SiteType.Cell, site, level, SiteType.Cell, to, -1, -1, -1, false);
                        if (this.isDecision()) {
                            actionMove.setDecision(true);
                        }
                        final Move move = new Move(actionMove);
                        move.setFromNonDecision(site);
                        move.setLevelMinNonDecision(level);
                        move.setLevelMaxNonDecision(level);
                        move.setToNonDecision(to);
                        move.setMover(context.state().mover());
                        moves.moves().add(move);
                    }
                }
            }
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
        final long gameFlags = super.gameFlags(game) | 0x2000000L | 0x2000L | 0x8L | 0x1000L | 0x1L;
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
    }
}
