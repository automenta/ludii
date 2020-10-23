// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value;

public class URIValue extends StringValue
{
    String cssText;
    
    public URIValue(final String cssText, final String uri) {
        super((short)20, uri);
        this.cssText = cssText;
    }
    
    @Override
    public String getCssText() {
        return "url(" + this.cssText + ')';
    }
}
