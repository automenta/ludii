// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.awt.AlphaComposite;
import java.util.HashMap;
import java.util.Map;

public class SVGAlphaComposite extends AbstractSVGConverter
{
    private Map compositeDefsMap;
    private boolean backgroundAccessRequired;
    
    public SVGAlphaComposite(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
        this.compositeDefsMap = new HashMap();
        this.backgroundAccessRequired = false;
        this.compositeDefsMap.put(AlphaComposite.Src, this.compositeToSVG(AlphaComposite.Src));
        this.compositeDefsMap.put(AlphaComposite.SrcIn, this.compositeToSVG(AlphaComposite.SrcIn));
        this.compositeDefsMap.put(AlphaComposite.SrcOut, this.compositeToSVG(AlphaComposite.SrcOut));
        this.compositeDefsMap.put(AlphaComposite.DstIn, this.compositeToSVG(AlphaComposite.DstIn));
        this.compositeDefsMap.put(AlphaComposite.DstOut, this.compositeToSVG(AlphaComposite.DstOut));
        this.compositeDefsMap.put(AlphaComposite.DstOver, this.compositeToSVG(AlphaComposite.DstOver));
        this.compositeDefsMap.put(AlphaComposite.Clear, this.compositeToSVG(AlphaComposite.Clear));
    }
    
    public List getAlphaCompositeFilterSet() {
        return new LinkedList(this.compositeDefsMap.values());
    }
    
    public boolean requiresBackgroundAccess() {
        return this.backgroundAccessRequired;
    }
    
    @Override
    public SVGDescriptor toSVG(final GraphicContext gc) {
        return this.toSVG((AlphaComposite)gc.getComposite());
    }
    
    public SVGCompositeDescriptor toSVG(final AlphaComposite composite) {
        SVGCompositeDescriptor compositeDesc = this.descMap.get(composite);
        if (compositeDesc == null) {
            final String opacityValue = this.doubleString(composite.getAlpha());
            String filterValue = null;
            Element filterDef = null;
            if (composite.getRule() != 3) {
                final AlphaComposite majorComposite = AlphaComposite.getInstance(composite.getRule());
                filterDef = this.compositeDefsMap.get(majorComposite);
                this.defSet.add(filterDef);
                final StringBuffer filterAttrBuf = new StringBuffer("url(");
                filterAttrBuf.append("#");
                filterAttrBuf.append(filterDef.getAttributeNS(null, "id"));
                filterAttrBuf.append(")");
                filterValue = filterAttrBuf.toString();
            }
            else {
                filterValue = "none";
            }
            compositeDesc = new SVGCompositeDescriptor(opacityValue, filterValue, filterDef);
            this.descMap.put(composite, compositeDesc);
        }
        if (composite.getRule() != 3) {
            this.backgroundAccessRequired = true;
        }
        return compositeDesc;
    }
    
    private Element compositeToSVG(final AlphaComposite composite) {
        String operator = null;
        String input1 = null;
        String input2 = null;
        String k2 = "0";
        String id = null;
        switch (composite.getRule()) {
            case 1: {
                operator = "arithmetic";
                input1 = "SourceGraphic";
                input2 = "BackgroundImage";
                id = "alphaCompositeClear";
                break;
            }
            case 2: {
                operator = "arithmetic";
                input1 = "SourceGraphic";
                input2 = "BackgroundImage";
                id = "alphaCompositeSrc";
                k2 = "1";
                break;
            }
            case 5: {
                operator = "in";
                input1 = "SourceGraphic";
                input2 = "BackgroundImage";
                id = "alphaCompositeSrcIn";
                break;
            }
            case 7: {
                operator = "out";
                input1 = "SourceGraphic";
                input2 = "BackgroundImage";
                id = "alphaCompositeSrcOut";
                break;
            }
            case 6: {
                operator = "in";
                input2 = "SourceGraphic";
                input1 = "BackgroundImage";
                id = "alphaCompositeDstIn";
                break;
            }
            case 8: {
                operator = "out";
                input2 = "SourceGraphic";
                input1 = "BackgroundImage";
                id = "alphaCompositeDstOut";
                break;
            }
            case 4: {
                operator = "over";
                input2 = "SourceGraphic";
                input1 = "BackgroundImage";
                id = "alphaCompositeDstOver";
                break;
            }
            default: {
                throw new RuntimeException("invalid rule:" + composite.getRule());
            }
        }
        final Element compositeFilter = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "filter");
        compositeFilter.setAttributeNS(null, "id", id);
        compositeFilter.setAttributeNS(null, "filterUnits", "objectBoundingBox");
        compositeFilter.setAttributeNS(null, "x", "0%");
        compositeFilter.setAttributeNS(null, "y", "0%");
        compositeFilter.setAttributeNS(null, "width", "100%");
        compositeFilter.setAttributeNS(null, "height", "100%");
        final Element feComposite = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "feComposite");
        feComposite.setAttributeNS(null, "operator", operator);
        feComposite.setAttributeNS(null, "in", input1);
        feComposite.setAttributeNS(null, "in2", input2);
        feComposite.setAttributeNS(null, "k2", k2);
        feComposite.setAttributeNS(null, "result", "composite");
        final Element feFlood = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "feFlood");
        feFlood.setAttributeNS(null, "flood-color", "white");
        feFlood.setAttributeNS(null, "flood-opacity", "1");
        feFlood.setAttributeNS(null, "result", "flood");
        final Element feMerge = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "feMerge");
        final Element feMergeNodeFlood = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "feMergeNode");
        feMergeNodeFlood.setAttributeNS(null, "in", "flood");
        final Element feMergeNodeComposite = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "feMergeNode");
        feMergeNodeComposite.setAttributeNS(null, "in", "composite");
        feMerge.appendChild(feMergeNodeFlood);
        feMerge.appendChild(feMergeNodeComposite);
        compositeFilter.appendChild(feFlood);
        compositeFilter.appendChild(feComposite);
        compositeFilter.appendChild(feMerge);
        return compositeFilter;
    }
}
