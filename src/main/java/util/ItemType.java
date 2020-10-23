// 
// Decompiled by Procyon v0.5.36
// 

package util;

public enum ItemType
{
    Container, 
    Hand, 
    Fan, 
    Dice, 
    Hints, 
    Regions, 
    Map, 
    Dominoes, 
    Component;
    
    public static boolean isContainer(final ItemType itemType) {
        return itemType.ordinal() <= ItemType.Dice.ordinal();
    }
    
    public static boolean isComponent(final ItemType itemType) {
        return itemType.ordinal() >= ItemType.Dominoes.ordinal();
    }
    
    public static boolean isRegion(final ItemType itemType) {
        return itemType == ItemType.Regions;
    }
    
    public static boolean isMap(final ItemType itemType) {
        return itemType == ItemType.Map;
    }
    
    public static boolean isHints(final ItemType itemType) {
        return itemType == ItemType.Hints;
    }
}
