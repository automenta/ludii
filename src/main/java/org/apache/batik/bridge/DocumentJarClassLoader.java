// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.Enumeration;
import java.security.Permission;
import java.security.Policy;
import java.security.PermissionCollection;
import java.security.cert.Certificate;
import java.net.URL;
import java.security.CodeSource;
import java.net.URLClassLoader;

public class DocumentJarClassLoader extends URLClassLoader
{
    protected CodeSource documentCodeSource;
    
    public DocumentJarClassLoader(final URL jarURL, final URL documentURL) {
        super(new URL[] { jarURL });
        this.documentCodeSource = null;
        if (documentURL != null) {
            this.documentCodeSource = new CodeSource(documentURL, (Certificate[])null);
        }
    }
    
    @Override
    protected PermissionCollection getPermissions(final CodeSource codesource) {
        final Policy p = Policy.getPolicy();
        PermissionCollection pc = null;
        if (p != null) {
            pc = p.getPermissions(codesource);
        }
        if (this.documentCodeSource != null) {
            final PermissionCollection urlPC = super.getPermissions(this.documentCodeSource);
            if (pc != null) {
                final Enumeration items = urlPC.elements();
                while (items.hasMoreElements()) {
                    pc.add(items.nextElement());
                }
            }
            else {
                pc = urlPC;
            }
        }
        return pc;
    }
}
