// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tabs.pages;

import app.display.views.tabs.TabPage;
import app.display.views.tabs.TabView;
import game.Game;
import manager.utils.ContextSnapshot;
import metadata.Metadata;
import util.Context;

import java.awt.*;
import java.util.Arrays;

public class InfoPage extends TabPage
{
    public InfoPage(final Rectangle rect, final String title, final String text, final int pageIndex, final TabView parent) {
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
                if (!metadata.info().getDescription().isEmpty()) {
                    this.addText("Description:\n");
                    this.addText(metadata.info().getDescription().get(metadata.info().getDescription().size() - 1));
                    this.addText("\n\n");
                }
                if (!metadata.info().getAuthor().isEmpty()) {
                    this.addText("Author:\n");
                    this.addText(metadata.info().getAuthor().get(metadata.info().getAuthor().size() - 1));
                    this.addText("\n\n");
                }
                if (!metadata.info().getPublisher().isEmpty()) {
                    this.addText("Publisher:\n");
                    this.addText(metadata.info().getPublisher().get(metadata.info().getPublisher().size() - 1));
                    this.addText("\n\n");
                }
                if (!metadata.info().getDate().isEmpty()) {
                    this.addText("Date:\n");
                    this.addText(metadata.info().getDate().get(metadata.info().getDate().size() - 1));
                    this.addText("\n\n");
                }
                if (metadata.info().getAliases().length > 0) {
                    this.addText("Aliases:\n");
                    this.addText(Arrays.toString(metadata.info().getAliases()));
                    this.addText("\n\n");
                }
                if (!metadata.info().getOrigin().isEmpty()) {
                    this.addText("Origin:\n");
                    this.addText(metadata.info().getOrigin().get(metadata.info().getOrigin().size() - 1));
                    this.addText("\n\n");
                }
                if (!metadata.info().getClassification().isEmpty()) {
                    this.addText("Classification:\n");
                    this.addText(metadata.info().getClassification().get(metadata.info().getClassification().size() - 1));
                    this.addText("\n\n");
                }
                if (!metadata.info().getCredit().isEmpty()) {
                    this.addText("Credit:\n");
                    this.addText(metadata.info().getCredit().get(metadata.info().getCredit().size() - 1));
                    this.addText("\n\n");
                }
                if (!metadata.info().getVersion().isEmpty()) {
                    this.addText("Version:\n");
                    this.addText(metadata.info().getVersion().get(metadata.info().getVersion().size() - 1));
                    this.addText("\n\n");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
