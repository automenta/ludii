// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.util.HaltingThread;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Document;
import org.apache.batik.util.SVGConstants;

public class GVTBuilder implements SVGConstants
{
    public GraphicsNode build(final BridgeContext ctx, final Document document) {
        ctx.setDocument(document);
        ctx.initializeDocument(document);
        ctx.setGVTBuilder(this);
        final DocumentBridge dBridge = ctx.getDocumentBridge();
        RootGraphicsNode rootNode = null;
        try {
            rootNode = dBridge.createGraphicsNode(ctx, document);
            final Element svgElement = document.getDocumentElement();
            GraphicsNode topNode = null;
            final Bridge bridge = ctx.getBridge(svgElement);
            if (bridge == null || !(bridge instanceof GraphicsNodeBridge)) {
                return null;
            }
            final GraphicsNodeBridge gnBridge = (GraphicsNodeBridge)bridge;
            topNode = gnBridge.createGraphicsNode(ctx, svgElement);
            if (topNode == null) {
                return null;
            }
            rootNode.getChildren().add(topNode);
            this.buildComposite(ctx, svgElement, (CompositeGraphicsNode)topNode);
            gnBridge.buildGraphicsNode(ctx, svgElement, topNode);
            dBridge.buildGraphicsNode(ctx, document, rootNode);
        }
        catch (BridgeException ex) {
            ex.setGraphicsNode(rootNode);
            throw ex;
        }
        if (ctx.isInteractive()) {
            ctx.addUIEventListeners(document);
            ctx.addGVTListener(document);
        }
        if (ctx.isDynamic()) {
            ctx.addDOMListeners();
        }
        return rootNode;
    }
    
    public GraphicsNode build(final BridgeContext ctx, final Element e) {
        final Bridge bridge = ctx.getBridge(e);
        if (bridge instanceof GenericBridge) {
            ((GenericBridge)bridge).handleElement(ctx, e);
            this.handleGenericBridges(ctx, e);
            return null;
        }
        if (bridge == null || !(bridge instanceof GraphicsNodeBridge)) {
            this.handleGenericBridges(ctx, e);
            return null;
        }
        final GraphicsNodeBridge gnBridge = (GraphicsNodeBridge)bridge;
        if (!gnBridge.getDisplay(e)) {
            this.handleGenericBridges(ctx, e);
            return null;
        }
        final GraphicsNode gn = gnBridge.createGraphicsNode(ctx, e);
        if (gn != null) {
            if (gnBridge.isComposite()) {
                this.buildComposite(ctx, e, (CompositeGraphicsNode)gn);
            }
            else {
                this.handleGenericBridges(ctx, e);
            }
            gnBridge.buildGraphicsNode(ctx, e, gn);
        }
        if (ctx.isDynamic()) {}
        return gn;
    }
    
    protected void buildComposite(final BridgeContext ctx, final Element e, final CompositeGraphicsNode parentNode) {
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                this.buildGraphicsNode(ctx, (Element)n, parentNode);
            }
        }
    }
    
    protected void buildGraphicsNode(final BridgeContext ctx, final Element e, final CompositeGraphicsNode parentNode) {
        if (HaltingThread.hasBeenHalted()) {
            throw new InterruptedBridgeException();
        }
        final Bridge bridge = ctx.getBridge(e);
        if (bridge instanceof GenericBridge) {
            ((GenericBridge)bridge).handleElement(ctx, e);
            this.handleGenericBridges(ctx, e);
            return;
        }
        if (bridge == null || !(bridge instanceof GraphicsNodeBridge)) {
            this.handleGenericBridges(ctx, e);
            return;
        }
        if (!CSSUtilities.convertDisplay(e)) {
            this.handleGenericBridges(ctx, e);
            return;
        }
        final GraphicsNodeBridge gnBridge = (GraphicsNodeBridge)bridge;
        try {
            final GraphicsNode gn = gnBridge.createGraphicsNode(ctx, e);
            if (gn != null) {
                parentNode.getChildren().add(gn);
                if (gnBridge.isComposite()) {
                    this.buildComposite(ctx, e, (CompositeGraphicsNode)gn);
                }
                else {
                    this.handleGenericBridges(ctx, e);
                }
                gnBridge.buildGraphicsNode(ctx, e, gn);
            }
            else {
                this.handleGenericBridges(ctx, e);
            }
        }
        catch (BridgeException ex) {
            final GraphicsNode errNode = ex.getGraphicsNode();
            if (errNode != null) {
                parentNode.getChildren().add(errNode);
                gnBridge.buildGraphicsNode(ctx, e, errNode);
                ex.setGraphicsNode(null);
            }
            throw ex;
        }
    }
    
    protected void handleGenericBridges(final BridgeContext ctx, final Element e) {
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Element) {
                final Element e2 = (Element)n;
                final Bridge b = ctx.getBridge(e2);
                if (b instanceof GenericBridge) {
                    ((GenericBridge)b).handleElement(ctx, e2);
                }
                this.handleGenericBridges(ctx, e2);
            }
        }
    }
}
