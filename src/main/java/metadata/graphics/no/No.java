// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.no;

import annotations.Opt;
import metadata.graphics.GraphicsItem;
import metadata.graphics.no.Boolean.*;

public class No implements GraphicsItem
{
    public static GraphicsItem construct(final NoBooleanType boardType, @Opt final Boolean value) {
        switch (boardType) {
            case Board -> {
                return new NoBoard(value);
            }
            case Animation -> {
                return new NoAnimation(value);
            }
            case HandScale -> {
                return new NoHandScale(value);
            }
            case DicePips -> {
                return new NoDicePips(value);
            }
            case Curves -> {
                return new NoCurves(value);
            }
            case MaskedColour -> {
                return new NoMaskedColour(value);
            }
            default -> throw new IllegalArgumentException("No(): A NoBooleanType is not implemented.");
        }
    }
    
    private No() {
    }
}
