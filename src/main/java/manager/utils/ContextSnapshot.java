// 
// Decompiled by Procyon v0.5.36
// 

package manager.utils;

import util.Context;

public class ContextSnapshot
{
    static int counter;
    private static Context copyOfCurrentContext;
    
    private ContextSnapshot() {
    }
    
    public static void setContext(final Context context) {
        ContextSnapshot.copyOfCurrentContext = new Context(context);
    }
    
    public static Context getContext() {
        return ContextSnapshot.copyOfCurrentContext;
    }
    
    static {
        ContextSnapshot.counter = 0;
        ContextSnapshot.copyOfCurrentContext = null;
    }
}
