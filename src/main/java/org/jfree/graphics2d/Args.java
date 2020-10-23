// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d;

public class Args
{
    private Args() {
    }
    
    public static void nullNotPermitted(final Object obj, final String ref) {
        if (obj == null) {
            throw new IllegalArgumentException("Null '" + ref + "' argument.");
        }
    }
    
    public static void arrayMustHaveLength(final int length, final boolean[] array, final String ref) {
        nullNotPermitted(array, "array");
        if (array.length != length) {
            throw new IllegalArgumentException("Array '" + ref + "' requires length " + length);
        }
    }
    
    public static void arrayMustHaveLength(final int length, final double[] array, final String ref) {
        nullNotPermitted(array, "array");
        if (array.length != length) {
            throw new IllegalArgumentException("Array '" + ref + "' requires length " + length);
        }
    }
}
