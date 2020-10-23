// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocalizableSupport implements Localizable
{
    protected LocaleGroup localeGroup;
    protected String bundleName;
    protected ClassLoader classLoader;
    protected Locale locale;
    protected Locale usedLocale;
    List resourceBundles;
    Class lastResourceClass;
    Class cls;
    
    public LocalizableSupport(final String s, final Class cls) {
        this(s, cls, null);
    }
    
    public LocalizableSupport(final String s, final Class cls, final ClassLoader cl) {
        this.localeGroup = LocaleGroup.DEFAULT;
        this.resourceBundles = new ArrayList();
        this.bundleName = s;
        this.cls = cls;
        this.classLoader = cl;
    }
    
    public LocalizableSupport(final String s) {
        this(s, (ClassLoader)null);
    }
    
    public LocalizableSupport(final String s, final ClassLoader cl) {
        this.localeGroup = LocaleGroup.DEFAULT;
        this.resourceBundles = new ArrayList();
        this.bundleName = s;
        this.classLoader = cl;
    }
    
    @Override
    public void setLocale(final Locale l) {
        if (this.locale != l) {
            this.locale = l;
            this.resourceBundles.clear();
            this.lastResourceClass = null;
        }
    }
    
    @Override
    public Locale getLocale() {
        return this.locale;
    }
    
    public void setLocaleGroup(final LocaleGroup lg) {
        this.localeGroup = lg;
    }
    
    public LocaleGroup getLocaleGroup() {
        return this.localeGroup;
    }
    
    public void setDefaultLocale(final Locale l) {
        this.localeGroup.setLocale(l);
    }
    
    public Locale getDefaultLocale() {
        return this.localeGroup.getLocale();
    }
    
    @Override
    public String formatMessage(final String key, final Object[] args) {
        return MessageFormat.format(this.getString(key), args);
    }
    
    protected Locale getCurrentLocale() {
        if (this.locale != null) {
            return this.locale;
        }
        final Locale l = this.localeGroup.getLocale();
        if (l != null) {
            return l;
        }
        return Locale.getDefault();
    }
    
    protected boolean setUsedLocale() {
        final Locale l = this.getCurrentLocale();
        if (this.usedLocale == l) {
            return false;
        }
        this.usedLocale = l;
        this.resourceBundles.clear();
        this.lastResourceClass = null;
        return true;
    }
    
    public ResourceBundle getResourceBundle() {
        return this.getResourceBundle(0);
    }
    
    protected boolean hasNextResourceBundle(final int i) {
        return i == 0 || i < this.resourceBundles.size() || (this.lastResourceClass != null && this.lastResourceClass != Object.class);
    }
    
    protected ResourceBundle lookupResourceBundle(final String bundle, final Class theClass) {
        ClassLoader cl = this.classLoader;
        ResourceBundle rb = null;
        if (cl != null) {
            try {
                rb = ResourceBundle.getBundle(bundle, this.usedLocale, cl);
            }
            catch (MissingResourceException ex) {}
            if (rb != null) {
                return rb;
            }
        }
        if (theClass != null) {
            try {
                cl = theClass.getClassLoader();
            }
            catch (SecurityException ex2) {}
        }
        if (cl == null) {
            cl = this.getClass().getClassLoader();
        }
        try {
            rb = ResourceBundle.getBundle(bundle, this.usedLocale, cl);
        }
        catch (MissingResourceException ex3) {}
        return rb;
    }
    
    protected ResourceBundle getResourceBundle(final int i) {
        this.setUsedLocale();
        ResourceBundle rb = null;
        if (this.cls == null) {
            if (this.resourceBundles.size() == 0) {
                rb = this.lookupResourceBundle(this.bundleName, null);
                this.resourceBundles.add(rb);
            }
            return this.resourceBundles.get(0);
        }
        while (i >= this.resourceBundles.size()) {
            if (this.lastResourceClass == Object.class) {
                return null;
            }
            if (this.lastResourceClass == null) {
                this.lastResourceClass = this.cls;
            }
            else {
                this.lastResourceClass = this.lastResourceClass.getSuperclass();
            }
            final Class cl = this.lastResourceClass;
            final String bundle = cl.getPackage().getName() + "." + this.bundleName;
            this.resourceBundles.add(this.lookupResourceBundle(bundle, cl));
        }
        return this.resourceBundles.get(i);
    }
    
    public String getString(final String key) throws MissingResourceException {
        this.setUsedLocale();
        for (int i = 0; this.hasNextResourceBundle(i); ++i) {
            final ResourceBundle rb = this.getResourceBundle(i);
            if (rb != null) {
                try {
                    final String ret = rb.getString(key);
                    if (ret != null) {
                        return ret;
                    }
                }
                catch (MissingResourceException ex) {}
            }
        }
        final String classStr = (this.cls != null) ? this.cls.toString() : this.bundleName;
        throw new MissingResourceException("Unable to find resource: " + key, classStr, key);
    }
    
    public int getInteger(final String key) throws MissingResourceException {
        final String i = this.getString(key);
        try {
            return Integer.parseInt(i);
        }
        catch (NumberFormatException e) {
            throw new MissingResourceException("Malformed integer", this.bundleName, key);
        }
    }
    
    public int getCharacter(final String key) throws MissingResourceException {
        final String s = this.getString(key);
        if (s == null || s.length() == 0) {
            throw new MissingResourceException("Malformed character", this.bundleName, key);
        }
        return s.charAt(0);
    }
}
