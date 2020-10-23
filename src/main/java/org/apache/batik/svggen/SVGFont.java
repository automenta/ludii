// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import org.w3c.dom.NodeList;
import java.awt.font.LineMetrics;
import java.awt.font.GlyphMetrics;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.awt.geom.AffineTransform;
import java.awt.font.FontRenderContext;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextAttribute;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

public class SVGFont extends AbstractSVGConverter
{
    public static final float EXTRA_LIGHT;
    public static final float LIGHT;
    public static final float DEMILIGHT;
    public static final float REGULAR;
    public static final float SEMIBOLD;
    public static final float MEDIUM;
    public static final float DEMIBOLD;
    public static final float BOLD;
    public static final float HEAVY;
    public static final float EXTRABOLD;
    public static final float ULTRABOLD;
    public static final float POSTURE_REGULAR;
    public static final float POSTURE_OBLIQUE;
    static final float[] fontStyles;
    static final String[] svgStyles;
    static final float[] fontWeights;
    static final String[] svgWeights;
    static Map logicalFontMap;
    static final int COMMON_FONT_SIZE = 100;
    final Map fontStringMap;
    
    public SVGFont(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
        this.fontStringMap = new HashMap();
    }
    
    public void recordFontUsage(final String string, final Font font) {
        final Font commonSizeFont = createCommonSizeFont(font);
        final String fontKey = commonSizeFont.getFamily() + commonSizeFont.getStyle();
        CharListHelper chl = this.fontStringMap.get(fontKey);
        if (chl == null) {
            chl = new CharListHelper();
        }
        for (int i = 0; i < string.length(); ++i) {
            final char ch = string.charAt(i);
            chl.add(ch);
        }
        this.fontStringMap.put(fontKey, chl);
    }
    
    private static Font createCommonSizeFont(final Font font) {
        final Map attributes = new HashMap();
        attributes.put(TextAttribute.SIZE, 100.0f);
        attributes.put(TextAttribute.TRANSFORM, null);
        return font.deriveFont(attributes);
    }
    
    @Override
    public SVGDescriptor toSVG(final GraphicContext gc) {
        return this.toSVG(gc.getFont(), gc.getFontRenderContext());
    }
    
    public SVGFontDescriptor toSVG(final Font font, final FontRenderContext frc) {
        final FontRenderContext localFRC = new FontRenderContext(new AffineTransform(), frc.isAntiAliased(), frc.usesFractionalMetrics());
        final String fontSize = this.doubleString(font.getSize2D()) + "px";
        final String fontWeight = weightToSVG(font);
        final String fontStyle = styleToSVG(font);
        final String fontFamilyStr = familyToSVG(font);
        final Font commonSizeFont = createCommonSizeFont(font);
        final String fontKey = commonSizeFont.getFamily() + commonSizeFont.getStyle();
        final CharListHelper clh = this.fontStringMap.get(fontKey);
        if (clh == null) {
            return new SVGFontDescriptor(fontSize, fontWeight, fontStyle, fontFamilyStr, null);
        }
        final Document domFactory = this.generatorContext.domFactory;
        final SVGFontDescriptor fontDesc = this.descMap.get(fontKey);
        Element fontDef;
        if (fontDesc != null) {
            fontDef = fontDesc.getDef();
        }
        else {
            fontDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "font");
            final Element fontFace = domFactory.createElementNS("http://www.w3.org/2000/svg", "font-face");
            String svgFontFamilyString = fontFamilyStr;
            if (fontFamilyStr.startsWith("'") && fontFamilyStr.endsWith("'")) {
                svgFontFamilyString = fontFamilyStr.substring(1, fontFamilyStr.length() - 1);
            }
            fontFace.setAttributeNS(null, "font-family", svgFontFamilyString);
            fontFace.setAttributeNS(null, "font-weight", fontWeight);
            fontFace.setAttributeNS(null, "font-style", fontStyle);
            fontFace.setAttributeNS(null, "units-per-em", "100");
            fontDef.appendChild(fontFace);
            final Element missingGlyphElement = domFactory.createElementNS("http://www.w3.org/2000/svg", "missing-glyph");
            final int[] missingGlyphCode = { commonSizeFont.getMissingGlyphCode() };
            final GlyphVector gv = commonSizeFont.createGlyphVector(localFRC, missingGlyphCode);
            Shape missingGlyphShape = gv.getGlyphOutline(0);
            final GlyphMetrics gm = gv.getGlyphMetrics(0);
            final AffineTransform at = AffineTransform.getScaleInstance(1.0, -1.0);
            missingGlyphShape = at.createTransformedShape(missingGlyphShape);
            missingGlyphElement.setAttributeNS(null, "d", SVGPath.toSVGPathData(missingGlyphShape, this.generatorContext));
            missingGlyphElement.setAttributeNS(null, "horiz-adv-x", String.valueOf(gm.getAdvance()));
            fontDef.appendChild(missingGlyphElement);
            fontDef.setAttributeNS(null, "horiz-adv-x", String.valueOf(gm.getAdvance()));
            final LineMetrics lm = commonSizeFont.getLineMetrics("By", localFRC);
            fontFace.setAttributeNS(null, "ascent", String.valueOf(lm.getAscent()));
            fontFace.setAttributeNS(null, "descent", String.valueOf(lm.getDescent()));
            fontDef.setAttributeNS(null, "id", this.generatorContext.idGenerator.generateID("font"));
        }
        final String textUsingFont = clh.getNewChars();
        clh.clearNewChars();
        for (int i = textUsingFont.length() - 1; i >= 0; --i) {
            final char c = textUsingFont.charAt(i);
            final String searchStr = String.valueOf(c);
            boolean foundGlyph = false;
            final NodeList fontChildren = fontDef.getChildNodes();
            for (int j = 0; j < fontChildren.getLength(); ++j) {
                if (fontChildren.item(j) instanceof Element) {
                    final Element childElement = (Element)fontChildren.item(j);
                    if (childElement.getAttributeNS(null, "unicode").equals(searchStr)) {
                        foundGlyph = true;
                        break;
                    }
                }
            }
            if (foundGlyph) {
                break;
            }
            final Element glyphElement = domFactory.createElementNS("http://www.w3.org/2000/svg", "glyph");
            final GlyphVector gv2 = commonSizeFont.createGlyphVector(localFRC, "" + c);
            Shape glyphShape = gv2.getGlyphOutline(0);
            final GlyphMetrics gm2 = gv2.getGlyphMetrics(0);
            final AffineTransform at2 = AffineTransform.getScaleInstance(1.0, -1.0);
            glyphShape = at2.createTransformedShape(glyphShape);
            glyphElement.setAttributeNS(null, "d", SVGPath.toSVGPathData(glyphShape, this.generatorContext));
            glyphElement.setAttributeNS(null, "horiz-adv-x", String.valueOf(gm2.getAdvance()));
            glyphElement.setAttributeNS(null, "unicode", String.valueOf(c));
            fontDef.appendChild(glyphElement);
        }
        final SVGFontDescriptor newFontDesc = new SVGFontDescriptor(fontSize, fontWeight, fontStyle, fontFamilyStr, fontDef);
        if (fontDesc == null) {
            this.descMap.put(fontKey, newFontDesc);
            this.defSet.add(fontDef);
        }
        return newFontDesc;
    }
    
    public static String familyToSVG(final Font font) {
        String fontFamilyStr = font.getFamily();
        final String logicalFontFamily = SVGFont.logicalFontMap.get(font.getName().toLowerCase());
        if (logicalFontFamily != null) {
            fontFamilyStr = logicalFontFamily;
        }
        else {
            final char QUOTE = '\'';
            fontFamilyStr = '\'' + fontFamilyStr + '\'';
        }
        return fontFamilyStr;
    }
    
    public static String styleToSVG(final Font font) {
        final Map attrMap = font.getAttributes();
        Float styleValue = attrMap.get(TextAttribute.POSTURE);
        if (styleValue == null) {
            if (font.isItalic()) {
                styleValue = TextAttribute.POSTURE_OBLIQUE;
            }
            else {
                styleValue = TextAttribute.POSTURE_REGULAR;
            }
        }
        float style;
        int i;
        for (style = styleValue, i = 0, i = 0; i < SVGFont.fontStyles.length && style > SVGFont.fontStyles[i]; ++i) {}
        return SVGFont.svgStyles[i];
    }
    
    public static String weightToSVG(final Font font) {
        final Map attrMap = font.getAttributes();
        Float weightValue = attrMap.get(TextAttribute.WEIGHT);
        if (weightValue == null) {
            if (font.isBold()) {
                weightValue = TextAttribute.WEIGHT_BOLD;
            }
            else {
                weightValue = TextAttribute.WEIGHT_REGULAR;
            }
        }
        float weight;
        int i;
        for (weight = weightValue, i = 0, i = 0; i < SVGFont.fontWeights.length && weight > SVGFont.fontWeights[i]; ++i) {}
        return SVGFont.svgWeights[i];
    }
    
    static {
        EXTRA_LIGHT = TextAttribute.WEIGHT_EXTRA_LIGHT;
        LIGHT = TextAttribute.WEIGHT_LIGHT;
        DEMILIGHT = TextAttribute.WEIGHT_DEMILIGHT;
        REGULAR = TextAttribute.WEIGHT_REGULAR;
        SEMIBOLD = TextAttribute.WEIGHT_SEMIBOLD;
        MEDIUM = TextAttribute.WEIGHT_MEDIUM;
        DEMIBOLD = TextAttribute.WEIGHT_DEMIBOLD;
        BOLD = TextAttribute.WEIGHT_BOLD;
        HEAVY = TextAttribute.WEIGHT_HEAVY;
        EXTRABOLD = TextAttribute.WEIGHT_EXTRABOLD;
        ULTRABOLD = TextAttribute.WEIGHT_ULTRABOLD;
        POSTURE_REGULAR = TextAttribute.POSTURE_REGULAR;
        POSTURE_OBLIQUE = TextAttribute.POSTURE_OBLIQUE;
        fontStyles = new float[] { SVGFont.POSTURE_REGULAR + (SVGFont.POSTURE_OBLIQUE - SVGFont.POSTURE_REGULAR) / 2.0f };
        svgStyles = new String[] { "normal", "italic" };
        fontWeights = new float[] { SVGFont.EXTRA_LIGHT + (SVGFont.LIGHT - SVGFont.EXTRA_LIGHT) / 2.0f, SVGFont.LIGHT + (SVGFont.DEMILIGHT - SVGFont.LIGHT) / 2.0f, SVGFont.DEMILIGHT + (SVGFont.REGULAR - SVGFont.DEMILIGHT) / 2.0f, SVGFont.REGULAR + (SVGFont.SEMIBOLD - SVGFont.REGULAR) / 2.0f, SVGFont.SEMIBOLD + (SVGFont.MEDIUM - SVGFont.SEMIBOLD) / 2.0f, SVGFont.MEDIUM + (SVGFont.DEMIBOLD - SVGFont.MEDIUM) / 2.0f, SVGFont.DEMIBOLD + (SVGFont.BOLD - SVGFont.DEMIBOLD) / 2.0f, SVGFont.BOLD + (SVGFont.HEAVY - SVGFont.BOLD) / 2.0f, SVGFont.HEAVY + (SVGFont.EXTRABOLD - SVGFont.HEAVY) / 2.0f, SVGFont.EXTRABOLD + (SVGFont.ULTRABOLD - SVGFont.EXTRABOLD) };
        svgWeights = new String[] { "100", "200", "300", "normal", "500", "500", "600", "bold", "800", "800", "900" };
        (SVGFont.logicalFontMap = new HashMap()).put("dialog", "sans-serif");
        SVGFont.logicalFontMap.put("dialoginput", "monospace");
        SVGFont.logicalFontMap.put("monospaced", "monospace");
        SVGFont.logicalFontMap.put("serif", "serif");
        SVGFont.logicalFontMap.put("sansserif", "sans-serif");
        SVGFont.logicalFontMap.put("symbol", "'WingDings'");
    }
    
    private static class CharListHelper
    {
        private int nUsed;
        private int[] charList;
        private StringBuffer freshChars;
        
        CharListHelper() {
            this.nUsed = 0;
            this.charList = new int[40];
            this.freshChars = new StringBuffer(40);
        }
        
        String getNewChars() {
            return this.freshChars.toString();
        }
        
        void clearNewChars() {
            this.freshChars = new StringBuffer(40);
        }
        
        boolean add(final int c) {
            int pos = binSearch(this.charList, this.nUsed, c);
            if (pos >= 0) {
                return false;
            }
            if (this.nUsed == this.charList.length) {
                final int[] t = new int[this.nUsed + 20];
                System.arraycopy(this.charList, 0, t, 0, this.nUsed);
                this.charList = t;
            }
            pos = -pos - 1;
            System.arraycopy(this.charList, pos, this.charList, pos + 1, this.nUsed - pos);
            this.charList[pos] = c;
            this.freshChars.append((char)c);
            ++this.nUsed;
            return true;
        }
        
        static int binSearch(final int[] list, final int nUsed, final int chr) {
            int low = 0;
            int high = nUsed - 1;
            while (low <= high) {
                final int mid = low + high >>> 1;
                final int midVal = list[mid];
                if (midVal < chr) {
                    low = mid + 1;
                }
                else {
                    if (midVal <= chr) {
                        return mid;
                    }
                    high = mid - 1;
                }
            }
            return -(low + 1);
        }
    }
}
