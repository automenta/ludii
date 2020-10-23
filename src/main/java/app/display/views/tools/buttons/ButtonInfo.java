// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tools.buttons;

import app.display.dialogs.AboutDialog;
import app.display.views.tools.ToolButton;
import util.SettingsVC;
import util.locations.FullLocation;

import java.awt.*;

public class ButtonInfo extends ToolButton
{
    public ButtonInfo(final int cx, final int cy, final int sx, final int sy) {
        super("Info", cx, cy, sx, sy);
        this.tooltipMessage = "Info";
    }
    
    @Override
    public void draw(final Graphics2D g2d) {
        g2d.setColor(this.getButtonColour());
        final Font oldFont = g2d.getFont();
        final int r = 10;
        g2d.fillArc(this.cx - 10, this.cy - 10, 21, 21, 0, 360);
        final int fontSize = 17;
        final int flags = 3;
        final Font font = new Font("Arial", 3, 17);
        g2d.setFont(font);
        g2d.setColor(Color.white);
        g2d.drawString("i", this.cx - 3, this.cy + 6);
        g2d.setFont(oldFont);
    }
    
    @Override
    public void press() {
        AboutDialog.showAboutDialog();
        SettingsVC.selectedLocation = new FullLocation(-1);
    }
}
