// 
// Decompiled by Procyon v0.5.36
// 

package language.compiler.exceptions;

import main.StringRoutines;

public class BadArrayElementException extends CompilerException
{
    private static final long serialVersionUID = 1L;
    private final String expectedType;
    private final String elementType;
    
    public BadArrayElementException(final String expectedType, final String elementType) {
        this.expectedType = expectedType;
        this.elementType = elementType;
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
        sb.append(StringRoutines.highlightText(safeDescription, this.elementType, "font", "red"));
        sb.append("</p>");
        sb.append("</html>");
        System.out.println(sb);
        return sb.toString();
    }
    
    @Override
    public String getMessageTitle() {
        return "Array element of type " + this.elementType + " but type " + this.expectedType + " expected.";
    }
}
