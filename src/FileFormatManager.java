import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.management.JMException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.text.*;
import javax.swing.text.DefaultEditorKit.*;
import javax.swing.text.StyledEditorKit.AlignmentAction;
import javax.swing.text.StyledEditorKit.FontFamilyAction;
import javax.swing.text.StyledEditorKit.FontSizeAction;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import java.util.List;

import java.awt.*;
import java.awt.event.*;

/*
Used to:
- Open files
- Generate Window
- Facilitate user interaction
- Save and format input
-   ...
*/
public class FileFormatManager {
    Frame frame;
    TextWindow textWindow;
    FormatingUI formatingUI;
    FileUI fileUI;
    

    /*
     * 0 = English
     * 1 = German
     */
    String[] languages = { "English", "Deutsch" };
    int currentLanguage = 0;

    // undo and redo
    public Document editorPaneDocument;
    protected UndoHandler undoHandler = new UndoHandler();
    protected UndoManager undoManager = new UndoManager();
    UndoAction undoAction = null;
    RedoAction redoAction = null;

    public FileFormatManager() {
        frame = new Frame("Nova Text Editor");
        frame.setLayout(new BorderLayout());

        // TEMPORARY START WITH EMPTY TEXT WINDOW
        textWindow = newTextWindow();
        textWindow.setContentType("text/rtf");
        textWindow.setDocument(new DefaultStyledDocument());
        JScrollPane editorScrollPane = new JScrollPane(textWindow);
        frame.add(editorScrollPane, BorderLayout.CENTER);

        KeyStroke undoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.META_MASK);
        KeyStroke redoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.META_MASK);


        undoAction = new UndoAction();
        textWindow.getInputMap().put(undoKeystroke, "undoKeystroke");
        textWindow.getActionMap().put("undoKeystroke", undoAction);

        redoAction = new RedoAction();
        textWindow.getInputMap().put(redoKeystroke, "redoKeystroke");
        textWindow.getActionMap().put("redoKeystroke", redoAction);

        // Adding the File UI to the Window
        fileUI = new FileUI();
        JPanel fileUIPanel = fileUI.createPanel(guiLanguageDicitonary, this);
        frame.add(fileUIPanel, BorderLayout.NORTH);

        // Adding the Formating UI to the Window
        formatingUI = new FormatingUI();
        JPanel formatingPanel = formatingUI.createPanel(guiLanguageDicitonary, this);
        frame.add(formatingPanel, BorderLayout.WEST);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(true);
        frame.setVisible(true);

        // Using WindowListener for closing the window
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        editorPaneDocument = textWindow.getDocument();

        editorPaneDocument.addUndoableEditListener(undoHandler);
    }

    public static List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container)
                compList.addAll(getAllComponents((Container) comp));
        }
        return compList;
    }

    public void updateLanguage() {
        JPanel formatPanel = formatingUI.getJPanel();

        List<Component> comps = getAllComponents(frame);
        for (Component component : comps) {
            if (component instanceof JCheckBox) {
                System.out.println("CHECKBOX");
                JCheckBox myCheck = (JCheckBox) component;
                myCheck.setText(guiLanguageDicitonary.get((String) myCheck.getName())[currentLanguage]);
            } else if (component instanceof JLabel && component.getName() != null) {
                System.out.println("JLabel " + component.getName() + " \n");
                JLabel myLabel = (JLabel) component;
                myLabel.setText(guiLanguageDicitonary.get(myLabel.getName())[currentLanguage]);
            } else if (component instanceof JComboBox) {
                System.out.println("Combo Box \n");
                JComboBox<String> myBox = (JComboBox<String>) component;
                String[] options = myBox.getName().split(" ");
                myBox.removeAllItems();
                for (String string : options) {
                    myBox.addItem(guiLanguageDicitonary.get(string)[currentLanguage]);
                }
                formatPanel.add(myBox);
            } else if (component instanceof JButton && component.getName() != null) {
                JButton myButton = (JButton) component;
                myButton.setText(guiLanguageDicitonary.get(myButton.getName())[currentLanguage]);
            }

        }
    }

    public void quit() {
        System.exit(0);
    }

    public void openFile() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose destination.");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int option = jfc.showOpenDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();

            RTFEditorKit kit = new RTFEditorKit();
            try {
                textWindow.setContentType("text/rtf");
                InputStream inputStream = new FileInputStream(selectedFile);
                DefaultStyledDocument styledDocument = new DefaultStyledDocument(new StyleContext());
                kit.read(inputStream, styledDocument, 0);
                textWindow.setDocument(styledDocument);
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(frame, "Error opening file: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IOException | javax.swing.text.BadLocationException e) {
                JOptionPane.showMessageDialog(frame, "Error opening file: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public void saveFile() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose destination.");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int option = jfc.showSaveDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            // Ensure it has the .rtf extension
            if (!selectedFile.getName().toLowerCase().endsWith(".rtf")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".rtf");
            }

            StyledDocument doc = (StyledDocument) textWindow.getDocument();
            RTFEditorKit rtfKit = new RTFEditorKit();

            try (OutputStream out = new FileOutputStream(selectedFile)) {
                rtfKit.write(out, doc, 0, doc.getLength());
                JOptionPane.showMessageDialog(frame, "File saved successfully!");
            } catch (IOException | javax.swing.text.BadLocationException e) {
                JOptionPane.showMessageDialog(frame, "Error saving file: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Text Interface for writing and displaying formatted Text
    public class TextWindow extends JTextPane {
        public TextWindow(String[] initString, String[] initStyles) {

            StyledDocument doc = this.getStyledDocument();

            // Load the text pane with styled text.
            try {
                for (int i = 0; i < initString.length; i++) {
                    doc.insertString(doc.getLength(), initString[i],
                            doc.getStyle(initStyles[i]));
                }
            } catch (BadLocationException ble) {
                System.err.println("Couldn't insert initial text into text pane.");
            }

        }
    }

    public TextWindow newTextWindow() {
        String[] defaultText = {};
        String[] defaultStyle = {};
        TextWindow t = new TextWindow(defaultText, defaultStyle);
        t.setBackground(Color.white);
        return t;
    }
    // java undo and redo action classes

    public class UndoHandler implements UndoableEditListener {

        /**
         * Messaged when the Document has created an edit, the edit is added to
         * <code>undoManager</code>, an instance of UndoManager.
         */
        public void undoableEditHappened(UndoableEditEvent e) {
            undoManager.addEdit(e.getEdit());
            undoAction.update();
            redoAction.update();
        }
    }

    public class UndoAction extends AbstractAction {
        public UndoAction() {
            super("Undo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                undoManager.undo();
            } catch (CannotUndoException ex) {
                // TODO deal with this
                // ex.printStackTrace();
            }
            update();
            redoAction.update();
        }

        protected void update() {
            if (undoManager.canUndo()) {
                setEnabled(true);
                //putValue(Action.NAME, undoManager.getUndoPresentationName());
            } else {
                setEnabled(false);
                //putValue(Action.NAME, "Undo");
            }
        }
    }

    public class RedoAction extends AbstractAction {
        public RedoAction() {
            super("Redo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                undoManager.redo();
            } catch (CannotRedoException ex) {
                // TODO deal with this
                ex.printStackTrace();
            }
            update();
            undoAction.update();
        }

        protected void update() {
            if (undoManager.canRedo()) {
                setEnabled(true);
                //putValue(Action.NAME, undoManager.getRedoPresentationName());
            } else {
                setEnabled(false);
                //putValue(Action.NAME, "Redo");
            }
        }
    }

    /*
     * "Toolbox" for all UI's to follow certain appearances
     * For Example share styles between UIs:
     * - Buttons
     * - Dropdowns
     * - Sliders
     */
    interface GUI {
        public JPanel createPanel(Map<String, String[]> guiLanguageDicitonary, FileFormatManager fileFormatManager);

        public class DefaultButton extends BasicButtonUI {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
            }
        }
    }

     Map<String, String[]> guiLanguageDicitonary = new HashMap<>() {
        {
            // Type Name English, German
            put("btn_save_", new String[] { "SAVE", "SPEICHERN" });
            put("btn_open_", new String[] { "OPEN", "ÖFFNEN" });
            put("btn_quit_", new String[] { "QUIT", "SCHLIESEN" });
            put("btn_language_", new String[] { "LANGUAGE", "SPRACHE" });
            put("btn_undo_", new String[] {"UNDO", "ZURÜCK"});
            put("btn_redo_", new String[] {"REDO", "NOCHMALS"});

            put("lbl_formating_", new String[] { "Formating", "Formatieren" });
            put("btn_cut_", new String[] { "Cut", "Ausschneiden" });
            put("btn_copy_", new String[] { "Copy", "Kopieren" });
            put("btn_textColour_", new String[] { "Colour", "Farbe" });
            put("btn_textColourBG_", new String[] { "Text Background Colour", "Text Hintergrund Farbe" });
            put("btn_paste_", new String[] { "Paste", "Einfügen" });
            put("cbox_align_", new String[] { "Alignment", "Ausrichtung" });
            put("cbox_align0_", new String[] { "Align Left", "Linksbündig" });
            put("cbox_align1_", new String[] { "Align Center", "Zentriert" });
            put("cbox_align2_", new String[] { "Align Right", "Rechtsbündig" });
            put("cbox_align3_", new String[] { "Align Justified", "Blocksatz" });
            put("lbl_textSize_", new String[] { "Size", "Größe" });
            put("chk_bold_", new String[] { "Bold", "Fett" });
            put("chk_italic_", new String[] { "Italic", "Kursiv" });
            put("chk_underline_", new String[] { "Underline", "Unterstrichen" });
            put("chk_strikethrough_", new String[] { "Strikethrough", "Durchstreichen" });
        }
    };

    public Vector<String> getEditorFonts() {

            String[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            Vector<String> returnList = new Vector<>();

            for (String font : availableFonts) {

                if (FONT_LIST.contains(font)) {

                    returnList.add(font);
                }
            }

            return returnList;
        }

    public static final List<String> FONT_LIST = Arrays
            .asList(new String[] { "Arial", "Calibri", "Cambria", "Courier New", "Comic Sans MS", "Dialog", "Georgia",
                    "Helevetica", "Lucida Sans", "Monospaced", "Tahoma", "Times New Roman", "Verdana" });
}
