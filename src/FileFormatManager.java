import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.*;
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
        textWindow.setContentType("text/rtf");
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

    public void quit(){
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

            JButton openButton = new JButton(guiLanguageDicitonary.get("btn_open_")[language]);
            openButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    openFile();
                }
            });

            fileManagingLayout.add(openButton);

            JButton quitButton = new JButton(guiLanguageDicitonary.get("btn_quit_")[language]);
            quitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    quit();
                }
            });

            fileManagingLayout.add(quitButton);

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
            put("btn_open_", new String[] { "OPEN", "ÖFFNEN" });
            put("btn_quit_", new String[] { "QUIT", "SCHLIESEN" });
        }
    };

}
