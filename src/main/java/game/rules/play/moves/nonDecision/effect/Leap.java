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
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.functions.region.sites.Sites;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.types.board.StepType;
import game.util.directions.CompassDirection;
import game.util.moves.From;
import game.util.moves.To;
import topology.Topology;
import topology.TopologyElement;
import util.ContainerId;
import util.Context;
import util.Move;
import util.MoveUtilities;
import util.action.move.ActionMove;

public final class Leap extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLocationFn;
    private final RegionFunction walk;
    private final BooleanFunction forward;
    private final BooleanFunction goRule;
    private final Moves sideEffect;
    private SiteType type;
    
    public Leap(@Opt final From from, final StepType[][] walk, @Opt @Name final BooleanFunction forward, @Opt @Name final BooleanFunction rotations, final To to, @Opt final Then then) {
        super(then);
        this.startLocationFn = ((from == null) ? new game.functions.ints.context.From(null) : from.loc());
        this.type = ((from == null) ? null : from.type());
        this.walk = Sites.construct(null, this.startLocationFn, walk, rotations);
        this.forward = ((forward == null) ? BooleanConstant.construct(false) : forward);
        this.goRule = to.cond();
        this.sideEffect = to.effect();
    }
    
    @Override
    public final Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int from = this.startLocationFn.eval(context);
        final int cid = new ContainerId(null, null, null, null, new IntConstant(from)).eval(context);
        final Topology graph = context.containers()[cid].topology();
        CompassDirection facing = null;
        if (this.forward.eval(context)) {
            final int pieceIndex = context.state().containerStates()[cid].what(from, this.type);
            if (pieceIndex != 0) {
                final Component piece = context.game().equipment().components()[pieceIndex];
                facing = (CompassDirection)piece.getDirn();
            }
        }
        if (from == -1) {
            return moves;
        }
        final int origFrom = context.from();
        final int origTo = context.to();
        final int[] sites;
        final int[] sitesAfterWalk = sites = this.walk.eval(context).sites();
        for (final int to : sites) {
            final TopologyElement fromV = ((this.type != null && this.type == SiteType.Cell) || (this.type == null && context.game().board().defaultSite() != SiteType.Vertex)) ? graph.cells().get(from) : graph.vertices().get(from);
            final TopologyElement toV = ((this.type != null && this.type == SiteType.Cell) || (this.type == null && context.game().board().defaultSite() != SiteType.Vertex)) ? graph.cells().get(to) : graph.vertices().get(to);
            if (facing == null || checkForward(facing, fromV, toV)) {
                context.setTo(to);
                if (this.goRule.eval(context)) {
                    final ActionMove actionMove = new ActionMove(SiteType.Cell, from, -1, SiteType.Cell, to, -1, -1, -1, false);
                    if (this.isDecision()) {
                        actionMove.setDecision(true);
                    }
                    Move thisAction = new Move(actionMove);
                    thisAction = MoveUtilities.chainRuleWithAction(context, this.sideEffect, thisAction, true, false);
                    MoveUtilities.chainRuleCrossProduct(context, moves, null, thisAction, false);
                    thisAction.setFromNonDecision(from);
                    thisAction.setToNonDecision(to);
                }
            }
        }
        context.setTo(origTo);
        context.setFrom(origFrom);
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
    
    private static boolean checkForward(final CompassDirection facing, final TopologyElement from, final TopologyElement to) {
        switch (facing) {
            case N -> {
                return from.row() < to.row();
            }
            case NE -> {
                return from.row() < to.row() && from.col() < to.col();
            }
            case E -> {
                return from.col() < to.col();
            }
            case SE -> {
                return from.row() > to.row() && from.col() < to.col();
            }
            case S -> {
                return from.row() > to.row();
            }
            case SW -> {
                return from.row() > to.row() && from.col() > to.col();
            }
            case W -> {
                return from.col() > to.col();
            }
            case NW -> {
                return from.row() < to.row() && from.col() > to.col();
            }
            default -> {
                return false;
            }
        }
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game);
        gameFlags |= 0x1L;
        if (this.startLocationFn != null) {
            gameFlags |= this.startLocationFn.gameFlags(game);
        }
        if (this.sideEffect != null) {
            gameFlags |= this.sideEffect.gameFlags(game);
        }
        if (this.walk != null) {
            gameFlags |= this.walk.gameFlags(game);
        }
        if (this.goRule != null) {
            gameFlags |= this.goRule.gameFlags(game);
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
        if (this.startLocationFn != null) {
            this.startLocationFn.preprocess(game);
        }
        if (this.walk != null) {
            this.walk.preprocess(game);
        }
        if (this.goRule != null) {
            this.goRule.preprocess(game);
        }
        if (this.sideEffect != null) {
            this.sideEffect.preprocess(game);
        }
    }
    
    public IntFunction startLocationFn() {
        return this.startLocationFn;
    }
    
    public RegionFunction walk() {
        return this.walk;
    }
    
    public BooleanFunction goRule() {
        return this.goRule;
    }
}
