// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.w3c.dom.Node;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import java.awt.RenderingHints;
import org.apache.batik.gvt.ShapeNode;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import org.apache.batik.anim.dom.SVGOMUseElement;
import java.awt.geom.Area;
import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGClipPathElementBridge extends AnimatableGenericSVGBridge implements ClipBridge
{
    @Override
    public String getLocalName() {
        return "clipPath";
    }
    
    @Override
    public ClipRable createClip(final BridgeContext ctx, final Element clipElement, final Element clipedElement, final GraphicsNode clipedNode) {
        String s = clipElement.getAttributeNS(null, "transform");
        AffineTransform Tx;
        if (s.length() != 0) {
            Tx = SVGUtilities.convertTransform(clipElement, "transform", s, ctx);
        }
        else {
            Tx = new AffineTransform();
        }
        s = clipElement.getAttributeNS(null, "clipPathUnits");
        short coordSystemType;
        if (s.length() == 0) {
            coordSystemType = 1;
        }
        else {
            coordSystemType = SVGUtilities.parseCoordinateSystem(clipElement, "clipPathUnits", s, ctx);
        }
        if (coordSystemType == 2) {
            Tx = SVGUtilities.toObjectBBox(Tx, clipedNode);
        }
        final Area clipPath = new Area();
        final GVTBuilder builder = ctx.getGVTBuilder();
        boolean hasChildren = false;
        for (Node node = clipElement.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == 1) {
                Element child = (Element)node;
                final GraphicsNode clipNode = builder.build(ctx, child);
                if (clipNode != null) {
                    hasChildren = true;
                    if (child instanceof SVGOMUseElement) {
                        final Node shadowChild = ((SVGOMUseElement)child).getCSSFirstChild();
                        if (shadowChild != null && shadowChild.getNodeType() == 1) {
                            child = (Element)shadowChild;
                        }
                    }
                    final int wr = CSSUtilities.convertClipRule(child);
                    final GeneralPath path = new GeneralPath(clipNode.getOutline());
                    path.setWindingRule(wr);
                    AffineTransform at = clipNode.getTransform();
                    if (at == null) {
                        at = Tx;
                    }
                    else {
                        at.preConcatenate(Tx);
                    }
                    Shape outline = at.createTransformedShape(path);
                    final ShapeNode outlineNode = new ShapeNode();
                    outlineNode.setShape(outline);
                    final ClipRable clip = CSSUtilities.convertClipPath(child, outlineNode, ctx);
                    if (clip != null) {
                        final Area area = new Area(outline);
                        area.subtract(new Area(clip.getClipPath()));
                        outline = area;
                    }
                    clipPath.add(new Area(outline));
                }
            }
        }
        if (!hasChildren) {
            return null;
        }
        final ShapeNode clipPathNode = new ShapeNode();
        clipPathNode.setShape(clipPath);
        final ClipRable clipElementClipPath = CSSUtilities.convertClipPath(clipElement, clipPathNode, ctx);
        if (clipElementClipPath != null) {
            clipPath.subtract(new Area(clipElementClipPath.getClipPath()));
        }
        Filter filter = clipedNode.getFilter();
        if (filter == null) {
            filter = clipedNode.getGraphicsNodeRable(true);
        }
        boolean useAA = false;
        final RenderingHints hints = CSSUtilities.convertShapeRendering(clipElement, null);
        if (hints != null) {
            final Object o = hints.get(RenderingHints.KEY_ANTIALIASING);
            useAA = (o == RenderingHints.VALUE_ANTIALIAS_ON);
        }
        return new ClipRable8Bit(filter, clipPath, useAA);
    }
}
