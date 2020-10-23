// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.security.Policy;
import java.net.URL;

public class ApplicationSecurityEnforcer
{
    public static final String EXCEPTION_ALIEN_SECURITY_MANAGER = "ApplicationSecurityEnforcer.message.security.exception.alien.security.manager";
    public static final String EXCEPTION_NO_POLICY_FILE = "ApplicationSecurityEnforcer.message.null.pointer.exception.no.policy.file";
    public static final String PROPERTY_JAVA_SECURITY_POLICY = "java.security.policy";
    public static final String JAR_PROTOCOL = "jar:";
    public static final String JAR_URL_FILE_SEPARATOR = "!/";
    public static final String PROPERTY_APP_DEV_BASE = "app.dev.base";
    public static final String PROPERTY_APP_JAR_BASE = "app.jar.base";
    public static final String APP_MAIN_CLASS_DIR = "classes/";
    protected Class appMainClass;
    protected String securityPolicy;
    protected String appMainClassRelativeURL;
    protected BatikSecurityManager lastSecurityManagerInstalled;
    
    @Deprecated
    public ApplicationSecurityEnforcer(final Class appMainClass, final String securityPolicy, final String appJarFile) {
        this(appMainClass, securityPolicy);
    }
    
    public ApplicationSecurityEnforcer(final Class appMainClass, final String securityPolicy) {
        this.appMainClass = appMainClass;
        this.securityPolicy = securityPolicy;
        this.appMainClassRelativeURL = appMainClass.getName().replace('.', '/') + ".class";
    }
    
    public void enforceSecurity(final boolean enforce) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null && sm != this.lastSecurityManagerInstalled) {
            throw new SecurityException(Messages.getString("ApplicationSecurityEnforcer.message.security.exception.alien.security.manager"));
        }
        if (enforce) {
            System.setSecurityManager(null);
            this.installSecurityManager();
        }
        else if (sm != null) {
            System.setSecurityManager(null);
            this.lastSecurityManagerInstalled = null;
        }
    }
    
    public URL getPolicyURL() {
        final ClassLoader cl = this.appMainClass.getClassLoader();
        final URL policyURL = cl.getResource(this.securityPolicy);
        if (policyURL == null) {
            throw new NullPointerException(Messages.formatMessage("ApplicationSecurityEnforcer.message.null.pointer.exception.no.policy.file", new Object[] { this.securityPolicy }));
        }
        return policyURL;
    }
    
    public void installSecurityManager() {
        final Policy policy = Policy.getPolicy();
        final BatikSecurityManager securityManager = new BatikSecurityManager();
        final ClassLoader cl = this.appMainClass.getClassLoader();
        final String securityPolicyProperty = System.getProperty("java.security.policy");
        if (securityPolicyProperty == null || securityPolicyProperty.equals("")) {
            final URL policyURL = this.getPolicyURL();
            System.setProperty("java.security.policy", policyURL.toString());
        }
        final URL mainClassURL = cl.getResource(this.appMainClassRelativeURL);
        if (mainClassURL == null) {
            throw new RuntimeException(this.appMainClassRelativeURL);
        }
        final String expandedMainClassName = mainClassURL.toString();
        if (expandedMainClassName.startsWith("jar:")) {
            this.setJarBase(expandedMainClassName);
        }
        else {
            this.setDevBase(expandedMainClassName);
        }
        System.setSecurityManager(securityManager);
        this.lastSecurityManagerInstalled = securityManager;
        policy.refresh();
        if (securityPolicyProperty == null || securityPolicyProperty.equals("")) {
            System.setProperty("java.security.policy", "");
        }
    }
    
    private void setJarBase(String expandedMainClassName) {
        final String curAppJarBase = System.getProperty("app.jar.base");
        if (curAppJarBase == null) {
            expandedMainClassName = expandedMainClassName.substring("jar:".length());
            int codeBaseEnd = expandedMainClassName.indexOf("!/" + this.appMainClassRelativeURL);
            if (codeBaseEnd == -1) {
                throw new RuntimeException();
            }
            String appCodeBase = expandedMainClassName.substring(0, codeBaseEnd);
            codeBaseEnd = appCodeBase.lastIndexOf(47);
            if (codeBaseEnd == -1) {
                appCodeBase = "";
            }
            else {
                appCodeBase = appCodeBase.substring(0, codeBaseEnd);
            }
            System.setProperty("app.jar.base", appCodeBase);
        }
    }
    
    private void setDevBase(final String expandedMainClassName) {
        final String curAppCodeBase = System.getProperty("app.dev.base");
        if (curAppCodeBase == null) {
            final int codeBaseEnd = expandedMainClassName.indexOf("classes/" + this.appMainClassRelativeURL);
            if (codeBaseEnd == -1) {
                throw new RuntimeException();
            }
            final String appCodeBase = expandedMainClassName.substring(0, codeBaseEnd);
            System.setProperty("app.dev.base", appCodeBase);
        }
    }
}
