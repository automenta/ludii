// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

public class Trace
{
    private static int level;
    private static boolean enabled;
    
    public static void enter(final Object o, final String fn, final Object[] args) {
        if (Trace.enabled) {
            System.err.print("LOG\t");
            for (int i = 0; i < Trace.level; ++i) {
                System.err.print("  ");
            }
            if (fn == null) {
                System.err.print("new " + o.getClass().getName() + "(");
            }
            else {
                System.err.print(o + "." + fn + "(");
            }
            if (args != null) {
                System.err.print(args[0]);
                for (int i = 1; i < args.length; ++i) {
                    System.err.print(", " + args[i]);
                }
            }
            System.err.println(")");
        }
        ++Trace.level;
    }
    
    public static void exit() {
        --Trace.level;
    }
    
    public static void print(final String s) {
        if (Trace.enabled) {
            System.err.print("LOG\t");
            for (int i = 0; i < Trace.level; ++i) {
                System.err.print("  ");
            }
            System.err.println(s);
        }
    }
    
    static {
        Trace.enabled = false;
    }
}
