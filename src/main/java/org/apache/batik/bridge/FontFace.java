// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractNode;
import org.w3c.dom.svg.SVGDocument;
import java.util.Iterator;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.gvt.font.GVTFontFamily;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.batik.gvt.font.GVTFontFace;

public abstract class FontFace extends GVTFontFace implements ErrorConstants
{
    List srcs;
    
    public FontFace(final List srcs, final String familyName, final float unitsPerEm, final String fontWeight, final String fontStyle, final String fontVariant, final String fontStretch, final float slope, final String panose1, final float ascent, final float descent, final float strikethroughPosition, final float strikethroughThickness, final float underlinePosition, final float underlineThickness, final float overlinePosition, final float overlineThickness) {
        super(familyName, unitsPerEm, fontWeight, fontStyle, fontVariant, fontStretch, slope, panose1, ascent, descent, strikethroughPosition, strikethroughThickness, underlinePosition, underlineThickness, overlinePosition, overlineThickness);
        this.srcs = srcs;
    }
    
    protected FontFace(final String familyName) {
        super(familyName);
    }
    
    public static CSSFontFace createFontFace(final String familyName, final FontFace src) {
        return new CSSFontFace(new LinkedList(src.srcs), familyName, src.unitsPerEm, src.fontWeight, src.fontStyle, src.fontVariant, src.fontStretch, src.slope, src.panose1, src.ascent, src.descent, src.strikethroughPosition, src.strikethroughThickness, src.underlinePosition, src.underlineThickness, src.overlinePosition, src.overlineThickness);
    }
    
    public GVTFontFamily getFontFamily(final BridgeContext ctx) {
        final FontFamilyResolver fontFamilyResolver = ctx.getFontFamilyResolver();
        GVTFontFamily family = fontFamilyResolver.resolve(this.familyName, this);
        if (family != null) {
            return family;
        }
        for (final Object o : this.srcs) {
            if (o instanceof String) {
                family = fontFamilyResolver.resolve((String)o, this);
                if (family != null) {
                    return family;
                }
                continue;
            }
            else {
                if (!(o instanceof ParsedURL)) {
                    continue;
                }
                try {
                    final GVTFontFamily ff = this.getFontFamily(ctx, (ParsedURL)o);
                    if (ff != null) {
                        return ff;
                    }
                    continue;
                }
                catch (SecurityException ex) {
                    ctx.getUserAgent().displayError(ex);
                }
                catch (BridgeException ex2) {
                    if (!"uri.unsecure".equals(ex2.getCode())) {
                        continue;
                    }
                    ctx.getUserAgent().displayError(ex2);
                }
                catch (Exception ex3) {}
            }
        }
        return null;
    }
    
    protected GVTFontFamily getFontFamily(final BridgeContext ctx, ParsedURL purl) {
        final String purlStr = purl.toString();
        final Element e = this.getBaseElement(ctx);
        final SVGDocument svgDoc = (SVGDocument)e.getOwnerDocument();
        final String docURL = svgDoc.getURL();
        ParsedURL pDocURL = null;
        if (docURL != null) {
            pDocURL = new ParsedURL(docURL);
        }
        final String baseURI = AbstractNode.getBaseURI(e);
        purl = new ParsedURL(baseURI, purlStr);
        final UserAgent userAgent = ctx.getUserAgent();
        try {
            userAgent.checkLoadExternalResource(purl, pDocURL);
        }
        catch (SecurityException ex) {
            userAgent.displayError(ex);
            return null;
        }
        if (purl.getRef() != null) {
            final Element ref = ctx.getReferencedElement(e, purlStr);
            if (!ref.getNamespaceURI().equals("http://www.w3.org/2000/svg") || !ref.getLocalName().equals("font")) {
                return null;
            }
            final SVGDocument doc = (SVGDocument)e.getOwnerDocument();
            final SVGDocument rdoc = (SVGDocument)ref.getOwnerDocument();
            Element fontElt = ref;
            if (doc != rdoc) {
                fontElt = (Element)doc.importNode(ref, true);
                final String base = AbstractNode.getBaseURI(ref);
                final Element g = doc.createElementNS("http://www.w3.org/2000/svg", "g");
                g.appendChild(fontElt);
                g.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:base", base);
                CSSUtilities.computeStyleAndURIs(ref, fontElt, purlStr);
            }
            Element fontFaceElt = null;
            for (Node n = fontElt.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n.getNodeType() == 1 && n.getNamespaceURI().equals("http://www.w3.org/2000/svg") && n.getLocalName().equals("font-face")) {
                    fontFaceElt = (Element)n;
                    break;
                }
            }
            final SVGFontFaceElementBridge fontFaceBridge = (SVGFontFaceElementBridge)ctx.getBridge("http://www.w3.org/2000/svg", "font-face");
            final GVTFontFace gff = fontFaceBridge.createFontFace(ctx, fontFaceElt);
            return new SVGFontFamily(gff, fontElt, ctx);
        }
        else {
            try {
                return ctx.getFontFamilyResolver().loadFont(purl.openStream(), this);
            }
            catch (Exception ex2) {
                return null;
            }
        }
    }
    
    protected Element getBaseElement(final BridgeContext ctx) {
        final SVGDocument d = (SVGDocument)ctx.getDocument();
        return d.getRootElement();
    }
}
