// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tabs.pages;

import app.display.views.tabs.TabPage;
import app.display.views.tabs.TabView;
import util.Context;

import java.awt.*;

public class AnalysisPage extends TabPage
{
    public AnalysisPage(final Rectangle rect, final String title, final String text, final int pageIndex, final TabView parent) {
        super(rect, title, text, pageIndex, parent);
    }
    
    @Override
    public void updatePage(final Context context) {
    }
    
    @Override
    public void reset() {
        this.clear();
    }
}
