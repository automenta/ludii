// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util.io;

import java.util.MissingResourceException;
import java.util.Locale;
import org.apache.batik.i18n.LocalizableSupport;

public class Messages
{
    protected static final String RESOURCES = "org.apache.batik.util.io.Messages";
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
    
    static {
        Messages.localizableSupport = new LocalizableSupport("org.apache.batik.util.io.Messages", Messages.class.getClassLoader());
    }
}
