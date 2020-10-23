// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d.svg;

import org.jfree.graphics2d.*;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SVGGraphics2D extends Graphics2D
{
    private static final String CLIP_KEY_PREFIX = "clip-";
    private final int width;
    private final int height;
    private final SVGUnits units;
    private String shapeRendering;
    private String textRendering;
    private SVGUnits fontSizeUnits;
    private final RenderingHints hints;
    private boolean checkStrokeControlHint;
    private int transformDP;
    private DecimalFormat transformFormat;
    private int geometryDP;
    private DecimalFormat geometryFormat;
    private final StringBuilder sb;
    private String defsKeyPrefix;
    private Map<GradientPaintKey, String> gradientPaints;
    private Map<LinearGradientPaintKey, String> linearGradientPaints;
    private Map<RadialGradientPaintKey, String> radialGradientPaints;
    private List<String> clipPaths;
    private String filePrefix;
    private String fileSuffix;
    private List<ImageElement> imageElements;
    private Shape clip;
    private String clipRef;
    private AffineTransform transform;
    private Paint paint;
    private Color color;
    private Composite composite;
    private Stroke stroke;
    private double zeroStrokeWidth;
    private Font font;
    private final FontRenderContext fontRenderContext;
    private FontMapper fontMapper;
    private Color background;
    private BufferedImage fmImage;
    private Graphics2D fmImageG2D;
    private Line2D line;
    Rectangle2D rect;
    private RoundRectangle2D roundRect;
    private Ellipse2D oval;
    private Arc2D arc;
    private String gradientPaintRef;
    private GraphicsConfiguration deviceConfiguration;
    private final Set<String> elementIDs;
    private static final String DEFAULT_STROKE_CAP = "butt";
    private static final String DEFAULT_STROKE_JOIN = "miter";
    private static final float DEFAULT_MITER_LIMIT = 4.0f;
    
    public SVGGraphics2D(final int width, final int height) {
        this(width, height, null, new StringBuilder());
    }
    
    public SVGGraphics2D(final int width, final int height, final SVGUnits units) {
        this(width, height, units, new StringBuilder());
    }
    
    public SVGGraphics2D(final int width, final int height, final StringBuilder sb) {
        this(width, height, null, sb);
    }
    
    public SVGGraphics2D(final int width, final int height, final SVGUnits units, final StringBuilder sb) {
        this.shapeRendering = "auto";
        this.textRendering = "auto";
        this.fontSizeUnits = SVGUnits.PX;
        this.checkStrokeControlHint = true;
        this.defsKeyPrefix = "";
        this.gradientPaints = new HashMap<>();
        this.linearGradientPaints = new HashMap<>();
        this.radialGradientPaints = new HashMap<>();
        this.clipPaths = new ArrayList<>();
        this.transform = new AffineTransform();
        this.paint = Color.BLACK;
        this.color = Color.BLACK;
        this.composite = AlphaComposite.getInstance(3, 1.0f);
        this.stroke = new BasicStroke(1.0f);
        this.fontRenderContext = new FontRenderContext(null, false, true);
        this.background = Color.BLACK;
        this.gradientPaintRef = null;
        this.width = width;
        this.height = height;
        this.units = units;
        this.shapeRendering = "auto";
        this.textRendering = "auto";
        this.defsKeyPrefix = "_" + System.nanoTime();
        this.clip = null;
        this.imageElements = new ArrayList<>();
        this.filePrefix = "image-";
        this.fileSuffix = ".png";
        this.font = new Font("SansSerif", 0, 12);
        this.fontMapper = new StandardFontMapper();
        this.zeroStrokeWidth = 0.1;
        this.sb = sb;
        this.hints = new RenderingHints(SVGHints.KEY_IMAGE_HANDLING, SVGHints.VALUE_IMAGE_HANDLING_EMBED);
        final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        this.transformFormat = new DecimalFormat("0.######", dfs);
        this.geometryFormat = new DecimalFormat("0.##", dfs);
        this.elementIDs = new HashSet<>();
    }
    
    private SVGGraphics2D(final SVGGraphics2D parent) {
        this(parent.width, parent.height, parent.units, parent.sb);
        this.shapeRendering = parent.shapeRendering;
        this.textRendering = parent.textRendering;
        this.fontMapper = parent.fontMapper;
        this.getRenderingHints().add(parent.hints);
        this.checkStrokeControlHint = parent.checkStrokeControlHint;
        this.setTransformDP(parent.transformDP);
        this.setGeometryDP(parent.geometryDP);
        this.defsKeyPrefix = parent.defsKeyPrefix;
        this.gradientPaints = parent.gradientPaints;
        this.linearGradientPaints = parent.linearGradientPaints;
        this.radialGradientPaints = parent.radialGradientPaints;
        this.clipPaths = parent.clipPaths;
        this.filePrefix = parent.filePrefix;
        this.fileSuffix = parent.fileSuffix;
        this.imageElements = parent.imageElements;
        this.zeroStrokeWidth = parent.zeroStrokeWidth;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public SVGUnits getUnits() {
        return this.units;
    }
    
    public String getShapeRendering() {
        return this.shapeRendering;
    }
    
    public void setShapeRendering(final String value) {
        if (!value.equals("auto") && !value.equals("crispEdges") && !value.equals("geometricPrecision") && !value.equals("optimizeSpeed")) {
            throw new IllegalArgumentException("Unrecognised value: " + value);
        }
        this.shapeRendering = value;
    }
    
    public String getTextRendering() {
        return this.textRendering;
    }
    
    public void setTextRendering(final String value) {
        if (!value.equals("auto") && !value.equals("optimizeSpeed") && !value.equals("optimizeLegibility") && !value.equals("geometricPrecision")) {
            throw new IllegalArgumentException("Unrecognised value: " + value);
        }
        this.textRendering = value;
    }
    
    public boolean getCheckStrokeControlHint() {
        return this.checkStrokeControlHint;
    }
    
    public void setCheckStrokeControlHint(final boolean check) {
        this.checkStrokeControlHint = check;
    }
    
    public String getDefsKeyPrefix() {
        return this.defsKeyPrefix;
    }
    
    public void setDefsKeyPrefix(final String prefix) {
        Args.nullNotPermitted(prefix, "prefix");
        this.defsKeyPrefix = prefix;
    }
    
    public int getTransformDP() {
        return this.transformDP;
    }
    
    public void setTransformDP(final int dp) {
        this.transformDP = dp;
        if (dp < 1 || dp > 10) {
            this.transformFormat = null;
            return;
        }
        final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        this.transformFormat = new DecimalFormat("0." + "##########".substring(0, dp), dfs);
    }
    
    public int getGeometryDP() {
        return this.geometryDP;
    }
    
    public void setGeometryDP(final int dp) {
        this.geometryDP = dp;
        if (dp < 1 || dp > 10) {
            this.geometryFormat = null;
            return;
        }
        final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        this.geometryFormat = new DecimalFormat("0." + "##########".substring(0, dp), dfs);
    }
    
    public String getFilePrefix() {
        return this.filePrefix;
    }
    
    public void setFilePrefix(final String prefix) {
        Args.nullNotPermitted(prefix, "prefix");
        this.filePrefix = prefix;
    }
    
    public String getFileSuffix() {
        return this.fileSuffix;
    }
    
    public void setFileSuffix(final String suffix) {
        Args.nullNotPermitted(suffix, "suffix");
        this.fileSuffix = suffix;
    }
    
    public double getZeroStrokeWidth() {
        return this.zeroStrokeWidth;
    }
    
    public void setZeroStrokeWidth(final double width) {
        if (width < 0.0) {
            throw new IllegalArgumentException("Width cannot be negative.");
        }
        this.zeroStrokeWidth = width;
    }
    
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        if (this.deviceConfiguration == null) {
            this.deviceConfiguration = new SVGGraphicsConfiguration(this.width, this.height);
        }
        return this.deviceConfiguration;
    }
    
    @Override
    public Graphics create() {
        final SVGGraphics2D copy = new SVGGraphics2D(this);
        copy.setRenderingHints(this.getRenderingHints());
        copy.setTransform(this.getTransform());
        copy.setClip(this.getClip());
        copy.setPaint(this.getPaint());
        copy.setColor(this.getColor());
        copy.setComposite(this.getComposite());
        copy.setStroke(this.getStroke());
        copy.setFont(this.getFont());
        copy.setBackground(this.getBackground());
        copy.setFilePrefix(this.getFilePrefix());
        copy.setFileSuffix(this.getFileSuffix());
        return copy;
    }
    
    @Override
    public Paint getPaint() {
        return this.paint;
    }
    
    @Override
    public void setPaint(final Paint paint) {
        if (paint == null) {
            return;
        }
        this.paint = paint;
        this.gradientPaintRef = null;
        if (paint instanceof Color) {
            this.setColor((Color)paint);
        }
        else if (paint instanceof GradientPaint) {
            final GradientPaint gp = (GradientPaint)paint;
            final GradientPaintKey key = new GradientPaintKey(gp);
            final String ref = this.gradientPaints.get(key);
            if (ref == null) {
                final int count = this.gradientPaints.keySet().size();
                final String id = this.defsKeyPrefix + "gp" + count;
                this.elementIDs.add(id);
                this.gradientPaints.put(key, id);
                this.gradientPaintRef = id;
            }
            else {
                this.gradientPaintRef = ref;
            }
        }
        else if (paint instanceof LinearGradientPaint) {
            final LinearGradientPaint lgp = (LinearGradientPaint)paint;
            final LinearGradientPaintKey key2 = new LinearGradientPaintKey(lgp);
            final String ref = this.linearGradientPaints.get(key2);
            if (ref == null) {
                final int count = this.linearGradientPaints.keySet().size();
                final String id = this.defsKeyPrefix + "lgp" + count;
                this.elementIDs.add(id);
                this.linearGradientPaints.put(key2, id);
                this.gradientPaintRef = id;
            }
        }
        else if (paint instanceof RadialGradientPaint) {
            final RadialGradientPaint rgp = (RadialGradientPaint)paint;
            final RadialGradientPaintKey key3 = new RadialGradientPaintKey(rgp);
            final String ref = this.radialGradientPaints.get(key3);
            if (ref == null) {
                final int count = this.radialGradientPaints.keySet().size();
                final String id = this.defsKeyPrefix + "rgp" + count;
                this.elementIDs.add(id);
                this.radialGradientPaints.put(key3, id);
                this.gradientPaintRef = id;
            }
        }
    }
    
    @Override
    public Color getColor() {
        return this.color;
    }
    
    @Override
    public void setColor(final Color c) {
        if (c == null) {
            return;
        }
        this.color = c;
        this.paint = c;
    }
    
    @Override
    public Color getBackground() {
        return this.background;
    }
    
    @Override
    public void setBackground(final Color color) {
        this.background = color;
    }
    
    @Override
    public Composite getComposite() {
        return this.composite;
    }
    
    @Override
    public void setComposite(final Composite comp) {
        if (comp == null) {
            throw new IllegalArgumentException("Null 'comp' argument.");
        }
        this.composite = comp;
    }
    
    @Override
    public Stroke getStroke() {
        return this.stroke;
    }
    
    @Override
    public void setStroke(final Stroke s) {
        if (s == null) {
            throw new IllegalArgumentException("Null 's' argument.");
        }
        this.stroke = s;
    }
    
    @Override
    public Object getRenderingHint(final RenderingHints.Key hintKey) {
        return this.hints.get(hintKey);
    }
    
    @Override
    public void setRenderingHint(final RenderingHints.Key hintKey, final Object hintValue) {
        if (hintKey == null) {
            throw new NullPointerException("Null 'hintKey' not permitted.");
        }
        if (SVGHints.isBeginGroupKey(hintKey)) {
            String groupId = null;
            String ref = null;
            List<Map.Entry> otherKeysAndValues = null;
            if (hintValue instanceof String) {
                groupId = (String)hintValue;
            }
            else if (hintValue instanceof Map) {
                final Map hintValueMap = (Map)hintValue;
                groupId = (String) hintValueMap.get("id");
                ref = (String) hintValueMap.get("ref");
                for (final Object obj : hintValueMap.entrySet()) {
                    final Map.Entry e = (Map.Entry)obj;
                    final Object key = e.getKey();
                    if (!"id".equals(key)) {
                        if ("ref".equals(key)) {
                            continue;
                        }
                        if (otherKeysAndValues == null) {
                            otherKeysAndValues = new ArrayList<>();
                        }
                        otherKeysAndValues.add(e);
                    }
                }
            }
            this.sb.append("<g");
            if (groupId != null) {
                if (this.elementIDs.contains(groupId)) {
                    throw new IllegalArgumentException("The group id (" + groupId + ") is not unique.");
                }
                this.sb.append(" id=\"").append(groupId).append("\"");
                this.elementIDs.add(groupId);
            }
            if (ref != null) {
                this.sb.append(" jfreesvg:ref=\"");
                this.sb.append(SVGUtils.escapeForXML(ref)).append("\"");
            }
            if (otherKeysAndValues != null) {
                for (final Map.Entry e2 : otherKeysAndValues) {
                    this.sb.append(" ").append(e2.getKey()).append("=\"");
                    this.sb.append(SVGUtils.escapeForXML(String.valueOf(e2.getValue()))).append("\"");
                }
            }
            this.sb.append(">");
        }
        else if (SVGHints.isEndGroupKey(hintKey)) {
            this.sb.append("</g>\n");
        }
        else if (SVGHints.isElementTitleKey(hintKey) && hintValue != null) {
            this.sb.append("<title>");
            this.sb.append(SVGUtils.escapeForXML(String.valueOf(hintValue)));
            this.sb.append("</title>");
        }
        else {
            this.hints.put(hintKey, hintValue);
        }
    }
    
    @Override
    public RenderingHints getRenderingHints() {
        return (RenderingHints)this.hints.clone();
    }
    
    @Override
    public void setRenderingHints(final Map<?, ?> hints) {
        this.hints.clear();
        this.addRenderingHints(hints);
    }
    
    @Override
    public void addRenderingHints(final Map<?, ?> hints) {
        this.hints.putAll(hints);
    }
    
    private void appendOptionalElementIDFromHint(final StringBuilder sb) {
        final String elementID = (String)this.hints.get(SVGHints.KEY_ELEMENT_ID);
        if (elementID != null) {
            this.hints.put(SVGHints.KEY_ELEMENT_ID, null);
            if (this.elementIDs.contains(elementID)) {
                throw new IllegalStateException("The element id " + elementID + " is already used.");
            }
            this.elementIDs.add(elementID);
            this.sb.append("id=\"").append(elementID).append("\" ");
        }
    }
    
    @Override
    public void draw(final Shape s) {
        if (!(this.stroke instanceof BasicStroke)) {
            this.fill(this.stroke.createStrokedShape(s));
            return;
        }
        if (s instanceof Line2D) {
            final Line2D l = (Line2D)s;
            this.sb.append("<line ");
            this.appendOptionalElementIDFromHint(this.sb);
            this.sb.append("x1=\"").append(this.geomDP(l.getX1())).append("\" y1=\"").append(this.geomDP(l.getY1())).append("\" x2=\"").append(this.geomDP(l.getX2())).append("\" y2=\"").append(this.geomDP(l.getY2())).append("\" ");
            this.sb.append("style=\"").append(this.strokeStyle()).append("\" ");
            if (!this.transform.isIdentity()) {
                this.sb.append("transform=\"").append(this.getSVGTransform(this.transform)).append("\" ");
            }
            this.sb.append(this.getClipPathRef());
            this.sb.append("/>");
        }
        else if (s instanceof Rectangle2D) {
            final Rectangle2D r = (Rectangle2D)s;
            this.sb.append("<rect ");
            this.appendOptionalElementIDFromHint(this.sb);
            this.sb.append("x=\"").append(this.geomDP(r.getX())).append("\" y=\"").append(this.geomDP(r.getY())).append("\" width=\"").append(this.geomDP(r.getWidth())).append("\" height=\"").append(this.geomDP(r.getHeight())).append("\" ");
            this.sb.append("style=\"").append(this.strokeStyle()).append("; fill: none").append("\" ");
            if (!this.transform.isIdentity()) {
                this.sb.append("transform=\"").append(this.getSVGTransform(this.transform)).append("\" ");
            }
            this.sb.append(this.getClipPathRef());
            this.sb.append("/>");
        }
        else if (s instanceof Ellipse2D) {
            final Ellipse2D e = (Ellipse2D)s;
            this.sb.append("<ellipse ");
            this.appendOptionalElementIDFromHint(this.sb);
            this.sb.append("cx=\"").append(this.geomDP(e.getCenterX())).append("\" cy=\"").append(this.geomDP(e.getCenterY())).append("\" rx=\"").append(this.geomDP(e.getWidth() / 2.0)).append("\" ry=\"").append(this.geomDP(e.getHeight() / 2.0)).append("\" ");
            this.sb.append("style=\"").append(this.strokeStyle()).append("; fill: none").append("\" ");
            if (!this.transform.isIdentity()) {
                this.sb.append("transform=\"").append(this.getSVGTransform(this.transform)).append("\" ");
            }
            this.sb.append(this.getClipPathRef());
            this.sb.append("/>");
        }
        else if (s instanceof Path2D) {
            final Path2D path = (Path2D)s;
            this.sb.append("<g ");
            this.appendOptionalElementIDFromHint(this.sb);
            this.sb.append("style=\"").append(this.strokeStyle()).append("; fill: none").append("\" ");
            if (!this.transform.isIdentity()) {
                this.sb.append("transform=\"").append(this.getSVGTransform(this.transform)).append("\" ");
            }
            this.sb.append(this.getClipPathRef());
            this.sb.append(">");
            this.sb.append("<path ").append(this.getSVGPathData(path)).append("/>");
            this.sb.append("</g>");
        }
        else {
            this.draw(new GeneralPath(s));
        }
    }
    
    @Override
    public void fill(final Shape s) {
        if (s instanceof Rectangle2D) {
            final Rectangle2D r = (Rectangle2D)s;
            if (r.isEmpty()) {
                return;
            }
            this.sb.append("<rect ");
            this.appendOptionalElementIDFromHint(this.sb);
            this.sb.append("x=\"").append(this.geomDP(r.getX())).append("\" y=\"").append(this.geomDP(r.getY())).append("\" width=\"").append(this.geomDP(r.getWidth())).append("\" height=\"").append(this.geomDP(r.getHeight())).append("\" ");
            this.sb.append("style=\"").append(this.getSVGFillStyle()).append("\" ");
            if (!this.transform.isIdentity()) {
                this.sb.append("transform=\"").append(this.getSVGTransform(this.transform)).append("\" ");
            }
            this.sb.append(this.getClipPathRef());
            this.sb.append("/>");
        }
        else if (s instanceof Ellipse2D) {
            final Ellipse2D e = (Ellipse2D)s;
            this.sb.append("<ellipse ");
            this.appendOptionalElementIDFromHint(this.sb);
            this.sb.append("cx=\"").append(this.geomDP(e.getCenterX())).append("\" cy=\"").append(this.geomDP(e.getCenterY())).append("\" rx=\"").append(this.geomDP(e.getWidth() / 2.0)).append("\" ry=\"").append(this.geomDP(e.getHeight() / 2.0)).append("\" ");
            this.sb.append("style=\"").append(this.getSVGFillStyle()).append("\" ");
            if (!this.transform.isIdentity()) {
                this.sb.append("transform=\"").append(this.getSVGTransform(this.transform)).append("\" ");
            }
            this.sb.append(this.getClipPathRef());
            this.sb.append("/>");
        }
        else if (s instanceof Path2D) {
            final Path2D path = (Path2D)s;
            this.sb.append("<g ");
            this.appendOptionalElementIDFromHint(this.sb);
            this.sb.append("style=\"").append(this.getSVGFillStyle());
            this.sb.append("; stroke: none").append("\" ");
            if (!this.transform.isIdentity()) {
                this.sb.append("transform=\"").append(this.getSVGTransform(this.transform)).append("\" ");
            }
            this.sb.append(this.getClipPathRef());
            this.sb.append(">");
            this.sb.append("<path ").append(this.getSVGPathData(path)).append("/>");
            this.sb.append("</g>");
        }
        else {
            this.fill(new GeneralPath(s));
        }
    }
    
    private String getSVGPathData(final Path2D path) {
        final StringBuilder b = new StringBuilder("d=\"");
        final float[] coords = new float[6];
        boolean first = true;
        final PathIterator iterator = path.getPathIterator(null);
        while (!iterator.isDone()) {
            final int type = iterator.currentSegment(coords);
            if (!first) {
                b.append(" ");
            }
            first = false;
            switch (type) {
                case 0: {
                    b.append("M ").append(this.geomDP(coords[0])).append(" ").append(this.geomDP(coords[1]));
                    break;
                }
                case 1: {
                    b.append("L ").append(this.geomDP(coords[0])).append(" ").append(this.geomDP(coords[1]));
                    break;
                }
                case 2: {
                    b.append("Q ").append(this.geomDP(coords[0])).append(" ").append(this.geomDP(coords[1])).append(" ").append(this.geomDP(coords[2])).append(" ").append(this.geomDP(coords[3]));
                    break;
                }
                case 3: {
                    b.append("C ").append(this.geomDP(coords[0])).append(" ").append(this.geomDP(coords[1])).append(" ").append(this.geomDP(coords[2])).append(" ").append(this.geomDP(coords[3])).append(" ").append(this.geomDP(coords[4])).append(" ").append(this.geomDP(coords[5]));
                    break;
                }
                case 4: {
                    b.append("Z ");
                    break;
                }
            }
            iterator.next();
        }
        return b.append("\"").toString();
    }
    
    private float getAlpha() {
        float alpha = 1.0f;
        if (this.composite instanceof AlphaComposite) {
            final AlphaComposite ac = (AlphaComposite)this.composite;
            alpha = ac.getAlpha();
        }
        return alpha;
    }
    
    private String svgColorStr() {
        final String result = "black;";
        if (this.paint instanceof Color) {
            return this.rgbColorStr((Color)this.paint);
        }
        if (this.paint instanceof GradientPaint || this.paint instanceof LinearGradientPaint || this.paint instanceof RadialGradientPaint) {
            return "url(#" + this.gradientPaintRef + ")";
        }
        return result;
    }
    
    private String rgbColorStr(final Color c) {
        final StringBuilder b = new StringBuilder("rgb(");
        b.append(c.getRed()).append(",").append(c.getGreen()).append(",").append(c.getBlue()).append(")");
        return b.toString();
    }
    
    private String rgbaColorStr(final Color c) {
        final StringBuilder b = new StringBuilder("rgba(");
        final double alphaPercent = c.getAlpha() / 255.0;
        b.append(c.getRed()).append(",").append(c.getGreen()).append(",").append(c.getBlue());
        b.append(",").append(this.transformDP(alphaPercent));
        b.append(")");
        return b.toString();
    }
    
    private String strokeStyle() {
        double strokeWidth = 1.0;
        String strokeCap = "butt";
        String strokeJoin = "miter";
        float miterLimit = 4.0f;
        float[] dashArray = new float[0];
        if (this.stroke instanceof BasicStroke) {
            final BasicStroke bs = (BasicStroke)this.stroke;
            strokeWidth = ((bs.getLineWidth() > 0.0) ? bs.getLineWidth() : this.zeroStrokeWidth);
            switch (bs.getEndCap()) {
                case 1: {
                    strokeCap = "round";
                    break;
                }
                case 2: {
                    strokeCap = "square";
                    break;
                }
            }
            switch (bs.getLineJoin()) {
                case 2: {
                    strokeJoin = "bevel";
                    break;
                }
                case 1: {
                    strokeJoin = "round";
                    break;
                }
            }
            miterLimit = bs.getMiterLimit();
            dashArray = bs.getDashArray();
        }
        final StringBuilder b = new StringBuilder();
        b.append("stroke-width: ").append(strokeWidth).append(";");
        b.append("stroke: ").append(this.svgColorStr()).append(";");
        b.append("stroke-opacity: ").append(this.getColorAlpha() * this.getAlpha()).append(";");
        if (!strokeCap.equals("butt")) {
            b.append("stroke-linecap: ").append(strokeCap).append(";");
        }
        if (!strokeJoin.equals("miter")) {
            b.append("stroke-linejoin: ").append(strokeJoin).append(";");
        }
        if (Math.abs(4.0f - miterLimit) < 0.001) {
            b.append("stroke-miterlimit: ").append(this.geomDP(miterLimit));
        }
        if (dashArray != null && dashArray.length != 0) {
            b.append("stroke-dasharray: ");
            for (int i = 0; i < dashArray.length; ++i) {
                if (i != 0) {
                    b.append(", ");
                }
                b.append(dashArray[i]);
            }
            b.append(";");
        }
        if (this.checkStrokeControlHint) {
            final Object hint = this.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            if (RenderingHints.VALUE_STROKE_NORMALIZE.equals(hint) && !this.shapeRendering.equals("crispEdges")) {
                b.append("shape-rendering:crispEdges;");
            }
            if (RenderingHints.VALUE_STROKE_PURE.equals(hint) && !this.shapeRendering.equals("geometricPrecision")) {
                b.append("shape-rendering:geometricPrecision;");
            }
        }
        return b.toString();
    }
    
    private float getColorAlpha() {
        if (this.paint instanceof Color) {
            final Color c = (Color)this.paint;
            return c.getAlpha() / 255.0f;
        }
        return 1.0f;
    }
    
    private String getSVGFillStyle() {
        final StringBuilder b = new StringBuilder();
        b.append("fill: ").append(this.svgColorStr()).append("; ");
        b.append("fill-opacity: ").append(this.getColorAlpha() * this.getAlpha());
        return b.toString();
    }
    
    @Override
    public Font getFont() {
        return this.font;
    }
    
    @Override
    public void setFont(final Font font) {
        if (font == null) {
            return;
        }
        this.font = font;
    }
    
    public FontMapper getFontMapper() {
        return this.fontMapper;
    }
    
    public void setFontMapper(final FontMapper mapper) {
        Args.nullNotPermitted(mapper, "mapper");
        this.fontMapper = mapper;
    }
    
    public SVGUnits getFontSizeUnits() {
        return this.fontSizeUnits;
    }
    
    public void setFontSizeUnits(final SVGUnits fontSizeUnits) {
        Args.nullNotPermitted(fontSizeUnits, "fontSizeUnits");
        this.fontSizeUnits = fontSizeUnits;
    }
    
    private String getSVGFontStyle() {
        final StringBuilder b = new StringBuilder();
        b.append("fill: ").append(this.svgColorStr()).append("; ");
        b.append("fill-opacity: ").append(this.getColorAlpha() * this.getAlpha()).append("; ");
        final String fontFamily = this.fontMapper.mapFont(this.font.getFamily());
        b.append("font-family: ").append(fontFamily).append("; ");
        b.append("font-size: ").append(this.font.getSize()).append(this.fontSizeUnits).append(";");
        if (this.font.isBold()) {
            b.append(" font-weight: bold;");
        }
        if (this.font.isItalic()) {
            b.append(" font-style: italic;");
        }
        return b.toString();
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font f) {
        if (this.fmImage == null) {
            this.fmImage = new BufferedImage(10, 10, 1);
            (this.fmImageG2D = this.fmImage.createGraphics()).setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        }
        return this.fmImageG2D.getFontMetrics(f);
    }
    
    @Override
    public FontRenderContext getFontRenderContext() {
        return this.fontRenderContext;
    }
    
    @Override
    public void drawString(final String str, final int x, final int y) {
        this.drawString(str, (float)x, (float)y);
    }
    
    @Override
    public void drawString(final String str, final float x, final float y) {
        if (str == null) {
            throw new NullPointerException("Null 'str' argument.");
        }
        if (str.isEmpty()) {
            return;
        }
        if (!SVGHints.VALUE_DRAW_STRING_TYPE_VECTOR.equals(this.hints.get(SVGHints.KEY_DRAW_STRING_TYPE))) {
            this.sb.append("<g ");
            this.appendOptionalElementIDFromHint(this.sb);
            if (!this.transform.isIdentity()) {
                this.sb.append("transform=\"").append(this.getSVGTransform(this.transform)).append("\"");
            }
            this.sb.append(">");
            this.sb.append("<text x=\"").append(this.geomDP(x)).append("\" y=\"").append(this.geomDP(y)).append("\"");
            this.sb.append(" style=\"").append(this.getSVGFontStyle()).append("\"");
            final Object hintValue = this.getRenderingHint(SVGHints.KEY_TEXT_RENDERING);
            if (hintValue != null) {
                final String textRenderValue = hintValue.toString();
                this.sb.append(" text-rendering=\"").append(textRenderValue).append("\"");
            }
            this.sb.append(" ").append(this.getClipPathRef());
            this.sb.append(">");
            this.sb.append(SVGUtils.escapeForXML(str)).append("</text>");
            this.sb.append("</g>");
        }
        else {
            final AttributedString as = new AttributedString(str, this.font.getAttributes());
            this.drawString(as.getIterator(), x, y);
        }
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator iterator, final int x, final int y) {
        this.drawString(iterator, (float)x, (float)y);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator iterator, final float x, final float y) {
        final Set<AttributedCharacterIterator.Attribute> s = iterator.getAllAttributeKeys();
        if (!s.isEmpty()) {
            final TextLayout layout = new TextLayout(iterator, this.getFontRenderContext());
            layout.draw(this, x, y);
        }
        else {
            final StringBuilder strb = new StringBuilder();
            iterator.first();
            for (int i = iterator.getBeginIndex(); i < iterator.getEndIndex(); ++i) {
                strb.append(iterator.current());
                iterator.next();
            }
            this.drawString(strb.toString(), x, y);
        }
    }
    
    @Override
    public void drawGlyphVector(final GlyphVector g, final float x, final float y) {
        this.fill(g.getOutline(x, y));
    }
    
    @Override
    public void translate(final int tx, final int ty) {
        this.translate(tx, (double)ty);
    }
    
    @Override
    public void translate(final double tx, final double ty) {
        final AffineTransform t = this.getTransform();
        t.translate(tx, ty);
        this.setTransform(t);
    }
    
    @Override
    public void rotate(final double theta) {
        final AffineTransform t = this.getTransform();
        t.rotate(theta);
        this.setTransform(t);
    }
    
    @Override
    public void rotate(final double theta, final double x, final double y) {
        this.translate(x, y);
        this.rotate(theta);
        this.translate(-x, -y);
    }
    
    @Override
    public void scale(final double sx, final double sy) {
        final AffineTransform t = this.getTransform();
        t.scale(sx, sy);
        this.setTransform(t);
    }
    
    @Override
    public void shear(final double shx, final double shy) {
        this.transform(AffineTransform.getShearInstance(shx, shy));
    }
    
    @Override
    public void transform(final AffineTransform t) {
        final AffineTransform tx = this.getTransform();
        tx.concatenate(t);
        this.setTransform(tx);
    }
    
    @Override
    public AffineTransform getTransform() {
        return (AffineTransform)this.transform.clone();
    }
    
    @Override
    public void setTransform(final AffineTransform t) {
        if (t == null) {
            this.transform = new AffineTransform();
        }
        else {
            this.transform = new AffineTransform(t);
        }
        this.clipRef = null;
    }
    
    @Override
    public boolean hit(final Rectangle rect, final Shape s, final boolean onStroke) {
        Shape ts;
        if (onStroke) {
            ts = this.transform.createTransformedShape(this.stroke.createStrokedShape(s));
        }
        else {
            ts = this.transform.createTransformedShape(s);
        }
        if (!rect.getBounds2D().intersects(ts.getBounds2D())) {
            return false;
        }
        final Area a1 = new Area(rect);
        final Area a2 = new Area(ts);
        a1.intersect(a2);
        return !a1.isEmpty();
    }
    
    @Override
    public void setPaintMode() {
    }
    
    @Override
    public void setXORMode(final Color c) {
    }
    
    @Override
    public Rectangle getClipBounds() {
        if (this.clip == null) {
            return null;
        }
        return this.getClip().getBounds();
    }
    
    @Override
    public Shape getClip() {
        if (this.clip == null) {
            return null;
        }
        try {
            final AffineTransform inv = this.transform.createInverse();
            return inv.createTransformedShape(this.clip);
        }
        catch (NoninvertibleTransformException ex) {
            return null;
        }
    }
    
    @Override
    public void setClip(final Shape shape) {
        this.clip = this.transform.createTransformedShape(shape);
        this.clipRef = null;
    }
    
    private String registerClip(final Shape clip) {
        if (clip == null) {
            return this.clipRef = null;
        }
        final String pathStr = this.getSVGPathData(new Path2D.Double(clip));
        int index = this.clipPaths.indexOf(pathStr);
        if (index < 0) {
            this.clipPaths.add(pathStr);
            index = this.clipPaths.size() - 1;
        }
        return this.defsKeyPrefix + "clip-" + index;
    }
    
    private String transformDP(final double d) {
        if (this.transformFormat != null) {
            return this.transformFormat.format(d);
        }
        return String.valueOf(d);
    }
    
    private String geomDP(final double d) {
        if (this.geometryFormat != null) {
            return this.geometryFormat.format(d);
        }
        return String.valueOf(d);
    }
    
    private String getSVGTransform(final AffineTransform t) {
        final StringBuilder b = new StringBuilder("matrix(");
        b.append(this.transformDP(t.getScaleX())).append(",");
        b.append(this.transformDP(t.getShearY())).append(",");
        b.append(this.transformDP(t.getShearX())).append(",");
        b.append(this.transformDP(t.getScaleY())).append(",");
        b.append(this.transformDP(t.getTranslateX())).append(",");
        b.append(this.transformDP(t.getTranslateY())).append(")");
        return b.toString();
    }
    
    @Override
    public void clip(Shape s) {
        if (s instanceof Line2D) {
            s = s.getBounds2D();
        }
        if (this.clip == null) {
            this.setClip(s);
            return;
        }
        final Shape ts = this.transform.createTransformedShape(s);
        if (!ts.intersects(this.clip.getBounds2D())) {
            this.setClip(new Rectangle2D.Double());
        }
        else {
            final Area a1 = new Area(ts);
            final Area a2 = new Area(this.clip);
            a1.intersect(a2);
            this.clip = new Path2D.Double(a1);
        }
        this.clipRef = null;
    }
    
    @Override
    public void clipRect(final int x, final int y, final int width, final int height) {
        this.setRect(x, y, width, height);
        this.clip(this.rect);
    }
    
    @Override
    public void setClip(final int x, final int y, final int width, final int height) {
        this.setRect(x, y, width, height);
        this.setClip(this.rect);
    }
    
    @Override
    public void drawLine(final int x1, final int y1, final int x2, final int y2) {
        if (this.line == null) {
            this.line = new Line2D.Double(x1, y1, x2, y2);
        }
        else {
            this.line.setLine(x1, y1, x2, y2);
        }
        this.draw(this.line);
    }
    
    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
        this.setRect(x, y, width, height);
        this.fill(this.rect);
    }
    
    @Override
    public void clearRect(final int x, final int y, final int width, final int height) {
        if (this.getBackground() == null) {
            return;
        }
        final Paint saved = this.getPaint();
        this.setPaint(this.getBackground());
        this.fillRect(x, y, width, height);
        this.setPaint(saved);
    }
    
    @Override
    public void drawRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        this.setRoundRect(x, y, width, height, arcWidth, arcHeight);
        this.draw(this.roundRect);
    }
    
    @Override
    public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        this.setRoundRect(x, y, width, height, arcWidth, arcHeight);
        this.fill(this.roundRect);
    }
    
    @Override
    public void drawOval(final int x, final int y, final int width, final int height) {
        this.setOval(x, y, width, height);
        this.draw(this.oval);
    }
    
    @Override
    public void fillOval(final int x, final int y, final int width, final int height) {
        this.setOval(x, y, width, height);
        this.fill(this.oval);
    }
    
    @Override
    public void drawArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        this.setArc(x, y, width, height, startAngle, arcAngle);
        this.draw(this.arc);
    }
    
    @Override
    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        this.setArc(x, y, width, height, startAngle, arcAngle);
        this.fill(this.arc);
    }
    
    @Override
    public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final GeneralPath p = GraphicsUtils.createPolygon(xPoints, yPoints, nPoints, false);
        this.draw(p);
    }
    
    @Override
    public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final GeneralPath p = GraphicsUtils.createPolygon(xPoints, yPoints, nPoints, true);
        this.draw(p);
    }
    
    @Override
    public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final GeneralPath p = GraphicsUtils.createPolygon(xPoints, yPoints, nPoints, true);
        this.fill(p);
    }
    
    private byte[] getPNGBytes(final Image img) {
        RenderedImage ri;
        if (img instanceof RenderedImage) {
            ri = (RenderedImage)img;
        }
        else {
            final BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), 2);
            final Graphics2D g2 = bi.createGraphics();
            g2.drawImage(img, 0, 0, null);
            ri = bi;
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(ri, "png", baos);
        }
        catch (IOException ex) {
            Logger.getLogger(SVGGraphics2D.class.getName()).log(Level.SEVERE, "IOException while writing PNG data.", ex);
        }
        return baos.toByteArray();
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final ImageObserver observer) {
        if (img == null) {
            return true;
        }
        final int w = img.getWidth(observer);
        if (w < 0) {
            return false;
        }
        final int h = img.getHeight(observer);
        return h >= 0 && this.drawImage(img, x, y, w, h, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int w, final int h, final ImageObserver observer) {
        if (img == null) {
            return true;
        }
        final Object hint = this.getRenderingHint(SVGHints.KEY_IMAGE_HANDLING);
        if (SVGHints.VALUE_IMAGE_HANDLING_EMBED.equals(hint)) {
            this.sb.append("<image ");
            this.appendOptionalElementIDFromHint(this.sb);
            this.sb.append("preserveAspectRatio=\"none\" ");
            this.sb.append("xlink:href=\"data:image/png;base64,");
            this.sb.append(DatatypeConverter.printBase64Binary(this.getPNGBytes(img)));
            this.sb.append("\" ");
            this.sb.append(this.getClipPathRef()).append(" ");
            if (!this.transform.isIdentity()) {
                this.sb.append("transform=\"").append(this.getSVGTransform(this.transform)).append("\" ");
            }
            this.sb.append("x=\"").append(this.geomDP(x)).append("\" y=\"").append(this.geomDP(y)).append("\" ");
            this.sb.append("width=\"").append(this.geomDP(w)).append("\" height=\"").append(this.geomDP(h)).append("\"/>\n");
            return true;
        }
        final int count = this.imageElements.size();
        String href = (String)this.hints.get(SVGHints.KEY_IMAGE_HREF);
        if (href == null) {
            href = this.filePrefix + count + this.fileSuffix;
        }
        else {
            this.hints.put(SVGHints.KEY_IMAGE_HREF, null);
        }
        final ImageElement imageElement = new ImageElement(href, img);
        this.imageElements.add(imageElement);
        this.sb.append("<image ");
        this.appendOptionalElementIDFromHint(this.sb);
        this.sb.append("xlink:href=\"");
        this.sb.append(href).append("\" ");
        this.sb.append(this.getClipPathRef()).append(" ");
        if (!this.transform.isIdentity()) {
            this.sb.append("transform=\"").append(this.getSVGTransform(this.transform)).append("\" ");
        }
        this.sb.append("x=\"").append(this.geomDP(x)).append("\" y=\"").append(this.geomDP(y)).append("\" ");
        this.sb.append("width=\"").append(this.geomDP(w)).append("\" height=\"").append(this.geomDP(h)).append("\"/>\n");
        return true;
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final Color bgcolor, final ImageObserver observer) {
        if (img == null) {
            return true;
        }
        final int w = img.getWidth(null);
        if (w < 0) {
            return false;
        }
        final int h = img.getHeight(null);
        return h >= 0 && this.drawImage(img, x, y, w, h, bgcolor, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int w, final int h, final Color bgcolor, final ImageObserver observer) {
        final Paint saved = this.getPaint();
        this.setPaint(bgcolor);
        this.fillRect(x, y, w, h);
        this.setPaint(saved);
        return this.drawImage(img, x, y, w, h, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer) {
        final int w = dx2 - dx1;
        final int h = dy2 - dy1;
        final BufferedImage img2 = new BufferedImage(w, h, 2);
        final Graphics2D g2 = img2.createGraphics();
        g2.drawImage(img, 0, 0, w, h, sx1, sy1, sx2, sy2, null);
        return this.drawImage(img2, dx1, dy1, null);
    }
    
    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final Color bgcolor, final ImageObserver observer) {
        final Paint saved = this.getPaint();
        this.setPaint(bgcolor);
        this.fillRect(dx1, dy1, dx2 - dx1, dy2 - dy1);
        this.setPaint(saved);
        return this.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }
    
    @Override
    public void drawRenderedImage(final RenderedImage img, final AffineTransform xform) {
        final BufferedImage bi = GraphicsUtils.convertRenderedImage(img);
        this.drawImage(bi, xform, null);
    }
    
    @Override
    public void drawRenderableImage(final RenderableImage img, final AffineTransform xform) {
        final RenderedImage ri = img.createDefaultRendering();
        this.drawRenderedImage(ri, xform);
    }
    
    @Override
    public boolean drawImage(final Image img, final AffineTransform xform, final ImageObserver obs) {
        final AffineTransform savedTransform = this.getTransform();
        if (xform != null) {
            this.transform(xform);
        }
        final boolean result = this.drawImage(img, 0, 0, obs);
        if (xform != null) {
            this.setTransform(savedTransform);
        }
        return result;
    }
    
    @Override
    public void drawImage(final BufferedImage img, final BufferedImageOp op, final int x, final int y) {
        BufferedImage imageToDraw = img;
        if (op != null) {
            imageToDraw = op.filter(img, null);
        }
        this.drawImage(imageToDraw, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, (float)x, (float)y), null);
    }
    
    @Override
    public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
    }
    
    @Override
    public void dispose() {
    }
    
    public String getSVGElement() {
        return this.getSVGElement(null);
    }
    
    public String getSVGElement(final String id) {
        return this.getSVGElement(id, true, null, null, null);
    }
    
    public String getSVGElement(final String id, final boolean includeDimensions, final ViewBox viewBox, final PreserveAspectRatio preserveAspectRatio, final MeetOrSlice meetOrSlice) {
        final StringBuilder svg = new StringBuilder("<svg ");
        if (id != null) {
            svg.append("id=\"").append(id).append("\" ");
        }
        final String unitStr = (this.units != null) ? this.units.toString() : "";
        svg.append("xmlns=\"http://www.w3.org/2000/svg\" ").append("xmlns:xlink=\"http://www.w3.org/1999/xlink\" ").append("xmlns:jfreesvg=\"http://www.jfree.org/jfreesvg/svg\" ");
        if (includeDimensions) {
            svg.append("width=\"").append(this.width).append(unitStr).append("\" height=\"").append(this.height).append(unitStr).append("\" ");
        }
        if (viewBox != null) {
            svg.append("viewBox=\"").append(viewBox.valueStr()).append("\" ");
            if (preserveAspectRatio != null) {
                svg.append("preserveAspectRatio=\"").append(preserveAspectRatio.toString());
                if (meetOrSlice != null) {
                    svg.append(" ").append(meetOrSlice.toString());
                }
                svg.append("\" ");
            }
        }
        svg.append("text-rendering=\"").append(this.textRendering).append("\" shape-rendering=\"").append(this.shapeRendering).append("\">\n");
        final StringBuilder defs = new StringBuilder("<defs>");
        for (final GradientPaintKey key : this.gradientPaints.keySet()) {
            defs.append(this.getLinearGradientElement(this.gradientPaints.get(key), key.getPaint()));
            defs.append("\n");
        }
        for (final LinearGradientPaintKey key2 : this.linearGradientPaints.keySet()) {
            defs.append(this.getLinearGradientElement(this.linearGradientPaints.get(key2), key2.getPaint()));
            defs.append("\n");
        }
        for (final RadialGradientPaintKey key3 : this.radialGradientPaints.keySet()) {
            defs.append(this.getRadialGradientElement(this.radialGradientPaints.get(key3), key3.getPaint()));
            defs.append("\n");
        }
        for (int i = 0; i < this.clipPaths.size(); ++i) {
            final StringBuilder b = new StringBuilder("<clipPath id=\"").append(this.defsKeyPrefix).append("clip-").append(i).append("\">");
            b.append("<path ").append(this.clipPaths.get(i)).append("/>");
            b.append("</clipPath>").append("\n");
            defs.append(b.toString());
        }
        defs.append("</defs>\n");
        svg.append(defs);
        svg.append(this.sb);
        svg.append("</svg>");
        return svg.toString();
    }
    
    public String getSVGDocument() {
        final StringBuilder b = new StringBuilder();
        b.append("<?xml version=\"1.0\"?>\n");
        b.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" ");
        b.append("\"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">\n");
        b.append(this.getSVGElement());
        return b.append("\n").toString();
    }
    
    public List<ImageElement> getSVGImages() {
        return this.imageElements;
    }
    
    public Set<String> getElementIDs() {
        return new HashSet<>(this.elementIDs);
    }
    
    private String getLinearGradientElement(final String id, final GradientPaint paint) {
        final StringBuilder b = new StringBuilder("<linearGradient id=\"").append(id).append("\" ");
        final Point2D p1 = paint.getPoint1();
        final Point2D p2 = paint.getPoint2();
        b.append("x1=\"").append(this.geomDP(p1.getX())).append("\" ");
        b.append("y1=\"").append(this.geomDP(p1.getY())).append("\" ");
        b.append("x2=\"").append(this.geomDP(p2.getX())).append("\" ");
        b.append("y2=\"").append(this.geomDP(p2.getY())).append("\" ");
        b.append("gradientUnits=\"userSpaceOnUse\">");
        final Color c1 = paint.getColor1();
        b.append("<stop offset=\"0%\" stop-color=\"").append(this.rgbColorStr(c1)).append("\"");
        if (c1.getAlpha() < 255) {
            final double alphaPercent = c1.getAlpha() / 255.0;
            b.append(" stop-opacity=\"").append(this.transformDP(alphaPercent)).append("\"");
        }
        b.append("/>");
        final Color c2 = paint.getColor2();
        b.append("<stop offset=\"100%\" stop-color=\"").append(this.rgbColorStr(c2)).append("\"");
        if (c2.getAlpha() < 255) {
            final double alphaPercent2 = c2.getAlpha() / 255.0;
            b.append(" stop-opacity=\"").append(this.transformDP(alphaPercent2)).append("\"");
        }
        b.append("/>");
        return b.append("</linearGradient>").toString();
    }
    
    private String getLinearGradientElement(final String id, final LinearGradientPaint paint) {
        final StringBuilder b = new StringBuilder("<linearGradient id=\"").append(id).append("\" ");
        final Point2D p1 = paint.getStartPoint();
        final Point2D p2 = paint.getEndPoint();
        b.append("x1=\"").append(this.geomDP(p1.getX())).append("\" ");
        b.append("y1=\"").append(this.geomDP(p1.getY())).append("\" ");
        b.append("x2=\"").append(this.geomDP(p2.getX())).append("\" ");
        b.append("y2=\"").append(this.geomDP(p2.getY())).append("\" ");
        if (!paint.getCycleMethod().equals(MultipleGradientPaint.CycleMethod.NO_CYCLE)) {
            final String sm = paint.getCycleMethod().equals(MultipleGradientPaint.CycleMethod.REFLECT) ? "reflect" : "repeat";
            b.append("spreadMethod=\"").append(sm).append("\" ");
        }
        b.append("gradientUnits=\"userSpaceOnUse\">");
        for (int i = 0; i < paint.getFractions().length; ++i) {
            final Color c = paint.getColors()[i];
            final float fraction = paint.getFractions()[i];
            b.append("<stop offset=\"").append(this.geomDP(fraction * 100.0f)).append("%\" stop-color=\"").append(this.rgbColorStr(c)).append("\"");
            if (c.getAlpha() < 255) {
                final double alphaPercent = c.getAlpha() / 255.0;
                b.append(" stop-opacity=\"").append(this.transformDP(alphaPercent)).append("\"");
            }
            b.append("/>");
        }
        return b.append("</linearGradient>").toString();
    }
    
    private String getRadialGradientElement(final String id, final RadialGradientPaint rgp) {
        final StringBuilder b = new StringBuilder("<radialGradient id=\"").append(id).append("\" gradientUnits=\"userSpaceOnUse\" ");
        final Point2D center = rgp.getCenterPoint();
        final Point2D focus = rgp.getFocusPoint();
        final float radius = rgp.getRadius();
        b.append("cx=\"").append(this.geomDP(center.getX())).append("\" ");
        b.append("cy=\"").append(this.geomDP(center.getY())).append("\" ");
        b.append("r=\"").append(this.geomDP(radius)).append("\" ");
        b.append("fx=\"").append(this.geomDP(focus.getX())).append("\" ");
        b.append("fy=\"").append(this.geomDP(focus.getY())).append("\">");
        final Color[] colors = rgp.getColors();
        final float[] fractions = rgp.getFractions();
        for (int i = 0; i < colors.length; ++i) {
            final Color c = colors[i];
            final float f = fractions[i];
            b.append("<stop offset=\"").append(this.geomDP(f * 100.0f)).append("%\" ");
            b.append("stop-color=\"").append(this.rgbColorStr(c)).append("\"");
            if (c.getAlpha() < 255) {
                final double alphaPercent = c.getAlpha() / 255.0;
                b.append(" stop-opacity=\"").append(this.transformDP(alphaPercent)).append("\"");
            }
            b.append("/>");
        }
        return b.append("</radialGradient>").toString();
    }
    
    private String getClipPathRef() {
        if (this.clip == null) {
            return "";
        }
        if (this.clipRef == null) {
            this.clipRef = this.registerClip(this.getClip());
        }
        final StringBuilder b = new StringBuilder();
        b.append("clip-path=\"url(#").append(this.clipRef).append(")\"");
        return b.toString();
    }
    
    private void setRect(final int x, final int y, final int width, final int height) {
        if (this.rect == null) {
            this.rect = new Rectangle2D.Double(x, y, width, height);
        }
        else {
            this.rect.setRect(x, y, width, height);
        }
    }
    
    private void setRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        if (this.roundRect == null) {
            this.roundRect = new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight);
        }
        else {
            this.roundRect.setRoundRect(x, y, width, height, arcWidth, arcHeight);
        }
    }
    
    private void setArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        if (this.arc == null) {
            this.arc = new Arc2D.Double(x, y, width, height, startAngle, arcAngle, 0);
        }
        else {
            this.arc.setArc(x, y, width, height, startAngle, arcAngle, 0);
        }
    }
    
    private void setOval(final int x, final int y, final int width, final int height) {
        if (this.oval == null) {
            this.oval = new Ellipse2D.Double(x, y, width, height);
        }
        else {
            this.oval.setFrame(x, y, width, height);
        }
    }
}
