// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tabs.pages;

import app.display.views.tabs.TabPage;
import app.display.views.tabs.TabView;
import game.Game;
import main.options.Option;
import manager.utils.ContextSnapshot;
import manager.utils.SettingsManager;
import metadata.Metadata;
import util.Context;

import java.awt.*;
import java.util.List;

public class RulesPage extends TabPage
{
    public RulesPage(final Rectangle rect, final String title, final String text, final int pageIndex, final TabView parent) {
        super(rect, title, text, pageIndex, parent);
    }
    
    @Override
    public void updatePage(final Context context) {
    }
    
    @Override
    public void reset() {
        this.clear();
        final Game game = ContextSnapshot.getContext().game();
        try {
            final Metadata metadata = game.metadata();
            if (metadata != null) {
                if (metadata.info().getRules().size() > 0) {
                    this.addText("Rules:\n");
                    for (final String s : metadata.info().getRules()) {
                        this.addText(s);
                        this.addText("\n\n");
                    }
                }
                if (metadata.info().getSource().size() > 0) {
                    this.addText("Source:\n");
                    for (final String s : metadata.info().getSource()) {
                        this.addText(s);
                        this.addText("\n");
                    }
                    this.addText("\n");
                }
                if (SettingsManager.userSelections.ruleset() == -1) {
                    final List<Option> activeOptions = game.description().gameOptions().activeOptionObjects(SettingsManager.userSelections.selectedOptionStrings());
                    if (activeOptions.size() > 0) {
                        this.addText("Options:\n");
                        for (final Option option : activeOptions) {
                            this.addText(option.description() + "\n");
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
