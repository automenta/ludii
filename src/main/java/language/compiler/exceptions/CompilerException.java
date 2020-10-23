// 
// Decompiled by Procyon v0.5.36
// 

package language.compiler.exceptions;

public class CompilerException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public CompilerException(final String messageBody, final CompilerException e) {
        super(messageBody, e);
    }
    
    public CompilerException() {
    }
    
    public String getMessageBody(final String gameDescription) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<h2>");
        sb.append(this.getMessageTitle());
        sb.append("</h2>");
        sb.append("<br/>");
        sb.append("<p>");
        sb.append(gameDescription);
        sb.append("</p>");
        sb.append("</html>");
        return sb.toString();
    }
    
    public String getMessageTitle() {
        return "A compiler error has occurred.";
    }
}
