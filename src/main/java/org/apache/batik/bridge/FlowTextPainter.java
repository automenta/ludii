// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.font.TextAttribute;
import java.util.HashSet;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.flow.TextLineBreaks;
import org.apache.batik.gvt.font.GVTLineMetrics;
import java.text.CharacterIterator;
import org.apache.batik.gvt.font.GVTFont;
import java.util.Arrays;
import org.apache.batik.gvt.flow.GlyphGroupInfo;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.flow.LineInfo;
import org.apache.batik.gvt.flow.FlowRegions;
import org.apache.batik.gvt.flow.RegionInfo;
import org.apache.batik.gvt.font.MultiGlyphVector;
import java.util.LinkedList;
import org.apache.batik.gvt.flow.BlockInfo;
import org.apache.batik.gvt.flow.WordInfo;
import java.awt.font.FontRenderContext;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.text.AttributedCharacterIterator;

public class FlowTextPainter extends StrokingTextPainter
{
    protected static TextPainter singleton;
    public static final char SOFT_HYPHEN = '\u00ad';
    public static final char ZERO_WIDTH_SPACE = '\u200b';
    public static final char ZERO_WIDTH_JOINER = '\u200d';
    public static final char SPACE = ' ';
    public static final AttributedCharacterIterator.Attribute WORD_LIMIT;
    public static final AttributedCharacterIterator.Attribute FLOW_REGIONS;
    public static final AttributedCharacterIterator.Attribute FLOW_LINE_BREAK;
    public static final AttributedCharacterIterator.Attribute LINE_HEIGHT;
    public static final AttributedCharacterIterator.Attribute GVT_FONT;
    protected static Set szAtts;
    
    public static TextPainter getInstance() {
        return FlowTextPainter.singleton;
    }
    
    @Override
    public List getTextRuns(final TextNode node, final AttributedCharacterIterator aci) {
        List textRuns = node.getTextRuns();
        if (textRuns != null) {
            return textRuns;
        }
        final AttributedCharacterIterator[] chunkACIs = this.getTextChunkACIs(aci);
        textRuns = this.computeTextRuns(node, aci, chunkACIs);
        aci.first();
        final List rgns = (List)aci.getAttribute(FlowTextPainter.FLOW_REGIONS);
        if (rgns != null) {
            final Iterator i = textRuns.iterator();
            final List chunkLayouts = new ArrayList();
            TextRun tr = i.next();
            List layouts = new ArrayList();
            chunkLayouts.add(layouts);
            layouts.add(tr.getLayout());
            while (i.hasNext()) {
                tr = i.next();
                if (tr.isFirstRunInChunk()) {
                    layouts = new ArrayList();
                    chunkLayouts.add(layouts);
                }
                layouts.add(tr.getLayout());
            }
            textWrap(chunkACIs, chunkLayouts, rgns, this.fontRenderContext);
        }
        node.setTextRuns(textRuns);
        return node.getTextRuns();
    }
    
    public static boolean textWrap(final AttributedCharacterIterator[] acis, final List chunkLayouts, final List flowRects, final FontRenderContext frc) {
        final WordInfo[][] wordInfos = new WordInfo[acis.length][];
        final Iterator clIter = chunkLayouts.iterator();
        float prevBotMargin = 0.0f;
        int numWords = 0;
        final BlockInfo[] blockInfos = new BlockInfo[acis.length];
        final float[] topSkip = new float[acis.length];
        int chunk = 0;
        while (clIter.hasNext()) {
            final AttributedCharacterIterator aci = acis[chunk];
            final List gvl = new LinkedList();
            final List layouts = clIter.next();
            for (final Object layout : layouts) {
                final GlyphLayout gl = (GlyphLayout)layout;
                gvl.add(gl.getGlyphVector());
            }
            final GVTGlyphVector gv = new MultiGlyphVector(gvl);
            wordInfos[chunk] = doWordAnalysis(gv, aci, numWords, frc);
            aci.first();
            final BlockInfo bi = (BlockInfo)aci.getAttribute(FlowTextPainter.FLOW_PARAGRAPH);
            bi.initLineInfo(frc);
            blockInfos[chunk] = bi;
            if (prevBotMargin > bi.getTopMargin()) {
                topSkip[chunk] = prevBotMargin;
            }
            else {
                topSkip[chunk] = bi.getTopMargin();
            }
            prevBotMargin = bi.getBottomMargin();
            numWords += wordInfos[chunk].length;
            ++chunk;
        }
        final Iterator frIter = flowRects.iterator();
        RegionInfo currentRegion = null;
        int currWord = 0;
        int chunk2 = 0;
        final List lineInfos = new LinkedList();
        while (frIter.hasNext()) {
            currentRegion = frIter.next();
            final FlowRegions fr = new FlowRegions(currentRegion.getShape());
            while (chunk2 < wordInfos.length) {
                final WordInfo[] chunkInfo = wordInfos[chunk2];
                final BlockInfo bi2 = blockInfos[chunk2];
                WordInfo wi = chunkInfo[currWord];
                Object flowLine = wi.getFlowLine();
                double lh = Math.max(wi.getLineHeight(), bi2.getLineHeight());
                LineInfo li = new LineInfo(fr, bi2, true);
                double newY = li.getCurrentY() + topSkip[chunk2];
                topSkip[chunk2] = 0.0f;
                if (li.gotoY(newY)) {
                    break;
                }
                while (!li.addWord(wi)) {
                    newY = li.getCurrentY() + lh * 0.1;
                    if (li.gotoY(newY)) {
                        break;
                    }
                }
                if (fr.done()) {
                    break;
                }
                ++currWord;
                while (currWord < chunkInfo.length) {
                    wi = chunkInfo[currWord];
                    if (wi.getFlowLine() != flowLine || !li.addWord(wi)) {
                        li.layout();
                        lineInfos.add(li);
                        li = null;
                        flowLine = wi.getFlowLine();
                        lh = Math.max(wi.getLineHeight(), bi2.getLineHeight());
                        if (!fr.newLine(lh)) {
                            break;
                        }
                        li = new LineInfo(fr, bi2, false);
                        while (!li.addWord(wi)) {
                            newY = li.getCurrentY() + lh * 0.1;
                            if (li.gotoY(newY)) {
                                break;
                            }
                        }
                        if (fr.done()) {
                            break;
                        }
                    }
                    ++currWord;
                }
                if (li != null) {
                    li.setParaEnd(true);
                    li.layout();
                }
                if (fr.done()) {
                    break;
                }
                ++chunk2;
                currWord = 0;
                if (bi2.isFlowRegionBreak()) {
                    break;
                }
                if (!fr.newLine(lh)) {
                    break;
                }
            }
            if (chunk2 == wordInfos.length) {
                break;
            }
        }
        final boolean overflow = chunk2 < wordInfos.length;
        while (chunk2 < wordInfos.length) {
            for (WordInfo[] chunkInfo = wordInfos[chunk2]; currWord < chunkInfo.length; ++currWord) {
                final WordInfo wi2 = chunkInfo[currWord];
                for (int numGG = wi2.getNumGlyphGroups(), gg = 0; gg < numGG; ++gg) {
                    final GlyphGroupInfo ggi = wi2.getGlyphGroup(gg);
                    final GVTGlyphVector gv2 = ggi.getGlyphVector();
                    for (int end = ggi.getEnd(), g = ggi.getStart(); g <= end; ++g) {
                        gv2.setGlyphVisible(g, false);
                    }
                }
            }
            ++chunk2;
            currWord = 0;
        }
        return overflow;
    }
    
    static int[] allocWordMap(final int[] wordMap, int sz) {
        if (wordMap != null) {
            if (sz <= wordMap.length) {
                return wordMap;
            }
            if (sz < wordMap.length * 2) {
                sz = wordMap.length * 2;
            }
        }
        final int[] ret = new int[sz];
        int ext = (wordMap != null) ? wordMap.length : 0;
        if (sz < ext) {
            ext = sz;
        }
        if (ext != 0) {
            System.arraycopy(wordMap, 0, ret, 0, ext);
        }
        Arrays.fill(ret, ext, sz, -1);
        return ret;
    }
    
    static WordInfo[] doWordAnalysis(final GVTGlyphVector gv, final AttributedCharacterIterator aci, final int numWords, final FontRenderContext frc) {
        final int numGlyphs = gv.getNumGlyphs();
        final int[] glyphWords = new int[numGlyphs];
        int[] wordMap = allocWordMap(null, 10);
        int maxWord = 0;
        int aciIdx = aci.getBeginIndex();
        for (int i = 0; i < numGlyphs; ++i) {
            final int cnt = gv.getCharacterCount(i, i);
            aci.setIndex(aciIdx);
            Integer integer = (Integer)aci.getAttribute(FlowTextPainter.WORD_LIMIT);
            int minWord = integer - numWords;
            if (minWord > maxWord) {
                maxWord = minWord;
                wordMap = allocWordMap(wordMap, maxWord + 1);
            }
            ++aciIdx;
            for (int c = 1; c < cnt; ++c) {
                aci.setIndex(aciIdx);
                integer = (Integer)aci.getAttribute(FlowTextPainter.WORD_LIMIT);
                final int cWord = integer - numWords;
                if (cWord > maxWord) {
                    maxWord = cWord;
                    wordMap = allocWordMap(wordMap, maxWord + 1);
                }
                if (cWord < minWord) {
                    wordMap[minWord] = cWord;
                    minWord = cWord;
                }
                else if (cWord > minWord) {
                    wordMap[cWord] = minWord;
                }
                ++aciIdx;
            }
            glyphWords[i] = minWord;
        }
        int words = 0;
        WordInfo[] cWordMap = new WordInfo[maxWord + 1];
        for (int j = 0; j <= maxWord; ++j) {
            int nw = wordMap[j];
            if (nw == -1) {
                cWordMap[j] = new WordInfo(words++);
            }
            else {
                int word;
                for (word = nw, nw = wordMap[j]; nw != -1; nw = wordMap[word]) {
                    word = nw;
                }
                wordMap[j] = word;
                cWordMap[j] = cWordMap[word];
            }
        }
        wordMap = null;
        final WordInfo[] wordInfos = new WordInfo[words];
        for (int k = 0; k <= maxWord; ++k) {
            final WordInfo wi = cWordMap[k];
            wordInfos[wi.getIndex()] = cWordMap[k];
        }
        aciIdx = aci.getBeginIndex();
        final int aciEnd = aci.getEndIndex();
        char ch = aci.setIndex(aciIdx);
        int aciWordStart = aciIdx;
        GVTFont gvtFont = (GVTFont)aci.getAttribute(FlowTextPainter.GVT_FONT);
        float lineHeight = 1.0f;
        final Float lineHeightFloat = (Float)aci.getAttribute(FlowTextPainter.LINE_HEIGHT);
        if (lineHeightFloat != null) {
            lineHeight = lineHeightFloat;
        }
        int runLimit = aci.getRunLimit(FlowTextPainter.szAtts);
        WordInfo prevWI = null;
        final float[] lastAdvAdj = new float[numGlyphs];
        final float[] advAdj = new float[numGlyphs];
        final boolean[] hideLast = new boolean[numGlyphs];
        final boolean[] hide = new boolean[numGlyphs];
        final boolean[] space = new boolean[numGlyphs];
        final float[] glyphPos = gv.getGlyphPositions(0, numGlyphs + 1, null);
        for (int l = 0; l < numGlyphs; ++l) {
            final char pch = ch;
            ch = aci.setIndex(aciIdx);
            final Integer integer2 = (Integer)aci.getAttribute(FlowTextPainter.WORD_LIMIT);
            final WordInfo theWI = cWordMap[integer2 - numWords];
            if (theWI.getFlowLine() == null) {
                theWI.setFlowLine(aci.getAttribute(FlowTextPainter.FLOW_LINE_BREAK));
            }
            if (prevWI == null) {
                prevWI = theWI;
            }
            else if (prevWI != theWI) {
                final GVTLineMetrics lm = gvtFont.getLineMetrics(aci, aciWordStart, aciIdx, frc);
                prevWI.addLineMetrics(gvtFont, lm);
                prevWI.addLineHeight(lineHeight);
                aciWordStart = aciIdx;
                prevWI = theWI;
            }
            final int chCnt = gv.getCharacterCount(l, l);
            if (chCnt == 1) {
                switch (ch) {
                    case '\u00ad': {
                        hideLast[l] = true;
                        final char nch = aci.next();
                        aci.previous();
                        final float kern = gvtFont.getHKern(pch, nch);
                        advAdj[l] = -(glyphPos[2 * l + 2] - glyphPos[2 * l] + kern);
                        break;
                    }
                    case '\u200d': {
                        hide[l] = true;
                        break;
                    }
                    case '\u200b': {
                        hide[l] = true;
                        break;
                    }
                    case ' ': {
                        space[l] = true;
                        final char nch = aci.next();
                        aci.previous();
                        final float kern = gvtFont.getHKern(pch, nch);
                        lastAdvAdj[l] = -(glyphPos[2 * l + 2] - glyphPos[2 * l] + kern);
                        break;
                    }
                }
            }
            aciIdx += chCnt;
            if (aciIdx > runLimit && aciIdx < aciEnd) {
                final GVTLineMetrics lm2 = gvtFont.getLineMetrics(aci, aciWordStart, runLimit, frc);
                prevWI.addLineMetrics(gvtFont, lm2);
                prevWI.addLineHeight(lineHeight);
                prevWI = null;
                aciWordStart = aciIdx;
                aci.setIndex(aciIdx);
                gvtFont = (GVTFont)aci.getAttribute(FlowTextPainter.GVT_FONT);
                final Float f = (Float)aci.getAttribute(FlowTextPainter.LINE_HEIGHT);
                lineHeight = f;
                runLimit = aci.getRunLimit(FlowTextPainter.szAtts);
            }
        }
        final GVTLineMetrics lm3 = gvtFont.getLineMetrics(aci, aciWordStart, runLimit, frc);
        prevWI.addLineMetrics(gvtFont, lm3);
        prevWI.addLineHeight(lineHeight);
        final int[] wordGlyphCounts = new int[words];
        for (int m = 0; m < numGlyphs; ++m) {
            final int word2 = glyphWords[m];
            final int cWord2 = cWordMap[word2].getIndex();
            glyphWords[m] = cWord2;
            final int[] array = wordGlyphCounts;
            final int n = cWord2;
            ++array[n];
        }
        cWordMap = null;
        final int[][] wordGlyphs = new int[words][];
        final int[] wordGlyphGroupsCounts = new int[words];
        for (int i2 = 0; i2 < numGlyphs; ++i2) {
            final int cWord3 = glyphWords[i2];
            int[] wgs = wordGlyphs[cWord3];
            if (wgs == null) {
                final int[][] array2 = wordGlyphs;
                final int n2 = cWord3;
                final int[] array3 = new int[wordGlyphCounts[cWord3]];
                array2[n2] = array3;
                wgs = array3;
                wordGlyphCounts[cWord3] = 0;
            }
            final int cnt2 = wordGlyphCounts[cWord3];
            wgs[cnt2] = i2;
            if (cnt2 == 0) {
                final int[] array4 = wordGlyphGroupsCounts;
                final int n3 = cWord3;
                ++array4[n3];
            }
            else if (wgs[cnt2 - 1] != i2 - 1) {
                final int[] array5 = wordGlyphGroupsCounts;
                final int n4 = cWord3;
                ++array5[n4];
            }
            final int[] array6 = wordGlyphCounts;
            final int n5 = cWord3;
            ++array6[n5];
        }
        for (int i2 = 0; i2 < words; ++i2) {
            final int cnt3 = wordGlyphGroupsCounts[i2];
            final GlyphGroupInfo[] wordGlyphGroups = new GlyphGroupInfo[cnt3];
            if (cnt3 == 1) {
                final int[] glyphs = wordGlyphs[i2];
                final int start = glyphs[0];
                final int end = glyphs[glyphs.length - 1];
                wordGlyphGroups[0] = new GlyphGroupInfo(gv, start, end, hide, hideLast[end], glyphPos, advAdj, lastAdvAdj, space);
            }
            else {
                int glyphGroup = 0;
                final int[] glyphs2 = wordGlyphs[i2];
                int start2;
                int prev = start2 = glyphs2[0];
                for (int j2 = 1; j2 < glyphs2.length; ++j2) {
                    if (prev + 1 != glyphs2[j2]) {
                        final int end2 = glyphs2[j2 - 1];
                        wordGlyphGroups[glyphGroup] = new GlyphGroupInfo(gv, start2, end2, hide, hideLast[end2], glyphPos, advAdj, lastAdvAdj, space);
                        start2 = glyphs2[j2];
                        ++glyphGroup;
                    }
                    prev = glyphs2[j2];
                }
                final int end3 = glyphs2[glyphs2.length - 1];
                wordGlyphGroups[glyphGroup] = new GlyphGroupInfo(gv, start2, end3, hide, hideLast[end3], glyphPos, advAdj, lastAdvAdj, space);
            }
            wordInfos[i2].setGlyphGroups(wordGlyphGroups);
        }
        return wordInfos;
    }
    
    static {
        FlowTextPainter.singleton = new FlowTextPainter();
        WORD_LIMIT = TextLineBreaks.WORD_LIMIT;
        FLOW_REGIONS = GVTAttributedCharacterIterator.TextAttribute.FLOW_REGIONS;
        FLOW_LINE_BREAK = GVTAttributedCharacterIterator.TextAttribute.FLOW_LINE_BREAK;
        LINE_HEIGHT = GVTAttributedCharacterIterator.TextAttribute.LINE_HEIGHT;
        GVT_FONT = GVTAttributedCharacterIterator.TextAttribute.GVT_FONT;
        (FlowTextPainter.szAtts = new HashSet()).add(TextAttribute.SIZE);
        FlowTextPainter.szAtts.add(FlowTextPainter.GVT_FONT);
        FlowTextPainter.szAtts.add(FlowTextPainter.LINE_HEIGHT);
    }
}
