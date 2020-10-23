// 
// Decompiled by Procyon v0.5.36
// 

package bridge;

import controllers.Controller;
import view.component.ComponentStyle;
import view.container.ContainerStyle;

import java.util.ArrayList;

public enum Bridge
{ ;
    private static PlatformGraphics graphicsRenderer;
    private static ArrayList<ContainerStyle> containerStyles;
    private static ArrayList<ComponentStyle> componentStyles;
    private static ArrayList<Controller> containerControllers;

    public static void setGraphicsRenderer(final PlatformGraphics g) {
        Bridge.graphicsRenderer = g;
    }
    
    public static PlatformGraphics graphicsRenderer() {
        return Bridge.graphicsRenderer;
    }
    
    public static void addContainerStyle(final ContainerStyle containerStyle, final int index) {
        for (int i = Bridge.containerStyles.size(); i <= index; ++i) {
            Bridge.containerStyles.add(null);
        }
        Bridge.containerStyles.set(index, containerStyle);
    }
    
    public static void clearContainerStyles() {
        Bridge.containerStyles.clear();
    }
    
    public static ContainerStyle getContainerStyle(final int index) {
        return Bridge.containerStyles.get(index);
    }
    
    public static ArrayList<ContainerStyle> getContainerStyles() {
        return Bridge.containerStyles;
    }
    
    public static void addComponentStyle(final ComponentStyle componentStyle, final int index) {
        for (int i = Bridge.componentStyles.size(); i <= index; ++i) {
            Bridge.componentStyles.add(null);
        }
        Bridge.componentStyles.set(index, componentStyle);
    }
    
    public static void clearComponentStyles() {
        Bridge.componentStyles.clear();
    }
    
    public static ComponentStyle getComponentStyle(final int index) {
        return Bridge.componentStyles.get(index);
    }
    
    public static ArrayList<ComponentStyle> getComponentStyles() {
        return Bridge.componentStyles;
    }
    
    public static void addContainerController(final Controller containerController, final int index) {
        for (int i = Bridge.containerControllers.size(); i <= index; ++i) {
            Bridge.containerControllers.add(null);
        }
        Bridge.containerControllers.set(index, containerController);
    }
    
    public static void clearContainerControllers() {
        Bridge.containerControllers.clear();
    }
    
    public static Controller getContainerController(final int index) {
        return Bridge.containerControllers.get(index);
    }
    
    static {
        Bridge.containerStyles = new ArrayList<>();
        Bridge.componentStyles = new ArrayList<>();
        Bridge.containerControllers = new ArrayList<>();
    }
    
//    private static class BridgeProvider
//    {
//        public static final Bridge BRIDGE;
//
//        static {
//            BRIDGE = new Bridge();
//        }
//    }
}
