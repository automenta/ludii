// 
// Decompiled by Procyon v0.5.36
// 

package game.types.component;

public enum CardType
{
    Joker(0, "?"), 
    Ace(1, "A"), 
    Two(2, "2"), 
    Three(3, "3"), 
    Four(4, "4"), 
    Five(5, "5"), 
    Six(6, "6"), 
    Seven(7, "7"), 
    Eight(8, "8"), 
    Nine(9, "9"), 
    Ten(10, "10"), 
    Jack(10, "J"), 
    Queen(10, "Q"), 
    King(10, "K");
    
    private final int number;
    private final String label;
    
    CardType(final int number, final String label) {
        this.number = number;
        this.label = label;
    }
    
    public String label() {
        return this.label;
    }
    
    public int number() {
        return this.number;
    }
    
    public boolean isRoyal() {
        boolean result = this == CardType.Jack || this == CardType.Queen || this == CardType.King;
        result = (result || this == CardType.Joker);
        return result;
    }
}
