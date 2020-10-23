// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.HashMap;
import org.apache.batik.ext.awt.g2d.TransformStackElement;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.apache.batik.ext.awt.g2d.GraphicContext;

public class DOMGroupManager implements SVGSyntax, ErrorConstants
{
    public static final short DRAW = 1;
    public static final short FILL = 16;
    protected GraphicContext gc;
    protected DOMTreeManager domTreeManager;
    protected SVGGraphicContext groupGC;
    protected Element currentGroup;
    
    public DOMGroupManager(final GraphicContext gc, final DOMTreeManager domTreeManager) {
        if (gc == null) {
            throw new SVGGraphics2DRuntimeException("gc should not be null");
        }
        if (domTreeManager == null) {
            throw new SVGGraphics2DRuntimeException("domTreeManager should not be null");
        }
        this.gc = gc;
        this.domTreeManager = domTreeManager;
        this.recycleCurrentGroup();
        this.groupGC = domTreeManager.gcConverter.toSVG(gc);
    }
    
    void recycleCurrentGroup() {
        this.currentGroup = this.domTreeManager.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "g");
    }
    
    public void addElement(final Element element) {
        this.addElement(element, (short)17);
    }
    
    public void addElement(final Element element, final short method) {
        if (!this.currentGroup.hasChildNodes()) {
            this.currentGroup.appendChild(element);
            this.groupGC = this.domTreeManager.gcConverter.toSVG(this.gc);
            final SVGGraphicContext deltaGC = processDeltaGC(this.groupGC, this.domTreeManager.defaultGC);
            this.domTreeManager.getStyleHandler().setStyle(this.currentGroup, deltaGC.getGroupContext(), this.domTreeManager.getGeneratorContext());
            if ((method & 0x1) == 0x0) {
                deltaGC.getGraphicElementContext().put("stroke", "none");
            }
            if ((method & 0x10) == 0x0) {
                deltaGC.getGraphicElementContext().put("fill", "none");
            }
            this.domTreeManager.getStyleHandler().setStyle(element, deltaGC.getGraphicElementContext(), this.domTreeManager.getGeneratorContext());
            this.setTransform(this.currentGroup, deltaGC.getTransformStack());
            this.domTreeManager.appendGroup(this.currentGroup, this);
        }
        else if (this.gc.isTransformStackValid()) {
            final SVGGraphicContext elementGC = this.domTreeManager.gcConverter.toSVG(this.gc);
            final SVGGraphicContext deltaGC2 = processDeltaGC(elementGC, this.groupGC);
            this.trimContextForElement(deltaGC2, element);
            if (this.countOverrides(deltaGC2) <= this.domTreeManager.maxGCOverrides) {
                this.currentGroup.appendChild(element);
                if ((method & 0x1) == 0x0) {
                    deltaGC2.getContext().put("stroke", "none");
                }
                if ((method & 0x10) == 0x0) {
                    deltaGC2.getContext().put("fill", "none");
                }
                this.domTreeManager.getStyleHandler().setStyle(element, deltaGC2.getContext(), this.domTreeManager.getGeneratorContext());
                this.setTransform(element, deltaGC2.getTransformStack());
            }
            else {
                this.currentGroup = this.domTreeManager.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "g");
                this.addElement(element, method);
            }
        }
        else {
            this.currentGroup = this.domTreeManager.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "g");
            this.gc.validateTransformStack();
            this.addElement(element, method);
        }
    }
    
    protected int countOverrides(final SVGGraphicContext deltaGC) {
        return deltaGC.getGroupContext().size();
    }
    
    protected void trimContextForElement(final SVGGraphicContext svgGC, final Element element) {
        final String tag = element.getTagName();
        final Map groupAttrMap = svgGC.getGroupContext();
        if (tag != null) {
            for (final Object o : groupAttrMap.keySet()) {
                final String attrName = (String)o;
                final SVGAttribute attr = SVGAttributeMap.get(attrName);
                if (attr != null && !attr.appliesTo(tag)) {
                    groupAttrMap.remove(attrName);
                }
            }
        }
    }
    
    protected void setTransform(final Element element, final TransformStackElement[] transformStack) {
        final String transform = this.domTreeManager.gcConverter.toSVG(transformStack).trim();
        if (transform.length() > 0) {
            element.setAttributeNS(null, "transform", transform);
        }
    }
    
    static SVGGraphicContext processDeltaGC(final SVGGraphicContext gc, final SVGGraphicContext referenceGc) {
        final Map groupDelta = processDeltaMap(gc.getGroupContext(), referenceGc.getGroupContext());
        final Map graphicElementDelta = gc.getGraphicElementContext();
        final TransformStackElement[] gcTransformStack = gc.getTransformStack();
        final TransformStackElement[] referenceStack = referenceGc.getTransformStack();
        final int deltaStackLength = gcTransformStack.length - referenceStack.length;
        final TransformStackElement[] deltaTransformStack = new TransformStackElement[deltaStackLength];
        System.arraycopy(gcTransformStack, referenceStack.length, deltaTransformStack, 0, deltaStackLength);
        final SVGGraphicContext deltaGC = new SVGGraphicContext(groupDelta, graphicElementDelta, deltaTransformStack);
        return deltaGC;
    }
    
    static Map processDeltaMap(final Map map, final Map referenceMap) {
        final Map mapDelta = new HashMap();
        for (final Object o : map.keySet()) {
            final String key = (String)o;
            final String value = map.get(key);
            final String refValue = referenceMap.get(key);
            if (!value.equals(refValue)) {
                mapDelta.put(key, value);
            }
        }
        return mapDelta;
    }
}
