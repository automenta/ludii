// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import java.awt.Shape;
import org.apache.batik.gvt.GraphicsNode;
import java.awt.geom.Point2D;
import java.util.StringTokenizer;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;
import org.apache.batik.parser.PathParser;
import org.apache.batik.parser.AWTPathProducer;
import java.awt.geom.AffineTransform;
import org.apache.batik.gvt.font.Glyph;
import org.apache.batik.gvt.text.TextPaintInfo;
import org.apache.batik.gvt.font.GVTFontFace;
import org.w3c.dom.Element;

public class SVGGlyphElementBridge extends AbstractSVGBridge implements ErrorConstants
{
    protected SVGGlyphElementBridge() {
    }
    
    @Override
    public String getLocalName() {
        return "glyph";
    }
    
    public Glyph createGlyph(final BridgeContext ctx, final Element glyphElement, final Element textElement, final int glyphCode, final float fontSize, final GVTFontFace fontFace, final TextPaintInfo tpi) {
        final float fontHeight = fontFace.getUnitsPerEm();
        final float scale = fontSize / fontHeight;
        final AffineTransform scaleTransform = AffineTransform.getScaleInstance(scale, -scale);
        final String d = glyphElement.getAttributeNS(null, "d");
        Shape dShape = null;
        if (d.length() != 0) {
            final AWTPathProducer app = new AWTPathProducer();
            app.setWindingRule(CSSUtilities.convertFillRule(textElement));
            try {
                final PathParser pathParser = new PathParser();
                pathParser.setPathHandler(app);
                pathParser.parse(d);
            }
            catch (ParseException pEx) {
                throw new BridgeException(ctx, glyphElement, pEx, "attribute.malformed", new Object[] { "d" });
            }
            finally {
                final Shape shape = app.getShape();
                final Shape transformedShape = dShape = scaleTransform.createTransformedShape(shape);
            }
        }
        final NodeList glyphChildren = glyphElement.getChildNodes();
        final int numChildren = glyphChildren.getLength();
        int numGlyphChildren = 0;
        for (int i = 0; i < numChildren; ++i) {
            final Node childNode = glyphChildren.item(i);
            if (childNode.getNodeType() == 1) {
                ++numGlyphChildren;
            }
        }
        CompositeGraphicsNode glyphContentNode = null;
        if (numGlyphChildren > 0) {
            final GVTBuilder builder = ctx.getGVTBuilder();
            glyphContentNode = new CompositeGraphicsNode();
            final Element fontElementClone = (Element)glyphElement.getParentNode().cloneNode(false);
            final NamedNodeMap fontAttributes = glyphElement.getParentNode().getAttributes();
            for (int numAttributes = fontAttributes.getLength(), j = 0; j < numAttributes; ++j) {
                fontElementClone.setAttributeNode((Attr)fontAttributes.item(j));
            }
            final Element clonedGlyphElement = (Element)glyphElement.cloneNode(true);
            fontElementClone.appendChild(clonedGlyphElement);
            textElement.appendChild(fontElementClone);
            final CompositeGraphicsNode glyphChildrenNode = new CompositeGraphicsNode();
            glyphChildrenNode.setTransform(scaleTransform);
            final NodeList clonedGlyphChildren = clonedGlyphElement.getChildNodes();
            for (int numClonedChildren = clonedGlyphChildren.getLength(), k = 0; k < numClonedChildren; ++k) {
                final Node childNode2 = clonedGlyphChildren.item(k);
                if (childNode2.getNodeType() == 1) {
                    final Element childElement = (Element)childNode2;
                    final GraphicsNode childGraphicsNode = builder.build(ctx, childElement);
                    glyphChildrenNode.add(childGraphicsNode);
                }
            }
            glyphContentNode.add(glyphChildrenNode);
            textElement.removeChild(fontElementClone);
        }
        final String unicode = glyphElement.getAttributeNS(null, "unicode");
        final String nameList = glyphElement.getAttributeNS(null, "glyph-name");
        final List names = new ArrayList();
        final StringTokenizer st = new StringTokenizer(nameList, " ,");
        while (st.hasMoreTokens()) {
            names.add(st.nextToken());
        }
        final String orientation = glyphElement.getAttributeNS(null, "orientation");
        final String arabicForm = glyphElement.getAttributeNS(null, "arabic-form");
        final String lang = glyphElement.getAttributeNS(null, "lang");
        final Element parentFontElement = (Element)glyphElement.getParentNode();
        String s = glyphElement.getAttributeNS(null, "horiz-adv-x");
        if (s.length() == 0) {
            s = parentFontElement.getAttributeNS(null, "horiz-adv-x");
            if (s.length() == 0) {
                throw new BridgeException(ctx, parentFontElement, "attribute.missing", new Object[] { "horiz-adv-x" });
            }
        }
        float horizAdvX;
        try {
            horizAdvX = SVGUtilities.convertSVGNumber(s) * scale;
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, glyphElement, nfEx, "attribute.malformed", new Object[] { "horiz-adv-x", s });
        }
        s = glyphElement.getAttributeNS(null, "vert-adv-y");
        if (s.length() == 0) {
            s = parentFontElement.getAttributeNS(null, "vert-adv-y");
            if (s.length() == 0) {
                s = String.valueOf(fontFace.getUnitsPerEm());
            }
        }
        float vertAdvY;
        try {
            vertAdvY = SVGUtilities.convertSVGNumber(s) * scale;
        }
        catch (NumberFormatException nfEx2) {
            throw new BridgeException(ctx, glyphElement, nfEx2, "attribute.malformed", new Object[] { "vert-adv-y", s });
        }
        s = glyphElement.getAttributeNS(null, "vert-origin-x");
        if (s.length() == 0) {
            s = parentFontElement.getAttributeNS(null, "vert-origin-x");
            if (s.length() == 0) {
                s = Float.toString(horizAdvX / 2.0f);
            }
        }
        float vertOriginX;
        try {
            vertOriginX = SVGUtilities.convertSVGNumber(s) * scale;
        }
        catch (NumberFormatException nfEx3) {
            throw new BridgeException(ctx, glyphElement, nfEx3, "attribute.malformed", new Object[] { "vert-origin-x", s });
        }
        s = glyphElement.getAttributeNS(null, "vert-origin-y");
        if (s.length() == 0) {
            s = parentFontElement.getAttributeNS(null, "vert-origin-y");
            if (s.length() == 0) {
                s = String.valueOf(fontFace.getAscent());
            }
        }
        float vertOriginY;
        try {
            vertOriginY = SVGUtilities.convertSVGNumber(s) * -scale;
        }
        catch (NumberFormatException nfEx4) {
            throw new BridgeException(ctx, glyphElement, nfEx4, "attribute.malformed", new Object[] { "vert-origin-y", s });
        }
        final Point2D vertOrigin = new Point2D.Float(vertOriginX, vertOriginY);
        s = parentFontElement.getAttributeNS(null, "horiz-origin-x");
        if (s.length() == 0) {
            s = "0";
        }
        float horizOriginX;
        try {
            horizOriginX = SVGUtilities.convertSVGNumber(s) * scale;
        }
        catch (NumberFormatException nfEx5) {
            throw new BridgeException(ctx, parentFontElement, nfEx5, "attribute.malformed", new Object[] { "horiz-origin-x", s });
        }
        s = parentFontElement.getAttributeNS(null, "horiz-origin-y");
        if (s.length() == 0) {
            s = "0";
        }
        float horizOriginY;
        try {
            horizOriginY = SVGUtilities.convertSVGNumber(s) * -scale;
        }
        catch (NumberFormatException nfEx6) {
            throw new BridgeException(ctx, glyphElement, nfEx6, "attribute.malformed", new Object[] { "horiz-origin-y", s });
        }
        final Point2D horizOrigin = new Point2D.Float(horizOriginX, horizOriginY);
        return new Glyph(unicode, names, orientation, arabicForm, lang, horizOrigin, vertOrigin, horizAdvX, vertAdvY, glyphCode, tpi, dShape, glyphContentNode);
    }
}
