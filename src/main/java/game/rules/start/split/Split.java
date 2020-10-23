// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start.split;

import game.Game;
import game.equipment.container.Container;
import game.equipment.container.other.Deck;
import game.rules.start.StartRule;
import game.types.board.SiteType;
import util.Context;
import util.Move;
import util.action.BaseAction;
import util.action.move.ActionMove;
import util.state.containerStackingState.BaseContainerStateStacking;

import java.util.ArrayList;
import java.util.List;

public final class Split extends StartRule
{
    private static final long serialVersionUID = 1L;
    
    public Split(final SplitType type) {
        switch (type) {
        }
    }
    
    @Override
    public void eval(final Context context) {
        if (context.game().handDeck().isEmpty()) {
            return;
        }
        final List<Integer> handIndex = new ArrayList<>();
        for (final Container c : context.containers()) {
            if (c.isHand() && !c.isDeck() && !c.isDice()) {
                handIndex.add(context.sitesFrom()[c.index()]);
            }
        }
        if (handIndex.size() != context.game().players().count()) {
            return;
        }
        final Deck deck = context.game().handDeck().get(0);
        final BaseContainerStateStacking cs = (BaseContainerStateStacking)context.containerState(deck.index());
        final int indexSiteDeck = context.sitesFrom()[deck.index()];
        final int sizeDeck = cs.sizeStackCell(indexSiteDeck);
        int hand = 0;
        for (int indexCard = 0; indexCard < sizeDeck; ++indexCard) {
            final BaseAction actionAtomic = new ActionMove(SiteType.Cell, indexSiteDeck, 0, SiteType.Cell, handIndex.get(hand), -1, -1, -1, false);
            actionAtomic.apply(context, true);
            context.trial().moves().add(new Move(actionAtomic));
            context.trial().addInitPlacement();
            if (hand == context.game().players().count() - 1) {
                hand = 0;
            }
            else {
                ++hand;
            }
        }
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 8192L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    @Override
    public String toString() {
        final String str = "(splitDeck)";
        return "(splitDeck)";
    }
}
