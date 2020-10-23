// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.take.simple;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.equipment.container.Container;
import game.equipment.container.other.Hand;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.Move;
import util.action.hidden.ActionSetMasked;
import util.action.hidden.ActionSetVisible;
import util.action.move.ActionAdd;
import util.state.containerState.ContainerState;

@Hide
public final class TakeDomino extends Effect
{
    private static final long serialVersionUID = 1L;
    
    public TakeDomino(@Opt final Then then) {
        super(then);
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final TIntArrayList remainingDominoes = context.state().remainingDominoes();
        if (remainingDominoes.isEmpty()) {
            return moves;
        }
        int site = -1;
        final Container[] containers = context.containers();
        final int length = containers.length;
        int k = 0;
        while (k < length) {
            final Container container = containers[k];
            if (container.isHand()) {
                final Hand hand = (Hand)container;
                final ContainerState cs = context.containerState(hand.index());
                if (hand.owner() == context.state().mover()) {
                    final int pid = hand.index();
                    int siteHand;
                    for (int siteFrom = siteHand = context.sitesFrom()[pid]; siteHand < siteFrom + hand.numSites(); ++siteHand) {
                        if (cs.whatCell(siteHand) == 0) {
                            site = siteHand;
                            break;
                        }
                    }
                    break;
                }
                break;
            }
            else {
                ++k;
            }
        }
        if (site == -1) {
            return moves;
        }
        final TIntArrayList available = new TIntArrayList();
        for (int i = 0; i < remainingDominoes.size(); ++i) {
            available.add(i);
        }
        final int index = context.rng().nextInt(available.size());
        final int what = remainingDominoes.getQuick(index);
        final ActionAdd actionAdd = new ActionAdd(SiteType.Cell, site, what, 1, 0, -1, null, null, null);
        final Move move = new Move(actionAdd);
        final ActionSetVisible actionSetVisible = new ActionSetVisible(SiteType.Cell, site, 0, context.state().mover());
        move.actions().add(actionSetVisible);
        for (int pid = 1; pid < context.game().players().size(); ++pid) {
            if (pid != context.state().mover()) {
                final ActionSetMasked actionSetMasked = new ActionSetMasked(SiteType.Cell, site, 0, pid);
                move.actions().add(actionSetMasked);
            }
        }
        moves.moves().add(move);
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long gameFlags = 0x8004000L | super.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    @Override
    public String toEnglish() {
        return "TakeDomino";
    }
}
