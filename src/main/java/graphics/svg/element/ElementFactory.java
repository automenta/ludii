// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element;

import graphics.svg.element.shape.*;
import graphics.svg.element.shape.path.Path;
import graphics.svg.element.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElementFactory
{
    private static final List<Element> prototypes;
    private static ElementFactory singleton;
    
    private ElementFactory() {
        ElementFactory.prototypes.add(new Circle());
        ElementFactory.prototypes.add(new Ellipse());
        ElementFactory.prototypes.add(new Line());
        ElementFactory.prototypes.add(new Polygon());
        ElementFactory.prototypes.add(new Polyline());
        ElementFactory.prototypes.add(new Rect());
        ElementFactory.prototypes.add(new Path());
        ElementFactory.prototypes.add(new Text());
    }
    
    public static ElementFactory get() {
        if (ElementFactory.singleton == null) {
            ElementFactory.singleton = new ElementFactory();
        }
        return ElementFactory.singleton;
    }
    
    public List<Element> prototypes() {
        return Collections.unmodifiableList(ElementFactory.prototypes);
    }
    
    public Element generate(final String label) {
        for (final Element prototype : ElementFactory.prototypes) {
            if (prototype.label().equals(label)) {
                return prototype.newInstance();
            }
        }
        System.out.println("* Failed to find prototype for Element " + label + ".");
        return null;
    }
    
    static {
        prototypes = new ArrayList<>();
        ElementFactory.singleton = null;
    }
}
