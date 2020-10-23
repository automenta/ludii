// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine;

import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;
import org.apache.batik.css.engine.value.Value;

public interface CSSContext
{
    Value getSystemColor(final String p0);
    
    Value getDefaultFontFamily();
    
    float getLighterFontWeight(final float p0);
    
    float getBolderFontWeight(final float p0);
    
    float getPixelUnitToMillimeter();
    
    float getPixelToMillimeter();
    
    float getMediumFontSize();
    
    float getBlockWidth(final Element p0);
    
    float getBlockHeight(final Element p0);
    
    void checkLoadExternalResource(final ParsedURL p0, final ParsedURL p1) throws SecurityException;
    
    boolean isDynamic();
    
    boolean isInteractive();
    
    CSSEngine getCSSEngineForElement(final Element p0);
}
