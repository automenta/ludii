// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tools.buttons;

import app.display.dialogs.SettingsDialog;
import app.display.views.tools.ToolButton;
import util.SettingsVC;
import util.locations.FullLocation;

import java.awt.*;

public class ButtonSettings extends ToolButton
{
    public ButtonSettings(final int cx, final int cy, final int sx, final int sy) {
        super("Settings", cx, cy, sx, sy);
        this.tooltipMessage = "Preferences";
    }
    
    @Override
    public void draw(final Graphics2D g2d) {
        g2d.setColor(this.getButtonColour());
        final int d = 10;
        final int dd = 7;
        g2d.drawLine(this.cx - 10, this.cy, this.cx + 10, this.cy);
        g2d.drawLine(this.cx, this.cy - 10, this.cx, this.cy + 10);
        g2d.drawLine(this.cx - 7, this.cy - 7, this.cx + 7, this.cy + 7);
        g2d.drawLine(this.cx - 7, this.cy + 7, this.cx + 7, this.cy - 7);
        final int r = 7;
        g2d.fillArc(this.cx - 7, this.cy - 7, 15, 15, 0, 360);
        final int rr = 3;
        g2d.setColor(Color.white);
        g2d.fillArc(this.cx - 3, this.cy - 3, 7, 7, 0, 360);
    }
    
    @Override
    public void press() {
        SettingsDialog.createAndShowGUI(0);
        SettingsVC.selectedLocation = new FullLocation(-1);
    }
}
