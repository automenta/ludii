// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.util.ParsedURL;

public class DefaultExternalResourceSecurity implements ExternalResourceSecurity
{
    public static final String DATA_PROTOCOL = "data";
    public static final String ERROR_CANNOT_ACCESS_DOCUMENT_URL = "DefaultExternalResourceSecurity.error.cannot.access.document.url";
    public static final String ERROR_EXTERNAL_RESOURCE_FROM_DIFFERENT_URL = "DefaultExternalResourceSecurity.error.external.resource.from.different.url";
    protected SecurityException se;
    
    @Override
    public void checkLoadExternalResource() {
        if (this.se != null) {
            this.se.fillInStackTrace();
            throw this.se;
        }
    }
    
    public DefaultExternalResourceSecurity(final ParsedURL externalResourceURL, final ParsedURL docURL) {
        if (docURL == null) {
            this.se = new SecurityException(Messages.formatMessage("DefaultExternalResourceSecurity.error.cannot.access.document.url", new Object[] { externalResourceURL }));
        }
        else {
            final String docHost = docURL.getHost();
            final String externalResourceHost = externalResourceURL.getHost();
            if (docHost != externalResourceHost && (docHost == null || !docHost.equals(externalResourceHost)) && (externalResourceURL == null || !"data".equals(externalResourceURL.getProtocol()))) {
                this.se = new SecurityException(Messages.formatMessage("DefaultExternalResourceSecurity.error.external.resource.from.different.url", new Object[] { externalResourceURL }));
            }
        }
    }
}
