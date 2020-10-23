// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.Iterator;
import java.util.Collection;
import java.util.StringTokenizer;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.batik.gvt.font.AWTGVTFont;
import java.awt.Font;
import org.apache.batik.gvt.font.GVTFontFamily;
import java.io.InputStream;
import org.apache.batik.gvt.font.GVTFontFace;
import java.util.List;
import java.util.Map;
import org.apache.batik.gvt.font.AWTFontFamily;

public final class DefaultFontFamilyResolver implements FontFamilyResolver
{
    public static final DefaultFontFamilyResolver SINGLETON;
    private static final AWTFontFamily DEFAULT_FONT_FAMILY;
    protected static final Map fonts;
    protected static final List awtFontFamilies;
    protected static final List awtFonts;
    protected static final Map resolvedFontFamilies;
    
    private DefaultFontFamilyResolver() {
    }
    
    @Override
    public AWTFontFamily resolve(final String familyName, final FontFace fontFace) {
        final String fontName = DefaultFontFamilyResolver.fonts.get(fontFace.getFamilyName().toLowerCase());
        if (fontName == null) {
            return null;
        }
        final GVTFontFace face = FontFace.createFontFace(fontName, fontFace);
        return new AWTFontFamily(fontFace);
    }
    
    @Override
    public GVTFontFamily loadFont(final InputStream in, final FontFace ff) throws Exception {
        final Font font = Font.createFont(0, in);
        return new AWTFontFamily(ff, font);
    }
    
    @Override
    public GVTFontFamily resolve(String familyName) {
        familyName = familyName.toLowerCase();
        GVTFontFamily resolvedFF = DefaultFontFamilyResolver.resolvedFontFamilies.get(familyName);
        if (resolvedFF == null) {
            final String awtFamilyName = DefaultFontFamilyResolver.fonts.get(familyName);
            if (awtFamilyName != null) {
                resolvedFF = new AWTFontFamily(awtFamilyName);
            }
            DefaultFontFamilyResolver.resolvedFontFamilies.put(familyName, resolvedFF);
        }
        return resolvedFF;
    }
    
    @Override
    public GVTFontFamily getFamilyThatCanDisplay(final char c) {
        for (int i = 0; i < DefaultFontFamilyResolver.awtFontFamilies.size(); ++i) {
            final AWTFontFamily fontFamily = DefaultFontFamilyResolver.awtFontFamilies.get(i);
            final AWTGVTFont font = DefaultFontFamilyResolver.awtFonts.get(i);
            if (font.canDisplay(c) && fontFamily.getFamilyName().indexOf("Song") == -1) {
                return fontFamily;
            }
        }
        return null;
    }
    
    @Override
    public GVTFontFamily getDefault() {
        return DefaultFontFamilyResolver.DEFAULT_FONT_FAMILY;
    }
    
    static {
        SINGLETON = new DefaultFontFamilyResolver();
        DEFAULT_FONT_FAMILY = new AWTFontFamily("SansSerif");
        fonts = new HashMap();
        awtFontFamilies = new ArrayList();
        awtFonts = new ArrayList();
        DefaultFontFamilyResolver.fonts.put("sans-serif", "SansSerif");
        DefaultFontFamilyResolver.fonts.put("serif", "Serif");
        DefaultFontFamilyResolver.fonts.put("times", "Serif");
        DefaultFontFamilyResolver.fonts.put("times new roman", "Serif");
        DefaultFontFamilyResolver.fonts.put("cursive", "Dialog");
        DefaultFontFamilyResolver.fonts.put("fantasy", "Symbol");
        DefaultFontFamilyResolver.fonts.put("monospace", "Monospaced");
        DefaultFontFamilyResolver.fonts.put("monospaced", "Monospaced");
        DefaultFontFamilyResolver.fonts.put("courier", "Monospaced");
        final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final String[] fontNames = env.getAvailableFontFamilyNames();
        for (int nFonts = (fontNames != null) ? fontNames.length : 0, i = 0; i < nFonts; ++i) {
            DefaultFontFamilyResolver.fonts.put(fontNames[i].toLowerCase(), fontNames[i]);
            final StringTokenizer st = new StringTokenizer(fontNames[i]);
            String fontNameWithoutSpaces = "";
            while (st.hasMoreTokens()) {
                fontNameWithoutSpaces += st.nextToken();
            }
            DefaultFontFamilyResolver.fonts.put(fontNameWithoutSpaces.toLowerCase(), fontNames[i]);
            final String fontNameWithDashes = fontNames[i].replace(' ', '-');
            if (!fontNameWithDashes.equals(fontNames[i])) {
                DefaultFontFamilyResolver.fonts.put(fontNameWithDashes.toLowerCase(), fontNames[i]);
            }
        }
        final Font[] arr$;
        final Font[] allFonts = arr$ = env.getAllFonts();
        for (final Font f : arr$) {
            DefaultFontFamilyResolver.fonts.put(f.getFontName().toLowerCase(), f.getFontName());
        }
        DefaultFontFamilyResolver.awtFontFamilies.add(DefaultFontFamilyResolver.DEFAULT_FONT_FAMILY);
        DefaultFontFamilyResolver.awtFonts.add(new AWTGVTFont(DefaultFontFamilyResolver.DEFAULT_FONT_FAMILY.getFamilyName(), 0, 12));
        final Collection fontValues = DefaultFontFamilyResolver.fonts.values();
        for (final Object fontValue : fontValues) {
            final String fontFamily = (String)fontValue;
            final AWTFontFamily awtFontFamily = new AWTFontFamily(fontFamily);
            DefaultFontFamilyResolver.awtFontFamilies.add(awtFontFamily);
            final AWTGVTFont font = new AWTGVTFont(fontFamily, 0, 12);
            DefaultFontFamilyResolver.awtFonts.add(font);
        }
        resolvedFontFamilies = new HashMap();
    }
}
