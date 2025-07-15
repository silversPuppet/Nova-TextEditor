import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.*;
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

    public FileFormatManager() {
        frame = new Frame("Nova Text Editor");
        frame.setLayout(new BorderLayout());

        //TEMPORARY START WITH EMPTY TEXT WINDOW
        textWindow = newTextWindow();
        frame.add(textWindow, BorderLayout.CENTER);


        //Adding the File UI to the Window 
        FileUI fileUI = new FileUI(); 
        JPanel fileUIPanel = fileUI.creatPanel();
        frame.add(fileUIPanel, BorderLayout.NORTH);

        //Adding the Formating UI to the Window 
        FormatingUI formUI = new FormatingUI(); 
        JPanel formatingPanel = formUI.creatPanel();
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

    public void saveFile(){
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose destination.");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        try{
            File file = new File(jfc.getSelectedFile().getAbsolutePath());
            FileWriter outputFile = new FileWriter(file);

            //How to save Style/Formating as well? 
            outputFile.write(textWindow.getText());
            outputFile.close();
        } catch (FileNotFoundException ex) {
            Component file = null;
            JOptionPane.showMessageDialog(file,"File not found.");
        } catch (IOException ex) {
            Component file = null;
            JOptionPane.showMessageDialog(file,"Error.");
        }

    }

    // Text Interface for writing and displaying formatted Text
    private class TextWindow extends JTextPane{
       public TextWindow(String[] initString, String[] initStyles){
            
        StyledDocument doc = this.getStyledDocument();

            //Load the text pane with styled text.
            try {
               for (int i=0; i < initString.length; i++) {
                    doc.insertString(doc.getLength(), initString[i],
                                    doc.getStyle(initStyles[i]));
                }
            } catch (BadLocationException ble) {
               System.err.println("Couldn't insert initial text into text pane.");
            }

       }
    }

    public TextWindow newTextWindow(){
        String[] defaultText = {};
        String[] defaultStyle = {};
        TextWindow t = new TextWindow(defaultText, defaultStyle);
        t.setBackground(Color.white);
        return t;
    }

    // GUI for interacting with Files

    //TO DO: Extract Similair Functionality to parent class
    private class FileUI {
        public JPanel creatPanel(){
            JPanel fileManagingLayout = new JPanel(); 
            fileManagingLayout.setLayout(new BoxLayout(fileManagingLayout, BoxLayout.LINE_AXIS));
            
            //Save Button Names in Array for easy language changes 
            JButton saveButton = new JButton("SAVE");

            fileManagingLayout.add(saveButton);
            return fileManagingLayout;
        }
    }

    // GUI for interacting with the TextWindows Format
    private class FormatingUI {
         public JPanel creatPanel(){
            JPanel fileManagingLayout = new JPanel(); 
            fileManagingLayout.setLayout(new BoxLayout(fileManagingLayout, BoxLayout.LINE_AXIS));
            
            //Save Button Names in Array for easy language changes 
            //TO DO FORMAT TO HEADING
            JLabel formatingLabel = new JLabel("Formatting");

            fileManagingLayout.add(formatingLabel);
            return fileManagingLayout;
        }
   }

}
