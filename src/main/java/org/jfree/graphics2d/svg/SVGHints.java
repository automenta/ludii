// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d.svg;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class SVGHints
{
    public static final Key KEY_IMAGE_HANDLING;
    public static final Object VALUE_IMAGE_HANDLING_EMBED;
    public static final Object VALUE_IMAGE_HANDLING_REFERENCE;
    public static final Key KEY_TEXT_RENDERING;
    public static final String VALUE_TEXT_RENDERING_AUTO = "auto";
    public static final String VALUE_TEXT_RENDERING_SPEED = "optimizeSpeed";
    public static final String VALUE_TEXT_RENDERING_LEGIBILITY = "optimizeLegibility";
    public static final String VALUE_TEXT_RENDERING_PRECISION = "geometricPrecision";
    public static final String VALUE_TEXT_RENDERING_INHERIT = "inherit";
    public static final Key KEY_IMAGE_HREF;
    public static final Key KEY_ELEMENT_ID;
    public static final Key KEY_BEGIN_GROUP;
    public static final Key KEY_END_GROUP;
    public static final Key KEY_ELEMENT_TITLE;
    public static final Key KEY_DRAW_STRING_TYPE;
    public static final Object VALUE_DRAW_STRING_TYPE_STANDARD;
    public static final Object VALUE_DRAW_STRING_TYPE_VECTOR;
    private static final List<RenderingHints.Key> beginGroupKeys;
    private static final List<RenderingHints.Key> endGroupKeys;
    private static final List<RenderingHints.Key> elementTitleKeys;
    
    private SVGHints() {
    }
    
    public static List<RenderingHints.Key> getBeginGroupKeys() {
        return new ArrayList<>(SVGHints.beginGroupKeys);
    }
    
    public static void addBeginGroupKey(final RenderingHints.Key key) {
        SVGHints.beginGroupKeys.add(key);
    }
    
    public static void removeBeginGroupKey(final RenderingHints.Key key) {
        SVGHints.beginGroupKeys.remove(key);
    }
    
    public static void clearBeginGroupKeys() {
        SVGHints.beginGroupKeys.clear();
    }
    
    public static boolean isBeginGroupKey(final RenderingHints.Key key) {
        return SVGHints.KEY_BEGIN_GROUP.equals(key) || SVGHints.beginGroupKeys.contains(key);
    }
    
    public static List<RenderingHints.Key> getEndGroupKeys() {
        return new ArrayList<>(SVGHints.endGroupKeys);
    }
    
    public static void addEndGroupKey(final RenderingHints.Key key) {
        SVGHints.endGroupKeys.add(key);
    }
    
    public static void removeEndGroupKey(final RenderingHints.Key key) {
        SVGHints.endGroupKeys.remove(key);
    }
    
    public static void clearEndGroupKeys() {
        SVGHints.endGroupKeys.clear();
    }
    
    public static boolean isEndGroupKey(final RenderingHints.Key key) {
        return SVGHints.KEY_END_GROUP.equals(key) || SVGHints.endGroupKeys.contains(key);
    }
    
    public static List<RenderingHints.Key> getElementTitleKeys() {
        return new ArrayList<>(SVGHints.elementTitleKeys);
    }
    
    public static void addElementTitleKey(final RenderingHints.Key key) {
        SVGHints.elementTitleKeys.add(key);
    }
    
    public static void removeElementTitleKey(final RenderingHints.Key key) {
        SVGHints.elementTitleKeys.remove(key);
    }
    
    public static void clearElementTitleKeys() {
        SVGHints.elementTitleKeys.clear();
    }
    
    public static boolean isElementTitleKey(final RenderingHints.Key key) {
        return SVGHints.KEY_ELEMENT_TITLE.equals(key) || SVGHints.elementTitleKeys.contains(key);
    }
    
    private static boolean isOrsonChartsOnClasspath() {
        return getOrsonChartsBeginElementKey() != null;
    }
    
    private static boolean isJFreeChartOnClasspath() {
        return getJFreeChartBeginElementKey() != null;
    }
    
    private static RenderingHints.Key fetchKey(final String className, final String fieldName) {
        try {
            final Class<?> hintsClass = Class.forName(className);
            final Field f = hintsClass.getDeclaredField(fieldName);
            return (RenderingHints.Key)f.get(null);
        }
        catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | SecurityException | NoSuchFieldException e) {
            return null;
        }
    }
    
    private static RenderingHints.Key getOrsonChartsBeginElementKey() {
        return fetchKey("com.orsoncharts.Chart3DHints", "KEY_BEGIN_ELEMENT");
    }
    
    private static RenderingHints.Key getOrsonChartsEndElementKey() {
        return fetchKey("com.orsoncharts.Chart3DHints", "KEY_END_ELEMENT");
    }
    
    private static RenderingHints.Key getOrsonChartsElementTitleKey() {
        return fetchKey("com.orsoncharts.Chart3DHints", "KEY_ELEMENT_TITLE");
    }
    
    private static RenderingHints.Key getJFreeChartBeginElementKey() {
        return fetchKey("org.jfree.chart.ChartHints", "KEY_BEGIN_ELEMENT");
    }
    
    private static RenderingHints.Key getJFreeChartEndElementKey() {
        return fetchKey("org.jfree.chart.ChartHints", "KEY_END_ELEMENT");
    }
    
    static {
        KEY_IMAGE_HANDLING = new Key(0);
        VALUE_IMAGE_HANDLING_EMBED = "VALUE_IMAGE_HANDLING_EMBED";
        VALUE_IMAGE_HANDLING_REFERENCE = "VALUE_IMAGE_HANDLING_REFERENCE";
        KEY_TEXT_RENDERING = new Key(1);
        KEY_IMAGE_HREF = new Key(2);
        KEY_ELEMENT_ID = new Key(3);
        KEY_BEGIN_GROUP = new Key(4);
        KEY_END_GROUP = new Key(5);
        KEY_ELEMENT_TITLE = new Key(6);
        KEY_DRAW_STRING_TYPE = new Key(7);
        VALUE_DRAW_STRING_TYPE_STANDARD = "VALUE_DRAW_STRING_TYPE_STANDARD";
        VALUE_DRAW_STRING_TYPE_VECTOR = "VALUE_DRAW_STRING_TYPE_VECTOR";
        beginGroupKeys = new ArrayList<>();
        endGroupKeys = new ArrayList<>();
        elementTitleKeys = new ArrayList<>();
        if (isOrsonChartsOnClasspath()) {
            SVGHints.beginGroupKeys.add(getOrsonChartsBeginElementKey());
            SVGHints.endGroupKeys.add(getOrsonChartsEndElementKey());
            SVGHints.elementTitleKeys.add(getOrsonChartsElementTitleKey());
        }
        if (isJFreeChartOnClasspath()) {
            SVGHints.beginGroupKeys.add(getJFreeChartBeginElementKey());
            SVGHints.endGroupKeys.add(getJFreeChartEndElementKey());
        }
    }
    
    public static class Key extends RenderingHints.Key
    {
        public Key(final int privateKey) {
            super(privateKey);
        }
        
        @Override
        public boolean isCompatibleValue(final Object val) {
            switch (this.intKey()) {
                case 0 -> {
                    return SVGHints.VALUE_IMAGE_HANDLING_EMBED.equals(val) || SVGHints.VALUE_IMAGE_HANDLING_REFERENCE.equals(val);
                }
                case 1 -> {
                    return "auto".equals(val) || "inherit".equals(val) || "optimizeLegibility".equals(val) || "geometricPrecision".equals(val) || "optimizeSpeed".equals(val);
                }
                case 2, 4, 3 -> {
                    return val == null || val instanceof String;
                }
                case 5 -> {
                    return true;
                }
                case 6 -> {
                    return val instanceof String;
                }
                case 7 -> {
                    return val == null || SVGHints.VALUE_DRAW_STRING_TYPE_STANDARD.equals(val) || SVGHints.VALUE_DRAW_STRING_TYPE_VECTOR.equals(val);
                }
                default -> throw new RuntimeException("Not possible!");
            }
        }
    }
}
