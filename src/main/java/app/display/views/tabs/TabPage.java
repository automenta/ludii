// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tabs;

import app.DesktopApp;
import app.display.views.View;
import app.display.views.tabs.pages.InfoPage;
import app.display.views.tabs.pages.RulesPage;
import app.utils.SettingsDesktop;
import util.Context;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class TabPage extends View
{
    protected String title;
    protected JTextPane textArea;
    protected HTMLDocument doc;
    protected Style textstyle;
    protected Color fontColour;
    protected Color fadedFontColour;
    protected JScrollPane scrollPane;
    public String solidText;
    public String fadedText;
    public Rectangle titleRect;
    protected boolean mouseOverTitle;
    public final int pageIndex;
    private final TabView parent;
    
    public TabPage(final Rectangle rect, final String title, final String text, final int pageIndex, final TabView parent) {
        this.title = "Tab";
        (this.textArea = new JTextPane()).setContentType("text/html");
        this.doc = (HTMLDocument)this.textArea.getDocument();
        this.textstyle = this.textArea.addStyle("text style", null);
        this.scrollPane = new JScrollPane(this.textArea);
        this.solidText = "";
        this.fadedText = "";
        this.titleRect = null;
        this.mouseOverTitle = false;
        this.parent = parent;
        this.placement = rect;
        this.title = title;
        this.pageIndex = pageIndex;
        final int charWidth = 9;
        final int wd = 9 * this.title.length();
        final int ht = TabView.tabTitleHeight;
        this.titleRect = new Rectangle(rect.x, rect.y, wd, ht);
        this.scrollPane.setBounds(this.placement);
        this.scrollPane.setBorder(null);
        this.scrollPane.setVisible(false);
        this.scrollPane.setFocusable(false);
        this.textArea.setFocusable(true);
        this.textArea.setEditable(false);
        this.textArea.setBackground(new Color(255, 255, 255));
        final DefaultCaret caret = (DefaultCaret)this.textArea.getCaret();
        caret.setUpdatePolicy(2);
        this.textArea.setFont(new Font("Arial", 0, SettingsDesktop.tabFontSize));
        this.textArea.setContentType("text/html");
        this.textArea.putClientProperty("JEditorPane.honorDisplayProperties", true);
        if (SettingsDesktop.darkMode) {
            this.fontColour = new Color(250, 250, 250);
            this.textArea.setBackground(Color.black);
        }
        else {
            this.fontColour = new Color(50, 50, 50);
            this.textArea.setBackground(Color.white);
        }
        this.fadedFontColour = new Color(this.fontColour.getRed() + (int)((255 - this.fontColour.getRed()) * 0.75), this.fontColour.getGreen() + (int)((255 - this.fontColour.getGreen()) * 0.75), this.fontColour.getBlue() + (int)((255 - this.fontColour.getBlue()) * 0.75));
        StyleConstants.setForeground(this.textstyle, this.fontColour);
        this.textArea.setVisible(false);
        this.textArea.setText(text);
        DesktopApp.view().setLayout(null);
        DesktopApp.view().add(this.scrollPane());
        this.textArea.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                }
                catch (IOException | URISyntaxException ex2) {
                    ex2.printStackTrace();
                }
            }
        });
    }
    
    public abstract void updatePage(final Context p0);
    
    public abstract void reset();
    
    public String title() {
        return this.title;
    }
    
    public Rectangle titleRect() {
        return this.titleRect;
    }
    
    public void setTitleRect(final int x, final int y, final int wd, final int ht) {
        this.titleRect = new Rectangle(x, y, wd, ht);
    }
    
    public JScrollPane scrollPane() {
        return this.scrollPane;
    }
    
    public void show(final boolean show) {
        this.textArea.setVisible(show);
        this.scrollPane.setVisible(show);
    }
    
    public void clear() {
        this.textArea.setText("");
    }
    
    public void disableCaretUpdates() {
        final DefaultCaret caret = (DefaultCaret)this.textArea.getCaret();
        caret.setUpdatePolicy(1);
    }
    
    public void enableCaretUpdates() {
        final DefaultCaret caret = (DefaultCaret)this.textArea.getCaret();
        caret.setUpdatePolicy(2);
    }
    
    public void addText(final String str) {
        StyleConstants.setForeground(this.textstyle, this.fontColour);
        try {
            if (this instanceof InfoPage || this instanceof RulesPage) {
                final HTMLEditorKit editorKit = (HTMLEditorKit)this.textArea.getEditorKit();
                this.doc = (HTMLDocument)this.textArea.getDocument();
                try {
                    editorKit.insertHTML(this.doc, this.doc.getLength(), str + "<br>", 0, 0, null);
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
                final StringWriter writer = new StringWriter();
                try {
                    editorKit.write(writer, this.doc, 0, this.doc.getLength());
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
                this.solidText = writer.toString();
            }
            else {
                this.doc.insertString(this.doc.getLength(), str, this.textstyle);
                this.solidText = this.doc.getText(0, this.doc.getLength());
            }
        }
        catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
    
    protected void addFadedText(final String str) {
        StyleConstants.setForeground(this.textstyle, this.fadedFontColour);
        try {
            this.doc.insertString(this.doc.getLength(), str, this.textstyle);
            this.fadedText = this.doc.getText(this.solidText.length(), this.doc.getLength() - this.solidText.length());
            Rectangle r = null;
            this.textArea.setCaretPosition(this.solidText.length());
            r = this.textArea.modelToView(this.textArea.getCaretPosition());
            this.textArea.scrollRectToVisible(r);
        }
        catch (Exception ex) {}
    }
    
    public String text() {
        try {
            return this.doc.getText(0, this.doc.getLength());
        }
        catch (BadLocationException e) {
            e.printStackTrace();
            return this.textArea.getText();
        }
    }
    
    @Override
    public void paint(final Graphics2D g2d) {
        if (!this.parent.titlesSet()) {
            this.parent.setTitleRects();
        }
        this.drawTabPageTitle(g2d);
        this.paintDebug(g2d, Color.YELLOW);
    }
    
    private void drawTabPageTitle(final Graphics2D g2d) {
        final Font oldFont = g2d.getFont();
        final Font font = new Font("Arial", 1, 16);
        g2d.setFont(font);
        final Color dark = new Color(50, 50, 50);
        final Color light = new Color(255, 255, 255);
        final Color mouseOver = new Color(150, 150, 150);
        if (this.pageIndex == TabView.selected()) {
            g2d.setColor(dark);
        }
        else if (this.mouseOverTitle) {
            g2d.setColor(mouseOver);
        }
        else {
            g2d.setColor(light);
        }
        final String str = this.title();
        final Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(str, g2d);
        final int tx = this.titleRect.x + (int)(this.titleRect.width / 2 - bounds.getWidth() / 2.0);
        final int ty = this.titleRect.y + this.titleRect.height / 2 + 5;
        g2d.drawString(str, tx, ty);
        g2d.setFont(oldFont);
    }
    
    @Override
    public void mouseOverAt(final Point pixel) {
        if (this.titleRect.contains(pixel.x, pixel.y)) {
            if (!this.mouseOverTitle) {
                this.mouseOverTitle = true;
                DesktopApp.view().repaint(this.titleRect);
            }
        }
        else if (this.mouseOverTitle) {
            this.mouseOverTitle = false;
            DesktopApp.view().repaint(this.titleRect);
        }
    }
}
