// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.util.Collection;
import java.util.Map;
import org.w3c.dom.Comment;
import java.util.Iterator;
import org.w3c.dom.Node;
import java.util.LinkedList;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.w3c.dom.Element;
import java.util.List;

public class DOMTreeManager implements SVGSyntax, ErrorConstants
{
    int maxGCOverrides;
    protected final List groupManagers;
    protected List genericDefSet;
    SVGGraphicContext defaultGC;
    protected Element topLevelGroup;
    SVGGraphicContextConverter gcConverter;
    protected SVGGeneratorContext generatorContext;
    protected SVGBufferedImageOp filterConverter;
    protected List otherDefs;
    
    public DOMTreeManager(final GraphicContext gc, final SVGGeneratorContext generatorContext, final int maxGCOverrides) {
        this.groupManagers = Collections.synchronizedList(new ArrayList<Object>());
        this.genericDefSet = new LinkedList();
        if (gc == null) {
            throw new SVGGraphics2DRuntimeException("gc should not be null");
        }
        if (maxGCOverrides <= 0) {
            throw new SVGGraphics2DRuntimeException("maxGcOverrides should be greater than zero");
        }
        if (generatorContext == null) {
            throw new SVGGraphics2DRuntimeException("generatorContext should not be null");
        }
        this.generatorContext = generatorContext;
        this.maxGCOverrides = maxGCOverrides;
        this.recycleTopLevelGroup();
        this.defaultGC = this.gcConverter.toSVG(gc);
    }
    
    public void addGroupManager(final DOMGroupManager groupManager) {
        if (groupManager != null) {
            this.groupManagers.add(groupManager);
        }
    }
    
    public void removeGroupManager(final DOMGroupManager groupManager) {
        if (groupManager != null) {
            this.groupManagers.remove(groupManager);
        }
    }
    
    public void appendGroup(final Element group, final DOMGroupManager groupManager) {
        this.topLevelGroup.appendChild(group);
        synchronized (this.groupManagers) {
            final int nManagers = this.groupManagers.size();
            for (final Object groupManager2 : this.groupManagers) {
                final DOMGroupManager gm = (DOMGroupManager)groupManager2;
                if (gm != groupManager) {
                    gm.recycleCurrentGroup();
                }
            }
        }
    }
    
    protected void recycleTopLevelGroup() {
        this.recycleTopLevelGroup(true);
    }
    
    protected void recycleTopLevelGroup(final boolean recycleConverters) {
        synchronized (this.groupManagers) {
            final int nManagers = this.groupManagers.size();
            for (final Object groupManager : this.groupManagers) {
                final DOMGroupManager gm = (DOMGroupManager)groupManager;
                gm.recycleCurrentGroup();
            }
        }
        this.topLevelGroup = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "g");
        if (recycleConverters) {
            this.filterConverter = new SVGBufferedImageOp(this.generatorContext);
            this.gcConverter = new SVGGraphicContextConverter(this.generatorContext);
        }
    }
    
    public void setTopLevelGroup(final Element topLevelGroup) {
        if (topLevelGroup == null) {
            throw new SVGGraphics2DRuntimeException("topLevelGroup should not be null");
        }
        if (!"g".equalsIgnoreCase(topLevelGroup.getTagName())) {
            throw new SVGGraphics2DRuntimeException("topLevelGroup should be a group <g>");
        }
        this.recycleTopLevelGroup(false);
        this.topLevelGroup = topLevelGroup;
    }
    
    public Element getRoot() {
        return this.getRoot(null);
    }
    
    public Element getRoot(final Element svgElement) {
        Element svg = svgElement;
        if (svg == null) {
            svg = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "svg");
        }
        if (this.gcConverter.getCompositeConverter().getAlphaCompositeConverter().requiresBackgroundAccess()) {
            svg.setAttributeNS(null, "enable-background", "new");
        }
        if (this.generatorContext.generatorComment != null) {
            final Comment generatorComment = this.generatorContext.domFactory.createComment(this.generatorContext.generatorComment);
            svg.appendChild(generatorComment);
        }
        this.applyDefaultRenderingStyle(svg);
        svg.appendChild(this.getGenericDefinitions());
        svg.appendChild(this.getTopLevelGroup());
        return svg;
    }
    
    public void applyDefaultRenderingStyle(final Element element) {
        final Map groupDefaults = this.defaultGC.getGroupContext();
        this.generatorContext.styleHandler.setStyle(element, groupDefaults, this.generatorContext);
    }
    
    public Element getGenericDefinitions() {
        final Element genericDefs = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "defs");
        for (final Object aGenericDefSet : this.genericDefSet) {
            genericDefs.appendChild((Node)aGenericDefSet);
        }
        genericDefs.setAttributeNS(null, "id", "genericDefs");
        return genericDefs;
    }
    
    public ExtensionHandler getExtensionHandler() {
        return this.generatorContext.getExtensionHandler();
    }
    
    void setExtensionHandler(final ExtensionHandler extensionHandler) {
        this.generatorContext.setExtensionHandler(extensionHandler);
    }
    
    public List getDefinitionSet() {
        final List defSet = this.gcConverter.getDefinitionSet();
        defSet.removeAll(this.genericDefSet);
        defSet.addAll(this.filterConverter.getDefinitionSet());
        if (this.otherDefs != null) {
            defSet.addAll(this.otherDefs);
            this.otherDefs = null;
        }
        this.filterConverter = new SVGBufferedImageOp(this.generatorContext);
        this.gcConverter = new SVGGraphicContextConverter(this.generatorContext);
        return defSet;
    }
    
    public void addOtherDef(final Element definition) {
        if (this.otherDefs == null) {
            this.otherDefs = new LinkedList();
        }
        this.otherDefs.add(definition);
    }
    
    public Element getTopLevelGroup() {
        final boolean includeDefinitionSet = true;
        return this.getTopLevelGroup(includeDefinitionSet);
    }
    
    public Element getTopLevelGroup(final boolean includeDefinitionSet) {
        final Element topLevelGroup = this.topLevelGroup;
        if (includeDefinitionSet) {
            final List defSet = this.getDefinitionSet();
            if (defSet.size() > 0) {
                Element defElement = null;
                final NodeList defsElements = topLevelGroup.getElementsByTagName("defs");
                if (defsElements.getLength() > 0) {
                    defElement = (Element)defsElements.item(0);
                }
                if (defElement == null) {
                    defElement = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "defs");
                    defElement.setAttributeNS(null, "id", this.generatorContext.idGenerator.generateID("defs"));
                    topLevelGroup.insertBefore(defElement, topLevelGroup.getFirstChild());
                }
                for (final Object aDefSet : defSet) {
                    defElement.appendChild((Node)aDefSet);
                }
            }
        }
        this.recycleTopLevelGroup(false);
        return topLevelGroup;
    }
    
    public SVGBufferedImageOp getFilterConverter() {
        return this.filterConverter;
    }
    
    public SVGGraphicContextConverter getGraphicContextConverter() {
        return this.gcConverter;
    }
    
    SVGGeneratorContext getGeneratorContext() {
        return this.generatorContext;
    }
    
    Document getDOMFactory() {
        return this.generatorContext.domFactory;
    }
    
    StyleHandler getStyleHandler() {
        return this.generatorContext.styleHandler;
    }
}
