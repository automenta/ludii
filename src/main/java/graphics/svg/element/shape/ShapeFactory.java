// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape;

import graphics.svg.element.shape.path.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShapeFactory
{
    private static final List<Shape> prototypes;
    private static ShapeFactory singleton;
    
    private ShapeFactory() {
        ShapeFactory.prototypes.add(new Circle());
        ShapeFactory.prototypes.add(new Ellipse());
        ShapeFactory.prototypes.add(new Line());
        ShapeFactory.prototypes.add(new Polygon());
        ShapeFactory.prototypes.add(new Polyline());
        ShapeFactory.prototypes.add(new Rect());
        ShapeFactory.prototypes.add(new Path());
    }
    
    public static ShapeFactory get() {
        if (ShapeFactory.singleton == null) {
            ShapeFactory.singleton = new ShapeFactory();
        }
        return ShapeFactory.singleton;
    }
    
    public List<Shape> prototypes() {
        return Collections.unmodifiableList(ShapeFactory.prototypes);
    }
    
    public Shape generate(final String label) {
        for (final Shape prototype : ShapeFactory.prototypes) {
            if (prototype.label().equals(label)) {
                return (Shape)prototype.newInstance();
            }
        }
        System.out.println("* Failed to find prototype for Element " + label + ".");
        return null;
    }
    
    static {
        prototypes = new ArrayList<>();
        ShapeFactory.singleton = null;
    }
}
