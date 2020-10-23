// 
// Decompiled by Procyon v0.5.36
// 

package game.players;

import annotations.Opt;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.util.directions.DirectionFacing;
import gnu.trove.list.array.TIntArrayList;
import util.BaseLudeme;
import util.Context;

import java.awt.*;
import java.io.Serializable;

public final class Player extends BaseLudeme implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int index;
    private String name;
    private Color colour;
    private final DirectionFacing direction;
    private final TIntArrayList enemies;
    private final Moves generator;
    
    public Player(@Opt final String name, @Opt final DirectionFacing dirn, @Opt final Moves generator) {
        this.enemies = new TIntArrayList();
        this.name = (name);
        this.direction = (dirn);
        this.generator = generator;
    }
    
    public int index() {
        return this.index;
    }
    
    public void setIndex(final int id) {
        this.index = id;
    }
    
    public String name() {
        return this.name;
    }
    
    public void setName(final String s) {
        this.name = s;
    }
    
    public Color colour() {
        return this.colour;
    }
    
    public DirectionFacing direction() {
        return this.direction;
    }
    
    public TIntArrayList enemies() {
        return this.enemies;
    }
    
    public void setEnemies(final int numPlayers) {
        for (int id = 1; id <= numPlayers; ++id) {
            if (id != this.index) {
                this.enemies.add(id);
            }
        }
    }
    
    public Moves generator() {
        return this.generator;
    }
    
    public Moves generate(final Context context) {
        if (this.generator != null) {
            return this.generator.eval(context);
        }
        return new BaseMoves(null);
    }
    
    public void setDefaultColour() {
        switch (this.index) {
            case 1 -> {
                this.colour = new Color(255, 255, 255);
                break;
            }
            case 2 -> {
                this.colour = new Color(63, 63, 63);
                break;
            }
            case 3 -> {
                this.colour = new Color(191, 191, 191);
                break;
            }
            case 4 -> {
                this.colour = new Color(255, 0, 0);
                break;
            }
            case 5 -> {
                this.colour = new Color(0, 127, 255);
                break;
            }
            case 6 -> {
                this.colour = new Color(0, 200, 255);
                break;
            }
            case 7 -> {
                this.colour = new Color(230, 230, 0);
                break;
            }
            case 8 -> {
                this.colour = new Color(0, 230, 230);
                break;
            }
            default -> {
                this.colour = null;
                break;
            }
        }
    }
    
    @Override
    public String toString() {
        final String str = "Player(name: " + this.name + ", index: " + this.index + ", colour: " + this.colour + ")";
        return str;
    }
}
