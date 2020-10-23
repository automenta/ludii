// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

public class NoLoadExternalResourceSecurity implements ExternalResourceSecurity
{
    public static final String ERROR_NO_EXTERNAL_RESOURCE_ALLOWED = "NoLoadExternalResourceSecurity.error.no.external.resource.allowed";
    protected SecurityException se;
    
    @Override
    public void checkLoadExternalResource() {
        if (this.se != null) {
            this.se.fillInStackTrace();
            throw this.se;
        }
    }
    
    public NoLoadExternalResourceSecurity() {
        this.se = new SecurityException(Messages.formatMessage("NoLoadExternalResourceSecurity.error.no.external.resource.allowed", null));
    }
}
