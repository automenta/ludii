// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.Node;
import org.apache.batik.gvt.GraphicsNode;
import java.awt.geom.Point2D;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import java.awt.Shape;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.Marker;
import org.w3c.dom.Element;

public class SVGMarkerElementBridge extends AnimatableGenericSVGBridge implements MarkerBridge, ErrorConstants
{
    protected SVGMarkerElementBridge() {
    }
    
    @Override
    public String getLocalName() {
        return "marker";
    }
    
    @Override
    public Marker createMarker(final BridgeContext ctx, final Element markerElement, final Element paintedElement) {
        final GVTBuilder builder = ctx.getGVTBuilder();
        CompositeGraphicsNode markerContentNode = new CompositeGraphicsNode();
        boolean hasChildren = false;
        for (Node n = markerElement.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                final Element child = (Element)n;
                final GraphicsNode markerNode = builder.build(ctx, child);
                if (markerNode != null) {
                    hasChildren = true;
                    markerContentNode.getChildren().add(markerNode);
                }
            }
        }
        if (!hasChildren) {
            return null;
        }
        final org.apache.batik.parser.UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, paintedElement);
        float markerWidth = 3.0f;
        String s = markerElement.getAttributeNS(null, "markerWidth");
        if (s.length() != 0) {
            markerWidth = UnitProcessor.svgHorizontalLengthToUserSpace(s, "markerWidth", uctx);
        }
        if (markerWidth == 0.0f) {
            return null;
        }
        float markerHeight = 3.0f;
        s = markerElement.getAttributeNS(null, "markerHeight");
        if (s.length() != 0) {
            markerHeight = UnitProcessor.svgVerticalLengthToUserSpace(s, "markerHeight", uctx);
        }
        if (markerHeight == 0.0f) {
            return null;
        }
        s = markerElement.getAttributeNS(null, "orient");
        double orient;
        if (s.length() == 0) {
            orient = 0.0;
        }
        else if ("auto".equals(s)) {
            orient = Double.NaN;
        }
        else {
            try {
                orient = SVGUtilities.convertSVGNumber(s);
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(ctx, markerElement, nfEx, "attribute.malformed", new Object[] { "orient", s });
            }
        }
        final Value val = CSSUtilities.getComputedStyle(paintedElement, 52);
        final float strokeWidth = val.getFloatValue();
        s = markerElement.getAttributeNS(null, "markerUnits");
        short unitsType;
        if (s.length() == 0) {
            unitsType = 3;
        }
        else {
            unitsType = SVGUtilities.parseMarkerCoordinateSystem(markerElement, "markerUnits", s, ctx);
        }
        AffineTransform markerTxf;
        if (unitsType == 3) {
            markerTxf = new AffineTransform();
            markerTxf.scale(strokeWidth, strokeWidth);
        }
        else {
            markerTxf = new AffineTransform();
        }
        final AffineTransform preserveAspectRatioTransform = ViewBox.getPreserveAspectRatioTransform(markerElement, markerWidth, markerHeight, ctx);
        if (preserveAspectRatioTransform == null) {
            return null;
        }
        markerTxf.concatenate(preserveAspectRatioTransform);
        markerContentNode.setTransform(markerTxf);
        if (CSSUtilities.convertOverflow(markerElement)) {
            final float[] offsets = CSSUtilities.convertClip(markerElement);
            Rectangle2D markerClip;
            if (offsets == null) {
                markerClip = new Rectangle2D.Float(0.0f, 0.0f, strokeWidth * markerWidth, strokeWidth * markerHeight);
            }
            else {
                markerClip = new Rectangle2D.Float(offsets[3], offsets[0], strokeWidth * markerWidth - offsets[1] - offsets[3], strokeWidth * markerHeight - offsets[2] - offsets[0]);
            }
            final CompositeGraphicsNode comp = new CompositeGraphicsNode();
            comp.getChildren().add(markerContentNode);
            final Filter clipSrc = comp.getGraphicsNodeRable(true);
            comp.setClip(new ClipRable8Bit(clipSrc, markerClip));
            markerContentNode = comp;
        }
        float refX = 0.0f;
        s = markerElement.getAttributeNS(null, "refX");
        if (s.length() != 0) {
            refX = UnitProcessor.svgHorizontalCoordinateToUserSpace(s, "refX", uctx);
        }
        float refY = 0.0f;
        s = markerElement.getAttributeNS(null, "refY");
        if (s.length() != 0) {
            refY = UnitProcessor.svgVerticalCoordinateToUserSpace(s, "refY", uctx);
        }
        final float[] ref = { refX, refY };
        markerTxf.transform(ref, 0, ref, 0, 1);
        final Marker marker = new Marker(markerContentNode, new Point2D.Float(ref[0], ref[1]), orient);
        return marker;
    }
}
