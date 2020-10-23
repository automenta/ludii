// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.awt.Toolkit;
import java.awt.GraphicsEnvironment;

public abstract class Platform
{
    public static boolean isOSX;
    
    public static int getScreenResolution() {
        if (GraphicsEnvironment.isHeadless()) {
            return 96;
        }
        return Toolkit.getDefaultToolkit().getScreenResolution();
    }
    
    static {
        Platform.isOSX = System.getProperty("os.name").equals("Mac OS X");
    }
}
