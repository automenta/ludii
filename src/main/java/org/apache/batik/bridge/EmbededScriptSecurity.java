// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.util.ParsedURL;

public class EmbededScriptSecurity implements ScriptSecurity
{
    public static final String DATA_PROTOCOL = "data";
    public static final String ERROR_CANNOT_ACCESS_DOCUMENT_URL = "DefaultScriptSecurity.error.cannot.access.document.url";
    public static final String ERROR_SCRIPT_NOT_EMBEDED = "EmbededScriptSecurity.error.script.not.embeded";
    protected SecurityException se;
    
    @Override
    public void checkLoadScript() {
        if (this.se != null) {
            throw this.se;
        }
    }
    
    public EmbededScriptSecurity(final String scriptType, final ParsedURL scriptURL, final ParsedURL docURL) {
        if (docURL == null) {
            this.se = new SecurityException(Messages.formatMessage("DefaultScriptSecurity.error.cannot.access.document.url", new Object[] { scriptURL }));
        }
        else if (!docURL.equals(scriptURL) && (scriptURL == null || !"data".equals(scriptURL.getProtocol()))) {
            this.se = new SecurityException(Messages.formatMessage("EmbededScriptSecurity.error.script.not.embeded", new Object[] { scriptURL }));
        }
    }
}
