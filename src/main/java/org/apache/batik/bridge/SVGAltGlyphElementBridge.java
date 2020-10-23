// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.font.GVTFontFace;
import org.apache.batik.gvt.text.TextPaintInfo;
import org.w3c.dom.NodeList;
import org.apache.batik.dom.AbstractNode;
import org.w3c.dom.Node;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.font.Glyph;
import org.w3c.dom.Element;
import java.text.AttributedCharacterIterator;

public class SVGAltGlyphElementBridge extends AbstractSVGBridge implements ErrorConstants
{
    public static final AttributedCharacterIterator.Attribute PAINT_INFO;
    
    @Override
    public String getLocalName() {
        return "altGlyph";
    }
    
    public Glyph[] createAltGlyphArray(final BridgeContext ctx, final Element altGlyphElement, final float fontSize, final AttributedCharacterIterator aci) {
        final String uri = XLinkSupport.getXLinkHref(altGlyphElement);
        Element refElement = null;
        try {
            refElement = ctx.getReferencedElement(altGlyphElement, uri);
        }
        catch (BridgeException e) {
            if ("uri.unsecure".equals(e.getCode())) {
                ctx.getUserAgent().displayError(e);
            }
        }
        if (refElement == null) {
            return null;
        }
        if (!"http://www.w3.org/2000/svg".equals(refElement.getNamespaceURI())) {
            return null;
        }
        if (!refElement.getLocalName().equals("glyph")) {
            if (refElement.getLocalName().equals("altGlyphDef")) {
                final SVGOMDocument document = (SVGOMDocument)altGlyphElement.getOwnerDocument();
                final SVGOMDocument refDocument = (SVGOMDocument)refElement.getOwnerDocument();
                final boolean isLocal = refDocument == document;
                final Element localRefElement = (Element)(isLocal ? refElement : document.importNode(refElement, true));
                if (!isLocal) {
                    final String base = AbstractNode.getBaseURI(altGlyphElement);
                    final Element g = document.createElementNS("http://www.w3.org/2000/svg", "g");
                    g.appendChild(localRefElement);
                    g.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:base", base);
                    CSSUtilities.computeStyleAndURIs(refElement, localRefElement, uri);
                }
                final NodeList altGlyphDefChildren = localRefElement.getChildNodes();
                boolean containsGlyphRefNodes = false;
                for (int numAltGlyphDefChildren = altGlyphDefChildren.getLength(), i = 0; i < numAltGlyphDefChildren; ++i) {
                    final Node altGlyphChild = altGlyphDefChildren.item(i);
                    if (altGlyphChild.getNodeType() == 1) {
                        final Element agc = (Element)altGlyphChild;
                        if ("http://www.w3.org/2000/svg".equals(agc.getNamespaceURI()) && "glyphRef".equals(agc.getLocalName())) {
                            containsGlyphRefNodes = true;
                            break;
                        }
                    }
                }
                if (containsGlyphRefNodes) {
                    final NodeList glyphRefNodes = localRefElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "glyphRef");
                    final int numGlyphRefNodes = glyphRefNodes.getLength();
                    final Glyph[] glyphArray = new Glyph[numGlyphRefNodes];
                    for (int j = 0; j < numGlyphRefNodes; ++j) {
                        final Element glyphRefElement = (Element)glyphRefNodes.item(j);
                        final String glyphUri = XLinkSupport.getXLinkHref(glyphRefElement);
                        final Glyph glyph = this.getGlyph(ctx, glyphUri, glyphRefElement, fontSize, aci);
                        if (glyph == null) {
                            return null;
                        }
                        glyphArray[j] = glyph;
                    }
                    return glyphArray;
                }
                final NodeList altGlyphItemNodes = localRefElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "altGlyphItem");
                final int numAltGlyphItemNodes = altGlyphItemNodes.getLength();
                if (numAltGlyphItemNodes > 0) {
                    boolean foundMatchingGlyph = false;
                    Glyph[] glyphArray2 = null;
                    for (int k = 0; k < numAltGlyphItemNodes && !foundMatchingGlyph; ++k) {
                        final Element altGlyphItemElement = (Element)altGlyphItemNodes.item(k);
                        final NodeList altGlyphRefNodes = altGlyphItemElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "glyphRef");
                        final int numAltGlyphRefNodes = altGlyphRefNodes.getLength();
                        glyphArray2 = new Glyph[numAltGlyphRefNodes];
                        foundMatchingGlyph = true;
                        for (int l = 0; l < numAltGlyphRefNodes; ++l) {
                            final Element glyphRefElement2 = (Element)altGlyphRefNodes.item(l);
                            final String glyphUri2 = XLinkSupport.getXLinkHref(glyphRefElement2);
                            final Glyph glyph2 = this.getGlyph(ctx, glyphUri2, glyphRefElement2, fontSize, aci);
                            if (glyph2 == null) {
                                foundMatchingGlyph = false;
                                break;
                            }
                            glyphArray2[l] = glyph2;
                        }
                    }
                    if (!foundMatchingGlyph) {
                        return null;
                    }
                    return glyphArray2;
                }
            }
            return null;
        }
        final Glyph glyph3 = this.getGlyph(ctx, uri, altGlyphElement, fontSize, aci);
        if (glyph3 == null) {
            return null;
        }
        final Glyph[] glyphArray3 = { glyph3 };
        return glyphArray3;
    }
    
    private Glyph getGlyph(final BridgeContext ctx, final String glyphUri, final Element altGlyphElement, final float fontSize, final AttributedCharacterIterator aci) {
        Element refGlyphElement = null;
        try {
            refGlyphElement = ctx.getReferencedElement(altGlyphElement, glyphUri);
        }
        catch (BridgeException e) {
            if ("uri.unsecure".equals(e.getCode())) {
                ctx.getUserAgent().displayError(e);
            }
        }
        if (refGlyphElement == null || !"http://www.w3.org/2000/svg".equals(refGlyphElement.getNamespaceURI()) || !"glyph".equals(refGlyphElement.getLocalName())) {
            return null;
        }
        final SVGOMDocument document = (SVGOMDocument)altGlyphElement.getOwnerDocument();
        final SVGOMDocument refDocument = (SVGOMDocument)refGlyphElement.getOwnerDocument();
        final boolean isLocal = refDocument == document;
        Element localGlyphElement = null;
        Element localFontFaceElement = null;
        Element localFontElement = null;
        if (isLocal) {
            localGlyphElement = refGlyphElement;
            localFontElement = (Element)localGlyphElement.getParentNode();
            final NodeList fontFaceElements = localFontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "font-face");
            if (fontFaceElements.getLength() > 0) {
                localFontFaceElement = (Element)fontFaceElements.item(0);
            }
        }
        else {
            localFontElement = (Element)document.importNode(refGlyphElement.getParentNode(), true);
            final String base = AbstractNode.getBaseURI(altGlyphElement);
            final Element g = document.createElementNS("http://www.w3.org/2000/svg", "g");
            g.appendChild(localFontElement);
            g.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:base", base);
            CSSUtilities.computeStyleAndURIs((Element)refGlyphElement.getParentNode(), localFontElement, glyphUri);
            final String glyphId = refGlyphElement.getAttributeNS(null, "id");
            final NodeList glyphElements = localFontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "glyph");
            for (int i = 0; i < glyphElements.getLength(); ++i) {
                final Element glyphElem = (Element)glyphElements.item(i);
                if (glyphElem.getAttributeNS(null, "id").equals(glyphId)) {
                    localGlyphElement = glyphElem;
                    break;
                }
            }
            final NodeList fontFaceElements2 = localFontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "font-face");
            if (fontFaceElements2.getLength() > 0) {
                localFontFaceElement = (Element)fontFaceElements2.item(0);
            }
        }
        if (localGlyphElement == null || localFontFaceElement == null) {
            return null;
        }
        final SVGFontFaceElementBridge fontFaceBridge = (SVGFontFaceElementBridge)ctx.getBridge(localFontFaceElement);
        final SVGFontFace fontFace = fontFaceBridge.createFontFace(ctx, localFontFaceElement);
        final SVGGlyphElementBridge glyphBridge = (SVGGlyphElementBridge)ctx.getBridge(localGlyphElement);
        aci.first();
        final TextPaintInfo tpi = (TextPaintInfo)aci.getAttribute(SVGAltGlyphElementBridge.PAINT_INFO);
        return glyphBridge.createGlyph(ctx, localGlyphElement, altGlyphElement, -1, fontSize, fontFace, tpi);
    }
    
    static {
        PAINT_INFO = GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO;
    }
}
