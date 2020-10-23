// 
// Decompiled by Procyon v0.5.36
// 

package optimisers;

import main.collections.FVector;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SGD extends Optimiser
{
    private static final long serialVersionUID = 1L;
    protected final float momentum;
    private FVector lastVelocity;
    
    public SGD(final float baseStepSize) {
        super(baseStepSize);
        this.lastVelocity = null;
        this.momentum = 0.0f;
    }
    
    public SGD(final float baseStepSize, final float momentum) {
        super(baseStepSize);
        this.lastVelocity = null;
        this.momentum = momentum;
    }
    
    @Override
    public void maximiseObjective(final FVector params, final FVector gradients) {
        final FVector velocity = gradients.copy();
        velocity.mult(this.baseStepSize);
        if (this.momentum > 0.0f && this.lastVelocity != null) {
            while (this.lastVelocity.dim() < velocity.dim()) {
                this.lastVelocity = this.lastVelocity.append(0.0f);
            }
            velocity.addScaled(this.lastVelocity, this.momentum);
        }
        params.add(velocity);
        this.lastVelocity = velocity;
    }
    
    public static SGD fromLines(final String[] lines) {
        float baseStepSize = 0.05f;
        float momentum = 0.0f;
        for (final String line : lines) {
            final String[] lineParts = line.split(",");
            if (lineParts[0].toLowerCase().startsWith("basestepsize=")) {
                baseStepSize = Float.parseFloat(lineParts[0].substring("basestepsize=".length()));
            }
            else if (lineParts[0].toLowerCase().startsWith("momentum=")) {
                momentum = Float.parseFloat(lineParts[0].substring("momentum=".length()));
            }
        }
        return new SGD(baseStepSize, momentum);
    }
    
    @Override
    public void writeToFile(final String filepath) {
        try (final ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filepath)))) {
            out.writeObject(this);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
