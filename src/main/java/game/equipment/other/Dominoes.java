// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.other;

import annotations.Name;
import annotations.Opt;
import game.equipment.Item;
import game.equipment.component.tile.Domino;
import game.types.play.RoleType;
import util.ItemType;

import java.util.ArrayList;

public class Dominoes extends Item
{
    final int upTo;
    
    public Dominoes(@Opt @Name final Integer upTo) {
        super(null, -1, RoleType.Shared);
        this.upTo = ((upTo == null) ? 6 : upTo);
        if (this.upTo < 0 || this.upTo > 16) {
            throw new IllegalArgumentException("The limit of the dominoes can not be negative or to exceed 16.");
        }
        this.setType(ItemType.Dominoes);
    }
    
    public ArrayList<Domino> generateDominoes() {
        final ArrayList<Domino> dominoes = new ArrayList<>();
        for (int i = 0; i <= this.upTo; ++i) {
            for (int j = i; j <= this.upTo; ++j) {
                final Domino domino = new Domino("Domino" + i + j, RoleType.Shared, i, j, null);
                dominoes.add(domino);
            }
        }
        return dominoes;
    }
}
