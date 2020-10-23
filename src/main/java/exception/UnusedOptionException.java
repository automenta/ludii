// 
// Decompiled by Procyon v0.5.36
// 

package exception;

public class UnusedOptionException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public UnusedOptionException(final String optionString) {
        super("Unused option: " + optionString);
    }
}
