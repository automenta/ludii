// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.script.rhino;

import org.mozilla.javascript.ClassShutter;

public class RhinoClassShutter implements ClassShutter
{
    public boolean visibleToScripts(final String fullClassName) {
        if (fullClassName.startsWith("org.mozilla.javascript")) {
            return false;
        }
        if (fullClassName.startsWith("org.apache.batik.")) {
            final String batikPkg = fullClassName.substring(17);
            if (batikPkg.startsWith("script")) {
                return false;
            }
            if (batikPkg.startsWith("apps")) {
                return false;
            }
            if (batikPkg.startsWith("bridge.")) {
                final String batikBridgeClass = batikPkg.substring(7);
                if (batikBridgeClass.startsWith("ScriptingEnvironment")) {
                    if (batikBridgeClass.startsWith("$Window$", 20)) {
                        final String c = batikBridgeClass.substring(28);
                        if (c.equals("IntervalScriptTimerTask") || c.equals("IntervalRunnableTimerTask") || c.equals("TimeoutScriptTimerTask") || c.equals("TimeoutRunnableTimerTask")) {
                            return true;
                        }
                    }
                    return false;
                }
                if (batikBridgeClass.startsWith("BaseScriptingEnvironment")) {
                    return false;
                }
            }
        }
        return true;
    }
}
