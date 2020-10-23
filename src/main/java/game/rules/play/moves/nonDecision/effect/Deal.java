// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.equipment.component.Component;
import game.equipment.container.Container;
import game.equipment.container.other.Deck;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.types.component.DealableType;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.Move;
import util.action.BaseAction;
import util.action.hidden.ActionSetMasked;
import util.action.hidden.ActionSetVisible;
import util.action.move.ActionAdd;
import util.action.move.ActionMove;
import util.state.containerState.ContainerState;

import java.util.ArrayList;
import java.util.List;

public final class Deal extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction countFn;
    private final DealableType type;
    private final IntFunction beginWith;
    
    public Deal(final DealableType type, @Opt final IntFunction count, @Opt @Name final IntFunction beginWith, @Opt final Then then) {
        super(then);
        this.type = type;
        this.countFn = ((count == null) ? new IntConstant(1) : count);
        this.beginWith = beginWith;
    }
    
    @Override
    public Moves eval(final Context context) {
        if (this.type == DealableType.Cards) {
            return this.evalCards(context);
        }
        if (this.type == DealableType.Dominoes) {
            return this.evalDominoes(context);
        }
        return new BaseMoves(super.then());
    }
    
    public Moves evalCards(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        if (context.game().handDeck().isEmpty()) {
            return moves;
        }
        final List<Integer> handIndex = new ArrayList<>();
        for (final Container c : context.containers()) {
            if (c.isHand() && !c.isDeck() && !c.isDice()) {
                handIndex.add(context.sitesFrom()[c.index()]);
            }
        }
        if (handIndex.size() != context.game().players().count()) {
            return moves;
        }
        final Deck deck = context.game().handDeck().get(0);
        final ContainerState cs = context.containerState(deck.index());
        final int indexSiteDeck = context.sitesFrom()[deck.index()];
        final int sizeDeck = cs.sizeStackCell(indexSiteDeck);
        final int count = this.countFn.eval(context);
        if (sizeDeck < count * handIndex.size()) {
            throw new IllegalArgumentException("You can not deal so much cards.");
        }
        int hand = (this.beginWith == null) ? 0 : (this.beginWith.eval(context) - 1);
        int counter = 0;
        for (int indexCard = 0; indexCard < count * handIndex.size(); ++indexCard) {
            final BaseAction dealAction = new ActionMove(SiteType.Cell, indexSiteDeck, cs.sizeStackCell(indexSiteDeck) - 1 - counter, SiteType.Cell, handIndex.get(hand), -1, -1, -1, false);
            final Move move = new Move(dealAction);
            for (int pid = 1; pid < context.game().players().size(); ++pid) {
                final BaseAction makeMaskedAction = new ActionSetMasked(SiteType.Cell, handIndex.get(hand), -1, pid);
                move.actions().add(makeMaskedAction);
            }
            final BaseAction makeVisibleAction = new ActionSetVisible(SiteType.Cell, handIndex.get(hand), -1, hand + 1);
            move.actions().add(makeVisibleAction);
            moves.moves().add(move);
            if (hand == context.game().players().count() - 1) {
                hand = 0;
            }
            else {
                ++hand;
            }
            ++counter;
        }
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    public Moves evalDominoes(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final TIntArrayList handIndex = new TIntArrayList();
        for (final Container c : context.containers()) {
            if (c.isHand() && !c.isDeck() && !c.isDice()) {
                handIndex.add(context.sitesFrom()[c.index()]);
            }
        }
        if (handIndex.size() != context.game().players().count()) {
            return moves;
        }
        final Component[] components = context.components();
        final int count = this.countFn.eval(context);
        if (components.length < count * handIndex.size()) {
            throw new IllegalArgumentException("You can not deal so much dominoes.");
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
        for (int dealed = 0; dealed < count * 2; ++dealed) {
            final int index = context.rng().nextInt(toDeal.size());
            final int indexComponent = toDeal.getQuick(index);
            final Component component = components[indexComponent];
            final int currentPlayer = dealed % nbPlayers;
            final BaseAction actionAtomic = new ActionAdd(SiteType.Cell, handIndex.getQuick(currentPlayer) + dealed / nbPlayers, component.index(), count, -1, -1, null, masked.get(currentPlayer), null);
            final Move move = new Move(actionAtomic);
            moves.moves().add(move);
            toDeal.removeAt(index);
        }
        if (this.then() != null) {
            for (int k = 0; k < moves.moves().size(); ++k) {
                moves.moves().get(k).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    @Override
    public boolean canMove(final Context context) {
        return false;
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        if (this.type == DealableType.Cards) {
            return 0x2000L | this.countFn.gameFlags(game) | super.gameFlags(game);
        }
        if (this.type == DealableType.Dominoes) {
            return 0x8004048L | this.countFn.gameFlags(game) | super.gameFlags(game);
        }
        return this.countFn.gameFlags(game) | super.gameFlags(game);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.countFn.preprocess(game);
        if (this.beginWith != null) {
            this.beginWith.preprocess(game);
        }
    }
    
    @Override
    public String toEnglish() {
        return "Deal";
    }
}
