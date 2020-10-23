// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.Graphics2D;
import org.w3c.dom.Element;
import java.awt.geom.Rectangle2D;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.awt.image.RenderedImage;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.TexturePaint;
import org.apache.batik.ext.awt.g2d.GraphicContext;

public class SVGTexturePaint extends AbstractSVGConverter
{
    public SVGTexturePaint(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    @Override
    public SVGDescriptor toSVG(final GraphicContext gc) {
        return this.toSVG((TexturePaint)gc.getPaint());
    }
    
    public SVGPaintDescriptor toSVG(final TexturePaint texture) {
        SVGPaintDescriptor patternDesc = this.descMap.get(texture);
        final Document domFactory = this.generatorContext.domFactory;
        if (patternDesc == null) {
            final Rectangle2D anchorRect = texture.getAnchorRect();
            final Element patternDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "pattern");
            patternDef.setAttributeNS(null, "patternUnits", "userSpaceOnUse");
            patternDef.setAttributeNS(null, "x", this.doubleString(anchorRect.getX()));
            patternDef.setAttributeNS(null, "y", this.doubleString(anchorRect.getY()));
            patternDef.setAttributeNS(null, "width", this.doubleString(anchorRect.getWidth()));
            patternDef.setAttributeNS(null, "height", this.doubleString(anchorRect.getHeight()));
            BufferedImage textureImage = texture.getImage();
            if (textureImage.getWidth() > 0 && textureImage.getHeight() > 0 && (textureImage.getWidth() != anchorRect.getWidth() || textureImage.getHeight() != anchorRect.getHeight()) && anchorRect.getWidth() > 0.0 && anchorRect.getHeight() > 0.0) {
                final double scaleX = anchorRect.getWidth() / textureImage.getWidth();
                final double scaleY = anchorRect.getHeight() / textureImage.getHeight();
                final BufferedImage newImage = new BufferedImage((int)(scaleX * textureImage.getWidth()), (int)(scaleY * textureImage.getHeight()), 2);
                final Graphics2D g = newImage.createGraphics();
                g.scale(scaleX, scaleY);
                g.drawImage(textureImage, 0, 0, null);
                g.dispose();
                textureImage = newImage;
            }
            final Element patternContent = this.generatorContext.genericImageHandler.createElement(this.generatorContext);
            this.generatorContext.genericImageHandler.handleImage((RenderedImage)textureImage, patternContent, 0, 0, textureImage.getWidth(), textureImage.getHeight(), this.generatorContext);
            patternDef.appendChild(patternContent);
            patternDef.setAttributeNS(null, "id", this.generatorContext.idGenerator.generateID("pattern"));
            final String patternAttrBuf = "url(#" + patternDef.getAttributeNS(null, "id") + ")";
            patternDesc = new SVGPaintDescriptor(patternAttrBuf, "1", patternDef);
            this.descMap.put(texture, patternDesc);
            this.defSet.add(patternDef);
        }
        return patternDesc;
    }
}
