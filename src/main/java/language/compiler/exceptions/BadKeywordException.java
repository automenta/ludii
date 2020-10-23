// 
// Decompiled by Procyon v0.5.36
// 

package language.compiler.exceptions;

import main.StringRoutines;

public class BadKeywordException extends CompilerException
{
    private static final long serialVersionUID = 1L;
    private final String badKeyword;
    private final String message;
    
    public BadKeywordException(final String badKeyword, final String message) {
        this.badKeyword = badKeyword;
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
        sb.append(StringRoutines.highlightText(safeDescription, this.badKeyword, "font", "red"));
        sb.append("</p>");
        sb.append("</html>");
        System.out.println(sb);
        return sb.toString();
    }
    
    @Override
    public String getMessageTitle() {
        String str = "The keyword \"" + this.badKeyword + "\" cannot be recognised.";
        if (this.message != null) {
            str = str + " " + this.message;
        }
        return str;
    }
}
