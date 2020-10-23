// 
// Decompiled by Procyon v0.5.36
// 

package language.compiler.exceptions;

import main.StringRoutines;

public class BadSyntaxException extends CompilerException
{
    private static final long serialVersionUID = 1L;
    private final String keyword;
    private final String message;
    
    public BadSyntaxException(final String keyword, final String message) {
        this.keyword = keyword;
        this.message = message;
    }
    
    @Override
    public String getMessageBody(final String gameDescription) {
        final String safeDescription = StringRoutines.escapeText(gameDescription);
        final StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<h2>");
        sb.append(this.getMessageTitle());
        sb.append("</h2>");
        sb.append("<br/>");
        sb.append("<p>");
        sb.append((this.keyword == null) ? safeDescription : StringRoutines.highlightText(safeDescription, this.keyword, "font", "red"));
        sb.append("</p>");
        sb.append("</html>");
        System.out.println(sb);
        return sb.toString();
    }
    
    @Override
    public String getMessageTitle() {
        return "Syntax error: " + this.message;
    }
}