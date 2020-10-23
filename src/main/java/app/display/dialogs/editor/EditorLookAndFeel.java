// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.editor;

import java.awt.*;

public class EditorLookAndFeel
{
    static final String PARAM_TABLE_START = "<table class=\"params\" border=\"0\" cellspacing=0 cellpadding=0>";
    static final String DOC_TABLE_START = "<table cellspacing=0 cellpadding=10 width='100%' >";
    static final String TABLE_END = "</table>";
    static final String ROW_START = "<tr>";
    static final String ROW_END = "</tr>";
    static final String DOC_ROW_START = "<tr style='border: 1px silver solid;'>";
    static final String DOC_ROW_END = "</tr>";
    static final String CELL_START = "<td>";
    static final String CELL_END = "</td>";
    static final String TABLE_HEADER_START = "<th>";
    static final String TABLE_HEADER_END = "</th>";
    static final String HEADING_START = "<b>";
    static final String HEADING_END = "</b>";
    static final String REMARK_START = "";
    static final String REMARK_END = "";
    static final String KEYWORD_START = "<b>";
    static final String KEYWORD_END = "</b>";
    static final String BR = "<br/>";
    static final String PAD = "&nbsp;";
    static final String MIN_CELL_DISTANCE = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
    static final String HORIZONTAL_LINE3 = "<hr width=\"100%\"/>";
    static final Color STRING_COLOUR;
    static final Color FLOAT_COLOUR;
    static final Color INT_COLOUR;
    static final Color LABEL_COLOUR;
    static final Color CLASS_COLOUR;
    static final Color ENUM_COLOUR;
    static final Color RULE_COLOUR;
    static final Color DEFAULT_COLOUR;
    private static final Color[] BRACKET_COLOURS_BY_DEPTH;
    private static final Color[] CURLY_BRACKET_COLOURS_BY_DEPTH;
    private static final Color BAD_BRACKET_COLOUR;
    
    public static Color bracketColourByDepthAndType(final EditorTokenType type, final int depth) {
        if (depth < 0) {
            return EditorLookAndFeel.BAD_BRACKET_COLOUR;
        }
        final int index = Math.max(0, depth % EditorLookAndFeel.CURLY_BRACKET_COLOURS_BY_DEPTH.length);
        switch (type) {
            case OPEN_ANGLE, CLOSE_ANGLE -> {
                return EditorLookAndFeel.RULE_COLOUR;
            }
            case OPEN_CURLY, CLOSE_CURLY -> {
                return EditorLookAndFeel.CURLY_BRACKET_COLOURS_BY_DEPTH[index];
            }
            case OPEN_ROUND, CLOSE_ROUND, OPEN_SQUARE, CLOSE_SQUARE -> {
                return EditorLookAndFeel.BRACKET_COLOURS_BY_DEPTH[index];
            }
            default -> {
                System.out.println("Unexpected bracket type received!");
                return EditorLookAndFeel.DEFAULT_COLOUR;
            }
        }
    }
    
    static {
        STRING_COLOUR = new Color(79, 126, 97);
        FLOAT_COLOUR = new Color(70, 95, 185);
        INT_COLOUR = new Color(70, 95, 185);
        LABEL_COLOUR = new Color(160, 160, 160);
        CLASS_COLOUR = new Color(125, 35, 94);
        ENUM_COLOUR = new Color(100, 64, 63);
        RULE_COLOUR = new Color(220, 0, 0);
        DEFAULT_COLOUR = Color.BLACK;
        BRACKET_COLOURS_BY_DEPTH = new Color[] { new Color(0, 40, 150), new Color(50, 120, 240) };
        CURLY_BRACKET_COLOURS_BY_DEPTH = new Color[] { new Color(50, 50, 50), new Color(140, 140, 140) };
        BAD_BRACKET_COLOUR = new Color(200, 0, 0);
    }
}
