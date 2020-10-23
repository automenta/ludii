// 
// Decompiled by Procyon v0.5.36
// 

package app.loading;

import app.DesktopApp;
import app.display.MainWindow;
import app.utils.SettingsDesktop;
import util.SettingsVC;
import util.locations.FullLocation;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class FileLoading
{
    public static final String selectFile(final JFrame parent, final boolean isOpen, final String relativePath, final String description, final MainWindow view, final String... extensions) {
        final String baseFolder = System.getProperty("user.dir");
        SettingsVC.selectedLocation = new FullLocation(-1);
        String folder = baseFolder + relativePath;
        final File testFile = new File(folder);
        if (!testFile.exists()) {
            folder = baseFolder;
        }
        final JFileChooser dlg = new JFileChooser(folder);
        dlg.setPreferredSize(new Dimension(500, 500));
        final FileFilter filter = new FileNameExtensionFilter(description, extensions);
        dlg.setFileFilter(filter);
        int response;
        if (isOpen) {
            response = dlg.showOpenDialog(parent);
        }
        else {
            response = dlg.showSaveDialog(parent);
        }
        if (response == 0) {
            return dlg.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
    
    public static void createFileChoosers() {
        DesktopApp.setJsonFileChooser(createFileChooser(DesktopApp.lastSelectedJsonPath(), ".json", "JSON files (.json)"));
        DesktopApp.setJarFileChooser(createFileChooser(DesktopApp.lastSelectedJarPath(), ".jar", "JAR files (.jar)"));
        DesktopApp.setGameFileChooser(createFileChooser(DesktopApp.lastSelectedGamePath(), ".lud", "LUD files (.lud)"));
        DesktopApp.setSaveGameFileChooser(new JFileChooser(DesktopApp.lastSelectedSaveGamePath()));
        DesktopApp.saveGameFileChooser().setPreferredSize(new Dimension(SettingsDesktop.defaultWidth, SettingsDesktop.defaultHeight));
        DesktopApp.setLoadTrialFileChooser(new JFileChooser(DesktopApp.lastSelectedLoadTrialPath()));
        DesktopApp.loadTrialFileChooser().setPreferredSize(new Dimension(SettingsDesktop.defaultWidth, SettingsDesktop.defaultHeight));
        DesktopApp.setLoadTournamentFileChooser(new JFileChooser(DesktopApp.lastSelectedLoadTournamentPath()));
        DesktopApp.loadTournamentFileChooser().setPreferredSize(new Dimension(SettingsDesktop.defaultWidth, SettingsDesktop.defaultHeight));
    }
    
    private static JFileChooser createFileChooser(final String defaultDir, final String extension, final String description) {
        JFileChooser fileChooser;
        if (defaultDir != null && defaultDir.length() > 0 && new File(defaultDir).exists()) {
            fileChooser = new JFileChooser(defaultDir);
        }
        else {
            fileChooser = new JFileChooser("");
        }
        final FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || f.getName().endsWith(extension);
            }
            
            @Override
            public String getDescription() {
                return description;
            }
        };
        fileChooser.setFileFilter(filter);
        fileChooser.setPreferredSize(new Dimension(SettingsDesktop.defaultWidth, SettingsDesktop.defaultHeight));
        final Action details = fileChooser.getActionMap().get("viewTypeDetails");
        if (details != null) {
            details.actionPerformed(null);
        }
        return fileChooser;
    }
}
