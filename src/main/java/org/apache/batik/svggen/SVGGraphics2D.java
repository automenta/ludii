// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.Iterator;
import java.util.Collection;
import java.awt.GraphicsConfiguration;
import java.awt.Paint;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.awt.font.GlyphVector;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImageOp;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.Map;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import java.awt.geom.AffineTransform;
import java.awt.font.TextAttribute;
import java.util.HashSet;
import java.awt.image.BufferedImage;
import org.w3c.dom.Document;
import java.util.Set;
import java.awt.Graphics2D;
import java.awt.Dimension;
import org.apache.batik.ext.awt.g2d.AbstractGraphics2D;

public class SVGGraphics2D extends AbstractGraphics2D implements Cloneable, SVGSyntax, ErrorConstants
{
    public static final String DEFAULT_XML_ENCODING = "ISO-8859-1";
    public static final int DEFAULT_MAX_GC_OVERRIDES = 3;
    protected DOMTreeManager domTreeManager;
    protected DOMGroupManager domGroupManager;
    protected SVGGeneratorContext generatorCtx;
    protected SVGShape shapeConverter;
    protected Dimension svgCanvasSize;
    protected Graphics2D fmg;
    protected Set unsupportedAttributes;
    
    public final Dimension getSVGCanvasSize() {
        return this.svgCanvasSize;
    }
    
    public final void setSVGCanvasSize(final Dimension svgCanvasSize) {
        this.svgCanvasSize = new Dimension(svgCanvasSize);
    }
    
    public final SVGGeneratorContext getGeneratorContext() {
        return this.generatorCtx;
    }
    
    public final SVGShape getShapeConverter() {
        return this.shapeConverter;
    }
    
    public final DOMTreeManager getDOMTreeManager() {
        return this.domTreeManager;
    }
    
    protected final void setDOMTreeManager(final DOMTreeManager treeMgr) {
        this.domTreeManager = treeMgr;
        this.generatorCtx.genericImageHandler.setDOMTreeManager(this.domTreeManager);
    }
    
    protected final DOMGroupManager getDOMGroupManager() {
        return this.domGroupManager;
    }
    
    protected final void setDOMGroupManager(final DOMGroupManager groupMgr) {
        this.domGroupManager = groupMgr;
    }
    
    public final Document getDOMFactory() {
        return this.generatorCtx.domFactory;
    }
    
    public final ImageHandler getImageHandler() {
        return this.generatorCtx.imageHandler;
    }
    
    public final GenericImageHandler getGenericImageHandler() {
        return this.generatorCtx.genericImageHandler;
    }
    
    public final ExtensionHandler getExtensionHandler() {
        return this.generatorCtx.extensionHandler;
    }
    
    public final void setExtensionHandler(final ExtensionHandler extensionHandler) {
        this.generatorCtx.setExtensionHandler(extensionHandler);
    }
    
    public SVGGraphics2D(final Document domFactory) {
        this(SVGGeneratorContext.createDefault(domFactory), false);
    }
    
    public SVGGraphics2D(final Document domFactory, final ImageHandler imageHandler, final ExtensionHandler extensionHandler, final boolean textAsShapes) {
        this(buildSVGGeneratorContext(domFactory, imageHandler, extensionHandler), textAsShapes);
    }
    
    public static SVGGeneratorContext buildSVGGeneratorContext(final Document domFactory, final ImageHandler imageHandler, final ExtensionHandler extensionHandler) {
        final SVGGeneratorContext generatorCtx = new SVGGeneratorContext(domFactory);
        generatorCtx.setIDGenerator(new SVGIDGenerator());
        generatorCtx.setExtensionHandler(extensionHandler);
        generatorCtx.setImageHandler(imageHandler);
        generatorCtx.setStyleHandler(new DefaultStyleHandler());
        generatorCtx.setComment("Generated by the Batik Graphics2D SVG Generator");
        generatorCtx.setErrorHandler(new DefaultErrorHandler());
        return generatorCtx;
    }
    
    public SVGGraphics2D(final SVGGeneratorContext generatorCtx, final boolean textAsShapes) {
        super(textAsShapes);
        final BufferedImage bi = new BufferedImage(1, 1, 2);
        this.fmg = bi.createGraphics();
        (this.unsupportedAttributes = new HashSet()).add(TextAttribute.BACKGROUND);
        this.unsupportedAttributes.add(TextAttribute.BIDI_EMBEDDING);
        this.unsupportedAttributes.add(TextAttribute.CHAR_REPLACEMENT);
        this.unsupportedAttributes.add(TextAttribute.JUSTIFICATION);
        this.unsupportedAttributes.add(TextAttribute.RUN_DIRECTION);
        this.unsupportedAttributes.add(TextAttribute.SUPERSCRIPT);
        this.unsupportedAttributes.add(TextAttribute.SWAP_COLORS);
        this.unsupportedAttributes.add(TextAttribute.TRANSFORM);
        this.unsupportedAttributes.add(TextAttribute.WIDTH);
        if (generatorCtx == null) {
            throw new SVGGraphics2DRuntimeException("generatorContext should not be null");
        }
        this.setGeneratorContext(generatorCtx);
    }
    
    protected void setGeneratorContext(final SVGGeneratorContext generatorCtx) {
        this.generatorCtx = generatorCtx;
        this.gc = new GraphicContext(new AffineTransform());
        final SVGGeneratorContext.GraphicContextDefaults gcDefaults = generatorCtx.getGraphicContextDefaults();
        if (gcDefaults != null) {
            if (gcDefaults.getPaint() != null) {
                this.gc.setPaint(gcDefaults.getPaint());
            }
            if (gcDefaults.getStroke() != null) {
                this.gc.setStroke(gcDefaults.getStroke());
            }
            if (gcDefaults.getComposite() != null) {
                this.gc.setComposite(gcDefaults.getComposite());
            }
            if (gcDefaults.getClip() != null) {
                this.gc.setClip(gcDefaults.getClip());
            }
            if (gcDefaults.getRenderingHints() != null) {
                this.gc.setRenderingHints(gcDefaults.getRenderingHints());
            }
            if (gcDefaults.getFont() != null) {
                this.gc.setFont(gcDefaults.getFont());
            }
            if (gcDefaults.getBackground() != null) {
                this.gc.setBackground(gcDefaults.getBackground());
            }
        }
        this.shapeConverter = new SVGShape(generatorCtx);
        this.domTreeManager = new DOMTreeManager(this.gc, generatorCtx, 3);
        this.domGroupManager = new DOMGroupManager(this.gc, this.domTreeManager);
        this.domTreeManager.addGroupManager(this.domGroupManager);
        generatorCtx.genericImageHandler.setDOMTreeManager(this.domTreeManager);
    }
    
    public SVGGraphics2D(final SVGGraphics2D g) {
        super(g);
        final BufferedImage bi = new BufferedImage(1, 1, 2);
        this.fmg = bi.createGraphics();
        (this.unsupportedAttributes = new HashSet()).add(TextAttribute.BACKGROUND);
        this.unsupportedAttributes.add(TextAttribute.BIDI_EMBEDDING);
        this.unsupportedAttributes.add(TextAttribute.CHAR_REPLACEMENT);
        this.unsupportedAttributes.add(TextAttribute.JUSTIFICATION);
        this.unsupportedAttributes.add(TextAttribute.RUN_DIRECTION);
        this.unsupportedAttributes.add(TextAttribute.SUPERSCRIPT);
        this.unsupportedAttributes.add(TextAttribute.SWAP_COLORS);
        this.unsupportedAttributes.add(TextAttribute.TRANSFORM);
        this.unsupportedAttributes.add(TextAttribute.WIDTH);
        this.generatorCtx = g.generatorCtx;
        this.gc.validateTransformStack();
        this.shapeConverter = g.shapeConverter;
        this.domTreeManager = g.domTreeManager;
        this.domGroupManager = new DOMGroupManager(this.gc, this.domTreeManager);
        this.domTreeManager.addGroupManager(this.domGroupManager);
    }
    
    public void stream(final String svgFileName) throws SVGGraphics2DIOException {
        this.stream(svgFileName, false);
    }
    
    public void stream(final String svgFileName, final boolean useCss) throws SVGGraphics2DIOException {
        try {
            final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(svgFileName), "ISO-8859-1");
            this.stream(writer, useCss);
            writer.flush();
            writer.close();
        }
        catch (SVGGraphics2DIOException io) {
            throw io;
        }
        catch (IOException e) {
            this.generatorCtx.errorHandler.handleError(new SVGGraphics2DIOException(e));
        }
    }
    
    public void stream(final Writer writer) throws SVGGraphics2DIOException {
        this.stream(writer, false);
    }
    
    public void stream(final Writer writer, final boolean useCss, final boolean escaped) throws SVGGraphics2DIOException {
        final Element svgRoot = this.getRoot();
        this.stream(svgRoot, writer, useCss, escaped);
    }
    
    public void stream(final Writer writer, final boolean useCss) throws SVGGraphics2DIOException {
        final Element svgRoot = this.getRoot();
        this.stream(svgRoot, writer, useCss, false);
    }
    
    public void stream(final Element svgRoot, final Writer writer) throws SVGGraphics2DIOException {
        this.stream(svgRoot, writer, false, false);
    }
    
    public void stream(final Element svgRoot, final Writer writer, final boolean useCss, final boolean escaped) throws SVGGraphics2DIOException {
        final Node rootParent = svgRoot.getParentNode();
        final Node nextSibling = svgRoot.getNextSibling();
        try {
            svgRoot.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/svg");
            svgRoot.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xlink", "http://www.w3.org/1999/xlink");
            final DocumentFragment svgDocument = svgRoot.getOwnerDocument().createDocumentFragment();
            svgDocument.appendChild(svgRoot);
            if (useCss) {
                SVGCSSStyler.style(svgDocument);
            }
            XmlWriter.writeXml(svgDocument, writer, escaped);
            writer.flush();
        }
        catch (SVGGraphics2DIOException e) {
            this.generatorCtx.errorHandler.handleError(e);
        }
        catch (IOException io) {
            this.generatorCtx.errorHandler.handleError(new SVGGraphics2DIOException(io));
        }
        finally {
            if (rootParent != null) {
                if (nextSibling == null) {
                    rootParent.appendChild(svgRoot);
                }
                else {
                    rootParent.insertBefore(svgRoot, nextSibling);
                }
            }
        }
    }
    
    public List getDefinitionSet() {
        return this.domTreeManager.getDefinitionSet();
    }
    
    public Element getTopLevelGroup() {
        return this.getTopLevelGroup(true);
    }
    
    public Element getTopLevelGroup(final boolean includeDefinitionSet) {
        return this.domTreeManager.getTopLevelGroup(includeDefinitionSet);
    }
    
    public void setTopLevelGroup(final Element topLevelGroup) {
        this.domTreeManager.setTopLevelGroup(topLevelGroup);
    }
    
    public Element getRoot() {
        return this.getRoot(null);
    }
    
    public Element getRoot(Element svgRoot) {
        svgRoot = this.domTreeManager.getRoot(svgRoot);
        if (this.svgCanvasSize != null) {
            svgRoot.setAttributeNS(null, "width", String.valueOf(this.svgCanvasSize.width));
            svgRoot.setAttributeNS(null, "height", String.valueOf(this.svgCanvasSize.height));
        }
        return svgRoot;
    }
    
    @Override
    public Graphics create() {
        return new SVGGraphics2D(this);
    }
    
    @Override
    public void setXORMode(final Color c1) {
        this.generatorCtx.errorHandler.handleError(new SVGGraphics2DRuntimeException("XOR Mode is not supported by Graphics2D SVG Generator"));
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font f) {
        return this.fmg.getFontMetrics(f);
    }
    
    @Override
    public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final ImageObserver observer) {
        final Element imageElement = this.getGenericImageHandler().createElement(this.getGeneratorContext());
        final AffineTransform xform = this.getGenericImageHandler().handleImage(img, imageElement, x, y, img.getWidth(null), img.getHeight(null), this.getGeneratorContext());
        if (xform == null) {
            this.domGroupManager.addElement(imageElement);
        }
        else {
            AffineTransform inverseTransform = null;
            try {
                inverseTransform = xform.createInverse();
            }
            catch (NoninvertibleTransformException e) {
                throw new SVGGraphics2DRuntimeException("unexpected exception");
            }
            this.gc.transform(xform);
            this.domGroupManager.addElement(imageElement);
            this.gc.transform(inverseTransform);
        }
        return true;
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final ImageObserver observer) {
        final Element imageElement = this.getGenericImageHandler().createElement(this.getGeneratorContext());
        final AffineTransform xform = this.getGenericImageHandler().handleImage(img, imageElement, x, y, width, height, this.getGeneratorContext());
        if (xform == null) {
            this.domGroupManager.addElement(imageElement);
        }
        else {
            AffineTransform inverseTransform = null;
            try {
                inverseTransform = xform.createInverse();
            }
            catch (NoninvertibleTransformException e) {
                throw new SVGGraphics2DRuntimeException("unexpected exception");
            }
            this.gc.transform(xform);
            this.domGroupManager.addElement(imageElement);
            this.gc.transform(inverseTransform);
        }
        return true;
    }
    
    @Override
    public void dispose() {
        this.domTreeManager.removeGroupManager(this.domGroupManager);
    }
    
    @Override
    public void draw(final Shape s) {
        final Stroke stroke = this.gc.getStroke();
        if (stroke instanceof BasicStroke) {
            final Element svgShape = this.shapeConverter.toSVG(s);
            if (svgShape != null) {
                this.domGroupManager.addElement(svgShape, (short)1);
            }
        }
        else {
            final Shape strokedShape = stroke.createStrokedShape(s);
            this.fill(strokedShape);
        }
    }
    
    @Override
    public boolean drawImage(final Image img, final AffineTransform xform, final ImageObserver obs) {
        boolean retVal = true;
        if (xform == null) {
            retVal = this.drawImage(img, 0, 0, null);
        }
        else if (xform.getDeterminant() != 0.0) {
            AffineTransform inverseTransform = null;
            try {
                inverseTransform = xform.createInverse();
            }
            catch (NoninvertibleTransformException e) {
                throw new SVGGraphics2DRuntimeException("unexpected exception");
            }
            this.gc.transform(xform);
            retVal = this.drawImage(img, 0, 0, null);
            this.gc.transform(inverseTransform);
        }
        else {
            final AffineTransform savTransform = new AffineTransform(this.gc.getTransform());
            this.gc.transform(xform);
            retVal = this.drawImage(img, 0, 0, null);
            this.gc.setTransform(savTransform);
        }
        return retVal;
    }
    
    @Override
    public void drawImage(BufferedImage img, final BufferedImageOp op, final int x, final int y) {
        img = op.filter(img, null);
        this.drawImage(img, x, y, null);
    }
    
    @Override
    public void drawRenderedImage(final RenderedImage img, final AffineTransform trans2) {
        final Element image = this.getGenericImageHandler().createElement(this.getGeneratorContext());
        final AffineTransform trans3 = this.getGenericImageHandler().handleImage(img, image, img.getMinX(), img.getMinY(), img.getWidth(), img.getHeight(), this.getGeneratorContext());
        AffineTransform xform;
        if (trans2 == null) {
            xform = trans3;
        }
        else if (trans3 == null) {
            xform = trans2;
        }
        else {
            xform = new AffineTransform(trans2);
            xform.concatenate(trans3);
        }
        if (xform == null) {
            this.domGroupManager.addElement(image);
        }
        else if (xform.getDeterminant() != 0.0) {
            AffineTransform inverseTransform = null;
            try {
                inverseTransform = xform.createInverse();
            }
            catch (NoninvertibleTransformException e) {
                throw new SVGGraphics2DRuntimeException("unexpected exception");
            }
            this.gc.transform(xform);
            this.domGroupManager.addElement(image);
            this.gc.transform(inverseTransform);
        }
        else {
            final AffineTransform savTransform = new AffineTransform(this.gc.getTransform());
            this.gc.transform(xform);
            this.domGroupManager.addElement(image);
            this.gc.setTransform(savTransform);
        }
    }
    
    @Override
    public void drawRenderableImage(final RenderableImage img, final AffineTransform trans2) {
        final Element image = this.getGenericImageHandler().createElement(this.getGeneratorContext());
        final AffineTransform trans3 = this.getGenericImageHandler().handleImage(img, image, img.getMinX(), img.getMinY(), img.getWidth(), img.getHeight(), this.getGeneratorContext());
        AffineTransform xform;
        if (trans2 == null) {
            xform = trans3;
        }
        else if (trans3 == null) {
            xform = trans2;
        }
        else {
            xform = new AffineTransform(trans2);
            xform.concatenate(trans3);
        }
        if (xform == null) {
            this.domGroupManager.addElement(image);
        }
        else if (xform.getDeterminant() != 0.0) {
            AffineTransform inverseTransform = null;
            try {
                inverseTransform = xform.createInverse();
            }
            catch (NoninvertibleTransformException e) {
                throw new SVGGraphics2DRuntimeException("unexpected exception");
            }
            this.gc.transform(xform);
            this.domGroupManager.addElement(image);
            this.gc.transform(inverseTransform);
        }
        else {
            final AffineTransform savTransform = new AffineTransform(this.gc.getTransform());
            this.gc.transform(xform);
            this.domGroupManager.addElement(image);
            this.gc.setTransform(savTransform);
        }
    }
    
    @Override
    public void drawString(final String s, final float x, final float y) {
        if (this.textAsShapes) {
            final GlyphVector gv = this.getFont().createGlyphVector(this.getFontRenderContext(), s);
            this.drawGlyphVector(gv, x, y);
            return;
        }
        if (this.generatorCtx.svgFont) {
            this.domTreeManager.gcConverter.getFontConverter().recordFontUsage(s, this.getFont());
        }
        final AffineTransform savTxf = this.getTransform();
        final AffineTransform txtTxf = this.transformText(x, y);
        final Element text = this.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "text");
        text.setAttributeNS(null, "x", this.generatorCtx.doubleString(x));
        text.setAttributeNS(null, "y", this.generatorCtx.doubleString(y));
        text.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", "preserve");
        text.appendChild(this.getDOMFactory().createTextNode(s));
        this.domGroupManager.addElement(text, (short)16);
        if (txtTxf != null) {
            this.setTransform(savTxf);
        }
    }
    
    private AffineTransform transformText(final float x, final float y) {
        AffineTransform txtTxf = null;
        final Font font = this.getFont();
        if (font != null) {
            txtTxf = font.getTransform();
            if (txtTxf != null && !txtTxf.isIdentity()) {
                final AffineTransform t = new AffineTransform();
                t.translate(x, y);
                t.concatenate(txtTxf);
                t.translate(-x, -y);
                this.transform(t);
            }
            else {
                txtTxf = null;
            }
        }
        return txtTxf;
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator ati, final float x, final float y) {
        if (this.textAsShapes || this.usesUnsupportedAttributes(ati)) {
            final TextLayout layout = new TextLayout(ati, this.getFontRenderContext());
            layout.draw(this, x, y);
            return;
        }
        boolean multiSpans = false;
        if (ati.getRunLimit() < ati.getEndIndex()) {
            multiSpans = true;
        }
        final Element text = this.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "text");
        text.setAttributeNS(null, "x", this.generatorCtx.doubleString(x));
        text.setAttributeNS(null, "y", this.generatorCtx.doubleString(y));
        text.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", "preserve");
        final Font baseFont = this.getFont();
        final Paint basePaint = this.getPaint();
        char ch = ati.first();
        this.setTextElementFill(ati);
        this.setTextFontAttributes(ati, baseFont);
        final SVGGraphicContext textGC = this.domTreeManager.getGraphicContextConverter().toSVG(this.gc);
        this.domGroupManager.addElement(text, (short)16);
        textGC.getContext().put("stroke", "none");
        textGC.getGroupContext().put("stroke", "none");
        boolean firstSpan = true;
        final AffineTransform savTxf = this.getTransform();
        AffineTransform txtTxf = null;
        while (ch != '\uffff') {
            Element tspan = text;
            if (multiSpans) {
                tspan = this.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "tspan");
                text.appendChild(tspan);
            }
            this.setTextElementFill(ati);
            final boolean resetTransform = this.setTextFontAttributes(ati, baseFont);
            if (resetTransform || firstSpan) {
                txtTxf = this.transformText(x, y);
                firstSpan = false;
            }
            final int start = ati.getIndex();
            final int end = ati.getRunLimit() - 1;
            final StringBuffer buf = new StringBuffer(end - start);
            buf.append(ch);
            for (int i = start; i < end; ++i) {
                ch = ati.next();
                buf.append(ch);
            }
            final String s = buf.toString();
            if (this.generatorCtx.isEmbeddedFontsOn()) {
                this.getDOMTreeManager().getGraphicContextConverter().getFontConverter().recordFontUsage(s, this.getFont());
            }
            final SVGGraphicContext elementGC = this.domTreeManager.gcConverter.toSVG(this.gc);
            elementGC.getGroupContext().put("stroke", "none");
            final SVGGraphicContext deltaGC = DOMGroupManager.processDeltaGC(elementGC, textGC);
            this.setTextElementAttributes(deltaGC, ati);
            this.domTreeManager.getStyleHandler().setStyle(tspan, deltaGC.getContext(), this.domTreeManager.getGeneratorContext());
            tspan.appendChild(this.getDOMFactory().createTextNode(s));
            if ((resetTransform || firstSpan) && txtTxf != null) {
                this.setTransform(savTxf);
            }
            ch = ati.next();
        }
        this.setFont(baseFont);
        this.setPaint(basePaint);
    }
    
    @Override
    public void fill(final Shape s) {
        final Element svgShape = this.shapeConverter.toSVG(s);
        if (svgShape != null) {
            this.domGroupManager.addElement(svgShape, (short)16);
        }
    }
    
    private boolean setTextFontAttributes(final AttributedCharacterIterator ati, final Font baseFont) {
        boolean resetTransform = false;
        if (ati.getAttribute(TextAttribute.FONT) != null || ati.getAttribute(TextAttribute.FAMILY) != null || ati.getAttribute(TextAttribute.WEIGHT) != null || ati.getAttribute(TextAttribute.POSTURE) != null || ati.getAttribute(TextAttribute.SIZE) != null) {
            final Map m = ati.getAttributes();
            final Font f = baseFont.deriveFont(m);
            this.setFont(f);
            resetTransform = true;
        }
        return resetTransform;
    }
    
    private void setTextElementFill(final AttributedCharacterIterator ati) {
        if (ati.getAttribute(TextAttribute.FOREGROUND) != null) {
            final Color color = (Color)ati.getAttribute(TextAttribute.FOREGROUND);
            this.setPaint(color);
        }
    }
    
    private void setTextElementAttributes(final SVGGraphicContext tspanGC, final AttributedCharacterIterator ati) {
        String decoration = "";
        if (this.isUnderline(ati)) {
            decoration += "underline ";
        }
        if (this.isStrikeThrough(ati)) {
            decoration += "line-through ";
        }
        final int len = decoration.length();
        if (len != 0) {
            tspanGC.getContext().put("text-decoration", decoration.substring(0, len - 1));
        }
    }
    
    private boolean isBold(final AttributedCharacterIterator ati) {
        final Object weight = ati.getAttribute(TextAttribute.WEIGHT);
        return weight != null && !weight.equals(TextAttribute.WEIGHT_REGULAR) && !weight.equals(TextAttribute.WEIGHT_DEMILIGHT) && !weight.equals(TextAttribute.WEIGHT_EXTRA_LIGHT) && !weight.equals(TextAttribute.WEIGHT_LIGHT);
    }
    
    private boolean isItalic(final AttributedCharacterIterator ati) {
        final Object attr = ati.getAttribute(TextAttribute.POSTURE);
        return TextAttribute.POSTURE_OBLIQUE.equals(attr);
    }
    
    private boolean isUnderline(final AttributedCharacterIterator ati) {
        final Object attr = ati.getAttribute(TextAttribute.UNDERLINE);
        return TextAttribute.UNDERLINE_ON.equals(attr);
    }
    
    private boolean isStrikeThrough(final AttributedCharacterIterator ati) {
        final Object attr = ati.getAttribute(TextAttribute.STRIKETHROUGH);
        return TextAttribute.STRIKETHROUGH_ON.equals(attr);
    }
    
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return null;
    }
    
    public void setUnsupportedAttributes(final Set attrs) {
        if (attrs == null) {
            this.unsupportedAttributes = null;
        }
        else {
            this.unsupportedAttributes = new HashSet(attrs);
        }
    }
    
    public boolean usesUnsupportedAttributes(final AttributedCharacterIterator aci) {
        if (this.unsupportedAttributes == null) {
            return false;
        }
        final Set allAttrs = aci.getAllAttributeKeys();
        for (final Object allAttr : allAttrs) {
            if (this.unsupportedAttributes.contains(allAttr)) {
                return true;
            }
        }
        return false;
    }
}
