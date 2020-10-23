// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.font.TextAttribute;
import org.apache.batik.css.engine.value.Value;
import java.util.StringTokenizer;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.apache.batik.util.CSSConstants;

public abstract class TextUtilities implements CSSConstants, ErrorConstants
{
    public static String getElementContent(final Element e) {
        final StringBuffer result = new StringBuffer();
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            switch (n.getNodeType()) {
                case 1: {
                    result.append(getElementContent((Element)n));
                    break;
                }
                case 3:
                case 4: {
                    result.append(n.getNodeValue());
                    break;
                }
            }
        }
        return result.toString();
    }
    
    public static ArrayList svgHorizontalCoordinateArrayToUserSpace(final Element element, final String attrName, final String valueStr, final BridgeContext ctx) {
        final org.apache.batik.parser.UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, element);
        final ArrayList values = new ArrayList();
        final StringTokenizer st = new StringTokenizer(valueStr, ", ", false);
        while (st.hasMoreTokens()) {
            values.add(UnitProcessor.svgHorizontalCoordinateToUserSpace(st.nextToken(), attrName, uctx));
        }
        return values;
    }
    
    public static ArrayList svgVerticalCoordinateArrayToUserSpace(final Element element, final String attrName, final String valueStr, final BridgeContext ctx) {
        final org.apache.batik.parser.UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, element);
        final ArrayList values = new ArrayList();
        final StringTokenizer st = new StringTokenizer(valueStr, ", ", false);
        while (st.hasMoreTokens()) {
            values.add(UnitProcessor.svgVerticalCoordinateToUserSpace(st.nextToken(), attrName, uctx));
        }
        return values;
    }
    
    public static ArrayList svgRotateArrayToFloats(final Element element, final String attrName, final String valueStr, final BridgeContext ctx) {
        final StringTokenizer st = new StringTokenizer(valueStr, ", ", false);
        final ArrayList values = new ArrayList();
        while (st.hasMoreTokens()) {
            try {
                final String s = st.nextToken();
                values.add((float)Math.toRadians(SVGUtilities.convertSVGNumber(s)));
                continue;
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(ctx, element, nfEx, "attribute.malformed", new Object[] { attrName, valueStr });
            }
            break;
        }
        return values;
    }
    
    public static Float convertFontSize(final Element e) {
        final Value v = CSSUtilities.getComputedStyle(e, 22);
        return v.getFloatValue();
    }
    
    public static Float convertFontStyle(final Element e) {
        final Value v = CSSUtilities.getComputedStyle(e, 25);
        switch (v.getStringValue().charAt(0)) {
            case 'n': {
                return TextAttribute.POSTURE_REGULAR;
            }
            default: {
                return TextAttribute.POSTURE_OBLIQUE;
            }
        }
    }
    
    public static Float convertFontStretch(final Element e) {
        final Value v = CSSUtilities.getComputedStyle(e, 24);
        final String s = v.getStringValue();
        switch (s.charAt(0)) {
            case 'u': {
                if (s.charAt(6) == 'c') {
                    return TextAttribute.WIDTH_CONDENSED;
                }
                return TextAttribute.WIDTH_EXTENDED;
            }
            case 'e': {
                if (s.charAt(6) == 'c') {
                    return TextAttribute.WIDTH_CONDENSED;
                }
                if (s.length() == 8) {
                    return TextAttribute.WIDTH_SEMI_EXTENDED;
                }
                return TextAttribute.WIDTH_EXTENDED;
            }
            case 's': {
                if (s.charAt(6) == 'c') {
                    return TextAttribute.WIDTH_SEMI_CONDENSED;
                }
                return TextAttribute.WIDTH_SEMI_EXTENDED;
            }
            default: {
                return TextAttribute.WIDTH_REGULAR;
            }
        }
    }
    
    public static Float convertFontWeight(final Element e) {
        final Value v = CSSUtilities.getComputedStyle(e, 27);
        final int weight = (int)v.getFloatValue();
        switch (weight) {
            case 100: {
                return TextAttribute.WEIGHT_EXTRA_LIGHT;
            }
            case 200: {
                return TextAttribute.WEIGHT_LIGHT;
            }
            case 300: {
                return TextAttribute.WEIGHT_DEMILIGHT;
            }
            case 400: {
                return TextAttribute.WEIGHT_REGULAR;
            }
            case 500: {
                return TextAttribute.WEIGHT_SEMIBOLD;
            }
            default: {
                final String javaVersionString = System.getProperty("java.specification.version");
                final float javaVersion = (javaVersionString != null) ? Float.parseFloat(javaVersionString) : 1.5f;
                if (javaVersion < 1.5) {
                    return TextAttribute.WEIGHT_BOLD;
                }
                switch (weight) {
                    case 600: {
                        return TextAttribute.WEIGHT_MEDIUM;
                    }
                    case 700: {
                        return TextAttribute.WEIGHT_BOLD;
                    }
                    case 800: {
                        return TextAttribute.WEIGHT_HEAVY;
                    }
                    case 900: {
                        return TextAttribute.WEIGHT_ULTRABOLD;
                    }
                    default: {
                        return TextAttribute.WEIGHT_REGULAR;
                    }
                }
                break;
            }
        }
    }
    
    public static TextNode.Anchor convertTextAnchor(final Element e) {
        final Value v = CSSUtilities.getComputedStyle(e, 53);
        switch (v.getStringValue().charAt(0)) {
            case 's': {
                return TextNode.Anchor.START;
            }
            case 'm': {
                return TextNode.Anchor.MIDDLE;
            }
            default: {
                return TextNode.Anchor.END;
            }
        }
    }
    
    public static Object convertBaselineShift(final Element e) {
        final Value v = CSSUtilities.getComputedStyle(e, 1);
        if (v.getPrimitiveType() != 21) {
            return v.getFloatValue();
        }
        final String s = v.getStringValue();
        switch (s.charAt(2)) {
            case 'p': {
                return TextAttribute.SUPERSCRIPT_SUPER;
            }
            case 'b': {
                return TextAttribute.SUPERSCRIPT_SUB;
            }
            default: {
                return null;
            }
        }
    }
    
    public static Float convertKerning(final Element e) {
        final Value v = CSSUtilities.getComputedStyle(e, 31);
        if (v.getPrimitiveType() == 21) {
            return null;
        }
        return v.getFloatValue();
    }
    
    public static Float convertLetterSpacing(final Element e) {
        final Value v = CSSUtilities.getComputedStyle(e, 32);
        if (v.getPrimitiveType() == 21) {
            return null;
        }
        return v.getFloatValue();
    }
    
    public static Float convertWordSpacing(final Element e) {
        final Value v = CSSUtilities.getComputedStyle(e, 58);
        if (v.getPrimitiveType() == 21) {
            return null;
        }
        return v.getFloatValue();
    }
}
