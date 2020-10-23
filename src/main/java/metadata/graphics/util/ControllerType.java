// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.util;

import metadata.graphics.GraphicsItem;

public enum ControllerType implements GraphicsItem
{
    BasicController, 
    PyramidalController;
    
    public static ControllerType fromName(final String value) {
        try {
            return valueOf(value);
        }
        catch (Exception e) {
            return ControllerType.BasicController;
        }
    }
}
