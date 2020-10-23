// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.geom.Line2D;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.awt.geom.GeneralPath;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import java.awt.Shape;

public class SVGClip extends AbstractSVGConverter
{
    public static final Shape ORIGIN;
    public static final SVGClipDescriptor NO_CLIP;
    private SVGShape shapeConverter;
    
    public SVGClip(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
        this.shapeConverter = new SVGShape(generatorContext);
    }
    
    @Override
    public SVGDescriptor toSVG(final GraphicContext gc) {
        final Shape clip = gc.getClip();
        SVGClipDescriptor clipDesc = null;
        if (clip != null) {
            final StringBuffer clipPathAttrBuf = new StringBuffer("url(");
            final GeneralPath clipPath = new GeneralPath(clip);
            final ClipKey clipKey = new ClipKey(clipPath, this.generatorContext);
            clipDesc = this.descMap.get(clipKey);
            if (clipDesc == null) {
                final Element clipDef = this.clipToSVG(clip);
                if (clipDef == null) {
                    clipDesc = SVGClip.NO_CLIP;
                }
                else {
                    clipPathAttrBuf.append("#");
                    clipPathAttrBuf.append(clipDef.getAttributeNS(null, "id"));
                    clipPathAttrBuf.append(")");
                    clipDesc = new SVGClipDescriptor(clipPathAttrBuf.toString(), clipDef);
                    this.descMap.put(clipKey, clipDesc);
                    this.defSet.add(clipDef);
                }
            }
        }
        else {
            clipDesc = SVGClip.NO_CLIP;
        }
        return clipDesc;
    }
    
    private Element clipToSVG(final Shape clip) {
        final Element clipDef = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "clipPath");
        clipDef.setAttributeNS(null, "clipPathUnits", "userSpaceOnUse");
        clipDef.setAttributeNS(null, "id", this.generatorContext.idGenerator.generateID("clipPath"));
        final Element clipPath = this.shapeConverter.toSVG(clip);
        if (clipPath != null) {
            clipDef.appendChild(clipPath);
            return clipDef;
        }
        clipDef.appendChild(this.shapeConverter.toSVG(SVGClip.ORIGIN));
        return clipDef;
    }
    
    static {
        ORIGIN = new Line2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        NO_CLIP = new SVGClipDescriptor("none", null);
    }
}
