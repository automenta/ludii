// 
// Decompiled by Procyon v0.5.36
// 

package util;

public abstract class BaseLudeme implements Ludeme
{
    @Override
    public String toEnglish() {
        return "<" + this.getClass().getSimpleName() + ">";
    }
}
