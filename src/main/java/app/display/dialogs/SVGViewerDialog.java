// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs;

import app.DesktopApp;
import app.display.SVGWindow;
import app.display.util.SVGUtil;
import app.loading.MiscLoading;
import manager.utils.ContextSnapshot;
import org.jfree.graphics2d.svg.SVGGraphics2D;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

public class SVGViewerDialog
{
    static String lastKeyPressed;
    
    public static String showDialog(final JFrame frame, final String[] choices) {
        final JPanel contentPane = new JPanel();
        final SVGWindow svgView = new SVGWindow();
        contentPane.setLayout(new BorderLayout());
        SVGViewerDialog.lastKeyPressed = "";
        final List<svgLoaderNode> leafNodes = new ArrayList<>();
        final Map<String, svgLoaderNode> nodesMap = new HashMap<>();
        final svgLoaderNode root = new svgLoaderNode("svgs", File.separator + "svg" + File.separator);
        for (final String choice : choices) {
            String str = choice.replaceAll(Pattern.quote("\\"), "/");
            if (str.startsWith("/")) {
                str = str.substring(1);
            }
            final String[] parts = str.split("/");
            if (!parts[0].equals("svg")) {
                System.err.println("top level is not svg: " + parts[0]);
            }
            String runningFullName = File.separator + "svg" + File.separator;
            svgLoaderNode internalNode = root;
            for (int i = 1; i < parts.length - 1; ++i) {
                runningFullName = runningFullName + parts[i] + File.separator;
                svgLoaderNode nextInternal;
                if (!nodesMap.containsKey(runningFullName)) {
                    nextInternal = new svgLoaderNode(parts[i], runningFullName);
                    nodesMap.put(runningFullName, nextInternal);
                    int childIdx;
                    for (childIdx = 0; childIdx < internalNode.getChildCount(); ++childIdx) {
                        final svgLoaderNode existingChild = (svgLoaderNode)internalNode.getChildAt(childIdx);
                        final String name = (String)existingChild.getUserObject();
                        if (name.endsWith(".svg")) {
                            break;
                        }
                        if (parts[i].compareToIgnoreCase(name) < 0) {
                            break;
                        }
                    }
                    internalNode.insert(nextInternal, childIdx);
                }
                else {
                    nextInternal = nodesMap.get(runningFullName);
                }
                internalNode = nextInternal;
            }
            final svgLoaderNode leafNode = new svgLoaderNode(parts[parts.length - 1], choice);
            nodesMap.put(choice, leafNode);
            leafNodes.add(leafNode);
            internalNode.add(leafNode);
        }
        final svgLoaderTree tree = new svgLoaderTree(root);
        expandAllNodes(tree, 0, tree.getRowCount());
        tree.getSelectionModel().setSelectionMode(1);
        final JTextField filterField = new JTextField();
        filterField.setFont(new Font("Arial", 0, 20));
        final Font gainFont = new Font("Arial", 0, 20);
        final Font lostFont = new Font("Arial", 0, 20);
        final String hint = "Search SVG";
        filterField.setText("Search SVG");
        filterField.setFont(lostFont);
        filterField.setForeground(Color.GRAY);
        final KeyEventDispatcher keyDispatcher = e -> {
            boolean focusRequested = false;
            if (e.getKeyCode() == 38) {
                if (!tree.hasFocus()) {
                    SVGViewerDialog.lastKeyPressed = "UP";
                    tree.requestFocus();
                }
            }
            else if (e.getKeyCode() == 40) {
                if (!tree.hasFocus()) {
                    SVGViewerDialog.lastKeyPressed = "DOWN";
                    tree.requestFocus();
                }
            }
            else if (e.getKeyCode() != 37 && e.getKeyCode() != 39 && KeyEvent.getKeyText(e.getKeyCode()).length() == 1 && !filterField.hasFocus()) {
                SVGViewerDialog.lastKeyPressed = Character.toString(e.getKeyChar());
                filterField.requestFocus();
                focusRequested = true;
            }
            String fileName = null;
            final TreePath treePath = tree.getSelectionPath();
            if (treePath != null) {
                final svgLoaderNode selectedLeaf = (svgLoaderNode)treePath.getLastPathComponent();
                if (selectedLeaf.isLeaf()) {
                    fileName = selectedLeaf.fullName;
                }
            }
            if (fileName != null) {
                SVGViewerDialog.displayImage(fileName, contentPane, svgView);
            }
            return focusRequested;
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyDispatcher);
        tree.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {
            }
            
            @Override
            public void keyPressed(final KeyEvent e) {
                final int keyCode = e.getKeyCode();
                if (keyCode == 38) {
                    this.updateSVGView();
                }
                if (keyCode == 40) {
                    this.updateSVGView();
                }
            }
            
            private void updateSVGView() {
                EventQueue.invokeLater(() -> {
                    String fileName = null;
                    final TreePath treePath = tree.getSelectionPath();
                    if (treePath != null) {
                        final svgLoaderNode selectedLeaf = (svgLoaderNode)treePath.getLastPathComponent();
                        if (selectedLeaf.isLeaf()) {
                            fileName = selectedLeaf.fullName;
                        }
                    }
                    if (fileName != null) {
                        SVGViewerDialog.displayImage(fileName, contentPane, svgView);
                    }
                });
            }
            
            @Override
            public void keyReleased(final KeyEvent e) {
            }
        });
        tree.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent e) {
                if (SVGViewerDialog.lastKeyPressed == "UP") {
                    tree.setSelectionRow(tree.getLeadSelectionRow() - 1);
                    SVGViewerDialog.lastKeyPressed = "";
                }
                if (SVGViewerDialog.lastKeyPressed == "DOWN") {
                    tree.setSelectionRow(tree.getLeadSelectionRow() + 1);
                    SVGViewerDialog.lastKeyPressed = "";
                }
            }
        });
        filterField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent e) {
                if (filterField.getText().equals("Search SVG")) {
                    filterField.setText("");
                    filterField.setFont(gainFont);
                }
                else {
                    filterField.setText(filterField.getText());
                    filterField.setFont(gainFont);
                }
                if (!SVGViewerDialog.lastKeyPressed.equals("")) {
                    filterField.setText(filterField.getText() + SVGViewerDialog.lastKeyPressed);
                    SVGViewerDialog.lastKeyPressed = "";
                }
                this.setTextColour();
            }
            
            @Override
            public void focusLost(final FocusEvent e) {
                if (filterField.getText().equals("Search SVG") || filterField.getText().length() == 0) {
                    filterField.setText("Search SVG");
                }
                this.setTextColour();
            }
            
            public void setTextColour() {
                if (filterField.getText().equals("Search SVG")) {
                    filterField.setFont(lostFont);
                    filterField.setForeground(Color.GRAY);
                }
                else {
                    filterField.setFont(gainFont);
                    filterField.setForeground(Color.BLACK);
                }
            }
        });
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                this.handleEvent(e);
            }
            
            @Override
            public void removeUpdate(final DocumentEvent e) {
                this.handleEvent(e);
            }
            
            @Override
            public void changedUpdate(final DocumentEvent e) {
                this.handleEvent(e);
            }
            
            public void handleEvent(final DocumentEvent e) {
                tree.updateTreeFilter(filterField);
                tree.revalidate();
            }
        });
        final JScrollPane treeView = new JScrollPane(tree);
        treeView.setPreferredSize(new Dimension(300, 400));
        contentPane.add(treeView, "West");
        contentPane.add(filterField, "South");
        contentPane.add(svgView, "Center");
        contentPane.setPreferredSize(new Dimension(1000, 400));
        contentPane.addHierarchyListener(e -> {
            final Window window = SwingUtilities.getWindowAncestor(contentPane);
            if (window instanceof Dialog) {
                final Dialog dialog = (Dialog)window;
                if (!dialog.isResizable()) {
                    dialog.setResizable(true);
                }
                tree.requestFocus();
            }
        });
        tree.addFocusListener(new FocusListener() {
            private boolean isFirstTime = true;
            
            @Override
            public void focusGained(final FocusEvent e) {
            }
            
            @Override
            public void focusLost(final FocusEvent e) {
                if (this.isFirstTime) {
                    tree.requestFocus();
                    this.isFirstTime = false;
                }
            }
        });
        final URL iconURL = DesktopApp.class.getResource("/ludii-logo-100x100.png");
        BufferedImage image = null;
        try {
            image = ImageIO.read(iconURL);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        final JOptionPane pane = new JOptionPane(contentPane, -1, -1, null, null, null);
        final JDialog dialog = pane.createDialog("Choose an SVG to View");
        dialog.setIconImage(image);
        dialog.setDefaultCloseOperation(2);
        dialog.setModal(true);
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                String fileName = null;
                final TreePath treePath = tree.getSelectionPath();
                if (treePath != null) {
                    final svgLoaderNode selectedLeaf = (svgLoaderNode)treePath.getLastPathComponent();
                    if (selectedLeaf.isLeaf()) {
                        fileName = selectedLeaf.fullName;
                    }
                }
                if (fileName != null) {
                    SVGViewerDialog.displayImage(fileName, contentPane, svgView);
                }
            }
        });
        final Enumeration<TreeNode> bfsEnumeration = root.breadthFirstEnumeration();
        while (bfsEnumeration.hasMoreElements()) {
            final svgLoaderNode node = (svgLoaderNode) bfsEnumeration.nextElement();
            final Enumeration<TreeNode> children = node.children();
            while (children.hasMoreElements()) {
                final svgLoaderNode child = (svgLoaderNode) children.nextElement();
                if (!child.isLeaf()) {
                    tree.expandPath(new TreePath(node.getPath()));
                    break;
                }
            }
        }
        dialog.setVisible(true);
        final Object selectedValue = pane.getValue();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyDispatcher);
        int result;
        if (selectedValue == null) {
            result = -1;
        }
        else if (selectedValue instanceof Integer) {
            result = (int)selectedValue;
        }
        else {
            result = -1;
        }
        if (result == 0) {
            final TreePath treePath = tree.getSelectionPath();
            if (treePath != null) {
                final svgLoaderNode selectedLeaf = (svgLoaderNode)treePath.getLastPathComponent();
                if (!selectedLeaf.isLeaf()) {
                    return null;
                }
                return selectedLeaf.fullName;
            }
        }
        return null;
    }
    
    private static void expandAllNodes(final JTree tree, final int startingIndex, final int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }
        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }
    
    static void displayImage(final String filePath, final JPanel contentPane, final SVGWindow svgView) {
        final int sz = contentPane.getWidth() / 3;
        final String fileName = filePath.replaceAll(Pattern.quote("\\"), "/");
        final SVGGraphics2D svg = MiscLoading.renderImageSVGInternal(sz, fileName, ContextSnapshot.getContext(), 1);
        final SVGGraphics2D svg2 = MiscLoading.renderImageSVGInternal(sz, fileName, ContextSnapshot.getContext(), 2);
        final BufferedImage componentImageDot1 = SVGUtil.createSVGImage(svg.getSVGDocument(), sz, sz);
        final BufferedImage componentImageDot2 = SVGUtil.createSVGImage(svg2.getSVGDocument(), sz, sz);
        svgView.setImages(componentImageDot1, componentImageDot2);
        svgView.repaint();
    }
    
    static {
        SVGViewerDialog.lastKeyPressed = "";
    }
    
    private static class svgLoaderNode extends DefaultMutableTreeNode
    {
        private static final long serialVersionUID = 1L;
        public final String fullName;
        protected boolean isVisible;
        
        public svgLoaderNode(final String shortName, final String fullName) {
            super(shortName);
            this.isVisible = true;
            this.fullName = fullName;
        }
        
        public TreeNode getChildAt(final int index, final boolean filter) {
            if (!filter) {
                return super.getChildAt(index);
            }
            int visibleIdx = -1;
            final Enumeration<TreeNode> e = this.children.elements();
            while (e.hasMoreElements()) {
                final svgLoaderNode node = (svgLoaderNode) e.nextElement();
                if (node.isVisible) {
                    ++visibleIdx;
                }
                if (visibleIdx == index) {
                    return node;
                }
            }
            throw new ArrayIndexOutOfBoundsException("index unmatched after filtering");
        }
        
        public int getChildCount(final boolean filter) {
            if (!filter) {
                return super.getChildCount();
            }
            int count = 0;
            final Enumeration<TreeNode> e = this.children.elements();
            while (e.hasMoreElements()) {
                final svgLoaderNode node = (svgLoaderNode) e.nextElement();
                if (node.isVisible) {
                    ++count;
                }
            }
            return count;
        }
        
        public void updateVisibility(final String filterText) {
            if (this.isLeaf()) {
                this.isVisible = this.fullName.toLowerCase().replaceAll(Pattern.quote("-"), "").replaceAll(Pattern.quote(" "), "").contains(filterText);
            }
            else {
                this.isVisible = false;
                final Enumeration<TreeNode> e = this.children.elements();
                while (e.hasMoreElements()) {
                    final svgLoaderNode child = (svgLoaderNode) e.nextElement();
                    child.updateVisibility(filterText);
                    if (child.isVisible) {
                        this.isVisible = true;
                    }
                }
            }
        }
    }
    
    private static class svgLoaderTreeModel extends DefaultTreeModel
    {
        private static final long serialVersionUID = 1L;
        protected boolean filterActive;
        
        public svgLoaderTreeModel(final svgLoaderNode root) {
            super(root);
            this.filterActive = false;
        }
        
        public void setFilterActive(final boolean active) {
            this.filterActive = active;
        }
        
        @Override
        public Object getChild(final Object parent, final int index) {
            return ((svgLoaderNode)parent).getChildAt(index, this.filterActive);
        }
        
        @Override
        public int getChildCount(final Object parent) {
            return ((svgLoaderNode)parent).getChildCount(this.filterActive);
        }
    }
    
    private static class svgLoaderTree extends JTree
    {
        private static final long serialVersionUID = 1L;
        
        public svgLoaderTree(final svgLoaderNode root) {
            super(new svgLoaderTreeModel(root));
        }
        
        @Override
        public TreePath getNextMatch(final String prefix, final int startingRow, final Position.Bias bias) {
            final int max = this.getRowCount();
            if (prefix == null) {
                throw new IllegalArgumentException();
            }
            if (startingRow < 0 || startingRow >= max) {
                throw new IllegalArgumentException();
            }
            final String str = prefix.toUpperCase();
            final int increment = (bias == Position.Bias.Forward) ? 1 : -1;
            int row = startingRow;
            do {
                final TreePath path = this.getPathForRow(row);
                final svgLoaderNode rowNode = (svgLoaderNode)path.getLastPathComponent();
                final Enumeration<TreeNode> bfsEnumeration = rowNode.breadthFirstEnumeration();
                while (bfsEnumeration.hasMoreElements()) {
                    final svgLoaderNode node = (svgLoaderNode) bfsEnumeration.nextElement();
                    final String nodeName = ((String)node.getUserObject()).toUpperCase();
                    if (nodeName.startsWith(str) && nodeName.endsWith(".SVG")) {
                        return new TreePath(node.getPath());
                    }
                }
                row = (row + increment + max) % max;
            } while (row != startingRow);
            return null;
        }
        
        public void updateTreeFilter(final JTextField filterField) {
            final svgLoaderTreeModel model = (svgLoaderTreeModel)this.getModel();
            model.setFilterActive(false);
            String filterText = filterField.getText().toLowerCase().replaceAll(Pattern.quote("-"), "").replaceAll(Pattern.quote(" "), "");
            final svgLoaderNode root = (svgLoaderNode)model.getRoot();
            if (filterText.equals("searchsvg")) {
                filterText = "";
            }
            if (filterText.length() > 0) {
                root.updateVisibility(filterText);
                model.setFilterActive(true);
            }
            model.reload();
            if (filterText.length() == 0) {
                final Enumeration<TreeNode> bfsEnumeration = root.breadthFirstEnumeration();
                while (bfsEnumeration.hasMoreElements()) {
                    final svgLoaderNode node = (svgLoaderNode) bfsEnumeration.nextElement();
                    final Enumeration<TreeNode> children = node.children();
                    while (children.hasMoreElements()) {
                        final svgLoaderNode child = (svgLoaderNode) children.nextElement();
                        if (!child.isLeaf()) {
                            this.expandPath(new TreePath(node.getPath()));
                            break;
                        }
                    }
                }
            }
            else {
                final Enumeration<TreeNode> bfsEnumeration = root.breadthFirstEnumeration();
                while (bfsEnumeration.hasMoreElements()) {
                    final svgLoaderNode node = (svgLoaderNode) bfsEnumeration.nextElement();
                    if (!node.isLeaf()) {
                        this.expandPath(new TreePath(node.getPath()));
                    }
                }
                final Enumeration<TreeNode> dfsEnumeration = root.depthFirstEnumeration();
                while (dfsEnumeration.hasMoreElements()) {
                    final svgLoaderNode node2 = (svgLoaderNode) dfsEnumeration.nextElement();
                    if (node2.isLeaf()) {
                        final String svgFilename = ((String)node2.getUserObject()).toLowerCase().replaceAll(Pattern.quote("-"), "").replaceAll(Pattern.quote(" "), "");
                        if (svgFilename.startsWith(filterText)) {
                            this.setSelectionPath(new TreePath(node2.getPath()));
                            break;
                        }
                        continue;
                    }
                }
            }
        }
    }
}
