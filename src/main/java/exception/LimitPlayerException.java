// 
// Decompiled by Procyon v0.5.36
// 

package exception;

public class LimitPlayerException extends RuntimeException
{
    public LimitPlayerException(final int numPlayers) {
        System.err.println("Instanciation of a play with " + numPlayers);
    }
}
