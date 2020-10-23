// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.w3c.dom.Node;
import java.awt.geom.Rectangle2D;
import org.apache.batik.gvt.filter.MaskRable8Bit;
import java.awt.geom.AffineTransform;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGMaskElementBridge extends AnimatableGenericSVGBridge implements MaskBridge
{
    @Override
    public String getLocalName() {
        return "mask";
    }
    
    @Override
    public Mask createMask(final BridgeContext ctx, final Element maskElement, final Element maskedElement, final GraphicsNode maskedNode) {
        final Rectangle2D maskRegion = SVGUtilities.convertMaskRegion(maskElement, maskedElement, maskedNode, ctx);
        final GVTBuilder builder = ctx.getGVTBuilder();
        final CompositeGraphicsNode maskNode = new CompositeGraphicsNode();
        final CompositeGraphicsNode maskNodeContent = new CompositeGraphicsNode();
        maskNode.getChildren().add(maskNodeContent);
        boolean hasChildren = false;
        for (Node node = maskElement.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == 1) {
                final Element child = (Element)node;
                final GraphicsNode gn = builder.build(ctx, child);
                if (gn != null) {
                    hasChildren = true;
                    maskNodeContent.getChildren().add(gn);
                }
            }
        }
        if (!hasChildren) {
            return null;
        }
        String s = maskElement.getAttributeNS(null, "transform");
        AffineTransform Tx;
        if (s.length() != 0) {
            Tx = SVGUtilities.convertTransform(maskElement, "transform", s, ctx);
        }
        else {
            Tx = new AffineTransform();
        }
        s = maskElement.getAttributeNS(null, "maskContentUnits");
        short coordSystemType;
        if (s.length() == 0) {
            coordSystemType = 1;
        }
        else {
            coordSystemType = SVGUtilities.parseCoordinateSystem(maskElement, "maskContentUnits", s, ctx);
        }
        if (coordSystemType == 2) {
            Tx = SVGUtilities.toObjectBBox(Tx, maskedNode);
        }
        maskNodeContent.setTransform(Tx);
        Filter filter = maskedNode.getFilter();
        if (filter == null) {
            filter = maskedNode.getGraphicsNodeRable(true);
        }
        return new MaskRable8Bit(filter, maskNode, maskRegion);
    }
}
