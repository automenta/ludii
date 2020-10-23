// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.svg.SVGDocument;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;
import java.util.Iterator;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.Cursor;
import org.w3c.dom.svg.SVGAElement;
import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.util.XMLResourceDescriptor;
import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import org.apache.batik.util.SVGFeatureStrings;
import java.util.HashSet;
import java.util.Set;

public class UserAgentAdapter implements UserAgent
{
    protected Set FEATURES;
    protected Set extensions;
    protected BridgeContext ctx;
    
    public UserAgentAdapter() {
        this.FEATURES = new HashSet();
        this.extensions = new HashSet();
    }
    
    public void setBridgeContext(final BridgeContext ctx) {
        this.ctx = ctx;
    }
    
    public void addStdFeatures() {
        SVGFeatureStrings.addSupportedFeatureStrings(this.FEATURES);
    }
    
    @Override
    public Dimension2D getViewportSize() {
        return new Dimension(1, 1);
    }
    
    @Override
    public void displayMessage(final String message) {
    }
    
    public void displayError(final String message) {
        this.displayMessage(message);
    }
    
    @Override
    public void displayError(final Exception e) {
        this.displayError(e.getMessage());
    }
    
    @Override
    public void showAlert(final String message) {
    }
    
    @Override
    public String showPrompt(final String message) {
        return null;
    }
    
    @Override
    public String showPrompt(final String message, final String defaultValue) {
        return null;
    }
    
    @Override
    public boolean showConfirm(final String message) {
        return false;
    }
    
    @Override
    public float getPixelUnitToMillimeter() {
        return 0.26458332f;
    }
    
    @Override
    public float getPixelToMM() {
        return this.getPixelUnitToMillimeter();
    }
    
    @Override
    public String getDefaultFontFamily() {
        return "Arial, Helvetica, sans-serif";
    }
    
    @Override
    public float getMediumFontSize() {
        return 228.59999f / (72.0f * this.getPixelUnitToMillimeter());
    }
    
    @Override
    public float getLighterFontWeight(final float f) {
        return getStandardLighterFontWeight(f);
    }
    
    @Override
    public float getBolderFontWeight(final float f) {
        return getStandardBolderFontWeight(f);
    }
    
    @Override
    public String getLanguages() {
        return "en";
    }
    
    @Override
    public String getMedia() {
        return "all";
    }
    
    @Override
    public String getAlternateStyleSheet() {
        return null;
    }
    
    @Override
    public String getUserStyleSheetURI() {
        return null;
    }
    
    @Override
    public String getXMLParserClassName() {
        return XMLResourceDescriptor.getXMLParserClassName();
    }
    
    @Override
    public boolean isXMLParserValidating() {
        return false;
    }
    
    @Override
    public EventDispatcher getEventDispatcher() {
        return null;
    }
    
    @Override
    public void openLink(final SVGAElement elt) {
    }
    
    @Override
    public void setSVGCursor(final Cursor cursor) {
    }
    
    @Override
    public void setTextSelection(final Mark start, final Mark end) {
    }
    
    @Override
    public void deselectAll() {
    }
    
    public void runThread(final Thread t) {
    }
    
    @Override
    public AffineTransform getTransform() {
        return null;
    }
    
    @Override
    public void setTransform(final AffineTransform at) {
    }
    
    @Override
    public Point getClientAreaLocationOnScreen() {
        return new Point();
    }
    
    @Override
    public boolean hasFeature(final String s) {
        return this.FEATURES.contains(s);
    }
    
    @Override
    public boolean supportExtension(final String s) {
        return this.extensions.contains(s);
    }
    
    @Override
    public void registerExtension(final BridgeExtension ext) {
        final Iterator i = ext.getImplementedExtensions();
        while (i.hasNext()) {
            this.extensions.add(i.next());
        }
    }
    
    @Override
    public void handleElement(final Element elt, final Object data) {
    }
    
    @Override
    public ScriptSecurity getScriptSecurity(final String scriptType, final ParsedURL scriptURL, final ParsedURL docURL) {
        return new DefaultScriptSecurity(scriptType, scriptURL, docURL);
    }
    
    @Override
    public void checkLoadScript(final String scriptType, final ParsedURL scriptURL, final ParsedURL docURL) throws SecurityException {
        final ScriptSecurity s = this.getScriptSecurity(scriptType, scriptURL, docURL);
        if (s != null) {
            s.checkLoadScript();
        }
    }
    
    @Override
    public ExternalResourceSecurity getExternalResourceSecurity(final ParsedURL resourceURL, final ParsedURL docURL) {
        return new RelaxedExternalResourceSecurity(resourceURL, docURL);
    }
    
    @Override
    public void checkLoadExternalResource(final ParsedURL resourceURL, final ParsedURL docURL) throws SecurityException {
        final ExternalResourceSecurity s = this.getExternalResourceSecurity(resourceURL, docURL);
        if (s != null) {
            s.checkLoadExternalResource();
        }
    }
    
    public static float getStandardLighterFontWeight(final float f) {
        final int weight = (int)((f + 50.0f) / 100.0f) * 100;
        switch (weight) {
            case 100: {
                return 100.0f;
            }
            case 200: {
                return 100.0f;
            }
            case 300: {
                return 200.0f;
            }
            case 400: {
                return 300.0f;
            }
            case 500: {
                return 400.0f;
            }
            case 600: {
                return 400.0f;
            }
            case 700: {
                return 400.0f;
            }
            case 800: {
                return 400.0f;
            }
            case 900: {
                return 400.0f;
            }
            default: {
                throw new IllegalArgumentException("Bad Font Weight: " + f);
            }
        }
    }
    
    public static float getStandardBolderFontWeight(final float f) {
        final int weight = (int)((f + 50.0f) / 100.0f) * 100;
        switch (weight) {
            case 100: {
                return 600.0f;
            }
            case 200: {
                return 600.0f;
            }
            case 300: {
                return 600.0f;
            }
            case 400: {
                return 600.0f;
            }
            case 500: {
                return 600.0f;
            }
            case 600: {
                return 700.0f;
            }
            case 700: {
                return 800.0f;
            }
            case 800: {
                return 900.0f;
            }
            case 900: {
                return 900.0f;
            }
            default: {
                throw new IllegalArgumentException("Bad Font Weight: " + f);
            }
        }
    }
    
    @Override
    public SVGDocument getBrokenLinkDocument(final Element e, final String url, final String message) {
        throw new BridgeException(this.ctx, e, "uri.image.broken", new Object[] { url, message });
    }
    
    @Override
    public void loadDocument(final String url) {
    }
    
    @Override
    public FontFamilyResolver getFontFamilyResolver() {
        return DefaultFontFamilyResolver.SINGLETON;
    }
}
