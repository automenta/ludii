// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.dom.events.DOMTimeEvent;
import org.apache.batik.dom.svg.SVGOMEvent;
import org.w3c.dom.events.Event;
import org.apache.batik.dom.events.DocumentEventSupport;
import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.Element;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.stylesheets.StyleSheet;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.DocumentType;
import org.apache.batik.css.dom.CSSOMSVGViewCSS;
import org.w3c.dom.css.ViewCSS;
import java.net.URL;
import org.w3c.css.sac.InputSource;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Document;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.css.parser.ExtendedParser;
import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.dom.AbstractStylableDocument;
import org.apache.batik.i18n.LocalizableSupport;
import org.w3c.dom.DOMImplementation;
import java.util.HashMap;
import org.apache.batik.dom.util.CSSStyleDeclarationFactory;
import org.apache.batik.dom.ExtensibleDOMImplementation;

public class SVGDOMImplementation extends ExtensibleDOMImplementation implements CSSStyleDeclarationFactory
{
    public static final String SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg";
    protected static final String RESOURCES = "org.apache.batik.dom.svg.resources.Messages";
    protected HashMap<String, ElementFactory> factories;
    protected static HashMap<String, ElementFactory> svg11Factories;
    protected static final DOMImplementation DOM_IMPLEMENTATION;
    
    public static DOMImplementation getDOMImplementation() {
        return SVGDOMImplementation.DOM_IMPLEMENTATION;
    }
    
    public SVGDOMImplementation() {
        this.factories = SVGDOMImplementation.svg11Factories;
        this.registerFeature("CSS", "2.0");
        this.registerFeature("StyleSheets", "2.0");
        this.registerFeature("SVG", new String[] { "1.0", "1.1" });
        this.registerFeature("SVGEvents", new String[] { "1.0", "1.1" });
    }
    
    @Override
    protected void initLocalizable() {
        this.localizableSupport = new LocalizableSupport("org.apache.batik.dom.svg.resources.Messages", this.getClass().getClassLoader());
    }
    
    @Override
    public CSSEngine createCSSEngine(final AbstractStylableDocument doc, final CSSContext ctx, final ExtendedParser ep, final ValueManager[] vms, final ShorthandManager[] sms) {
        final ParsedURL durl = ((SVGOMDocument)doc).getParsedURL();
        final CSSEngine result = new SVGCSSEngine(doc, durl, ep, vms, sms, ctx);
        final URL url = this.getClass().getResource("resources/UserAgentStyleSheet.css");
        if (url != null) {
            final ParsedURL purl = new ParsedURL(url);
            final InputSource is = new InputSource(purl.toString());
            result.setUserAgentStyleSheet(result.parseStyleSheet(is, purl, "all"));
        }
        return result;
    }
    
    @Override
    public ViewCSS createViewCSS(final AbstractStylableDocument doc) {
        return new CSSOMSVGViewCSS(doc.getCSSEngine());
    }
    
    @Override
    public Document createDocument(final String namespaceURI, final String qualifiedName, final DocumentType doctype) throws DOMException {
        final Document result = new SVGOMDocument(doctype, this);
        if (qualifiedName != null) {
            result.appendChild(result.createElementNS(namespaceURI, qualifiedName));
        }
        return result;
    }
    
    @Override
    public CSSStyleSheet createCSSStyleSheet(final String title, final String media) {
        throw new UnsupportedOperationException("DOMImplementationCSS.createCSSStyleSheet is not implemented");
    }
    
    @Override
    public CSSStyleDeclaration createCSSStyleDeclaration() {
        throw new UnsupportedOperationException("CSSStyleDeclarationFactory.createCSSStyleDeclaration is not implemented");
    }
    
    @Override
    public StyleSheet createStyleSheet(final Node n, final HashMap<String, String> attrs) {
        throw new UnsupportedOperationException("StyleSheetFactory.createStyleSheet is not implemented");
    }
    
    public CSSStyleSheet getUserAgentStyleSheet() {
        throw new UnsupportedOperationException("StyleSheetFactory.getUserAgentStyleSheet is not implemented");
    }
    
    @Override
    public Element createElementNS(final AbstractDocument document, final String namespaceURI, final String qualifiedName) {
        if (!"http://www.w3.org/2000/svg".equals(namespaceURI)) {
            return super.createElementNS(document, namespaceURI, qualifiedName);
        }
        final String name = DOMUtilities.getLocalName(qualifiedName);
        final ElementFactory ef = this.factories.get(name);
        if (ef != null) {
            return ef.create(DOMUtilities.getPrefix(qualifiedName), document);
        }
        throw document.createDOMException((short)8, "invalid.element", new Object[] { namespaceURI, qualifiedName });
    }
    
    @Override
    public DocumentEventSupport createDocumentEventSupport() {
        final DocumentEventSupport result = new DocumentEventSupport();
        result.registerEventFactory("SVGEvents", new DocumentEventSupport.EventFactory() {
            @Override
            public Event createEvent() {
                return new SVGOMEvent();
            }
        });
        result.registerEventFactory("TimeEvent", new DocumentEventSupport.EventFactory() {
            @Override
            public Event createEvent() {
                return new DOMTimeEvent();
            }
        });
        return result;
    }
    
    static {
        (SVGDOMImplementation.svg11Factories = new HashMap<String, ElementFactory>()).put("a", new AElementFactory());
        SVGDOMImplementation.svg11Factories.put("altGlyph", new AltGlyphElementFactory());
        SVGDOMImplementation.svg11Factories.put("altGlyphDef", new AltGlyphDefElementFactory());
        SVGDOMImplementation.svg11Factories.put("altGlyphItem", new AltGlyphItemElementFactory());
        SVGDOMImplementation.svg11Factories.put("animate", new AnimateElementFactory());
        SVGDOMImplementation.svg11Factories.put("animateColor", new AnimateColorElementFactory());
        SVGDOMImplementation.svg11Factories.put("animateMotion", new AnimateMotionElementFactory());
        SVGDOMImplementation.svg11Factories.put("animateTransform", new AnimateTransformElementFactory());
        SVGDOMImplementation.svg11Factories.put("circle", new CircleElementFactory());
        SVGDOMImplementation.svg11Factories.put("clipPath", new ClipPathElementFactory());
        SVGDOMImplementation.svg11Factories.put("color-profile", new ColorProfileElementFactory());
        SVGDOMImplementation.svg11Factories.put("cursor", new CursorElementFactory());
        SVGDOMImplementation.svg11Factories.put("definition-src", new DefinitionSrcElementFactory());
        SVGDOMImplementation.svg11Factories.put("defs", new DefsElementFactory());
        SVGDOMImplementation.svg11Factories.put("desc", new DescElementFactory());
        SVGDOMImplementation.svg11Factories.put("ellipse", new EllipseElementFactory());
        SVGDOMImplementation.svg11Factories.put("feBlend", new FeBlendElementFactory());
        SVGDOMImplementation.svg11Factories.put("feColorMatrix", new FeColorMatrixElementFactory());
        SVGDOMImplementation.svg11Factories.put("feComponentTransfer", new FeComponentTransferElementFactory());
        SVGDOMImplementation.svg11Factories.put("feComposite", new FeCompositeElementFactory());
        SVGDOMImplementation.svg11Factories.put("feConvolveMatrix", new FeConvolveMatrixElementFactory());
        SVGDOMImplementation.svg11Factories.put("feDiffuseLighting", new FeDiffuseLightingElementFactory());
        SVGDOMImplementation.svg11Factories.put("feDisplacementMap", new FeDisplacementMapElementFactory());
        SVGDOMImplementation.svg11Factories.put("feDistantLight", new FeDistantLightElementFactory());
        SVGDOMImplementation.svg11Factories.put("feFlood", new FeFloodElementFactory());
        SVGDOMImplementation.svg11Factories.put("feFuncA", new FeFuncAElementFactory());
        SVGDOMImplementation.svg11Factories.put("feFuncR", new FeFuncRElementFactory());
        SVGDOMImplementation.svg11Factories.put("feFuncG", new FeFuncGElementFactory());
        SVGDOMImplementation.svg11Factories.put("feFuncB", new FeFuncBElementFactory());
        SVGDOMImplementation.svg11Factories.put("feGaussianBlur", new FeGaussianBlurElementFactory());
        SVGDOMImplementation.svg11Factories.put("feImage", new FeImageElementFactory());
        SVGDOMImplementation.svg11Factories.put("feMerge", new FeMergeElementFactory());
        SVGDOMImplementation.svg11Factories.put("feMergeNode", new FeMergeNodeElementFactory());
        SVGDOMImplementation.svg11Factories.put("feMorphology", new FeMorphologyElementFactory());
        SVGDOMImplementation.svg11Factories.put("feOffset", new FeOffsetElementFactory());
        SVGDOMImplementation.svg11Factories.put("fePointLight", new FePointLightElementFactory());
        SVGDOMImplementation.svg11Factories.put("feSpecularLighting", new FeSpecularLightingElementFactory());
        SVGDOMImplementation.svg11Factories.put("feSpotLight", new FeSpotLightElementFactory());
        SVGDOMImplementation.svg11Factories.put("feTile", new FeTileElementFactory());
        SVGDOMImplementation.svg11Factories.put("feTurbulence", new FeTurbulenceElementFactory());
        SVGDOMImplementation.svg11Factories.put("filter", new FilterElementFactory());
        SVGDOMImplementation.svg11Factories.put("font", new FontElementFactory());
        SVGDOMImplementation.svg11Factories.put("font-face", new FontFaceElementFactory());
        SVGDOMImplementation.svg11Factories.put("font-face-format", new FontFaceFormatElementFactory());
        SVGDOMImplementation.svg11Factories.put("font-face-name", new FontFaceNameElementFactory());
        SVGDOMImplementation.svg11Factories.put("font-face-src", new FontFaceSrcElementFactory());
        SVGDOMImplementation.svg11Factories.put("font-face-uri", new FontFaceUriElementFactory());
        SVGDOMImplementation.svg11Factories.put("foreignObject", new ForeignObjectElementFactory());
        SVGDOMImplementation.svg11Factories.put("g", new GElementFactory());
        SVGDOMImplementation.svg11Factories.put("glyph", new GlyphElementFactory());
        SVGDOMImplementation.svg11Factories.put("glyphRef", new GlyphRefElementFactory());
        SVGDOMImplementation.svg11Factories.put("hkern", new HkernElementFactory());
        SVGDOMImplementation.svg11Factories.put("image", new ImageElementFactory());
        SVGDOMImplementation.svg11Factories.put("line", new LineElementFactory());
        SVGDOMImplementation.svg11Factories.put("linearGradient", new LinearGradientElementFactory());
        SVGDOMImplementation.svg11Factories.put("marker", new MarkerElementFactory());
        SVGDOMImplementation.svg11Factories.put("mask", new MaskElementFactory());
        SVGDOMImplementation.svg11Factories.put("metadata", new MetadataElementFactory());
        SVGDOMImplementation.svg11Factories.put("missing-glyph", new MissingGlyphElementFactory());
        SVGDOMImplementation.svg11Factories.put("mpath", new MpathElementFactory());
        SVGDOMImplementation.svg11Factories.put("path", new PathElementFactory());
        SVGDOMImplementation.svg11Factories.put("pattern", new PatternElementFactory());
        SVGDOMImplementation.svg11Factories.put("polygon", new PolygonElementFactory());
        SVGDOMImplementation.svg11Factories.put("polyline", new PolylineElementFactory());
        SVGDOMImplementation.svg11Factories.put("radialGradient", new RadialGradientElementFactory());
        SVGDOMImplementation.svg11Factories.put("rect", new RectElementFactory());
        SVGDOMImplementation.svg11Factories.put("set", new SetElementFactory());
        SVGDOMImplementation.svg11Factories.put("script", new ScriptElementFactory());
        SVGDOMImplementation.svg11Factories.put("stop", new StopElementFactory());
        SVGDOMImplementation.svg11Factories.put("style", new StyleElementFactory());
        SVGDOMImplementation.svg11Factories.put("svg", new SvgElementFactory());
        SVGDOMImplementation.svg11Factories.put("switch", new SwitchElementFactory());
        SVGDOMImplementation.svg11Factories.put("symbol", new SymbolElementFactory());
        SVGDOMImplementation.svg11Factories.put("text", new TextElementFactory());
        SVGDOMImplementation.svg11Factories.put("textPath", new TextPathElementFactory());
        SVGDOMImplementation.svg11Factories.put("title", new TitleElementFactory());
        SVGDOMImplementation.svg11Factories.put("tref", new TrefElementFactory());
        SVGDOMImplementation.svg11Factories.put("tspan", new TspanElementFactory());
        SVGDOMImplementation.svg11Factories.put("use", new UseElementFactory());
        SVGDOMImplementation.svg11Factories.put("view", new ViewElementFactory());
        SVGDOMImplementation.svg11Factories.put("vkern", new VkernElementFactory());
        DOM_IMPLEMENTATION = new SVGDOMImplementation();
    }
    
    protected static class AElementFactory implements ElementFactory
    {
        public AElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMAElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class AltGlyphElementFactory implements ElementFactory
    {
        public AltGlyphElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMAltGlyphElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class AltGlyphDefElementFactory implements ElementFactory
    {
        public AltGlyphDefElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMAltGlyphDefElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class AltGlyphItemElementFactory implements ElementFactory
    {
        public AltGlyphItemElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMAltGlyphItemElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class AnimateElementFactory implements ElementFactory
    {
        public AnimateElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMAnimateElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class AnimateColorElementFactory implements ElementFactory
    {
        public AnimateColorElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMAnimateColorElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class AnimateMotionElementFactory implements ElementFactory
    {
        public AnimateMotionElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMAnimateMotionElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class AnimateTransformElementFactory implements ElementFactory
    {
        public AnimateTransformElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMAnimateTransformElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class CircleElementFactory implements ElementFactory
    {
        public CircleElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMCircleElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class ClipPathElementFactory implements ElementFactory
    {
        public ClipPathElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMClipPathElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class ColorProfileElementFactory implements ElementFactory
    {
        public ColorProfileElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMColorProfileElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class CursorElementFactory implements ElementFactory
    {
        public CursorElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMCursorElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class DefinitionSrcElementFactory implements ElementFactory
    {
        public DefinitionSrcElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMDefinitionSrcElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class DefsElementFactory implements ElementFactory
    {
        public DefsElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMDefsElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class DescElementFactory implements ElementFactory
    {
        public DescElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMDescElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class EllipseElementFactory implements ElementFactory
    {
        public EllipseElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMEllipseElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeBlendElementFactory implements ElementFactory
    {
        public FeBlendElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEBlendElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeColorMatrixElementFactory implements ElementFactory
    {
        public FeColorMatrixElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEColorMatrixElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeComponentTransferElementFactory implements ElementFactory
    {
        public FeComponentTransferElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEComponentTransferElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeCompositeElementFactory implements ElementFactory
    {
        public FeCompositeElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFECompositeElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeConvolveMatrixElementFactory implements ElementFactory
    {
        public FeConvolveMatrixElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEConvolveMatrixElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeDiffuseLightingElementFactory implements ElementFactory
    {
        public FeDiffuseLightingElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEDiffuseLightingElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeDisplacementMapElementFactory implements ElementFactory
    {
        public FeDisplacementMapElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEDisplacementMapElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeDistantLightElementFactory implements ElementFactory
    {
        public FeDistantLightElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEDistantLightElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeFloodElementFactory implements ElementFactory
    {
        public FeFloodElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEFloodElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeFuncAElementFactory implements ElementFactory
    {
        public FeFuncAElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEFuncAElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeFuncRElementFactory implements ElementFactory
    {
        public FeFuncRElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEFuncRElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeFuncGElementFactory implements ElementFactory
    {
        public FeFuncGElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEFuncGElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeFuncBElementFactory implements ElementFactory
    {
        public FeFuncBElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEFuncBElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeGaussianBlurElementFactory implements ElementFactory
    {
        public FeGaussianBlurElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEGaussianBlurElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeImageElementFactory implements ElementFactory
    {
        public FeImageElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEImageElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeMergeElementFactory implements ElementFactory
    {
        public FeMergeElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEMergeElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeMergeNodeElementFactory implements ElementFactory
    {
        public FeMergeNodeElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEMergeNodeElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeMorphologyElementFactory implements ElementFactory
    {
        public FeMorphologyElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEMorphologyElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeOffsetElementFactory implements ElementFactory
    {
        public FeOffsetElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEOffsetElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FePointLightElementFactory implements ElementFactory
    {
        public FePointLightElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFEPointLightElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeSpecularLightingElementFactory implements ElementFactory
    {
        public FeSpecularLightingElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFESpecularLightingElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeSpotLightElementFactory implements ElementFactory
    {
        public FeSpotLightElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFESpotLightElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeTileElementFactory implements ElementFactory
    {
        public FeTileElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFETileElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FeTurbulenceElementFactory implements ElementFactory
    {
        public FeTurbulenceElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFETurbulenceElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FilterElementFactory implements ElementFactory
    {
        public FilterElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFilterElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FontElementFactory implements ElementFactory
    {
        public FontElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFontElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FontFaceElementFactory implements ElementFactory
    {
        public FontFaceElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFontFaceElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FontFaceFormatElementFactory implements ElementFactory
    {
        public FontFaceFormatElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFontFaceFormatElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FontFaceNameElementFactory implements ElementFactory
    {
        public FontFaceNameElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFontFaceNameElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FontFaceSrcElementFactory implements ElementFactory
    {
        public FontFaceSrcElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFontFaceSrcElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class FontFaceUriElementFactory implements ElementFactory
    {
        public FontFaceUriElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMFontFaceUriElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class ForeignObjectElementFactory implements ElementFactory
    {
        public ForeignObjectElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMForeignObjectElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class GElementFactory implements ElementFactory
    {
        public GElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMGElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class GlyphElementFactory implements ElementFactory
    {
        public GlyphElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMGlyphElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class GlyphRefElementFactory implements ElementFactory
    {
        public GlyphRefElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMGlyphRefElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class HkernElementFactory implements ElementFactory
    {
        public HkernElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMHKernElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class ImageElementFactory implements ElementFactory
    {
        public ImageElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMImageElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class LineElementFactory implements ElementFactory
    {
        public LineElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMLineElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class LinearGradientElementFactory implements ElementFactory
    {
        public LinearGradientElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMLinearGradientElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class MarkerElementFactory implements ElementFactory
    {
        public MarkerElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMMarkerElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class MaskElementFactory implements ElementFactory
    {
        public MaskElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMMaskElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class MetadataElementFactory implements ElementFactory
    {
        public MetadataElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMMetadataElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class MissingGlyphElementFactory implements ElementFactory
    {
        public MissingGlyphElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMMissingGlyphElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class MpathElementFactory implements ElementFactory
    {
        public MpathElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMMPathElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class PathElementFactory implements ElementFactory
    {
        public PathElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMPathElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class PatternElementFactory implements ElementFactory
    {
        public PatternElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMPatternElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class PolygonElementFactory implements ElementFactory
    {
        public PolygonElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMPolygonElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class PolylineElementFactory implements ElementFactory
    {
        public PolylineElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMPolylineElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class RadialGradientElementFactory implements ElementFactory
    {
        public RadialGradientElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMRadialGradientElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class RectElementFactory implements ElementFactory
    {
        public RectElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMRectElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class ScriptElementFactory implements ElementFactory
    {
        public ScriptElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMScriptElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class SetElementFactory implements ElementFactory
    {
        public SetElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMSetElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class StopElementFactory implements ElementFactory
    {
        public StopElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMStopElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class StyleElementFactory implements ElementFactory
    {
        public StyleElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMStyleElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class SvgElementFactory implements ElementFactory
    {
        public SvgElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMSVGElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class SwitchElementFactory implements ElementFactory
    {
        public SwitchElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMSwitchElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class SymbolElementFactory implements ElementFactory
    {
        public SymbolElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMSymbolElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class TextElementFactory implements ElementFactory
    {
        public TextElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMTextElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class TextPathElementFactory implements ElementFactory
    {
        public TextPathElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMTextPathElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class TitleElementFactory implements ElementFactory
    {
        public TitleElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMTitleElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class TrefElementFactory implements ElementFactory
    {
        public TrefElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMTRefElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class TspanElementFactory implements ElementFactory
    {
        public TspanElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMTSpanElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class UseElementFactory implements ElementFactory
    {
        public UseElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMUseElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class ViewElementFactory implements ElementFactory
    {
        public ViewElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMViewElement(prefix, (AbstractDocument)doc);
        }
    }
    
    protected static class VkernElementFactory implements ElementFactory
    {
        public VkernElementFactory() {
        }
        
        @Override
        public Element create(final String prefix, final Document doc) {
            return new SVGOMVKernElement(prefix, (AbstractDocument)doc);
        }
    }
}
