// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine;

import org.apache.batik.css.engine.value.svg12.MarginShorthandManager;
import org.apache.batik.css.engine.value.svg12.TextAlignManager;
import org.apache.batik.css.engine.value.svg.OpacityManager;
import org.apache.batik.css.engine.value.svg.SVGColorManager;
import org.apache.batik.css.engine.value.svg12.MarginLengthManager;
import org.apache.batik.css.engine.value.svg12.LineHeightManager;
import org.apache.batik.css.parser.ExtendedParser;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Document;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.ValueManager;

public class SVG12CSSEngine extends SVGCSSEngine
{
    public static final ValueManager[] SVG_VALUE_MANAGERS;
    public static final ShorthandManager[] SVG_SHORTHAND_MANAGERS;
    public static final int LINE_HEIGHT_INDEX = 60;
    public static final int INDENT_INDEX = 61;
    public static final int MARGIN_BOTTOM_INDEX = 62;
    public static final int MARGIN_LEFT_INDEX = 63;
    public static final int MARGIN_RIGHT_INDEX = 64;
    public static final int MARGIN_TOP_INDEX = 65;
    public static final int SOLID_COLOR_INDEX = 66;
    public static final int SOLID_OPACITY_INDEX = 67;
    public static final int TEXT_ALIGN_INDEX = 68;
    public static final int FINAL_INDEX = 68;
    
    public SVG12CSSEngine(final Document doc, final ParsedURL uri, final ExtendedParser p, final CSSContext ctx) {
        super(doc, uri, p, SVG12CSSEngine.SVG_VALUE_MANAGERS, SVG12CSSEngine.SVG_SHORTHAND_MANAGERS, ctx);
        this.lineHeightIndex = 60;
    }
    
    public SVG12CSSEngine(final Document doc, final ParsedURL uri, final ExtendedParser p, final ValueManager[] vms, final ShorthandManager[] sms, final CSSContext ctx) {
        super(doc, uri, p, SVGCSSEngine.mergeArrays(SVG12CSSEngine.SVG_VALUE_MANAGERS, vms), SVGCSSEngine.mergeArrays(SVG12CSSEngine.SVG_SHORTHAND_MANAGERS, sms), ctx);
        this.lineHeightIndex = 60;
    }
    
    static {
        SVG_VALUE_MANAGERS = new ValueManager[] { new LineHeightManager(), new MarginLengthManager("indent"), new MarginLengthManager("margin-bottom"), new MarginLengthManager("margin-left"), new MarginLengthManager("margin-right"), new MarginLengthManager("margin-top"), new SVGColorManager("solid-color"), new OpacityManager("solid-opacity", true), new TextAlignManager() };
        SVG_SHORTHAND_MANAGERS = new ShorthandManager[] { new MarginShorthandManager() };
    }
}
