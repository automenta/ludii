// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.w3c.dom.Element;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import java.util.Collections;
import java.util.Iterator;
import org.apache.batik.bridge.SVGBridgeExtension;

public class SVG12BridgeExtension extends SVGBridgeExtension
{
    @Override
    public float getPriority() {
        return 0.0f;
    }
    
    @Override
    public Iterator getImplementedExtensions() {
        return Collections.EMPTY_LIST.iterator();
    }
    
    @Override
    public String getAuthor() {
        return "The Apache Batik Team.";
    }
    
    @Override
    public String getContactAddress() {
        return "batik-dev@xmlgraphics.apache.org";
    }
    
    @Override
    public String getURL() {
        return "http://xml.apache.org/batik";
    }
    
    @Override
    public String getDescription() {
        return "The required SVG 1.2 tags";
    }
    
    @Override
    public void registerTags(final BridgeContext ctx) {
        super.registerTags(ctx);
        ctx.putBridge(new SVGFlowRootElementBridge());
        ctx.putBridge(new SVGMultiImageElementBridge());
        ctx.putBridge(new SVGSolidColorElementBridge());
        ctx.putBridge(new SVG12TextElementBridge());
        ctx.putBridge(new XBLShadowTreeElementBridge());
        ctx.putBridge(new XBLContentElementBridge());
        ctx.setDefaultBridge(new BindableElementBridge());
        ctx.putReservedNamespaceURI(null);
        ctx.putReservedNamespaceURI("http://www.w3.org/2000/svg");
        ctx.putReservedNamespaceURI("http://www.w3.org/2004/xbl");
    }
    
    @Override
    public boolean isDynamicElement(final Element e) {
        final String ns = e.getNamespaceURI();
        if ("http://www.w3.org/2004/xbl".equals(ns)) {
            return true;
        }
        if (!"http://www.w3.org/2000/svg".equals(ns)) {
            return false;
        }
        final String ln = e.getLocalName();
        return ln.equals("script") || ln.equals("handler") || ln.startsWith("animate") || ln.equals("set");
    }
}
