// 
// Decompiled by Procyon v0.5.36
// 

package game.players;

import annotations.Opt;
import exception.LimitPlayerException;
import game.rules.play.moves.Moves;
import util.BaseLudeme;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Players extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final List<Player> players;
    
    public Players(final Player[] players) {
        (this.players = new ArrayList<>()).add(null);
        if (players != null) {
            for (int p = 0; p < players.length; ++p) {
                final Player player = players[p];
                if (player.name() == null) {
                    player.setName("Player " + (p + 1));
                }
                player.setIndex(p + 1);
                player.setDefaultColour();
                player.setEnemies(players.length);
                this.players.add(player);
            }
        }
        if (this.players.size() > 17) {
            throw new LimitPlayerException(this.players.size());
        }
    }
    
    public Players(final Integer numPlayers, @Opt final Moves generator) {
        (this.players = new ArrayList<>()).add(null);
        if (this.players != null) {
            for (int p = 0; p < numPlayers; ++p) {
                final Player player = new Player(null, null, generator);
                if (player.name() == null) {
                    player.setName("Player " + (p + 1));
                }
                player.setIndex(p + 1);
                player.setDefaultColour();
                player.setEnemies(numPlayers);
                this.players.add(player);
            }
        }
        if (this.players.size() > 17) {
            throw new LimitPlayerException(this.players.size());
        }
    }
    
    public int count() {
        return this.players.size() - 1;
    }
    
    public int size() {
        return this.players.size();
    }
    
    public List<Player> players() {
        return Collections.unmodifiableList(this.players);
    }
}
