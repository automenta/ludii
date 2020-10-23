// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.HashSet;
import java.awt.geom.GeneralPath;
import java.awt.Stroke;
import java.awt.Paint;
import java.awt.Shape;
import org.apache.batik.gvt.text.TextPaintInfo;
import java.awt.geom.Rectangle2D;
import org.apache.batik.gvt.font.GVTLineMetrics;
import org.apache.batik.gvt.font.GVTGlyphMetrics;
import java.awt.font.FontRenderContext;
import java.awt.RenderingHints;
import org.apache.batik.gvt.font.GVTFontFamily;
import java.text.CharacterIterator;
import org.apache.batik.gvt.font.GVTFont;
import java.text.AttributedString;
import java.awt.font.TextAttribute;
import java.util.Iterator;
import org.apache.batik.gvt.text.AttributedCharacterSpanIterator;
import org.apache.batik.gvt.text.TextPath;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.batik.gvt.text.BidiAttributedCharacterIterator;
import java.util.List;
import java.awt.Graphics2D;
import java.util.Set;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import java.text.AttributedCharacterIterator;

public class StrokingTextPainter extends BasicTextPainter
{
    public static final AttributedCharacterIterator.Attribute PAINT_INFO;
    public static final AttributedCharacterIterator.Attribute FLOW_REGIONS;
    public static final AttributedCharacterIterator.Attribute FLOW_PARAGRAPH;
    public static final AttributedCharacterIterator.Attribute TEXT_COMPOUND_ID;
    public static final AttributedCharacterIterator.Attribute GVT_FONT;
    public static final AttributedCharacterIterator.Attribute GVT_FONTS;
    public static final AttributedCharacterIterator.Attribute BIDI_LEVEL;
    public static final AttributedCharacterIterator.Attribute XPOS;
    public static final AttributedCharacterIterator.Attribute YPOS;
    public static final AttributedCharacterIterator.Attribute TEXTPATH;
    public static final AttributedCharacterIterator.Attribute WRITING_MODE;
    public static final Integer WRITING_MODE_TTB;
    public static final Integer WRITING_MODE_RTL;
    public static final AttributedCharacterIterator.Attribute ANCHOR_TYPE;
    public static final Integer ADJUST_SPACING;
    public static final Integer ADJUST_ALL;
    public static final GVTAttributedCharacterIterator.TextAttribute ALT_GLYPH_HANDLER;
    static Set extendedAtts;
    protected static TextPainter singleton;
    
    public static TextPainter getInstance() {
        return StrokingTextPainter.singleton;
    }
    
    @Override
    public void paint(final TextNode node, final Graphics2D g2d) {
        final AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        if (aci == null) {
            return;
        }
        final List textRuns = this.getTextRuns(node, aci);
        this.paintDecorations(textRuns, g2d, 1);
        this.paintDecorations(textRuns, g2d, 4);
        this.paintTextRuns(textRuns, g2d);
        this.paintDecorations(textRuns, g2d, 2);
    }
    
    protected void printAttrs(final AttributedCharacterIterator aci) {
        aci.first();
        int start = aci.getBeginIndex();
        System.out.print("AttrRuns: ");
        while (aci.current() != '\uffff') {
            final int end = aci.getRunLimit();
            System.out.print("" + (end - start) + ", ");
            aci.setIndex(end);
            start = end;
        }
        System.out.println("");
    }
    
    public List getTextRuns(final TextNode node, final AttributedCharacterIterator aci) {
        List textRuns = node.getTextRuns();
        if (textRuns != null) {
            return textRuns;
        }
        final AttributedCharacterIterator[] chunkACIs = this.getTextChunkACIs(aci);
        textRuns = this.computeTextRuns(node, aci, chunkACIs);
        node.setTextRuns(textRuns);
        return node.getTextRuns();
    }
    
    public List computeTextRuns(final TextNode node, final AttributedCharacterIterator aci, final AttributedCharacterIterator[] chunkACIs) {
        final int[][] chunkCharMaps = new int[chunkACIs.length][];
        int chunkStart = aci.getBeginIndex();
        for (int i = 0; i < chunkACIs.length; ++i) {
            final BidiAttributedCharacterIterator iter = new BidiAttributedCharacterIterator(chunkACIs[i], this.fontRenderContext, chunkStart);
            chunkACIs[i] = iter;
            chunkCharMaps[i] = iter.getCharMap();
            chunkStart += chunkACIs[i].getEndIndex() - chunkACIs[i].getBeginIndex();
        }
        return this.computeTextRuns(node, aci, chunkACIs, chunkCharMaps);
    }
    
    protected List computeTextRuns(final TextNode node, final AttributedCharacterIterator aci, final AttributedCharacterIterator[] chunkACIs, final int[][] chunkCharMaps) {
        int chunkStart = aci.getBeginIndex();
        for (int i = 0; i < chunkACIs.length; ++i) {
            chunkACIs[i] = this.createModifiedACIForFontMatching(chunkACIs[i]);
            chunkStart += chunkACIs[i].getEndIndex() - chunkACIs[i].getBeginIndex();
        }
        final List perNodeRuns = new ArrayList();
        TextChunk prevChunk = null;
        int currentChunk = 0;
        Point2D location = node.getLocation();
        TextChunk chunk;
        do {
            chunkACIs[currentChunk].first();
            List perChunkRuns = new ArrayList();
            chunk = this.getTextChunk(node, chunkACIs[currentChunk], (int[])((chunkCharMaps != null) ? chunkCharMaps[currentChunk] : null), perChunkRuns, prevChunk);
            perChunkRuns = this.reorderTextRuns(chunk, perChunkRuns);
            chunkACIs[currentChunk].first();
            if (chunk != null) {
                location = this.adjustChunkOffsets(location, perChunkRuns, chunk);
            }
            perNodeRuns.addAll(perChunkRuns);
            prevChunk = chunk;
            ++currentChunk;
        } while (chunk != null && currentChunk < chunkACIs.length);
        return perNodeRuns;
    }
    
    protected List reorderTextRuns(final TextChunk chunk, final List runs) {
        return runs;
    }
    
    protected AttributedCharacterIterator[] getTextChunkACIs(final AttributedCharacterIterator aci) {
        final List aciList = new ArrayList();
        int chunkStartIndex = aci.getBeginIndex();
        aci.first();
        final Object writingMode = aci.getAttribute(StrokingTextPainter.WRITING_MODE);
        final boolean vertical = writingMode == StrokingTextPainter.WRITING_MODE_TTB;
        while (aci.setIndex(chunkStartIndex) != '\uffff') {
            TextPath prevTextPath = null;
            for (int start = chunkStartIndex, end = 0; aci.setIndex(start) != '\uffff'; start = end) {
                final TextPath textPath = (TextPath)aci.getAttribute(StrokingTextPainter.TEXTPATH);
                if (start != chunkStartIndex) {
                    if (vertical) {
                        final Float runY = (Float)aci.getAttribute(StrokingTextPainter.YPOS);
                        if (runY != null && !runY.isNaN()) {
                            break;
                        }
                    }
                    else {
                        final Float runX = (Float)aci.getAttribute(StrokingTextPainter.XPOS);
                        if (runX != null && !runX.isNaN()) {
                            break;
                        }
                    }
                    if (prevTextPath == null && textPath != null) {
                        break;
                    }
                    if (prevTextPath != null && textPath == null) {
                        break;
                    }
                }
                prevTextPath = textPath;
                if (aci.getAttribute(StrokingTextPainter.FLOW_PARAGRAPH) != null) {
                    end = aci.getRunLimit(StrokingTextPainter.FLOW_PARAGRAPH);
                    aci.setIndex(end);
                    break;
                }
                end = aci.getRunLimit(StrokingTextPainter.TEXT_COMPOUND_ID);
                if (start == chunkStartIndex) {
                    final TextNode.Anchor anchor = (TextNode.Anchor)aci.getAttribute(StrokingTextPainter.ANCHOR_TYPE);
                    if (anchor != TextNode.Anchor.START) {
                        if (vertical) {
                            final Float runY2 = (Float)aci.getAttribute(StrokingTextPainter.YPOS);
                            if (runY2 == null) {
                                continue;
                            }
                            if (runY2.isNaN()) {
                                continue;
                            }
                        }
                        else {
                            final Float runX2 = (Float)aci.getAttribute(StrokingTextPainter.XPOS);
                            if (runX2 == null) {
                                continue;
                            }
                            if (runX2.isNaN()) {
                                continue;
                            }
                        }
                        for (int i = start + 1; i < end; ++i) {
                            aci.setIndex(i);
                            if (vertical) {
                                final Float runY3 = (Float)aci.getAttribute(StrokingTextPainter.YPOS);
                                if (runY3 == null) {
                                    break;
                                }
                                if (runY3.isNaN()) {
                                    break;
                                }
                            }
                            else {
                                final Float runX3 = (Float)aci.getAttribute(StrokingTextPainter.XPOS);
                                if (runX3 == null) {
                                    break;
                                }
                                if (runX3.isNaN()) {
                                    break;
                                }
                            }
                            aciList.add(new AttributedCharacterSpanIterator(aci, i - 1, i));
                            chunkStartIndex = i;
                        }
                    }
                }
            }
            final int chunkEndIndex = aci.getIndex();
            aciList.add(new AttributedCharacterSpanIterator(aci, chunkStartIndex, chunkEndIndex));
            chunkStartIndex = chunkEndIndex;
        }
        final AttributedCharacterIterator[] aciArray = new AttributedCharacterIterator[aciList.size()];
        final Iterator iter = aciList.iterator();
        int j = 0;
        while (iter.hasNext()) {
            aciArray[j] = iter.next();
            ++j;
        }
        return aciArray;
    }
    
    protected AttributedCharacterIterator createModifiedACIForFontMatching(final AttributedCharacterIterator aci) {
        aci.first();
        AttributedString as = null;
        int asOff = 0;
        final int begin = aci.getBeginIndex();
        boolean moreChunks = true;
        int end = aci.getRunStart(StrokingTextPainter.TEXT_COMPOUND_ID);
        while (moreChunks) {
            int start = end;
            end = aci.getRunLimit(StrokingTextPainter.TEXT_COMPOUND_ID);
            final int aciLength = end - start;
            final List fonts = (List)aci.getAttribute(StrokingTextPainter.GVT_FONTS);
            float fontSize = 12.0f;
            final Float fsFloat = (Float)aci.getAttribute(TextAttribute.SIZE);
            if (fsFloat != null) {
                fontSize = fsFloat;
            }
            if (fonts.size() == 0) {
                fonts.add(this.getFontFamilyResolver().getDefault().deriveFont(fontSize, aci));
            }
            final boolean[] fontAssigned = new boolean[aciLength];
            if (as == null) {
                as = new AttributedString(aci);
            }
            GVTFont defaultFont = null;
            int numSet = 0;
            int firstUnset = start;
            for (final Object font1 : fonts) {
                int currentIndex = firstUnset;
                boolean firstUnsetSet = false;
                aci.setIndex(currentIndex);
                final GVTFont font2 = (GVTFont)font1;
                if (defaultFont == null) {
                    defaultFont = font2;
                }
                while (currentIndex < end) {
                    int displayUpToIndex = font2.canDisplayUpTo(aci, currentIndex, end);
                    final Object altGlyphElement = aci.getAttribute(StrokingTextPainter.ALT_GLYPH_HANDLER);
                    if (altGlyphElement != null) {
                        displayUpToIndex = -1;
                    }
                    if (displayUpToIndex == -1) {
                        displayUpToIndex = end;
                    }
                    if (displayUpToIndex <= currentIndex) {
                        if (!firstUnsetSet) {
                            firstUnset = currentIndex;
                            firstUnsetSet = true;
                        }
                        ++currentIndex;
                    }
                    else {
                        int runStart = -1;
                        for (int j = currentIndex; j < displayUpToIndex; ++j) {
                            if (fontAssigned[j - start]) {
                                if (runStart != -1) {
                                    as.addAttribute(StrokingTextPainter.GVT_FONT, font2, runStart - begin, j - begin);
                                    runStart = -1;
                                }
                            }
                            else if (runStart == -1) {
                                runStart = j;
                            }
                            fontAssigned[j - start] = true;
                            ++numSet;
                        }
                        if (runStart != -1) {
                            as.addAttribute(StrokingTextPainter.GVT_FONT, font2, runStart - begin, displayUpToIndex - begin);
                        }
                        currentIndex = displayUpToIndex + 1;
                    }
                }
                if (numSet == aciLength) {
                    break;
                }
            }
            int runStart2 = -1;
            GVTFontFamily prevFF = null;
            GVTFont prevF = defaultFont;
            for (int i = 0; i < aciLength; ++i) {
                if (fontAssigned[i]) {
                    if (runStart2 != -1) {
                        as.addAttribute(StrokingTextPainter.GVT_FONT, prevF, runStart2 + asOff, i + asOff);
                        runStart2 = -1;
                        prevF = null;
                        prevFF = null;
                    }
                }
                else {
                    final char c = aci.setIndex(start + i);
                    final GVTFontFamily fontFamily = this.getFontFamilyResolver().getFamilyThatCanDisplay(c);
                    if (runStart2 == -1) {
                        runStart2 = i;
                        prevFF = fontFamily;
                        if (prevFF == null) {
                            prevF = defaultFont;
                        }
                        else {
                            prevF = fontFamily.deriveFont(fontSize, aci);
                        }
                    }
                    else if (prevFF != fontFamily) {
                        as.addAttribute(StrokingTextPainter.GVT_FONT, prevF, runStart2 + asOff, i + asOff);
                        runStart2 = i;
                        prevFF = fontFamily;
                        if (prevFF == null) {
                            prevF = defaultFont;
                        }
                        else {
                            prevF = fontFamily.deriveFont(fontSize, aci);
                        }
                    }
                }
            }
            if (runStart2 != -1) {
                as.addAttribute(StrokingTextPainter.GVT_FONT, prevF, runStart2 + asOff, aciLength + asOff);
            }
            asOff += aciLength;
            if (aci.setIndex(end) == '\uffff') {
                moreChunks = false;
            }
            start = end;
        }
        if (as != null) {
            return as.getIterator();
        }
        return aci;
    }
    
    protected FontFamilyResolver getFontFamilyResolver() {
        return DefaultFontFamilyResolver.SINGLETON;
    }
    
    protected Set getTextRunBoundaryAttributes() {
        return StrokingTextPainter.extendedAtts;
    }
    
    protected TextChunk getTextChunk(final TextNode node, final AttributedCharacterIterator aci, final int[] charMap, final List textRuns, final TextChunk prevChunk) {
        int beginChunk = 0;
        if (prevChunk != null) {
            beginChunk = prevChunk.end;
        }
        int endChunk = beginChunk;
        final int begin = aci.getIndex();
        if (aci.current() == '\uffff') {
            return null;
        }
        final Point2D.Float offset = new Point2D.Float(0.0f, 0.0f);
        final Point2D.Float advance = new Point2D.Float(0.0f, 0.0f);
        boolean isChunkStart = true;
        TextSpanLayout layout = null;
        final Set textRunBoundaryAttributes = this.getTextRunBoundaryAttributes();
        while (true) {
            final int start = aci.getRunStart(textRunBoundaryAttributes);
            final int end = aci.getRunLimit(textRunBoundaryAttributes);
            final AttributedCharacterIterator runaci = new AttributedCharacterSpanIterator(aci, start, end);
            final int[] subCharMap = new int[end - start];
            if (charMap != null) {
                System.arraycopy(charMap, start - begin, subCharMap, 0, subCharMap.length);
            }
            else {
                for (int i = 0, n = subCharMap.length; i < n; ++i) {
                    subCharMap[i] = i;
                }
            }
            FontRenderContext frc = this.fontRenderContext;
            final RenderingHints rh = node.getRenderingHints();
            if (rh != null && rh.get(RenderingHints.KEY_TEXT_ANTIALIASING) == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF) {
                frc = this.aaOffFontRenderContext;
            }
            layout = this.getTextLayoutFactory().createTextLayout(runaci, subCharMap, offset, frc);
            textRuns.add(new TextRun(layout, runaci, isChunkStart));
            final Point2D layoutAdvance = layout.getAdvance2D();
            final Point2D.Float float1 = advance;
            float1.x += (float)layoutAdvance.getX();
            final Point2D.Float float2 = advance;
            float2.y += (float)layoutAdvance.getY();
            ++endChunk;
            if (aci.setIndex(end) == '\uffff') {
                break;
            }
            isChunkStart = false;
        }
        return new TextChunk(beginChunk, endChunk, advance);
    }
    
    protected Point2D adjustChunkOffsets(final Point2D location, final List textRuns, final TextChunk chunk) {
        final int numRuns = chunk.end - chunk.begin;
        TextRun r = textRuns.get(0);
        final int anchorType = r.getAnchorType();
        final Float length = r.getLength();
        final Integer lengthAdj = r.getLengthAdjust();
        boolean doAdjust = true;
        if (length == null || length.isNaN()) {
            doAdjust = false;
        }
        int numChars = 0;
        for (int i = 0; i < numRuns; ++i) {
            r = textRuns.get(i);
            final AttributedCharacterIterator aci = r.getACI();
            numChars += aci.getEndIndex() - aci.getBeginIndex();
        }
        if (lengthAdj == GVTAttributedCharacterIterator.TextAttribute.ADJUST_SPACING && numChars == 1) {
            doAdjust = false;
        }
        float xScale = 1.0f;
        float yScale = 1.0f;
        r = textRuns.get(numRuns - 1);
        TextSpanLayout layout = r.getLayout();
        final GVTGlyphMetrics lastMetrics = layout.getGlyphMetrics(layout.getGlyphCount() - 1);
        final GVTLineMetrics lastLineMetrics = layout.getLineMetrics();
        final Rectangle2D lastBounds = lastMetrics.getBounds2D();
        final float halfLeading = (lastMetrics.getVerticalAdvance() - (lastLineMetrics.getAscent() + lastLineMetrics.getDescent())) / 2.0f;
        final float lastW = (float)(lastBounds.getWidth() + lastBounds.getX());
        final float lastH = (float)(halfLeading + lastLineMetrics.getAscent() + (lastBounds.getHeight() + lastBounds.getY()));
        Point2D visualAdvance;
        if (!doAdjust) {
            visualAdvance = new Point2D.Float((float)chunk.advance.getX(), (float)(chunk.advance.getY() + lastH - lastMetrics.getVerticalAdvance()));
        }
        else {
            final Point2D advance = chunk.advance;
            if (layout.isVertical()) {
                if (lengthAdj == StrokingTextPainter.ADJUST_SPACING) {
                    yScale = (float)((length - lastH) / (advance.getY() - lastMetrics.getVerticalAdvance()));
                }
                else {
                    final double adv = advance.getY() + lastH - lastMetrics.getVerticalAdvance();
                    yScale = (float)(length / adv);
                }
                visualAdvance = new Point2D.Float(0.0f, length);
            }
            else {
                if (lengthAdj == StrokingTextPainter.ADJUST_SPACING) {
                    xScale = (float)((length - lastW) / (advance.getX() - lastMetrics.getHorizontalAdvance()));
                }
                else {
                    final double adv = advance.getX() + lastW - lastMetrics.getHorizontalAdvance();
                    xScale = (float)(length / adv);
                }
                visualAdvance = new Point2D.Float(length, 0.0f);
            }
            final Point2D.Float adv2 = new Point2D.Float(0.0f, 0.0f);
            for (int j = 0; j < numRuns; ++j) {
                r = textRuns.get(j);
                layout = r.getLayout();
                layout.setScale(xScale, yScale, lengthAdj == StrokingTextPainter.ADJUST_SPACING);
                final Point2D lAdv = layout.getAdvance2D();
                final Point2D.Float float1 = adv2;
                float1.x += (float)lAdv.getX();
                final Point2D.Float float2 = adv2;
                float2.y += (float)lAdv.getY();
            }
            chunk.advance = adv2;
        }
        float dx = 0.0f;
        float dy = 0.0f;
        switch (anchorType) {
            case 1: {
                dx = (float)(-visualAdvance.getX() / 2.0);
                dy = (float)(-visualAdvance.getY() / 2.0);
                break;
            }
            case 2: {
                dx = (float)(-visualAdvance.getX());
                dy = (float)(-visualAdvance.getY());
                break;
            }
        }
        r = textRuns.get(0);
        layout = r.getLayout();
        AttributedCharacterIterator runaci = r.getACI();
        runaci.first();
        final boolean vertical = layout.isVertical();
        Float runX = (Float)runaci.getAttribute(StrokingTextPainter.XPOS);
        Float runY = (Float)runaci.getAttribute(StrokingTextPainter.YPOS);
        TextPath textPath = (TextPath)runaci.getAttribute(StrokingTextPainter.TEXTPATH);
        float absX = (float)location.getX();
        float absY = (float)location.getY();
        float tpShiftX = 0.0f;
        float tpShiftY = 0.0f;
        if (runX != null && !runX.isNaN()) {
            absX = (tpShiftX = runX);
        }
        if (runY != null && !runY.isNaN()) {
            absY = (tpShiftY = runY);
        }
        if (vertical) {
            absY += dy;
            tpShiftY += dy;
            tpShiftX = 0.0f;
        }
        else {
            absX += dx;
            tpShiftX += dx;
            tpShiftY = 0.0f;
        }
        for (int k = 0; k < numRuns; ++k) {
            r = textRuns.get(k);
            layout = r.getLayout();
            runaci = r.getACI();
            runaci.first();
            textPath = (TextPath)runaci.getAttribute(StrokingTextPainter.TEXTPATH);
            if (vertical) {
                runX = (Float)runaci.getAttribute(StrokingTextPainter.XPOS);
                if (runX != null && !runX.isNaN()) {
                    absX = runX;
                }
            }
            else {
                runY = (Float)runaci.getAttribute(StrokingTextPainter.YPOS);
                if (runY != null && !runY.isNaN()) {
                    absY = runY;
                }
            }
            if (textPath == null) {
                layout.setOffset(new Point2D.Float(absX, absY));
                final Point2D ladv = layout.getAdvance2D();
                absX += (float)ladv.getX();
                absY += (float)ladv.getY();
            }
            else {
                layout.setOffset(new Point2D.Float(tpShiftX, tpShiftY));
                Point2D ladv = layout.getAdvance2D();
                tpShiftX += (float)ladv.getX();
                tpShiftY += (float)ladv.getY();
                ladv = layout.getTextPathAdvance();
                absX = (float)ladv.getX();
                absY = (float)ladv.getY();
            }
        }
        return new Point2D.Float(absX, absY);
    }
    
    protected void paintDecorations(final List textRuns, final Graphics2D g2d, final int decorationType) {
        Paint prevPaint = null;
        Paint prevStrokePaint = null;
        Stroke prevStroke = null;
        boolean prevVisible = true;
        Rectangle2D decorationRect = null;
        double yLoc = 0.0;
        double height = 0.0;
        for (final Object textRun1 : textRuns) {
            final TextRun textRun2 = (TextRun)textRun1;
            final AttributedCharacterIterator runaci = textRun2.getACI();
            runaci.first();
            Paint paint = null;
            Stroke stroke = null;
            Paint strokePaint = null;
            boolean visible = true;
            final TextPaintInfo tpi = (TextPaintInfo)runaci.getAttribute(StrokingTextPainter.PAINT_INFO);
            if (tpi != null) {
                visible = tpi.visible;
                if (tpi.composite != null) {
                    g2d.setComposite(tpi.composite);
                }
                switch (decorationType) {
                    case 1: {
                        paint = tpi.underlinePaint;
                        stroke = tpi.underlineStroke;
                        strokePaint = tpi.underlineStrokePaint;
                        break;
                    }
                    case 4: {
                        paint = tpi.overlinePaint;
                        stroke = tpi.overlineStroke;
                        strokePaint = tpi.overlineStrokePaint;
                        break;
                    }
                    case 2: {
                        paint = tpi.strikethroughPaint;
                        stroke = tpi.strikethroughStroke;
                        strokePaint = tpi.strikethroughStrokePaint;
                        break;
                    }
                    default: {
                        return;
                    }
                }
            }
            if (textRun2.isFirstRunInChunk()) {
                final Shape s = textRun2.getLayout().getDecorationOutline(decorationType);
                final Rectangle2D r2d = s.getBounds2D();
                yLoc = r2d.getY();
                height = r2d.getHeight();
            }
            if (textRun2.isFirstRunInChunk() || paint != prevPaint || stroke != prevStroke || strokePaint != prevStrokePaint || visible != prevVisible) {
                if (prevVisible && decorationRect != null) {
                    if (prevPaint != null) {
                        g2d.setPaint(prevPaint);
                        g2d.fill(decorationRect);
                    }
                    if (prevStroke != null && prevStrokePaint != null) {
                        g2d.setPaint(prevStrokePaint);
                        g2d.setStroke(prevStroke);
                        g2d.draw(decorationRect);
                    }
                }
                decorationRect = null;
            }
            if ((paint != null || strokePaint != null) && !textRun2.getLayout().isVertical() && !textRun2.getLayout().isOnATextPath()) {
                final Shape decorationShape = textRun2.getLayout().getDecorationOutline(decorationType);
                if (decorationRect == null) {
                    final Rectangle2D r2d = decorationShape.getBounds2D();
                    decorationRect = new Rectangle2D.Double(r2d.getX(), yLoc, r2d.getWidth(), height);
                }
                else {
                    final Rectangle2D bounds = decorationShape.getBounds2D();
                    final double minX = Math.min(decorationRect.getX(), bounds.getX());
                    final double maxX = Math.max(decorationRect.getMaxX(), bounds.getMaxX());
                    decorationRect.setRect(minX, yLoc, maxX - minX, height);
                }
            }
            prevPaint = paint;
            prevStroke = stroke;
            prevStrokePaint = strokePaint;
            prevVisible = visible;
        }
        if (prevVisible && decorationRect != null) {
            if (prevPaint != null) {
                g2d.setPaint(prevPaint);
                g2d.fill(decorationRect);
            }
            if (prevStroke != null && prevStrokePaint != null) {
                g2d.setPaint(prevStrokePaint);
                g2d.setStroke(prevStroke);
                g2d.draw(decorationRect);
            }
        }
    }
    
    protected void paintTextRuns(final List textRuns, final Graphics2D g2d) {
        for (final Object textRun1 : textRuns) {
            final TextRun textRun2 = (TextRun)textRun1;
            final AttributedCharacterIterator runaci = textRun2.getACI();
            runaci.first();
            final TextPaintInfo tpi = (TextPaintInfo)runaci.getAttribute(StrokingTextPainter.PAINT_INFO);
            if (tpi != null && tpi.composite != null) {
                g2d.setComposite(tpi.composite);
            }
            textRun2.getLayout().draw(g2d);
        }
    }
    
    @Override
    public Shape getOutline(final TextNode node) {
        GeneralPath outline = null;
        final AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        final List textRuns = this.getTextRuns(node, aci);
        for (final Object textRun1 : textRuns) {
            final TextRun textRun2 = (TextRun)textRun1;
            final TextSpanLayout textRunLayout = textRun2.getLayout();
            final GeneralPath textRunOutline = new GeneralPath(textRunLayout.getOutline());
            if (outline == null) {
                outline = textRunOutline;
            }
            else {
                outline.setWindingRule(1);
                outline.append(textRunOutline, false);
            }
        }
        final Shape underline = this.getDecorationOutline(textRuns, 1);
        final Shape strikeThrough = this.getDecorationOutline(textRuns, 2);
        final Shape overline = this.getDecorationOutline(textRuns, 4);
        if (underline != null) {
            if (outline == null) {
                outline = new GeneralPath(underline);
            }
            else {
                outline.setWindingRule(1);
                outline.append(underline, false);
            }
        }
        if (strikeThrough != null) {
            if (outline == null) {
                outline = new GeneralPath(strikeThrough);
            }
            else {
                outline.setWindingRule(1);
                outline.append(strikeThrough, false);
            }
        }
        if (overline != null) {
            if (outline == null) {
                outline = new GeneralPath(overline);
            }
            else {
                outline.setWindingRule(1);
                outline.append(overline, false);
            }
        }
        return outline;
    }
    
    @Override
    public Rectangle2D getBounds2D(final TextNode node) {
        final AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        final List textRuns = this.getTextRuns(node, aci);
        Rectangle2D bounds = null;
        for (final Object textRun1 : textRuns) {
            final TextRun textRun2 = (TextRun)textRun1;
            final TextSpanLayout textRunLayout = textRun2.getLayout();
            final Rectangle2D runBounds = textRunLayout.getBounds2D();
            if (runBounds != null) {
                if (bounds == null) {
                    bounds = runBounds;
                }
                else {
                    bounds.add(runBounds);
                }
            }
        }
        final Shape underline = this.getDecorationStrokeOutline(textRuns, 1);
        if (underline != null) {
            if (bounds == null) {
                bounds = underline.getBounds2D();
            }
            else {
                bounds.add(underline.getBounds2D());
            }
        }
        final Shape strikeThrough = this.getDecorationStrokeOutline(textRuns, 2);
        if (strikeThrough != null) {
            if (bounds == null) {
                bounds = strikeThrough.getBounds2D();
            }
            else {
                bounds.add(strikeThrough.getBounds2D());
            }
        }
        final Shape overline = this.getDecorationStrokeOutline(textRuns, 4);
        if (overline != null) {
            if (bounds == null) {
                bounds = overline.getBounds2D();
            }
            else {
                bounds.add(overline.getBounds2D());
            }
        }
        return bounds;
    }
    
    protected Shape getDecorationOutline(final List textRuns, final int decorationType) {
        GeneralPath outline = null;
        Paint prevPaint = null;
        Paint prevStrokePaint = null;
        Stroke prevStroke = null;
        Rectangle2D decorationRect = null;
        double yLoc = 0.0;
        double height = 0.0;
        for (final Object textRun1 : textRuns) {
            final TextRun textRun2 = (TextRun)textRun1;
            final AttributedCharacterIterator runaci = textRun2.getACI();
            runaci.first();
            Paint paint = null;
            Stroke stroke = null;
            Paint strokePaint = null;
            final TextPaintInfo tpi = (TextPaintInfo)runaci.getAttribute(StrokingTextPainter.PAINT_INFO);
            if (tpi != null) {
                switch (decorationType) {
                    case 1: {
                        paint = tpi.underlinePaint;
                        stroke = tpi.underlineStroke;
                        strokePaint = tpi.underlineStrokePaint;
                        break;
                    }
                    case 4: {
                        paint = tpi.overlinePaint;
                        stroke = tpi.overlineStroke;
                        strokePaint = tpi.overlineStrokePaint;
                        break;
                    }
                    case 2: {
                        paint = tpi.strikethroughPaint;
                        stroke = tpi.strikethroughStroke;
                        strokePaint = tpi.strikethroughStrokePaint;
                        break;
                    }
                    default: {
                        return null;
                    }
                }
            }
            if (textRun2.isFirstRunInChunk()) {
                final Shape s = textRun2.getLayout().getDecorationOutline(decorationType);
                final Rectangle2D r2d = s.getBounds2D();
                yLoc = r2d.getY();
                height = r2d.getHeight();
            }
            if ((textRun2.isFirstRunInChunk() || paint != prevPaint || stroke != prevStroke || strokePaint != prevStrokePaint) && decorationRect != null) {
                if (outline == null) {
                    outline = new GeneralPath(decorationRect);
                }
                else {
                    outline.append(decorationRect, false);
                }
                decorationRect = null;
            }
            if ((paint != null || strokePaint != null) && !textRun2.getLayout().isVertical() && !textRun2.getLayout().isOnATextPath()) {
                final Shape decorationShape = textRun2.getLayout().getDecorationOutline(decorationType);
                if (decorationRect == null) {
                    final Rectangle2D r2d = decorationShape.getBounds2D();
                    decorationRect = new Rectangle2D.Double(r2d.getX(), yLoc, r2d.getWidth(), height);
                }
                else {
                    final Rectangle2D bounds = decorationShape.getBounds2D();
                    final double minX = Math.min(decorationRect.getX(), bounds.getX());
                    final double maxX = Math.max(decorationRect.getMaxX(), bounds.getMaxX());
                    decorationRect.setRect(minX, yLoc, maxX - minX, height);
                }
            }
            prevPaint = paint;
            prevStroke = stroke;
            prevStrokePaint = strokePaint;
        }
        if (decorationRect != null) {
            if (outline == null) {
                outline = new GeneralPath(decorationRect);
            }
            else {
                outline.append(decorationRect, false);
            }
        }
        return outline;
    }
    
    protected Shape getDecorationStrokeOutline(final List textRuns, final int decorationType) {
        GeneralPath outline = null;
        Paint prevPaint = null;
        Paint prevStrokePaint = null;
        Stroke prevStroke = null;
        Rectangle2D decorationRect = null;
        double yLoc = 0.0;
        double height = 0.0;
        for (final Object textRun1 : textRuns) {
            final TextRun textRun2 = (TextRun)textRun1;
            final AttributedCharacterIterator runaci = textRun2.getACI();
            runaci.first();
            Paint paint = null;
            Stroke stroke = null;
            Paint strokePaint = null;
            final TextPaintInfo tpi = (TextPaintInfo)runaci.getAttribute(StrokingTextPainter.PAINT_INFO);
            if (tpi != null) {
                switch (decorationType) {
                    case 1: {
                        paint = tpi.underlinePaint;
                        stroke = tpi.underlineStroke;
                        strokePaint = tpi.underlineStrokePaint;
                        break;
                    }
                    case 4: {
                        paint = tpi.overlinePaint;
                        stroke = tpi.overlineStroke;
                        strokePaint = tpi.overlineStrokePaint;
                        break;
                    }
                    case 2: {
                        paint = tpi.strikethroughPaint;
                        stroke = tpi.strikethroughStroke;
                        strokePaint = tpi.strikethroughStrokePaint;
                        break;
                    }
                    default: {
                        return null;
                    }
                }
            }
            if (textRun2.isFirstRunInChunk()) {
                final Shape s = textRun2.getLayout().getDecorationOutline(decorationType);
                final Rectangle2D r2d = s.getBounds2D();
                yLoc = r2d.getY();
                height = r2d.getHeight();
            }
            if ((textRun2.isFirstRunInChunk() || paint != prevPaint || stroke != prevStroke || strokePaint != prevStrokePaint) && decorationRect != null) {
                Shape s = null;
                if (prevStroke != null && prevStrokePaint != null) {
                    s = prevStroke.createStrokedShape(decorationRect);
                }
                else if (prevPaint != null) {
                    s = decorationRect;
                }
                if (s != null) {
                    if (outline == null) {
                        outline = new GeneralPath(s);
                    }
                    else {
                        outline.append(s, false);
                    }
                }
                decorationRect = null;
            }
            if ((paint != null || strokePaint != null) && !textRun2.getLayout().isVertical() && !textRun2.getLayout().isOnATextPath()) {
                final Shape decorationShape = textRun2.getLayout().getDecorationOutline(decorationType);
                if (decorationRect == null) {
                    final Rectangle2D r2d = decorationShape.getBounds2D();
                    decorationRect = new Rectangle2D.Double(r2d.getX(), yLoc, r2d.getWidth(), height);
                }
                else {
                    final Rectangle2D bounds = decorationShape.getBounds2D();
                    final double minX = Math.min(decorationRect.getX(), bounds.getX());
                    final double maxX = Math.max(decorationRect.getMaxX(), bounds.getMaxX());
                    decorationRect.setRect(minX, yLoc, maxX - minX, height);
                }
            }
            prevPaint = paint;
            prevStroke = stroke;
            prevStrokePaint = strokePaint;
        }
        if (decorationRect != null) {
            Shape s2 = null;
            if (prevStroke != null && prevStrokePaint != null) {
                s2 = prevStroke.createStrokedShape(decorationRect);
            }
            else if (prevPaint != null) {
                s2 = decorationRect;
            }
            if (s2 != null) {
                if (outline == null) {
                    outline = new GeneralPath(s2);
                }
                else {
                    outline.append(s2, false);
                }
            }
        }
        return outline;
    }
    
    @Override
    public Mark getMark(final TextNode node, final int index, final boolean leadingEdge) {
        final AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        if (index < aci.getBeginIndex() || index > aci.getEndIndex()) {
            return null;
        }
        final TextHit textHit = new TextHit(index, leadingEdge);
        return new BasicMark(node, textHit);
    }
    
    @Override
    protected Mark hitTest(final double x, final double y, final TextNode node) {
        final AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        final List textRuns = this.getTextRuns(node, aci);
        if (textRuns != null) {
            for (final Object textRun1 : textRuns) {
                final TextRun textRun2 = (TextRun)textRun1;
                final TextSpanLayout layout = textRun2.getLayout();
                final TextHit textHit = layout.hitTestChar((float)x, (float)y);
                final Rectangle2D bounds = layout.getBounds2D();
                if (textHit != null && bounds != null && bounds.contains(x, y)) {
                    return new BasicMark(node, textHit);
                }
            }
        }
        return null;
    }
    
    @Override
    public Mark selectFirst(final TextNode node) {
        final AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        final TextHit textHit = new TextHit(aci.getBeginIndex(), false);
        return new BasicMark(node, textHit);
    }
    
    @Override
    public Mark selectLast(final TextNode node) {
        final AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        final TextHit textHit = new TextHit(aci.getEndIndex() - 1, false);
        return new BasicMark(node, textHit);
    }
    
    @Override
    public int[] getSelected(final Mark startMark, final Mark finishMark) {
        if (startMark == null || finishMark == null) {
            return null;
        }
        BasicMark start;
        BasicMark finish;
        try {
            start = (BasicMark)startMark;
            finish = (BasicMark)finishMark;
        }
        catch (ClassCastException cce) {
            throw new RuntimeException("This Mark was not instantiated by this TextPainter class!");
        }
        final TextNode textNode = start.getTextNode();
        if (textNode == null) {
            return null;
        }
        if (textNode != finish.getTextNode()) {
            throw new RuntimeException("Markers are from different TextNodes!");
        }
        final AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        final int[] result = { start.getHit().getCharIndex(), finish.getHit().getCharIndex() };
        final List textRuns = this.getTextRuns(textNode, aci);
        final Iterator trI = textRuns.iterator();
        int startGlyphIndex = -1;
        int endGlyphIndex = -1;
        TextSpanLayout startLayout = null;
        TextSpanLayout endLayout = null;
        while (trI.hasNext()) {
            final TextRun tr = trI.next();
            final TextSpanLayout tsl = tr.getLayout();
            if (startGlyphIndex == -1) {
                startGlyphIndex = tsl.getGlyphIndex(result[0]);
                if (startGlyphIndex != -1) {
                    startLayout = tsl;
                }
            }
            if (endGlyphIndex == -1) {
                endGlyphIndex = tsl.getGlyphIndex(result[1]);
                if (endGlyphIndex != -1) {
                    endLayout = tsl;
                }
            }
            if (startGlyphIndex != -1 && endGlyphIndex != -1) {
                break;
            }
        }
        if (startLayout == null || endLayout == null) {
            return null;
        }
        final int startCharCount = startLayout.getCharacterCount(startGlyphIndex, startGlyphIndex);
        final int endCharCount = endLayout.getCharacterCount(endGlyphIndex, endGlyphIndex);
        if (startCharCount > 1) {
            if (result[0] > result[1] && startLayout.isLeftToRight()) {
                final int[] array = result;
                final int n = 0;
                array[n] += startCharCount - 1;
            }
            else if (result[1] > result[0] && !startLayout.isLeftToRight()) {
                final int[] array2 = result;
                final int n2 = 0;
                array2[n2] -= startCharCount - 1;
            }
        }
        if (endCharCount > 1) {
            if (result[1] > result[0] && endLayout.isLeftToRight()) {
                final int[] array3 = result;
                final int n3 = 1;
                array3[n3] += endCharCount - 1;
            }
            else if (result[0] > result[1] && !endLayout.isLeftToRight()) {
                final int[] array4 = result;
                final int n4 = 1;
                array4[n4] -= endCharCount - 1;
            }
        }
        return result;
    }
    
    @Override
    public Shape getHighlightShape(final Mark beginMark, final Mark endMark) {
        if (beginMark == null || endMark == null) {
            return null;
        }
        BasicMark begin;
        BasicMark end;
        try {
            begin = (BasicMark)beginMark;
            end = (BasicMark)endMark;
        }
        catch (ClassCastException cce) {
            throw new RuntimeException("This Mark was not instantiated by this TextPainter class!");
        }
        final TextNode textNode = begin.getTextNode();
        if (textNode == null) {
            return null;
        }
        if (textNode != end.getTextNode()) {
            throw new RuntimeException("Markers are from different TextNodes!");
        }
        final AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        int beginIndex = begin.getHit().getCharIndex();
        int endIndex = end.getHit().getCharIndex();
        if (beginIndex > endIndex) {
            final BasicMark tmpMark = begin;
            begin = end;
            end = tmpMark;
            final int tmpIndex = beginIndex;
            beginIndex = endIndex;
            endIndex = tmpIndex;
        }
        final List textRuns = this.getTextRuns(textNode, aci);
        final GeneralPath highlightedShape = new GeneralPath();
        for (final Object textRun1 : textRuns) {
            final TextRun textRun2 = (TextRun)textRun1;
            final TextSpanLayout layout = textRun2.getLayout();
            final Shape layoutHighlightedShape = layout.getHighlightShape(beginIndex, endIndex);
            if (layoutHighlightedShape != null && !layoutHighlightedShape.getBounds().isEmpty()) {
                highlightedShape.append(layoutHighlightedShape, false);
            }
        }
        return highlightedShape;
    }
    
    static {
        PAINT_INFO = GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO;
        FLOW_REGIONS = GVTAttributedCharacterIterator.TextAttribute.FLOW_REGIONS;
        FLOW_PARAGRAPH = GVTAttributedCharacterIterator.TextAttribute.FLOW_PARAGRAPH;
        TEXT_COMPOUND_ID = GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_ID;
        GVT_FONT = GVTAttributedCharacterIterator.TextAttribute.GVT_FONT;
        GVT_FONTS = GVTAttributedCharacterIterator.TextAttribute.GVT_FONTS;
        BIDI_LEVEL = GVTAttributedCharacterIterator.TextAttribute.BIDI_LEVEL;
        XPOS = GVTAttributedCharacterIterator.TextAttribute.X;
        YPOS = GVTAttributedCharacterIterator.TextAttribute.Y;
        TEXTPATH = GVTAttributedCharacterIterator.TextAttribute.TEXTPATH;
        WRITING_MODE = GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE;
        WRITING_MODE_TTB = GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE_TTB;
        WRITING_MODE_RTL = GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE_RTL;
        ANCHOR_TYPE = GVTAttributedCharacterIterator.TextAttribute.ANCHOR_TYPE;
        ADJUST_SPACING = GVTAttributedCharacterIterator.TextAttribute.ADJUST_SPACING;
        ADJUST_ALL = GVTAttributedCharacterIterator.TextAttribute.ADJUST_ALL;
        ALT_GLYPH_HANDLER = GVTAttributedCharacterIterator.TextAttribute.ALT_GLYPH_HANDLER;
        (StrokingTextPainter.extendedAtts = new HashSet()).add(StrokingTextPainter.FLOW_PARAGRAPH);
        StrokingTextPainter.extendedAtts.add(StrokingTextPainter.TEXT_COMPOUND_ID);
        StrokingTextPainter.extendedAtts.add(StrokingTextPainter.GVT_FONT);
        StrokingTextPainter.singleton = new StrokingTextPainter();
    }
    
    public static class TextChunk
    {
        public int begin;
        public int end;
        public Point2D advance;
        
        public TextChunk(final int begin, final int end, final Point2D advance) {
            this.begin = begin;
            this.end = end;
            this.advance = new Point2D.Float((float)advance.getX(), (float)advance.getY());
        }
    }
    
    public static class TextRun
    {
        protected AttributedCharacterIterator aci;
        protected TextSpanLayout layout;
        protected int anchorType;
        protected boolean firstRunInChunk;
        protected Float length;
        protected Integer lengthAdjust;
        private int level;
        private int reversals;
        
        public TextRun(final TextSpanLayout layout, final AttributedCharacterIterator aci, final boolean firstRunInChunk) {
            this.layout = layout;
            (this.aci = aci).first();
            this.firstRunInChunk = firstRunInChunk;
            this.anchorType = 0;
            final TextNode.Anchor anchor = (TextNode.Anchor)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.ANCHOR_TYPE);
            if (anchor != null) {
                this.anchorType = anchor.getType();
            }
            if (aci.getAttribute(StrokingTextPainter.WRITING_MODE) == StrokingTextPainter.WRITING_MODE_RTL) {
                if (this.anchorType == 0) {
                    this.anchorType = 2;
                }
                else if (this.anchorType == 2) {
                    this.anchorType = 0;
                }
            }
            this.length = (Float)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.BBOX_WIDTH);
            this.lengthAdjust = (Integer)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.LENGTH_ADJUST);
            final Integer level = (Integer)aci.getAttribute(StrokingTextPainter.BIDI_LEVEL);
            if (level != null) {
                this.level = level;
            }
            else {
                this.level = -1;
            }
        }
        
        public AttributedCharacterIterator getACI() {
            return this.aci;
        }
        
        public TextSpanLayout getLayout() {
            return this.layout;
        }
        
        public int getAnchorType() {
            return this.anchorType;
        }
        
        public Float getLength() {
            return this.length;
        }
        
        public Integer getLengthAdjust() {
            return this.lengthAdjust;
        }
        
        public boolean isFirstRunInChunk() {
            return this.firstRunInChunk;
        }
        
        public int getBidiLevel() {
            return this.level;
        }
        
        public void reverse() {
            ++this.reversals;
        }
        
        public void maybeReverseGlyphs(final boolean mirror) {
            if ((this.reversals & 0x1) == 0x1) {
                this.layout.maybeReverse(mirror);
            }
        }
    }
}
