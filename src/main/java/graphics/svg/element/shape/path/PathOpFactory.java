// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathOpFactory
{
    private static final List<PathOp> prototypes;
    private static PathOpFactory singleton;
    
    private PathOpFactory() {
        PathOpFactory.prototypes.add(new MoveTo());
        PathOpFactory.prototypes.add(new LineTo());
        PathOpFactory.prototypes.add(new HorzLineTo());
        PathOpFactory.prototypes.add(new VertLineTo());
        PathOpFactory.prototypes.add(new QuadTo());
        PathOpFactory.prototypes.add(new CubicTo());
        PathOpFactory.prototypes.add(new ShortQuadTo());
        PathOpFactory.prototypes.add(new ShortCubicTo());
        PathOpFactory.prototypes.add(new Arc());
        PathOpFactory.prototypes.add(new Close());
    }
    
    public static PathOpFactory get() {
        if (PathOpFactory.singleton == null) {
            PathOpFactory.singleton = new PathOpFactory();
        }
        return PathOpFactory.singleton;
    }
    
    public List<PathOp> prototypes() {
        return Collections.unmodifiableList(PathOpFactory.prototypes);
    }
    
    public PathOp generate(final char label) {
        PathOp prototype = null;
        for (final PathOp prototypeN : PathOpFactory.prototypes) {
            if (prototypeN.matchesLabel(label)) {
                prototype = prototypeN;
                break;
            }
        }
        if (prototype == null) {
            System.out.println("* Failed to find prototype for PathOp " + label + ".");
            return null;
        }
        final PathOp op = prototype.newInstance();
        op.setLabel(label);
        return op;
    }
    
    static {
        prototypes = new ArrayList<>();
        PathOpFactory.singleton = null;
    }
}
