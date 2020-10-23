// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import game.Game;
import game.equipment.container.board.Track;
import game.functions.booleans.BooleanFunction;
import game.functions.booleans.is.in.IsIn;
import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.region.sites.index.SitesEmpty;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.graph.Radial;
import game.util.moves.Between;
import game.util.moves.From;
import game.util.moves.To;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.Move;
import util.MoveUtilities;
import util.action.Action;
import util.action.move.ActionAdd;
import util.action.move.ActionMove;

import java.util.ArrayList;
import java.util.List;

public final class Slide extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLocationFn;
    private final IntFunction limit;
    private final IntFunction minFn;
    private final BooleanFunction goRule;
    private final BooleanFunction stopRule;
    private final BooleanFunction toRule;
    private final IntFunction let;
    private final Moves sideEffect;
    private final DirectionsFunction dirnChoice;
    private final String trackName;
    protected SiteType type;
    private List<Track> preComputedTracks;
    
    public Slide(@Opt final From from, @Opt final String track, @Opt final Direction directions, @Opt final Between between, @Opt final To to, @Opt final Then then) {
        super(then);
        this.preComputedTracks = new ArrayList<>();
        this.startLocationFn = ((from == null) ? new game.functions.ints.context.From(null) : from.loc());
        this.type = ((from == null) ? null : from.type());
        this.minFn = ((between == null || between.range() == null) ? new IntConstant(-1) : between.range().minFn());
        this.limit = ((between == null || between.range() == null) ? new IntConstant(1000) : between.range().maxFn());
        this.sideEffect = ((to == null || to.effect() == null) ? null : to.effect().effect());
        this.goRule = ((between == null || between.condition() == null) ? IsIn.construct(null, new IntFunction[] { game.functions.ints.context.Between.instance() }, SitesEmpty.construct(null, null)) : between.condition());
        this.stopRule = ((to == null) ? null : to.cond());
        this.let = ((between == null) ? null : between.trail());
        this.dirnChoice = ((directions != null) ? directions.directionsFunctions() : new Directions(AbsoluteDirection.Adjacent, null));
        this.trackName = track;
        this.toRule = ((to == null || to.effect() == null) ? null : to.effect().condition());
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int from = this.startLocationFn.eval(context);
        final int min = this.minFn.eval(context);
        if (from == -1) {
            return moves;
        }
        if (this.trackName != null) {
            return this.slideByTrack(context);
        }
        final int origFrom = context.from();
        final int origTo = context.to();
        final int origBetween = context.between();
        final Topology topology = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final TopologyElement fromV = topology.getGraphElements(realType).get(from);
        final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(realType, fromV, null, null, null, context);
        final int maxPathLength = this.limit.eval(context);
        context.setFrom(fromV.index());
        for (final AbsoluteDirection direction : directions) {
            final List<Radial> radials = topology.trajectories().radials(this.type, fromV.index(), direction);
            for (final Radial radial : radials) {
                context.setBetween(origBetween);
                int toIdx = 1;
                while (toIdx < radial.steps().length && toIdx <= maxPathLength) {
                    final int to = radial.steps()[toIdx].id();
                    context.setTo(to);
                    if (this.stopRule != null && this.stopRule.eval(context) && min <= toIdx) {
                        final ActionMove actionMove = new ActionMove(this.type, from, -1, this.type, to, -1, -1, -1, false);
                        if (this.isDecision()) {
                            actionMove.setDecision(true);
                        }
                        Move thisAction = new Move(actionMove);
                        thisAction = MoveUtilities.chainRuleWithAction(context, this.sideEffect, thisAction, true, false);
                        if (this.let != null) {
                            final int pieceToLet = this.let.eval(context);
                            for (int i = 0; i < toIdx; ++i) {
                                final Action add = new ActionAdd(this.type, radial.steps()[i].id(), pieceToLet, 1, -1, -1, null, null, null);
                                thisAction.actions().add(add);
                            }
                        }
                        context.setTo(to);
                        if (this.toRule == null || this.toRule.eval(context)) {
                            MoveUtilities.chainRuleCrossProduct(context, moves, null, thisAction, false);
                            thisAction.setFromNonDecision(from);
                            thisAction.setToNonDecision(to);
                            break;
                        }
                        break;
                    }
                    else {
                        context.setBetween(to);
                        if (!this.goRule.eval(context)) {
                            break;
                        }
                        if (min <= toIdx) {
                            final ActionMove actionMove = new ActionMove(this.type, from, -1, this.type, to, -1, -1, -1, false);
                            if (this.isDecision()) {
                                actionMove.setDecision(true);
                            }
                            final Move thisAction = new Move(actionMove);
                            thisAction.setFromNonDecision(from);
                            thisAction.setToNonDecision(to);
                            if (this.let != null) {
                                final int pieceToLet = this.let.eval(context);
                                for (int i = 0; i < toIdx; ++i) {
                                    final Action add = new ActionAdd(this.type, radial.steps()[i].id(), pieceToLet, 1, -1, -1, null, null, null);
                                    thisAction.actions().add(add);
                                }
                            }
                            context.setTo(to);
                            if (this.toRule == null || this.toRule.eval(context)) {
                                MoveUtilities.chainRuleCrossProduct(context, moves, null, thisAction, false);
                                thisAction.setFromNonDecision(from);
                                thisAction.setToNonDecision(to);
                            }
                        }
                        ++toIdx;
                    }
                }
            }
        }
        context.setTo(origTo);
        context.setFrom(origFrom);
        context.setBetween(origBetween);
        for (final Move m : moves.moves()) {
            m.setMover(context.state().mover());
        }
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    private Moves slideByTrack(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        if (this.preComputedTracks.size() == 0) {
            return moves;
        }
        final int from = this.startLocationFn.eval(context);
        final int origFrom = context.from();
        final int origTo = context.to();
        final int origStep = context.between();
        for (final Track track : this.preComputedTracks) {
            for (int i = 0; i < track.elems().length; ++i) {
                if (track.elems()[i].site == from) {
                    int index = i;
                    int nbBump = track.elems()[index].bump;
                    int to = track.elems()[index].site;
                    for (int nbElem = 1; track.elems()[index].next != -1 && nbElem < track.elems().length; index = track.elems()[index].nextIndex, nbBump += track.elems()[index].bump) {
                        to = track.elems()[index].next;
                        ++nbElem;
                        context.setTo(to);
                        if (nbBump > 0) {
                            if (this.stopRule != null && this.stopRule.eval(context)) {
                                final ActionMove actionMove = new ActionMove(this.type, from, -1, this.type, to, -1, -1, -1, false);
                                if (this.isDecision()) {
                                    actionMove.setDecision(true);
                                }
                                Move thisAction = new Move(actionMove);
                                thisAction = MoveUtilities.chainRuleWithAction(context, this.sideEffect, thisAction, true, false);
                                MoveUtilities.chainRuleCrossProduct(context, moves, null, thisAction, false);
                                thisAction.setFromNonDecision(from);
                                thisAction.setToNonDecision(to);
                                break;
                            }
                            context.setTo(to);
                            if (this.toRule == null || this.toRule.eval(context)) {
                                final ActionMove actionMove = new ActionMove(this.type, from, -1, this.type, to, -1, -1, -1, false);
                                if (this.isDecision()) {
                                    actionMove.setDecision(true);
                                }
                                final Move thisAction = new Move(actionMove);
                                thisAction.setFromNonDecision(from);
                                thisAction.setToNonDecision(to);
                                MoveUtilities.chainRuleCrossProduct(context, moves, null, thisAction, false);
                                thisAction.setFromNonDecision(from);
                                thisAction.setToNonDecision(to);
                            }
                        }
                        context.setBetween(to);
                        if (!this.goRule.eval(context)) {
                            break;
                        }
                    }
                }
            }
        }
        context.setTo(origTo);
        context.setFrom(origFrom);
        context.setBetween(origStep);
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game) | this.goRule.gameFlags(game);
        gameFlags |= 0x1L;
        if (this.startLocationFn != null) {
            gameFlags |= this.startLocationFn.gameFlags(game);
        }
        if (this.limit != null) {
            gameFlags |= this.limit.gameFlags(game);
        }
        if (this.stopRule != null) {
            gameFlags |= this.stopRule.gameFlags(game);
        }
        if (this.toRule != null) {
            gameFlags |= this.toRule.gameFlags(game);
        }
        if (this.let != null) {
            gameFlags |= this.let.gameFlags(game);
        }
        if (this.sideEffect != null) {
            gameFlags |= this.sideEffect.gameFlags(game);
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
        this.goRule.preprocess(game);
        if (this.startLocationFn != null) {
            this.startLocationFn.preprocess(game);
        }
        if (this.limit != null) {
            this.limit.preprocess(game);
        }
        if (this.let != null) {
            this.let.preprocess(game);
        }
        if (this.stopRule != null) {
            this.stopRule.preprocess(game);
        }
        if (this.sideEffect != null) {
            this.sideEffect.preprocess(game);
        }
        if (this.toRule != null) {
            this.toRule.preprocess(game);
        }
        if (this.trackName != null) {
            this.preComputedTracks = new ArrayList<>();
            for (final Track t : game.board().tracks()) {
                if (t.name().equals(this.trackName) || this.trackName.equals("AllTracks")) {
                    this.preComputedTracks.add(t);
                }
            }
        }
    }
    
    public BooleanFunction goRule() {
        return this.goRule;
    }
    
    public BooleanFunction stopRule() {
        return this.stopRule;
    }
}
