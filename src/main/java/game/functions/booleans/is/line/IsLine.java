// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.line;

import annotations.*;
import game.Game;
import game.equipment.component.Component;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.ints.last.LastTo;
import game.functions.region.RegionFunction;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.directions.AbsoluteDirection;
import game.util.graph.Radial;
import gnu.trove.list.array.TIntArrayList;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.locations.FullLocation;
import util.locations.Location;
import util.state.containerStackingState.BaseContainerStateStacking;
import util.state.containerState.ContainerState;
import util.state.puzzleState.ContainerDeductionPuzzleState;

import java.util.ArrayList;
import java.util.List;

@Hide
public class IsLine extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction length;
    private final AbsoluteDirection dirn;
    private final IntFunction through;
    private final RegionFunction throughAny;
    private final IntFunction[] whatFn;
    private final IntFunction whoFn;
    private final BooleanFunction exactly;
    private final BooleanFunction byLevelFn;
    private final BooleanFunction condition;
    private SiteType type;
    
    public IsLine(@Opt final SiteType type, final IntFunction length, @Opt final AbsoluteDirection dirn, @Opt @Or @Name final IntFunction through, @Opt @Or @Name final RegionFunction throughAny, @Opt @Or2 final RoleType who, @Opt @Or2 @Name final IntFunction what, @Opt @Or2 @Name final IntFunction[] whats, @Opt @Name final BooleanFunction exact, @Opt @Name final BooleanFunction If, @Opt @Name final BooleanFunction byLevel) {
        this.length = length;
        this.dirn = ((dirn == null) ? AbsoluteDirection.Adjacent : dirn);
        this.through = ((through == null) ? new LastTo(null) : through);
        this.throughAny = throughAny;
        this.exactly = ((exact == null) ? BooleanConstant.construct(false) : exact);
        this.condition = ((If == null) ? BooleanConstant.construct(true) : If);
        if (whats != null) {
            this.whatFn = whats;
        }
        else if (what != null) {
            (this.whatFn = new IntFunction[1])[0] = what;
        }
        else {
            this.whatFn = null;
        }
        this.whoFn = ((who != null) ? new Id(null, who) : null);
        this.type = type;
        this.byLevelFn = ((byLevel == null) ? BooleanConstant.construct(false) : byLevel);
    }
    
    @Override
    public boolean eval(final Context context) {
        if (context.game().isStacking()) {
            return this.evalStack(context);
        }
        if (context.game().isDeductionPuzzle()) {
            return this.evalDeductionPuzzle(context);
        }
        int[] pivots;
        if (this.throughAny != null) {
            final TIntArrayList listPivots = new TIntArrayList(this.throughAny.eval(context).sites());
            if (this.whatFn != null) {
                final TIntArrayList whats = new TIntArrayList();
                for (final IntFunction what : this.whatFn) {
                    whats.add(what.eval(context));
                }
                for (int i = listPivots.size() - 1; i >= 0; --i) {
                    final int loc = listPivots.getQuick(i);
                    final int contId = context.containerId()[loc];
                    final ContainerState state = context.state().containerStates()[contId];
                    final int what2 = state.what(loc, this.type);
                    if (!whats.contains(what2)) {
                        listPivots.removeAt(i);
                    }
                }
            }
            pivots = listPivots.toArray();
        }
        else {
            pivots = new int[] { this.through.eval(context) };
        }
        final Topology graph = context.topology();
        final boolean playOnCell = (this.type != null && this.type == SiteType.Cell) || (this.type == null && context.game().board().defaultSite() != SiteType.Vertex);
        for (final int locn : pivots) {
            if (locn < 0) {
                return false;
            }
            final int origTo = context.to();
            context.setTo(locn);
            if (!this.condition.eval(context)) {
                context.setTo(origTo);
                return false;
            }
            if (playOnCell && locn >= graph.cells().size()) {
                return false;
            }
            if (!playOnCell && locn >= graph.vertices().size()) {
                return false;
            }
            final TopologyElement vertexLoc = playOnCell ? graph.cells().get(locn) : graph.vertices().get(locn);
            final ContainerState state2 = context.state().containerStates()[context.containerId()[vertexLoc.index()]];
            final TIntArrayList whats2 = new TIntArrayList();
            final int whatLocn = state2.what(locn, this.type);
            if (this.whatFn == null) {
                if (this.whoFn == null) {
                    whats2.add(whatLocn);
                } else if (this.whoFn != null) {
                    final int who = this.whoFn.eval(context);
                    for (int j = 1; j < context.components().length; ++j) {
                        final Component component = context.components()[j];
                        if (component.owner() == who) {
                            whats2.add(component.index());
                        }
                    }
                }
            } else {
                for (final IntFunction what3 : this.whatFn) {
                    whats2.add(what3.eval(context));
                }
            }
            if (whats2.contains(whatLocn)) {
                final int len = this.length.eval(context);
                final boolean exact = this.exactly.eval(context);
                final List<Radial> radials = graph.trajectories().radials(this.type, locn).distinctInDirection(this.dirn);
                for (final Radial radial : radials) {
                    int count = whats2.contains(whatLocn) ? 1 : 0;
                    for (int indexPath = 1; indexPath < radial.steps().length; ++indexPath) {
                        final int index = radial.steps()[indexPath].id();
                        context.setTo(index);
                        if (!whats2.contains(state2.what(index, this.type)) || !this.condition.eval(context)) {
                            break;
                        }
                        ++count;
                        if (!exact && count == len) {
                            context.setTo(origTo);
                            return true;
                        }
                    }
                    final List<Radial> oppositeRadials = radial.opposites();
                    if (oppositeRadials != null) {
                        for (final Radial oppositeRadial : oppositeRadials) {
                            int oppositeCount = count;
                            for (int indexPath2 = 1; indexPath2 < oppositeRadial.steps().length; ++indexPath2) {
                                final int index2 = oppositeRadial.steps()[indexPath2].id();
                                context.setTo(index2);
                                if (!whats2.contains(state2.what(index2, this.type)) || !this.condition.eval(context)) {
                                    break;
                                }
                                ++oppositeCount;
                                if (!exact && oppositeCount == len) {
                                    context.setTo(origTo);
                                    return true;
                                }
                            }
                            if (oppositeCount == len) {
                                context.setTo(origTo);
                                return true;
                            }
                        }
                    } else {
                        if (count == len) {
                            context.setTo(origTo);
                            return true;
                        }
                    }
                }
                context.setTo(origTo);
            }
        }
        return false;
    }
    
    public boolean evalDeductionPuzzle(final Context context) {
        int[] pivots;
        if (this.throughAny != null) {
            final TIntArrayList listPivots = new TIntArrayList(this.throughAny.eval(context).sites());
            if (this.whatFn != null) {
                final TIntArrayList whats = new TIntArrayList();
                for (final IntFunction what : this.whatFn) {
                    whats.add(what.eval(context));
                }
                for (int i = 0; i < listPivots.size(); ++i) {
                    final int loc = listPivots.getQuick(i);
                    final int contId = context.containerId()[loc];
                    final ContainerState state = context.state().containerStates()[contId];
                    final int what2 = state.what(loc, this.type);
                    if (!whats.contains(what2)) {
                        listPivots.remove(loc);
                        --i;
                    }
                }
            }
            pivots = listPivots.toArray();
        }
        else {
            pivots = new int[] { this.through.eval(context) };
        }
        for (final int locn : pivots) {
            if (locn == -1) {
                return false;
            }
            final int origTo = context.to();
            context.setTo(locn);
            if (!this.condition.eval(context)) {
                context.setTo(origTo);
                return false;
            }
            final int contId2 = 0;
            final Topology graph = context.containers()[0].topology();
            final boolean playOnCell = (this.type != null && this.type == SiteType.Cell) || (this.type == null && context.game().board().defaultSite() != SiteType.Vertex);
            if (playOnCell && locn >= graph.cells().size()) {
                return false;
            }
            if (!playOnCell && locn >= graph.vertices().size()) {
                return false;
            }
            final ContainerDeductionPuzzleState state2 = (ContainerDeductionPuzzleState) context.state().containerStates()[0];
            if (!state2.isResolved(locn, this.type)) {
                return false;
            }
            final TIntArrayList whats2 = new TIntArrayList();
            final int whatLocn = state2.what(locn, this.type);
            if (this.whatFn == null) {
                whats2.add(whatLocn);
            } else {
                for (final IntFunction what3 : this.whatFn) {
                    whats2.add(what3.eval(context));
                }
            }
            if (!whats2.contains(whatLocn)) {
                return false;
            }
            final int from = context.from();
            final int len = this.length.eval(context);
            final boolean exact = this.exactly.eval(context);
            final List<Radial> radials = graph.trajectories().radials(this.type, locn).distinctInDirection(this.dirn);
            for (final Radial radial : radials) {
                int count = whats2.contains(state2.what(locn, this.type)) ? 1 : 0;
                for (int indexPath = 1; indexPath < radial.steps().length; ++indexPath) {
                    final int index = radial.steps()[indexPath].id();
                    if (!state2.isResolved(index, this.type)) {
                        break;
                    }
                    context.setTo(index);
                    if (!whats2.contains(state2.what(index, this.type)) || (this.whatFn != null && index == from) || !this.condition.eval(context)) {
                        break;
                    }
                    ++count;
                    if (!exact && count == len) {
                        context.setTo(origTo);
                        return true;
                    }
                }
                final List<Radial> oppositeRadials = radial.opposites();
                if (oppositeRadials != null) {
                    for (final Radial oppositeRadial : oppositeRadials) {
                        int oppositeCount = count;
                        for (int indexPath2 = 1; indexPath2 < oppositeRadial.steps().length; ++indexPath2) {
                            final int index2 = oppositeRadial.steps()[indexPath2].id();
                            if (!state2.isResolved(index2, this.type)) {
                                break;
                            }
                            context.setTo(index2);
                            if (!whats2.contains(state2.what(index2, this.type)) || (this.whatFn != null && index2 == from) || !this.condition.eval(context)) {
                                break;
                            }
                            ++oppositeCount;
                            if (!exact && oppositeCount == len) {
                                context.setTo(origTo);
                                return true;
                            }
                        }
                        if (oppositeCount == len) {
                            context.setTo(origTo);
                            return true;
                        }
                    }
                } else {
                    if (count == len) {
                        context.setTo(origTo);
                        return true;
                    }
                }
            }
            context.setTo(origTo);
        }
        return false;
    }
    
    private boolean evalStack(final Context context) {
        final int locn = this.through.eval(context);
        if (locn == -1) {
            return false;
        }
        final int contId = context.containerId()[locn];
        final Topology graph = context.containers()[contId].topology();
        final BaseContainerStateStacking state = (BaseContainerStateStacking)context.state().containerStates()[contId];
        final TIntArrayList whats = new TIntArrayList();
        if (this.whatFn == null) {
            whats.add(state.what(locn, this.type));
        }
        else {
            for (final IntFunction what : this.whatFn) {
                whats.add(what.eval(context));
            }
        }
        final int len = this.length.eval(context);
        if (len == 1) {
            return true;
        }
        final boolean exact = this.exactly.eval(context);
        final boolean byLevel = this.byLevelFn.eval(context);
        if (byLevel) {
            if (state.sizeStackCell(locn) >= len) {
                final int level = state.sizeStackCell(locn) - 2;
                int count = 1;
                for (int i = 0; i < len - 1 && whats.contains(state.what(locn, level - i, this.type)); ++i) {
                    ++count;
                    if (!exact && count == len) {
                        return true;
                    }
                }
                if (count == len) {
                    return true;
                }
            }
            final List<Radial> radials = graph.trajectories().radials(this.type, locn).distinctInDirection(this.dirn);
            final int levelOrigin = state.sizeStackCell(locn) - 1;
            for (final Radial radial : radials) {
                final List<Radial> oppositeRadials = radial.opposites();
                int count2 = 0;
                for (int indexPath = 0; indexPath < radial.steps().length; ++indexPath) {
                    final int index = radial.steps()[indexPath].id();
                    if (state.sizeStackCell(index) <= levelOrigin) {
                        break;
                    }
                    if (!whats.contains(state.what(index, levelOrigin, this.type))) {
                        break;
                    }
                    ++count2;
                    if (!exact && count2 == len) {
                        return true;
                    }
                }
                if (oppositeRadials != null) {
                    for (final Radial oppositeRadial : oppositeRadials) {
                        int oppositeCount = count2;
                        for (int indexPath2 = 1; indexPath2 < oppositeRadial.steps().length; ++indexPath2) {
                            final int index2 = oppositeRadial.steps()[indexPath2].id();
                            if (state.sizeStackCell(index2) <= levelOrigin) {
                                break;
                            }
                            if (!whats.contains(state.what(index2, levelOrigin, this.type))) {
                                break;
                            }
                            ++oppositeCount;
                            if (!exact && oppositeCount == len) {
                                return true;
                            }
                        }
                        if (oppositeCount == len) {
                            return true;
                        }
                    }
                }
                else if (count2 == len) {
                    return true;
                }
                count2 = 0;
                int diffLevel = 0;
                for (int indexPath3 = 0; indexPath3 < radial.steps().length; ++indexPath3) {
                    if (levelOrigin - diffLevel != -1) {
                        final int index3 = radial.steps()[indexPath3].id();
                        if (state.sizeStackCell(index3) <= levelOrigin - diffLevel) {
                            break;
                        }
                        if (!whats.contains(state.what(index3, levelOrigin - diffLevel, this.type))) {
                            break;
                        }
                        ++count2;
                        ++diffLevel;
                        if (!exact && count2 == len) {
                            return true;
                        }
                    }
                }
                if (oppositeRadials != null) {
                    diffLevel = 1;
                    for (final Radial oppositeRadial2 : oppositeRadials) {
                        int oppositeCount2 = count2;
                        for (int indexPath4 = 1; indexPath4 < oppositeRadial2.steps().length; ++indexPath4) {
                            final int index4 = oppositeRadial2.steps()[indexPath4].id();
                            if (state.sizeStackCell(index4) <= levelOrigin + diffLevel) {
                                break;
                            }
                            if (!whats.contains(state.what(index4, levelOrigin + diffLevel, this.type))) {
                                break;
                            }
                            ++oppositeCount2;
                            ++diffLevel;
                            if (!exact && oppositeCount2 == len) {
                                return true;
                            }
                        }
                        if (count2 == len) {
                            return true;
                        }
                    }
                }
                else if (count2 == len) {
                    return true;
                }
                count2 = 0;
                diffLevel = 0;
                for (int indexPath3 = 0; indexPath3 < radial.steps().length; ++indexPath3) {
                    final int index3 = radial.steps()[indexPath3].id();
                    if (state.sizeStackCell(index3) <= levelOrigin + diffLevel) {
                        break;
                    }
                    if (!whats.contains(state.what(index3, levelOrigin + diffLevel, this.type))) {
                        break;
                    }
                    ++count2;
                    ++diffLevel;
                    if (!exact && count2 == len) {
                        return true;
                    }
                }
                if (oppositeRadials != null) {
                    diffLevel = 1;
                    for (final Radial oppositeRadial2 : oppositeRadials) {
                        int oppositeCount2 = count2;
                        for (int indexPath4 = 1; indexPath4 < oppositeRadial2.steps().length; ++indexPath4) {
                            if (levelOrigin - diffLevel != -1) {
                                final int index4 = oppositeRadial2.steps()[indexPath4].id();
                                if (state.sizeStackCell(index4) <= levelOrigin - diffLevel) {
                                    break;
                                }
                                if (!whats.contains(state.what(index4, levelOrigin - diffLevel, this.type))) {
                                    break;
                                }
                                ++oppositeCount2;
                                ++diffLevel;
                                if (!exact && oppositeCount2 == len) {
                                    return true;
                                }
                            }
                        }
                        if (oppositeCount2 == len) {
                            return true;
                        }
                    }
                }
                else {
                    if (count2 == len) {
                        return true;
                    }
                }
            }
            return false;
        }
        int count3 = 0;
        for (int sizeStack = state.sizeStack(locn, this.type), level2 = 0; level2 < sizeStack; ++level2) {
            final int whatLevel = state.what(locn, level2, this.type);
            if (whats.contains(whatLevel)) {
                ++count3;
                break;
            }
        }
        if (count3 == 0) {
            return false;
        }
        final List<Radial> radials2 = graph.trajectories().radials(this.type, locn).distinctInDirection(this.dirn);
        for (final Radial radial2 : radials2) {
            count3 = 1;
            for (int indexPath5 = 1; indexPath5 < radial2.steps().length; ++indexPath5) {
                final int index5 = radial2.steps()[indexPath5].id();
                context.setTo(index5);
                boolean whatFound = false;
                for (int sizeStackTo = state.sizeStack(index5, this.type), level3 = 0; level3 < sizeStackTo; ++level3) {
                    final int whatLevel2 = state.what(index5, level3, this.type);
                    if (whats.contains(whatLevel2)) {
                        whatFound = true;
                        break;
                    }
                }
                if (!whatFound || !this.condition.eval(context)) {
                    break;
                }
                ++count3;
                if (!exact && count3 == len) {
                    return true;
                }
            }
            final List<Radial> oppositeRadials2 = radial2.opposites();
            if (oppositeRadials2 != null) {
                for (final Radial oppositeRadial : oppositeRadials2) {
                    int oppositeCount = count3;
                    for (int indexPath2 = 1; indexPath2 < oppositeRadial.steps().length; ++indexPath2) {
                        final int index2 = oppositeRadial.steps()[indexPath2].id();
                        boolean whatFound2 = false;
                        for (int sizeStackTo2 = state.sizeStack(index2, this.type), level4 = 0; level4 < sizeStackTo2; ++level4) {
                            final int whatLevel3 = state.what(index2, level4, this.type);
                            if (whats.contains(whatLevel3)) {
                                whatFound2 = true;
                                break;
                            }
                        }
                        context.setTo(index2);
                        if (!whatFound2 || !this.condition.eval(context)) {
                            break;
                        }
                        ++oppositeCount;
                        if (!exact && oppositeCount == len) {
                            return true;
                        }
                    }
                    if (oppositeCount == len) {
                        return true;
                    }
                }
            }
            else {
                if (count3 == len) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        String str = "";
        str = str + "Line(" + this.length + ", " + this.dirn + ", " + this.through + ", " + this.exactly + ")";
        return str;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = this.length.gameFlags(game);
        if (this.type != null && (this.type == SiteType.Edge || this.type == SiteType.Vertex)) {
            flags |= 0x800000L;
        }
        if (this.exactly != null) {
            flags |= this.exactly.gameFlags(game);
        }
        if (this.through != null) {
            flags |= this.through.gameFlags(game);
        }
        if (this.whatFn != null) {
            for (final IntFunction what : this.whatFn) {
                flags |= what.gameFlags(game);
            }
        }
        if (this.whoFn != null) {
            flags |= this.whoFn.gameFlags(game);
        }
        flags |= this.condition.gameFlags(game);
        flags |= this.byLevelFn.gameFlags(game);
        if (this.throughAny != null) {
            flags |= this.throughAny.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.length.preprocess(game);
        if (this.exactly != null) {
            this.exactly.preprocess(game);
        }
        if (this.through != null) {
            this.through.preprocess(game);
        }
        if (this.whatFn != null) {
            for (final IntFunction what : this.whatFn) {
                what.preprocess(game);
            }
        }
        if (this.whoFn != null) {
            this.whoFn.preprocess(game);
        }
        this.byLevelFn.preprocess(game);
        this.condition.preprocess(game);
        if (this.throughAny != null) {
            this.throughAny.preprocess(game);
        }
    }
    
    public IntFunction length() {
        return this.length;
    }
    
    @Override
    public List<Location> satisfyingSites(final Context context) {
        if (!this.eval(context)) {
            return new ArrayList<>();
        }
        final List<Location> winningSites = new ArrayList<>();
        final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
        int[] pivots;
        if (this.throughAny != null) {
            final TIntArrayList listPivots = new TIntArrayList(this.throughAny.eval(context).sites());
            if (this.whatFn != null) {
                final TIntArrayList whats = new TIntArrayList();
                for (final IntFunction what : this.whatFn) {
                    whats.add(what.eval(context));
                }
                for (int i = listPivots.size() - 1; i >= 0; --i) {
                    final int loc = listPivots.getQuick(i);
                    final int contId = context.containerId()[loc];
                    final ContainerState state = context.state().containerStates()[contId];
                    final int what2 = state.what(loc, this.type);
                    if (!whats.contains(what2)) {
                        listPivots.removeAt(i);
                    }
                }
            }
            pivots = listPivots.toArray();
        }
        else {
            pivots = new int[] { this.through.eval(context) };
        }
        final Topology graph = context.topology();
        final boolean playOnCell = (this.type != null && this.type == SiteType.Cell) || (this.type == null && context.game().board().defaultSite() != SiteType.Vertex);
        for (final int locn : pivots) {
            if (locn == -1) {
                return new ArrayList<>();
            }
            final int origTo = context.to();
            context.setTo(locn);
            if (!this.condition.eval(context)) {
                context.setTo(origTo);
                return new ArrayList<>();
            }
            if (playOnCell && locn >= graph.cells().size()) {
                return new ArrayList<>();
            }
            if (!playOnCell && locn >= graph.vertices().size()) {
                return new ArrayList<>();
            }
            final TopologyElement vertexLoc = playOnCell ? graph.cells().get(locn) : graph.vertices().get(locn);
            final ContainerState state2 = context.state().containerStates()[context.containerId()[vertexLoc.index()]];
            final TIntArrayList whats2 = new TIntArrayList();
            final int whatLocn = state2.what(locn, this.type);
            if (this.whatFn == null) {
                if (this.whoFn == null) {
                    whats2.add(whatLocn);
                } else if (this.whoFn != null) {
                    final int who = this.whoFn.eval(context);
                    for (int j = 1; j < context.components().length; ++j) {
                        final Component component = context.components()[j];
                        if (component.owner() == who) {
                            whats2.add(component.index());
                        }
                    }
                }
            } else {
                for (final IntFunction what3 : this.whatFn) {
                    whats2.add(what3.eval(context));
                }
            }
            if (whats2.contains(whatLocn)) {
                final int len = this.length.eval(context);
                final boolean exact = this.exactly.eval(context);
                final List<Radial> radials = graph.trajectories().radials(this.type, locn).distinctInDirection(this.dirn);
                for (final Radial radial : radials) {
                    winningSites.clear();
                    winningSites.add(new FullLocation(locn, 0, realType));
                    int count = whats2.contains(whatLocn) ? 1 : 0;
                    for (int indexPath = 1; indexPath < radial.steps().length; ++indexPath) {
                        final int index = radial.steps()[indexPath].id();
                        context.setTo(index);
                        if (!whats2.contains(state2.what(index, this.type)) || !this.condition.eval(context)) {
                            break;
                        }
                        ++count;
                        winningSites.add(new FullLocation(index, 0, realType));
                        if (!exact && count == len) {
                            context.setTo(origTo);
                            return winningSites;
                        }
                    }
                    final List<Radial> oppositeRadials = radial.opposites();
                    if (oppositeRadials != null) {
                        for (final Radial oppositeRadial : oppositeRadials) {
                            int oppositeCount = count;
                            for (int indexPath2 = 1; indexPath2 < oppositeRadial.steps().length; ++indexPath2) {
                                final int index2 = oppositeRadial.steps()[indexPath2].id();
                                context.setTo(index2);
                                if (!whats2.contains(state2.what(index2, this.type)) || !this.condition.eval(context)) {
                                    break;
                                }
                                winningSites.add(new FullLocation(index2, 0, realType));
                                ++oppositeCount;
                                if (!exact && oppositeCount == len) {
                                    context.setTo(origTo);
                                    return winningSites;
                                }
                            }
                            if (oppositeCount == len) {
                                context.setTo(origTo);
                                return winningSites;
                            }
                        }
                    } else {
                        if (count == len) {
                            context.setTo(origTo);
                            return winningSites;
                        }
                    }
                }
                context.setTo(origTo);
            }
        }
        return new ArrayList<>();
    }
}
