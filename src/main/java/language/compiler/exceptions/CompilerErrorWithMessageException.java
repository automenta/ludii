// 
// Decompiled by Procyon v0.5.36
// 

package language.compiler.exceptions;

public class CompilerErrorWithMessageException extends CompilerException
{
    private static final long serialVersionUID = 1L;
    private final String message;
    
    public CompilerErrorWithMessageException(final String message) {
        this.message = message;
    }
    
    @Override
    public String getMessageBody(final String gameDescription) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.message);
        System.out.println(sb);
        return sb.toString();
    }
    
    @Override
    public String getMessageTitle() {
        return this.message;
    }
}
