package root;/*
 * Decompiled with CFR 0.150.
 */

public class SettingsGeneral {
    private static int maxNumberValue = 0;
    private static boolean moveCoord = true;

    public static int getMaxNumberValue() {
        return maxNumberValue;
    }

    public static void setMaxNumberValue(int maxNumberValue) {
        SettingsGeneral.maxNumberValue = maxNumberValue;
    }

    public static boolean isMoveCoord() {
        return moveCoord;
    }

    public static void setMoveCoord(boolean moveCoord) {
        SettingsGeneral.moveCoord = moveCoord;
    }
}

