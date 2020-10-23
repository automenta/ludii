// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.HashSet;
import org.apache.batik.gvt.text.ArabicTextHandler;
import java.awt.font.TextAttribute;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.BasicStroke;
import java.util.Map;
import org.apache.batik.gvt.font.AWTGVTFont;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import java.awt.Shape;
import java.awt.Graphics2D;
import org.apache.batik.gvt.font.GVTGlyphMetrics;
import org.apache.batik.gvt.font.AltGlyphHandler;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import java.text.CharacterIterator;
import java.awt.font.FontRenderContext;
import java.util.Set;
import org.apache.batik.gvt.text.TextPath;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import org.apache.batik.gvt.font.GVTLineMetrics;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTGlyphVector;

public class GlyphLayout implements TextSpanLayout
{
    protected GVTGlyphVector gv;
    private GVTFont font;
    private GVTLineMetrics metrics;
    private AttributedCharacterIterator aci;
    protected Point2D advance;
    private Point2D offset;
    private float xScale;
    private float yScale;
    private TextPath textPath;
    private Point2D textPathAdvance;
    private int[] charMap;
    private boolean vertical;
    private boolean adjSpacing;
    private float[] glyphAdvances;
    private boolean isAltGlyph;
    protected boolean layoutApplied;
    private boolean spacingApplied;
    private boolean pathApplied;
    public static final AttributedCharacterIterator.Attribute FLOW_LINE_BREAK;
    public static final AttributedCharacterIterator.Attribute FLOW_PARAGRAPH;
    public static final AttributedCharacterIterator.Attribute FLOW_EMPTY_PARAGRAPH;
    public static final AttributedCharacterIterator.Attribute LINE_HEIGHT;
    public static final AttributedCharacterIterator.Attribute VERTICAL_ORIENTATION;
    public static final AttributedCharacterIterator.Attribute VERTICAL_ORIENTATION_ANGLE;
    public static final AttributedCharacterIterator.Attribute HORIZONTAL_ORIENTATION_ANGLE;
    private static final AttributedCharacterIterator.Attribute X;
    private static final AttributedCharacterIterator.Attribute Y;
    private static final AttributedCharacterIterator.Attribute DX;
    private static final AttributedCharacterIterator.Attribute DY;
    private static final AttributedCharacterIterator.Attribute ROTATION;
    private static final AttributedCharacterIterator.Attribute BASELINE_SHIFT;
    private static final AttributedCharacterIterator.Attribute WRITING_MODE;
    private static final Integer WRITING_MODE_TTB;
    private static final Integer ORIENTATION_AUTO;
    public static final AttributedCharacterIterator.Attribute GVT_FONT;
    protected static Set runAtts;
    protected static Set szAtts;
    public static final double eps = 1.0E-5;
    
    public GlyphLayout(final AttributedCharacterIterator aci, final int[] charMap, final Point2D offset, final FontRenderContext frc) {
        this.xScale = 1.0f;
        this.yScale = 1.0f;
        this.adjSpacing = true;
        this.layoutApplied = false;
        this.spacingApplied = false;
        this.pathApplied = false;
        this.aci = aci;
        this.offset = offset;
        this.font = this.getFont();
        this.charMap = charMap;
        this.metrics = this.font.getLineMetrics(aci, aci.getBeginIndex(), aci.getEndIndex(), frc);
        this.gv = null;
        this.aci.first();
        this.vertical = (aci.getAttribute(GlyphLayout.WRITING_MODE) == GlyphLayout.WRITING_MODE_TTB);
        this.textPath = (TextPath)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.TEXTPATH);
        final AltGlyphHandler altGlyphHandler = (AltGlyphHandler)this.aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.ALT_GLYPH_HANDLER);
        if (altGlyphHandler != null) {
            this.gv = altGlyphHandler.createGlyphVector(frc, this.font.getSize(), this.aci);
            if (this.gv != null) {
                this.isAltGlyph = true;
            }
        }
        if (this.gv == null) {
            this.gv = this.font.createGlyphVector(frc, this.aci);
        }
    }
    
    @Override
    public GVTGlyphVector getGlyphVector() {
        return this.gv;
    }
    
    @Override
    public Point2D getOffset() {
        return this.offset;
    }
    
    @Override
    public void setScale(float xScale, float yScale, final boolean adjSpacing) {
        if (this.vertical) {
            xScale = 1.0f;
        }
        else {
            yScale = 1.0f;
        }
        if (xScale != this.xScale || yScale != this.yScale || adjSpacing != this.adjSpacing) {
            this.xScale = xScale;
            this.yScale = yScale;
            this.adjSpacing = adjSpacing;
            this.spacingApplied = false;
            this.glyphAdvances = null;
            this.pathApplied = false;
        }
    }
    
    @Override
    public void setOffset(final Point2D offset) {
        if (offset.getX() != this.offset.getX() || offset.getY() != this.offset.getY()) {
            if (this.layoutApplied || this.spacingApplied) {
                final float dx = (float)(offset.getX() - this.offset.getX());
                final float dy = (float)(offset.getY() - this.offset.getY());
                final int numGlyphs = this.gv.getNumGlyphs();
                final float[] gp = this.gv.getGlyphPositions(0, numGlyphs + 1, null);
                final Point2D.Float pos = new Point2D.Float();
                for (int i = 0; i <= numGlyphs; ++i) {
                    pos.x = gp[2 * i] + dx;
                    pos.y = gp[2 * i + 1] + dy;
                    this.gv.setGlyphPosition(i, pos);
                }
            }
            this.offset = offset;
            this.pathApplied = false;
        }
    }
    
    @Override
    public GVTGlyphMetrics getGlyphMetrics(final int glyphIndex) {
        return this.gv.getGlyphMetrics(glyphIndex);
    }
    
    @Override
    public GVTLineMetrics getLineMetrics() {
        return this.metrics;
    }
    
    @Override
    public boolean isVertical() {
        return this.vertical;
    }
    
    @Override
    public boolean isOnATextPath() {
        return this.textPath != null;
    }
    
    @Override
    public int getGlyphCount() {
        return this.gv.getNumGlyphs();
    }
    
    @Override
    public int getCharacterCount(final int startGlyphIndex, final int endGlyphIndex) {
        return this.gv.getCharacterCount(startGlyphIndex, endGlyphIndex);
    }
    
    @Override
    public boolean isLeftToRight() {
        this.aci.first();
        final int bidiLevel = (int)this.aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.BIDI_LEVEL);
        return (bidiLevel & 0x1) == 0x0;
    }
    
    private final void syncLayout() {
        if (!this.pathApplied) {
            this.doPathLayout();
        }
    }
    
    @Override
    public void draw(final Graphics2D g2d) {
        this.syncLayout();
        this.gv.draw(g2d, this.aci);
    }
    
    @Override
    public Point2D getAdvance2D() {
        this.adjustTextSpacing();
        return this.advance;
    }
    
    @Override
    public Shape getOutline() {
        this.syncLayout();
        return this.gv.getOutline();
    }
    
    @Override
    public float[] getGlyphAdvances() {
        if (this.glyphAdvances != null) {
            return this.glyphAdvances;
        }
        if (!this.spacingApplied) {
            this.adjustTextSpacing();
        }
        final int numGlyphs = this.gv.getNumGlyphs();
        final float[] glyphPos = this.gv.getGlyphPositions(0, numGlyphs + 1, null);
        this.glyphAdvances = new float[numGlyphs + 1];
        int off = 0;
        if (this.isVertical()) {
            off = 1;
        }
        final float start = glyphPos[off];
        for (int i = 0; i < numGlyphs + 1; ++i) {
            this.glyphAdvances[i] = glyphPos[2 * i + off] - start;
        }
        return this.glyphAdvances;
    }
    
    @Override
    public Shape getDecorationOutline(final int decorationType) {
        this.syncLayout();
        final Shape g = new GeneralPath();
        if ((decorationType & 0x1) != 0x0) {
            ((GeneralPath)g).append(this.getUnderlineShape(), false);
        }
        if ((decorationType & 0x2) != 0x0) {
            ((GeneralPath)g).append(this.getStrikethroughShape(), false);
        }
        if ((decorationType & 0x4) != 0x0) {
            ((GeneralPath)g).append(this.getOverlineShape(), false);
        }
        return g;
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        this.syncLayout();
        return this.gv.getBounds2D(this.aci);
    }
    
    @Override
    public Rectangle2D getGeometricBounds() {
        this.syncLayout();
        final Rectangle2D gvB = this.gv.getGeometricBounds();
        final Rectangle2D decB = this.getDecorationOutline(7).getBounds2D();
        return gvB.createUnion(decB);
    }
    
    @Override
    public Point2D getTextPathAdvance() {
        this.syncLayout();
        if (this.textPath != null) {
            return this.textPathAdvance;
        }
        return this.getAdvance2D();
    }
    
    @Override
    public int getGlyphIndex(final int charIndex) {
        final int numGlyphs = this.getGlyphCount();
        int j = 0;
        for (int i = 0; i < numGlyphs; ++i) {
            for (int count = this.getCharacterCount(i, i), n = 0; n < count; ++n) {
                final int glyphCharIndex = this.charMap[j++];
                if (charIndex == glyphCharIndex) {
                    return i;
                }
                if (j >= this.charMap.length) {
                    return -1;
                }
            }
        }
        return -1;
    }
    
    public int getLastGlyphIndex(final int charIndex) {
        final int numGlyphs = this.getGlyphCount();
        int j = this.charMap.length - 1;
        for (int i = numGlyphs - 1; i >= 0; --i) {
            for (int count = this.getCharacterCount(i, i), n = 0; n < count; ++n) {
                final int glyphCharIndex = this.charMap[j--];
                if (charIndex == glyphCharIndex) {
                    return i;
                }
                if (j < 0) {
                    return -1;
                }
            }
        }
        return -1;
    }
    
    @Override
    public double getComputedOrientationAngle(final int index) {
        if (!this.isGlyphOrientationAuto()) {
            return this.getGlyphOrientationAngle();
        }
        if (!this.isVertical()) {
            return 0.0;
        }
        final char ch = this.aci.setIndex(index);
        if (this.isLatinChar(ch)) {
            return 90.0;
        }
        return 0.0;
    }
    
    @Override
    public Shape getHighlightShape(int beginCharIndex, int endCharIndex) {
        this.syncLayout();
        if (beginCharIndex > endCharIndex) {
            final int temp = beginCharIndex;
            beginCharIndex = endCharIndex;
            endCharIndex = temp;
        }
        GeneralPath shape = null;
        final int numGlyphs = this.getGlyphCount();
        final Point2D.Float[] topPts = new Point2D.Float[2 * numGlyphs];
        final Point2D.Float[] botPts = new Point2D.Float[2 * numGlyphs];
        int ptIdx = 0;
        int currentChar = 0;
        for (int i = 0; i < numGlyphs; ++i) {
            final int glyphCharIndex = this.charMap[currentChar];
            if (glyphCharIndex >= beginCharIndex && glyphCharIndex <= endCharIndex && this.gv.isGlyphVisible(i)) {
                final Shape gbounds = this.gv.getGlyphLogicalBounds(i);
                if (gbounds != null) {
                    if (shape == null) {
                        shape = new GeneralPath();
                    }
                    final float[] pts = new float[6];
                    int count = 0;
                    int type = -1;
                    final PathIterator pi = gbounds.getPathIterator(null);
                    Point2D.Float firstPt = null;
                    while (!pi.isDone()) {
                        type = pi.currentSegment(pts);
                        if (type == 0 || type == 1) {
                            if (count > 4) {
                                break;
                            }
                            if (count == 4) {
                                if (firstPt == null || firstPt.x != pts[0]) {
                                    break;
                                }
                                if (firstPt.y != pts[1]) {
                                    break;
                                }
                            }
                            else {
                                final Point2D.Float pt = new Point2D.Float(pts[0], pts[1]);
                                if (count == 0) {
                                    firstPt = pt;
                                }
                                switch (count) {
                                    case 0: {
                                        botPts[ptIdx] = pt;
                                        break;
                                    }
                                    case 1: {
                                        topPts[ptIdx] = pt;
                                        break;
                                    }
                                    case 2: {
                                        topPts[ptIdx + 1] = pt;
                                        break;
                                    }
                                    case 3: {
                                        botPts[ptIdx + 1] = pt;
                                        break;
                                    }
                                }
                            }
                        }
                        else {
                            if (type != 4 || count < 4) {
                                break;
                            }
                            if (count > 5) {
                                break;
                            }
                        }
                        ++count;
                        pi.next();
                    }
                    if (pi.isDone()) {
                        if (botPts[ptIdx] != null && (topPts[ptIdx].x != topPts[ptIdx + 1].x || topPts[ptIdx].y != topPts[ptIdx + 1].y)) {
                            ptIdx += 2;
                        }
                    }
                    else {
                        addPtsToPath(shape, topPts, botPts, ptIdx);
                        ptIdx = 0;
                        shape.append(gbounds, false);
                    }
                }
            }
            currentChar += this.getCharacterCount(i, i);
            if (currentChar >= this.charMap.length) {
                currentChar = this.charMap.length - 1;
            }
        }
        addPtsToPath(shape, topPts, botPts, ptIdx);
        return shape;
    }
    
    public static boolean epsEQ(final double a, final double b) {
        return a + 1.0E-5 > b && a - 1.0E-5 < b;
    }
    
    public static int makeConvexHull(final Point2D.Float[] pts, final int numPts) {
        for (int i = 1; i < numPts; ++i) {
            if (pts[i].x < pts[i - 1].x || (pts[i].x == pts[i - 1].x && pts[i].y < pts[i - 1].y)) {
                final Point2D.Float tmp = pts[i];
                pts[i] = pts[i - 1];
                pts[i - 1] = tmp;
                i = 0;
            }
        }
        Point2D.Float pt0 = pts[0];
        Point2D.Float pt2 = pts[numPts - 1];
        final Point2D.Float dxdy = new Point2D.Float(pt2.x - pt0.x, pt2.y - pt0.y);
        final float c = dxdy.y * pt0.x - dxdy.x * pt0.y;
        final Point2D.Float[] topList = new Point2D.Float[numPts];
        final Point2D.Float[] botList = new Point2D.Float[numPts];
        botList[0] = (topList[0] = pts[0]);
        int nTopPts = 1;
        int nBotPts = 1;
        for (int j = 1; j < numPts - 1; ++j) {
            Point2D.Float pt3 = pts[j];
            float soln = dxdy.x * pt3.y - dxdy.y * pt3.x + c;
            if (soln < 0.0f) {
                while (nBotPts >= 2) {
                    pt0 = botList[nBotPts - 2];
                    pt2 = botList[nBotPts - 1];
                    final float dx = pt2.x - pt0.x;
                    final float dy = pt2.y - pt0.y;
                    final float c2 = dy * pt0.x - dx * pt0.y;
                    soln = dx * pt3.y - dy * pt3.x + c2;
                    if (soln > 1.0E-5) {
                        break;
                    }
                    if (soln > -1.0E-5) {
                        if (pt2.y < pt3.y) {
                            pt3 = pt2;
                        }
                        --nBotPts;
                        break;
                    }
                    --nBotPts;
                }
                botList[nBotPts++] = pt3;
            }
            else {
                while (nTopPts >= 2) {
                    pt0 = topList[nTopPts - 2];
                    pt2 = topList[nTopPts - 1];
                    final float dx = pt2.x - pt0.x;
                    final float dy = pt2.y - pt0.y;
                    final float c2 = dy * pt0.x - dx * pt0.y;
                    soln = dx * pt3.y - dy * pt3.x + c2;
                    if (soln < -1.0E-5) {
                        break;
                    }
                    if (soln < 1.0E-5) {
                        if (pt2.y > pt3.y) {
                            pt3 = pt2;
                        }
                        --nTopPts;
                        break;
                    }
                    --nTopPts;
                }
                topList[nTopPts++] = pt3;
            }
        }
        final Point2D.Float pt4 = pts[numPts - 1];
        while (nBotPts >= 2) {
            pt0 = botList[nBotPts - 2];
            pt2 = botList[nBotPts - 1];
            final float dx2 = pt2.x - pt0.x;
            final float dy2 = pt2.y - pt0.y;
            final float c3 = dy2 * pt0.x - dx2 * pt0.y;
            final float soln = dx2 * pt4.y - dy2 * pt4.x + c3;
            if (soln > 1.0E-5) {
                break;
            }
            if (soln > -1.0E-5) {
                if (pt2.y >= pt4.y) {
                    --nBotPts;
                    break;
                }
                break;
            }
            else {
                --nBotPts;
            }
        }
        while (nTopPts >= 2) {
            pt0 = topList[nTopPts - 2];
            pt2 = topList[nTopPts - 1];
            final float dx2 = pt2.x - pt0.x;
            final float dy2 = pt2.y - pt0.y;
            final float c3 = dy2 * pt0.x - dx2 * pt0.y;
            final float soln = dx2 * pt4.y - dy2 * pt4.x + c3;
            if (soln < -1.0E-5) {
                break;
            }
            if (soln < 1.0E-5) {
                if (pt2.y <= pt4.y) {
                    --nTopPts;
                    break;
                }
                break;
            }
            else {
                --nTopPts;
            }
        }
        System.arraycopy(topList, 0, pts, 0, nTopPts);
        int k = nTopPts;
        pts[k++] = pts[numPts - 1];
        for (int n = nBotPts - 1; n > 0; --n, ++k) {
            pts[k] = botList[n];
        }
        return k;
    }
    
    public static void addPtsToPath(final GeneralPath shape, final Point2D.Float[] topPts, final Point2D.Float[] botPts, final int numPts) {
        if (numPts < 2) {
            return;
        }
        if (numPts == 2) {
            shape.moveTo(topPts[0].x, topPts[0].y);
            shape.lineTo(topPts[1].x, topPts[1].y);
            shape.lineTo(botPts[1].x, botPts[1].y);
            shape.lineTo(botPts[0].x, botPts[0].y);
            shape.lineTo(topPts[0].x, topPts[0].y);
            return;
        }
        final Point2D.Float[] boxes = new Point2D.Float[8];
        final Point2D.Float[] chull = new Point2D.Float[8];
        boxes[4] = topPts[0];
        boxes[5] = topPts[1];
        boxes[6] = botPts[1];
        boxes[7] = botPts[0];
        final Area[] areas = new Area[numPts / 2];
        int nAreas = 0;
        for (int i = 2; i < numPts; i += 2) {
            boxes[0] = boxes[4];
            boxes[1] = boxes[5];
            boxes[2] = boxes[6];
            boxes[3] = boxes[7];
            boxes[4] = topPts[i];
            boxes[5] = topPts[i + 1];
            boxes[6] = botPts[i + 1];
            boxes[7] = botPts[i];
            float delta = boxes[2].x - boxes[0].x;
            float dist = delta * delta;
            delta = boxes[2].y - boxes[0].y;
            dist += delta * delta;
            float sz = (float)Math.sqrt(dist);
            delta = boxes[6].x - boxes[4].x;
            dist = delta * delta;
            delta = boxes[6].y - boxes[4].y;
            dist += delta * delta;
            sz += (float)Math.sqrt(dist);
            delta = (boxes[0].x + boxes[1].x + boxes[2].x + boxes[3].x - (boxes[4].x + boxes[5].x + boxes[6].x + boxes[7].x)) / 4.0f;
            dist = delta * delta;
            delta = (boxes[0].y + boxes[1].y + boxes[2].y + boxes[3].y - (boxes[4].y + boxes[5].y + boxes[6].y + boxes[7].y)) / 4.0f;
            dist += delta * delta;
            dist = (float)Math.sqrt(dist);
            final GeneralPath gp = new GeneralPath();
            if (dist < sz) {
                System.arraycopy(boxes, 0, chull, 0, 8);
                final int npts = makeConvexHull(chull, 8);
                gp.moveTo(chull[0].x, chull[0].y);
                for (int n = 1; n < npts; ++n) {
                    gp.lineTo(chull[n].x, chull[n].y);
                }
                gp.closePath();
            }
            else {
                mergeAreas(shape, areas, nAreas);
                nAreas = 0;
                if (i == 2) {
                    gp.moveTo(boxes[0].x, boxes[0].y);
                    gp.lineTo(boxes[1].x, boxes[1].y);
                    gp.lineTo(boxes[2].x, boxes[2].y);
                    gp.lineTo(boxes[3].x, boxes[3].y);
                    gp.closePath();
                    shape.append(gp, false);
                    gp.reset();
                }
                gp.moveTo(boxes[4].x, boxes[4].y);
                gp.lineTo(boxes[5].x, boxes[5].y);
                gp.lineTo(boxes[6].x, boxes[6].y);
                gp.lineTo(boxes[7].x, boxes[7].y);
                gp.closePath();
            }
            areas[nAreas++] = new Area(gp);
        }
        mergeAreas(shape, areas, nAreas);
    }
    
    public static void mergeAreas(final GeneralPath shape, final Area[] shapes, int nShapes) {
        while (nShapes > 1) {
            int n = 0;
            for (int i = 1; i < nShapes; i += 2) {
                shapes[i - 1].add(shapes[i]);
                shapes[n++] = shapes[i - 1];
                shapes[i] = null;
            }
            if ((nShapes & 0x1) == 0x1) {
                shapes[n - 1].add(shapes[nShapes - 1]);
            }
            nShapes /= 2;
        }
        if (nShapes == 1) {
            shape.append(shapes[0], false);
        }
    }
    
    @Override
    public TextHit hitTestChar(final float x, final float y) {
        this.syncLayout();
        TextHit textHit = null;
        int currentChar = 0;
        for (int i = 0; i < this.gv.getNumGlyphs(); ++i) {
            final Shape gbounds = this.gv.getGlyphLogicalBounds(i);
            if (gbounds != null) {
                final Rectangle2D gbounds2d = gbounds.getBounds2D();
                if (gbounds.contains(x, y)) {
                    final boolean isRightHalf = x > gbounds2d.getX() + gbounds2d.getWidth() / 2.0;
                    final boolean isLeadingEdge = !isRightHalf;
                    final int charIndex = this.charMap[currentChar];
                    textHit = new TextHit(charIndex, isLeadingEdge);
                    return textHit;
                }
            }
            currentChar += this.getCharacterCount(i, i);
            if (currentChar >= this.charMap.length) {
                currentChar = this.charMap.length - 1;
            }
        }
        return textHit;
    }
    
    protected GVTFont getFont() {
        this.aci.first();
        final GVTFont gvtFont = (GVTFont)this.aci.getAttribute(GlyphLayout.GVT_FONT);
        if (gvtFont != null) {
            return gvtFont;
        }
        return new AWTGVTFont(this.aci.getAttributes());
    }
    
    protected Shape getOverlineShape() {
        double y = this.metrics.getOverlineOffset();
        final float overlineThickness = this.metrics.getOverlineThickness();
        y += overlineThickness;
        this.aci.first();
        final Float dy = (Float)this.aci.getAttribute(GlyphLayout.DY);
        if (dy != null) {
            y += dy;
        }
        final Stroke overlineStroke = new BasicStroke(overlineThickness);
        final Rectangle2D logicalBounds = this.gv.getLogicalBounds();
        return overlineStroke.createStrokedShape(new Line2D.Double(logicalBounds.getMinX() + overlineThickness / 2.0, this.offset.getY() + y, logicalBounds.getMaxX() - overlineThickness / 2.0, this.offset.getY() + y));
    }
    
    protected Shape getUnderlineShape() {
        double y = this.metrics.getUnderlineOffset();
        final float underlineThickness = this.metrics.getUnderlineThickness();
        y += underlineThickness * 1.5;
        final BasicStroke underlineStroke = new BasicStroke(underlineThickness);
        this.aci.first();
        final Float dy = (Float)this.aci.getAttribute(GlyphLayout.DY);
        if (dy != null) {
            y += dy;
        }
        final Rectangle2D logicalBounds = this.gv.getLogicalBounds();
        return underlineStroke.createStrokedShape(new Line2D.Double(logicalBounds.getMinX() + underlineThickness / 2.0, this.offset.getY() + y, logicalBounds.getMaxX() - underlineThickness / 2.0, this.offset.getY() + y));
    }
    
    protected Shape getStrikethroughShape() {
        double y = this.metrics.getStrikethroughOffset();
        final float strikethroughThickness = this.metrics.getStrikethroughThickness();
        final Stroke strikethroughStroke = new BasicStroke(strikethroughThickness);
        this.aci.first();
        final Float dy = (Float)this.aci.getAttribute(GlyphLayout.DY);
        if (dy != null) {
            y += dy;
        }
        final Rectangle2D logicalBounds = this.gv.getLogicalBounds();
        return strikethroughStroke.createStrokedShape(new Line2D.Double(logicalBounds.getMinX() + strikethroughThickness / 2.0, this.offset.getY() + y, logicalBounds.getMaxX() - strikethroughThickness / 2.0, this.offset.getY() + y));
    }
    
    protected void doExplicitGlyphLayout() {
        this.gv.performDefaultLayout();
        final float baselineAscent = this.vertical ? ((float)this.gv.getLogicalBounds().getWidth()) : (this.metrics.getAscent() + Math.abs(this.metrics.getDescent()));
        final int numGlyphs = this.gv.getNumGlyphs();
        final float[] gp = this.gv.getGlyphPositions(0, numGlyphs + 1, null);
        float verticalFirstOffset = 0.0f;
        float horizontalFirstOffset = 0.0f;
        final boolean glyphOrientationAuto = this.isGlyphOrientationAuto();
        int glyphOrientationAngle = 0;
        if (!glyphOrientationAuto) {
            glyphOrientationAngle = this.getGlyphOrientationAngle();
        }
        int i = 0;
        final int aciStart = this.aci.getBeginIndex();
        int aciIndex = 0;
        char ch = this.aci.first();
        int runLimit = aciIndex + aciStart;
        Float x = null;
        Float y = null;
        Float dx = null;
        Float dy = null;
        Float rotation = null;
        Object baseline = null;
        float shift_x_pos = 0.0f;
        float shift_y_pos = 0.0f;
        float curr_x_pos = (float)this.offset.getX();
        float curr_y_pos = (float)this.offset.getY();
        final Point2D.Float pos = new Point2D.Float();
        boolean hasArabicTransparent = false;
        while (i < numGlyphs) {
            if (aciIndex + aciStart >= runLimit) {
                runLimit = this.aci.getRunLimit(GlyphLayout.runAtts);
                x = (Float)this.aci.getAttribute(GlyphLayout.X);
                y = (Float)this.aci.getAttribute(GlyphLayout.Y);
                dx = (Float)this.aci.getAttribute(GlyphLayout.DX);
                dy = (Float)this.aci.getAttribute(GlyphLayout.DY);
                rotation = (Float)this.aci.getAttribute(GlyphLayout.ROTATION);
                baseline = this.aci.getAttribute(GlyphLayout.BASELINE_SHIFT);
            }
            final GVTGlyphMetrics gm = this.gv.getGlyphMetrics(i);
            if (i == 0) {
                if (this.isVertical()) {
                    if (glyphOrientationAuto) {
                        if (this.isLatinChar(ch)) {
                            verticalFirstOffset = 0.0f;
                        }
                        else {
                            final float advY = gm.getVerticalAdvance();
                            final float asc = this.metrics.getAscent();
                            final float dsc = this.metrics.getDescent();
                            verticalFirstOffset = asc + (advY - (asc + dsc)) / 2.0f;
                        }
                    }
                    else if (glyphOrientationAngle == 0) {
                        final float advY = gm.getVerticalAdvance();
                        final float asc = this.metrics.getAscent();
                        final float dsc = this.metrics.getDescent();
                        verticalFirstOffset = asc + (advY - (asc + dsc)) / 2.0f;
                    }
                    else {
                        verticalFirstOffset = 0.0f;
                    }
                }
                else if (glyphOrientationAngle == 270) {
                    horizontalFirstOffset = (float)gm.getBounds2D().getHeight();
                }
                else {
                    horizontalFirstOffset = 0.0f;
                }
            }
            else if (glyphOrientationAuto && verticalFirstOffset == 0.0f && !this.isLatinChar(ch)) {
                final float advY = gm.getVerticalAdvance();
                final float asc = this.metrics.getAscent();
                final float dsc = this.metrics.getDescent();
                verticalFirstOffset = asc + (advY - (asc + dsc)) / 2.0f;
            }
            float ox = 0.0f;
            float oy = 0.0f;
            float glyphOrientationRotation = 0.0f;
            float glyphRotation = 0.0f;
            if (ch != '\uffff') {
                if (this.vertical) {
                    if (glyphOrientationAuto) {
                        if (this.isLatinChar(ch)) {
                            glyphOrientationRotation = 1.5707964f;
                        }
                        else {
                            glyphOrientationRotation = 0.0f;
                        }
                    }
                    else {
                        glyphOrientationRotation = (float)Math.toRadians(glyphOrientationAngle);
                    }
                    if (this.textPath != null) {
                        x = null;
                    }
                }
                else {
                    glyphOrientationRotation = (float)Math.toRadians(glyphOrientationAngle);
                    if (this.textPath != null) {
                        y = null;
                    }
                }
                if (rotation == null || rotation.isNaN()) {
                    glyphRotation = glyphOrientationRotation;
                }
                else {
                    glyphRotation = rotation + glyphOrientationRotation;
                }
                if (x != null && !x.isNaN()) {
                    if (i == 0) {
                        shift_x_pos = (float)(x - this.offset.getX());
                    }
                    curr_x_pos = x - shift_x_pos;
                }
                if (dx != null && !dx.isNaN()) {
                    curr_x_pos += dx;
                }
                if (y != null && !y.isNaN()) {
                    if (i == 0) {
                        shift_y_pos = (float)(y - this.offset.getY());
                    }
                    curr_y_pos = y - shift_y_pos;
                }
                if (dy != null && !dy.isNaN()) {
                    curr_y_pos += dy;
                }
                else if (i > 0) {
                    curr_y_pos += gp[i * 2 + 1] - gp[i * 2 - 1];
                }
                float baselineAdjust = 0.0f;
                if (baseline != null) {
                    if (baseline instanceof Integer) {
                        if (baseline == TextAttribute.SUPERSCRIPT_SUPER) {
                            baselineAdjust = baselineAscent * 0.5f;
                        }
                        else if (baseline == TextAttribute.SUPERSCRIPT_SUB) {
                            baselineAdjust = -baselineAscent * 0.5f;
                        }
                    }
                    else if (baseline instanceof Float) {
                        baselineAdjust = (float)baseline;
                    }
                    if (this.vertical) {
                        ox = baselineAdjust;
                    }
                    else {
                        oy = -baselineAdjust;
                    }
                }
                if (this.vertical) {
                    oy += verticalFirstOffset;
                    if (glyphOrientationAuto) {
                        if (this.isLatinChar(ch)) {
                            ox += this.metrics.getStrikethroughOffset();
                        }
                        else {
                            final Rectangle2D glyphBounds = this.gv.getGlyphVisualBounds(i).getBounds2D();
                            ox -= (float)(glyphBounds.getMaxX() - gp[2 * i] - glyphBounds.getWidth() / 2.0);
                        }
                    }
                    else {
                        final Rectangle2D glyphBounds = this.gv.getGlyphVisualBounds(i).getBounds2D();
                        if (glyphOrientationAngle == 0) {
                            ox -= (float)(glyphBounds.getMaxX() - gp[2 * i] - glyphBounds.getWidth() / 2.0);
                        }
                        else if (glyphOrientationAngle == 180) {
                            ox += (float)(glyphBounds.getMaxX() - gp[2 * i] - glyphBounds.getWidth() / 2.0);
                        }
                        else if (glyphOrientationAngle == 90) {
                            ox += this.metrics.getStrikethroughOffset();
                        }
                        else {
                            ox -= this.metrics.getStrikethroughOffset();
                        }
                    }
                }
                else {
                    ox += horizontalFirstOffset;
                    if (glyphOrientationAngle == 90) {
                        oy -= gm.getHorizontalAdvance();
                    }
                    else if (glyphOrientationAngle == 180) {
                        oy -= this.metrics.getAscent();
                    }
                }
            }
            pos.x = curr_x_pos + ox;
            pos.y = curr_y_pos + oy;
            this.gv.setGlyphPosition(i, pos);
            if (ArabicTextHandler.arabicCharTransparent(ch)) {
                hasArabicTransparent = true;
            }
            else if (this.vertical) {
                float advanceY = 0.0f;
                if (glyphOrientationAuto) {
                    if (this.isLatinChar(ch)) {
                        advanceY = gm.getHorizontalAdvance();
                    }
                    else {
                        advanceY = gm.getVerticalAdvance();
                    }
                }
                else if (glyphOrientationAngle == 0 || glyphOrientationAngle == 180) {
                    advanceY = gm.getVerticalAdvance();
                }
                else if (glyphOrientationAngle == 90) {
                    advanceY = gm.getHorizontalAdvance();
                }
                else {
                    advanceY = gm.getHorizontalAdvance();
                    this.gv.setGlyphTransform(i, AffineTransform.getTranslateInstance(0.0, advanceY));
                }
                curr_y_pos += advanceY;
            }
            else {
                float advanceX = 0.0f;
                if (glyphOrientationAngle == 0) {
                    advanceX = gm.getHorizontalAdvance();
                }
                else if (glyphOrientationAngle == 180) {
                    advanceX = gm.getHorizontalAdvance();
                    this.gv.setGlyphTransform(i, AffineTransform.getTranslateInstance(advanceX, 0.0));
                }
                else {
                    advanceX = gm.getVerticalAdvance();
                }
                curr_x_pos += advanceX;
            }
            if (!epsEQ(glyphRotation, 0.0)) {
                AffineTransform glyphTransform = this.gv.getGlyphTransform(i);
                if (glyphTransform == null) {
                    glyphTransform = new AffineTransform();
                }
                AffineTransform rotAt;
                if (epsEQ(glyphRotation, 1.5707963267948966)) {
                    rotAt = new AffineTransform(0.0f, 1.0f, -1.0f, 0.0f, 0.0f, 0.0f);
                }
                else if (epsEQ(glyphRotation, 3.141592653589793)) {
                    rotAt = new AffineTransform(-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f);
                }
                else if (epsEQ(glyphRotation, 4.71238898038469)) {
                    rotAt = new AffineTransform(0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
                }
                else {
                    rotAt = AffineTransform.getRotateInstance(glyphRotation);
                }
                glyphTransform.concatenate(rotAt);
                this.gv.setGlyphTransform(i, glyphTransform);
            }
            aciIndex += this.gv.getCharacterCount(i, i);
            if (aciIndex >= this.charMap.length) {
                aciIndex = this.charMap.length - 1;
            }
            ch = this.aci.setIndex(aciIndex + aciStart);
            ++i;
        }
        pos.x = curr_x_pos;
        pos.y = curr_y_pos;
        this.gv.setGlyphPosition(i, pos);
        this.advance = new Point2D.Float((float)(curr_x_pos - this.offset.getX()), (float)(curr_y_pos - this.offset.getY()));
        if (hasArabicTransparent) {
            ch = this.aci.first();
            aciIndex = 0;
            i = 0;
            int transparentStart = -1;
            while (i < numGlyphs) {
                if (ArabicTextHandler.arabicCharTransparent(ch)) {
                    if (transparentStart == -1) {
                        transparentStart = i;
                    }
                }
                else if (transparentStart != -1) {
                    final Point2D loc = this.gv.getGlyphPosition(i);
                    final GVTGlyphMetrics gm2 = this.gv.getGlyphMetrics(i);
                    final int tyS = 0;
                    final int txS = 0;
                    float advX = 0.0f;
                    float advY2 = 0.0f;
                    if (this.vertical) {
                        if (glyphOrientationAuto || glyphOrientationAngle == 90) {
                            advY2 = gm2.getHorizontalAdvance();
                        }
                        else if (glyphOrientationAngle == 270) {
                            advY2 = 0.0f;
                        }
                        else if (glyphOrientationAngle == 0) {
                            advX = gm2.getHorizontalAdvance();
                        }
                        else {
                            advX = -gm2.getHorizontalAdvance();
                        }
                    }
                    else if (glyphOrientationAngle == 0) {
                        advX = gm2.getHorizontalAdvance();
                    }
                    else if (glyphOrientationAngle == 90) {
                        advY2 = gm2.getHorizontalAdvance();
                    }
                    else if (glyphOrientationAngle == 180) {
                        advX = 0.0f;
                    }
                    else {
                        advY2 = -gm2.getHorizontalAdvance();
                    }
                    final float baseX = (float)(loc.getX() + advX);
                    final float baseY = (float)(loc.getY() + advY2);
                    for (int j = transparentStart; j < i; ++j) {
                        Point2D locT = this.gv.getGlyphPosition(j);
                        final GVTGlyphMetrics gmT = this.gv.getGlyphMetrics(j);
                        float locX = (float)locT.getX();
                        float locY = (float)locT.getY();
                        final float tx = 0.0f;
                        final float ty = 0.0f;
                        final float advT = gmT.getHorizontalAdvance();
                        if (this.vertical) {
                            if (glyphOrientationAuto || glyphOrientationAngle == 90) {
                                locY = baseY - advT;
                            }
                            else if (glyphOrientationAngle == 270) {
                                locY = baseY + advT;
                            }
                            else if (glyphOrientationAngle == 0) {
                                locX = baseX - advT;
                            }
                            else {
                                locX = baseX + advT;
                            }
                        }
                        else if (glyphOrientationAngle == 0) {
                            locX = baseX - advT;
                        }
                        else if (glyphOrientationAngle == 90) {
                            locY = baseY - advT;
                        }
                        else if (glyphOrientationAngle == 180) {
                            locX = baseX + advT;
                        }
                        else {
                            locY = baseY + advT;
                        }
                        locT = new Point2D.Double(locX, locY);
                        this.gv.setGlyphPosition(j, locT);
                        if (txS != 0 || tyS != 0) {
                            final AffineTransform at = AffineTransform.getTranslateInstance(tx, ty);
                            at.concatenate(this.gv.getGlyphTransform(i));
                            this.gv.setGlyphTransform(i, at);
                        }
                    }
                    transparentStart = -1;
                }
                aciIndex += this.gv.getCharacterCount(i, i);
                if (aciIndex >= this.charMap.length) {
                    aciIndex = this.charMap.length - 1;
                }
                ch = this.aci.setIndex(aciIndex + aciStart);
                ++i;
            }
        }
        this.layoutApplied = true;
        this.spacingApplied = false;
        this.glyphAdvances = null;
        this.pathApplied = false;
    }
    
    protected void adjustTextSpacing() {
        if (this.spacingApplied) {
            return;
        }
        if (!this.layoutApplied) {
            this.doExplicitGlyphLayout();
        }
        this.aci.first();
        final Boolean customSpacing = (Boolean)this.aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.CUSTOM_SPACING);
        if (customSpacing != null && customSpacing) {
            this.advance = this.doSpacing((Float)this.aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.KERNING), (Float)this.aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.LETTER_SPACING), (Float)this.aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.WORD_SPACING));
            this.layoutApplied = false;
        }
        this.applyStretchTransform(!this.adjSpacing);
        this.spacingApplied = true;
        this.pathApplied = false;
    }
    
    protected Point2D doSpacing(final Float kern, final Float letterSpacing, final Float wordSpacing) {
        boolean autoKern = true;
        boolean doWordSpacing = false;
        boolean doLetterSpacing = false;
        float kernVal = 0.0f;
        float letterSpacingVal = 0.0f;
        if (kern != null && !kern.isNaN()) {
            kernVal = kern;
            autoKern = false;
        }
        if (letterSpacing != null && !letterSpacing.isNaN()) {
            letterSpacingVal = letterSpacing;
            doLetterSpacing = true;
        }
        if (wordSpacing != null && !wordSpacing.isNaN()) {
            doWordSpacing = true;
        }
        final int numGlyphs = this.gv.getNumGlyphs();
        float dx = 0.0f;
        float dy = 0.0f;
        final Point2D[] newPositions = new Point2D[numGlyphs + 1];
        Point2D prevPos = this.gv.getGlyphPosition(0);
        int prevCode = this.gv.getGlyphCode(0);
        float x = (float)prevPos.getX();
        float y = (float)prevPos.getY();
        final Point2D lastCharAdvance = new Point2D.Double(this.advance.getX() - (this.gv.getGlyphPosition(numGlyphs - 1).getX() - x), this.advance.getY() - (this.gv.getGlyphPosition(numGlyphs - 1).getY() - y));
        try {
            final GVTFont font = this.gv.getFont();
            if (numGlyphs > 1 && (doLetterSpacing || !autoKern)) {
                for (int i = 1; i <= numGlyphs; ++i) {
                    final Point2D gpos = this.gv.getGlyphPosition(i);
                    final int currCode = (i == numGlyphs) ? -1 : this.gv.getGlyphCode(i);
                    dx = (float)gpos.getX() - (float)prevPos.getX();
                    dy = (float)gpos.getY() - (float)prevPos.getY();
                    if (autoKern) {
                        if (this.vertical) {
                            dy += letterSpacingVal;
                        }
                        else {
                            dx += letterSpacingVal;
                        }
                    }
                    else if (this.vertical) {
                        float vKern = 0.0f;
                        if (currCode != -1) {
                            vKern = font.getVKern(prevCode, currCode);
                        }
                        dy += kernVal - vKern + letterSpacingVal;
                    }
                    else {
                        float hKern = 0.0f;
                        if (currCode != -1) {
                            hKern = font.getHKern(prevCode, currCode);
                        }
                        dx += kernVal - hKern + letterSpacingVal;
                    }
                    x += dx;
                    y += dy;
                    newPositions[i] = new Point2D.Float(x, y);
                    prevPos = gpos;
                    prevCode = currCode;
                }
                for (int i = 1; i <= numGlyphs; ++i) {
                    if (newPositions[i] != null) {
                        this.gv.setGlyphPosition(i, newPositions[i]);
                    }
                }
            }
            if (this.vertical) {
                lastCharAdvance.setLocation(lastCharAdvance.getX(), lastCharAdvance.getY() + kernVal + letterSpacingVal);
            }
            else {
                lastCharAdvance.setLocation(lastCharAdvance.getX() + kernVal + letterSpacingVal, lastCharAdvance.getY());
            }
            dx = 0.0f;
            dy = 0.0f;
            prevPos = this.gv.getGlyphPosition(0);
            x = (float)prevPos.getX();
            y = (float)prevPos.getY();
            if (numGlyphs > 1 && doWordSpacing) {
                for (int i = 1; i < numGlyphs; ++i) {
                    Point2D gpos = this.gv.getGlyphPosition(i);
                    dx = (float)gpos.getX() - (float)prevPos.getX();
                    dy = (float)gpos.getY() - (float)prevPos.getY();
                    boolean inWS = false;
                    final int beginWS = i;
                    int endWS = i;
                    for (GVTGlyphMetrics gm = this.gv.getGlyphMetrics(i); gm.getBounds2D().getWidth() < 0.01 || gm.isWhitespace(); gm = this.gv.getGlyphMetrics(i)) {
                        if (!inWS) {
                            inWS = true;
                        }
                        if (i == numGlyphs - 1) {
                            break;
                        }
                        ++i;
                        ++endWS;
                        gpos = this.gv.getGlyphPosition(i);
                    }
                    if (inWS) {
                        final int nWS = endWS - beginWS;
                        final float px = (float)prevPos.getX();
                        final float py = (float)prevPos.getY();
                        dx = (float)(gpos.getX() - px) / (nWS + 1);
                        dy = (float)(gpos.getY() - py) / (nWS + 1);
                        if (this.vertical) {
                            dy += wordSpacing / (nWS + 1);
                        }
                        else {
                            dx += wordSpacing / (nWS + 1);
                        }
                        for (int j = beginWS; j <= endWS; ++j) {
                            x += dx;
                            y += dy;
                            newPositions[j] = new Point2D.Float(x, y);
                        }
                    }
                    else {
                        dx = (float)(gpos.getX() - prevPos.getX());
                        dy = (float)(gpos.getY() - prevPos.getY());
                        x += dx;
                        y += dy;
                        newPositions[i] = new Point2D.Float(x, y);
                    }
                    prevPos = gpos;
                }
                final Point2D gPos = this.gv.getGlyphPosition(numGlyphs);
                x += (float)(gPos.getX() - prevPos.getX());
                y += (float)(gPos.getY() - prevPos.getY());
                newPositions[numGlyphs] = new Point2D.Float(x, y);
                for (int k = 1; k <= numGlyphs; ++k) {
                    if (newPositions[k] != null) {
                        this.gv.setGlyphPosition(k, newPositions[k]);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        final double advX = this.gv.getGlyphPosition(numGlyphs - 1).getX() - this.gv.getGlyphPosition(0).getX();
        final double advY = this.gv.getGlyphPosition(numGlyphs - 1).getY() - this.gv.getGlyphPosition(0).getY();
        final Point2D newAdvance = new Point2D.Double(advX + lastCharAdvance.getX(), advY + lastCharAdvance.getY());
        return newAdvance;
    }
    
    protected void applyStretchTransform(final boolean stretchGlyphs) {
        if (this.xScale == 1.0f && this.yScale == 1.0f) {
            return;
        }
        final AffineTransform scaleAT = AffineTransform.getScaleInstance(this.xScale, this.yScale);
        final int numGlyphs = this.gv.getNumGlyphs();
        final float[] gp = this.gv.getGlyphPositions(0, numGlyphs + 1, null);
        final float initX = gp[0];
        final float initY = gp[1];
        final Point2D.Float pos = new Point2D.Float();
        for (int i = 0; i <= numGlyphs; ++i) {
            final float dx = gp[2 * i] - initX;
            final float dy = gp[2 * i + 1] - initY;
            pos.x = initX + dx * this.xScale;
            pos.y = initY + dy * this.yScale;
            this.gv.setGlyphPosition(i, pos);
            if (stretchGlyphs && i != numGlyphs) {
                final AffineTransform glyphTransform = this.gv.getGlyphTransform(i);
                if (glyphTransform != null) {
                    glyphTransform.preConcatenate(scaleAT);
                    this.gv.setGlyphTransform(i, glyphTransform);
                }
                else {
                    this.gv.setGlyphTransform(i, scaleAT);
                }
            }
        }
        this.advance = new Point2D.Float((float)(this.advance.getX() * this.xScale), (float)(this.advance.getY() * this.yScale));
        this.layoutApplied = false;
    }
    
    protected void doPathLayout() {
        if (this.pathApplied) {
            return;
        }
        if (!this.spacingApplied) {
            this.adjustTextSpacing();
        }
        this.getGlyphAdvances();
        if (this.textPath == null) {
            this.pathApplied = true;
            return;
        }
        final boolean horizontal = !this.isVertical();
        final boolean glyphOrientationAuto = this.isGlyphOrientationAuto();
        int glyphOrientationAngle = 0;
        if (!glyphOrientationAuto) {
            glyphOrientationAngle = this.getGlyphOrientationAngle();
        }
        final float pathLength = this.textPath.lengthOfPath();
        final float startOffset = this.textPath.getStartOffset();
        final int numGlyphs = this.gv.getNumGlyphs();
        for (int i = 0; i < numGlyphs; ++i) {
            this.gv.setGlyphVisible(i, true);
        }
        float glyphsLength;
        if (horizontal) {
            glyphsLength = (float)this.gv.getLogicalBounds().getWidth();
        }
        else {
            glyphsLength = (float)this.gv.getLogicalBounds().getHeight();
        }
        if (pathLength == 0.0f || glyphsLength == 0.0f) {
            this.pathApplied = true;
            this.textPathAdvance = this.advance;
            return;
        }
        final Point2D firstGlyphPosition = this.gv.getGlyphPosition(0);
        float glyphOffset = 0.0f;
        float currentPosition;
        if (horizontal) {
            glyphOffset = (float)firstGlyphPosition.getY();
            currentPosition = (float)(firstGlyphPosition.getX() + startOffset);
        }
        else {
            glyphOffset = (float)firstGlyphPosition.getX();
            currentPosition = (float)(firstGlyphPosition.getY() + startOffset);
        }
        char ch = this.aci.first();
        final int start = this.aci.getBeginIndex();
        int currentChar = 0;
        int lastGlyphDrawn = -1;
        float lastGlyphAdvance = 0.0f;
        for (int j = 0; j < numGlyphs; ++j) {
            final Point2D currentGlyphPos = this.gv.getGlyphPosition(j);
            float glyphAdvance = 0.0f;
            float nextGlyphOffset = 0.0f;
            final Point2D nextGlyphPosition = this.gv.getGlyphPosition(j + 1);
            if (horizontal) {
                glyphAdvance = (float)(nextGlyphPosition.getX() - currentGlyphPos.getX());
                nextGlyphOffset = (float)(nextGlyphPosition.getY() - currentGlyphPos.getY());
            }
            else {
                glyphAdvance = (float)(nextGlyphPosition.getY() - currentGlyphPos.getY());
                nextGlyphOffset = (float)(nextGlyphPosition.getX() - currentGlyphPos.getX());
            }
            final Rectangle2D glyphBounds = this.gv.getGlyphOutline(j).getBounds2D();
            final float glyphWidth = (float)glyphBounds.getWidth();
            final float glyphHeight = (float)glyphBounds.getHeight();
            float glyphMidX = 0.0f;
            if (glyphWidth > 0.0f) {
                glyphMidX = (float)(glyphBounds.getX() + glyphWidth / 2.0f);
                glyphMidX -= (float)currentGlyphPos.getX();
            }
            float glyphMidY = 0.0f;
            if (glyphHeight > 0.0f) {
                glyphMidY = (float)(glyphBounds.getY() + glyphHeight / 2.0f);
                glyphMidY -= (float)currentGlyphPos.getY();
            }
            float charMidPos;
            if (horizontal) {
                charMidPos = currentPosition + glyphMidX;
            }
            else {
                charMidPos = currentPosition + glyphMidY;
            }
            final Point2D charMidPoint = this.textPath.pointAtLength(charMidPos);
            if (charMidPoint != null) {
                final float angle = this.textPath.angleAtLength(charMidPos);
                final AffineTransform glyphPathTransform = new AffineTransform();
                if (horizontal) {
                    glyphPathTransform.rotate(angle);
                }
                else {
                    glyphPathTransform.rotate(angle - 1.5707963267948966);
                }
                if (horizontal) {
                    glyphPathTransform.translate(0.0, glyphOffset);
                }
                else {
                    glyphPathTransform.translate(glyphOffset, 0.0);
                }
                if (horizontal) {
                    glyphPathTransform.translate(-glyphMidX, 0.0);
                }
                else {
                    glyphPathTransform.translate(0.0, -glyphMidY);
                }
                final AffineTransform glyphTransform = this.gv.getGlyphTransform(j);
                if (glyphTransform != null) {
                    glyphPathTransform.concatenate(glyphTransform);
                }
                this.gv.setGlyphTransform(j, glyphPathTransform);
                this.gv.setGlyphPosition(j, charMidPoint);
                lastGlyphDrawn = j;
                lastGlyphAdvance = glyphAdvance;
            }
            else {
                this.gv.setGlyphVisible(j, false);
            }
            currentPosition += glyphAdvance;
            glyphOffset += nextGlyphOffset;
            currentChar += this.gv.getCharacterCount(j, j);
            if (currentChar >= this.charMap.length) {
                currentChar = this.charMap.length - 1;
            }
            ch = this.aci.setIndex(currentChar + start);
        }
        if (lastGlyphDrawn > -1) {
            final Point2D lastGlyphPos = this.gv.getGlyphPosition(lastGlyphDrawn);
            if (horizontal) {
                this.textPathAdvance = new Point2D.Double(lastGlyphPos.getX() + lastGlyphAdvance, lastGlyphPos.getY());
            }
            else {
                this.textPathAdvance = new Point2D.Double(lastGlyphPos.getX(), lastGlyphPos.getY() + lastGlyphAdvance);
            }
        }
        else {
            this.textPathAdvance = new Point2D.Double(0.0, 0.0);
        }
        this.layoutApplied = false;
        this.spacingApplied = false;
        this.pathApplied = true;
    }
    
    protected boolean isLatinChar(final char c) {
        if (c < '\u00ff' && Character.isLetterOrDigit(c)) {
            return true;
        }
        final Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return block == Character.UnicodeBlock.BASIC_LATIN || block == Character.UnicodeBlock.LATIN_1_SUPPLEMENT || block == Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL || block == Character.UnicodeBlock.LATIN_EXTENDED_A || block == Character.UnicodeBlock.LATIN_EXTENDED_B || block == Character.UnicodeBlock.ARABIC || block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A || block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B;
    }
    
    protected boolean isGlyphOrientationAuto() {
        if (!this.isVertical()) {
            return false;
        }
        this.aci.first();
        final Integer vOrient = (Integer)this.aci.getAttribute(GlyphLayout.VERTICAL_ORIENTATION);
        return vOrient == null || vOrient == GlyphLayout.ORIENTATION_AUTO;
    }
    
    protected int getGlyphOrientationAngle() {
        int glyphOrientationAngle = 0;
        this.aci.first();
        Float angle;
        if (this.isVertical()) {
            angle = (Float)this.aci.getAttribute(GlyphLayout.VERTICAL_ORIENTATION_ANGLE);
        }
        else {
            angle = (Float)this.aci.getAttribute(GlyphLayout.HORIZONTAL_ORIENTATION_ANGLE);
        }
        if (angle != null) {
            glyphOrientationAngle = (int)(float)angle;
        }
        if (glyphOrientationAngle != 0 || glyphOrientationAngle != 90 || glyphOrientationAngle != 180 || glyphOrientationAngle != 270) {
            while (glyphOrientationAngle < 0) {
                glyphOrientationAngle += 360;
            }
            while (glyphOrientationAngle >= 360) {
                glyphOrientationAngle -= 360;
            }
            if (glyphOrientationAngle <= 45 || glyphOrientationAngle > 315) {
                glyphOrientationAngle = 0;
            }
            else if (glyphOrientationAngle > 45 && glyphOrientationAngle <= 135) {
                glyphOrientationAngle = 90;
            }
            else if (glyphOrientationAngle > 135 && glyphOrientationAngle <= 225) {
                glyphOrientationAngle = 180;
            }
            else {
                glyphOrientationAngle = 270;
            }
        }
        return glyphOrientationAngle;
    }
    
    @Override
    public boolean hasCharacterIndex(final int index) {
        for (final int aCharMap : this.charMap) {
            if (index == aCharMap) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isAltGlyph() {
        return this.isAltGlyph;
    }
    
    @Override
    public boolean isReversed() {
        return this.gv.isReversed();
    }
    
    @Override
    public void maybeReverse(final boolean mirror) {
        this.gv.maybeReverse(mirror);
    }
    
    static {
        FLOW_LINE_BREAK = GVTAttributedCharacterIterator.TextAttribute.FLOW_LINE_BREAK;
        FLOW_PARAGRAPH = GVTAttributedCharacterIterator.TextAttribute.FLOW_PARAGRAPH;
        FLOW_EMPTY_PARAGRAPH = GVTAttributedCharacterIterator.TextAttribute.FLOW_EMPTY_PARAGRAPH;
        LINE_HEIGHT = GVTAttributedCharacterIterator.TextAttribute.LINE_HEIGHT;
        VERTICAL_ORIENTATION = GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION;
        VERTICAL_ORIENTATION_ANGLE = GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION_ANGLE;
        HORIZONTAL_ORIENTATION_ANGLE = GVTAttributedCharacterIterator.TextAttribute.HORIZONTAL_ORIENTATION_ANGLE;
        X = GVTAttributedCharacterIterator.TextAttribute.X;
        Y = GVTAttributedCharacterIterator.TextAttribute.Y;
        DX = GVTAttributedCharacterIterator.TextAttribute.DX;
        DY = GVTAttributedCharacterIterator.TextAttribute.DY;
        ROTATION = GVTAttributedCharacterIterator.TextAttribute.ROTATION;
        BASELINE_SHIFT = GVTAttributedCharacterIterator.TextAttribute.BASELINE_SHIFT;
        WRITING_MODE = GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE;
        WRITING_MODE_TTB = GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE_TTB;
        ORIENTATION_AUTO = GVTAttributedCharacterIterator.TextAttribute.ORIENTATION_AUTO;
        GVT_FONT = GVTAttributedCharacterIterator.TextAttribute.GVT_FONT;
        (GlyphLayout.runAtts = new HashSet()).add(GlyphLayout.X);
        GlyphLayout.runAtts.add(GlyphLayout.Y);
        GlyphLayout.runAtts.add(GlyphLayout.DX);
        GlyphLayout.runAtts.add(GlyphLayout.DY);
        GlyphLayout.runAtts.add(GlyphLayout.ROTATION);
        GlyphLayout.runAtts.add(GlyphLayout.BASELINE_SHIFT);
        (GlyphLayout.szAtts = new HashSet()).add(TextAttribute.SIZE);
        GlyphLayout.szAtts.add(GlyphLayout.GVT_FONT);
        GlyphLayout.szAtts.add(GlyphLayout.LINE_HEIGHT);
    }
}
