package root;/*
 * Decompiled with CFR 0.150.
 */

import java.io.Serializable;

public final class Status
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int winner;

    public Status(int winner) {
        this.winner = winner;
    }

    public int winner() {
        return this.winner;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.winner;
        return result;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Status)) {
            return false;
        }
        Status otherStatus = (Status)other;
        return this.winner == otherStatus.winner;
    }

    public String toString() {
        String str = "";
        str = this.winner == 0 ? "Nobody wins." : "Player " + this.winner + " wins.";
        return str;
    }
}

