// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tools;

import app.display.MainWindow;
import game.rules.play.moves.Moves;
import manager.utils.ContextSnapshot;
import util.Context;
import util.Move;
import util.action.Action;

import java.awt.*;
import java.util.ArrayList;

public abstract class ToolButton
{
    protected String name;
    protected int cx;
    protected int cy;
    protected int sx;
    protected int sy;
    protected static final Color buttonColour;
    protected static final Color rolloverButtonColour;
    protected static final Color invalidButtonColour;
    protected Rectangle rect;
    protected boolean mouseOver;
    protected String tooltipMessage;
    
    public ToolButton(final String name, final int cx, final int cy, final int sx, final int sy) {
        this.name = "?";
        this.rect = new Rectangle();
        this.mouseOver = false;
        this.tooltipMessage = "Default Message";
        this.name = name;
        this.cx = cx;
        this.cy = cy;
        this.sx = sx;
        this.sy = sy;
        this.rect.x = cx - sx / 2;
        this.rect.y = cy - sy / 2;
        this.rect.width = sx;
        this.rect.height = sy;
    }
    
    public String name() {
        return this.name;
    }
    
    public void setPosition(final int x, final int y) {
        this.cx = x;
        this.cy = y;
        this.rect.x = this.cx - this.sx / 2;
        this.rect.y = this.cy - this.sy / 2;
    }
    
    public abstract void draw(final Graphics2D p0);
    
    public abstract void press();
    
    public boolean hit(final int x, final int y) {
        return x >= this.cx - this.sx / 2 && x <= this.cx + this.sx / 2 && y >= this.cy - this.sy / 2 && y <= this.cy + this.sy / 2;
    }
    
    public void setMouseOver(final boolean b) {
        this.mouseOver = b;
    }
    
    public boolean mouseOver() {
        return this.mouseOver;
    }
    
    public Rectangle rect() {
        return this.rect;
    }
    
    public String tooltipMessage() {
        return this.tooltipMessage;
    }
    
    protected boolean isEnabled() {
        return true;
    }
    
    protected void showPossibleMovesTemporaryMessage() {
        final Context context = ContextSnapshot.getContext();
        final Moves legal = context.game().moves(context);
        final ArrayList<String> allOtherMoveDescriptions = new ArrayList<>();
        for (final Move move : legal.moves()) {
            int i = 0;
            while (i < move.actions().size()) {
                if (move.actions().get(i).isDecision()) {
                    final Action decisionAction = move.actions().get(i);
                    final String desc = decisionAction.getDescription();
                    if (!allOtherMoveDescriptions.contains(desc)) {
                        allOtherMoveDescriptions.add(desc);
                        break;
                    }
                    break;
                }
                else {
                    ++i;
                }
            }
        }
        if (allOtherMoveDescriptions.size() > 0) {
            String tempMessageString = "You can ";
            for (final String s : allOtherMoveDescriptions) {
                tempMessageString = tempMessageString + s + " or ";
            }
            tempMessageString = tempMessageString.substring(0, tempMessageString.length() - 4);
            tempMessageString += ".";
            MainWindow.setTemporaryMessage(tempMessageString);
        }
    }
    
    protected Color getButtonColour() {
        if (!this.isEnabled()) {
            return ToolButton.invalidButtonColour;
        }
        if (this.mouseOver) {
            return ToolButton.rolloverButtonColour;
        }
        return ToolButton.buttonColour;
    }
    
    static {
        buttonColour = new Color(50, 50, 50);
        rolloverButtonColour = new Color(127, 127, 127);
        invalidButtonColour = new Color(220, 220, 220);
    }
}
