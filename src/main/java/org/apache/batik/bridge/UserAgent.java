// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.svg.SVGDocument;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.Cursor;
import org.w3c.dom.svg.SVGAElement;
import java.awt.geom.Dimension2D;
import org.apache.batik.gvt.event.EventDispatcher;

public interface UserAgent
{
    EventDispatcher getEventDispatcher();
    
    Dimension2D getViewportSize();
    
    void displayError(final Exception p0);
    
    void displayMessage(final String p0);
    
    void showAlert(final String p0);
    
    String showPrompt(final String p0);
    
    String showPrompt(final String p0, final String p1);
    
    boolean showConfirm(final String p0);
    
    float getPixelUnitToMillimeter();
    
    float getPixelToMM();
    
    float getMediumFontSize();
    
    float getLighterFontWeight(final float p0);
    
    float getBolderFontWeight(final float p0);
    
    String getDefaultFontFamily();
    
    String getLanguages();
    
    String getUserStyleSheetURI();
    
    void openLink(final SVGAElement p0);
    
    void setSVGCursor(final Cursor p0);
    
    void setTextSelection(final Mark p0, final Mark p1);
    
    void deselectAll();
    
    String getXMLParserClassName();
    
    boolean isXMLParserValidating();
    
    AffineTransform getTransform();
    
    void setTransform(final AffineTransform p0);
    
    String getMedia();
    
    String getAlternateStyleSheet();
    
    Point getClientAreaLocationOnScreen();
    
    boolean hasFeature(final String p0);
    
    boolean supportExtension(final String p0);
    
    void registerExtension(final BridgeExtension p0);
    
    void handleElement(final Element p0, final Object p1);
    
    ScriptSecurity getScriptSecurity(final String p0, final ParsedURL p1, final ParsedURL p2);
    
    void checkLoadScript(final String p0, final ParsedURL p1, final ParsedURL p2) throws SecurityException;
    
    ExternalResourceSecurity getExternalResourceSecurity(final ParsedURL p0, final ParsedURL p1);
    
    void checkLoadExternalResource(final ParsedURL p0, final ParsedURL p1) throws SecurityException;
    
    SVGDocument getBrokenLinkDocument(final Element p0, final String p1, final String p2);
    
    void loadDocument(final String p0);
    
    FontFamilyResolver getFontFamilyResolver();
}
