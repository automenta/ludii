// 
// Decompiled by Procyon v0.5.36
// 

package game.util.equipment;

import annotations.Name;
import annotations.Opt;
import game.types.component.CardType;
import util.BaseLudeme;

public class Card extends BaseLudeme
{
    private final int rank;
    private final int value;
    private final int trumpRank;
    private final int trumpValue;
    private final int biased;
    private final CardType type;
    
    public Card(final CardType type, @Name final Integer rank, @Name final Integer value, @Opt @Name final Integer trumpRank, @Opt @Name final Integer trumpValue, @Opt @Name final Integer biased) {
        this.type = type;
        this.rank = rank;
        this.value = value;
        this.trumpRank = ((trumpRank == null) ? rank : ((int)trumpRank));
        this.trumpValue = ((trumpValue == null) ? value : ((int)trumpValue));
        this.biased = ((biased == null) ? -1 : biased);
    }
    
    public CardType type() {
        return this.type;
    }
    
    public int rank() {
        return this.rank;
    }
    
    public int value() {
        return this.value;
    }
    
    public int trumpRank() {
        return this.trumpRank;
    }
    
    public int trumpValue() {
        return this.trumpValue;
    }
    
    public int biased() {
        return this.biased;
    }
}
