// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.MissingResourceException;
import java.util.Locale;
import org.apache.batik.i18n.LocalizableSupport;

public class Messages
{
    protected static final String RESOURCES = "org.apache.batik.bridge.resources.Messages";
    protected static LocalizableSupport localizableSupport;
    
    protected Messages() {
    }
    
    public static void setLocale(final Locale l) {
        Messages.localizableSupport.setLocale(l);
    }
    
    public static Locale getLocale() {
        return Messages.localizableSupport.getLocale();
    }
    
    public static String formatMessage(final String key, final Object[] args) throws MissingResourceException {
        return Messages.localizableSupport.formatMessage(key, args);
    }
    
    public static String getMessage(final String key) throws MissingResourceException {
        return formatMessage(key, null);
    }
    
    static {
        Messages.localizableSupport = new LocalizableSupport("org.apache.batik.bridge.resources.Messages", Messages.class.getClassLoader());
    }
}
