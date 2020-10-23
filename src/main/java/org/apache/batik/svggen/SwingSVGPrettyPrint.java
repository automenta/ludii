// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import javax.swing.border.Border;
import java.awt.Component;
import javax.swing.JProgressBar;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.JPopupMenu;
import javax.swing.AbstractButton;
import javax.swing.plaf.ComponentUI;
import javax.swing.UIManager;
import java.awt.Graphics;
import java.awt.Rectangle;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.swing.JScrollBar;
import javax.swing.JComboBox;
import javax.swing.JComponent;

public abstract class SwingSVGPrettyPrint implements SVGSyntax
{
    public static void print(final JComponent cmp, final SVGGraphics2D svgGen) {
        if (cmp instanceof JComboBox || cmp instanceof JScrollBar) {
            printHack(cmp, svgGen);
            return;
        }
        final SVGGraphics2D g = (SVGGraphics2D)svgGen.create();
        g.setColor(cmp.getForeground());
        g.setFont(cmp.getFont());
        final Element topLevelGroup = g.getTopLevelGroup();
        if (cmp.getWidth() <= 0 || cmp.getHeight() <= 0) {
            return;
        }
        final Rectangle clipRect = g.getClipBounds();
        if (clipRect == null) {
            g.setClip(0, 0, cmp.getWidth(), cmp.getHeight());
        }
        paintComponent(cmp, g);
        paintBorder(cmp, g);
        paintChildren(cmp, g);
        final Element cmpGroup = g.getTopLevelGroup();
        cmpGroup.setAttributeNS(null, "id", svgGen.getGeneratorContext().idGenerator.generateID(cmp.getClass().getName()));
        topLevelGroup.appendChild(cmpGroup);
        svgGen.setTopLevelGroup(topLevelGroup);
    }
    
    private static void printHack(final JComponent cmp, final SVGGraphics2D svgGen) {
        final SVGGraphics2D g = (SVGGraphics2D)svgGen.create();
        g.setColor(cmp.getForeground());
        g.setFont(cmp.getFont());
        final Element topLevelGroup = g.getTopLevelGroup();
        if (cmp.getWidth() <= 0 || cmp.getHeight() <= 0) {
            return;
        }
        final Rectangle clipRect = g.getClipBounds();
        if (clipRect == null) {
            g.setClip(0, 0, cmp.getWidth(), cmp.getHeight());
        }
        cmp.paint(g);
        final Element cmpGroup = g.getTopLevelGroup();
        cmpGroup.setAttributeNS(null, "id", svgGen.getGeneratorContext().idGenerator.generateID(cmp.getClass().getName()));
        topLevelGroup.appendChild(cmpGroup);
        svgGen.setTopLevelGroup(topLevelGroup);
    }
    
    private static void paintComponent(final JComponent cmp, final SVGGraphics2D svgGen) {
        final ComponentUI ui = UIManager.getUI(cmp);
        if (ui != null) {
            ui.installUI(cmp);
            ui.update(svgGen, cmp);
        }
    }
    
    private static void paintBorder(final JComponent cmp, final SVGGraphics2D svgGen) {
        final Border border = cmp.getBorder();
        if (border != null) {
            if (cmp instanceof AbstractButton || cmp instanceof JPopupMenu || cmp instanceof JToolBar || cmp instanceof JMenuBar || cmp instanceof JProgressBar) {
                if ((cmp instanceof AbstractButton && ((AbstractButton)cmp).isBorderPainted()) || (cmp instanceof JPopupMenu && ((JPopupMenu)cmp).isBorderPainted()) || (cmp instanceof JToolBar && ((JToolBar)cmp).isBorderPainted()) || (cmp instanceof JMenuBar && ((JMenuBar)cmp).isBorderPainted()) || (cmp instanceof JProgressBar && ((JProgressBar)cmp).isBorderPainted())) {
                    border.paintBorder(cmp, svgGen, 0, 0, cmp.getWidth(), cmp.getHeight());
                }
            }
            else {
                border.paintBorder(cmp, svgGen, 0, 0, cmp.getWidth(), cmp.getHeight());
            }
        }
    }
    
    private static void paintChildren(final JComponent cmp, final SVGGraphics2D svgGen) {
        int i = cmp.getComponentCount() - 1;
        final Rectangle tmpRect = new Rectangle();
        while (i >= 0) {
            final Component comp = cmp.getComponent(i);
            if (comp != null && JComponent.isLightweightComponent(comp) && comp.isVisible()) {
                Rectangle cr = null;
                final boolean isJComponent = comp instanceof JComponent;
                if (isJComponent) {
                    cr = tmpRect;
                    comp.getBounds(cr);
                }
                else {
                    cr = comp.getBounds();
                }
                final boolean hitClip = svgGen.hitClip(cr.x, cr.y, cr.width, cr.height);
                if (hitClip) {
                    final SVGGraphics2D cg = (SVGGraphics2D)svgGen.create(cr.x, cr.y, cr.width, cr.height);
                    cg.setColor(comp.getForeground());
                    cg.setFont(comp.getFont());
                    if (comp instanceof JComponent) {
                        print((JComponent)comp, cg);
                    }
                    else {
                        comp.paint(cg);
                    }
                }
            }
            --i;
        }
    }
}
