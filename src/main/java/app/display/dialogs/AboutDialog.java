// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs;

import app.DesktopApp;
import game.equipment.container.Container;
import manager.Manager;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AboutDialog
{
    public static void showAboutDialog() {
        final URL iconURL = DesktopApp.class.getResource("/all-logos-64.png");
        final ImageIcon icon = new ImageIcon(iconURL);
        final StringBuilder sbDescription = new StringBuilder();
        sbDescription.append("Ludii General Game System\n");
        final StringBuilder sbVersion = new StringBuilder();
        sbVersion.append("Version 1.0.8 (18/9/2020)\n");
        final StringBuilder sbLegal = new StringBuilder();
        sbLegal.append("Cameron Browne (c) 2017-20\n\n");
        final StringBuilder sbTeam = new StringBuilder();
        sbTeam.append("Design: Cameron Browne\n\n");
        sbTeam.append("Code: Cameron Browne, Eric Piette, Matthew Stephenson,\nDennis Soemers, Stephen Tavener, Tahmina Begum, \nCoen Hacking and Lianne Hufkens\n\n");
        sbTeam.append("Testing: Wijnand Engelkes\n\n");
        sbTeam.append("Advisor: Walter Crist\n\n");
        final StringBuilder sbAdmin = new StringBuilder();
        sbAdmin.append("Developed as part of the Digital Ludeme Project \nfunded by ERC Consolidator Grant #771292 run by\nCameron Browne at Maastricht University\n\n");
        final StringBuilder sbURLs = new StringBuilder();
        sbURLs.append("http://ludii.games\nhttp://ludeme.eu\n\n");
        final StringBuilder sbCredits = new StringBuilder();
        final Map<Integer, String> creditMap = new HashMap<>();
        for (final game.equipment.component.Component component : Manager.ref().context().game().equipment().components()) {
            if (component != null && component.credit() != null) {
                final Integer key = component.getNameWithoutNumber().hashCode();
                if (creditMap.get(key) == null) {
                    sbCredits.append(component.credit()).append("\n");
                    creditMap.put(key, "Found");
                }
            }
        }
        for (final Container container : Manager.ref().context().game().equipment().containers()) {
            if (container != null && container.credit() != null) {
                final Integer key = container.name().hashCode();
                if (creditMap.get(key) == null) {
                    sbCredits.append(container.credit()).append("\n");
                    creditMap.put(key, "Found");
                }
            }
        }
        sbCredits.append("Pling audio file by KevanGC from http://soundbible.com/1645-Pling.html.\n");
        JOptionPane.showMessageDialog(DesktopApp.frame(), sbDescription.toString() + sbVersion.toString() + sbLegal.toString() + sbTeam.toString() + sbAdmin.toString() + sbURLs.toString() + sbCredits.toString(), "Ludii Player", -1, icon);
    }
}
