// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start;

import game.Game;
import game.rules.Rule;

public abstract class StartRule implements Rule
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public String toEnglish() {
        return "<StartRule>";
    }
    
    public int count() {
        return 0;
    }
    
    public int howManyPlace(final Game game) {
        return 0;
    }
    
    public boolean isSet() {
        return false;
    }
}
