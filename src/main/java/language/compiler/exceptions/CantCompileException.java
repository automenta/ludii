// 
// Decompiled by Procyon v0.5.36
// 

package language.compiler.exceptions;

import main.StringRoutines;

public class CantCompileException extends CompilerException
{
    private static final long serialVersionUID = 1L;
    private final String badKeyword;
    
    public CantCompileException(final String badKeyword) {
        this.badKeyword = StringRoutines.toDromedaryCase(badKeyword);
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
        return "Can't compile the ludeme \"" + this.badKeyword + "\".";
    }
}
