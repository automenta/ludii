// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.editor;

import java.awt.event.KeyEvent;

public enum EditorActions
{
    UNDO, 
    REDO, 
    DELETE_LINE, 
    AUTOSUGGEST, 
    COPY_SELECTION, 
    REMOVE_SELECTION, 
    PASTE_BUFFER, 
    NO_ACTION, 
    TAB;
    
    public static EditorActions fromKeyEvent(final KeyEvent e) {
        if (e.getKeyCode() == 9) {
            return EditorActions.TAB;
        }
        if ((e.getModifiers() & 0x2) == 0x0) {
            return EditorActions.NO_ACTION;
        }
        switch (e.getKeyCode()) {
            case 68 -> {
                return EditorActions.DELETE_LINE;
            }
            case 89 -> {
                return EditorActions.REDO;
            }
            case 90 -> {
                return EditorActions.UNDO;
            }
            case 46 -> {
                return EditorActions.AUTOSUGGEST;
            }
            case 67 -> {
                return EditorActions.COPY_SELECTION;
            }
            case 88 -> {
                return EditorActions.REMOVE_SELECTION;
            }
            case 86 -> {
                return EditorActions.PASTE_BUFFER;
            }
            default -> {
                return EditorActions.NO_ACTION;
            }
        }
    }
}
