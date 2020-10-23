// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;
import org.apache.batik.parser.PathParser;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.text.TextPath;
import org.w3c.dom.Element;

public class SVGTextPathElementBridge extends AnimatableGenericSVGBridge implements ErrorConstants
{
    @Override
    public String getLocalName() {
        return "textPath";
    }
    
    @Override
    public void handleElement(final BridgeContext ctx, final Element e) {
    }
    
    public TextPath createTextPath(final BridgeContext ctx, final Element textPathElement) {
        final String uri = XLinkSupport.getXLinkHref(textPathElement);
        final Element pathElement = ctx.getReferencedElement(textPathElement, uri);
        if (pathElement == null || !"http://www.w3.org/2000/svg".equals(pathElement.getNamespaceURI()) || !pathElement.getLocalName().equals("path")) {
            throw new BridgeException(ctx, textPathElement, "uri.badTarget", new Object[] { uri });
        }
        String s = pathElement.getAttributeNS(null, "d");
        Shape pathShape = null;
        if (s.length() != 0) {
            final AWTPathProducer app = new AWTPathProducer();
            app.setWindingRule(CSSUtilities.convertFillRule(pathElement));
            try {
                final PathParser pathParser = new PathParser();
                pathParser.setPathHandler(app);
                pathParser.parse(s);
            }
            catch (ParseException pEx) {
                throw new BridgeException(ctx, pathElement, pEx, "attribute.malformed", new Object[] { "d" });
            }
            finally {
                pathShape = app.getShape();
            }
            s = pathElement.getAttributeNS(null, "transform");
            if (s.length() != 0) {
                final AffineTransform tr = SVGUtilities.convertTransform(pathElement, "transform", s, ctx);
                pathShape = tr.createTransformedShape(pathShape);
            }
            final TextPath textPath = new TextPath(new GeneralPath(pathShape));
            s = textPathElement.getAttributeNS(null, "startOffset");
            if (s.length() > 0) {
                float startOffset = 0.0f;
                final int percentIndex = s.indexOf(37);
                if (percentIndex != -1) {
                    final float pathLength = textPath.lengthOfPath();
                    final String percentString = s.substring(0, percentIndex);
                    float startOffsetPercent = 0.0f;
                    try {
                        startOffsetPercent = SVGUtilities.convertSVGNumber(percentString);
                    }
                    catch (NumberFormatException e) {
                        throw new BridgeException(ctx, textPathElement, "attribute.malformed", new Object[] { "startOffset", s });
                    }
                    startOffset = (float)(startOffsetPercent * pathLength / 100.0);
                }
                else {
                    final org.apache.batik.parser.UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, textPathElement);
                    startOffset = UnitProcessor.svgOtherLengthToUserSpace(s, "startOffset", uctx);
                }
                textPath.setStartOffset(startOffset);
            }
            return textPath;
        }
        throw new BridgeException(ctx, pathElement, "attribute.missing", new Object[] { "d" });
    }
}
