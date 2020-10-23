// 
// Decompiled by Procyon v0.5.36
// 

package main;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtils
{
    public static Object[] castArray(final Object array) {
        final Object[] casted = new Object[Array.getLength(array)];
        for (int i = 0; i < casted.length; ++i) {
            casted[i] = Array.get(array, i);
        }
        return casted;
    }
    
    public static List<Field> getAllFields(final Class<?> clazz) {
        final List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            fields.addAll(getAllFields(clazz.getSuperclass()));
        }
        return fields;
    }
}
