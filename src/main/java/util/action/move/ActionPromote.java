// 
// Decompiled by Procyon v0.5.36
// 

package util.action.move;

import game.equipment.component.Component;
import game.types.board.SiteType;
import main.SettingsGeneral;
import util.Context;
import util.action.Action;
import util.action.ActionType;
import util.action.BaseAction;
import util.state.containerState.ContainerState;

public final class ActionPromote extends BaseAction
{
    private static final long serialVersionUID = 1L;
    private final int to;
    private int level;
    private final int newWhat;
    private SiteType type;
    
    public ActionPromote(final SiteType type, final int to, final int what) {
        this.level = -1;
        this.to = to;
        this.newWhat = what;
        this.type = type;
    }
    
    public ActionPromote(final String detailedString) {
        this.level = -1;
        assert detailedString.startsWith("[Promote:");
        final String strType = Action.extractData(detailedString, "type");
        this.type = (strType.isEmpty() ? null : SiteType.valueOf(strType));
        final String strTo = Action.extractData(detailedString, "to");
        this.to = Integer.parseInt(strTo);
        final String strLevel = Action.extractData(detailedString, "level");
        this.level = (strLevel.isEmpty() ? -1 : Integer.parseInt(strLevel));
        final String strWhat = Action.extractData(detailedString, "what");
        this.newWhat = Integer.parseInt(strWhat);
        final String strDecision = Action.extractData(detailedString, "decision");
        this.decision = (!strDecision.isEmpty() && Boolean.parseBoolean(strDecision));
    }
    
    @Override
    public Action apply(final Context context, final boolean store) {
        this.type = ((this.type == null) ? context.board().defaultSite() : this.type);
        final int contID = (this.type == SiteType.Cell) ? context.containerId()[this.to] : 0;
        final ContainerState cs = context.state().containerStates()[contID];
        if ((context.game().gameFlags() & 0x10L) == 0x0L) {
            final int what = cs.what(this.to, this.type);
            Component piece = null;
            if (what != 0) {
                piece = context.components()[what];
                final int owner = piece.owner();
                if (owner != 0) {
                    context.state().owned().remove(owner, what, this.to);
                }
            }
            cs.remove(context.state(), this.to, this.type);
            final int who = (what < 1) ? 0 : context.components()[this.newWhat].owner();
            cs.setSite(context.state(), this.to, who, this.newWhat, 1, -1, -1, -1, this.type);
            if (this.newWhat != 0) {
                piece = context.components()[this.newWhat];
                final int owner2 = piece.owner();
                if (owner2 != 0) {
                    context.state().owned().add(owner2, this.newWhat, this.to, this.type);
                }
            }
        }
        else {
            Component piece2 = null;
            final int oldWhat = (this.level == -1) ? cs.what(this.to, this.type) : cs.what(this.to, this.level, this.type);
            piece2 = context.components()[oldWhat];
            final int previousOwner = piece2.owner();
            if (this.level == -1) {
                cs.remove(context.state(), this.to, this.type);
            }
            else {
                cs.remove(context.state(), this.to, this.level);
            }
            final int sizeStack = cs.sizeStack(this.to, this.type);
            if (cs.sizeStack(this.to, this.type) == 0) {
                cs.addToEmpty(this.to);
            }
            if (cs.sizeStack(this.to, this.type) != 0 && previousOwner != 0) {
                if (this.level == -1) {
                    context.state().owned().remove(previousOwner, oldWhat, this.to, sizeStack);
                }
                else {
                    context.state().owned().remove(previousOwner, oldWhat, this.to, this.level);
                }
            }
            final int who2 = (this.newWhat < 1) ? 0 : context.components()[this.newWhat].owner();
            cs.addItemGeneric(context.state(), this.to, this.newWhat, who2, context.game(), this.type);
            cs.removeFromEmpty(this.to);
            if (this.newWhat != 0) {
                piece2 = context.components()[this.newWhat];
                final int owner3 = piece2.owner();
                if (owner3 != 0) {
                    if (this.level == -1) {
                        context.state().owned().add(owner3, this.newWhat, this.to, sizeStack, this.type);
                    }
                    else {
                        context.state().owned().add(owner3, this.newWhat, this.to, this.level, this.type);
                    }
                }
            }
        }
        return this;
    }
    
    @Override
    public String toTrialFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Promote:");
        if (this.type != null || (context != null && this.type != context.board().defaultSite())) {
            sb.append("type=").append(this.type);
            sb.append(",to=").append(this.to);
        }
        else {
            sb.append("to=").append(this.to);
        }
        if (this.level != -1) {
            sb.append(",level=").append(this.level);
        }
        sb.append(",what=").append(this.newWhat);
        if (this.decision) {
            sb.append(",decision=").append(this.decision);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.decision ? 1231 : 1237);
        result = 31 * result + this.level;
        result = 31 * result + this.to;
        result = 31 * result + this.newWhat;
        result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionPromote)) {
            return false;
        }
        final ActionPromote other = (ActionPromote)obj;
        return this.decision == other.decision && this.level == other.level && this.to == other.to && this.newWhat == other.newWhat && this.type == other.type;
    }
    
    @Override
    public String getDescription() {
        return "Promote";
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
            sb.append(this.type).append(" ").append(newTo);
        }
        else {
            sb.append(newTo);
        }
        if (this.level != -1) {
            sb.append("/").append(this.level);
        }
        if (this.newWhat > 0 && this.newWhat < context.components().length) {
            sb.append(" => ").append(context.components()[this.newWhat].name());
        }
        return sb.toString();
    }
    
    @Override
    public String toMoveFormat(final Context context) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(Promote ");
        String newTo = String.valueOf(this.to);
        if (SettingsGeneral.isMoveCoord()) {
            final int cid = (this.type == SiteType.Cell || (this.type == null && context.board().defaultSite() == SiteType.Cell)) ? context.containerId()[this.to] : 0;
            if (cid == 0) {
                final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
                newTo = context.game().equipment().containers()[cid].topology().getGraphElements(realType).get(this.to).label();
            }
        }
        if (this.type != null && this.type != context.board().defaultSite()) {
            sb.append(this.type).append(" ").append(newTo);
        }
        else {
            sb.append(newTo);
        }
        if (this.level != -1) {
            sb.append("/").append(this.level);
        }
        if (this.newWhat > 0 && this.newWhat < context.components().length) {
            sb.append(" to ").append(context.components()[this.newWhat].name());
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
    public int count() {
        return 1;
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
        return this.newWhat;
    }
    
    @Override
    public ActionType actionType() {
        return ActionType.Promote;
    }
    
    public void setLevel(final int level) {
        this.level = level;
    }
}
