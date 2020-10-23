// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.g2d;

import java.awt.geom.AffineTransform;

public abstract class TransformStackElement implements Cloneable
{
    private TransformType type;
    private double[] transformParameters;
    
    protected TransformStackElement(final TransformType type, final double[] transformParameters) {
        this.type = type;
        this.transformParameters = transformParameters;
    }
    
    public Object clone() {
        TransformStackElement newElement = null;
        try {
            newElement = (TransformStackElement)super.clone();
        }
        catch (CloneNotSupportedException ex) {}
        final double[] transformParameters = new double[this.transformParameters.length];
        System.arraycopy(this.transformParameters, 0, transformParameters, 0, transformParameters.length);
        newElement.transformParameters = transformParameters;
        return newElement;
    }
    
    public static TransformStackElement createTranslateElement(final double tx, final double ty) {
        return new TransformStackElement(TransformType.TRANSLATE, new double[] { tx, ty }) {
            @Override
            boolean isIdentity(final double[] parameters) {
                return parameters[0] == 0.0 && parameters[1] == 0.0;
            }
        };
    }
    
    public static TransformStackElement createRotateElement(final double theta) {
        return new TransformStackElement(TransformType.ROTATE, new double[] { theta }) {
            @Override
            boolean isIdentity(final double[] parameters) {
                return Math.cos(parameters[0]) == 1.0;
            }
        };
    }
    
    public static TransformStackElement createScaleElement(final double scaleX, final double scaleY) {
        return new TransformStackElement(TransformType.SCALE, new double[] { scaleX, scaleY }) {
            @Override
            boolean isIdentity(final double[] parameters) {
                return parameters[0] == 1.0 && parameters[1] == 1.0;
            }
        };
    }
    
    public static TransformStackElement createShearElement(final double shearX, final double shearY) {
        return new TransformStackElement(TransformType.SHEAR, new double[] { shearX, shearY }) {
            @Override
            boolean isIdentity(final double[] parameters) {
                return parameters[0] == 0.0 && parameters[1] == 0.0;
            }
        };
    }
    
    public static TransformStackElement createGeneralTransformElement(final AffineTransform txf) {
        final double[] matrix = new double[6];
        txf.getMatrix(matrix);
        return new TransformStackElement(TransformType.GENERAL, matrix) {
            @Override
            boolean isIdentity(final double[] m) {
                return m[0] == 1.0 && m[2] == 0.0 && m[4] == 0.0 && m[1] == 0.0 && m[3] == 1.0 && m[5] == 0.0;
            }
        };
    }
    
    abstract boolean isIdentity(final double[] p0);
    
    public boolean isIdentity() {
        return this.isIdentity(this.transformParameters);
    }
    
    public double[] getTransformParameters() {
        return this.transformParameters;
    }
    
    public TransformType getType() {
        return this.type;
    }
    
    public boolean concatenate(final TransformStackElement stackElement) {
        boolean canConcatenate = false;
        if (this.type.toInt() == stackElement.type.toInt()) {
            canConcatenate = true;
            switch (this.type.toInt()) {
                case 0: {
                    final double[] transformParameters = this.transformParameters;
                    final int n = 0;
                    transformParameters[n] += stackElement.transformParameters[0];
                    final double[] transformParameters2 = this.transformParameters;
                    final int n2 = 1;
                    transformParameters2[n2] += stackElement.transformParameters[1];
                    break;
                }
                case 1: {
                    final double[] transformParameters3 = this.transformParameters;
                    final int n3 = 0;
                    transformParameters3[n3] += stackElement.transformParameters[0];
                    break;
                }
                case 2: {
                    final double[] transformParameters4 = this.transformParameters;
                    final int n4 = 0;
                    transformParameters4[n4] *= stackElement.transformParameters[0];
                    final double[] transformParameters5 = this.transformParameters;
                    final int n5 = 1;
                    transformParameters5[n5] *= stackElement.transformParameters[1];
                    break;
                }
                case 4: {
                    this.transformParameters = this.matrixMultiply(this.transformParameters, stackElement.transformParameters);
                    break;
                }
                default: {
                    canConcatenate = false;
                    break;
                }
            }
        }
        return canConcatenate;
    }
    
    private double[] matrixMultiply(final double[] matrix1, final double[] matrix2) {
        final double[] product = new double[6];
        final AffineTransform transform1 = new AffineTransform(matrix1);
        transform1.concatenate(new AffineTransform(matrix2));
        transform1.getMatrix(product);
        return product;
    }
}
