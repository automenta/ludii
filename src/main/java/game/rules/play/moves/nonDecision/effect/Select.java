// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.functions.region.sites.Sites;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.util.equipment.Region;
import game.util.moves.From;
import game.util.moves.To;
import util.Context;
import util.Move;
import util.action.move.ActionSelect;
import util.state.containerState.ContainerState;

public final class Select extends Effect
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region;
    private final BooleanFunction condition;
    private final RegionFunction regionTo;
    private final BooleanFunction conditionTo;
    private SiteType type;
    private boolean gameUsesStacking;
    
    public Select(final From from, @Opt final To to, @Opt final Then then) {
        super(then);
        this.gameUsesStacking = false;
        if (from.region() != null) {
            this.region = from.region();
        }
        else {
            this.region = Sites.construct(new IntFunction[] { from.loc() });
        }
        if (to == null) {
            this.regionTo = null;
        }
        else if (to.region() != null) {
            this.regionTo = to.region();
        }
        else if (to.loc() != null) {
            this.regionTo = Sites.construct(new IntFunction[] { to.loc() });
        }
        else {
            this.regionTo = null;
        }
        this.condition = ((from.cond() == null) ? BooleanConstant.construct(true) : from.cond());
        this.conditionTo = ((to == null || to.cond() == null) ? BooleanConstant.construct(true) : to.cond());
        this.type = from.type();
    }
    
    @Override
    public Moves eval(final Context context) {
        final BaseMoves moves = new BaseMoves(super.then());
        final Region sites = this.region.eval(context);
        final int origTo = context.to();
        final int origFrom = context.from();
        for (int site = sites.bitSet().nextSetBit(0); site >= 0; site = sites.bitSet().nextSetBit(site + 1)) {
            final ContainerState cs = (this.type == SiteType.Edge) ? context.containerState(0) : context.containerState(context.containerId()[site]);
            final int levelFrom = this.gameUsesStacking ? (cs.sizeStack(site, this.type) - 1) : -1;
            if (site != -1) {
                context.setFrom(site);
                context.setTo(site);
                if (this.condition.eval(context)) {
                    if (this.regionTo == null) {
                        final ActionSelect ActionSelect = new ActionSelect(this.type, site, levelFrom, -1, levelFrom);
                        if (this.isDecision()) {
                            ActionSelect.setDecision(true);
                        }
                        final Move action = new Move(ActionSelect);
                        action.setFromNonDecision(site);
                        action.setToNonDecision(site);
                        action.setMover(context.state().mover());
                        moves.moves().add(action);
                    }
                    else {
                        final Region sitesTo = this.regionTo.eval(context);
                        for (int siteTo = sitesTo.bitSet().nextSetBit(0); siteTo >= 0; siteTo = sitesTo.bitSet().nextSetBit(siteTo + 1)) {
                            final ContainerState csTo = context.containerState(context.containerId()[siteTo]);
                            final int levelTo = csTo.sizeStack(siteTo, this.type) - 1;
                            if (siteTo != -1) {
                                context.setFrom(site);
                                context.setTo(siteTo);
                                if (this.conditionTo.eval(context)) {
                                    final ActionSelect ActionSelect2 = new ActionSelect(this.type, site, levelFrom, siteTo, levelTo);
                                    if (this.isDecision()) {
                                        ActionSelect2.setDecision(true);
                                    }
                                    final Move action2 = new Move(ActionSelect2);
                                    action2.setFromNonDecision(site);
                                    action2.setToNonDecision(siteTo);
                                    action2.setMover(context.state().mover());
                                    moves.moves().add(action2);
                                }
                                context.setFrom(origFrom);
                            }
                        }
                    }
                }
            }
        }
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        context.setTo(origTo);
        context.setFrom(origFrom);
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game) | this.region.gameFlags(game) | this.condition.gameFlags(game) | this.conditionTo.gameFlags(game);
        if (this.regionTo != null) {
            gameFlags |= this.regionTo.gameFlags(game);
            gameFlags |= 0x1L;
        }
        if (this.then() != null) {
            gameFlags |= this.then().gameFlags(game);
        }
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return (this.regionTo == null || this.regionTo.isStatic()) && super.isStatic() && this.region.isStatic() && this.condition.isStatic() && this.conditionTo.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        super.preprocess(game);
        this.region.preprocess(game);
        this.condition.preprocess(game);
        if (this.regionTo != null) {
            this.regionTo.preprocess(game);
        }
        this.conditionTo.preprocess(game);
        this.gameUsesStacking = game.isStacking();
    }
    
    public RegionFunction region() {
        return this.region;
    }
}
