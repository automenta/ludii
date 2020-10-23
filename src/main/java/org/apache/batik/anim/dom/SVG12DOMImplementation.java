// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import java.util.Map;
import org.apache.batik.dom.events.EventSupport;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.svg12.XBLOMShadowTreeEvent;
import org.apache.batik.dom.svg12.SVGOMWheelEvent;
import org.w3c.dom.events.Event;
import org.apache.batik.dom.events.DocumentEventSupport;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.GenericElement;
import org.w3c.dom.Element;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.DocumentType;
import java.net.URL;
import org.w3c.css.sac.InputSource;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Document;
import org.apache.batik.css.engine.SVG12CSSEngine;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.css.parser.ExtendedParser;
import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.dom.AbstractStylableDocument;
import org.w3c.dom.DOMImplementation;
import org.apache.batik.dom.ExtensibleDOMImplementation;
import java.util.HashMap;

public class SVG12DOMImplementation extends SVGDOMImplementation
{
    protected static HashMap<String, ElementFactory> svg12Factories;
    protected static HashMap<String, ElementFactory> xblFactories;
    protected static final DOMImplementation DOM_IMPLEMENTATION;
    
    public SVG12DOMImplementation() {
        this.factories = SVG12DOMImplementation.svg12Factories;
        this.registerFeature("CSS", "2.0");
        this.registerFeature("StyleSheets", "2.0");
        this.registerFeature("SVG", new String[] { "1.0", "1.1", "1.2" });
        this.registerFeature("SVGEvents", new String[] { "1.0", "1.1", "1.2" });
    }
    
    @Override
    public CSSEngine createCSSEngine(final AbstractStylableDocument doc, final CSSContext ctx, final ExtendedParser ep, final ValueManager[] vms, final ShorthandManager[] sms) {
        final ParsedURL durl = ((SVGOMDocument)doc).getParsedURL();
        final CSSEngine result = new SVG12CSSEngine(doc, durl, ep, vms, sms, ctx);
        final URL url = this.getClass().getResource("resources/UserAgentStyleSheet.css");
        if (url != null) {
            final ParsedURL purl = new ParsedURL(url);
            final InputSource is = new InputSource(purl.toString());
            result.setUserAgentStyleSheet(result.parseStyleSheet(is, purl, "all"));
        }
        return result;
    }
    
    @Override
    public Document createDocument(final String namespaceURI, final String qualifiedName, final DocumentType doctype) throws DOMException {
        final SVGOMDocument result = new SVG12OMDocument(doctype, this);
        result.setIsSVG12(true);
        if (qualifiedName != null) {
            result.appendChild(result.createElementNS(namespaceURI, qualifiedName));
        }
        return result;
    }
    
    @Override
    public Element createElementNS(final AbstractDocument document, final String namespaceURI, final String qualifiedName) {
        if (namespaceURI == null) {
            return new GenericElement(qualifiedName.intern(), document);
        }
        final String name = DOMUtilities.getLocalName(qualifiedName);
        final String prefix = DOMUtilities.getPrefix(qualifiedName);
        if ("http://www.w3.org/2000/svg".equals(namespaceURI)) {
            final ElementFactory ef = this.factories.get(name);
            if (ef != null) {
                return ef.create(prefix, document);
            }
        }
        else if ("http://www.w3.org/2004/xbl".equals(namespaceURI)) {
            final ElementFactory ef = SVG12DOMImplementation.xblFactories.get(name);
            if (ef != null) {
                return ef.create(prefix, document);
            }
        }
        if (this.customFactories != null) {
            final ElementFactory cef = (ElementFactory)this.customFactories.get(namespaceURI, name);
            if (cef != null) {
                return cef.create(prefix, document);
            }
        }
        return new BindableElement(prefix, document, namespaceURI, name);
    }
    
    @Override
    public DocumentEventSupport createDocumentEventSupport() {
        final DocumentEventSupport result = super.createDocumentEventSupport();
        result.registerEventFactory("WheelEvent", new DocumentEventSupport.EventFactory() {
            @Override
            public Event createEvent() {
                return new SVGOMWheelEvent();
            }
        });
        result.registerEventFactory("ShadowTreeEvent", new DocumentEventSupport.EventFactory() {
            @Override
            public Event createEvent() {
                return new XBLOMShadowTreeEvent();
            }
        });
        return result;
    }
    
    @Override
    public EventSupport createEventSupport(final AbstractNode n) {
        return new XBLEventSupport(n);
    }
    
    public static DOMImplementation getDOMImplementation() {
        return SVG12DOMImplementation.DOM_IMPLEMENTATION;
    }
    
    static {
        (SVG12DOMImplementation.svg12Factories = new HashMap<String, ElementFactory>(SVG12DOMImplementation.svg11Factories)).put("flowDiv", new FlowDivElementFactory());
        SVG12DOMImplementation.svg12Factories.put("flowLine", new FlowLineElementFactory());
        SVG12DOMImplementation.svg12Factories.put("flowPara", new FlowParaElementFactory());
        SVG12DOMImplementation.svg12Factories.put("flowRegionBreak", new FlowRegionBreakElementFactory());
        SVG12DOMImplementation.svg12Factories.put("flowRegion", new FlowRegionElementFactory());
        SVG12DOMImplementation.svg12Factories.put("flowRegionExclude", new FlowRegionExcludeElementFactory());
        SVG12DOMImplementation.svg12Factories.put("flowRoot", new FlowRootElementFactory());
        SVG12DOMImplementation.svg12Factories.put("flowSpan", new FlowSpanElementFactory());
        SVG12DOMImplementation.svg12Factories.put("handler", new HandlerElementFactory());
        SVG12DOMImplementation.svg12Factories.put("multiImage", new MultiImageElementFactory());
        SVG12DOMImplementation.svg12Factories.put("solidColor", new SolidColorElementFactory());
        SVG12DOMImplementation.svg12Factories.put("subImage", new SubImageElementFactory());
        SVG12DOMImplementation.svg12Factories.put("subImageRef", new SubImageRefElementFactory());
        (SVG12DOMImplementation.xblFactories = new HashMap<String, ElementFactory>()).put("xbl", new XBLXBLElementFactory());
        SVG12DOMImplementation.xblFactories.put("definition", new XBLDefinitionElementFactory());
        SVG12DOMImplementation.xblFactories.put("template", new XBLTemplateElementFactory());
        SVG12DOMImplementation.xblFactories.put("content", new XBLContentElementFactory());
        SVG12DOMImplementation.xblFactories.put("handlerGroup", new XBLHandlerGroupElementFactory());
        SVG12DOMImplementation.xblFactories.put("import", new XBLImportElementFactory());
        SVG12DOMImplementation.xblFactories.put("shadowTree", new XBLShadowTreeElementFactory());
        DOM_IMPLEMENTATION = new SVG12DOMImplementation();
    }
    
    protected static class FlowDivElementFactory implements ElementFactory
    {
        public FlowDivElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFlowDivElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FlowLineElementFactory implements ElementFactory
    {
        public FlowLineElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFlowLineElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FlowParaElementFactory implements ElementFactory
    {
        public FlowParaElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFlowParaElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FlowRegionBreakElementFactory implements ElementFactory
    {
        public FlowRegionBreakElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFlowRegionBreakElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FlowRegionElementFactory implements ElementFactory
    {
        public FlowRegionElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFlowRegionElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FlowRegionExcludeElementFactory implements ElementFactory
    {
        public FlowRegionExcludeElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFlowRegionExcludeElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FlowRootElementFactory implements ElementFactory
    {
        public FlowRootElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFlowRootElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FlowSpanElementFactory implements ElementFactory
    {
        public FlowSpanElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFlowSpanElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class HandlerElementFactory implements ElementFactory
    {
        public HandlerElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMHandlerElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class MultiImageElementFactory implements ElementFactory
    {
        public MultiImageElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMMultiImageElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class SolidColorElementFactory implements ElementFactory
    {
        public SolidColorElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMSolidColorElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class SubImageElementFactory implements ElementFactory
    {
        public SubImageElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMSubImageElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class SubImageRefElementFactory implements ElementFactory
    {
        public SubImageRefElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMSubImageRefElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class XBLXBLElementFactory implements ElementFactory
    {
        public XBLXBLElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new XBLOMXBLElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class XBLDefinitionElementFactory implements ElementFactory
    {
        public XBLDefinitionElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new XBLOMDefinitionElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class XBLTemplateElementFactory implements ElementFactory
    {
        public XBLTemplateElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new XBLOMTemplateElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class XBLContentElementFactory implements ElementFactory
    {
        public XBLContentElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new XBLOMContentElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class XBLHandlerGroupElementFactory implements ElementFactory
    {
        public XBLHandlerGroupElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new XBLOMHandlerGroupElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class XBLImportElementFactory implements ElementFactory
    {
        public XBLImportElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new XBLOMImportElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class XBLShadowTreeElementFactory implements ElementFactory
    {
        public XBLShadowTreeElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new XBLOMShadowTreeElement(prefix, (AbstractDocument)doc);
        }
    }
}
