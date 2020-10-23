// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

public class NoLoadScriptSecurity implements ScriptSecurity
{
    public static final String ERROR_NO_SCRIPT_OF_TYPE_ALLOWED = "NoLoadScriptSecurity.error.no.script.of.type.allowed";
    protected SecurityException se;
    
    @Override
    public void checkLoadScript() {
        throw this.se;
    }
    
    public NoLoadScriptSecurity(final String scriptType) {
        this.se = new SecurityException(Messages.formatMessage("NoLoadScriptSecurity.error.no.script.of.type.allowed", new Object[] { scriptType }));
    }
}
