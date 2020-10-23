// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.Stack;
import org.apache.batik.ext.awt.g2d.TransformStackElement;
import org.apache.batik.ext.awt.g2d.GraphicContext;

public class SVGTransform extends AbstractSVGConverter
{
    private static double radiansToDegrees;
    
    public SVGTransform(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    @Override
    public SVGDescriptor toSVG(final GraphicContext gc) {
        return new SVGTransformDescriptor(this.toSVGTransform(gc));
    }
    
    public final String toSVGTransform(final GraphicContext gc) {
        return this.toSVGTransform(gc.getTransformStack());
    }
    
    public final String toSVGTransform(final TransformStackElement[] transformStack) {
        final int nTransforms = transformStack.length;
        final Stack presentation = new Stack() {
            @Override
            public Object push(final Object o) {
                Object element;
                if (((TransformStackElement)o).isIdentity()) {
                    element = this.pop();
                }
                else {
                    super.push(o);
                    element = null;
                }
                return element;
            }
            
            @Override
            public Object pop() {
                Object element = null;
                if (!super.empty()) {
                    element = super.pop();
                }
                return element;
            }
        };
        boolean canConcatenate = false;
        int i = 0;
        int j = 0;
        int next = 0;
        TransformStackElement element;
        for (element = null; i < nTransforms; i = j, element = presentation.push(element)) {
            next = i;
            if (element == null) {
                element = (TransformStackElement)transformStack[i].clone();
                ++next;
            }
            canConcatenate = true;
            for (j = next; j < nTransforms; ++j) {
                canConcatenate = element.concatenate(transformStack[j]);
                if (!canConcatenate) {
                    break;
                }
            }
        }
        if (element != null) {
            presentation.push(element);
        }
        final int nPresentations = presentation.size();
        final StringBuffer transformStackBuffer = new StringBuffer(nPresentations * 8);
        for (i = 0; i < nPresentations; ++i) {
            transformStackBuffer.append(this.convertTransform((TransformStackElement)presentation.get(i)));
            transformStackBuffer.append(" ");
        }
        final String transformValue = transformStackBuffer.toString().trim();
        return transformValue;
    }
    
    final String convertTransform(final TransformStackElement transformElement) {
        final StringBuffer transformString = new StringBuffer();
        final double[] transformParameters = transformElement.getTransformParameters();
        switch (transformElement.getType().toInt()) {
            case 0: {
                if (!transformElement.isIdentity()) {
                    transformString.append("translate");
                    transformString.append("(");
                    transformString.append(this.doubleString(transformParameters[0]));
                    transformString.append(",");
                    transformString.append(this.doubleString(transformParameters[1]));
                    transformString.append(")");
                    break;
                }
                break;
            }
            case 1: {
                if (!transformElement.isIdentity()) {
                    transformString.append("rotate");
                    transformString.append("(");
                    transformString.append(this.doubleString(SVGTransform.radiansToDegrees * transformParameters[0]));
                    transformString.append(")");
                    break;
                }
                break;
            }
            case 2: {
                if (!transformElement.isIdentity()) {
                    transformString.append("scale");
                    transformString.append("(");
                    transformString.append(this.doubleString(transformParameters[0]));
                    transformString.append(",");
                    transformString.append(this.doubleString(transformParameters[1]));
                    transformString.append(")");
                    break;
                }
                break;
            }
            case 3: {
                if (!transformElement.isIdentity()) {
                    transformString.append("matrix");
                    transformString.append("(");
                    transformString.append(1);
                    transformString.append(",");
                    transformString.append(this.doubleString(transformParameters[1]));
                    transformString.append(",");
                    transformString.append(this.doubleString(transformParameters[0]));
                    transformString.append(",");
                    transformString.append(1);
                    transformString.append(",");
                    transformString.append(0);
                    transformString.append(",");
                    transformString.append(0);
                    transformString.append(")");
                    break;
                }
                break;
            }
            case 4: {
                if (!transformElement.isIdentity()) {
                    transformString.append("matrix");
                    transformString.append("(");
                    transformString.append(this.doubleString(transformParameters[0]));
                    transformString.append(",");
                    transformString.append(this.doubleString(transformParameters[1]));
                    transformString.append(",");
                    transformString.append(this.doubleString(transformParameters[2]));
                    transformString.append(",");
                    transformString.append(this.doubleString(transformParameters[3]));
                    transformString.append(",");
                    transformString.append(this.doubleString(transformParameters[4]));
                    transformString.append(",");
                    transformString.append(this.doubleString(transformParameters[5]));
                    transformString.append(")");
                    break;
                }
                break;
            }
            default: {
                throw new RuntimeException();
            }
        }
        return transformString.toString();
    }
    
    static {
        SVGTransform.radiansToDegrees = 57.29577951308232;
    }
}
