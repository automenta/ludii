// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.card;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.card.simple.CardTrumpSuit;
import game.functions.ints.card.site.CardRank;
import game.functions.ints.card.site.CardSuit;
import game.functions.ints.card.site.CardTrumpRank;
import game.functions.ints.card.site.CardTrumpValue;
import util.Context;

public final class Card extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    
    public static IntFunction construct(final CardSimpleType cardType) {
        switch (cardType) {
            case TrumpSuit -> {
                return new CardTrumpSuit();
            }
            default -> throw new IllegalArgumentException("Card(): A CardSimpleType is not implemented.");
        }
    }
    
    public static IntFunction construct(final CardSiteType cardType, @Name final IntFunction at, @Name @Opt final IntFunction level) {
        switch (cardType) {
            case Rank -> {
                return new CardRank(at, level);
            }
            case Suit -> {
                return new CardSuit(at, level);
            }
            case TrumpRank -> {
                return new CardTrumpRank(at, level);
            }
            case TrumpValue -> {
                return new CardTrumpValue(at, level);
            }
            default -> throw new IllegalArgumentException("Card(): A CardSiteType is not implemented.");
        }
    }
    
    private Card() {
    }
    
    @Override
    public int eval(final Context context) {
        throw new UnsupportedOperationException("Card.eval(): Should never be called directly.");
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
