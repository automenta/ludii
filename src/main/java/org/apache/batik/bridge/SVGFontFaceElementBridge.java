// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.util.XLinkSupport;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Element;

public class SVGFontFaceElementBridge extends AbstractSVGBridge implements ErrorConstants
{
    @Override
    public String getLocalName() {
        return "font-face";
    }
    
    public SVGFontFace createFontFace(final BridgeContext ctx, final Element fontFaceElement) {
        final String familyNames = fontFaceElement.getAttributeNS(null, "font-family");
        String unitsPerEmStr = fontFaceElement.getAttributeNS(null, "units-per-em");
        if (unitsPerEmStr.length() == 0) {
            unitsPerEmStr = "1000";
        }
        float unitsPerEm;
        try {
            unitsPerEm = SVGUtilities.convertSVGNumber(unitsPerEmStr);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, fontFaceElement, nfEx, "attribute.malformed", new Object[] { "units-per-em", unitsPerEmStr });
        }
        String fontWeight = fontFaceElement.getAttributeNS(null, "font-weight");
        if (fontWeight.length() == 0) {
            fontWeight = "all";
        }
        String fontStyle = fontFaceElement.getAttributeNS(null, "font-style");
        if (fontStyle.length() == 0) {
            fontStyle = "all";
        }
        String fontVariant = fontFaceElement.getAttributeNS(null, "font-variant");
        if (fontVariant.length() == 0) {
            fontVariant = "normal";
        }
        String fontStretch = fontFaceElement.getAttributeNS(null, "font-stretch");
        if (fontStretch.length() == 0) {
            fontStretch = "normal";
        }
        String slopeStr = fontFaceElement.getAttributeNS(null, "slope");
        if (slopeStr.length() == 0) {
            slopeStr = "0";
        }
        float slope;
        try {
            slope = SVGUtilities.convertSVGNumber(slopeStr);
        }
        catch (NumberFormatException nfEx2) {
            throw new BridgeException(ctx, fontFaceElement, nfEx2, "attribute.malformed", new Object[] { "0", slopeStr });
        }
        String panose1 = fontFaceElement.getAttributeNS(null, "panose-1");
        if (panose1.length() == 0) {
            panose1 = "0 0 0 0 0 0 0 0 0 0";
        }
        String ascentStr = fontFaceElement.getAttributeNS(null, "ascent");
        if (ascentStr.length() == 0) {
            ascentStr = String.valueOf(unitsPerEm * 0.8);
        }
        float ascent;
        try {
            ascent = SVGUtilities.convertSVGNumber(ascentStr);
        }
        catch (NumberFormatException nfEx3) {
            throw new BridgeException(ctx, fontFaceElement, nfEx3, "attribute.malformed", new Object[] { "0", ascentStr });
        }
        String descentStr = fontFaceElement.getAttributeNS(null, "descent");
        if (descentStr.length() == 0) {
            descentStr = String.valueOf(unitsPerEm * 0.2);
        }
        float descent;
        try {
            descent = SVGUtilities.convertSVGNumber(descentStr);
        }
        catch (NumberFormatException nfEx4) {
            throw new BridgeException(ctx, fontFaceElement, nfEx4, "attribute.malformed", new Object[] { "0", descentStr });
        }
        String underlinePosStr = fontFaceElement.getAttributeNS(null, "underline-position");
        if (underlinePosStr.length() == 0) {
            underlinePosStr = String.valueOf(-3.0f * unitsPerEm / 40.0f);
        }
        float underlinePos;
        try {
            underlinePos = SVGUtilities.convertSVGNumber(underlinePosStr);
        }
        catch (NumberFormatException nfEx5) {
            throw new BridgeException(ctx, fontFaceElement, nfEx5, "attribute.malformed", new Object[] { "0", underlinePosStr });
        }
        String underlineThicknessStr = fontFaceElement.getAttributeNS(null, "underline-thickness");
        if (underlineThicknessStr.length() == 0) {
            underlineThicknessStr = String.valueOf(unitsPerEm / 20.0f);
        }
        float underlineThickness;
        try {
            underlineThickness = SVGUtilities.convertSVGNumber(underlineThicknessStr);
        }
        catch (NumberFormatException nfEx6) {
            throw new BridgeException(ctx, fontFaceElement, nfEx6, "attribute.malformed", new Object[] { "0", underlineThicknessStr });
        }
        String strikethroughPosStr = fontFaceElement.getAttributeNS(null, "strikethrough-position");
        if (strikethroughPosStr.length() == 0) {
            strikethroughPosStr = String.valueOf(3.0f * ascent / 8.0f);
        }
        float strikethroughPos;
        try {
            strikethroughPos = SVGUtilities.convertSVGNumber(strikethroughPosStr);
        }
        catch (NumberFormatException nfEx7) {
            throw new BridgeException(ctx, fontFaceElement, nfEx7, "attribute.malformed", new Object[] { "0", strikethroughPosStr });
        }
        String strikethroughThicknessStr = fontFaceElement.getAttributeNS(null, "strikethrough-thickness");
        if (strikethroughThicknessStr.length() == 0) {
            strikethroughThicknessStr = String.valueOf(unitsPerEm / 20.0f);
        }
        float strikethroughThickness;
        try {
            strikethroughThickness = SVGUtilities.convertSVGNumber(strikethroughThicknessStr);
        }
        catch (NumberFormatException nfEx8) {
            throw new BridgeException(ctx, fontFaceElement, nfEx8, "attribute.malformed", new Object[] { "0", strikethroughThicknessStr });
        }
        String overlinePosStr = fontFaceElement.getAttributeNS(null, "overline-position");
        if (overlinePosStr.length() == 0) {
            overlinePosStr = String.valueOf(ascent);
        }
        float overlinePos;
        try {
            overlinePos = SVGUtilities.convertSVGNumber(overlinePosStr);
        }
        catch (NumberFormatException nfEx9) {
            throw new BridgeException(ctx, fontFaceElement, nfEx9, "attribute.malformed", new Object[] { "0", overlinePosStr });
        }
        String overlineThicknessStr = fontFaceElement.getAttributeNS(null, "overline-thickness");
        if (overlineThicknessStr.length() == 0) {
            overlineThicknessStr = String.valueOf(unitsPerEm / 20.0f);
        }
        float overlineThickness;
        try {
            overlineThickness = SVGUtilities.convertSVGNumber(overlineThicknessStr);
        }
        catch (NumberFormatException nfEx10) {
            throw new BridgeException(ctx, fontFaceElement, nfEx10, "attribute.malformed", new Object[] { "0", overlineThicknessStr });
        }
        List srcs = null;
        final Element fontElt = SVGUtilities.getParentElement(fontFaceElement);
        if (!fontElt.getNamespaceURI().equals("http://www.w3.org/2000/svg") || !fontElt.getLocalName().equals("font")) {
            srcs = this.getFontFaceSrcs(fontFaceElement);
        }
        return new SVGFontFace(fontFaceElement, srcs, familyNames, unitsPerEm, fontWeight, fontStyle, fontVariant, fontStretch, slope, panose1, ascent, descent, strikethroughPos, strikethroughThickness, underlinePos, underlineThickness, overlinePos, overlineThickness);
    }
    
    public List getFontFaceSrcs(final Element fontFaceElement) {
        Element ffsrc = null;
        for (Node n = fontFaceElement.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1 && n.getNamespaceURI().equals("http://www.w3.org/2000/svg") && n.getLocalName().equals("font-face-src")) {
                ffsrc = (Element)n;
                break;
            }
        }
        if (ffsrc == null) {
            return null;
        }
        final List ret = new LinkedList();
        for (Node n2 = ffsrc.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
            if (n2.getNodeType() == 1) {
                if (n2.getNamespaceURI().equals("http://www.w3.org/2000/svg")) {
                    if (n2.getLocalName().equals("font-face-uri")) {
                        final Element ffuri = (Element)n2;
                        final String uri = XLinkSupport.getXLinkHref(ffuri);
                        final String base = AbstractNode.getBaseURI(ffuri);
                        ParsedURL purl;
                        if (base != null) {
                            purl = new ParsedURL(base, uri);
                        }
                        else {
                            purl = new ParsedURL(uri);
                        }
                        ret.add(purl);
                    }
                    else if (n2.getLocalName().equals("font-face-name")) {
                        final Element ffname = (Element)n2;
                        final String s = ffname.getAttribute("name");
                        if (s.length() != 0) {
                            ret.add(s);
                        }
                    }
                }
            }
        }
        return ret;
    }
}
