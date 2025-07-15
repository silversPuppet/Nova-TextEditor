import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

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

    /*
     * 0 = English
     * 1 = German
     */
    int language = 0;

    public FileFormatManager() {
        frame = new Frame("Nova Text Editor");
        frame.setLayout(new BorderLayout());

        // TEMPORARY START WITH EMPTY TEXT WINDOW
        textWindow = newTextWindow();
        frame.add(textWindow, BorderLayout.CENTER);

        // Adding the File UI to the Window
        FileUI fileUI = new FileUI();
        JPanel fileUIPanel = fileUI.createPanel();
        frame.add(fileUIPanel, BorderLayout.NORTH);

        // Adding the Formating UI to the Window
        FormatingUI formUI = new FormatingUI();
        JPanel formatingPanel = formUI.createPanel();
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
    }

    public void saveFile() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose destination.");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        

        int option = jfc.showSaveDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            StyledDocument doc = (StyledDocument) textWindow.getDocument();
            HTMLEditorKit kit = new HTMLEditorKit();

            BufferedOutputStream out;
            
                try {
                    out = new BufferedOutputStream(new FileOutputStream(jfc.getSelectedFile().getAbsoluteFile()));

                    kit.write(out, doc, doc.getStartPosition().getOffset(), doc.getLength());

                } catch (FileNotFoundException e) {

                } catch (IOException e){

                } catch (BadLocationException e){

                }
        }

    }

    // Text Interface for writing and displaying formatted Text
    private class TextWindow extends JTextPane {
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

    // GUI for interacting with Files
    private class FileUI implements GUI {
        public JPanel createPanel() {
            JPanel fileManagingLayout = new JPanel();
            fileManagingLayout.setLayout(new BoxLayout(fileManagingLayout, BoxLayout.LINE_AXIS));

            JButton saveButton = new JButton(guiLanguageDicitonary.get("btn_save_")[language]);
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    saveFile();
                }
            });

            fileManagingLayout.add(saveButton);

            return fileManagingLayout;
        }
    }

    // GUI for interacting with the TextWindows Format
    private class FormatingUI implements GUI {

        public JPanel createPanel() {
            JPanel fileManagingLayout = new JPanel();
            fileManagingLayout.setLayout(new BoxLayout(fileManagingLayout, BoxLayout.LINE_AXIS));

            // Save Button Names in Array for easy language changes
            // TO DO FORMAT TO HEADING
            JLabel formatingLabel = new JLabel("Formatting");

            fileManagingLayout.add(formatingLabel);
            return fileManagingLayout;
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
        public JPanel createPanel();

        public class DefaultButton extends BasicButtonUI {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
            }
        }
    }

    // Creates a Dictionary with Key-value Pairs
    // key en,de
    Map<String, String[]> guiLanguageDicitonary = new HashMap<>() {
        {
            // Type Name English, German
            put("btn_save_", new String[] { "SAVE", "SPEICHERN" });
        }
    };

}
