// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.text;

import java.awt.font.TextAttribute;
import java.util.Map;
import java.util.Set;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator;

public interface GVTAttributedCharacterIterator extends AttributedCharacterIterator
{
    void setString(final String p0);
    
    void setString(final AttributedString p0);
    
    void setAttributeArray(final TextAttribute p0, final Object[] p1, final int p2, final int p3);
    
    Set getAllAttributeKeys();
    
    Object getAttribute(final Attribute p0);
    
    Map getAttributes();
    
    int getRunLimit();
    
    int getRunLimit(final Attribute p0);
    
    int getRunLimit(final Set p0);
    
    int getRunStart();
    
    int getRunStart(final Attribute p0);
    
    int getRunStart(final Set p0);
    
    Object clone();
    
    char current();
    
    char first();
    
    int getBeginIndex();
    
    int getEndIndex();
    
    int getIndex();
    
    char last();
    
    char next();
    
    char previous();
    
    char setIndex(final int p0);
    
    public static class TextAttribute extends Attribute
    {
        public static final TextAttribute FLOW_PARAGRAPH;
        public static final TextAttribute FLOW_EMPTY_PARAGRAPH;
        public static final TextAttribute FLOW_LINE_BREAK;
        public static final TextAttribute FLOW_REGIONS;
        public static final TextAttribute LINE_HEIGHT;
        public static final TextAttribute PREFORMATTED;
        public static final TextAttribute TEXT_COMPOUND_DELIMITER;
        public static final TextAttribute TEXT_COMPOUND_ID;
        public static final TextAttribute ANCHOR_TYPE;
        public static final TextAttribute EXPLICIT_LAYOUT;
        public static final TextAttribute X;
        public static final TextAttribute Y;
        public static final TextAttribute DX;
        public static final TextAttribute DY;
        public static final TextAttribute ROTATION;
        public static final TextAttribute PAINT_INFO;
        public static final TextAttribute BBOX_WIDTH;
        public static final TextAttribute LENGTH_ADJUST;
        public static final TextAttribute CUSTOM_SPACING;
        public static final TextAttribute KERNING;
        public static final TextAttribute LETTER_SPACING;
        public static final TextAttribute WORD_SPACING;
        public static final TextAttribute TEXTPATH;
        public static final TextAttribute FONT_VARIANT;
        public static final TextAttribute BASELINE_SHIFT;
        public static final TextAttribute WRITING_MODE;
        public static final TextAttribute VERTICAL_ORIENTATION;
        public static final TextAttribute VERTICAL_ORIENTATION_ANGLE;
        public static final TextAttribute HORIZONTAL_ORIENTATION_ANGLE;
        public static final TextAttribute GVT_FONT_FAMILIES;
        public static final TextAttribute GVT_FONTS;
        public static final TextAttribute GVT_FONT;
        public static final TextAttribute ALT_GLYPH_HANDLER;
        public static final TextAttribute BIDI_LEVEL;
        public static final TextAttribute CHAR_INDEX;
        public static final TextAttribute ARABIC_FORM;
        public static final TextAttribute SCRIPT;
        public static final TextAttribute LANGUAGE;
        public static final Integer WRITING_MODE_LTR;
        public static final Integer WRITING_MODE_RTL;
        public static final Integer WRITING_MODE_TTB;
        public static final Integer ORIENTATION_ANGLE;
        public static final Integer ORIENTATION_AUTO;
        public static final Integer SMALL_CAPS;
        public static final Integer UNDERLINE_ON;
        public static final Boolean OVERLINE_ON;
        public static final Boolean STRIKETHROUGH_ON;
        public static final Integer ADJUST_SPACING;
        public static final Integer ADJUST_ALL;
        public static final Integer ARABIC_NONE;
        public static final Integer ARABIC_ISOLATED;
        public static final Integer ARABIC_TERMINAL;
        public static final Integer ARABIC_INITIAL;
        public static final Integer ARABIC_MEDIAL;
        
        public TextAttribute(final String s) {
            super(s);
        }
        
        static {
            FLOW_PARAGRAPH = new TextAttribute("FLOW_PARAGRAPH");
            FLOW_EMPTY_PARAGRAPH = new TextAttribute("FLOW_EMPTY_PARAGRAPH");
            FLOW_LINE_BREAK = new TextAttribute("FLOW_LINE_BREAK");
            FLOW_REGIONS = new TextAttribute("FLOW_REGIONS");
            LINE_HEIGHT = new TextAttribute("LINE_HEIGHT");
            PREFORMATTED = new TextAttribute("PREFORMATTED");
            TEXT_COMPOUND_DELIMITER = new TextAttribute("TEXT_COMPOUND_DELIMITER");
            TEXT_COMPOUND_ID = new TextAttribute("TEXT_COMPOUND_ID");
            ANCHOR_TYPE = new TextAttribute("ANCHOR_TYPE");
            EXPLICIT_LAYOUT = new TextAttribute("EXPLICIT_LAYOUT");
            X = new TextAttribute("X");
            Y = new TextAttribute("Y");
            DX = new TextAttribute("DX");
            DY = new TextAttribute("DY");
            ROTATION = new TextAttribute("ROTATION");
            PAINT_INFO = new TextAttribute("PAINT_INFO");
            BBOX_WIDTH = new TextAttribute("BBOX_WIDTH");
            LENGTH_ADJUST = new TextAttribute("LENGTH_ADJUST");
            CUSTOM_SPACING = new TextAttribute("CUSTOM_SPACING");
            KERNING = new TextAttribute("KERNING");
            LETTER_SPACING = new TextAttribute("LETTER_SPACING");
            WORD_SPACING = new TextAttribute("WORD_SPACING");
            TEXTPATH = new TextAttribute("TEXTPATH");
            FONT_VARIANT = new TextAttribute("FONT_VARIANT");
            BASELINE_SHIFT = new TextAttribute("BASELINE_SHIFT");
            WRITING_MODE = new TextAttribute("WRITING_MODE");
            VERTICAL_ORIENTATION = new TextAttribute("VERTICAL_ORIENTATION");
            VERTICAL_ORIENTATION_ANGLE = new TextAttribute("VERTICAL_ORIENTATION_ANGLE");
            HORIZONTAL_ORIENTATION_ANGLE = new TextAttribute("HORIZONTAL_ORIENTATION_ANGLE");
            GVT_FONT_FAMILIES = new TextAttribute("GVT_FONT_FAMILIES");
            GVT_FONTS = new TextAttribute("GVT_FONTS");
            GVT_FONT = new TextAttribute("GVT_FONT");
            ALT_GLYPH_HANDLER = new TextAttribute("ALT_GLYPH_HANDLER");
            BIDI_LEVEL = new TextAttribute("BIDI_LEVEL");
            CHAR_INDEX = new TextAttribute("CHAR_INDEX");
            ARABIC_FORM = new TextAttribute("ARABIC_FORM");
            SCRIPT = new TextAttribute("SCRIPT");
            LANGUAGE = new TextAttribute("LANGUAGE");
            WRITING_MODE_LTR = 1;
            WRITING_MODE_RTL = 2;
            WRITING_MODE_TTB = 3;
            ORIENTATION_ANGLE = 1;
            ORIENTATION_AUTO = 2;
            SMALL_CAPS = 16;
            UNDERLINE_ON = java.awt.font.TextAttribute.UNDERLINE_ON;
            OVERLINE_ON = Boolean.TRUE;
            STRIKETHROUGH_ON = java.awt.font.TextAttribute.STRIKETHROUGH_ON;
            ADJUST_SPACING = 0;
            ADJUST_ALL = 1;
            ARABIC_NONE = 0;
            ARABIC_ISOLATED = 1;
            ARABIC_TERMINAL = 2;
            ARABIC_INITIAL = 3;
            ARABIC_MEDIAL = 4;
        }
    }
    
    public interface AttributeFilter
    {
        AttributedCharacterIterator mutateAttributes(final AttributedCharacterIterator p0);
    }
}
