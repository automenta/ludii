// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.editor;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import java.awt.*;

public class TextPaneUtils
{
    private static final String HTML_BREAK = "<br/>";
    private static final String HTML_END = "</html>";
    private static final String HTML_START = "<html>";
    
    public static int getCaretRowNumber(final JTextPane textArea) {
        final int originalCarotPosition = textArea.getCaret().getDot();
        int rn = (originalCarotPosition == 0) ? 1 : 0;
        try {
            for (int offs = originalCarotPosition; offs > 0; offs = Utilities.getRowStart(textArea, offs) - 1, ++rn) {}
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        return rn;
    }
    
    public static Point cursorCoords(final JTextPane textArea) {
        final Caret caret = textArea.getCaret();
        final Point point = caret.getMagicCaretPosition();
        return point;
    }
    
    public static int startOfCaretCurrentRow(final JTextPane textArea) {
        try {
            final int originalCarotPosition = textArea.getCaret().getDot();
            return Utilities.getRowStart(textArea, originalCarotPosition);
        }
        catch (BadLocationException e2) {
            e2.printStackTrace();
            return 0;
        }
    }
    
    public static String getLettersBeforePoint(final JTextComponent tc, final Point pt) {
        try {
            final int pos = tc.viewToModel(pt);
            final int start = Utilities.getWordStart(tc, pos);
            return tc.getText(start, pos - start);
        }
        catch (BadLocationException e) {
            System.err.println(e);
            return null;
        }
    }
    
    public static String getWordAtPoint(final JTextComponent tc, final Point pt) {
        try {
            final int pos = tc.viewToModel(pt);
            final int start = Utilities.getWordStart(tc, pos);
            final int end = Utilities.getWordEnd(tc, pos);
            return tc.getText(start, end - start);
        }
        catch (BadLocationException e) {
            System.err.println(e);
            return null;
        }
    }
    
    public static String replaceWordAtPoint(final JTextComponent tc, final Point pt, final String newWord) {
        try {
            final int pos = tc.viewToModel(pt);
            final int start = Utilities.getWordStart(tc, pos);
            final int end = Utilities.getWordEnd(tc, pos);
            return tc.getText(0, start) + newWord + tc.getText(end, tc.getText().length() - end);
        }
        catch (BadLocationException e) {
            System.err.println(e);
            return null;
        }
    }
    
    public static String replaceWordAtCaret(final JTextComponent tc, final String newWord) {
        try {
            final int pos = tc.getCaretPosition();
            final int start = Utilities.getWordStart(tc, pos);
            final int end = Utilities.getWordEnd(tc, pos);
            return tc.getText(0, start) + newWord + tc.getText(end, tc.getText().length() - end);
        }
        catch (BadLocationException e) {
            System.err.println(e);
            return null;
        }
    }
    
    public static void insertAtCaret(final JTextComponent tc, final String text) {
        tc.replaceSelection(text);
    }
    
    public static String firstDiff(final String s1, final String s2) {
        for (int i = 0; i < s1.length() && i < s2.length(); ++i) {
            if (s1.charAt(i) != s2.charAt(i)) {
                final String sub = (i + 20 < s1.length()) ? s1.substring(i, i + 20) : s1.substring(i);
                return sub.replace("\r", "\\r").replace("\n", "\\n");
            }
        }
        return "";
    }
    
    public static String convertToHTML(final String source) {
        if (source.toLowerCase().startsWith("<html>")) {
            return source;
        }
        return "<html>" + source.replace("\r\n", "<br/>").replace("\r", "<br/>").replace("\n", "<br/>") + "</html>";
    }
}
