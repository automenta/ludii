// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.component.Component;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.ints.context.To;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.moves.Piece;
import game.util.moves.Player;
import util.Context;
import util.Move;
import util.action.BaseAction;
import util.action.move.ActionPromote;

public final class Promote extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction locationFn;
    private final IntFunction owner;
    private final String[] itemNames;
    private final IntFunction toWhat;
    private final IntFunction[] toWhats;
    private SiteType type;
    
    public Promote(@Opt final SiteType type, @Opt final IntFunction locationFn, final Piece what, @Opt @Or final Player who, @Opt @Or final RoleType role, @Opt final Then then) {
        super(then);
        int numNonNull = 0;
        if (who != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("ForEach(): With ForEachPieceType zero or one who, role parameter must be non-null.");
        }
        this.locationFn = ((locationFn == null) ? To.instance() : locationFn);
        if (what.nameComponent() != null) {
            (this.itemNames = new String[1])[0] = what.nameComponent();
            this.toWhat = null;
            this.toWhats = null;
        }
        else if (what.nameComponents() != null) {
            this.itemNames = what.nameComponents();
            this.toWhat = null;
            this.toWhats = null;
        }
        else if (what.components() != null) {
            this.toWhats = what.components();
            this.toWhat = null;
            this.itemNames = null;
        }
        else {
            this.itemNames = null;
            this.toWhat = what.component();
            this.toWhats = null;
        }
        this.owner = ((who == null && role == null) ? null : ((role != null) ? new Id(null, role) : who.originalIndex()));
        this.type = type;
    }
    
    @Override
    public Moves eval(final Context context) {
        final int location = this.locationFn.eval(context);
        String[] evalItemNames;
        if (this.toWhat != null) {
            evalItemNames = new String[] { null };
            final int id = this.toWhat.eval(context);
            if (id < 1) {
                return new BaseMoves(super.then());
            }
            evalItemNames[0] = context.components()[id].name();
        }
        else {
            evalItemNames = this.itemNames;
        }
        int[] whats;
        if (this.toWhats != null) {
            whats = new int[this.toWhats.length];
            for (int i = 0; i < this.toWhats.length; ++i) {
                whats[i] = this.toWhats[i].eval(context);
            }
        }
        else {
            whats = new int[evalItemNames.length];
            for (int i = 0; i < evalItemNames.length; ++i) {
                if (this.owner == null) {
                    final Component component = context.game().getComponent(evalItemNames[i]);
                    if (component == null) {
                        throw new RuntimeException("Component " + evalItemNames[i] + " is not defined.");
                    }
                    whats[i] = component.index();
                }
                else {
                    final int ownerId = this.owner.eval(context);
                    for (int j = 1; j < context.components().length; ++j) {
                        final Component component2 = context.components()[j];
                        if (component2.name().contains(evalItemNames[i]) && component2.owner() == ownerId) {
                            whats[i] = component2.index();
                            break;
                        }
                    }
                }
            }
        }
        final Moves moves = new BaseMoves(super.then());
        for (final int what : whats) {
            final BaseAction actionPromote = new ActionPromote(this.type, location, what);
            if (this.isDecision()) {
                actionPromote.setDecision(true);
            }
            final Move move = new Move(actionPromote);
            move.setFromNonDecision(location);
            move.setToNonDecision(location);
            move.setMover(context.state().mover());
            moves.moves().add(move);
        }
        if (this.then() != null) {
            for (int k = 0; k < moves.moves().size(); ++k) {
                moves.moves().get(k).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        return this.locationFn.eval(context) == target;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game) | this.locationFn.gameFlags(game);
        if (this.toWhat != null) {
            gameFlags |= this.toWhat.gameFlags(game);
        }
        if (this.then() != null) {
            gameFlags |= this.then().gameFlags(game);
        }
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        super.preprocess(game);
        this.locationFn.preprocess(game);
        if (this.toWhat != null) {
            this.toWhat.preprocess(game);
        }
    }
}
