// 
// Decompiled by Procyon v0.5.36
// 

package main;

public class SettingsGeneral
{
    private static int maxNumberValue;
    private static boolean moveCoord;
    
    public static int getMaxNumberValue() {
        return SettingsGeneral.maxNumberValue;
    }
    
    public static void setMaxNumberValue(final int maxNumberValue) {
        SettingsGeneral.maxNumberValue = maxNumberValue;
    }
    
    public static boolean isMoveCoord() {
        return SettingsGeneral.moveCoord;
    }
    
    public static void setMoveCoord(final boolean moveCoord) {
        SettingsGeneral.moveCoord = moveCoord;
    }
    
    static {
        SettingsGeneral.maxNumberValue = 0;
        SettingsGeneral.moveCoord = true;
    }
}
