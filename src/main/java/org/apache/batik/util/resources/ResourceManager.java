// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util.resources;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceManager
{
    protected ResourceBundle bundle;
    
    public ResourceManager(final ResourceBundle rb) {
        this.bundle = rb;
    }
    
    public String getString(final String key) throws MissingResourceException {
        return this.bundle.getString(key);
    }
    
    public List getStringList(final String key) throws MissingResourceException {
        return this.getStringList(key, " \t\n\r\f", false);
    }
    
    public List getStringList(final String key, final String delim) throws MissingResourceException {
        return this.getStringList(key, delim, false);
    }
    
    public List getStringList(final String key, final String delim, final boolean returnDelims) throws MissingResourceException {
        final List result = new ArrayList();
        final StringTokenizer st = new StringTokenizer(this.getString(key), delim, returnDelims);
        while (st.hasMoreTokens()) {
            result.add(st.nextToken());
        }
        return result;
    }
    
    public boolean getBoolean(final String key) throws MissingResourceException, ResourceFormatException {
        final String b = this.getString(key);
        if (b.equals("true")) {
            return true;
        }
        if (b.equals("false")) {
            return false;
        }
        throw new ResourceFormatException("Malformed boolean", this.bundle.getClass().getName(), key);
    }
    
    public int getInteger(final String key) throws MissingResourceException, ResourceFormatException {
        final String i = this.getString(key);
        try {
            return Integer.parseInt(i);
        }
        catch (NumberFormatException e) {
            throw new ResourceFormatException("Malformed integer", this.bundle.getClass().getName(), key);
        }
    }
    
    public int getCharacter(final String key) throws MissingResourceException, ResourceFormatException {
        final String s = this.getString(key);
        if (s == null || s.length() == 0) {
            throw new ResourceFormatException("Malformed character", this.bundle.getClass().getName(), key);
        }
        return s.charAt(0);
    }
}
