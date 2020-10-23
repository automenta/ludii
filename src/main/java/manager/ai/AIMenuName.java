// 
// Decompiled by Procyon v0.5.36
// 

package manager.ai;

public enum AIMenuName
{
    Human("Human"), 
    LudiiAI("Ludii AI"), 
    Random("Random"), 
    FlatMC("Flat MC"), 
    UCT("UCT"), 
    UCTUncapped("UCT (Uncapped)"), 
    MCGRAVE("MC-GRAVE"), 
    BiasedMCTS("Biased MCTS"), 
    BiasedMCTSUniformPlayouts("MCTS (Biased Selection)"), 
    AlphaBeta("Alpha-Beta"), 
    FromJAR("From JAR");
    
    public final String label;
    
    AIMenuName(final String label) {
        this.label = label;
    }
    
    public static AIMenuName getAIMenuName(final String label) {
        for (final AIMenuName menuName : values()) {
            if (menuName.label.equals(label)) {
                return menuName;
            }
        }
        return null;
    }
}
