// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.i18n;

import java.util.MissingResourceException;
import java.util.Locale;

public interface Localizable
{
    void setLocale(final Locale p0);
    
    Locale getLocale();
    
    String formatMessage(final String p0, final Object[] p1) throws MissingResourceException;
}
