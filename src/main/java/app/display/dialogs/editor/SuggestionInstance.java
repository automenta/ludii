// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.editor;

public class SuggestionInstance
{
    final String classPath;
    final String label;
    final String substitution;
    final String javadoc;
    
    public SuggestionInstance(final String classPath, final String label, final String substitution, final String javadoc) {
        this.classPath = classPath;
        this.label = label;
        this.substitution = substitution;
        this.javadoc = javadoc;
    }
}
