// 
// Decompiled by Procyon v0.5.36
// 

package exception;

public class IndexPlayerException extends RuntimeException
{
    public IndexPlayerException(final int index) {
        System.err.println("Instanciation of a player with the index " + index);
    }
}
