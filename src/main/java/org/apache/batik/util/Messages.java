// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.util.MissingResourceException;
import java.util.Locale;
import org.apache.batik.util.resources.ResourceManager;
import org.apache.batik.i18n.LocalizableSupport;

public class Messages
{
    protected static final String RESOURCES = "org.apache.batik.util.resources.Messages";
    protected static LocalizableSupport localizableSupport;
    protected static ResourceManager resourceManager;
    
    protected Messages() {
    }
    
    public static void setLocale(final Locale l) {
        Messages.localizableSupport.setLocale(l);
        Messages.resourceManager = new ResourceManager(Messages.localizableSupport.getResourceBundle());
    }
    
    public static Locale getLocale() {
        return Messages.localizableSupport.getLocale();
    }
    
    public static String formatMessage(final String key, final Object[] args) throws MissingResourceException {
        return Messages.localizableSupport.formatMessage(key, args);
    }
    
    public static String getString(final String key) throws MissingResourceException {
        return Messages.resourceManager.getString(key);
    }
    
    public static int getInteger(final String key) throws MissingResourceException {
        return Messages.resourceManager.getInteger(key);
    }
    
    public static int getCharacter(final String key) throws MissingResourceException {
        return Messages.resourceManager.getCharacter(key);
    }
    
    static {
        Messages.localizableSupport = new LocalizableSupport("org.apache.batik.util.resources.Messages", Messages.class.getClassLoader());
        Messages.resourceManager = new ResourceManager(Messages.localizableSupport.getResourceBundle());
    }
}
