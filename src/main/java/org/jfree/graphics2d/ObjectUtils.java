// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d;

public final class ObjectUtils
{
    private ObjectUtils() {
    }
    
    public static boolean equals(final Object obj1, final Object obj2) {
        if (obj1 == null) {
            return obj2 == null;
        }
        return obj1.equals(obj2);
    }
    
    public static int hashCode(final Object obj) {
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }
}
