// 
// Decompiled by Procyon v0.5.36
// 

package language.compiler.exceptions;

public class ParserException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public ParserException(final String messageBody, final ParserException e) {
        super(messageBody, e);
    }
    
    public ParserException() {
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
        return "A parser error has occurred.";
    }
}
