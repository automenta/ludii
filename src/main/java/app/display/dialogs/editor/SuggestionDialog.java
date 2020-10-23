// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.editor;

import language.grammar.Grammar;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SuggestionDialog extends JDialog implements KeyListener, ListSelectionListener, MouseListener, MouseMotionListener
{
    private static final long serialVersionUID = 4115195324471730562L;
    private static final Font FONT;
    private static final int VIEW_WIDTH = 600;
    private static final int VIEW_HEIGHT = 400;
    final EditorDialog parent;
    private final boolean isPartial;
    private final JList<String> list;
    private final JEditorPane docs;
    private final List<SuggestionInstance> suggestionInstances;
    
    public SuggestionDialog(final EditorDialog parent, final Point point, final boolean isPartial) {
        super(parent);
        this.suggestionInstances = new ArrayList<>();
        this.setUndecorated(true);
        this.parent = parent;
        this.isPartial = isPartial;
        final JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, 3));
        this.getContentPane().add(top);
        final JPanel fpanel = new JPanel();
        fpanel.setLayout(new BoxLayout(fpanel, 2));
        top.add(fpanel);
        final DefaultListModel<String> listModel = new DefaultListModel<>();
        (this.list = new JList<>()).setModel(listModel);
        this.list.getSelectionModel().addListSelectionListener(this);
        this.list.addMouseListener(this);
        this.list.addMouseMotionListener(this);
        this.list.setFont(SuggestionDialog.FONT);
        this.list.addKeyListener(this);
        final JScrollPane scroll1 = new JScrollPane(this.list);
        scroll1.setPreferredSize(new Dimension(600, 400));
        scroll1.setHorizontalScrollBarPolicy(30);
        fpanel.add(scroll1);
        (this.docs = new JEditorPane("text/html", "")).setEditable(false);
        this.docs.addKeyListener(this);
        final StyleSheet styleSheet = ((HTMLDocument)this.docs.getDocument()).getStyleSheet();
        styleSheet.addRule("body { font-family: " + SuggestionDialog.FONT.getFamily() + "; font-size: " + SuggestionDialog.FONT.getSize() + "pt; }");
        styleSheet.addRule("p { font-family: " + SuggestionDialog.FONT.getFamily() + "; font-size: " + SuggestionDialog.FONT.getSize() + "pt; }");
        styleSheet.addRule("* { font-family: " + SuggestionDialog.FONT.getFamily() + "; font-size: " + SuggestionDialog.FONT.getSize() + "pt; }");
        final JScrollPane scroll2 = new JScrollPane(this.docs);
        scroll2.setPreferredSize(new Dimension(600, 400));
        scroll2.setHorizontalScrollBarPolicy(30);
        fpanel.add(scroll2);
        this.setDefaultCloseOperation(2);
        this.setModalityType(ModalityType.MODELESS);
        this.addKeyListener(this);
        this.filterAndAdd(point);
        this.pack();
    }
    
    private void filterAndAdd(final Point screenPos) {
        this.setVisible(false);
        this.filter();
        if (this.isEmpty()) {
            this.parent.returnFocus();
            return;
        }
        this.setLocation(screenPos.x, screenPos.y);
        this.setVisible(true);
    }
    
    void filter() {
        final DefaultListModel<String> listModel = (DefaultListModel<String>) this.list.getModel();
        listModel.clear();
        this.list.removeAll();
        this.suggestionInstances.clear();
        final List<String> allCandidates = Grammar.grammar().classPaths(this.parent.getText(), this.parent.getCaretPosition(), this.isPartial);
        System.out.println("Returned classpaths: " + allCandidates);
        final List<SuggestionInstance> suggestionsFromClasspaths = EditorHelpDataHelper.suggestionsForClasspaths(this.parent.editorHelpData, allCandidates, this.isPartial);
        final String charsBefore = this.parent.charsBeforeCursor();
        System.out.println("### charsBefore:" + charsBefore);
        for (final SuggestionInstance si : suggestionsFromClasspaths) {
            if (!this.isPartial || matches(charsBefore, si.substitution)) {
                this.suggestionInstances.add(si);
            }
        }
        if (this.suggestionInstances.isEmpty()) {
            this.setVisible(false);
            this.parent.returnFocus();
            return;
        }
        System.out.println(this.suggestionInstances.size() + " suggestions found");
        this.suggestionInstances.sort(Comparator.comparing(a -> a.label));
        for (final SuggestionInstance si : this.suggestionInstances) {
            listModel.addElement(EditorHelpDataHelper.formatLabel(si.substitution));
        }
        this.list.setSelectedIndex(0);
        this.list.invalidate();
    }
    
    private static boolean matches(final String charsBefore, final String substitution) {
        final boolean result = substitution.startsWith(charsBefore) || substitution.startsWith("(" + charsBefore);
        System.out.println("testing: " + charsBefore + " vs " + substitution);
        return result;
    }
    
    public boolean isEmpty() {
        return this.suggestionInstances.isEmpty();
    }
    
    @Override
    public void keyTyped(final KeyEvent e) {
        if (e.isActionKey()) {
            return;
        }
        System.out.println("Key typed: " + e.toString());
        final char keyChar = e.getKeyChar();
        if (keyChar == '\uffff') {
            return;
        }
        switch (keyChar) {
            case '\u0003', '\t', '\f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '!', '\"', '#', '$' -> {
            }
            case '\n' -> {
                final int pos = this.list.getSelectedIndex();
                this.insertListEntryAndClose(pos);
            }
            case '\u001b' -> this.setVisible(false);
            case '\b' -> {
                if (this.isPartial) {
                    this.parent.applyBackspace();
                    this.updateList();
                }
            }
            case '\u007f' -> {
                if (this.isPartial) {
                    this.parent.applyDelete();
                    this.updateList();
                }
            }
            default -> {
                if (this.isPartial) {
                    this.parent.insertCharacter(e.getKeyChar());
                    this.updateList();
                }
            }
        }
    }
    
    private void updateList() {
        SwingUtilities.invokeLater(SuggestionDialog.this::filter);
    }
    
    @Override
    public void keyPressed(final KeyEvent e) {
        switch (e.getKeyCode()) {
            case 37 -> {
                this.parent.cursorLeft();
                this.updateList();
            }
            case 39 -> {
                this.parent.cursorRight();
                this.updateList();
            }
        }
    }
    
    @Override
    public void keyReleased(final KeyEvent e) {
    }
    
    @Override
    public void valueChanged(final ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        final int pos = this.list.getSelectedIndex();
        if (pos >= 0 && pos < this.suggestionInstances.size()) {
            this.docs.setText("<html>" + this.suggestionInstances.get(pos).javadoc + "</html>");
        }
    }
    
    @Override
    public void mouseClicked(final MouseEvent evt) {
        final int pos = this.list.locationToIndex(evt.getPoint());
        this.insertListEntryAndClose(pos);
    }
    
    private void insertListEntryAndClose(final int listSelection) {
        if (listSelection >= 0) {
            this.parent.replaceTokenScopeWith(this.suggestionInstances.get(listSelection).substitution, this.isPartial);
            this.setVisible(false);
        }
    }
    
    @Override
    public void mousePressed(final MouseEvent e) {
    }
    
    @Override
    public void mouseReleased(final MouseEvent e) {
    }
    
    @Override
    public void mouseEntered(final MouseEvent e) {
    }
    
    @Override
    public void mouseExited(final MouseEvent e) {
    }
    
    @Override
    public void mouseDragged(final MouseEvent e) {
    }
    
    @Override
    public void mouseMoved(final MouseEvent me) {
        final Point p = new Point(me.getX(), me.getY());
        final int index = this.list.locationToIndex(p);
        if (index >= 0) {
            this.list.setSelectedIndex(index);
        }
    }
    
    static {
        FONT = UIManager.getFont("Label.font");
    }
}
