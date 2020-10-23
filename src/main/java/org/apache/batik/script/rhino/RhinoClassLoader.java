// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.script.rhino;

import java.security.Permission;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.security.AccessControlContext;
import java.security.CodeSource;
import java.net.URL;
import org.mozilla.javascript.GeneratedClassLoader;
import java.net.URLClassLoader;

public class RhinoClassLoader extends URLClassLoader implements GeneratedClassLoader
{
    protected URL documentURL;
    protected CodeSource codeSource;
    protected AccessControlContext rhinoAccessControlContext;
    
    public RhinoClassLoader(final URL documentURL, final ClassLoader parent) {
        super((documentURL != null) ? new URL[] { documentURL } : new URL[0], parent);
        this.documentURL = documentURL;
        if (documentURL != null) {
            this.codeSource = new CodeSource(documentURL, (Certificate[])null);
        }
        final ProtectionDomain rhinoProtectionDomain = new ProtectionDomain(this.codeSource, this.getPermissions(this.codeSource));
        this.rhinoAccessControlContext = new AccessControlContext(new ProtectionDomain[] { rhinoProtectionDomain });
    }
    
    static URL[] getURL(final ClassLoader parent) {
        if (!(parent instanceof RhinoClassLoader)) {
            return new URL[0];
        }
        final URL documentURL = ((RhinoClassLoader)parent).documentURL;
        if (documentURL != null) {
            return new URL[] { documentURL };
        }
        return new URL[0];
    }
    
    public Class defineClass(final String name, final byte[] data) {
        return super.defineClass(name, data, 0, data.length, this.codeSource);
    }
    
    public void linkClass(final Class clazz) {
        super.resolveClass(clazz);
    }
    
    public AccessControlContext getAccessControlContext() {
        return this.rhinoAccessControlContext;
    }
    
    @Override
    protected PermissionCollection getPermissions(final CodeSource codesource) {
        PermissionCollection perms = null;
        if (codesource != null) {
            perms = super.getPermissions(codesource);
        }
        if (this.documentURL != null && perms != null) {
            Permission p = null;
            Permission dirPerm = null;
            try {
                p = this.documentURL.openConnection().getPermission();
            }
            catch (IOException e) {
                p = null;
            }
            if (p instanceof FilePermission) {
                String path = p.getName();
                if (!path.endsWith(File.separator)) {
                    final int dirEnd = path.lastIndexOf(File.separator);
                    if (dirEnd != -1) {
                        path = path.substring(0, dirEnd + 1);
                        path += "-";
                        dirPerm = new FilePermission(path, "read");
                        perms.add(dirPerm);
                    }
                }
            }
        }
        return perms;
    }
}
