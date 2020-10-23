// 
// Decompiled by Procyon v0.5.36
// 

package app.sandbox;

import game.Game;

public class SandboxUtil
{
    public static String isSandboxAllowed(final Game game) {
        String errorMessage = "";
        if (game.hasLargePiece()) {
            errorMessage = "Sandbox is not supported in games that have large pieces.";
        }
        else if (game.hasHandDice()) {
            errorMessage = "Sandbox is not supported in games that have dice.";
        }
        return errorMessage;
    }
}
