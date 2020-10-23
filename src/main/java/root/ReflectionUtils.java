package root;/*
 * Decompiled with CFR 0.150.
 */

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtils {
    public static Object[] castArray(Object array) {
        Object[] casted = new Object[Array.getLength(array)];
        for (int i = 0; i < casted.length; ++i) {
            casted[i] = Array.get(array, i);
        }
        return casted;
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        ArrayList<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            fields.addAll(ReflectionUtils.getAllFields(clazz.getSuperclass()));
        }
        return fields;
    }
}

