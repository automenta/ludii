// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.HashMap;
import org.apache.batik.ext.awt.g2d.TransformStackElement;
import java.util.Map;
import org.apache.batik.util.SVGConstants;

public class SVGGraphicContext implements SVGConstants, ErrorConstants
{
    private static final String[] leafOnlyAttributes;
    private static final String[] defaultValues;
    private Map context;
    private Map groupContext;
    private Map graphicElementContext;
    private TransformStackElement[] transformStack;
    
    public SVGGraphicContext(final Map context, final TransformStackElement[] transformStack) {
        if (context == null) {
            throw new SVGGraphics2DRuntimeException("context map(s) should not be null");
        }
        if (transformStack == null) {
            throw new SVGGraphics2DRuntimeException("transformer stack should not be null");
        }
        this.context = context;
        this.transformStack = transformStack;
        this.computeGroupAndGraphicElementContext();
    }
    
    public SVGGraphicContext(final Map groupContext, final Map graphicElementContext, final TransformStackElement[] transformStack) {
        if (groupContext == null || graphicElementContext == null) {
            throw new SVGGraphics2DRuntimeException("context map(s) should not be null");
        }
        if (transformStack == null) {
            throw new SVGGraphics2DRuntimeException("transformer stack should not be null");
        }
        this.groupContext = groupContext;
        this.graphicElementContext = graphicElementContext;
        this.transformStack = transformStack;
        this.computeContext();
    }
    
    public Map getContext() {
        return this.context;
    }
    
    public Map getGroupContext() {
        return this.groupContext;
    }
    
    public Map getGraphicElementContext() {
        return this.graphicElementContext;
    }
    
    public TransformStackElement[] getTransformStack() {
        return this.transformStack;
    }
    
    private void computeContext() {
        if (this.context != null) {
            return;
        }
        (this.context = new HashMap(this.groupContext)).putAll(this.graphicElementContext);
    }
    
    private void computeGroupAndGraphicElementContext() {
        if (this.groupContext != null) {
            return;
        }
        this.groupContext = new HashMap(this.context);
        this.graphicElementContext = new HashMap();
        for (int i = 0; i < SVGGraphicContext.leafOnlyAttributes.length; ++i) {
            final Object attrValue = this.groupContext.get(SVGGraphicContext.leafOnlyAttributes[i]);
            if (attrValue != null) {
                if (!attrValue.equals(SVGGraphicContext.defaultValues[i])) {
                    this.graphicElementContext.put(SVGGraphicContext.leafOnlyAttributes[i], attrValue);
                }
                this.groupContext.remove(SVGGraphicContext.leafOnlyAttributes[i]);
            }
        }
    }
    
    static {
        leafOnlyAttributes = new String[] { "opacity", "filter", "clip-path" };
        defaultValues = new String[] { "1", "none", "none" };
    }
}
