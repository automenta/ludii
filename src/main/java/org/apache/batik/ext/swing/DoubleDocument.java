// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.swing;

import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;

public class DoubleDocument extends PlainDocument
{
    @Override
    public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {
        if (str == null) {
            return;
        }
        final String curVal = this.getText(0, this.getLength());
        boolean hasDot = curVal.indexOf(46) != -1;
        final char[] buffer = str.toCharArray();
        final char[] digit = new char[buffer.length];
        int j = 0;
        if (offs == 0 && buffer != null && buffer.length > 0 && buffer[0] == '-') {
            digit[j++] = buffer[0];
        }
        for (final char aBuffer : buffer) {
            if (Character.isDigit(aBuffer)) {
                digit[j++] = aBuffer;
            }
            if (!hasDot && aBuffer == '.') {
                digit[j++] = '.';
                hasDot = true;
            }
        }
        final String added = new String(digit, 0, j);
        try {
            final StringBuffer val = new StringBuffer(curVal);
            val.insert(offs, added);
            final String valStr = val.toString();
            if (valStr.equals(".") || valStr.equals("-") || valStr.equals("-.")) {
                super.insertString(offs, added, a);
            }
            else {
                Double.valueOf(valStr);
                super.insertString(offs, added, a);
            }
        }
        catch (NumberFormatException ex) {}
    }
    
    public void setValue(final double d) {
        try {
            this.remove(0, this.getLength());
            this.insertString(0, String.valueOf(d), null);
        }
        catch (BadLocationException ex) {}
    }
    
    public double getValue() {
        try {
            final String t = this.getText(0, this.getLength());
            if (t != null && t.length() > 0) {
                return Double.parseDouble(t);
            }
            return 0.0;
        }
        catch (BadLocationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
