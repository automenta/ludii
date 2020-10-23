// 
// Decompiled by Procyon v0.5.36
// 

package features.visualisation;

import features.FeatureSet;
import features.features.Feature;
import game.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeaturesFrame extends JFrame
{
    private static final long serialVersionUID = 1L;
    protected JPanel imagePanel;
    protected static JFileChooser outDirChooser;
    
    public FeaturesFrame(final FeatureSet featureSet, final int player, final Game game, final String featureSetName) {
        super("Ludii Features");
        this.setDefaultCloseOperation(2);
        try {
            final URL resource = this.getClass().getResource("/ludii-logo-64x64.png");
            final BufferedImage image = ImageIO.read(resource);
            this.setIconImage(image);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        final int numFeatures = featureSet.getNumFeatures();
        final int numCols = 5;
        final int numRows = (int)Math.ceil(numFeatures / 5.0);
        this.imagePanel = new JPanel(new GridLayout(numRows, 5));
        final JScrollPane scrollPane = new JScrollPane(this.imagePanel);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        this.setContentPane(scrollPane);
        if (FeaturesFrame.outDirChooser == null) {
            (FeaturesFrame.outDirChooser = new JFileChooser("")).setFileSelectionMode(2);
            final FileFilter dirFilter = new FileFilter() {
                @Override
                public boolean accept(final File f) {
                    return f.isDirectory();
                }
                
                @Override
                public String getDescription() {
                    return "Directories";
                }
            };
            FeaturesFrame.outDirChooser.setFileFilter(dirFilter);
            FeaturesFrame.outDirChooser.setPreferredSize(new Dimension(1000, 600));
            final Action details = FeaturesFrame.outDirChooser.getActionMap().get("viewTypeDetails");
            if (details != null) {
                details.actionPerformed(null);
            }
        }
        FeaturesFrame.outDirChooser.setDialogTitle("Choose an empty output directory");
        final int fcReturnVal = FeaturesFrame.outDirChooser.showOpenDialog(null);
        if (fcReturnVal == 0) {
            final File outDir = FeaturesFrame.outDirChooser.getSelectedFile();
            if (outDir != null && outDir.exists()) {
                final File[] files = outDir.listFiles();
                if (files == null || files.length > 0) {
                    JOptionPane.showMessageDialog(null, "This is not an empty output directory.");
                }
                else {
                    createImages(featureSet, player, game, outDir, featureSetName);
                }
            }
        }
        this.pack();
        this.setLocationByPlatform(true);
    }
    
    private static void createImages(final FeatureSet featureSet, final int player, final Game game, final File outDir, final String featureSetName) {
        final Feature[] features = featureSet.features();
        try (final PrintWriter w = new PrintWriter(outDir.getAbsolutePath() + File.separator + featureSetName + ".tex", StandardCharsets.UTF_8)) {
            w.println("\\documentclass[a4paper,12pt]{article}");
            w.println("");
            w.println("\\usepackage{graphicx}");
            w.println("\\usepackage{subcaption}");
            w.println("\\usepackage{seqsplit}");
            w.println("\\usepackage{float}");
            w.println("");
            w.println("\\begin{document}");
            w.println("");
            for (int i = 1; i <= features.length; ++i) {
                FeatureToEPS.createEPS(features[i - 1], player, game, new File(outDir.getAbsolutePath() + File.separator + String.format("Feature_%05d.eps", i)));
                final boolean newFig = (i - 1) % 3 == 0;
                if (i > 1 && newFig) {
                    w.println("\\end{figure}");
                }
                if (newFig) {
                    w.println("\\begin{figure}[H]");
                    if (i > 1) {
                        w.println("\\ContinuedFloat");
                    }
                    w.println("\\centering");
                }
                w.println("\\begin{subfigure}{.3\\textwidth}");
                w.println("\\centering");
                w.println("\\includegraphics[height=3.5cm]{" + String.format("Feature_%05d.eps", i) + "}");
                w.println("\\caption*{\\scriptsize\\texttt{\\seqsplit{" + features[i - 1].toString().replaceAll(Pattern.quote("#"), Matcher.quoteReplacement("\\#")).replaceAll(Pattern.quote("{"), Matcher.quoteReplacement("\\{")).replaceAll(Pattern.quote("}"), Matcher.quoteReplacement("\\}")).replaceAll(Pattern.quote(" "), Matcher.quoteReplacement("~")) + "}}}");
                w.println("\\end{subfigure} \\hfill");
            }
            w.println("\\end{figure}");
            w.println("\\end{document}");
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }
    
    static {
        FeaturesFrame.outDirChooser = null;
    }
}
