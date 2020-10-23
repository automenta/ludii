package root;/*
 * Decompiled with CFR 0.150.
 */

public final class Utilities {
    public static void stackTrace() {
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        System.out.println("======================");
        for (int n = 0; n < ste.length; ++n) {
            System.out.println(ste[n]);
        }
        System.out.println("======================");
    }
}

