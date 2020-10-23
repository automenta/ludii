// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class MaxLengthTextDocument extends PlainDocument
{
    private static final long serialVersionUID = 1L;
    private int maxChars;
    
    @Override
    public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {
        if (str != null && this.getLength() + str.length() < this.maxChars) {
            super.insertString(offs, str, a);
        }
    }
    
    public void setMaxChars(final int i) {
        this.maxChars = i;
    }
    
    public int getMaxChars() {
        return this.maxChars;
    }
}
