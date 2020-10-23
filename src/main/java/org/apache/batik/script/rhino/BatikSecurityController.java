// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.script.rhino;

import org.mozilla.javascript.WrappedException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessControlContext;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import java.security.AccessController;
import org.mozilla.javascript.GeneratedClassLoader;
import org.mozilla.javascript.SecurityController;

public class BatikSecurityController extends SecurityController
{
    public GeneratedClassLoader createClassLoader(final ClassLoader parentLoader, final Object securityDomain) {
        if (securityDomain instanceof RhinoClassLoader) {
            return (GeneratedClassLoader)securityDomain;
        }
        throw new SecurityException("Script() objects are not supported");
    }
    
    public Object getDynamicSecurityDomain(final Object securityDomain) {
        final ClassLoader loader = (RhinoClassLoader)securityDomain;
        if (loader != null) {
            return loader;
        }
        return AccessController.getContext();
    }
    
    public Object callWithDomain(final Object securityDomain, final Context cx, final Callable callable, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
        AccessControlContext acc;
        if (securityDomain instanceof AccessControlContext) {
            acc = (AccessControlContext)securityDomain;
        }
        else {
            final RhinoClassLoader loader = (RhinoClassLoader)securityDomain;
            acc = loader.rhinoAccessControlContext;
        }
        final PrivilegedExceptionAction execAction = new PrivilegedExceptionAction() {
            @Override
            public Object run() {
                return callable.call(cx, scope, thisObj, args);
            }
        };
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Object>)execAction, acc);
        }
        catch (Exception e) {
            throw new WrappedException((Throwable)e);
        }
    }
}
