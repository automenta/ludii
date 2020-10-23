// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.component;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.rules.play.moves.Moves;
import game.types.component.CardType;
import game.types.play.RoleType;
import metadata.graphics.util.ComponentStyleType;

import java.io.Serializable;

public class Card extends Component implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final int trumpValue;
    private final int suit;
    private final int trumpRank;
    private final int rank;
    private final CardType cardType;
    
    public Card(final String label, final RoleType role, final CardType cardType, @Name final Integer rank, @Name final Integer value, @Name final Integer trumpRank, @Name final Integer trumpValue, @Name final Integer suit, @Opt final Moves generator) {
        super(label, role, null, null, value, generator);
        this.trumpValue = ((trumpValue == null) ? -1 : trumpValue);
        this.suit = ((suit == null) ? -1 : suit);
        this.trumpRank = ((trumpValue == null) ? -1 : trumpRank);
        this.rank = ((suit == null) ? -1 : rank);
        this.cardType = cardType;
        this.style = ComponentStyleType.Card;
    }
    
    protected Card(final Card other) {
        super(other);
        this.cardType = other.cardType;
        this.suit = other.suit;
        this.trumpValue = other.trumpValue;
        this.rank = other.rank;
        this.trumpRank = other.trumpRank;
    }
    
    @Override
    public Card clone() {
        return new Card(this);
    }
    
    @Override
    public boolean isCard() {
        return true;
    }
    
    @Override
    public int suit() {
        return this.suit;
    }
    
    @Override
    public int trumpValue() {
        return this.trumpValue;
    }
    
    @Override
    public int rank() {
        return this.rank;
    }
    
    @Override
    public int trumpRank() {
        return this.trumpRank;
    }
    
    @Override
    public CardType cardType() {
        return this.cardType;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return super.gameFlags(game) | 0x2000L;
    }
}
