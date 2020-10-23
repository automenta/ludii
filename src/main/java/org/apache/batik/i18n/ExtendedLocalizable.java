// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.i18n;

import java.util.ResourceBundle;
import java.util.Locale;

public interface ExtendedLocalizable extends Localizable
{
    void setLocaleGroup(final LocaleGroup p0);
    
    LocaleGroup getLocaleGroup();
    
    void setDefaultLocale(final Locale p0);
    
    Locale getDefaultLocale();
    
    ResourceBundle getResourceBundle();
}
