// 
// Decompiled by Procyon v0.5.36
// 

package exception;

public class DuplicateOptionUseException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public DuplicateOptionUseException(final String optionString) {
        super("Option with duplicate matches: " + optionString);
    }
}
