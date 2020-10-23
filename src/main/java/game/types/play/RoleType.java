// 
// Decompiled by Procyon v0.5.36
// 

package game.types.play;

public enum RoleType
{
    Neutral(0), 
    P1(1), 
    P2(2), 
    P3(3), 
    P4(4), 
    P5(5), 
    P6(6), 
    P7(7), 
    P8(8), 
    P9(9), 
    P10(10), 
    P11(11), 
    P12(12), 
    P13(13), 
    P14(14), 
    P15(15), 
    P16(16), 
    Team1(0), 
    Team2(0), 
    Team3(0), 
    Team4(0), 
    Team5(0), 
    Team6(0), 
    Team7(0), 
    Team8(0), 
    Team9(0), 
    Team10(0), 
    Team11(0), 
    Team12(0), 
    Team13(0), 
    Team14(0), 
    Team15(0), 
    Team16(0), 
    Each(0), 
    Shared(0), 
    All(0), 
    Any(0), 
    Mover(0), 
    Next(0), 
    Prev(0), 
    NonMover(0), 
    Enemy(0), 
    Ally(0), 
    NonAlly(0), 
    Partner(0), 
    NonPartner(0), 
    NonNeutral(0), 
    Player(0);
    
    private static final RoleType[] PlayerIdToRole;
    private final int owner;
    
    RoleType(final int owner) {
        this.owner = owner;
    }
    
    public int owner() {
        return this.owner;
    }
    
    public static RoleType roleForPlayerId(final int pid) {
        if (pid > 0 && pid <= 16) {
            return RoleType.PlayerIdToRole[pid];
        }
        return RoleType.Neutral;
    }
    
    static {
        PlayerIdToRole = values();
    }
}
