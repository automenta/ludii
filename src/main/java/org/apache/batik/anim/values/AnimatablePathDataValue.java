// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import java.util.Arrays;
import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatablePathDataValue extends AnimatableValue
{
    protected short[] commands;
    protected float[] parameters;
    protected static final char[] PATH_COMMANDS;
    protected static final int[] PATH_PARAMS;
    
    protected AnimatablePathDataValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatablePathDataValue(final AnimationTarget target, final short[] commands, final float[] parameters) {
        super(target);
        this.commands = commands;
        this.parameters = parameters;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        final AnimatablePathDataValue toValue = (AnimatablePathDataValue)to;
        final AnimatablePathDataValue accValue = (AnimatablePathDataValue)accumulation;
        final boolean hasTo = to != null;
        final boolean hasAcc = accumulation != null;
        final boolean canInterpolate = hasTo && toValue.parameters.length == this.parameters.length && Arrays.equals(toValue.commands, this.commands);
        final boolean canAccumulate = hasAcc && accValue.parameters.length == this.parameters.length && Arrays.equals(accValue.commands, this.commands);
        AnimatablePathDataValue base;
        if (!canInterpolate && hasTo && interpolation >= 0.5) {
            base = toValue;
        }
        else {
            base = this;
        }
        final int cmdCount = base.commands.length;
        final int paramCount = base.parameters.length;
        AnimatablePathDataValue res;
        if (result == null) {
            res = new AnimatablePathDataValue(this.target);
            res.commands = new short[cmdCount];
            res.parameters = new float[paramCount];
            System.arraycopy(base.commands, 0, res.commands, 0, cmdCount);
        }
        else {
            res = (AnimatablePathDataValue)result;
            if (res.commands == null || res.commands.length != cmdCount) {
                res.commands = new short[cmdCount];
                System.arraycopy(base.commands, 0, res.commands, 0, cmdCount);
                res.hasChanged = true;
            }
            else if (!Arrays.equals(base.commands, res.commands)) {
                System.arraycopy(base.commands, 0, res.commands, 0, cmdCount);
                res.hasChanged = true;
            }
        }
        for (int i = 0; i < paramCount; ++i) {
            float newValue = base.parameters[i];
            if (canInterpolate) {
                newValue += interpolation * (toValue.parameters[i] - newValue);
            }
            if (canAccumulate) {
                newValue += multiplier * accValue.parameters[i];
            }
            if (res.parameters[i] != newValue) {
                res.parameters[i] = newValue;
                res.hasChanged = true;
            }
        }
        return res;
    }
    
    public short[] getCommands() {
        return this.commands;
    }
    
    public float[] getParameters() {
        return this.parameters;
    }
    
    @Override
    public boolean canPace() {
        return false;
    }
    
    @Override
    public float distanceTo(final AnimatableValue other) {
        return 0.0f;
    }
    
    @Override
    public AnimatableValue getZeroValue() {
        final short[] cmds = new short[this.commands.length];
        System.arraycopy(this.commands, 0, cmds, 0, this.commands.length);
        final float[] params = new float[this.parameters.length];
        return new AnimatablePathDataValue(this.target, cmds, params);
    }
    
    @Override
    public String toStringRep() {
        final StringBuffer sb = new StringBuffer();
        int k = 0;
        for (final short command : this.commands) {
            sb.append(AnimatablePathDataValue.PATH_COMMANDS[command]);
            for (int j = 0; j < AnimatablePathDataValue.PATH_PARAMS[command]; ++j) {
                sb.append(' ');
                sb.append(this.parameters[k++]);
            }
        }
        return sb.toString();
    }
    
    static {
        PATH_COMMANDS = new char[] { ' ', 'z', 'M', 'm', 'L', 'l', 'C', 'c', 'Q', 'q', 'A', 'a', 'H', 'h', 'V', 'v', 'S', 's', 'T', 't' };
        PATH_PARAMS = new int[] { 0, 0, 2, 2, 2, 2, 6, 6, 4, 4, 7, 7, 1, 1, 1, 1, 4, 4, 2, 2 };
    }
}
