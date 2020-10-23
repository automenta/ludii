// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import java.util.MissingResourceException;
import java.util.Locale;
import org.apache.batik.dom.events.EventSupport;
import org.apache.batik.dom.events.DocumentEventSupport;
import java.util.HashMap;
import org.apache.batik.i18n.LocalizableSupport;
import java.io.Serializable;
import org.apache.batik.i18n.Localizable;
import org.w3c.dom.DOMImplementation;

public abstract class AbstractDOMImplementation implements DOMImplementation, Localizable, Serializable
{
    protected static final String RESOURCES = "org.apache.batik.dom.resources.Messages";
    protected LocalizableSupport localizableSupport;
    protected final HashMap<String, Object> features;
    
    protected void registerFeature(final String name, final Object value) {
        this.features.put(name.toLowerCase(), value);
    }
    
    protected AbstractDOMImplementation() {
        this.localizableSupport = new LocalizableSupport("org.apache.batik.dom.resources.Messages", this.getClass().getClassLoader());
        this.features = new HashMap<String, Object>();
        this.registerFeature("Core", new String[] { "2.0", "3.0" });
        this.registerFeature("XML", new String[] { "1.0", "2.0", "3.0" });
        this.registerFeature("Events", new String[] { "2.0", "3.0" });
        this.registerFeature("UIEvents", new String[] { "2.0", "3.0" });
        this.registerFeature("MouseEvents", new String[] { "2.0", "3.0" });
        this.registerFeature("TextEvents", "3.0");
        this.registerFeature("KeyboardEvents", "3.0");
        this.registerFeature("MutationEvents", new String[] { "2.0", "3.0" });
        this.registerFeature("MutationNameEvents", "3.0");
        this.registerFeature("Traversal", "2.0");
        this.registerFeature("XPath", "3.0");
    }
    
    @Override
    public boolean hasFeature(String feature, final String version) {
        if (feature == null || feature.length() == 0) {
            return false;
        }
        if (feature.charAt(0) == '+') {
            feature = feature.substring(1);
        }
        final Object v = this.features.get(feature.toLowerCase());
        if (v == null) {
            return false;
        }
        if (version == null || version.length() == 0) {
            return true;
        }
        if (v instanceof String) {
            return version.equals(v);
        }
        final String[] arr$;
        final String[] va = arr$ = (String[])v;
        for (final String aVa : arr$) {
            if (version.equals(aVa)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Object getFeature(final String feature, final String version) {
        if (this.hasFeature(feature, version)) {
            return this;
        }
        return null;
    }
    
    public DocumentEventSupport createDocumentEventSupport() {
        return new DocumentEventSupport();
    }
    
    public EventSupport createEventSupport(final AbstractNode n) {
        return new EventSupport(n);
    }
    
    @Override
    public void setLocale(final Locale l) {
        this.localizableSupport.setLocale(l);
    }
    
    @Override
    public Locale getLocale() {
        return this.localizableSupport.getLocale();
    }
    
    protected void initLocalizable() {
    }
    
    @Override
    public String formatMessage(final String key, final Object[] args) throws MissingResourceException {
        return this.localizableSupport.formatMessage(key, args);
    }
}
