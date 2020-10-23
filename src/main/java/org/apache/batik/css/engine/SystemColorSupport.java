// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine;

import java.util.HashMap;
import org.apache.batik.css.engine.value.RGBColorValue;
import org.apache.batik.css.engine.value.FloatValue;
import java.awt.SystemColor;
import org.apache.batik.css.engine.value.Value;
import java.util.Map;
import org.apache.batik.util.CSSConstants;

public class SystemColorSupport implements CSSConstants
{
    protected static final Map factories;
    
    public static Value getSystemColor(String ident) {
        ident = ident.toLowerCase();
        final SystemColor sc = SystemColorSupport.factories.get(ident);
        return new RGBColorValue(new FloatValue((short)1, (float)sc.getRed()), new FloatValue((short)1, (float)sc.getGreen()), new FloatValue((short)1, (float)sc.getBlue()));
    }
    
    protected SystemColorSupport() {
    }
    
    static {
        (factories = new HashMap()).put("activeborder", SystemColor.windowBorder);
        SystemColorSupport.factories.put("activecaption", SystemColor.activeCaption);
        SystemColorSupport.factories.put("appworkspace", SystemColor.desktop);
        SystemColorSupport.factories.put("background", SystemColor.desktop);
        SystemColorSupport.factories.put("buttonface", SystemColor.control);
        SystemColorSupport.factories.put("buttonhighlight", SystemColor.controlLtHighlight);
        SystemColorSupport.factories.put("buttonshadow", SystemColor.controlDkShadow);
        SystemColorSupport.factories.put("buttontext", SystemColor.controlText);
        SystemColorSupport.factories.put("captiontext", SystemColor.activeCaptionText);
        SystemColorSupport.factories.put("graytext", SystemColor.textInactiveText);
        SystemColorSupport.factories.put("highlight", SystemColor.textHighlight);
        SystemColorSupport.factories.put("highlighttext", SystemColor.textHighlightText);
        SystemColorSupport.factories.put("inactiveborder", SystemColor.windowBorder);
        SystemColorSupport.factories.put("inactivecaption", SystemColor.inactiveCaption);
        SystemColorSupport.factories.put("inactivecaptiontext", SystemColor.inactiveCaptionText);
        SystemColorSupport.factories.put("infobackground", SystemColor.info);
        SystemColorSupport.factories.put("infotext", SystemColor.infoText);
        SystemColorSupport.factories.put("menu", SystemColor.menu);
        SystemColorSupport.factories.put("menutext", SystemColor.menuText);
        SystemColorSupport.factories.put("scrollbar", SystemColor.scrollbar);
        SystemColorSupport.factories.put("threeddarkshadow", SystemColor.controlDkShadow);
        SystemColorSupport.factories.put("threedface", SystemColor.control);
        SystemColorSupport.factories.put("threedhighlight", SystemColor.controlHighlight);
        SystemColorSupport.factories.put("threedlightshadow", SystemColor.controlLtHighlight);
        SystemColorSupport.factories.put("threedshadow", SystemColor.controlShadow);
        SystemColorSupport.factories.put("window", SystemColor.window);
        SystemColorSupport.factories.put("windowframe", SystemColor.windowBorder);
        SystemColorSupport.factories.put("windowtext", SystemColor.windowText);
    }
}
