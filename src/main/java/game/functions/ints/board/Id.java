// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.board;

import annotations.Opt;
import game.Game;
import game.equipment.component.Component;
import game.equipment.container.Container;
import game.functions.ints.BaseIntFunction;
import game.types.play.RoleType;
import util.Context;

public final class Id extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final String nameComponent;
    private final RoleType who;
    
    public Id(@Opt final String name, @Opt final RoleType who) {
        this.nameComponent = (name);
        this.who = who;
    }
    
    public static IndexOfComponent construct(final String name) {
        return new IndexOfComponent(name);
    }
    
    @Override
    public int eval(final Context context) {
        if (this.who == RoleType.Player) {
            return context.iterator();
        }
        if (this.who != null && this.nameComponent == null) {
            switch (this.who) {
                case Neutral -> {
                    return 0;
                }
                case P1 -> {
                    return 1;
                }
                case P2 -> {
                    return 2;
                }
                case P3 -> {
                    return 3;
                }
                case P4 -> {
                    return 4;
                }
                case P5 -> {
                    return 5;
                }
                case P6 -> {
                    return 6;
                }
                case P7 -> {
                    return 7;
                }
                case P8 -> {
                    return 8;
                }
                case P9 -> {
                    return 9;
                }
                case P10 -> {
                    return 10;
                }
                case P11 -> {
                    return 11;
                }
                case P12 -> {
                    return 12;
                }
                case P13 -> {
                    return 13;
                }
                case P14 -> {
                    return 14;
                }
                case P15 -> {
                    return 15;
                }
                case P16 -> {
                    return 16;
                }
                case Team1 -> {
                    return 1;
                }
                case Team2 -> {
                    return 2;
                }
                case Team3 -> {
                    return 3;
                }
                case Team4 -> {
                    return 4;
                }
                case Team5 -> {
                    return 5;
                }
                case Team6 -> {
                    return 6;
                }
                case Team7 -> {
                    return 7;
                }
                case Team8 -> {
                    return 8;
                }
                case Team9 -> {
                    return 9;
                }
                case Team10 -> {
                    return 10;
                }
                case Team11 -> {
                    return 11;
                }
                case Team12 -> {
                    return 12;
                }
                case Team13 -> {
                    return 13;
                }
                case Team14 -> {
                    return 14;
                }
                case Team15 -> {
                    return 15;
                }
                case Team16 -> {
                    return 16;
                }
                case Shared -> {
                    return context.game().players().count() + 1;
                }
                case All -> {
                    return context.game().players().count() + 1;
                }
                case Any -> {
                    return context.game().players().count() + 1;
                }
                case Mover -> {
                    return context.state().mover();
                }
                case Next -> {
                    return context.state().next();
                }
                case Prev -> {
                    return context.state().prev();
                }
                default -> {
                    return -1;
                }
            }
        }
        else {
            if (this.who != null) {
                int playerId = 0;
                switch (this.who) {
                    case Neutral -> {
                        playerId = 0;
                        break;
                    }
                    case P1 -> {
                        playerId = 1;
                        break;
                    }
                    case P2 -> {
                        playerId = 2;
                        break;
                    }
                    case P3 -> {
                        playerId = 3;
                        break;
                    }
                    case P4 -> {
                        playerId = 4;
                        break;
                    }
                    case P5 -> {
                        playerId = 5;
                        break;
                    }
                    case P6 -> {
                        playerId = 6;
                        break;
                    }
                    case P7 -> {
                        playerId = 7;
                        break;
                    }
                    case P8 -> {
                        playerId = 8;
                        break;
                    }
                    case Shared -> {
                        playerId = context.game().players().count() + 1;
                        break;
                    }
                    case Mover -> {
                        playerId = context.state().mover();
                        break;
                    }
                    case Next -> {
                        playerId = context.state().next();
                        break;
                    }
                    case Prev -> {
                        playerId = context.state().prev();
                        break;
                    }
                    default -> {
                        return -1;
                    }
                }
                for (int i = 1; i < context.components().length; ++i) {
                    final Component component = context.components()[i];
                    if (component.name().contains(this.nameComponent) && component.owner() == playerId) {
                        return i;
                    }
                }
                return -1;
            }
            if (this.nameComponent != null) {
                for (int j = 0; j < context.containers().length; ++j) {
                    final Container container = context.containers()[j];
                    if (container.name().equals(this.nameComponent)) {
                        return j;
                    }
                }
                for (int j = 1; j < context.components().length; ++j) {
                    final Component component2 = context.components()[j];
                    if (component2.name().equals(this.nameComponent)) {
                        return j;
                    }
                }
            }
            return -1;
        }
    }
    
    @Override
    public boolean isStatic() {
        return this.who == RoleType.Neutral || this.who == RoleType.P1 || this.who == RoleType.P2 || this.who == RoleType.P3 || this.who == RoleType.P4 || this.who == RoleType.P5 || this.who == RoleType.P6 || this.who == RoleType.P7 || this.who == RoleType.P8 || this.who == RoleType.Shared;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    public static class IndexOfComponent extends BaseIntFunction
    {
        private static final long serialVersionUID = 1L;
        protected final String nameComponent;
        protected int precomputedIdx;
        
        public IndexOfComponent(final String name) {
            this.precomputedIdx = -1;
            this.nameComponent = name;
        }
        
        @Override
        public int eval(final Context context) {
            if (this.precomputedIdx == -1) {
                this.preprocess(context.game());
            }
            return this.precomputedIdx;
        }
        
        @Override
        public boolean isStatic() {
            return true;
        }
        
        @Override
        public long gameFlags(final Game game) {
            return 0L;
        }
        
        @Override
        public void preprocess(final Game game) {
            for (int i = 0; i < game.equipment().containers().length; ++i) {
                final Container container = game.equipment().containers()[i];
                if (container.name().equals(this.nameComponent)) {
                    this.precomputedIdx = i;
                    return;
                }
            }
            for (int i = 1; i < game.equipment().components().length; ++i) {
                final Component component = game.equipment().components()[i];
                if (component.name().equals(this.nameComponent)) {
                    this.precomputedIdx = i;
                    return;
                }
            }
        }
    }
}
