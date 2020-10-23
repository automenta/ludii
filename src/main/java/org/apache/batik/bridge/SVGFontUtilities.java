// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.gvt.font.GVTFontFace;
import org.apache.batik.gvt.font.UnresolvedFontFamily;
import java.util.StringTokenizer;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.batik.gvt.font.GVTFontFamily;
import java.util.Iterator;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.dom.NodeList;
import java.util.Map;
import org.apache.batik.css.engine.FontFaceRule;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.w3c.dom.Element;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Document;
import org.apache.batik.util.SVGConstants;

public abstract class SVGFontUtilities implements SVGConstants
{
    public static List getFontFaces(final Document doc, final BridgeContext ctx) {
        final Map fontFamilyMap = ctx.getFontFamilyMap();
        List ret = fontFamilyMap.get(doc);
        if (ret != null) {
            return ret;
        }
        ret = new LinkedList();
        final NodeList fontFaceElements = doc.getElementsByTagNameNS("http://www.w3.org/2000/svg", "font-face");
        final SVGFontFaceElementBridge fontFaceBridge = (SVGFontFaceElementBridge)ctx.getBridge("http://www.w3.org/2000/svg", "font-face");
        for (int i = 0; i < fontFaceElements.getLength(); ++i) {
            final Element fontFaceElement = (Element)fontFaceElements.item(i);
            ret.add(fontFaceBridge.createFontFace(ctx, fontFaceElement));
        }
        final CSSEngine engine = ((SVGOMDocument)doc).getCSSEngine();
        final List sms = engine.getFontFaces();
        for (final Object sm : sms) {
            final FontFaceRule ffr = (FontFaceRule)sm;
            ret.add(CSSFontFace.createCSSFontFace(engine, ffr));
        }
        return ret;
    }
    
    public static GVTFontFamily getFontFamily(final Element textElement, final BridgeContext ctx, final String fontFamilyName, final String fontWeight, final String fontStyle) {
        final String fontKeyName = fontFamilyName.toLowerCase() + " " + fontWeight + " " + fontStyle;
        final Map fontFamilyMap = ctx.getFontFamilyMap();
        final GVTFontFamily fontFamily = fontFamilyMap.get(fontKeyName);
        if (fontFamily != null) {
            return fontFamily;
        }
        final Document doc = textElement.getOwnerDocument();
        List fontFaces = fontFamilyMap.get(doc);
        if (fontFaces == null) {
            fontFaces = getFontFaces(doc, ctx);
            fontFamilyMap.put(doc, fontFaces);
        }
        final Iterator iter = fontFaces.iterator();
        final List svgFontFamilies = new LinkedList();
        while (iter.hasNext()) {
            final FontFace fontFace = iter.next();
            if (!fontFace.hasFamilyName(fontFamilyName)) {
                continue;
            }
            final String fontFaceStyle = fontFace.getFontStyle();
            if (!fontFaceStyle.equals("all") && fontFaceStyle.indexOf(fontStyle) == -1) {
                continue;
            }
            final GVTFontFamily ffam = fontFace.getFontFamily(ctx);
            if (ffam == null) {
                continue;
            }
            svgFontFamilies.add(ffam);
        }
        if (svgFontFamilies.size() == 1) {
            fontFamilyMap.put(fontKeyName, svgFontFamilies.get(0));
            return svgFontFamilies.get(0);
        }
        if (svgFontFamilies.size() > 1) {
            final String fontWeightNumber = getFontWeightNumberString(fontWeight);
            final List fontFamilyWeights = new ArrayList(svgFontFamilies.size());
            for (final Object svgFontFamily : svgFontFamilies) {
                final GVTFontFace fontFace2 = ((GVTFontFamily)svgFontFamily).getFontFace();
                String fontFaceWeight = fontFace2.getFontWeight();
                fontFaceWeight = getFontWeightNumberString(fontFaceWeight);
                fontFamilyWeights.add(fontFaceWeight);
            }
            final List newFontFamilyWeights = new ArrayList(fontFamilyWeights);
            for (int i = 100; i <= 900; i += 100) {
                final String weightString = String.valueOf(i);
                boolean matched = false;
                int minDifference = 1000;
                int minDifferenceIndex = 0;
                for (int j = 0; j < fontFamilyWeights.size(); ++j) {
                    final String fontFamilyWeight = fontFamilyWeights.get(j);
                    if (fontFamilyWeight.indexOf(weightString) > -1) {
                        matched = true;
                        break;
                    }
                    final StringTokenizer st = new StringTokenizer(fontFamilyWeight, " ,");
                    while (st.hasMoreTokens()) {
                        final int weightNum = Integer.parseInt(st.nextToken());
                        final int difference = Math.abs(weightNum - i);
                        if (difference < minDifference) {
                            minDifference = difference;
                            minDifferenceIndex = j;
                        }
                    }
                }
                if (!matched) {
                    final String newFontFamilyWeight = newFontFamilyWeights.get(minDifferenceIndex) + ", " + weightString;
                    newFontFamilyWeights.set(minDifferenceIndex, newFontFamilyWeight);
                }
            }
            for (int i = 0; i < svgFontFamilies.size(); ++i) {
                final String fontFaceWeight2 = newFontFamilyWeights.get(i);
                if (fontFaceWeight2.indexOf(fontWeightNumber) > -1) {
                    fontFamilyMap.put(fontKeyName, svgFontFamilies.get(i));
                    return svgFontFamilies.get(i);
                }
            }
            fontFamilyMap.put(fontKeyName, svgFontFamilies.get(0));
            return svgFontFamilies.get(0);
        }
        final GVTFontFamily gvtFontFamily = new UnresolvedFontFamily(fontFamilyName);
        fontFamilyMap.put(fontKeyName, gvtFontFamily);
        return gvtFontFamily;
    }
    
    protected static String getFontWeightNumberString(final String fontWeight) {
        if (fontWeight.equals("normal")) {
            return "400";
        }
        if (fontWeight.equals("bold")) {
            return "700";
        }
        if (fontWeight.equals("all")) {
            return "100, 200, 300, 400, 500, 600, 700, 800, 900";
        }
        return fontWeight;
    }
}
