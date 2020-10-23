// 
// Decompiled by Procyon v0.5.36
// 

package main;

public final class Utilities
{
    public static void stackTrace() {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        System.out.println("======================");
        for (StackTraceElement stackTraceElement : ste) {
            System.out.println(stackTraceElement);
        }
        System.out.println("======================");
    }
}
