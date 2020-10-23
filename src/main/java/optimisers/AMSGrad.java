// 
// Decompiled by Procyon v0.5.36
// 

package optimisers;

import collections.FVector;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class AMSGrad extends Optimiser
{
    private static final long serialVersionUID = 1L;
    protected final float beta1;
    protected final float beta2;
    protected final float epsilon;
    private FVector movingAvgGradients;
    private FVector movingAvgSquaredGradients;
    private FVector maxMovingAvgSquaredGradients;
    
    public AMSGrad(final float baseStepSize) {
        super(baseStepSize);
        this.movingAvgGradients = null;
        this.movingAvgSquaredGradients = null;
        this.maxMovingAvgSquaredGradients = null;
        this.beta1 = 0.9f;
        this.beta2 = 0.999f;
        this.epsilon = 1.0E-8f;
    }
    
    public AMSGrad(final float baseStepSize, final float beta1, final float beta2, final float epsilon) {
        super(baseStepSize);
        this.movingAvgGradients = null;
        this.movingAvgSquaredGradients = null;
        this.maxMovingAvgSquaredGradients = null;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.epsilon = epsilon;
    }
    
    @Override
    public void maximiseObjective(final FVector params, final FVector gradients) {
        if (this.movingAvgGradients == null) {
            this.movingAvgGradients = new FVector(gradients.dim());
            this.movingAvgSquaredGradients = new FVector(gradients.dim());
            this.maxMovingAvgSquaredGradients = new FVector(gradients.dim());
        }
        else {
            while (this.movingAvgGradients.dim() < gradients.dim()) {
                this.movingAvgGradients = this.movingAvgGradients.append(0.0f);
                this.movingAvgSquaredGradients = this.movingAvgSquaredGradients.append(0.0f);
                this.maxMovingAvgSquaredGradients = this.maxMovingAvgSquaredGradients.append(0.0f);
            }
        }
        this.movingAvgGradients.mult(this.beta1);
        this.movingAvgGradients.addScaled(gradients, 1.0f - this.beta1);
        final FVector gradientsSquared = gradients.copy();
        gradientsSquared.hadamardProduct(gradientsSquared);
        this.movingAvgSquaredGradients.mult(this.beta2);
        this.movingAvgSquaredGradients.addScaled(gradientsSquared, 1.0f - this.beta2);
        this.maxMovingAvgSquaredGradients = FVector.elementwiseMax(this.maxMovingAvgSquaredGradients, this.movingAvgSquaredGradients);
        final FVector velocity = this.movingAvgGradients.copy();
        velocity.mult(this.baseStepSize / (1.0f - this.beta1));
        final FVector denominator = this.maxMovingAvgSquaredGradients.copy();
        denominator.div(1.0f - this.beta2);
        denominator.sqrt();
        denominator.add(this.epsilon);
        velocity.elementwiseDivision(denominator);
        params.add(velocity);
    }
    
    public static AMSGrad fromLines(final String[] lines) {
        float baseStepSize = 3.0E-4f;
        float beta1 = 0.9f;
        float beta2 = 0.999f;
        float epsilon = 1.0E-8f;
        for (final String line : lines) {
            final String[] lineParts = line.split(",");
            if (lineParts[0].toLowerCase().startsWith("basestepsize=")) {
                baseStepSize = Float.parseFloat(lineParts[0].substring("basestepsize=".length()));
            }
            else if (lineParts[0].toLowerCase().startsWith("beta1=")) {
                beta1 = Float.parseFloat(lineParts[0].substring("beta1=".length()));
            }
            else if (lineParts[0].toLowerCase().startsWith("beta2=")) {
                beta2 = Float.parseFloat(lineParts[0].substring("beta2=".length()));
            }
            else if (lineParts[0].toLowerCase().startsWith("epsilon=")) {
                epsilon = Float.parseFloat(lineParts[0].substring("epsilon=".length()));
            }
        }
        return new AMSGrad(baseStepSize, beta1, beta2, epsilon);
    }
    
    @Override
    public void writeToFile(final String filepath) {
        try (final ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filepath)))) {
            out.writeObject(this);
            out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
