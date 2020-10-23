// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.io.Reader;
import java.io.InputStream;
import java.util.Enumeration;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;

public class Service
{
    static HashMap providerMap;
    
    public static synchronized Iterator providers(final Class cls) {
        final String serviceFile = "META-INF/services/" + cls.getName();
        List l = Service.providerMap.get(serviceFile);
        if (l != null) {
            return l.iterator();
        }
        l = new ArrayList();
        Service.providerMap.put(serviceFile, l);
        ClassLoader cl = null;
        try {
            cl = cls.getClassLoader();
        }
        catch (SecurityException ex2) {}
        if (cl == null) {
            cl = Service.class.getClassLoader();
        }
        if (cl == null) {
            return l.iterator();
        }
        Enumeration e;
        try {
            e = cl.getResources(serviceFile);
        }
        catch (IOException ioe) {
            return l.iterator();
        }
        while (e.hasMoreElements()) {
            InputStream is = null;
            Reader r = null;
            BufferedReader br = null;
            try {
                final URL u = e.nextElement();
                is = u.openStream();
                r = new InputStreamReader(is, "UTF-8");
                br = new BufferedReader(r);
                String line = br.readLine();
                while (line != null) {
                    try {
                        final int idx = line.indexOf(35);
                        if (idx != -1) {
                            line = line.substring(0, idx);
                        }
                        line = line.trim();
                        if (line.length() == 0) {
                            line = br.readLine();
                            continue;
                        }
                        final Object obj = cl.loadClass(line).getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                        l.add(obj);
                    }
                    catch (Exception ex3) {}
                    line = br.readLine();
                }
            }
            catch (Exception ex) {}
            catch (LinkageError le) {}
            finally {
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (IOException ex4) {}
                    is = null;
                }
                if (r != null) {
                    try {
                        r.close();
                    }
                    catch (IOException ex5) {}
                    r = null;
                }
                if (br != null) {
                    try {
                        br.close();
                    }
                    catch (IOException ex6) {}
                    br = null;
                }
            }
        }
        return l.iterator();
    }
    
    static {
        Service.providerMap = new HashMap();
    }
}
