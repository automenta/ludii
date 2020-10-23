// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.i18n;

import java.util.Locale;

public class LocaleGroup
{
    public static final LocaleGroup DEFAULT;
    protected Locale locale;
    
    public void setLocale(final Locale l) {
        this.locale = l;
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    static {
        DEFAULT = new LocaleGroup();
    }
}
