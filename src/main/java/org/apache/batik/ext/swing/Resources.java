// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.swing;

import java.util.MissingResourceException;
import java.util.Locale;
import org.apache.batik.util.resources.ResourceManager;
import org.apache.batik.i18n.LocalizableSupport;

public class Resources
{
    protected static final String RESOURCES = "org.apache.batik.ext.swing.resources.Messages";
    protected static LocalizableSupport localizableSupport;
    protected static ResourceManager resourceManager;
    
    protected Resources() {
    }
    
    public static void setLocale(final Locale l) {
        Resources.localizableSupport.setLocale(l);
        Resources.resourceManager = new ResourceManager(Resources.localizableSupport.getResourceBundle());
    }
    
    public static Locale getLocale() {
        return Resources.localizableSupport.getLocale();
    }
    
    public static String formatMessage(final String key, final Object[] args) throws MissingResourceException {
        return Resources.localizableSupport.formatMessage(key, args);
    }
    
    public static String getString(final String key) throws MissingResourceException {
        return Resources.resourceManager.getString(key);
    }
    
    public static int getInteger(final String key) throws MissingResourceException {
        return Resources.resourceManager.getInteger(key);
    }
    
    static {
        Resources.localizableSupport = new LocalizableSupport("org.apache.batik.ext.swing.resources.Messages", Resources.class.getClassLoader());
        Resources.resourceManager = new ResourceManager(Resources.localizableSupport.getResourceBundle());
    }
}
