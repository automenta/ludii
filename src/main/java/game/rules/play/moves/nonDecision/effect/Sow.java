// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.equipment.container.board.Track;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.count.site.CountNumber;
import game.functions.ints.last.LastTo;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.NonDecision;
import game.types.board.SiteType;
import util.Context;
import util.Move;
import util.action.move.ActionMove;

import java.util.ArrayList;
import java.util.List;

public final class Sow extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLoc;
    private final IntFunction countFn;
    private final String trackName;
    private final IntFunction ownerFn;
    private final boolean includeSelf;
    private final BooleanFunction origin;
    private final BooleanFunction skipFn;
    private final BooleanFunction captureRule;
    private final BooleanFunction backtracking;
    private final Moves captureEffect;
    private SiteType type;
    private List<Track> preComputedTracks;
    
    public Sow(@Opt final SiteType type, @Opt final IntFunction start, @Opt @Name final IntFunction count, @Opt final String trackName, @Opt @Name final IntFunction owner, @Opt @Name final BooleanFunction If, @Opt @Name final NonDecision apply, @Opt @Name final Boolean includeSelf, @Opt @Name final BooleanFunction origin, @Opt @Name final BooleanFunction skipIf, @Opt @Name final BooleanFunction backtracking, @Opt final Then then) {
        super(then);
        this.preComputedTracks = new ArrayList<>();
        this.startLoc = ((start == null) ? new LastTo(null) : start);
        this.includeSelf = (includeSelf == null || includeSelf);
        this.countFn = ((count == null) ? new CountNumber(null, null, this.startLoc) : count);
        this.trackName = trackName;
        this.captureRule = ((If == null) ? BooleanConstant.construct(true) : If);
        this.captureEffect = apply;
        this.skipFn = skipIf;
        this.backtracking = backtracking;
        this.origin = ((origin == null) ? BooleanConstant.construct(false) : origin);
        this.ownerFn = owner;
        this.type = type;
    }
    
    @Override
    public Moves eval(final Context context) {
        final int start = this.startLoc.eval(context);
        int count = this.countFn.eval(context);
        final Moves moves = new BaseMoves(super.then());
        final Move move = new Move(new ArrayList<>());
        final int origFrom = context.from();
        final int origTo = context.to();
        final int owner = (this.ownerFn == null) ? -1 : this.ownerFn.eval(context);
        Track track = null;
        for (final Track t : this.preComputedTracks) {
            if (this.trackName == null || (owner == -1 && t.name().equals(this.trackName)) || (owner != -1 && t.owner() == owner && t.name().contains(this.trackName))) {
                track = t;
                break;
            }
        }
        if (track == null) {
            return moves;
        }
        int i;
        for (i = 0; i < track.elems().length && track.elems()[i].site != start; ++i) {}
        if (this.origin.eval(context)) {
            move.actions().add(new ActionMove(this.type, start, -1, this.type, start, -1, -1, -1, false));
            --count;
            context.setTo(start);
        }
        for (int index = 0; index < count; ++index) {
            int to = track.elems()[i].next;
            context.setTo(to);
            if (this.skipFn != null && this.skipFn.eval(context)) {
                --index;
                i = track.elems()[i].nextIndex;
                to = track.elems()[i].next;
            }
            else {
                if (!this.includeSelf && to == start) {
                    i = track.elems()[i].nextIndex;
                    to = track.elems()[i].next;
                }
                move.actions().add(new ActionMove(this.type, start, -1, this.type, to, -1, -1, -1, false));
                context.setTo(to);
                i = track.elems()[i].nextIndex;
            }
        }
        moves.moves().add(move);
        if (this.captureRule != null && this.captureEffect != null) {
            for (final Move sowMove : moves.moves()) {
                final Context newContext = new Context(context);
                sowMove.apply(newContext, false);
                while (this.captureRule.eval(newContext)) {
                    newContext.setFrom(start);
                    final Moves capturingMoves = this.captureEffect.eval(newContext);
                    for (final Move m : capturingMoves.moves()) {
                        sowMove.actions().addAll(m.getAllActions(newContext));
                    }
                    final int to2 = track.elems()[i].prev;
                    i = track.elems()[i].prevIndex;
                    newContext.setTo(to2);
                    if (this.backtracking == null) {
                        break;
                    }
                    if (!this.backtracking.eval(newContext)) {
                        break;
                    }
                    if (to2 == start) {
                        break;
                    }
                }
            }
        }
        context.setTo(origTo);
        context.setFrom(origFrom);
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0x4L | super.gameFlags(game);
        flags |= 0x1L;
        flags |= this.startLoc.gameFlags(game);
        flags |= this.countFn.gameFlags(game);
        if (this.captureEffect != null) {
            flags |= this.captureEffect.gameFlags(game);
            if (this.captureEffect.then() != null) {
                flags |= this.captureEffect.then().gameFlags(game);
            }
        }
        flags |= SiteType.stateFlags(this.type);
        if (this.ownerFn != null) {
            flags |= this.ownerFn.gameFlags(game);
        }
        if (this.captureRule != null) {
            flags |= this.captureRule.gameFlags(game);
        }
        if (this.origin != null) {
            flags |= this.origin.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        super.preprocess(game);
        this.startLoc.preprocess(game);
        this.countFn.preprocess(game);
        this.origin.preprocess(game);
        if (this.captureEffect != null) {
            this.captureEffect.preprocess(game);
        }
        if (this.ownerFn != null) {
            this.ownerFn.preprocess(game);
        }
        if (this.captureRule != null) {
            this.captureRule.preprocess(game);
        }
        if (this.skipFn != null) {
            this.skipFn.preprocess(game);
        }
        if (this.backtracking != null) {
            this.backtracking.preprocess(game);
        }
        if (this.captureEffect != null) {
            this.captureEffect.preprocess(game);
        }
        this.preComputedTracks = new ArrayList<>();
        for (final Track t : game.board().tracks()) {
            if (this.trackName == null || t.name().contains(this.trackName)) {
                this.preComputedTracks.add(t);
            }
        }
    }
    
    @Override
    public String toEnglish() {
        return "Sow";
    }
}
