// 
// Decompiled by Procyon v0.5.36
// 

package language.compiler.exceptions;

import main.StringRoutines;

public class ArrayTypeNotFoundException extends CompilerException
{
    private static final long serialVersionUID = 1L;
    private final String expectedType;
    
    public ArrayTypeNotFoundException(final String expectedType) {
        this.expectedType = expectedType;
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
        sb.append(StringRoutines.highlightText(safeDescription, this.expectedType, "font", "red"));
        sb.append("</p>");
        sb.append("</html>");
        System.out.println(sb);
        return sb.toString();
    }
    
    @Override
    public String getMessageTitle() {
        return "The array type " + this.expectedType + " was not found.";
    }
}
