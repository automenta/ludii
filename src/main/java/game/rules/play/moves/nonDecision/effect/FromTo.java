// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.equipment.component.Component;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.intArray.state.Rotations;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.region.RegionFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.moves.From;
import game.util.moves.To;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.Move;
import util.MoveUtilities;
import util.action.Action;
import util.action.move.ActionCopy;
import util.action.move.ActionMove;
import util.action.move.ActionMoveN;
import util.action.move.ActionStackMove;
import util.action.state.ActionSetRotation;
import util.state.containerState.ContainerState;

public final class FromTo extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction locFrom;
    private final IntFunction levelFrom;
    private final IntFunction countFn;
    private final IntFunction locTo;
    private final Rotations rotationTo;
    private final IntFunction levelTo;
    private final RegionFunction regionFrom;
    private final RegionFunction regionTo;
    private final BooleanFunction moveRule;
    private final BooleanFunction captureRule;
    private final Moves captureEffect;
    private final RoleType mover;
    private final boolean stack;
    private SiteType typeFrom;
    private SiteType typeTo;
    private final BooleanFunction copy;
    
    public FromTo(final From from, final To to, @Opt @Name final IntFunction count, @Opt @Name final BooleanFunction copy, @Opt @Name final Boolean stack, @Opt final RoleType mover, @Opt final Then then) {
        super(then);
        this.locFrom = from.loc();
        this.levelFrom = from.level();
        this.countFn = count;
        this.locTo = to.loc();
        this.levelTo = to.level();
        this.regionFrom = from.region();
        this.regionTo = to.region();
        this.moveRule = ((to.cond() == null) ? BooleanConstant.construct(true) : to.cond());
        this.captureRule = ((to.effect() == null) ? null : to.effect().condition());
        this.captureEffect = ((to.effect() == null) ? null : to.effect().effect());
        this.mover = mover;
        this.rotationTo = to.rotations();
        this.stack = (stack != null && stack);
        this.typeFrom = from.type();
        this.typeTo = to.type();
        this.copy = ((copy == null) ? BooleanConstant.construct(false) : copy);
    }
    
    @Override
    public Moves eval(final Context context) {
        final int[] sitesFrom = (this.regionFrom == null) ? new int[] { this.locFrom.eval(context) } : this.regionFrom.eval(context).sites();
        final int origFrom = context.from();
        final int origTo = context.to();
        final boolean copyTo = this.copy.eval(context);
        final BaseMoves moves = new BaseMoves(super.then());
        for (final int from : sitesFrom) {
            if (from > -1) {
                final int cidFrom = context.containerId()[from];
                SiteType realTypeFrom = this.typeFrom;
                if (cidFrom > 0) {
                    realTypeFrom = SiteType.Cell;
                }
                else if (realTypeFrom == null) {
                    realTypeFrom = context.board().defaultSite();
                }
                final ContainerState cs = context.containerState(cidFrom);
                final int what = cs.what(from, realTypeFrom);
                final int count = cs.count(from, realTypeFrom);
                if (what > 0 || count > 0) {
                    context.setFrom(from);
                    final int[] sitesTo = (this.regionTo == null) ? new int[] { this.locTo.eval(context) } : this.regionTo.eval(context).sites();
                    context.setFrom(origFrom);
                    final Component component = context.components()[what];
                    if (component != null && component.isLargePiece()) {
                        final BaseMoves movesLargePiece = this.evalLargePiece(context, from, sitesTo);
                        for (final Move m : movesLargePiece.moves()) {
                            moves.moves().add(m);
                        }
                    }
                    else {
                        for (final int to : sitesTo) {
                            if (to > -1) {
                                final int cidTo = context.containerId()[to];
                                SiteType realTypeTo = this.typeTo;
                                if (cidTo > 0) {
                                    realTypeTo = SiteType.Cell;
                                }
                                else if (realTypeTo == null) {
                                    realTypeTo = context.board().defaultSite();
                                }
                                Action actionMove;
                                if (this.levelTo != null) {
                                    if (!this.stack) {
                                        if (this.levelFrom == null) {
                                            actionMove = new ActionMove(realTypeFrom, from, -1, realTypeTo, to, this.levelTo.eval(context), -1, -1, false);
                                            actionMove.setLevelFrom(cs.sizeStack(from, this.typeFrom) - 1);
                                        }
                                        else {
                                            actionMove = new ActionMove(realTypeFrom, from, this.levelFrom.eval(context), realTypeTo, to, this.levelTo.eval(context), -1, -1, false);
                                        }
                                    }
                                    else {
                                        actionMove = new ActionMove(realTypeFrom, from, -1, realTypeTo, to, this.levelTo.eval(context), -1, -1, true);
                                        actionMove.setLevelFrom(cs.sizeStack(from, this.typeFrom) - 1);
                                    }
                                }
                                else if (this.levelFrom == null && this.countFn == null) {
                                    if (copyTo) {
                                        actionMove = new ActionCopy(realTypeFrom, from, realTypeTo, to);
                                    }
                                    else {
                                        actionMove = new ActionMove(realTypeFrom, from, -1, realTypeTo, to, -1, -1, -1, false);
                                    }
                                    actionMove.setLevelFrom(cs.sizeStack(from, this.typeFrom) - 1);
                                }
                                else if (this.levelFrom != null) {
                                    actionMove = new ActionMove(realTypeFrom, from, this.levelFrom.eval(context), realTypeTo, to, -1, -1, -1, false);
                                }
                                else if (!this.stack) {
                                    actionMove = new ActionMoveN(realTypeFrom, from, realTypeTo, to, this.countFn.eval(context));
                                    actionMove.setLevelFrom(cs.sizeStack(from, this.typeFrom) - 1);
                                }
                                else {
                                    final int countStackElem = this.countFn.eval(context);
                                    actionMove = new ActionStackMove(realTypeFrom, from, realTypeTo, to, countStackElem);
                                    actionMove.setLevelFrom(cs.sizeStack(from, realTypeFrom) - countStackElem);
                                    actionMove.setLevelTo(cs.sizeStack(to, realTypeTo));
                                }
                                if (this.isDecision()) {
                                    actionMove.setDecision(true);
                                }
                                context.setFrom(from);
                                context.setTo(to);
                                if (this.moveRule == null || this.moveRule.eval(context)) {
                                    context.setFrom(origFrom);
                                    Move move = new Move(actionMove);
                                    move.setFromNonDecision(from);
                                    move.setToNonDecision(to);
                                    if (this.mover == null) {
                                        move.setMover(context.state().mover());
                                    }
                                    else {
                                        move.setMover(new Id(null, this.mover).eval(context));
                                    }
                                    if (context.game().isStacking()) {
                                        if (this.levelFrom == null) {
                                            move.setLevelMinNonDecision(context.state().containerStates()[context.containerId()[from]].sizeStack(from, realTypeFrom) - 1);
                                            move.setLevelMaxNonDecision(context.state().containerStates()[context.containerId()[from]].sizeStack(from, realTypeFrom) - 1);
                                        }
                                        else {
                                            move.setLevelMinNonDecision(this.levelFrom.eval(context));
                                            move.setLevelMaxNonDecision(this.levelFrom.eval(context));
                                        }
                                    }
                                    if (this.captureRule != null && this.captureRule.eval(context)) {
                                        context.setFrom(from);
                                        context.setTo(to);
                                        move = MoveUtilities.chainRuleWithAction(context, this.captureEffect, move, true, false);
                                        move.setFromNonDecision(from);
                                        move.setToNonDecision(to);
                                        if (this.mover == null) {
                                            move.setMover(context.state().mover());
                                        }
                                        else {
                                            move.setMover(new Id(null, this.mover).eval(context));
                                        }
                                        MoveUtilities.chainRuleCrossProduct(context, moves, null, move, false);
                                    }
                                    else if (this.rotationTo == null) {
                                        moves.moves().add(move);
                                    }
                                    else if (this.rotationTo != null) {
                                        final int[] eval;
                                        final int[] rotations = eval = this.rotationTo.eval(context);
                                        for (final int rotation : eval) {
                                            final Move moveWithRotation = new Move(move);
                                            final ActionSetRotation actionRotation = new ActionSetRotation(this.typeTo, to, rotation);
                                            moveWithRotation.actions().add(actionRotation);
                                            moves.moves().add(moveWithRotation);
                                        }
                                    }
                                }
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
    
    private BaseMoves evalLargePiece(final Context context, final int from, final int[] sitesTo) {
        final int origFrom = context.from();
        final int origTo = context.to();
        final BaseMoves moves = new BaseMoves(super.then());
        final ContainerState cs = context.containerState(context.containerId()[from]);
        final int what = cs.what(from, this.typeFrom);
        final int localState = cs.state(from, this.typeFrom);
        final Component largePiece = context.components()[what];
        final int nbPossibleStates = largePiece.walk().length * 4;
        final TIntArrayList currentLocs = largePiece.locs(context, from, localState, context.topology());
        final TIntArrayList newSitesTo = new TIntArrayList();
        for (int i = 0; i < sitesTo.length; ++i) {
            newSitesTo.add(sitesTo[i]);
        }
        for (int i = 1; i < currentLocs.size(); ++i) {
            newSitesTo.add(currentLocs.getQuick(i));
        }
        for (int index = 0; index < newSitesTo.size(); ++index) {
            final int to = newSitesTo.getQuick(index);
            for (int state = 0; state < nbPossibleStates; ++state) {
                final TIntArrayList locs = largePiece.locs(context, to, state, context.topology());
                if (locs != null) {
                    if (locs.size() > 0) {
                        final ContainerState csTo = context.containerState(context.containerId()[locs.getQuick(0)]);
                        boolean valid = true;
                        for (int j = 0; j < locs.size(); ++j) {
                            if (!largePiece.isDomino()) {
                                if (!newSitesTo.contains(locs.getQuick(j)) && locs.getQuick(j) != from) {
                                    valid = false;
                                    break;
                                }
                            }
                            else if (!csTo.isPlayable(locs.getQuick(j)) && context.trial().moveNumber() > 0) {
                                valid = false;
                                break;
                            }
                        }
                        if (valid && (from != to || (from == to && localState != state))) {
                            final ActionMove actionMove = new ActionMove(this.typeFrom, from, -1, this.typeTo, to, -1, state, -1, false);
                            if (this.isDecision()) {
                                actionMove.setDecision(true);
                            }
                            Move move = new Move(actionMove);
                            move = MoveUtilities.chainRuleWithAction(context, this.captureEffect, move, true, false);
                            move.setFromNonDecision(from);
                            move.setToNonDecision(to);
                            move.setStateNonDecision(state);
                            if (this.mover == null) {
                                move.setMover(context.state().mover());
                            }
                            else {
                                move.setMover(new Id(null, this.mover).eval(context));
                            }
                            MoveUtilities.chainRuleCrossProduct(context, moves, null, move, false);
                        }
                    }
                }
            }
        }
        context.setTo(origTo);
        context.setFrom(origFrom);
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game);
        gameFlags |= 0x1L;
        gameFlags |= SiteType.stateFlags(this.typeFrom);
        gameFlags |= SiteType.stateFlags(this.typeTo);
        if (this.locFrom != null) {
            gameFlags |= this.locFrom.gameFlags(game);
        }
        if (this.locTo != null) {
            gameFlags |= this.locTo.gameFlags(game);
        }
        if (this.regionFrom != null) {
            gameFlags |= this.regionFrom.gameFlags(game);
        }
        if (this.regionTo != null) {
            gameFlags |= this.regionTo.gameFlags(game);
        }
        if (this.levelFrom != null || this.stack) {
            gameFlags |= 0x10L;
        }
        if (this.rotationTo != null) {
            gameFlags |= (this.rotationTo.gameFlags(game) | 0x20000L);
        }
        gameFlags |= this.copy.gameFlags(game);
        if (this.then() != null) {
            gameFlags |= this.then().gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        boolean isStatic = true;
        if (this.locFrom != null) {
            isStatic = (isStatic && this.locFrom.isStatic());
        }
        if (this.locTo != null) {
            isStatic = (isStatic && this.locTo.isStatic());
        }
        if (this.regionFrom != null) {
            isStatic = (isStatic && this.regionFrom.isStatic());
        }
        if (this.regionTo != null) {
            isStatic = (isStatic && this.regionTo.isStatic());
        }
        isStatic = (isStatic && this.copy.isStatic());
        return isStatic;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.typeFrom == null) {
            this.typeFrom = game.board().defaultSite();
        }
        if (this.typeTo == null) {
            this.typeTo = game.board().defaultSite();
        }
        super.preprocess(game);
        if (this.locFrom != null) {
            this.locFrom.preprocess(game);
        }
        if (this.locTo != null) {
            this.locTo.preprocess(game);
        }
        if (this.regionFrom != null) {
            this.regionFrom.preprocess(game);
        }
        if (this.regionTo != null) {
            this.regionTo.preprocess(game);
        }
        if (this.rotationTo != null) {
            this.rotationTo.preprocess(game);
        }
        if (this.levelFrom != null) {
            this.levelFrom.preprocess(game);
        }
        if (this.countFn != null) {
            this.countFn.preprocess(game);
        }
        if (this.levelTo != null) {
            this.levelTo.preprocess(game);
        }
        if (this.moveRule != null) {
            this.moveRule.preprocess(game);
        }
        if (this.captureRule != null) {
            this.captureRule.preprocess(game);
        }
        if (this.captureEffect != null) {
            this.captureEffect.preprocess(game);
        }
        this.copy.preprocess(game);
    }
    
    public IntFunction locFrom() {
        return this.locFrom;
    }
    
    public IntFunction locTo() {
        return this.locTo;
    }
    
    public RegionFunction regionFrom() {
        return this.regionFrom;
    }
    
    public RegionFunction regionTo() {
        return this.regionTo;
    }
    
    public BooleanFunction moveRule() {
        return this.moveRule;
    }
}
