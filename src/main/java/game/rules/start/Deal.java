// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start;

import annotations.Opt;
import game.Game;
import game.equipment.component.Component;
import game.equipment.container.Container;
import game.equipment.container.other.Deck;
import game.types.board.SiteType;
import game.types.component.DealableType;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.Move;
import util.action.BaseAction;
import util.action.hidden.ActionSetVisible;
import util.action.move.ActionMove;
import util.state.containerState.ContainerState;

import java.util.ArrayList;
import java.util.List;

public final class Deal extends StartRule
{
    private static final long serialVersionUID = 1L;
    private final int count;
    private final DealableType type;
    
    public Deal(final DealableType type, @Opt final Integer count) {
        this.type = type;
        this.count = ((count == null) ? 1 : count);
    }
    
    @Override
    public void eval(final Context context) {
        if (this.type == DealableType.Cards) {
            this.evalCards(context);
        }
        else if (this.type == DealableType.Dominoes) {
            this.evalDominoes(context);
        }
    }
    
    public void evalCards(final Context context) {
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
        final ContainerState cs = context.containerState(deck.index());
        final int indexSiteDeck = context.sitesFrom()[deck.index()];
        final int sizeDeck = cs.sizeStackCell(indexSiteDeck);
        if (sizeDeck < this.count * handIndex.size()) {
            throw new IllegalArgumentException("You can not deal so much cards in the initial state.");
        }
        int hand = 0;
        int level = 0;
        for (int indexCard = 0; indexCard < this.count * handIndex.size(); ++indexCard) {
            final BaseAction dealAction = new ActionMove(SiteType.Cell, indexSiteDeck, cs.sizeStackCell(indexSiteDeck) - 1, SiteType.Cell, handIndex.get(hand), -1, -1, -1, false);
            dealAction.apply(context, true);
            context.trial().moves().add(new Move(dealAction));
            context.trial().addInitPlacement();
            final BaseAction makeVisibleAction = new ActionSetVisible(SiteType.Cell, handIndex.get(hand), level, hand + 1);
            makeVisibleAction.apply(context, true);
            context.trial().moves().add(new Move(makeVisibleAction));
            context.trial().addInitPlacement();
            if (hand == context.game().players().count() - 1) {
                hand = 0;
                ++level;
            }
            else {
                ++hand;
            }
        }
    }
    
    public void evalDominoes(final Context context) {
        final TIntArrayList handIndex = new TIntArrayList();
        for (final Container c : context.containers()) {
            if (c.isHand() && !c.isDeck() && !c.isDice()) {
                handIndex.add(context.sitesFrom()[c.index()]);
            }
        }
        if (handIndex.size() != context.game().players().count()) {
            return;
        }
        final Component[] components = context.components();
        if (components.length < this.count * handIndex.size()) {
            throw new IllegalArgumentException("You can not deal so much dominoes in the initial state.");
        }
        final TIntArrayList toDeal = new TIntArrayList();
        for (int i = 1; i < components.length; ++i) {
            toDeal.add(i);
        }
        final int nbPlayers = context.players().size() - 1;
        final ArrayList<boolean[]> masked = new ArrayList<>();
        for (int j = 1; j <= nbPlayers; ++j) {
            masked.add(new boolean[nbPlayers]);
            for (int k = 1; k <= nbPlayers; ++k) {
                masked.get(j - 1)[k - 1] = j != k;
            }
        }
        for (int dealed = 0; dealed < this.count * 2; ++dealed) {
            final int index = context.rng().nextInt(toDeal.size());
            final int indexComponent = toDeal.getQuick(index);
            final Component component = components[indexComponent];
            final int currentPlayer = dealed % nbPlayers;
            Start.placePieces(context, handIndex.getQuick(currentPlayer) + dealed / nbPlayers, component.index(), 1, -1, -1, false, null, masked.get(currentPlayer), SiteType.Cell);
            toDeal.removeAt(index);
        }
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public long gameFlags(final Game game) {
        if (this.type == DealableType.Cards) {
            return 8192L;
        }
        if (this.type == DealableType.Dominoes) {
            return 134234184L;
        }
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    @Override
    public String toString() {
        final String str = "(Deal" + this.type + ")";
        return str;
    }
}
