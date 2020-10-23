// 
// Decompiled by Procyon v0.5.36
// 

package util.action.move;

import game.Game;
import game.equipment.component.Component;
import game.equipment.container.board.Track;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import main.SettingsGeneral;
import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;
import util.state.containerState.ContainerState;
import util.state.onTrack.OnTrackIndices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ActionAdd extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private SiteType type;
    private final int to;
    private final int what;
    private final int count;
    private final int state;
    private final int rotation;
    private final boolean onStack;
    private int level;
    private final boolean[] invisible;
    private final boolean[] masked;
    
    public ActionAdd(final SiteType type, final int to, final int what, final int count, final int state, final int rotation, final boolean[] invisible, final boolean[] masked, final Boolean onStacking) {
        this.level = -1;
        this.to = to;
        this.what = what;
        this.count = count;
        this.state = state;
        this.rotation = rotation;
        this.onStack = (onStacking != null && onStacking);
        this.invisible = invisible;
        this.masked = masked;
        this.type = type;
    }
    
    public ActionAdd(final String detailedString) {
        this.level = -1;
        assert detailedString.startsWith("[Add:");
        final String strType = Action.extractData(detailedString, "type");
        this.type = (strType.isEmpty() ? null : SiteType.valueOf(strType));
        final String strTo = Action.extractData(detailedString, "to");
        this.to = Integer.parseInt(strTo);
        final String strLevel = Action.extractData(detailedString, "level");
        this.level = (strLevel.isEmpty() ? -1 : Integer.parseInt(strLevel));
        final String strWhat = Action.extractData(detailedString, "what");
        this.what = Integer.parseInt(strWhat);
        final String strCount = Action.extractData(detailedString, "count");
        this.count = (strCount.isEmpty() ? 1 : Integer.parseInt(strCount));
        final String strState = Action.extractData(detailedString, "state");
        this.state = (strState.isEmpty() ? -1 : Integer.parseInt(strState));
        final String strRotation = Action.extractData(detailedString, "rotation");
        this.rotation = (strRotation.isEmpty() ? -1 : Integer.parseInt(strRotation));
        final String strStack = Action.extractData(detailedString, "stack");
        this.onStack = (!strStack.isEmpty() && Boolean.parseBoolean(strStack));
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
        String strInvisible = Action.extractData(detailedString, "invisible");
        if (strInvisible.isEmpty()) {
            this.invisible = null;
        }
        else {
            final List<Boolean> bools = new ArrayList<>();
            while (strInvisible.contains(",")) {
                bools.add(Boolean.valueOf(strInvisible.substring(0, strInvisible.indexOf(44))));
                strInvisible = strInvisible.substring(strInvisible.indexOf(44) + 1);
            }
            bools.add(Boolean.valueOf(strInvisible));
            this.invisible = new boolean[bools.size()];
            for (int i = 0; i < bools.size(); ++i) {
                this.invisible[i] = bools.get(i);
            }
        }
        String strMasked = Action.extractData(detailedString, "masked");
        if (strMasked.isEmpty()) {
            this.masked = null;
        }
        else {
            final List<Boolean> bools2 = new ArrayList<>();
            while (strMasked.contains(",")) {
                bools2.add(Boolean.valueOf(strMasked.substring(0, strMasked.indexOf(44))));
                strMasked = strMasked.substring(strMasked.indexOf(44) + 1);
            }
            bools2.add(Boolean.valueOf(strMasked));
            this.masked = new boolean[bools2.size()];
            for (int j = 0; j < bools2.size(); ++j) {
                this.masked[j] = bools2.get(j);
            }
        }
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        if (this.to < 0) {
            return this;
        }
        this.type = ((this.type == null) ? context.board().defaultSite() : this.type);
        if (this.to >= context.board().topology().getGraphElements(this.type).size()) {
            this.type = SiteType.Cell;
        }
        final Game game = context.game();
        final int contID = (this.type == SiteType.Cell) ? context.containerId()[this.to] : 0;
        final ContainerState cs = context.state().containerStates()[contID];
        final int who = (this.what < 1) ? 0 : context.components()[this.what].owner();
        final boolean requiresStack = game.isStacking();
        if (requiresStack) {
            this.applyStack(context, cs);
        }
        if (this.invisible != null) {
            for (int i = 1; i <= this.invisible.length; ++i) {
                if (this.invisible[i - 1]) {
                    cs.setInvisibleCell(context.state(), this.to, i);
                }
                else {
                    cs.setVisibleCell(context.state(), this.to, i);
                }
            }
        }
        if (this.masked != null) {
            for (int i = 1; i <= this.masked.length; ++i) {
                if (this.masked[i - 1]) {
                    cs.setMaskedCell(context.state(), this.to, i);
                }
                else {
                    cs.setVisibleCell(context.state(), this.to, i);
                }
            }
        }
        int whatt = 0;
        whatt = cs.what(this.to, this.type);
        if (whatt == 0) {
            cs.setSite(context.state(), this.to, who, this.what, this.count, this.state, this.rotation, context.game().hasDominoes() ? 1 : -1, this.type);
            Component piece = null;
            if (this.what != 0) {
                piece = context.components()[this.what];
                final int owner = piece.owner();
                context.state().owned().add(owner, this.what, this.to, this.type);
                if (piece.isDomino()) {
                    context.state().remainingDominoes().remove(piece.index());
                }
            }
            this.applyLargePiece(context, piece, cs);
        }
        else {
            final int oldCount = cs.count(this.to, this.type);
            cs.setSite(context.state(), this.to, -1, -1, game.requiresCount() ? (oldCount + this.count) : 1, this.state, this.rotation, -1, this.type);
        }
        if (this.to >= context.board().numSites() || this.what < game.players().size()) {}
        this.updateTrackIndices(context);
        return this;
    }
    
    public void applyStack(final Context context, final ContainerState cs) {
        final int who = (this.what < 1) ? 0 : context.components()[this.what].owner();
        this.level = cs.sizeStack(this.to, this.type);
        Component piece = null;
        if (this.state != -1 || this.rotation != -1) {
            cs.addItemGeneric(context.state(), this.to, this.what, who, this.state, this.rotation, context.game(), this.type);
        }
        else if (this.invisible == null && this.masked == null) {
            cs.addItemGeneric(context.state(), this.to, this.what, who, context.game(), this.type);
        }
        else if (this.invisible != null) {
            cs.addItemGeneric(context.state(), this.to, this.what, who, context.game(), this.invisible, false, this.type);
        }
        else {
            cs.addItemGeneric(context.state(), this.to, this.what, who, context.game(), this.masked, true, this.type);
        }
        cs.removeFromEmpty(this.to, this.type);
        if (this.what != 0) {
            piece = context.components()[this.what];
            final int owner = piece.owner();
            context.state().owned().add(owner, this.what, this.to, cs.sizeStack(this.to, this.type) - 1, this.type);
        }
        this.updateTrackIndices(context);
    }
    
    public void applyLargePiece(final Context context, final Component piece, final ContainerState cs) {
        if (piece != null && piece.isLargePiece() && this.to < context.containers()[0].numSites()) {
            final Component largePiece = piece;
            final TIntArrayList locs = largePiece.locs(context, this.to, this.state, context.topology());
            for (int i = 0; i < locs.size(); ++i) {
                cs.removeFromEmpty(locs.getQuick(i), SiteType.Cell);
                cs.setCount(context.state(), locs.getQuick(i), 1);
            }
            if (largePiece.isDomino()) {
                for (int i = 0; i < 4; ++i) {
                    cs.setValue(context.state(), locs.getQuick(i), largePiece.getValue());
                    cs.setPlayable(context.state(), locs.getQuick(i), false);
                }
                for (int i = 4; i < 8; ++i) {
                    cs.setValue(context.state(), locs.getQuick(i), largePiece.getValue2());
                    cs.setPlayable(context.state(), locs.getQuick(i), false);
                }
            }
        }
    }
    
    public void updateTrackIndices(final Context context) {
        final OnTrackIndices onTrackIndices = context.state().onTrackIndices();
        if (onTrackIndices != null) {
            for (final Track track : context.board().tracks()) {
                final int trackIdx = track.trackIdx();
                final TIntArrayList indices = onTrackIndices.locToIndex(trackIdx, this.to);
                if (!indices.isEmpty()) {
                    onTrackIndices.add(trackIdx, this.what, this.count, indices.getQuick(0));
                }
            }
        }
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Add:");
        if (this.type != null || (context != null && this.type != context.board().defaultSite())) {
            sb.append("type=" + this.type);
            sb.append(",to=" + this.to);
        }
        else {
            sb.append("to=" + this.to);
        }
        if (this.level != -1) {
            sb.append(",level=" + this.level);
        }
        sb.append(",what=" + this.what);
        if (this.count > 1) {
            sb.append(",count=" + this.count);
        }
        if (this.state != -1) {
            sb.append(",state=" + this.state);
        }
        if (this.rotation != -1) {
            sb.append(",rotation=" + this.rotation);
        }
        if (this.onStack) {
            sb.append(",stack=" + this.onStack);
        }
        if (this.decision) {
            sb.append(",decision=" + this.decision);
        }
        if (this.invisible != null) {
            sb.append(",invisible=");
            for (int i = 0; i < this.invisible.length; ++i) {
                sb.append(this.invisible[i]);
                if (i < this.invisible.length - 1) {
                    sb.append(",");
                }
            }
        }
        if (this.masked != null) {
            sb.append(",masked=");
            for (int i = 0; i < this.masked.length; ++i) {
                sb.append(this.masked[i]);
                if (i < this.masked.length - 1) {
                    sb.append(",");
                }
            }
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.count;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + Arrays.hashCode(this.invisible);
        result = 31 * result + Arrays.hashCode(this.masked);
        result = 31 * result + this.to;
        result = 31 * result + (this.onStack ? 1231 : 1237);
        result = 31 * result + this.state;
        result = 31 * result + this.rotation;
        result = 31 * result + this.what;
        result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionAdd)) {
            return false;
        }
        final ActionAdd other = (ActionAdd)obj;
        return this.count == other.count && this.decision == other.decision && Arrays.equals(this.invisible, other.invisible) && Arrays.equals(this.masked, other.masked) && this.to == other.to && this.onStack == other.onStack && this.state == other.state && this.rotation == other.rotation && this.what == other.what && this.type == other.type;
    }
    
    @Override
    public String getDescription() {
        return "Add";
    }
    
    @Override
    public String toTurnFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        String newTo = String.valueOf(this.to);
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.type == SiteType.Cell || (this.type == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.to] : 0;
            if (cid == 0) {
                final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.to).label();
            }
        }
        if (this.type != null && this.type != context.board().defaultSite()) {
            sb.append(this.type + " " + newTo);
        }
        else {
            sb.append(newTo);
        }
        if (this.level != -1) {
            sb.append("/" + this.level);
        }
        if (this.onStack) {
            sb.append("^");
        }
        else {
            sb.append("+");
        }
        if (this.what > 0 && this.what < context.components().length) {
            sb.append(context.components()[this.what].name());
            if (this.count > 1) {
                sb.append("x" + this.count);
            }
        }
        if (this.state != -1) {
            sb.append("=" + this.state);
        }
        if (this.rotation != -1) {
            sb.append(" r" + this.rotation);
        }
        if (this.invisible != null) {
            sb.append(" invisible:");
            for (int i = 0; i < this.invisible.length; ++i) {
                if (this.invisible[i]) {
                    sb.append("P" + (i + 1));
                }
            }
        }
        if (this.masked != null) {
            sb.append(" masked:");
            for (int i = 0; i < this.masked.length; ++i) {
                if (this.masked[i]) {
                    sb.append("P" + (i + 1));
                }
            }
        }
        return sb.toString();
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(Add ");
        if (this.what > 0 && this.what < context.components().length) {
            sb.append(context.components()[this.what].name());
            if (this.count > 1) {
                sb.append("x" + this.count);
            }
        }
        String newTo = String.valueOf(this.to);
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.type == SiteType.Cell || (this.type == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.to] : 0;
            if (cid == 0) {
                final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.to).label();
            }
        }
        if (this.type != null && this.type != context.board().defaultSite()) {
            sb.append(" to " + this.type + " " + newTo);
        }
        else {
            sb.append(" to " + newTo);
        }
        if (this.level != -1) {
            sb.append("/" + this.level);
        }
        if (this.state != -1) {
            sb.append(" state=" + this.state);
        }
        if (this.rotation != -1) {
            sb.append(" rotation=" + this.rotation);
        }
        if (this.onStack) {
            sb.append(" on stack");
        }
        if (this.invisible != null) {
            sb.append(",invisible={");
            for (int i = 0; i < this.invisible.length; ++i) {
                if (this.invisible[i]) {
                    sb.append("P" + (i + 1));
                }
            }
            sb.append('}');
        }
        if (this.masked != null) {
            sb.append(",masked={");
            for (int i = 0; i < this.masked.length; ++i) {
                if (this.masked[i]) {
                    sb.append("P" + (i + 1));
                }
            }
            sb.append('}');
        }
        sb.append(')');
        return sb.toString();
    }
    
    @Override
    public SiteType fromType() {
        return this.type;
    }
    
    @Override
    public SiteType toType() {
        return this.type;
    }
    
    @Override
    public int from() {
        return this.to;
    }
    
    @Override
    public int to() {
        return this.to;
    }
    
    @Override
    public int levelFrom() {
        return (this.level == -1) ? 0 : this.level;
    }
    
    @Override
    public int levelTo() {
        return (this.level == -1) ? 0 : this.level;
    }
    
    @Override
    public int what() {
        return this.what;
    }
    
    @Override
    public int state() {
        return this.state;
    }
    
    @Override
    public int rotation() {
        return this.rotation;
    }
    
    @Override
    public int count() {
        return this.count;
    }
    
    @Override
    public boolean[] hidden() {
        return this.invisible;
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.Add;
    }
    
    public void setLevel(final int level) {
        this.level = level;
    }
}
