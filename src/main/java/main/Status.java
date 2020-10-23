// 
// Decompiled by Procyon v0.5.36
// 

package main;

import java.io.Serializable;

public final class Status implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final int winner;
    
    public Status(final int winner) {
        this.winner = winner;
    }
    
    public int winner() {
        return this.winner;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.winner;
        return result;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Status)) {
            return false;
        }
        final Status otherStatus = (Status)other;
        return this.winner == otherStatus.winner;
    }
    
    @Override
    public String toString() {
        String str = "";
        if (this.winner == 0) {
            str = "Nobody wins.";
        }
        else {
            str = "Player " + this.winner + " wins.";
        }
        return str;
    }
}
