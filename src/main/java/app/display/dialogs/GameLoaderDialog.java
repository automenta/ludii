// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs;

import app.DesktopApp;
import app.loading.aliases.AliasesData;

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
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

public class GameLoaderDialog
{
    static String lastKeyPressed;
    static String oldSearchString;
    
    public static String showDialog(final JFrame frame, final String[] choices, final String initialChoice, final boolean puzzlesAllowed) {
        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        GameLoaderDialog.lastKeyPressed = "";
        final List<GameLoaderNode> leafNodes = new ArrayList<>();
        final Map<String, GameLoaderNode> nodesMap = new HashMap<>();
        final GameLoaderNode root = new GameLoaderNode("Games", "/lud/");
        for (final String choice : choices) {
            String str = choice.replaceAll(Pattern.quote("\\"), "/");
            if (str.startsWith("/")) {
                str = str.substring(1);
            }
            final String[] parts = str.split("/");
            if (puzzlesAllowed || !str.contains("/puzzle")) {
                if (!parts[0].equals("lud")) {
                    System.err.println("top level is not lud: " + parts[0]);
                }
                String runningFullName = "/lud/";
                GameLoaderNode internalNode = root;
                for (int i = 1; i < parts.length - 1; ++i) {
                    runningFullName = runningFullName + parts[i] + "/";
                    GameLoaderNode nextInternal;
                    if (!nodesMap.containsKey(runningFullName)) {
                        nextInternal = new GameLoaderNode(parts[i], runningFullName);
                        nodesMap.put(runningFullName, nextInternal);
                        int childIdx;
                        for (childIdx = 0; childIdx < internalNode.getChildCount(); ++childIdx) {
                            final GameLoaderNode existingChild = (GameLoaderNode)internalNode.getChildAt(childIdx);
                            final String name = (String)existingChild.getUserObject();
                            if (existingChild.fullName.endsWith(".lud")) {
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
                final GameLoaderNode leafNode = new GameLoaderNode(parts[parts.length - 1].substring(0, parts[parts.length - 1].length() - 4), choice);
                nodesMap.put(choice, leafNode);
                leafNodes.add(leafNode);
                internalNode.add(leafNode);
            }
        }
        final GameLoaderTree tree = new GameLoaderTree(root);
        tree.getSelectionModel().setSelectionMode(1);
        try {
            tree.setSelectionPath(new TreePath(nodesMap.get(initialChoice).getPath()));
        }
        catch (Exception ex) {}
        final JTextField filterField = new JTextField();
        filterField.setFont(new Font("Arial", 0, 20));
        final Font gainFont = new Font("Arial", 0, 20);
        final Font lostFont = new Font("Arial", 0, 20);
        final String hint = "Search Game";
        filterField.setText("Search Game");
        filterField.setFont(lostFont);
        filterField.setForeground(Color.GRAY);
        tree.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent e) {
                if (GameLoaderDialog.lastKeyPressed == "UP") {
                    tree.setSelectionRow(tree.getLeadSelectionRow() - 1);
                    GameLoaderDialog.lastKeyPressed = "";
                }
                if (GameLoaderDialog.lastKeyPressed == "DOWN") {
                    tree.setSelectionRow(tree.getLeadSelectionRow() + 1);
                    GameLoaderDialog.lastKeyPressed = "";
                }
            }
        });
        filterField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent e) {
                if (filterField.getText().equals("Search Game")) {
                    filterField.setText("");
                    filterField.setFont(gainFont);
                }
                else {
                    filterField.setText(filterField.getText());
                    filterField.setFont(gainFont);
                }
                EventQueue.invokeLater(() -> {
                    final String currentSearchString = filterField.getText();
                    if (GameLoaderDialog.oldSearchString.equals(currentSearchString) || (GameLoaderDialog.oldSearchString.equals("Search Game") && currentSearchString.equals(""))) {
                        if (!GameLoaderDialog.lastKeyPressed.equals("")) {}
                        filterField.setText(filterField.getText() + GameLoaderDialog.lastKeyPressed);
                        GameLoaderDialog.lastKeyPressed = "";
                    }
                });
                this.setTextColour();
            }
            
            @Override
            public void focusLost(final FocusEvent e) {
                if (filterField.getText().equals("Search Game") || filterField.getText().length() == 0) {
                    filterField.setText("Search Game");
                }
                this.setTextColour();
            }
            
            public void setTextColour() {
                if (filterField.getText().equals("Search Game")) {
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
        contentPane.add(treeView, "Center");
        contentPane.add(filterField, "South");
        contentPane.setPreferredSize(new Dimension(650, 700));
        contentPane.addHierarchyListener(e -> {
            final Window window = SwingUtilities.getWindowAncestor(contentPane);
            if (window instanceof Dialog) {
                final Dialog dialog = (Dialog)window;
                if (!dialog.isResizable()) {
                    dialog.setResizable(true);
                }
                dialog.setLocationRelativeTo(DesktopApp.frame());
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
        final JOptionPane pane = new JOptionPane(contentPane, -1, 2, null, null, null);
        final JDialog dialog = pane.createDialog("Choose a Game to Load");
        dialog.setIconImage(image);
        dialog.setDefaultCloseOperation(2);
        dialog.setModal(true);
        final KeyEventDispatcher keyDispatcher = e -> {
            if (e.getID() == 401) {
                if (e.getKeyCode() == 10) {
                    pane.setValue(0);
                    dialog.dispose();
                }
                else if (e.getKeyCode() == 38) {
                    if (!tree.hasFocus()) {
                        GameLoaderDialog.lastKeyPressed = "UP";
                        tree.requestFocus();
                    }
                }
                else if (e.getKeyCode() == 40) {
                    if (!tree.hasFocus()) {
                        GameLoaderDialog.lastKeyPressed = "DOWN";
                        tree.requestFocus();
                    }
                }
                else if (e.getKeyCode() != 37 && e.getKeyCode() != 39 && KeyEvent.getKeyText(e.getKeyCode()).length() == 1 && !filterField.hasFocus()) {
                    GameLoaderDialog.oldSearchString = filterField.getText();
                    GameLoaderDialog.lastKeyPressed = Character.toString(e.getKeyChar());
                    filterField.requestFocus();
                    return true;
                }
            }
            return false;
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyDispatcher);
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2) {
                    final TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        final GameLoaderNode node = (GameLoaderNode)path.getLastPathComponent();
                        if (node.fullName.endsWith(".lud")) {
                            pane.setValue(0);
                            dialog.dispose();
                        }
                    }
                }
            }
        });
        final Enumeration<TreeNode> bfsEnumeration = root.breadthFirstEnumeration();
        while (bfsEnumeration.hasMoreElements()) {
            final GameLoaderNode node = (GameLoaderNode) bfsEnumeration.nextElement();
            final Enumeration<TreeNode> children = node.children();
            while (children.hasMoreElements()) {
                final GameLoaderNode child = (GameLoaderNode) children.nextElement();
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
                final GameLoaderNode selectedLeaf = (GameLoaderNode)treePath.getLastPathComponent();
                if (!selectedLeaf.isLeaf()) {
                    return null;
                }
                return selectedLeaf.fullName;
            }
        }
        return null;
    }
    
    static {
        GameLoaderDialog.lastKeyPressed = "";
        GameLoaderDialog.oldSearchString = "";
    }
    
    private static class GameLoaderNode extends DefaultMutableTreeNode
    {
        private static final long serialVersionUID = 1L;
        public final String fullName;
        protected final List<String> aliases;
        protected boolean isVisible;
        
        public GameLoaderNode(final String shortName, final String fullName) {
            super(shortName);
            this.isVisible = true;
            this.fullName = fullName;
            final AliasesData aliasesData = AliasesData.loadData();
            final List<String> loadedAliases = aliasesData.aliasesForGame(this.fullName.replaceAll(Pattern.quote("\\"), "/"));
            if (loadedAliases != null) {
                this.aliases = new ArrayList<>(loadedAliases.size());
                for (final String alias : loadedAliases) {
                    this.aliases.add(alias.toLowerCase().replaceAll(Pattern.quote("-"), "").replaceAll(Pattern.quote(" "), "").replaceAll(Pattern.quote("'"), ""));
                }
            }
            else {
                this.aliases = new ArrayList<>();
            }
        }
        
        public TreeNode getChildAt(final int index, final boolean filter) {
            if (!filter) {
                return super.getChildAt(index);
            }
            int visibleIdx = -1;
            final Enumeration<TreeNode> e = this.children.elements();
            while (e.hasMoreElements()) {
                final GameLoaderNode node = (GameLoaderNode) e.nextElement();
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
            try {
                final Enumeration<TreeNode> e = this.children.elements();
                while (e.hasMoreElements()) {
                    final GameLoaderNode node = (GameLoaderNode) e.nextElement();
                    if (node.isVisible) {
                        ++count;
                    }
                }
            }
            catch (Exception ex) {}
            return count;
        }
        
        public void updateVisibility(final String filterText) {
            if (this.isLeaf()) {
                final String[] fullNameSplit = this.fullName.split(Pattern.quote("/"));
                final String gameName = fullNameSplit[fullNameSplit.length - 1];
                if (!(this.isVisible = gameName.toLowerCase().replaceAll(Pattern.quote("-"), "").replaceAll(Pattern.quote(" "), "").replaceAll(Pattern.quote("'"), "").contains(filterText))) {
                    for (final String alias : this.aliases) {
                        if (alias.contains(filterText)) {
                            this.isVisible = true;
                            break;
                        }
                    }
                }
            }
            else {
                this.isVisible = false;
                final Enumeration<TreeNode> e = this.children.elements();
                while (e.hasMoreElements()) {
                    final GameLoaderNode child = (GameLoaderNode) e.nextElement();
                    child.updateVisibility(filterText);
                    if (child.isVisible) {
                        this.isVisible = true;
                    }
                }
            }
        }
    }
    
    private static class GameLoaderTreeModel extends DefaultTreeModel
    {
        private static final long serialVersionUID = 1L;
        protected boolean filterActive;
        
        public GameLoaderTreeModel(final GameLoaderNode root) {
            super(root);
            this.filterActive = false;
        }
        
        public void setFilterActive(final boolean active) {
            this.filterActive = active;
        }
        
        @Override
        public Object getChild(final Object parent, final int index) {
            return ((GameLoaderNode)parent).getChildAt(index, this.filterActive);
        }
        
        @Override
        public int getChildCount(final Object parent) {
            return ((GameLoaderNode)parent).getChildCount(this.filterActive);
        }
    }
    
    private static class GameLoaderTree extends JTree
    {
        private static final long serialVersionUID = 1L;
        
        public GameLoaderTree(final GameLoaderNode root) {
            super(new GameLoaderTreeModel(root));
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
                final GameLoaderNode rowNode = (GameLoaderNode)path.getLastPathComponent();
                final Enumeration<TreeNode> bfsEnumeration = rowNode.breadthFirstEnumeration();
                while (bfsEnumeration.hasMoreElements()) {
                    final GameLoaderNode node = (GameLoaderNode) bfsEnumeration.nextElement();
                    final String nodeName = ((String)node.getUserObject()).toUpperCase();
                    if (nodeName.startsWith(str) && node.fullName.endsWith(".LUD")) {
                        return new TreePath(node.getPath());
                    }
                }
                row = (row + increment + max) % max;
            } while (row != startingRow);
            return null;
        }
        
        public void updateTreeFilter(final JTextField filterField) {
            final GameLoaderTreeModel model = (GameLoaderTreeModel)this.getModel();
            model.setFilterActive(false);
            String filterText = filterField.getText().toLowerCase().replaceAll(Pattern.quote("-"), "").replaceAll(Pattern.quote(" "), "").replaceAll(Pattern.quote("'"), "");
            final GameLoaderNode root = (GameLoaderNode)model.getRoot();
            if (filterText.equals("searchgame")) {
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
                    final GameLoaderNode node = (GameLoaderNode) bfsEnumeration.nextElement();
                    final Enumeration<TreeNode> children = node.children();
                    while (children.hasMoreElements()) {
                        final GameLoaderNode child = (GameLoaderNode) children.nextElement();
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
                    final GameLoaderNode node = (GameLoaderNode) bfsEnumeration.nextElement();
                    if (!node.isLeaf()) {
                        this.expandPath(new TreePath(node.getPath()));
                    }
                }
                final Enumeration<TreeNode> dfsEnumeration = root.depthFirstEnumeration();
                while (dfsEnumeration.hasMoreElements()) {
                    final GameLoaderNode node2 = (GameLoaderNode) dfsEnumeration.nextElement();
                    if (node2.isLeaf()) {
                        final String gameFilename = ((String)node2.getUserObject()).toLowerCase().replaceAll(Pattern.quote("-"), "").replaceAll(Pattern.quote(" "), "").replaceAll(Pattern.quote("'"), "");
                        if (gameFilename.startsWith(filterText)) {
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
