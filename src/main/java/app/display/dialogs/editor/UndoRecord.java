// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.editor;

import javax.swing.*;

public class UndoRecord
{
    public final String text;
    public final int caretPos;
    public final int selectionStart;
    public final int selectionEnd;
    
    public UndoRecord(final JTextPane textArea) {
        this.text = textArea.getText();
        this.caretPos = textArea.getCaret().getDot();
        this.selectionStart = textArea.getSelectionStart();
        this.selectionEnd = textArea.getSelectionEnd();
    }
    
    public void apply(final JTextPane textArea) {
        textArea.setText(this.text);
        textArea.setCaretPosition(this.caretPos);
        textArea.setSelectionStart(this.selectionStart);
        textArea.setSelectionEnd(this.selectionEnd);
    }
    
    public boolean ignoreChanges(final JTextPane textArea) {
        return this.text.equals(textArea.getText());
    }
}
