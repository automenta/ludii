// 
// Decompiled by Procyon v0.5.36
// 

package optimisers;

import collections.FVector;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class DeepmindRMSProp extends Optimiser
{
    private static final long serialVersionUID = 1L;
    protected final float momentum;
    protected final float decay;
    protected final float epsilon;
    private FVector lastVelocity;
    private FVector movingAvgGradients;
    private FVector movingAvgSquaredGradients;
    
    public DeepmindRMSProp() {
        super(0.005f);
        this.lastVelocity = null;
        this.movingAvgGradients = null;
        this.movingAvgSquaredGradients = null;
        this.momentum = 0.9f;
        this.decay = 0.9f;
        this.epsilon = 1.0E-8f;
    }
    
    public DeepmindRMSProp(final float baseStepSize) {
        super(baseStepSize);
        this.lastVelocity = null;
        this.movingAvgGradients = null;
        this.movingAvgSquaredGradients = null;
        this.momentum = 0.9f;
        this.decay = 0.9f;
        this.epsilon = 1.0E-8f;
    }
    
    public DeepmindRMSProp(final float baseStepSize, final float momentum, final float decay, final float epsilon) {
        super(baseStepSize);
        this.lastVelocity = null;
        this.movingAvgGradients = null;
        this.movingAvgSquaredGradients = null;
        this.momentum = momentum;
        this.decay = decay;
        this.epsilon = epsilon;
    }
    
    @Override
    public void maximiseObjective(final FVector params, final FVector gradients) {
        final FVector velocity = gradients.copy();
        velocity.mult(this.baseStepSize / velocity.dim());
        if (this.movingAvgGradients == null) {
            this.movingAvgGradients = new FVector(gradients.dim());
            this.movingAvgSquaredGradients = new FVector(gradients.dim());
        }
        else {
            while (this.movingAvgGradients.dim() < gradients.dim()) {
                this.movingAvgGradients = this.movingAvgGradients.append(0.0f);
                this.movingAvgSquaredGradients = this.movingAvgSquaredGradients.append(0.0f);
            }
        }
        this.movingAvgGradients.mult(this.decay);
        this.movingAvgGradients.addScaled(gradients, 1.0f - this.decay);
        final FVector gradientsSquared = gradients.copy();
        gradientsSquared.hadamardProduct(gradientsSquared);
        this.movingAvgSquaredGradients.mult(this.decay);
        this.movingAvgSquaredGradients.addScaled(gradientsSquared, 1.0f - this.decay);
        final FVector denominator = this.movingAvgSquaredGradients.copy();
        final FVector temp = this.movingAvgGradients.copy();
        temp.hadamardProduct(temp);
        denominator.subtract(temp);
        denominator.add(this.epsilon);
        denominator.sqrt();
        velocity.elementwiseDivision(denominator);
        if (this.momentum > 0.0f && this.lastVelocity != null) {
            while (this.lastVelocity.dim() < velocity.dim()) {
                this.lastVelocity = this.lastVelocity.append(0.0f);
            }
            velocity.addScaled(this.lastVelocity, this.momentum);
        }
        params.add(velocity);
        this.lastVelocity = velocity;
    }
    
    public static DeepmindRMSProp fromLines(final String[] lines) {
        float baseStepSize = 0.005f;
        float momentum = 0.9f;
        float decay = 0.9f;
        float epsilon = 1.0E-8f;
        for (final String line : lines) {
            final String[] lineParts = line.split(",");
            if (lineParts[0].toLowerCase().startsWith("basestepsize=")) {
                baseStepSize = Float.parseFloat(lineParts[0].substring("basestepsize=".length()));
            }
            else if (lineParts[0].toLowerCase().startsWith("momentum=")) {
                momentum = Float.parseFloat(lineParts[0].substring("momentum=".length()));
            }
            else if (lineParts[0].toLowerCase().startsWith("decay=")) {
                decay = Float.parseFloat(lineParts[0].substring("decay=".length()));
            }
            else if (lineParts[0].toLowerCase().startsWith("epsilon=")) {
                epsilon = Float.parseFloat(lineParts[0].substring("epsilon=".length()));
            }
        }
        return new DeepmindRMSProp(baseStepSize, momentum, decay, epsilon);
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
