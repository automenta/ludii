// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.booleans.is.player.IsEnemy;
import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.functions.ints.IntFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.graph.Step;
import game.util.moves.Between;
import game.util.moves.From;
import gnu.trove.list.array.TIntArrayList;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.MoveUtilities;
import util.state.containerState.ContainerState;

import java.util.List;

public final class Enclose extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startFn;
    private final DirectionsFunction dirnChoice;
    private final BooleanFunction targetRule;
    private final Moves effect;
    private SiteType type;
    
    public Enclose(@Opt final SiteType type, @Opt final From from, @Opt final Direction directions, @Opt final Between between, @Opt final Then then) {
        super(then);
        this.startFn = ((from == null) ? new game.functions.ints.context.From(null) : from.loc());
        this.dirnChoice = ((directions != null) ? directions.directionsFunctions() : new Directions(AbsoluteDirection.Adjacent, null));
        this.targetRule = ((between == null || between.condition() == null) ? new IsEnemy(game.functions.ints.context.Between.instance(), null) : between.condition());
        this.effect = ((between == null || between.effect() == null) ? new Remove(null, game.functions.ints.context.Between.instance(), null, null, null, null) : between.effect());
        this.type = type;
    }
    
    @Override
    public final Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int from = this.startFn.eval(context);
        final int originBetween = context.between();
        final int originTo = context.to();
        if (from < 0) {
            return moves;
        }
        final Topology topology = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        if (from >= topology.getGraphElements(realType).size()) {
            return moves;
        }
        final ContainerState cs = context.containerState(0);
        final int what = cs.what(from, realType);
        if (what <= 0) {
            return moves;
        }
        final TIntArrayList aroundTarget = new TIntArrayList();
        final TopologyElement element = topology.getGraphElements(this.type).get(from);
        final List<AbsoluteDirection> directionsElement = this.dirnChoice.convertToAbsolute(this.type, element, null, null, null, context);
        for (final AbsoluteDirection direction : directionsElement) {
            final List<Step> steps = topology.trajectories().steps(this.type, element.index(), this.type, direction);
            for (final Step step : steps) {
                final int between = step.to().id();
                if (!aroundTarget.contains(between) && this.isTarget(context, between)) {
                    aroundTarget.add(between);
                }
            }
        }
        final TIntArrayList sitesChecked = new TIntArrayList();
        for (int indexEnclosed = 0; indexEnclosed < aroundTarget.size(); ++indexEnclosed) {
            final int target = aroundTarget.get(indexEnclosed);
            if (!sitesChecked.contains(target)) {
                final TIntArrayList enclosedGroup = new TIntArrayList();
                enclosedGroup.add(target);
                final TIntArrayList sitesExplored = new TIntArrayList();
                int i = 0;
                while (sitesExplored.size() != enclosedGroup.size()) {
                    final int site = enclosedGroup.get(i);
                    final TopologyElement siteElement = topology.getGraphElements(this.type).get(site);
                    final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(this.type, siteElement, null, null, null, context);
                    for (final AbsoluteDirection direction2 : directions) {
                        final List<Step> steps2 = topology.trajectories().steps(this.type, siteElement.index(), this.type, direction2);
                        for (final Step step2 : steps2) {
                            final int between2 = step2.to().id();
                            if (enclosedGroup.contains(between2)) {
                                continue;
                            }
                            if (!this.isTarget(context, between2)) {
                                continue;
                            }
                            enclosedGroup.add(between2);
                        }
                    }
                    sitesExplored.add(site);
                    ++i;
                }
                sitesChecked.addAll(sitesExplored);
                final TIntArrayList liberties = new TIntArrayList();
                for (int indexGroup = 0; indexGroup < enclosedGroup.size(); ++indexGroup) {
                    final int siteGroup = enclosedGroup.get(indexGroup);
                    final TopologyElement elem = topology.getGraphElements(this.type).get(siteGroup);
                    final List<AbsoluteDirection> directionsElem = this.dirnChoice.convertToAbsolute(this.type, elem, null, null, null, context);
                    for (final AbsoluteDirection direction3 : directionsElem) {
                        final List<Step> steps3 = topology.trajectories().steps(this.type, elem.index(), this.type, direction3);
                        for (final Step step3 : steps3) {
                            final int to = step3.to().id();
                            if (!liberties.contains(to) && cs.what(to, this.type) == 0) {
                                liberties.add(to);
                            }
                        }
                    }
                }
                if (liberties.isEmpty()) {
                    for (int indexBetween = 0; indexBetween < enclosedGroup.size(); ++indexBetween) {
                        final int between3 = enclosedGroup.get(indexBetween);
                        context.setBetween(between3);
                        MoveUtilities.chainRuleCrossProduct(context, moves, this.effect, null, false);
                    }
                }
            }
        }
        context.setTo(originTo);
        context.setBetween(originBetween);
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    private boolean isTarget(final Context context, final int location) {
        context.setBetween(location);
        return this.targetRule.eval(context);
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = super.gameFlags(game);
        flags |= SiteType.stateFlags(this.type);
        return this.startFn.gameFlags(game) | this.targetRule.gameFlags(game) | this.effect.gameFlags(game) | flags;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.startFn.preprocess(game);
        this.targetRule.preprocess(game);
        this.effect.preprocess(game);
        this.type = SiteType.use(this.type, game);
    }
    
    @Override
    public String toEnglish() {
        return "Enclose";
    }
}
