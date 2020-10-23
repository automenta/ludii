// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tabs;

import app.DesktopApp;
import app.display.MainWindow;
import app.display.views.View;
import app.display.views.tabs.pages.*;
import app.utils.SettingsDesktop;
import manager.Manager;
import util.Context;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TabView extends View
{
    public static int selected;
    private final List<TabPage> pages;
    protected final Color bgColour;
    public static final int fontSize = 16;
    public static int tabTitleHeight;
    public static final int PanelStatus = 0;
    public static final int PanelMoves = 1;
    public static final int PanelTurns = 2;
    public static final int PanelAnalysis = 3;
    public static final int PanelLudeme = 4;
    public static final int PanelRules = 5;
    public static final int PanelInfo = 6;
    boolean titlesSet;
    
    public TabView() {
        this.pages = new ArrayList<>();
        this.bgColour = new Color(255, 255, 230);
        this.titlesSet = false;
        TabView.tabTitleHeight = 16;
        DesktopApp.view();
        final int toolHeight = MainWindow.toolPanel().placement().height;
        DesktopApp.view();
        int startX;
        final int boardSize = startX = MainWindow.getStatePanel().placement().width;
        DesktopApp.view();
        int startY = MainWindow.getPlayerPanel().placement().height;
        int width = DesktopApp.view().getWidth() - boardSize;
        final int height2 = DesktopApp.view().getHeight();
        DesktopApp.view();
        int height = height2 - MainWindow.getPlayerPanel().placement().height - toolHeight;
        if (DesktopApp.view().getWidth() < DesktopApp.view().getHeight()) {
            startX = 0;
            final int n = boardSize;
            DesktopApp.view();
            startY = n + MainWindow.getPlayerPanel().placement().height;
            width = DesktopApp.view().getWidth();
            final int height3 = DesktopApp.view().getHeight();
            DesktopApp.view();
            height = height3 - MainWindow.getPlayerPanel().placement().height - toolHeight - boardSize;
        }
        this.placement.setBounds(startX, startY, width, height);
        final Rectangle tabPagePlacement = new Rectangle(this.placement.x + 10, this.placement.y + TabView.tabTitleHeight + 6, this.placement.width - 16, this.placement.height - TabView.tabTitleHeight - 20);
        final TabPage statusPage = new StatusPage(tabPagePlacement, " Status ", "", 0, this);
        final TabPage movesPage = new MovesPage(tabPagePlacement, " Moves ", "", 1, this);
        final TabPage turnsPage = new TurnsPage(tabPagePlacement, " Turns", "", 2, this);
        final TabPage analysisPage = new AnalysisPage(tabPagePlacement, " Analysis ", "", 3, this);
        final TabPage ludemePage = new LudemePage(tabPagePlacement, " Ludeme  ", "", 4, this);
        final TabPage rulesPage = new RulesPage(tabPagePlacement, " Rules ", "", 5, this);
        final TabPage infoPage = new InfoPage(tabPagePlacement, " Info  ", "", 6, this);
        this.pages.add(statusPage);
        this.pages.add(movesPage);
        this.pages.add(turnsPage);
        this.pages.add(analysisPage);
        this.pages.add(ludemePage);
        this.pages.add(rulesPage);
        this.pages.add(infoPage);
        DesktopApp.view();
        Label_0651: {
            Label_0647: {
                if (MainWindow.tabPanel() != null) {
                    final int width2 = this.placement.width;
                    DesktopApp.view();
                    if (width2 == MainWindow.tabPanel().placement.width) {
                        final int height4 = this.placement.height;
                        DesktopApp.view();
                        if (height4 == MainWindow.tabPanel().placement.height) {
                            break Label_0647;
                        }
                    }
                    DesktopApp.view();
                    for (final TabPage p : MainWindow.tabPanel().pages) {
                        this.pages.get(p.pageIndex).clear();
                        this.pages.get(p.pageIndex).addText(p.solidText);
                        this.pages.get(p.pageIndex).addFadedText(p.fadedText);
                    }
                    break Label_0651;
                }
            }
            this.resetTabs();
        }
        this.pages.get(0).clear();
        this.pages.get(0).addText(DesktopApp.savedStatusTabString);
        this.select(TabView.selected);
        for (final View view : this.pages) {
            DesktopApp.view();
            MainWindow.getPanels().add(view);
        }
    }
    
    public int fontSize() {
        return 16;
    }
    
    public boolean titlesSet() {
        return this.titlesSet;
    }
    
    @Override
    public void paint(final Graphics2D g2d) {
        final int x0 = this.placement.x;
        final int y0 = this.placement.y;
        final int sx = this.placement.width;
        final int sy = this.placement.height;
        if (!this.titlesSet) {
            this.setTitleRects();
            this.titlesSet = true;
        }
        if (SettingsDesktop.darkMode) {
            g2d.setColor(Color.black);
        }
        else {
            g2d.setColor(Color.white);
        }
        g2d.fillRect(x0, y0, sx, sy);
        TabView.tabTitleHeight = 16;
        final int tx0 = this.placement.x;
        final int ty0 = this.placement.y;
        final int tsx = this.placement.width;
        final int tsy = TabView.tabTitleHeight + 6;
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillRect(tx0, ty0, tsx, tsy);
        for (final TabPage page : this.pages) {
            page.paint(g2d);
        }
        this.paintDebug(g2d, Color.GREEN);
    }
    
    public void setTitleRects() {
        int x = this.placement.x;
        final int y = this.placement.y;
        for (final TabPage page : this.pages) {
            final int wd = (int)page.titleRect().getWidth();
            final int ht = TabView.tabTitleHeight + 6;
            page.setTitleRect(x, y, wd, ht);
            x += wd;
        }
    }
    
    public void select(final int pid) {
        for (final TabPage p : this.pages) {
            p.show(false);
        }
        this.pages.get(pid).show(true);
        TabView.selected = pid;
        Manager.app.repaint();
    }
    
    public void clickAt(final Point pixel) {
        for (final TabPage p : this.pages) {
            if (p.titleRect.contains(pixel.x, pixel.y)) {
                this.select(p.pageIndex);
            }
        }
    }
    
    public void updateTabs(final Context context) {
        for (TabPage page : this.pages) {
            page.updatePage(context);
        }
    }
    
    public void resetTabs() {
        for (int i = 1; i < this.pages.size(); ++i) {
            this.pages.get(i).reset();
        }
    }
    
    public List<TabPage> pages() {
        return this.pages;
    }
    
    public TabPage page(final int i) {
        return this.pages.get(i);
    }
    
    public static int selected() {
        return TabView.selected;
    }
    
    public static void setSelected(final int selected) {
        TabView.selected = selected;
    }
    
    static {
        TabView.selected = 0;
        TabView.tabTitleHeight = 16;
    }
}
