// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font;

import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.batik.svggen.font.table.KerningPair;
import org.apache.batik.svggen.font.table.KernSubtable;
import java.util.Set;
import org.apache.batik.svggen.font.table.Feature;
import org.apache.batik.svggen.font.table.LangSys;
import org.apache.batik.svggen.font.table.Script;
import org.apache.batik.svggen.font.table.CmapFormat;
import org.apache.batik.svggen.font.table.PostTable;
import org.apache.batik.svggen.font.table.KernTable;
import java.util.HashSet;
import org.apache.batik.svggen.font.table.SingleSubst;
import org.apache.batik.svggen.font.table.GsubTable;
import java.io.PrintStream;
import org.apache.batik.svggen.font.table.FeatureTags;
import org.apache.batik.svggen.font.table.ScriptTags;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.constants.XMLConstants;

public class SVGFont implements XMLConstants, SVGConstants, ScriptTags, FeatureTags
{
    static final String EOL;
    static final String PROPERTY_LINE_SEPARATOR = "line.separator";
    static final String PROPERTY_LINE_SEPARATOR_DEFAULT = "\n";
    static final int DEFAULT_FIRST = 32;
    static final int DEFAULT_LAST = 126;
    private static String QUOT_EOL;
    private static String CONFIG_USAGE;
    private static String CONFIG_SVG_BEGIN;
    private static String CONFIG_SVG_TEST_CARD_START;
    private static String CONFIG_SVG_TEST_CARD_END;
    public static final char ARG_KEY_START_CHAR = '-';
    public static final String ARG_KEY_CHAR_RANGE_LOW = "-l";
    public static final String ARG_KEY_CHAR_RANGE_HIGH = "-h";
    public static final String ARG_KEY_ID = "-id";
    public static final String ARG_KEY_ASCII = "-ascii";
    public static final String ARG_KEY_TESTCARD = "-testcard";
    public static final String ARG_KEY_AUTO_RANGE = "-autorange";
    public static final String ARG_KEY_OUTPUT_PATH = "-o";
    
    protected static String encodeEntities(final String s) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '<') {
                sb.append("&lt;");
            }
            else if (s.charAt(i) == '>') {
                sb.append("&gt;");
            }
            else if (s.charAt(i) == '&') {
                sb.append("&amp;");
            }
            else if (s.charAt(i) == '\'') {
                sb.append("&apos;");
            }
            else if (s.charAt(i) == '\"') {
                sb.append("&quot;");
            }
            else {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString();
    }
    
    protected static String getContourAsSVGPathData(final Glyph glyph, final int startIndex, final int count) {
        if (glyph.getPoint(startIndex).endOfContour) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        int offset = 0;
        while (offset < count) {
            final Point point = glyph.getPoint(startIndex + offset % count);
            final Point point_plus1 = glyph.getPoint(startIndex + (offset + 1) % count);
            final Point point_plus2 = glyph.getPoint(startIndex + (offset + 2) % count);
            if (offset == 0) {
                sb.append("M").append(String.valueOf(point.x)).append(" ").append(String.valueOf(point.y));
            }
            if (point.onCurve && point_plus1.onCurve) {
                if (point_plus1.x == point.x) {
                    sb.append("V").append(String.valueOf(point_plus1.y));
                }
                else if (point_plus1.y == point.y) {
                    sb.append("H").append(String.valueOf(point_plus1.x));
                }
                else {
                    sb.append("L").append(String.valueOf(point_plus1.x)).append(" ").append(String.valueOf(point_plus1.y));
                }
                ++offset;
            }
            else if (point.onCurve && !point_plus1.onCurve && point_plus2.onCurve) {
                sb.append("Q").append(String.valueOf(point_plus1.x)).append(" ").append(String.valueOf(point_plus1.y)).append(" ").append(String.valueOf(point_plus2.x)).append(" ").append(String.valueOf(point_plus2.y));
                offset += 2;
            }
            else if (point.onCurve && !point_plus1.onCurve && !point_plus2.onCurve) {
                sb.append("Q").append(String.valueOf(point_plus1.x)).append(" ").append(String.valueOf(point_plus1.y)).append(" ").append(String.valueOf(midValue(point_plus1.x, point_plus2.x))).append(" ").append(String.valueOf(midValue(point_plus1.y, point_plus2.y)));
                offset += 2;
            }
            else if (!point.onCurve && !point_plus1.onCurve) {
                sb.append("T").append(String.valueOf(midValue(point.x, point_plus1.x))).append(" ").append(String.valueOf(midValue(point.y, point_plus1.y)));
                ++offset;
            }
            else {
                if (point.onCurve || !point_plus1.onCurve) {
                    System.out.println("drawGlyph case not catered for!!");
                    break;
                }
                sb.append("T").append(String.valueOf(point_plus1.x)).append(" ").append(String.valueOf(point_plus1.y));
                ++offset;
            }
        }
        sb.append("Z");
        return sb.toString();
    }
    
    protected static String getSVGFontFaceElement(final Font font) {
        final StringBuffer sb = new StringBuffer();
        final String fontFamily = font.getNameTable().getRecord((short)1);
        final short unitsPerEm = font.getHeadTable().getUnitsPerEm();
        final String panose = font.getOS2Table().getPanose().toString();
        final short ascent = font.getHheaTable().getAscender();
        final short descent = font.getHheaTable().getDescender();
        final int baseline = 0;
        sb.append("<").append("font-face").append(SVGFont.EOL).append("    ").append("font-family").append("=\"").append(fontFamily).append(SVGFont.QUOT_EOL).append("    ").append("units-per-em").append("=\"").append(unitsPerEm).append(SVGFont.QUOT_EOL).append("    ").append("panose-1").append("=\"").append(panose).append(SVGFont.QUOT_EOL).append("    ").append("ascent").append("=\"").append(ascent).append(SVGFont.QUOT_EOL).append("    ").append("descent").append("=\"").append(descent).append(SVGFont.QUOT_EOL).append("    ").append("alphabetic").append("=\"").append(baseline).append('\"').append(" />").append(SVGFont.EOL);
        return sb.toString();
    }
    
    protected static void writeFontAsSVGFragment(final PrintStream ps, final Font font, final String id, int first, int last, final boolean autoRange, final boolean forceAscii) throws Exception {
        final int horiz_advance_x = font.getOS2Table().getAvgCharWidth();
        ps.print("<");
        ps.print("font");
        ps.print(" ");
        if (id != null) {
            ps.print("id");
            ps.print("=\"");
            ps.print(id);
            ps.print('\"');
            ps.print(" ");
        }
        ps.print("horiz-adv-x");
        ps.print("=\"");
        ps.print(horiz_advance_x);
        ps.print('\"');
        ps.print(" >");
        ps.print(getSVGFontFaceElement(font));
        CmapFormat cmapFmt = null;
        if (forceAscii) {
            cmapFmt = font.getCmapTable().getCmapFormat((short)1, (short)0);
        }
        else {
            cmapFmt = font.getCmapTable().getCmapFormat((short)3, (short)1);
            if (cmapFmt == null) {
                cmapFmt = font.getCmapTable().getCmapFormat((short)3, (short)0);
            }
        }
        if (cmapFmt == null) {
            throw new Exception("Cannot find a suitable cmap table");
        }
        final GsubTable gsub = (GsubTable)font.getTable(1196643650);
        SingleSubst initialSubst = null;
        SingleSubst medialSubst = null;
        SingleSubst terminalSubst = null;
        if (gsub != null) {
            final Script s = gsub.getScriptList().findScript("arab");
            if (s != null) {
                final LangSys ls = s.getDefaultLangSys();
                if (ls != null) {
                    final Feature init = gsub.getFeatureList().findFeature(ls, "init");
                    final Feature medi = gsub.getFeatureList().findFeature(ls, "medi");
                    final Feature fina = gsub.getFeatureList().findFeature(ls, "fina");
                    if (init != null) {
                        initialSubst = (SingleSubst)gsub.getLookupList().getLookup(init, 0).getSubtable(0);
                    }
                    if (medi != null) {
                        medialSubst = (SingleSubst)gsub.getLookupList().getLookup(medi, 0).getSubtable(0);
                    }
                    if (fina != null) {
                        terminalSubst = (SingleSubst)gsub.getLookupList().getLookup(fina, 0).getSubtable(0);
                    }
                }
            }
        }
        ps.println(getGlyphAsSVG(font, font.getGlyph(0), 0, horiz_advance_x, initialSubst, medialSubst, terminalSubst, ""));
        try {
            if (first == -1) {
                if (!autoRange) {
                    first = 32;
                }
                else {
                    first = cmapFmt.getFirst();
                }
            }
            if (last == -1) {
                if (!autoRange) {
                    last = 126;
                }
                else {
                    last = cmapFmt.getLast();
                }
            }
            final Set glyphSet = new HashSet();
            for (int i = first; i <= last; ++i) {
                final int glyphIndex = cmapFmt.mapCharCode(i);
                if (glyphIndex > 0) {
                    glyphSet.add(glyphIndex);
                    ps.println(getGlyphAsSVG(font, font.getGlyph(glyphIndex), glyphIndex, horiz_advance_x, initialSubst, medialSubst, terminalSubst, (32 <= i && i <= 127) ? encodeEntities(String.valueOf((char)i)) : ("&#x" + Integer.toHexString(i) + ";")));
                }
            }
            final KernTable kern = (KernTable)font.getTable(1801810542);
            if (kern != null) {
                final KernSubtable kst = kern.getSubtable(0);
                final PostTable post = (PostTable)font.getTable(1886352244);
                for (int j = 0; j < kst.getKerningPairCount(); ++j) {
                    final KerningPair kpair = kst.getKerningPair(j);
                    if (glyphSet.contains(kpair.getLeft()) && glyphSet.contains(kpair.getRight())) {
                        ps.println(getKerningPairAsSVG(kpair, post));
                    }
                }
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        ps.print("</");
        ps.print("font");
        ps.println(">");
    }
    
    protected static String getGlyphAsSVG(final Font font, final Glyph glyph, final int glyphIndex, final int defaultHorizAdvanceX, final String attrib, final String code) {
        final StringBuffer sb = new StringBuffer();
        int firstIndex = 0;
        int count = 0;
        final int horiz_advance_x = font.getHmtxTable().getAdvanceWidth(glyphIndex);
        if (glyphIndex == 0) {
            sb.append("<");
            sb.append("missing-glyph");
        }
        else {
            sb.append("<").append("glyph").append(" ").append("unicode").append("=\"").append(code).append('\"');
            final String glyphName = font.getPostTable().getGlyphName(glyphIndex);
            if (glyphName != null) {
                sb.append(" ").append("glyph-name").append("=\"").append(glyphName).append('\"');
            }
        }
        if (horiz_advance_x != defaultHorizAdvanceX) {
            sb.append(" ").append("horiz-adv-x").append("=\"").append(horiz_advance_x).append('\"');
        }
        if (attrib != null) {
            sb.append(attrib);
        }
        if (glyph != null) {
            sb.append(" ").append("d").append("=\"");
            for (int i = 0; i < glyph.getPointCount(); ++i) {
                ++count;
                if (glyph.getPoint(i).endOfContour) {
                    sb.append(getContourAsSVGPathData(glyph, firstIndex, count));
                    firstIndex = i + 1;
                    count = 0;
                }
            }
            sb.append('\"');
        }
        sb.append(" />");
        chopUpStringBuffer(sb);
        return sb.toString();
    }
    
    protected static String getGlyphAsSVG(final Font font, final Glyph glyph, final int glyphIndex, final int defaultHorizAdvanceX, final SingleSubst arabInitSubst, final SingleSubst arabMediSubst, final SingleSubst arabTermSubst, final String code) {
        final StringBuffer sb = new StringBuffer();
        boolean substituted = false;
        int arabInitGlyphIndex = glyphIndex;
        int arabMediGlyphIndex = glyphIndex;
        int arabTermGlyphIndex = glyphIndex;
        if (arabInitSubst != null) {
            arabInitGlyphIndex = arabInitSubst.substitute(glyphIndex);
        }
        if (arabMediSubst != null) {
            arabMediGlyphIndex = arabMediSubst.substitute(glyphIndex);
        }
        if (arabTermSubst != null) {
            arabTermGlyphIndex = arabTermSubst.substitute(glyphIndex);
        }
        if (arabInitGlyphIndex != glyphIndex) {
            sb.append(getGlyphAsSVG(font, font.getGlyph(arabInitGlyphIndex), arabInitGlyphIndex, defaultHorizAdvanceX, " arabic-form=\"initial\"", code));
            sb.append(SVGFont.EOL);
            substituted = true;
        }
        if (arabMediGlyphIndex != glyphIndex) {
            sb.append(getGlyphAsSVG(font, font.getGlyph(arabMediGlyphIndex), arabMediGlyphIndex, defaultHorizAdvanceX, " arabic-form=\"medial\"", code));
            sb.append(SVGFont.EOL);
            substituted = true;
        }
        if (arabTermGlyphIndex != glyphIndex) {
            sb.append(getGlyphAsSVG(font, font.getGlyph(arabTermGlyphIndex), arabTermGlyphIndex, defaultHorizAdvanceX, " arabic-form=\"terminal\"", code));
            sb.append(SVGFont.EOL);
            substituted = true;
        }
        if (substituted) {
            sb.append(getGlyphAsSVG(font, glyph, glyphIndex, defaultHorizAdvanceX, " arabic-form=\"isolated\"", code));
        }
        else {
            sb.append(getGlyphAsSVG(font, glyph, glyphIndex, defaultHorizAdvanceX, null, code));
        }
        return sb.toString();
    }
    
    protected static String getKerningPairAsSVG(final KerningPair kp, final PostTable post) {
        final String leftGlyphName = post.getGlyphName(kp.getLeft());
        final String rightGlyphName = post.getGlyphName(kp.getRight());
        final StringBuffer sb = new StringBuffer();
        sb.append("<").append("hkern").append(" ");
        if (leftGlyphName == null) {
            sb.append("u1").append("=\"");
            sb.append(kp.getLeft());
        }
        else {
            sb.append("g1").append("=\"");
            sb.append(leftGlyphName);
        }
        sb.append('\"').append(" ");
        if (rightGlyphName == null) {
            sb.append("u2").append("=\"");
            sb.append(kp.getRight());
        }
        else {
            sb.append("g2").append("=\"");
            sb.append(rightGlyphName);
        }
        sb.append('\"').append(" ").append("k").append("=\"");
        sb.append(-kp.getValue());
        sb.append('\"').append(" />");
        return sb.toString();
    }
    
    protected static void writeSvgBegin(final PrintStream ps) {
        ps.println(Messages.formatMessage(SVGFont.CONFIG_SVG_BEGIN, new Object[] { "-//W3C//DTD SVG 1.0//EN", "http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd" }));
    }
    
    protected static void writeSvgDefsBegin(final PrintStream ps) {
        ps.println("<defs >");
    }
    
    protected static void writeSvgDefsEnd(final PrintStream ps) {
        ps.println("</defs>");
    }
    
    protected static void writeSvgEnd(final PrintStream ps) {
        ps.println("</svg>");
    }
    
    protected static void writeSvgTestCard(final PrintStream ps, final String fontFamily) {
        ps.println(Messages.formatMessage(SVGFont.CONFIG_SVG_TEST_CARD_START, null));
        ps.println(fontFamily);
        ps.println(Messages.formatMessage(SVGFont.CONFIG_SVG_TEST_CARD_END, null));
    }
    
    public static void main(final String[] args) {
        try {
            final String path = parseArgs(args, null);
            final String low = parseArgs(args, "-l");
            final String high = parseArgs(args, "-h");
            final String id = parseArgs(args, "-id");
            final String ascii = parseArgs(args, "-ascii");
            final String testCard = parseArgs(args, "-testcard");
            final String outPath = parseArgs(args, "-o");
            final String autoRange = parseArgs(args, "-autorange");
            PrintStream ps = null;
            FileOutputStream fos = null;
            if (outPath != null) {
                fos = new FileOutputStream(outPath);
                ps = new PrintStream(fos);
            }
            else {
                ps = System.out;
            }
            if (path != null) {
                final Font font = Font.create(path);
                writeSvgBegin(ps);
                writeSvgDefsBegin(ps);
                writeFontAsSVGFragment(ps, font, id, (low != null) ? Integer.parseInt(low) : -1, (high != null) ? Integer.parseInt(high) : -1, autoRange != null, ascii != null);
                writeSvgDefsEnd(ps);
                if (testCard != null) {
                    final String fontFamily = font.getNameTable().getRecord((short)1);
                    writeSvgTestCard(ps, fontFamily);
                }
                writeSvgEnd(ps);
                if (fos != null) {
                    fos.close();
                }
            }
            else {
                usage();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            usage();
        }
    }
    
    private static void chopUpStringBuffer(final StringBuffer sb) {
        if (sb.length() < 256) {
            return;
        }
        for (int i = 240; i < sb.length(); ++i) {
            if (sb.charAt(i) == ' ') {
                sb.setCharAt(i, '\n');
                i += 240;
            }
        }
    }
    
    private static int midValue(final int a, final int b) {
        return a + (b - a) / 2;
    }
    
    private static String parseArgs(final String[] args, final String name) {
        for (int i = 0; i < args.length; ++i) {
            if (name == null) {
                if (args[i].charAt(0) != '-') {
                    return args[i];
                }
            }
            else if (name.equalsIgnoreCase(args[i])) {
                if (i < args.length - 1 && args[i + 1].charAt(0) != '-') {
                    return args[i + 1];
                }
                return args[i];
            }
        }
        return null;
    }
    
    private static void usage() {
        System.err.println(Messages.formatMessage(SVGFont.CONFIG_USAGE, null));
    }
    
    static {
        String temp;
        try {
            temp = System.getProperty("line.separator", "\n");
        }
        catch (SecurityException e) {
            temp = "\n";
        }
        EOL = temp;
        SVGFont.QUOT_EOL = '\"' + SVGFont.EOL;
        SVGFont.CONFIG_USAGE = "SVGFont.config.usage";
        SVGFont.CONFIG_SVG_BEGIN = "SVGFont.config.svg.begin";
        SVGFont.CONFIG_SVG_TEST_CARD_START = "SVGFont.config.svg.test.card.start";
        SVGFont.CONFIG_SVG_TEST_CARD_END = "SVGFont.config.svg.test.card.end";
    }
}
