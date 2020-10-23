// 
// Decompiled by Procyon v0.5.36
// 

package main;

public final class Utilities
{
    public static void stackTrace() {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        System.out.println("======================");
        for (int n = 0; n < ste.length; ++n) {
            System.out.println(ste[n]);
        }
        System.out.println("======================");
    }
}
