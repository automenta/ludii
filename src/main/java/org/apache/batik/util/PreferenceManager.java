// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.awt.Font;
import java.awt.Color;
import java.awt.Point;
import java.awt.Dimension;
import java.util.StringTokenizer;
import java.awt.Rectangle;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.security.AccessControlException;
import java.util.Map;
import java.util.Properties;

public class PreferenceManager
{
    protected Properties internal;
    protected Map defaults;
    protected String prefFileName;
    protected String fullName;
    protected static final String USER_HOME;
    protected static final String USER_DIR;
    protected static final String FILE_SEP;
    private static String PREF_DIR;
    
    protected static String getSystemProperty(final String prop) {
        try {
            return System.getProperty(prop);
        }
        catch (AccessControlException e) {
            return "";
        }
    }
    
    public PreferenceManager(final String prefFileName) {
        this(prefFileName, null);
    }
    
    public PreferenceManager(final String prefFileName, final Map defaults) {
        this.internal = null;
        this.defaults = null;
        this.prefFileName = null;
        this.fullName = null;
        this.prefFileName = prefFileName;
        this.defaults = defaults;
        this.internal = new Properties();
    }
    
    public static void setPreferenceDirectory(final String dir) {
        PreferenceManager.PREF_DIR = dir;
    }
    
    public static String getPreferenceDirectory() {
        return PreferenceManager.PREF_DIR;
    }
    
    public void load() throws IOException {
        FileInputStream fis = null;
        if (this.fullName != null) {
            try {
                fis = new FileInputStream(this.fullName);
            }
            catch (IOException e1) {
                this.fullName = null;
            }
        }
        if (this.fullName == null) {
            if (PreferenceManager.PREF_DIR != null) {
                try {
                    final String string = PreferenceManager.PREF_DIR + PreferenceManager.FILE_SEP + this.prefFileName;
                    this.fullName = string;
                    fis = new FileInputStream(string);
                }
                catch (IOException e2) {
                    this.fullName = null;
                }
            }
            if (this.fullName == null) {
                try {
                    final String string2 = PreferenceManager.USER_HOME + PreferenceManager.FILE_SEP + this.prefFileName;
                    this.fullName = string2;
                    fis = new FileInputStream(string2);
                }
                catch (IOException e3) {
                    try {
                        final String string3 = PreferenceManager.USER_DIR + PreferenceManager.FILE_SEP + this.prefFileName;
                        this.fullName = string3;
                        fis = new FileInputStream(string3);
                    }
                    catch (IOException e4) {
                        this.fullName = null;
                    }
                }
            }
        }
        if (this.fullName != null) {
            try {
                this.internal.load(fis);
            }
            finally {
                fis.close();
            }
        }
    }
    
    public void save() throws IOException {
        FileOutputStream fos = null;
        if (this.fullName != null) {
            try {
                fos = new FileOutputStream(this.fullName);
            }
            catch (IOException e4) {
                this.fullName = null;
            }
        }
        if (this.fullName == null) {
            if (PreferenceManager.PREF_DIR != null) {
                try {
                    final String string = PreferenceManager.PREF_DIR + PreferenceManager.FILE_SEP + this.prefFileName;
                    this.fullName = string;
                    fos = new FileOutputStream(string);
                }
                catch (IOException e5) {
                    this.fullName = null;
                }
            }
            if (this.fullName == null) {
                try {
                    final String string2 = PreferenceManager.USER_HOME + PreferenceManager.FILE_SEP + this.prefFileName;
                    this.fullName = string2;
                    fos = new FileOutputStream(string2);
                }
                catch (IOException e3) {
                    this.fullName = null;
                    throw e3;
                }
            }
        }
        try {
            this.internal.store(fos, this.prefFileName);
        }
        finally {
            fos.close();
        }
    }
    
    private Object getDefault(final String key) {
        if (this.defaults != null) {
            return this.defaults.get(key);
        }
        return null;
    }
    
    public Rectangle getRectangle(final String key) {
        final Rectangle defaultValue = (Rectangle)this.getDefault(key);
        final String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        final Rectangle result = new Rectangle();
        try {
            final StringTokenizer st = new StringTokenizer(sp, " ", false);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            String token = st.nextToken();
            final int x = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            final int y = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            final int w = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            final int h = Integer.parseInt(token);
            result.setBounds(x, y, w, h);
            return result;
        }
        catch (NumberFormatException e) {
            this.internal.remove(key);
            return defaultValue;
        }
    }
    
    public Dimension getDimension(final String key) {
        final Dimension defaultValue = (Dimension)this.getDefault(key);
        final String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        final Dimension result = new Dimension();
        try {
            final StringTokenizer st = new StringTokenizer(sp, " ", false);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            String token = st.nextToken();
            final int w = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            final int h = Integer.parseInt(token);
            result.setSize(w, h);
            return result;
        }
        catch (NumberFormatException e) {
            this.internal.remove(key);
            return defaultValue;
        }
    }
    
    public Point getPoint(final String key) {
        final Point defaultValue = (Point)this.getDefault(key);
        final String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        final Point result = new Point();
        try {
            final StringTokenizer st = new StringTokenizer(sp, " ", false);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            String token = st.nextToken();
            final int x = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            final int y = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            result.setLocation(x, y);
            return result;
        }
        catch (NumberFormatException e) {
            this.internal.remove(key);
            return defaultValue;
        }
    }
    
    public Color getColor(final String key) {
        final Color defaultValue = (Color)this.getDefault(key);
        final String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        try {
            final StringTokenizer st = new StringTokenizer(sp, " ", false);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            String token = st.nextToken();
            final int r = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            final int g = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            final int b = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            final int a = Integer.parseInt(token);
            return new Color(r, g, b, a);
        }
        catch (NumberFormatException e) {
            this.internal.remove(key);
            return defaultValue;
        }
    }
    
    public Font getFont(final String key) {
        final Font defaultValue = (Font)this.getDefault(key);
        final String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        try {
            final StringTokenizer st = new StringTokenizer(sp, " ", false);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            final String name = st.nextToken();
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            String token = st.nextToken();
            final int size = Integer.parseInt(token);
            if (!st.hasMoreTokens()) {
                this.internal.remove(key);
                return defaultValue;
            }
            token = st.nextToken();
            final int type = Integer.parseInt(token);
            return new Font(name, type, size);
        }
        catch (NumberFormatException e) {
            this.internal.remove(key);
            return defaultValue;
        }
    }
    
    public String getString(final String key) {
        String sp = this.internal.getProperty(key);
        if (sp == null) {
            sp = (String)this.getDefault(key);
        }
        return sp;
    }
    
    public String[] getStrings(final String mkey) {
        int i = 0;
        final ArrayList v = new ArrayList();
        while (true) {
            final String last = this.getString(mkey + i);
            ++i;
            if (last == null) {
                break;
            }
            v.add(last);
        }
        if (v.size() != 0) {
            final String[] str = new String[v.size()];
            return v.toArray(str);
        }
        return (String[])this.getDefault(mkey);
    }
    
    public URL getURL(final String key) {
        final URL defaultValue = (URL)this.getDefault(key);
        final String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        URL url = null;
        try {
            url = new URL(sp);
        }
        catch (MalformedURLException ex) {
            this.internal.remove(key);
            return defaultValue;
        }
        return url;
    }
    
    public URL[] getURLs(final String mkey) {
        int i = 0;
        final ArrayList v = new ArrayList();
        while (true) {
            final URL last = this.getURL(mkey + i);
            ++i;
            if (last == null) {
                break;
            }
            v.add(last);
        }
        if (v.size() != 0) {
            final URL[] path = new URL[v.size()];
            return v.toArray(path);
        }
        return (URL[])this.getDefault(mkey);
    }
    
    public File getFile(final String key) {
        final File defaultValue = (File)this.getDefault(key);
        final String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        final File file = new File(sp);
        if (file.exists()) {
            return file;
        }
        this.internal.remove(key);
        return defaultValue;
    }
    
    public File[] getFiles(final String mkey) {
        int i = 0;
        final ArrayList v = new ArrayList();
        while (true) {
            final File last = this.getFile(mkey + i);
            ++i;
            if (last == null) {
                break;
            }
            v.add(last);
        }
        if (v.size() != 0) {
            final File[] path = new File[v.size()];
            return v.toArray(path);
        }
        return (File[])this.getDefault(mkey);
    }
    
    public int getInteger(final String key) {
        int defaultValue = 0;
        if (this.getDefault(key) != null) {
            defaultValue = (int)this.getDefault(key);
        }
        final String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        int value;
        try {
            value = Integer.parseInt(sp);
        }
        catch (NumberFormatException ex) {
            this.internal.remove(key);
            return defaultValue;
        }
        return value;
    }
    
    public float getFloat(final String key) {
        float defaultValue = 0.0f;
        if (this.getDefault(key) != null) {
            defaultValue = (float)this.getDefault(key);
        }
        final String sp = this.internal.getProperty(key);
        if (sp == null) {
            return defaultValue;
        }
        float value;
        try {
            value = Float.parseFloat(sp);
        }
        catch (NumberFormatException ex) {
            this.setFloat(key, defaultValue);
            return defaultValue;
        }
        return value;
    }
    
    public boolean getBoolean(final String key) {
        if (this.internal.getProperty(key) != null) {
            return this.internal.getProperty(key).equals("true");
        }
        return this.getDefault(key) != null && (boolean)this.getDefault(key);
    }
    
    public void setRectangle(final String key, final Rectangle value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value.x + " " + value.y + " " + value.width + ' ' + value.height);
        }
        else {
            this.internal.remove(key);
        }
    }
    
    public void setDimension(final String key, final Dimension value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value.width + " " + value.height);
        }
        else {
            this.internal.remove(key);
        }
    }
    
    public void setPoint(final String key, final Point value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value.x + " " + value.y);
        }
        else {
            this.internal.remove(key);
        }
    }
    
    public void setColor(final String key, final Color value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value.getRed() + " " + value.getGreen() + " " + value.getBlue() + " " + value.getAlpha());
        }
        else {
            this.internal.remove(key);
        }
    }
    
    public void setFont(final String key, final Font value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value.getName() + " " + value.getSize() + " " + value.getStyle());
        }
        else {
            this.internal.remove(key);
        }
    }
    
    public void setString(final String key, final String value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value);
        }
        else {
            this.internal.remove(key);
        }
    }
    
    public void setStrings(final String mkey, final String[] values) {
        int j = 0;
        if (values != null) {
            for (final String value : values) {
                if (value != null) {
                    this.setString(mkey + j, value);
                    ++j;
                }
            }
        }
        while (true) {
            final String last = this.getString(mkey + j);
            if (last == null) {
                break;
            }
            this.setString(mkey + j, null);
            ++j;
        }
    }
    
    public void setURL(final String key, final URL value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value.toString());
        }
        else {
            this.internal.remove(key);
        }
    }
    
    public void setURLs(final String mkey, final URL[] values) {
        int j = 0;
        if (values != null) {
            for (final URL value : values) {
                if (value != null) {
                    this.setURL(mkey + j, value);
                    ++j;
                }
            }
        }
        while (true) {
            final String last = this.getString(mkey + j);
            if (last == null) {
                break;
            }
            this.setString(mkey + j, null);
            ++j;
        }
    }
    
    public void setFile(final String key, final File value) {
        if (value != null && !value.equals(this.getDefault(key))) {
            this.internal.setProperty(key, value.getAbsolutePath());
        }
        else {
            this.internal.remove(key);
        }
    }
    
    public void setFiles(final String mkey, final File[] values) {
        int j = 0;
        if (values != null) {
            for (final File value : values) {
                if (value != null) {
                    this.setFile(mkey + j, value);
                    ++j;
                }
            }
        }
        while (true) {
            final String last = this.getString(mkey + j);
            if (last == null) {
                break;
            }
            this.setString(mkey + j, null);
            ++j;
        }
    }
    
    public void setInteger(final String key, final int value) {
        if (this.getDefault(key) != null && (int)this.getDefault(key) != value) {
            this.internal.setProperty(key, Integer.toString(value));
        }
        else {
            this.internal.remove(key);
        }
    }
    
    public void setFloat(final String key, final float value) {
        if (this.getDefault(key) != null && (float)this.getDefault(key) != value) {
            this.internal.setProperty(key, Float.toString(value));
        }
        else {
            this.internal.remove(key);
        }
    }
    
    public void setBoolean(final String key, final boolean value) {
        if (this.getDefault(key) != null && (boolean)this.getDefault(key) != value) {
            this.internal.setProperty(key, value ? "true" : "false");
        }
        else {
            this.internal.remove(key);
        }
    }
    
    static {
        USER_HOME = getSystemProperty("user.home");
        USER_DIR = getSystemProperty("user.dir");
        FILE_SEP = getSystemProperty("file.separator");
        PreferenceManager.PREF_DIR = null;
    }
}
