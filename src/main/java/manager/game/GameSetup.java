// 
// Decompiled by Procyon v0.5.36
// 

package manager.game;

import bridge.Bridge;
import bridge.ViewControllerFactory;
import game.equipment.component.Component;
import game.equipment.container.Container;
import manager.Manager;
import metadata.Metadata;
import util.Context;

public class GameSetup
{
    public static void setMVC() {
        final Context context = Manager.ref().context();
        final Metadata metadata = context.metadata();
        if (metadata != null && metadata.graphics().boardStyle() != null) {
            context.board().setStyle(metadata.graphics().boardStyle());
        }
        for (final Container c : context.equipment().containers()) {
            Bridge.addContainerStyle(ViewControllerFactory.createStyle(c, c.style(), context), c.index());
            Bridge.addContainerController(ViewControllerFactory.createController(c, c.controller()), c.index());
        }
        for (final Component c2 : context.equipment().components()) {
            if (c2 != null) {
                if (metadata != null && metadata.graphics().componentStyle(c2.owner(), c2.name(), context) != null) {
                    c2.setStyle(metadata.graphics().componentStyle(c2.owner(), c2.name(), context));
                }
                Bridge.addComponentStyle(ViewControllerFactory.createStyle(c2, c2.style()), c2.index());
            }
        }
        Bridge.setGraphicsRenderer(Manager.app.platformGraphics());
    }
}
