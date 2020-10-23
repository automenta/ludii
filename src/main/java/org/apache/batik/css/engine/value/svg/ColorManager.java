// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.AbstractColorManager;

public class ColorManager extends AbstractColorManager
{
    protected static final Value DEFAULT_VALUE;
    
    @Override
    public boolean isInheritedProperty() {
        return true;
    }
    
    @Override
    public boolean isAnimatableProperty() {
        return true;
    }
    
    @Override
    public boolean isAdditiveProperty() {
        return true;
    }
    
    @Override
    public int getPropertyType() {
        return 6;
    }
    
    @Override
    public String getPropertyName() {
        return "color";
    }
    
    @Override
    public Value getDefaultValue() {
        return ColorManager.DEFAULT_VALUE;
    }
    
    static {
        DEFAULT_VALUE = SVGValueConstants.BLACK_RGB_VALUE;
        ColorManager.values.put("aliceblue", SVGValueConstants.ALICEBLUE_VALUE);
        ColorManager.values.put("antiquewhite", SVGValueConstants.ANTIQUEWHITE_VALUE);
        ColorManager.values.put("aquamarine", SVGValueConstants.AQUAMARINE_VALUE);
        ColorManager.values.put("azure", SVGValueConstants.AZURE_VALUE);
        ColorManager.values.put("beige", SVGValueConstants.BEIGE_VALUE);
        ColorManager.values.put("bisque", SVGValueConstants.BISQUE_VALUE);
        ColorManager.values.put("blanchedalmond", SVGValueConstants.BLANCHEDALMOND_VALUE);
        ColorManager.values.put("blueviolet", SVGValueConstants.BLUEVIOLET_VALUE);
        ColorManager.values.put("brown", SVGValueConstants.BROWN_VALUE);
        ColorManager.values.put("burlywood", SVGValueConstants.BURLYWOOD_VALUE);
        ColorManager.values.put("cadetblue", SVGValueConstants.CADETBLUE_VALUE);
        ColorManager.values.put("chartreuse", SVGValueConstants.CHARTREUSE_VALUE);
        ColorManager.values.put("chocolate", SVGValueConstants.CHOCOLATE_VALUE);
        ColorManager.values.put("coral", SVGValueConstants.CORAL_VALUE);
        ColorManager.values.put("cornflowerblue", SVGValueConstants.CORNFLOWERBLUE_VALUE);
        ColorManager.values.put("cornsilk", SVGValueConstants.CORNSILK_VALUE);
        ColorManager.values.put("crimson", SVGValueConstants.CRIMSON_VALUE);
        ColorManager.values.put("cyan", SVGValueConstants.CYAN_VALUE);
        ColorManager.values.put("darkblue", SVGValueConstants.DARKBLUE_VALUE);
        ColorManager.values.put("darkcyan", SVGValueConstants.DARKCYAN_VALUE);
        ColorManager.values.put("darkgoldenrod", SVGValueConstants.DARKGOLDENROD_VALUE);
        ColorManager.values.put("darkgray", SVGValueConstants.DARKGRAY_VALUE);
        ColorManager.values.put("darkgreen", SVGValueConstants.DARKGREEN_VALUE);
        ColorManager.values.put("darkgrey", SVGValueConstants.DARKGREY_VALUE);
        ColorManager.values.put("darkkhaki", SVGValueConstants.DARKKHAKI_VALUE);
        ColorManager.values.put("darkmagenta", SVGValueConstants.DARKMAGENTA_VALUE);
        ColorManager.values.put("darkolivegreen", SVGValueConstants.DARKOLIVEGREEN_VALUE);
        ColorManager.values.put("darkorange", SVGValueConstants.DARKORANGE_VALUE);
        ColorManager.values.put("darkorchid", SVGValueConstants.DARKORCHID_VALUE);
        ColorManager.values.put("darkred", SVGValueConstants.DARKRED_VALUE);
        ColorManager.values.put("darksalmon", SVGValueConstants.DARKSALMON_VALUE);
        ColorManager.values.put("darkseagreen", SVGValueConstants.DARKSEAGREEN_VALUE);
        ColorManager.values.put("darkslateblue", SVGValueConstants.DARKSLATEBLUE_VALUE);
        ColorManager.values.put("darkslategray", SVGValueConstants.DARKSLATEGRAY_VALUE);
        ColorManager.values.put("darkslategrey", SVGValueConstants.DARKSLATEGREY_VALUE);
        ColorManager.values.put("darkturquoise", SVGValueConstants.DARKTURQUOISE_VALUE);
        ColorManager.values.put("darkviolet", SVGValueConstants.DARKVIOLET_VALUE);
        ColorManager.values.put("deeppink", SVGValueConstants.DEEPPINK_VALUE);
        ColorManager.values.put("deepskyblue", SVGValueConstants.DEEPSKYBLUE_VALUE);
        ColorManager.values.put("dimgray", SVGValueConstants.DIMGRAY_VALUE);
        ColorManager.values.put("dimgrey", SVGValueConstants.DIMGREY_VALUE);
        ColorManager.values.put("dodgerblue", SVGValueConstants.DODGERBLUE_VALUE);
        ColorManager.values.put("firebrick", SVGValueConstants.FIREBRICK_VALUE);
        ColorManager.values.put("floralwhite", SVGValueConstants.FLORALWHITE_VALUE);
        ColorManager.values.put("forestgreen", SVGValueConstants.FORESTGREEN_VALUE);
        ColorManager.values.put("gainsboro", SVGValueConstants.GAINSBORO_VALUE);
        ColorManager.values.put("ghostwhite", SVGValueConstants.GHOSTWHITE_VALUE);
        ColorManager.values.put("gold", SVGValueConstants.GOLD_VALUE);
        ColorManager.values.put("goldenrod", SVGValueConstants.GOLDENROD_VALUE);
        ColorManager.values.put("greenyellow", SVGValueConstants.GREENYELLOW_VALUE);
        ColorManager.values.put("grey", SVGValueConstants.GREY_VALUE);
        ColorManager.values.put("honeydew", SVGValueConstants.HONEYDEW_VALUE);
        ColorManager.values.put("hotpink", SVGValueConstants.HOTPINK_VALUE);
        ColorManager.values.put("indianred", SVGValueConstants.INDIANRED_VALUE);
        ColorManager.values.put("indigo", SVGValueConstants.INDIGO_VALUE);
        ColorManager.values.put("ivory", SVGValueConstants.IVORY_VALUE);
        ColorManager.values.put("khaki", SVGValueConstants.KHAKI_VALUE);
        ColorManager.values.put("lavender", SVGValueConstants.LAVENDER_VALUE);
        ColorManager.values.put("lavenderblush", SVGValueConstants.LAVENDERBLUSH_VALUE);
        ColorManager.values.put("lawngreen", SVGValueConstants.LAWNGREEN_VALUE);
        ColorManager.values.put("lemonchiffon", SVGValueConstants.LEMONCHIFFON_VALUE);
        ColorManager.values.put("lightblue", SVGValueConstants.LIGHTBLUE_VALUE);
        ColorManager.values.put("lightcoral", SVGValueConstants.LIGHTCORAL_VALUE);
        ColorManager.values.put("lightcyan", SVGValueConstants.LIGHTCYAN_VALUE);
        ColorManager.values.put("lightgoldenrodyellow", SVGValueConstants.LIGHTGOLDENRODYELLOW_VALUE);
        ColorManager.values.put("lightgray", SVGValueConstants.LIGHTGRAY_VALUE);
        ColorManager.values.put("lightgreen", SVGValueConstants.LIGHTGREEN_VALUE);
        ColorManager.values.put("lightgrey", SVGValueConstants.LIGHTGREY_VALUE);
        ColorManager.values.put("lightpink", SVGValueConstants.LIGHTPINK_VALUE);
        ColorManager.values.put("lightsalmon", SVGValueConstants.LIGHTSALMON_VALUE);
        ColorManager.values.put("lightseagreen", SVGValueConstants.LIGHTSEAGREEN_VALUE);
        ColorManager.values.put("lightskyblue", SVGValueConstants.LIGHTSKYBLUE_VALUE);
        ColorManager.values.put("lightslategray", SVGValueConstants.LIGHTSLATEGRAY_VALUE);
        ColorManager.values.put("lightslategrey", SVGValueConstants.LIGHTSLATEGREY_VALUE);
        ColorManager.values.put("lightsteelblue", SVGValueConstants.LIGHTSTEELBLUE_VALUE);
        ColorManager.values.put("lightyellow", SVGValueConstants.LIGHTYELLOW_VALUE);
        ColorManager.values.put("limegreen", SVGValueConstants.LIMEGREEN_VALUE);
        ColorManager.values.put("linen", SVGValueConstants.LINEN_VALUE);
        ColorManager.values.put("magenta", SVGValueConstants.MAGENTA_VALUE);
        ColorManager.values.put("mediumaquamarine", SVGValueConstants.MEDIUMAQUAMARINE_VALUE);
        ColorManager.values.put("mediumblue", SVGValueConstants.MEDIUMBLUE_VALUE);
        ColorManager.values.put("mediumorchid", SVGValueConstants.MEDIUMORCHID_VALUE);
        ColorManager.values.put("mediumpurple", SVGValueConstants.MEDIUMPURPLE_VALUE);
        ColorManager.values.put("mediumseagreen", SVGValueConstants.MEDIUMSEAGREEN_VALUE);
        ColorManager.values.put("mediumslateblue", SVGValueConstants.MEDIUMSLATEBLUE_VALUE);
        ColorManager.values.put("mediumspringgreen", SVGValueConstants.MEDIUMSPRINGGREEN_VALUE);
        ColorManager.values.put("mediumturquoise", SVGValueConstants.MEDIUMTURQUOISE_VALUE);
        ColorManager.values.put("mediumvioletred", SVGValueConstants.MEDIUMVIOLETRED_VALUE);
        ColorManager.values.put("midnightblue", SVGValueConstants.MIDNIGHTBLUE_VALUE);
        ColorManager.values.put("mintcream", SVGValueConstants.MINTCREAM_VALUE);
        ColorManager.values.put("mistyrose", SVGValueConstants.MISTYROSE_VALUE);
        ColorManager.values.put("moccasin", SVGValueConstants.MOCCASIN_VALUE);
        ColorManager.values.put("navajowhite", SVGValueConstants.NAVAJOWHITE_VALUE);
        ColorManager.values.put("oldlace", SVGValueConstants.OLDLACE_VALUE);
        ColorManager.values.put("olivedrab", SVGValueConstants.OLIVEDRAB_VALUE);
        ColorManager.values.put("orange", SVGValueConstants.ORANGE_VALUE);
        ColorManager.values.put("orangered", SVGValueConstants.ORANGERED_VALUE);
        ColorManager.values.put("orchid", SVGValueConstants.ORCHID_VALUE);
        ColorManager.values.put("palegoldenrod", SVGValueConstants.PALEGOLDENROD_VALUE);
        ColorManager.values.put("palegreen", SVGValueConstants.PALEGREEN_VALUE);
        ColorManager.values.put("paleturquoise", SVGValueConstants.PALETURQUOISE_VALUE);
        ColorManager.values.put("palevioletred", SVGValueConstants.PALEVIOLETRED_VALUE);
        ColorManager.values.put("papayawhip", SVGValueConstants.PAPAYAWHIP_VALUE);
        ColorManager.values.put("peachpuff", SVGValueConstants.PEACHPUFF_VALUE);
        ColorManager.values.put("peru", SVGValueConstants.PERU_VALUE);
        ColorManager.values.put("pink", SVGValueConstants.PINK_VALUE);
        ColorManager.values.put("plum", SVGValueConstants.PLUM_VALUE);
        ColorManager.values.put("powderblue", SVGValueConstants.POWDERBLUE_VALUE);
        ColorManager.values.put("purple", SVGValueConstants.PURPLE_VALUE);
        ColorManager.values.put("rosybrown", SVGValueConstants.ROSYBROWN_VALUE);
        ColorManager.values.put("royalblue", SVGValueConstants.ROYALBLUE_VALUE);
        ColorManager.values.put("saddlebrown", SVGValueConstants.SADDLEBROWN_VALUE);
        ColorManager.values.put("salmon", SVGValueConstants.SALMON_VALUE);
        ColorManager.values.put("sandybrown", SVGValueConstants.SANDYBROWN_VALUE);
        ColorManager.values.put("seagreen", SVGValueConstants.SEAGREEN_VALUE);
        ColorManager.values.put("seashell", SVGValueConstants.SEASHELL_VALUE);
        ColorManager.values.put("sienna", SVGValueConstants.SIENNA_VALUE);
        ColorManager.values.put("skyblue", SVGValueConstants.SKYBLUE_VALUE);
        ColorManager.values.put("slateblue", SVGValueConstants.SLATEBLUE_VALUE);
        ColorManager.values.put("slategray", SVGValueConstants.SLATEGRAY_VALUE);
        ColorManager.values.put("slategrey", SVGValueConstants.SLATEGREY_VALUE);
        ColorManager.values.put("snow", SVGValueConstants.SNOW_VALUE);
        ColorManager.values.put("springgreen", SVGValueConstants.SPRINGGREEN_VALUE);
        ColorManager.values.put("steelblue", SVGValueConstants.STEELBLUE_VALUE);
        ColorManager.values.put("tan", SVGValueConstants.TAN_VALUE);
        ColorManager.values.put("thistle", SVGValueConstants.THISTLE_VALUE);
        ColorManager.values.put("tomato", SVGValueConstants.TOMATO_VALUE);
        ColorManager.values.put("turquoise", SVGValueConstants.TURQUOISE_VALUE);
        ColorManager.values.put("violet", SVGValueConstants.VIOLET_VALUE);
        ColorManager.values.put("wheat", SVGValueConstants.WHEAT_VALUE);
        ColorManager.values.put("whitesmoke", SVGValueConstants.WHITESMOKE_VALUE);
        ColorManager.values.put("yellowgreen", SVGValueConstants.YELLOWGREEN_VALUE);
        ColorManager.computedValues.put("black", SVGValueConstants.BLACK_RGB_VALUE);
        ColorManager.computedValues.put("silver", SVGValueConstants.SILVER_RGB_VALUE);
        ColorManager.computedValues.put("gray", SVGValueConstants.GRAY_RGB_VALUE);
        ColorManager.computedValues.put("white", SVGValueConstants.WHITE_RGB_VALUE);
        ColorManager.computedValues.put("maroon", SVGValueConstants.MAROON_RGB_VALUE);
        ColorManager.computedValues.put("red", SVGValueConstants.RED_RGB_VALUE);
        ColorManager.computedValues.put("purple", SVGValueConstants.PURPLE_RGB_VALUE);
        ColorManager.computedValues.put("fuchsia", SVGValueConstants.FUCHSIA_RGB_VALUE);
        ColorManager.computedValues.put("green", SVGValueConstants.GREEN_RGB_VALUE);
        ColorManager.computedValues.put("lime", SVGValueConstants.LIME_RGB_VALUE);
        ColorManager.computedValues.put("olive", SVGValueConstants.OLIVE_RGB_VALUE);
        ColorManager.computedValues.put("yellow", SVGValueConstants.YELLOW_RGB_VALUE);
        ColorManager.computedValues.put("navy", SVGValueConstants.NAVY_RGB_VALUE);
        ColorManager.computedValues.put("blue", SVGValueConstants.BLUE_RGB_VALUE);
        ColorManager.computedValues.put("teal", SVGValueConstants.TEAL_RGB_VALUE);
        ColorManager.computedValues.put("aqua", SVGValueConstants.AQUA_RGB_VALUE);
        ColorManager.computedValues.put("aliceblue", SVGValueConstants.ALICEBLUE_RGB_VALUE);
        ColorManager.computedValues.put("antiquewhite", SVGValueConstants.ANTIQUEWHITE_RGB_VALUE);
        ColorManager.computedValues.put("aquamarine", SVGValueConstants.AQUAMARINE_RGB_VALUE);
        ColorManager.computedValues.put("azure", SVGValueConstants.AZURE_RGB_VALUE);
        ColorManager.computedValues.put("beige", SVGValueConstants.BEIGE_RGB_VALUE);
        ColorManager.computedValues.put("bisque", SVGValueConstants.BISQUE_RGB_VALUE);
        ColorManager.computedValues.put("blanchedalmond", SVGValueConstants.BLANCHEDALMOND_RGB_VALUE);
        ColorManager.computedValues.put("blueviolet", SVGValueConstants.BLUEVIOLET_RGB_VALUE);
        ColorManager.computedValues.put("brown", SVGValueConstants.BROWN_RGB_VALUE);
        ColorManager.computedValues.put("burlywood", SVGValueConstants.BURLYWOOD_RGB_VALUE);
        ColorManager.computedValues.put("cadetblue", SVGValueConstants.CADETBLUE_RGB_VALUE);
        ColorManager.computedValues.put("chartreuse", SVGValueConstants.CHARTREUSE_RGB_VALUE);
        ColorManager.computedValues.put("chocolate", SVGValueConstants.CHOCOLATE_RGB_VALUE);
        ColorManager.computedValues.put("coral", SVGValueConstants.CORAL_RGB_VALUE);
        ColorManager.computedValues.put("cornflowerblue", SVGValueConstants.CORNFLOWERBLUE_RGB_VALUE);
        ColorManager.computedValues.put("cornsilk", SVGValueConstants.CORNSILK_RGB_VALUE);
        ColorManager.computedValues.put("crimson", SVGValueConstants.CRIMSON_RGB_VALUE);
        ColorManager.computedValues.put("cyan", SVGValueConstants.CYAN_RGB_VALUE);
        ColorManager.computedValues.put("darkblue", SVGValueConstants.DARKBLUE_RGB_VALUE);
        ColorManager.computedValues.put("darkcyan", SVGValueConstants.DARKCYAN_RGB_VALUE);
        ColorManager.computedValues.put("darkgoldenrod", SVGValueConstants.DARKGOLDENROD_RGB_VALUE);
        ColorManager.computedValues.put("darkgray", SVGValueConstants.DARKGRAY_RGB_VALUE);
        ColorManager.computedValues.put("darkgreen", SVGValueConstants.DARKGREEN_RGB_VALUE);
        ColorManager.computedValues.put("darkgrey", SVGValueConstants.DARKGREY_RGB_VALUE);
        ColorManager.computedValues.put("darkkhaki", SVGValueConstants.DARKKHAKI_RGB_VALUE);
        ColorManager.computedValues.put("darkmagenta", SVGValueConstants.DARKMAGENTA_RGB_VALUE);
        ColorManager.computedValues.put("darkolivegreen", SVGValueConstants.DARKOLIVEGREEN_RGB_VALUE);
        ColorManager.computedValues.put("darkorange", SVGValueConstants.DARKORANGE_RGB_VALUE);
        ColorManager.computedValues.put("darkorchid", SVGValueConstants.DARKORCHID_RGB_VALUE);
        ColorManager.computedValues.put("darkred", SVGValueConstants.DARKRED_RGB_VALUE);
        ColorManager.computedValues.put("darksalmon", SVGValueConstants.DARKSALMON_RGB_VALUE);
        ColorManager.computedValues.put("darkseagreen", SVGValueConstants.DARKSEAGREEN_RGB_VALUE);
        ColorManager.computedValues.put("darkslateblue", SVGValueConstants.DARKSLATEBLUE_RGB_VALUE);
        ColorManager.computedValues.put("darkslategray", SVGValueConstants.DARKSLATEGRAY_RGB_VALUE);
        ColorManager.computedValues.put("darkslategrey", SVGValueConstants.DARKSLATEGREY_RGB_VALUE);
        ColorManager.computedValues.put("darkturquoise", SVGValueConstants.DARKTURQUOISE_RGB_VALUE);
        ColorManager.computedValues.put("darkviolet", SVGValueConstants.DARKVIOLET_RGB_VALUE);
        ColorManager.computedValues.put("deeppink", SVGValueConstants.DEEPPINK_RGB_VALUE);
        ColorManager.computedValues.put("deepskyblue", SVGValueConstants.DEEPSKYBLUE_RGB_VALUE);
        ColorManager.computedValues.put("dimgray", SVGValueConstants.DIMGRAY_RGB_VALUE);
        ColorManager.computedValues.put("dimgrey", SVGValueConstants.DIMGREY_RGB_VALUE);
        ColorManager.computedValues.put("dodgerblue", SVGValueConstants.DODGERBLUE_RGB_VALUE);
        ColorManager.computedValues.put("firebrick", SVGValueConstants.FIREBRICK_RGB_VALUE);
        ColorManager.computedValues.put("floralwhite", SVGValueConstants.FLORALWHITE_RGB_VALUE);
        ColorManager.computedValues.put("forestgreen", SVGValueConstants.FORESTGREEN_RGB_VALUE);
        ColorManager.computedValues.put("gainsboro", SVGValueConstants.GAINSBORO_RGB_VALUE);
        ColorManager.computedValues.put("ghostwhite", SVGValueConstants.GHOSTWHITE_RGB_VALUE);
        ColorManager.computedValues.put("gold", SVGValueConstants.GOLD_RGB_VALUE);
        ColorManager.computedValues.put("goldenrod", SVGValueConstants.GOLDENROD_RGB_VALUE);
        ColorManager.computedValues.put("grey", SVGValueConstants.GREY_RGB_VALUE);
        ColorManager.computedValues.put("greenyellow", SVGValueConstants.GREENYELLOW_RGB_VALUE);
        ColorManager.computedValues.put("honeydew", SVGValueConstants.HONEYDEW_RGB_VALUE);
        ColorManager.computedValues.put("hotpink", SVGValueConstants.HOTPINK_RGB_VALUE);
        ColorManager.computedValues.put("indianred", SVGValueConstants.INDIANRED_RGB_VALUE);
        ColorManager.computedValues.put("indigo", SVGValueConstants.INDIGO_RGB_VALUE);
        ColorManager.computedValues.put("ivory", SVGValueConstants.IVORY_RGB_VALUE);
        ColorManager.computedValues.put("khaki", SVGValueConstants.KHAKI_RGB_VALUE);
        ColorManager.computedValues.put("lavender", SVGValueConstants.LAVENDER_RGB_VALUE);
        ColorManager.computedValues.put("lavenderblush", SVGValueConstants.LAVENDERBLUSH_RGB_VALUE);
        ColorManager.computedValues.put("lawngreen", SVGValueConstants.LAWNGREEN_RGB_VALUE);
        ColorManager.computedValues.put("lemonchiffon", SVGValueConstants.LEMONCHIFFON_RGB_VALUE);
        ColorManager.computedValues.put("lightblue", SVGValueConstants.LIGHTBLUE_RGB_VALUE);
        ColorManager.computedValues.put("lightcoral", SVGValueConstants.LIGHTCORAL_RGB_VALUE);
        ColorManager.computedValues.put("lightcyan", SVGValueConstants.LIGHTCYAN_RGB_VALUE);
        ColorManager.computedValues.put("lightgoldenrodyellow", SVGValueConstants.LIGHTGOLDENRODYELLOW_RGB_VALUE);
        ColorManager.computedValues.put("lightgray", SVGValueConstants.LIGHTGRAY_RGB_VALUE);
        ColorManager.computedValues.put("lightgreen", SVGValueConstants.LIGHTGREEN_RGB_VALUE);
        ColorManager.computedValues.put("lightgrey", SVGValueConstants.LIGHTGREY_RGB_VALUE);
        ColorManager.computedValues.put("lightpink", SVGValueConstants.LIGHTPINK_RGB_VALUE);
        ColorManager.computedValues.put("lightsalmon", SVGValueConstants.LIGHTSALMON_RGB_VALUE);
        ColorManager.computedValues.put("lightseagreen", SVGValueConstants.LIGHTSEAGREEN_RGB_VALUE);
        ColorManager.computedValues.put("lightskyblue", SVGValueConstants.LIGHTSKYBLUE_RGB_VALUE);
        ColorManager.computedValues.put("lightslategray", SVGValueConstants.LIGHTSLATEGRAY_RGB_VALUE);
        ColorManager.computedValues.put("lightslategrey", SVGValueConstants.LIGHTSLATEGREY_RGB_VALUE);
        ColorManager.computedValues.put("lightsteelblue", SVGValueConstants.LIGHTSTEELBLUE_RGB_VALUE);
        ColorManager.computedValues.put("lightyellow", SVGValueConstants.LIGHTYELLOW_RGB_VALUE);
        ColorManager.computedValues.put("limegreen", SVGValueConstants.LIMEGREEN_RGB_VALUE);
        ColorManager.computedValues.put("linen", SVGValueConstants.LINEN_RGB_VALUE);
        ColorManager.computedValues.put("magenta", SVGValueConstants.MAGENTA_RGB_VALUE);
        ColorManager.computedValues.put("mediumaquamarine", SVGValueConstants.MEDIUMAQUAMARINE_RGB_VALUE);
        ColorManager.computedValues.put("mediumblue", SVGValueConstants.MEDIUMBLUE_RGB_VALUE);
        ColorManager.computedValues.put("mediumorchid", SVGValueConstants.MEDIUMORCHID_RGB_VALUE);
        ColorManager.computedValues.put("mediumpurple", SVGValueConstants.MEDIUMPURPLE_RGB_VALUE);
        ColorManager.computedValues.put("mediumseagreen", SVGValueConstants.MEDIUMSEAGREEN_RGB_VALUE);
        ColorManager.computedValues.put("mediumslateblue", SVGValueConstants.MEDIUMSLATEBLUE_RGB_VALUE);
        ColorManager.computedValues.put("mediumspringgreen", SVGValueConstants.MEDIUMSPRINGGREEN_RGB_VALUE);
        ColorManager.computedValues.put("mediumturquoise", SVGValueConstants.MEDIUMTURQUOISE_RGB_VALUE);
        ColorManager.computedValues.put("mediumvioletred", SVGValueConstants.MEDIUMVIOLETRED_RGB_VALUE);
        ColorManager.computedValues.put("midnightblue", SVGValueConstants.MIDNIGHTBLUE_RGB_VALUE);
        ColorManager.computedValues.put("mintcream", SVGValueConstants.MINTCREAM_RGB_VALUE);
        ColorManager.computedValues.put("mistyrose", SVGValueConstants.MISTYROSE_RGB_VALUE);
        ColorManager.computedValues.put("moccasin", SVGValueConstants.MOCCASIN_RGB_VALUE);
        ColorManager.computedValues.put("navajowhite", SVGValueConstants.NAVAJOWHITE_RGB_VALUE);
        ColorManager.computedValues.put("oldlace", SVGValueConstants.OLDLACE_RGB_VALUE);
        ColorManager.computedValues.put("olivedrab", SVGValueConstants.OLIVEDRAB_RGB_VALUE);
        ColorManager.computedValues.put("orange", SVGValueConstants.ORANGE_RGB_VALUE);
        ColorManager.computedValues.put("orangered", SVGValueConstants.ORANGERED_RGB_VALUE);
        ColorManager.computedValues.put("orchid", SVGValueConstants.ORCHID_RGB_VALUE);
        ColorManager.computedValues.put("palegoldenrod", SVGValueConstants.PALEGOLDENROD_RGB_VALUE);
        ColorManager.computedValues.put("palegreen", SVGValueConstants.PALEGREEN_RGB_VALUE);
        ColorManager.computedValues.put("paleturquoise", SVGValueConstants.PALETURQUOISE_RGB_VALUE);
        ColorManager.computedValues.put("palevioletred", SVGValueConstants.PALEVIOLETRED_RGB_VALUE);
        ColorManager.computedValues.put("papayawhip", SVGValueConstants.PAPAYAWHIP_RGB_VALUE);
        ColorManager.computedValues.put("peachpuff", SVGValueConstants.PEACHPUFF_RGB_VALUE);
        ColorManager.computedValues.put("peru", SVGValueConstants.PERU_RGB_VALUE);
        ColorManager.computedValues.put("pink", SVGValueConstants.PINK_RGB_VALUE);
        ColorManager.computedValues.put("plum", SVGValueConstants.PLUM_RGB_VALUE);
        ColorManager.computedValues.put("powderblue", SVGValueConstants.POWDERBLUE_RGB_VALUE);
        ColorManager.computedValues.put("purple", SVGValueConstants.PURPLE_RGB_VALUE);
        ColorManager.computedValues.put("rosybrown", SVGValueConstants.ROSYBROWN_RGB_VALUE);
        ColorManager.computedValues.put("royalblue", SVGValueConstants.ROYALBLUE_RGB_VALUE);
        ColorManager.computedValues.put("saddlebrown", SVGValueConstants.SADDLEBROWN_RGB_VALUE);
        ColorManager.computedValues.put("salmon", SVGValueConstants.SALMON_RGB_VALUE);
        ColorManager.computedValues.put("sandybrown", SVGValueConstants.SANDYBROWN_RGB_VALUE);
        ColorManager.computedValues.put("seagreen", SVGValueConstants.SEAGREEN_RGB_VALUE);
        ColorManager.computedValues.put("seashell", SVGValueConstants.SEASHELL_RGB_VALUE);
        ColorManager.computedValues.put("sienna", SVGValueConstants.SIENNA_RGB_VALUE);
        ColorManager.computedValues.put("skyblue", SVGValueConstants.SKYBLUE_RGB_VALUE);
        ColorManager.computedValues.put("slateblue", SVGValueConstants.SLATEBLUE_RGB_VALUE);
        ColorManager.computedValues.put("slategray", SVGValueConstants.SLATEGRAY_RGB_VALUE);
        ColorManager.computedValues.put("slategrey", SVGValueConstants.SLATEGREY_RGB_VALUE);
        ColorManager.computedValues.put("snow", SVGValueConstants.SNOW_RGB_VALUE);
        ColorManager.computedValues.put("springgreen", SVGValueConstants.SPRINGGREEN_RGB_VALUE);
        ColorManager.computedValues.put("steelblue", SVGValueConstants.STEELBLUE_RGB_VALUE);
        ColorManager.computedValues.put("tan", SVGValueConstants.TAN_RGB_VALUE);
        ColorManager.computedValues.put("thistle", SVGValueConstants.THISTLE_RGB_VALUE);
        ColorManager.computedValues.put("tomato", SVGValueConstants.TOMATO_RGB_VALUE);
        ColorManager.computedValues.put("turquoise", SVGValueConstants.TURQUOISE_RGB_VALUE);
        ColorManager.computedValues.put("violet", SVGValueConstants.VIOLET_RGB_VALUE);
        ColorManager.computedValues.put("wheat", SVGValueConstants.WHEAT_RGB_VALUE);
        ColorManager.computedValues.put("whitesmoke", SVGValueConstants.WHITESMOKE_RGB_VALUE);
        ColorManager.computedValues.put("yellowgreen", SVGValueConstants.YELLOWGREEN_RGB_VALUE);
    }
}
